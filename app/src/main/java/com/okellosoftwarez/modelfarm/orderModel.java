package com.okellosoftwarez.modelfarm;

public class orderModel {
    String prdOrderedName, prdOrderedCapacity, prdOrderedTotal, prdOrderImage;

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
}
