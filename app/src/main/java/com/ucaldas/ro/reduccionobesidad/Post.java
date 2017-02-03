package com.ucaldas.ro.reduccionobesidad;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Dericop on 12/01/17.
 */

@IgnoreExtraProperties
public class Post {

    private String mId;
    private String mName;
    private String mCategory;
    private String mFrecuency;
    private String mImage;
    private String mDuration="";
    private String mUser;
    private long mAverage;
    private long mResult;
    private String mUserName;
    private String mTooShared;

    public Post(){

    }

    /*Constructores para la creación de un post*/

    public Post(String id, String name, String category, String frecuency, String image, String user) {
        mId = id;
        mName = name;
        mCategory = category;
        mFrecuency = frecuency;
        mImage = image;
        mUser = user;

    }

    public Post(String id, String name, String category, String frecuency, String image, String duration, String user){
        mId = id;
        mName = name;
        mCategory = category;
        mFrecuency = frecuency;
        mImage = image;
        mDuration = duration;
        mUser = user;
    }

    /*Constructores para obtener posts de la base de datos */

    public Post(String id, String name, String category, String frecuency, String image, String user, long result, long average, String userName, String tooShared) {
        mId = id;
        mName = name;
        mCategory = category;
        mFrecuency = frecuency;
        mImage = image;
        mUser = user;
        mResult = result;
        mAverage = average;
        mUserName = userName;
        mTooShared = tooShared;

    }

    public Post(String id, String name, String category, String frecuency, String image, String duration, String user, long result, long average, String userName, String tooShared){
        mId = id;
        mName = name;
        mCategory = category;
        mFrecuency = frecuency;
        mImage = image;
        mDuration = duration;
        mUser = user;
        mResult = result;
        mAverage = average;
        mUserName = userName;
        mTooShared = tooShared;
    }

    public String getmTooShared(){ return mTooShared; }

    public void setmTooShared(String tooShared){ mTooShared = tooShared; }

    public String getmUserName(){ return mUserName; }

    public void setmUserName(String userName){ mUserName = userName; }

    public long getmAverage(){ return mAverage; }

    public void setmAverage(long average){ mAverage = average; }

    public long getmResult(){ return mResult; }

    public void setmResult(long result){ mResult = result; }

    public String getmId(){ return mId; }

    public void setmId(String id){ mId = id; }

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
        result.put("id", mId);

        if(mResult != 0)
            result.put("result",mResult);

        if(mAverage != 0)
            result.put("average", mResult);

        if(!this.mDuration.equals(""))
            result.put("duration", mDuration);

        return result;
    }

    @Override
    public String toString() {
        return this.mName;
    }

}
