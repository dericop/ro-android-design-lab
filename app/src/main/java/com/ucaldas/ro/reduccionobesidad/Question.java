package com.ucaldas.ro.reduccionobesidad;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by disenoestrategico on 24/05/17.
 */

public class Question {
    private String id;
    private String correct;
    private String response1;
    private String response2;
    private long startDate;
    private long endDate;
    private String title;

    public Question(){}

    public Question(String id, String correct, String response1, String response2, long startDate, long endDate,
                    String title){
        this.id = id;
        this.correct = correct;
        this.response1 = response1;
        this.response2 = response2;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String getResponse1() {
        return response1;
    }

    public void setResponse1(String response1) {
        this.response1 = response1;
    }

    public String getResponse2() {
        return response2;
    }

    public void setResponse2(String response2) {
        this.response2 = response2;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
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

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("correct", correct);
        result.put("response1", response1);
        result.put("response2", response2);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("title", title);

        return result;
    }
}
