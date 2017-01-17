package com.ucaldas.ro.reduccionobesidad;

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
                    GenericTypeIndicator<Map<String, Post>> t = new GenericTypeIndicator<Map<String, Post> >() {};
                    Map<String, Post>  posts = dataSnapshot.getValue(t);
                    Log.v("DB", posts.get("-KaiTu_DYBk2-0uCHPKt")+"");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            savePost(new Post("Huevos, arepa y chocolate", "Agregado por: Juan Jaramillo", "Original: Carlos Lopez", R.drawable.sample_1));
        }



    }

    private void savePost(Post lead) {
        leads.put("1", lead);
    }

    public List<Post> getLeads() {
        return new ArrayList<>(leads.values());
    }
}
