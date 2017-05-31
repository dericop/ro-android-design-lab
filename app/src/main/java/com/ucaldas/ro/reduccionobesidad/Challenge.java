package com.ucaldas.ro.reduccionobesidad;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;


public class Challenge {
    private String id;
    private long initDate;
    private long endDate;
    private String title;
    private long result;

    public Challenge() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getInitDate() {
        return initDate;
    }

    public void setInitDate(long initDate) {
        this.initDate = initDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getResult() {
        return result;
    }

    public void setResult(long result) {
        this.result = result;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> r = new HashMap<>();
        r.put("id", id);
        r.put("initDate", initDate);
        r.put("endDate", endDate);
        r.put("title", title);
        r.put("result", result);

        return r;
    }


}
