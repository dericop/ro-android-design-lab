package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import com.getbase.floatingactionbutton.FloatingActionButton;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.florent37.tutoshowcase.TutoShowcase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.ucaldas.ro.reduccionobesidad.dummy.DummyContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class mHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TipsFragment.OnListFragmentInteractionListener {

    private TabLayout tabLayout;

    private static Context context;
    private DatabaseReference mDatabase;
    private DatabaseReference userRef;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    /* Atributos para el control de usuarios */
    static FirebaseUser user;

    static boolean isAdmin = false;

    // Tab References
    private Home home;
    private Simulation simulation;
    private Simulationv2 simulationv2;
    private MyItems myItems;
    private TipsFragment tips;

    private FloatingActionButton btn_add_tip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_home);

        if(mHome.user == null){
            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // AUser is signed in
                        Log.d("AUser", "onAuthStateChanged:signed_in:" + user.getUid());
                        Toast.makeText(getBaseContext(), getString(R.string.login_successfull), Toast.LENGTH_LONG);

                        mHome.user = user; //Asignación de usuario a la clase principal
                        initMHome();


                    } else {
                        // AUser is signed out
                        Log.d("AUser", "onAuthStateChanged:signed_out");
                        //progress.dismiss();
                        Toast.makeText(getBaseContext(), getString(R.string.login_fail), Toast.LENGTH_LONG);

                    }
                }
            };

            mAuth.addAuthStateListener(mAuthListener);
        }else{
            initMHome();
        }
    }

    private void initMHome(){
        mDatabase = FirebaseDatabase.getInstance().getReference();

        configureToolbarAndToggleActionBar();
        addListenerToFloatButton();
        configureNavigationView();
        initViewPager();

        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);

    }

    private void initViewPager(){
        /*
        * Configurar viewPager para navegación general de la aplicación
        * */
        ViewPager viewPager;
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

    }

    private void configureNavigationView(){
        /*
        * Configuración de la barra lateral de navegación, envío de datos de usuario
        * */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        if(user != null){
            //Datos del usuario logueado

            String email = user.getEmail();

            final TextView navHeaderTitle = (TextView)header.findViewById(R.id.nav_header_title);
            final TextView navHeaderSubtitle = (TextView)header.findViewById(R.id.nav_header_subtitle);
            final ImageView imageView = (ImageView)header.findViewById(R.id.imageView);

            navHeaderSubtitle.setText(email);

            configureDBReference();

            this.userRef.child(mHome.user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        AUser user = dataSnapshot.getValue(AUser.class);
                        if(user != null){
                            String displayName = user.getmUserName();
                            String image = user.getmPhotoUrl();

                            LinearLayout tipBtnContainer = (LinearLayout) findViewById(R.id.tip_button_container);
                            if(user.getIsAdmin() == 1){
                                tipBtnContainer.setVisibility(View.VISIBLE);
                                isAdmin = true;

                            } else {
                                tipBtnContainer.setVisibility(View.GONE);
                                isAdmin = false;
                            }
                            navHeaderTitle.setText(displayName);
                            Picasso.with(getBaseContext()).load(image).into(imageView);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void configureDBReference(){
        if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
            this.userRef = mDatabase.child("users");
        }else{
            this.userRef = mDatabase.child("users-reflexive");
        }
    }

    private void addListenerToFloatButton(){
        /*
        * Configuración de las acciones del botón flotante.
        * */
        final FloatingActionsMenu btn_actions_menu = (FloatingActionsMenu) findViewById(R.id.btn_actions_menu);
        final FloatingActionButton camera_action = (FloatingActionButton) findViewById(R.id.btn_add_post_camera);


        camera_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera_intent = new Intent(getBaseContext(), AddPost.class);
                camera_intent.putExtra("source","camera");
                startActivity(camera_intent);
                btn_actions_menu.collapse();
            }
        });

        FloatingActionButton gallery_action = (FloatingActionButton) findViewById(R.id.btn_add_post_gallery);
        gallery_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery_intent = new Intent(getBaseContext(), AddPost.class);
                gallery_intent.putExtra("source", "gallery");
                startActivity(gallery_intent);
                btn_actions_menu.collapse();
            }
        });


        btn_add_tip = (FloatingActionButton) findViewById(R.id.btn_add_tip);
        btn_add_tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addTipIntent = new Intent(getBaseContext(), AddTipActivity.class);
                startActivity(addTipIntent);
                btn_actions_menu.collapse();
            }
        });

        /*TutoShowcase.from(this)
                .setContentView(R.layout.list_home_item_reflexive)
                .on(camera_action)
                .addCircle()
                .show();*/
    }

    private void configureToolbarAndToggleActionBar(){
        /*
        * Habilitar toolbar para soporte de acciones
        * */

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /*if (Build.VERSION.SDK_INT >= 16){
            toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.header));
        }*/
    }

    private void setupViewPager(ViewPager viewPager) {
        /*
        * Configuraciones necesarios para el uso de viewPager
        * */
        home = new Home();
        myItems = new MyItems();
        tips = new TipsFragment();

        if(WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
            simulation = new Simulation();
        else
            simulationv2 = new Simulationv2();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(home, "Inicio");
        adapter.addFragment(myItems, "Mis Items");

        if(WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
            adapter.addFragment(simulation, "Simulación");
        else
            adapter.addFragment(simulationv2, "Simulación");

        adapter.addFragment(tips, "Detonantes");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        createTabIcons();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch(tab.getPosition()) {
                    case 0:
                        //Log.v("Items", "Home");
                        break;
                    case 1:
                        //Log.v("Items", "Mis items");
                        //myItems.loadItems();
                        break;
                    case 2:
                        //Log.v("Items", "Simulación");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void createTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Inicio");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_home, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);


        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Mis items");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_items, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);

        if(WelcomeActivity.CURRENT_APP_VERSION.equals("R"))
            tabThree.setText("Reporte");
        else
            tabThree.setText("Simulación");

        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_simulation, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("Tips");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_home, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.m_home, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            Intent confIntent = new Intent(getContext(), ConfigurationActivity.class);
            startActivity(confIntent);
        }//else if(id == R.id.nav_intro){
//
       // }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(Tip item) {
        Intent tipDetailIntent = new Intent(this, TipDetailActivity.class);
        tipDetailIntent.putExtra("title", item.getName());
        tipDetailIntent.putExtra("description", item.getDescription());
        tipDetailIntent.putExtra("image", item.getImage());
        
        startActivity(tipDetailIntent);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
