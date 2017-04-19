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
    private String description;
    private String image;
    private String app;

    public Tip(){}

    public Tip(String id, String name, String type, String description, String image, String app){
        this.id = id;
        this.name = name;
        this.type = type;
        this.image = image;
        this.description = description;
        this.app = app;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    @Exclude
    public Map<String, String> toMap(){
        HashMap<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("type", type);
        map.put("image", image);
        map.put("description", description);

        return map;
    }


}
