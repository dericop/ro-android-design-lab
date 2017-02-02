package com.ucaldas.ro.reduccionobesidad;

/**
 * Created by disenoestrategico on 2/02/17.
 */

public class AUser {

    private String mUid;
    private String mUserName;
    private String mEmail;
    private String mPhotoUrl;

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

}
