package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import com.getbase.floatingactionbutton.FloatingActionButton;
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
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class mHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static Context context;
    private static String CURRENT_APP_VERSION = "A"; //Existen dos opciones "A", "R"

    /* Atributos para el control de usuarios */
    static FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_home);

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
            String displayName = user.getDisplayName();
            String email = user.getEmail();
            Uri image = user.getPhotoUrl();

            //Actualización de titulo del menú lateral
            TextView navHeaderTitle = (TextView)header.findViewById(R.id.nav_header_title);
            navHeaderTitle.setText(displayName);

            //Actualización de subtitulo del menu lateral
            TextView navHeaderSubtitle = (TextView)header.findViewById(R.id.nav_header_subtitle);
            navHeaderSubtitle.setText(email);

            Log.v("AUser", image+"");

            //Actualización de la imagen de perfil
            ImageView imageView = (ImageView)header.findViewById(R.id.imageView);
            Picasso.with(getBaseContext()).load(image).into(imageView);
        }
    }

    private void addListenerToFloatButton(){
        /*
        * Configuración de las acciones del botón flotante.
        * */

        final FloatingActionButton camera_action = (FloatingActionButton) findViewById(R.id.btn_add_post_camera);
        camera_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera_intent = new Intent(getBaseContext(), AddPost.class);
                camera_intent.putExtra("source","camera");
                startActivity(camera_intent);
            }
        });

        FloatingActionButton gallery_action = (FloatingActionButton) findViewById(R.id.btn_add_post_gallery);
        gallery_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery_intent = new Intent(getBaseContext(), AddPost.class);
                gallery_intent.putExtra("source", "gallery");
                startActivity(gallery_intent);
            }
        });
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
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Home(), "Inicio");
        adapter.addFragment(new MyItems(), "Mis Items");

        if(mHome.CURRENT_APP_VERSION.equals("R"))
            adapter.addFragment(new Simulation(), "Simulación");
        else
            adapter.addFragment(new Simulationv2(), "Simulación");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        createTabIcons();
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
        tabThree.setText("Simulación");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_simulation, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);
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

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
