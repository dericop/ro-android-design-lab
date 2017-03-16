package com.ucaldas.ro.reduccionobesidad;

import com.google.firebase.database.Exclude;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by disenoestrategico on 1/03/17.
 */

public class Comment {
    private String id;
    private String detail;
    private long date;
    private String user;
    private String userPhoto;

    public Comment(){

    }

    public Comment(String detail, long date, String id){
        this.setDetail(detail);
        this.setDate(date);
        this.setId(id);
    }

    public String getUserPhoto(){ return userPhoto; }

    public void setUserPhoto(String userPhoto){ this.userPhoto = userPhoto; }

    public String getUser(){ return user; }

    public void setUser(String user){ this.user = user; }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("detail", detail);
        result.put("date", date);
        result.put("user", user);
        result.put("userPhoto", userPhoto);

        return result;
    }
}
