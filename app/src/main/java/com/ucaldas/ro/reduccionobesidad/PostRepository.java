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
        saveLead(new Post("Huevos, arepa y chocolate", "Agregado por: Juan Jaramillo", "Original: Carlos Lopez", R.drawable.sample_1));
        saveLead(new Post("Cereal con leche", "Agregado por: Daniel Rico", "Original: Daniel Rico", R.drawable.sample_2));
        saveLead(new Post("Montada en bici", "Agregado por: Sara bonz", "Origininal: Michal Jackson", R.drawable.sample_3));
        saveLead(new Post("Bandeja paisa", "Agregado por: Benito mirando", "Original: Benito peralta", R.drawable.sample_4));
        saveLead(new Post("Empanadas con arequipe light", "Agregado por: Juan jaramillo", "Original:", R.drawable.sample_f_1));
        saveLead(new Post("Leche con bocadillo", "Agregado por: Bernardo Acevedo", "Original:", R.drawable.sample_f_2));
        saveLead(new Post("Frijoles con chicharrón", "Agregado por: Juan Pablo", "Original: Mauricio Mejía", R.drawable.sample_f_3));
        saveLead(new Post("Lentejas con carne de cerdo", "Agregado por: Carlitos Amarello", "Original: Sofía Vergara", R.drawable.sample_f_4));
        saveLead(new Post("Avena con frutos secos", "Agregado por: Jonh Maeda", "Original: Bill Gates", R.drawable.sample_f_5));
        saveLead(new Post("Pan y Agua", "Agregado Por: Mirando fronteras", "Original: ", R.drawable.sample_1));
    }

    private void saveLead(Post lead) {
        leads.put(lead.getId(), lead);
    }

    public List<Post> getLeads() {
        return new ArrayList<>(leads.values());
    }
}
