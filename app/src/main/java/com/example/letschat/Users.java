package com.example.letschat;

public class Users {

    private String name;
    private String profile_image;
    private String status;

    public Users(String name, String profile_image, String status) {
        this.name = name;
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

}
