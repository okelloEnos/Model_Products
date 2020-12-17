package com.okellosoftwarez.modelfarm;

import com.google.firebase.database.Exclude;

public class orderModel {
    String prdOrderedName, prdOrderedCapacity, prdOrderedTotal, prdOrderImage, prdOrderKey,
            prdOrderPhone, prdOrderLocation, prdOrderedMail, prdRemCapacity;


    public orderModel() {
    }

    public orderModel(String prdOrderedName, String prdOrderedCapacity, String prdOrderedTotal,
                      String prdOrderImage, String prdOrderPhone, String prdOrderLocation, String Mail, String prdRemCapacity) {
        this.prdOrderedName = prdOrderedName;
        this.prdOrderedCapacity = prdOrderedCapacity;
        this.prdOrderedTotal = prdOrderedTotal;
        this.prdOrderImage = prdOrderImage;
        this.prdOrderPhone = prdOrderPhone;
        this.prdOrderLocation = prdOrderLocation;
        this.prdOrderedMail = Mail;
        this.prdRemCapacity = prdRemCapacity;
    }

    public String getPrdRemCapacity() {
        return prdRemCapacity;
    }

    public void setPrdRemCapacity(String prdRemCapacity) {
        this.prdRemCapacity = prdRemCapacity;
    }

    public String getPrdOrderedMail() {
        return prdOrderedMail;
    }

    public void setPrdOrderedMail(String prdOrderedMail) {
        this.prdOrderedMail = prdOrderedMail;
    }

    public String getPrdOrderLocation() {
        return prdOrderLocation;
    }

    public void setPrdOrderLocation(String prdOrderLocation) {
        this.prdOrderLocation = prdOrderLocation;
    }

    public String getPrdOrderPhone() {
        return prdOrderPhone;
    }

    public void setPrdOrderPhone(String prdOrderPhone) {
        this.prdOrderPhone = prdOrderPhone;
    }

    public String getPrdOrderImage() {
        return prdOrderImage;
    }

    public void setPrdOrderImage(String prdOrderImage) {
        this.prdOrderImage = prdOrderImage;
    }

    public String getPrdOrderedName() {
        return prdOrderedName;
    }

    public void setPrdOrderedName(String prdOrderedName) {
        this.prdOrderedName = prdOrderedName;
    }

    public String getPrdOrderedCapacity() {
        return prdOrderedCapacity;
    }

    public void setPrdOrderedCapacity(String prdOrderedCapacity) {
        this.prdOrderedCapacity = prdOrderedCapacity;
    }

    public String getPrdOrderedTotal() {
        return prdOrderedTotal;
    }

    public void setPrdOrderedTotal(String prdOrderedTotal) {
        this.prdOrderedTotal = prdOrderedTotal;
    }

    @Exclude
    public String getPrdOrderKey() {
        return prdOrderKey;
    }

    @Exclude
    public void setPrdOrderKey(String prdOrderKey) {
        this.prdOrderKey = prdOrderKey;
    }
}
