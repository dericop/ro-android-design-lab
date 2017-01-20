package com.ucaldas.ro.reduccionobesidad;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Dericop on 12/01/17.
 */

public class PostRepository {

    private static PostRepository repository = new PostRepository();
    private HashMap<String, Post> leads = new HashMap<>();

    public static PostRepository getInstance() {
        return repository;
    }

    private PostRepository() {
        FirebaseUser user = mHome.user;
        Log.v("DB", "preasd");

        if(user != null){
            DatabaseReference database = FirebaseDatabase.getInstance().getReference("user-posts").child(user.getUid());
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    /*GenericTypeIndicator<Map<String, Post>> t = new GenericTypeIndicator<Map<String, Post> >() {};
                    Map<String, Post>  posts = dataSnapshot.getValue(t);
                    Log.v("DB", posts.get("-KaiTu_DYBk2-0uCHPKt")+"");*/
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            savePost(new Post());
        }

    }

    private void savePost(Post lead) {
        leads.put("1",new Post("Huevos, arepa y chocolates mas una empada con queso", "Agregado por: Juan Jaramillo", "Original: Carlos Lopez", "http://colombia.gastronomia.com/uploads/noticias/bandeja%20paisa.bndFdXYzajQ1bWw0SVp5aS8vMTQ3Mzc4MjMxNC8.jpg"));
        leads.put("2",new Post("Huevos, arepa y chocolate", "Juan Jaramillo", "Carlos Lopez", "http://www.ibikes.cl/store/image/cache/data/B813012-200x200.jpg"));
        leads.put("3",new Post("Huevos, arepa y chocolate", "Juan Jaramillo", "Carlos Lopez", "http://www.ibikes.cl/store/image/cache/data/B813012-200x200.jpg"));
        leads.put("4",new Post("Huevos, arepa y chocolate", "Juan Jaramillo", "Carlos Lopez", "http://www.ibikes.cl/store/image/cache/data/B813012-200x200.jpg"));
        leads.put("5",new Post("Huevos, arepa y chocolate", "Juan Jaramillo", "Carlos Lopez", "http://www.ibikes.cl/store/image/cache/data/B813012-200x200.jpg"));
    }

    public List<Post> getLeads() {
        return new ArrayList<>(leads.values());
    }
}
