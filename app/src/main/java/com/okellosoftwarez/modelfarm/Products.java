package com.okellosoftwarez.modelfarm;

import com.google.firebase.database.Exclude;

import java.io.Serializable;


public class Products implements Serializable {
    private String ID;
    private String name;
    private String phone;
    private String location;
    private String image;
    private String price;
    private String capacity;
    private String email;

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

    @Exclude
    public String getID() {
        return ID;
    }

    public Products(String name, String phone, String location, String image, String price, String capacity, String email) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.image = image;
        this.price = price;
        this.capacity = capacity;
        this.email = email;
    }

    public Products(String name, String location, String image, String price, String capacity) {
        this.name = name;
        this.location = location;
        this.image = image;
        this.price = price;
        this.capacity = capacity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public void setID(String ID) {
        this.ID = ID;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
