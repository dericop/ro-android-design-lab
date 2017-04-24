package com.ucaldas.ro.reduccionobesidad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

            savePost(new Post());


    }

    private void savePost(Post lead) {

    }

    public List<Post> getLeads() {
        return new ArrayList<>(leads.values());
    }
}
