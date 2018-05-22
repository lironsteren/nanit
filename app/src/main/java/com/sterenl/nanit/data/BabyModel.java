package com.sterenl.nanit.data;

import org.joda.time.DateTime;

public class BabyModel {
    private String mName;
    private String mAvatar;
    private DateTime mBod;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public DateTime getBod() {
        return mBod;
    }

    public void setBod(DateTime bod) {
        mBod = bod;
    }
}
