package com.okellosoftwarez.modelfarm.models;

public class userModel {
    private String userName, email, phone, location;

    public userModel() {
    }

    public userModel(String userName, String email, String phone, String location) {
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.location = location;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
