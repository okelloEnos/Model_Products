package com.okellosoftwarez.modelfarm;

import com.google.firebase.database.Exclude;

public class orderModel {
    String prdOrderedName, prdOrderedCapacity, prdOrderedTotal, prdOrderImage, prdOrderKey, prdOrderPhone;

    public orderModel(String prdOrderedName, String prdOrderedCapacity, String prdOrderedTotal) {
        this.prdOrderedName = prdOrderedName;
        this.prdOrderedCapacity = prdOrderedCapacity;
        this.prdOrderedTotal = prdOrderedTotal;
    }

    public orderModel() {
    }

    public orderModel(String prdOrderedName, String prdOrderedCapacity, String prdOrderedTotal, String prdOrderImage) {
        this.prdOrderedName = prdOrderedName;
        this.prdOrderedCapacity = prdOrderedCapacity;
        this.prdOrderedTotal = prdOrderedTotal;
        this.prdOrderImage = prdOrderImage;
    }

    public orderModel(String prdOrderedName, String prdOrderedCapacity, String prdOrderedTotal, String prdOrderImage, String prdOrderPhone) {
        this.prdOrderedName = prdOrderedName;
        this.prdOrderedCapacity = prdOrderedCapacity;
        this.prdOrderedTotal = prdOrderedTotal;
        this.prdOrderImage = prdOrderImage;
        this.prdOrderPhone = prdOrderPhone;
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
