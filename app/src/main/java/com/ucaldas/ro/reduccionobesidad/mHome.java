package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
    static boolean comeBackFromChallenge = false;
    static boolean comeBackFromComment = false;
    static boolean comeBackFromPost = false;

    // Tab References
    private Home home;
    private Simulation simulation;
    private Simulationv2 simulationv2;
    private MyItems myItems;
    private TipsFragment tips;

    private FloatingActionButton btn_add_tip;
    TextView score_view;
    long score;

    private boolean isFirstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_home);

        String type = getIntent().getStringExtra("type");
        /*if(type != null){
            String data = getIntent().getStringExtra("data");
            Intent detailIntent = new Intent(getApplicationContext(), TipDetailActivity.class);
            detailIntent.putExtra("id", data);
            detailIntent.putExtra("notificationType", "tip");
        }*/

        if (mHome.user == null) {
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
        } else {
            initMHome();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!isFirstTime){
            if(comeBackFromChallenge){
                /*score+=5;
                if(score_view != null)
                    score_view.setText(score+"pts");*/

                LinearLayout challengeView = (LinearLayout) findViewById(R.id.challengeView);
                LinearLayout waitChallengeView = (LinearLayout) findViewById(R.id.wait_challenge);

                if(challengeView!=null && waitChallengeView!=null){
                    challengeView.setVisibility(View.GONE);
                    waitChallengeView.setVisibility(View.VISIBLE);
                    comeBackFromChallenge = false;
                }

            }else if(comeBackFromComment){

                if(score_view != null){
                    score+=1;
                    score_view.setText(score+"pts");
                    comeBackFromComment = false;
                }

            }else if(comeBackFromPost){
                if(score_view != null) {
                    score+=2;
                    score_view.setText(score+"pts");
                    comeBackFromPost = false;
                }

            }
        }

        isFirstTime = false;

    }

    private void initMHome() {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        configureToolbarAndToggleActionBar();
        addListenerToFloatButton();
        configureNavigationView();
        initViewPager();

        context = getApplicationContext();

        Log.v("Notifias", FirebaseInstanceId.getInstance().getToken());

    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);

        Log.v("Come", "Entra");

    }

    private void initViewPager() {
        /*
        * Configurar viewPager para navegación general de la aplicación
        * */
        ViewPager viewPager;
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

    }

    private void loadScore(View view){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        score_view = (TextView)view.findViewById(R.id.score);
        final ImageView img_coin = (ImageView) view.findViewById(R.id.img_coin);

        if(mHome.user != null){

            database.child("gamification-score").child(mHome.user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        HashMap<String, Object> data = (HashMap) dataSnapshot.getValue();
                        Iterator keysIt = data.keySet().iterator();
                        score = 0;

                        while(keysIt.hasNext()){
                            String key = (String)keysIt.next();
                            score += (long)((HashMap)data.get(key)).get("score");
                        }

                        score_view.setText(score +"pts");
                        updateCoin(score, img_coin);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateCoin(long score, ImageView img_coin){
        if(score>=0 && score<=20){
            img_coin.setImageDrawable(getResources().getDrawable(R.drawable.ic_5nivel));
        }else if(score>20 && score<=60){
            img_coin.setImageDrawable(getResources().getDrawable(R.drawable.ic_4nivel));
        }else if(score>60 && score<=120){
            img_coin.setImageDrawable(getResources().getDrawable(R.drawable.ic_bronce));
        }else if(score>120 && score<=240){
            img_coin.setImageDrawable(getResources().getDrawable(R.drawable.ic_plata));
        }else if(score>240){
            img_coin.setImageDrawable(getResources().getDrawable(R.drawable.ic_oro));
        }
    }

    private void configureNavigationView() {
        /*
        * Configuración de la barra lateral de navegación, envío de datos de usuario
        * */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        if (user != null) {
            //Datos del usuario logueado

            String email = user.getEmail();

            final TextView navHeaderTitle = (TextView) header.findViewById(R.id.nav_header_title);
            final TextView navHeaderSubtitle = (TextView) header.findViewById(R.id.nav_header_subtitle);
            final ImageView imageView = (ImageView) header.findViewById(R.id.imageView);

            navHeaderSubtitle.setText(email);

            configureDBReference();

            this.userRef.child(mHome.user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        AUser user = dataSnapshot.getValue(AUser.class);
                        if (user != null) {
                            String displayName = user.getmUserName();
                            String image = user.getmPhotoUrl();

                            LinearLayout tipBtnContainer = (LinearLayout) findViewById(R.id.tip_button_container);
                            if (user.getIsAdmin() == 1) {
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

    private void configureDBReference() {
        if (WelcomeActivity.CURRENT_APP_VERSION.equals("A")) {
            this.userRef = mDatabase.child("users");
        } else {
            this.userRef = mDatabase.child("users-reflexive");
        }
    }

    private void addListenerToFloatButton() {
        /*
        * Configuración de las acciones del botón flotante.
        * */
        final FloatingActionsMenu btn_actions_menu = (FloatingActionsMenu) findViewById(R.id.btn_actions_menu);
        final FloatingActionButton camera_action = (FloatingActionButton) findViewById(R.id.btn_add_post_camera);


        camera_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera_intent = new Intent(getBaseContext(), AddPost.class);
                camera_intent.putExtra("source", "camera");
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

    private void configureToolbarAndToggleActionBar() {
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

        if (WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
            simulation = new Simulation();
        else
            simulationv2 = new Simulationv2();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(home, "Inicio");
        adapter.addFragment(myItems, "Mis Items");

        if (WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
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

                switch (tab.getPosition()) {
                    case 0:
                        TextView hView = ((TextView) tab.getCustomView());
                        if (hView != null) {
                            hView.setTextColor(getResources().getColor(R.color.tab_text_enabled));
                            hView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_home, 0, 0);
                        }

                        TextView ht1 = ((TextView) tabLayout.getTabAt(1).getCustomView());
                        if (ht1 != null) {
                            ht1.setTextColor(getResources().getColor(R.color.tab_text_disabled));
                            ht1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_items_disabled, 0, 0);
                        }

                        TextView ht2 = ((TextView) tabLayout.getTabAt(2).getCustomView());
                        if (ht2 != null) {
                            ht2.setTextColor(getResources().getColor(R.color.tab_text_disabled));
                            ht2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_simulation_disabled, 0, 0);
                        }

                        TextView ht3 = ((TextView) tabLayout.getTabAt(3).getCustomView());
                        if (ht3 != null) {
                            ht3.setTextColor(getResources().getColor(R.color.tab_text_disabled));
                            ht3.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_tips_disabled, 0, 0);
                        }

                        break;
                    case 1:
                        TextView tView = ((TextView) tab.getCustomView());
                        if (tView != null) {
                            tView.setTextColor(getResources().getColor(R.color.tab_text_enabled));
                            tView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_items, 0, 0);
                        }

                        TextView tt1 = ((TextView) tabLayout.getTabAt(0).getCustomView());
                        if (tt1 != null) {
                            tt1.setTextColor(getResources().getColor(R.color.tab_text_disabled));
                            tt1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_home_disabled, 0, 0);
                        }

                        TextView tt2 = ((TextView) tabLayout.getTabAt(2).getCustomView());
                        if (tt2 != null) {
                            tt2.setTextColor(getResources().getColor(R.color.tab_text_disabled));
                            tt2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_simulation_disabled, 0, 0);
                        }

                        TextView tt3 = ((TextView) tabLayout.getTabAt(3).getCustomView());
                        if (tt3 != null) {
                            tt3.setTextColor(getResources().getColor(R.color.tab_text_disabled));
                            tt3.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_tips_disabled, 0, 0);
                        }

                        //Log.v("Items", "Mis items");
                        //if(myItems!=null)
                        //    myItems.loadItems();
                        break;
                    case 2:
                        TextView tiView = ((TextView) tab.getCustomView());
                        if (tiView != null) {
                            tiView.setTextColor(getResources().getColor(R.color.tab_text_enabled));
                            tiView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_simulation, 0, 0);
                        }

                        TextView it1 = ((TextView) tabLayout.getTabAt(0).getCustomView());
                        if (it1 != null) {
                            it1.setTextColor(getResources().getColor(R.color.tab_text_disabled));
                            it1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_home_disabled, 0, 0);
                        }

                        TextView it2 = ((TextView) tabLayout.getTabAt(1).getCustomView());
                        if (it2 != null) {
                            it2.setTextColor(getResources().getColor(R.color.tab_text_disabled));
                            it2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_items_disabled, 0, 0);
                        }

                        TextView it3 = ((TextView) tabLayout.getTabAt(3).getCustomView());
                        if (it3 != null) {
                            it3.setTextColor(getResources().getColor(R.color.tab_text_disabled));
                            it3.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_tips_disabled, 0, 0);
                        }

                        //Log.v("Items", "Simulación");
                        if (WelcomeActivity.CURRENT_APP_VERSION.equals("A")) {
                            if (simulation.tView != null) {
                                Log.v("Simu", "simulación");
                                simulation.loadData(simulation.tView);
                            }
                        } else {
                            if (simulationv2.tView != null) {
                                simulationv2.loadItems(simulationv2.tView);
                            }
                        }

                        break;

                    case 3:
                        TextView tnView = ((TextView) tab.getCustomView());
                        if (tnView != null) {
                            tnView.setTextColor(getResources().getColor(R.color.tab_text_enabled));
                            tnView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_tips, 0, 0);
                        }

                        TextView nt1 = ((TextView) tabLayout.getTabAt(0).getCustomView());
                        if (nt1 != null) {
                            nt1.setTextColor(getResources().getColor(R.color.tab_text_disabled));
                            nt1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_home_disabled, 0, 0);
                        }

                        TextView nt2 = ((TextView) tabLayout.getTabAt(1).getCustomView());
                        if (nt2 != null) {
                            nt2.setTextColor(getResources().getColor(R.color.tab_text_disabled));
                            nt2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_items_disabled, 0, 0);
                        }

                        TextView nt3 = ((TextView) tabLayout.getTabAt(2).getCustomView());
                        if (nt3 != null) {
                            nt3.setTextColor(getResources().getColor(R.color.tab_text_disabled));
                            nt3.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_simulation_disabled, 0, 0);
                        }

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
        tabTwo.setTextColor(getResources().getColor(R.color.tab_text_disabled));
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_items_disabled, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);

        if (WelcomeActivity.CURRENT_APP_VERSION.equals("R"))
            tabThree.setText("Reporte");
        else
            tabThree.setText("Simulación");

        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_simulation_disabled, 0, 0);
        tabThree.setTextColor(getResources().getColor(R.color.tab_text_disabled));
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("Tips");
        tabFour.setTextColor(getResources().getColor(R.color.tab_text_disabled));
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_tips_disabled, 0, 0);
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

        if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
            getMenuInflater().inflate(R.menu.m_home_corus, menu);
        }else{
            getMenuInflater().inflate(R.menu.m_home, menu);
        }

        for (int i = 0; i< menu.size() ;i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.getItemId() == R.id.gamification) {
                View view = MenuItemCompat.getActionView(menuItem);
                if (view != null) {
                    view.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), QuestionHistoricActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }else if(menuItem.getItemId() == R.id.gamification_corus){
                View view = MenuItemCompat.getActionView(menuItem);
                if (view != null) {
                    view.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), GamificationActivity.class);
                            startActivity(intent);
                        }
                    });
                    loadScore(view);
                }
            }
        }



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.v("Gamification", "Menu selected");
        switch(item.getItemId()){
            case R.id.gamification:
                Intent gamificationIntent = new Intent(getContext(), GamificationActivity.class);
                startActivity(gamificationIntent);

                break;
        }
        return true;
    }

    public void doThis(MenuItem item){
        Toast.makeText(this, "Hello World", Toast.LENGTH_LONG).show();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            Intent confIntent = new Intent(getContext(), ConfigurationActivity.class);
            startActivity(confIntent);
        }else if(id == R.id.nav_intro){
            WelcomeActivity.isOnRepeatTutorial = true;
            Intent intent = new Intent(getContext(), WelcomeActivity.class);
            startActivity(intent);
        }

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
        tipDetailIntent.putExtra("id", item.getId());

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
