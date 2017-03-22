package com.ucaldas.ro.reduccionobesidad;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by disenoestrategico on 2/02/17.
 */

public class AUser {

    private String mUid;
    private String mUserName;
    private String mEmail;
    private String mPhotoUrl;
    private String mGender;
    private long mWeight;

    public AUser(){

    }

    public AUser(String uid, String userName, String email, String photoUrl){
        mUid = uid;
        mUserName = userName;
        mEmail = email;
        mPhotoUrl = photoUrl;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }

    public void setmPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }

    @Exclude
    public HashMap<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("mUid", mUid);
        result.put("mUserName", mUserName);
        result.put("mEmail", mEmail);
        result.put("mPhotoUrl", mPhotoUrl);
        result.put("mGender", mGender);
        result.put("mWeight", getmWeight());

        return result;

    }

    public String getmGender() {
        return mGender;
    }

    public void setmGender(String mGender) {
        this.mGender = mGender;
    }

    public long getmWeight() {
        return mWeight;
    }

    public void setmWeight(long mWeight) {
        this.mWeight = mWeight;
    }
}
