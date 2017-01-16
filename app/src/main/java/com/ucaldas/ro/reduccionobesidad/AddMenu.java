package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddMenu extends AppCompatActivity {

    /*
    * ¿Como hacer un toast?
    * Toast.makeText(HelloGridView.this, text,Toast.LENGTH_SHORT).show();
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu); //Asignación de la vista que será controlada en esta clase.

        createAndConfigureToolbar();
        configureGrids();

    }

    private void createAndConfigureToolbar(){
        /*
        * Crea el toolbar, le cambia el color y lo habilita para acciones.
        * */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        changeStatusBarColor();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    private void configureGrids(){
        initActivitiesGrid();
        initFoodsGrid();
    }

    private void initActivitiesGrid(){
        /*
        * Asigna adaptador y crea eventos de escucha para el gridview de actividades
        * */
        GridView actGrid = (GridView) findViewById(R.id.activities_grid); //Obtiene el grid de la vista
        actGrid.setAdapter(new ActivityAdapter(this)); //Le asigna al grid un adaptador

        //Creación de eventos de click
        actGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                switch(position){
                    case 0: //¿Se ha clickeado el item nueva foto de actividades?
                        Intent intent = new Intent(getBaseContext(), AddPost.class);
                        intent.putExtra("SOURCE_ID", "activities");
                        startActivity(intent);
                        break;
                    case 1: //¿Se ha clickeado el item biblioteca de actividades?
                        Intent intentL = new Intent(getBaseContext(), LibraryActivity.class);
                        intentL.putExtra("SOURCE_ID", "activities");
                        startActivity(intentL);
                        break;
                }

            }
        });

    }

    private void initFoodsGrid(){
        /*
        * Asigna adaptador y crea eventos de escucha para el gridview de alimentos
        * */

        GridView foodsGrid = (GridView) findViewById(R.id.foodsGrid);
        foodsGrid.setAdapter(new FoodAdapter(this));

        foodsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                switch(position){ //¿Se ha clickeado el item nueva foto de alimentos?
                    case 0:
                        Intent intent = new Intent(getBaseContext(), AddPost.class);
                        intent.putExtra("SOURCE_ID", "foods");
                        startActivity(intent);
                        break;
                    case 1://¿Se hac clickeado el item biblioteca de actividades?
                        Intent intentL = new Intent(getBaseContext(), LibraryActivity.class);
                        intentL.putExtra("SOURCE_ID", "foods");
                        startActivity(intentL);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        /*
        * Habilita la navegación de la vista actual
        */
        finish();
        return false;
    }

    private void changeStatusBarColor() {
        /*
        * Se encarga de cambiar el color del menú superior
        * de los celulares con versión de android superior a llolipop*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
