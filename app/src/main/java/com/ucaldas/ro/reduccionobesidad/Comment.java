package com.ucaldas.ro.reduccionobesidad;

import java.util.Date;

/**
 * Created by disenoestrategico on 1/03/17.
 */

public class Comment {
    private String detail;
    private Date date;

    public Comment(){

    }

    public Comment(String detail, Date date){
        this.setDetail(detail);
        this.setDate(date);
    }


    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
