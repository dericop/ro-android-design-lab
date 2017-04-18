package com.ucaldas.ro.reduccionobesidad;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by disenoestrategico on 18/04/17.
 */

public class Tip {

    private String id;
    private String name;
    private String type;
    private String image;

    public Tip(){}

    public Tip(String id, String name, String type, String image){
        this.id = id;
        this.name = name;
        this.type = type;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Exclude
    public Map<String, String> toMap(){
        HashMap<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("type", type);
        map.put("image", image);

        return map;
    }





}
