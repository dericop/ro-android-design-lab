package com.ucaldas.ro.reduccionobesidad;

import android.net.Uri;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Dericop on 12/01/17.
 */

@IgnoreExtraProperties
public class Post {

    private String mName;
    private String mCategory;
    private String mFrecuency;
    private String mImage;
    private String mDuration="";
    private String mUser;

    public Post(){

    }

    public Post(String name, String category, String frecuency, String image, String user) {
        mName = name;
        mCategory = category;
        mFrecuency = frecuency;
        mImage = image;
        mUser = user;
    }

    public Post(String name, String category, String frecuency, String image, String duration, String user){
        mName = name;
        mCategory = category;
        mFrecuency = frecuency;
        mImage = image;
        mDuration = duration;
        mUser = user;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public String getmFrecuency() {
        return mFrecuency;
    }

    public void setmFrecuency(String mFrecuency) {
        this.mFrecuency = mFrecuency;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public String getmDuration(){ return mDuration; }

    public void setmDuration(String mDuration){ this.mDuration = mDuration; }

    public void setmUser(String mUser){ this.mUser = mUser; }

    public String getmUser(){ return mUser; }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", mName);
        result.put("category", mCategory);
        result.put("frecuency", mFrecuency);
        result.put("image", mImage);
        result.put("user", mUser);

        if(!this.mDuration.equals(""))
            result.put("duration", mDuration);

        return result;
    }

    @Override
    public String toString() {
        return this.mName;
    }
}
