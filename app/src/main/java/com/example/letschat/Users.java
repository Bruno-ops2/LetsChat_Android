package com.example.letschat;

import androidx.annotation.Nullable;

public class Users {

    private String name;
    private String uid;
    private String profile_image;
    private String status;

    public Users(String uid, String name, String profile_image, String status) {
        this.name = name;
        this.uid = uid;
        this.profile_image = profile_image;
        this.status = status;
    }


    //getters
    public String getName() {
        return name;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public String getStatus() {
        return status;
    }

    public String getUid() {
        return uid;
    }

    //setters
    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public void setUid(String uid) { this.uid = uid; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Users) {
            if(((Users)obj).uid.equals(this.uid))
                return true;
        }

        return false;
    }
}
