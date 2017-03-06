package com.ucaldas.ro.reduccionobesidad;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by disenoestrategico on 1/03/17.
 */

public class Comment {
    private String id;
    private String detail;
    private long date;

    public Comment(){

    }

    public Comment(String detail, long date, String id){
        this.setDetail(detail);
        this.setDate(date);
        this.setId(id);
    }


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
}
