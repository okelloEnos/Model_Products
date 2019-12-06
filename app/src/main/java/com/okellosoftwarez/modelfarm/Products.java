package com.okellosoftwarez.modelfarm;

import android.net.Uri;

import java.io.Serializable;


public class Products implements Serializable {
    private String ID;
    private String name;
    private String phone;
    private String location;
    private Uri imageUrl;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Products(String name, String phone, String location, String image) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.image = image;
    }


    public Products() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Products(String name, String phone, Uri imageUrl, String location) {
        this.name = name;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Uri getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
