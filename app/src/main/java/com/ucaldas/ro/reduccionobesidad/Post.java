package com.ucaldas.ro.reduccionobesidad;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dericop on 12/01/17.
 */

@IgnoreExtraProperties
public class Post{

    private String id;
    private String name;
    private String category;
    private String frecuency;
    private String image;
    private String duration ="";
    private String user;
    private long average;
    private long result;
    private String mUserName;
    private String last_share;
    private double r_pi;
    private double r_aa;
    private double r_gs;
    private double r_ch;
    private long countOfComments;
    public long replyCount;

    public Post(){

    }

    public Post(String id, String name, String category, String frecuency, String image, String user, long result, long average) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.frecuency = frecuency;
        this.image = image;
        this.user = user;
        this.result = result;
        this.average = average;
        last_share = "";
        countOfComments = 0;
    }

    public Post(String id, String name, String category, String frecuency, String image, String duration, String user, long result, long average, String userName, String tooShared){
        this.id = id;
        this.name = name;
        this.category = category;
        this.frecuency = frecuency;
        this.image = image;
        this.duration = duration;
        this.user = user;
        this.result = result;
        this.average = average;
        mUserName = userName;
        last_share = tooShared;
        countOfComments = 0;
    }

    public String getLast_share(){ return last_share; }

    public void setLast_share(String tooShared){ last_share = tooShared; }

    public String getmUserName(){ return mUserName; }

    public void setmUserName(String userName){ mUserName = userName; }

    public long getAverage(){ return average; }

    public void setAverage(long average){ this.average = average; }

    public long getResult(){ return result; }

    public void setResult(long result){ this.result = result; }

    public String getId(){ return id; }

    public void setId(String id){ this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFrecuency() {
        return frecuency;
    }

    public void setFrecuency(String frecuency) {
        this.frecuency = frecuency;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDuration(){ return duration; }

    public void setDuration(String duration){ this.duration = duration; }

    public void setUser(String user){ this.user = user; }

    public String getUser(){ return user; }

    public double getR_pi() {return r_pi;}

    public void setR_pi(double r_pi) {this.r_pi = r_pi;}

    public double getR_aa() {return r_aa;}

    public void setR_aa(double r_aa) {this.r_aa = r_aa;}

    public double getR_gs() {return r_gs;}

    public void setR_gs(double r_gs) {this.r_gs = r_gs;}

    public double getR_ch() {return r_ch;}

    public void setR_ch(double r_ch) {this.r_ch = r_ch;}

    public long getCountOfComments() { return countOfComments; }

    public void setCountOfComments(long countOfComments) { this.countOfComments = countOfComments; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("category", category);
        result.put("frecuency", frecuency);
        result.put("image", image);
        result.put("user", user);
        result.put("id", id);
        result.put("replyCount", replyCount);
        result.put("countOfComments", countOfComments);

        if(this.result != 0)
            result.put("result", this.result);

        if(average != 0)
            result.put("average", this.result);

        if(!this.duration.equals(""))
            result.put("duration", duration);

        if(this.r_pi != 0)
            result.put("pi", r_pi);

        if(this.r_pi != 0)
            result.put("aa", r_aa);

        if(this.r_pi != 0)
            result.put("gs", r_gs);

        if(this.r_pi != 0)
            result.put("ch", r_ch);

        return result;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(long replyCount) {
        this.replyCount = replyCount;
    }
}
