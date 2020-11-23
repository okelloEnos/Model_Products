package com.okellosoftwarez.modelfarm;

public class NotificationModel {
    String nPhone, nTotal, nName;

    public NotificationModel(String nPhone, String nTotal, String nName) {
        this.nPhone = nPhone;
        this.nTotal = nTotal;
        this.nName = nName;
    }

    public NotificationModel() {
    }

    public String getnPhone() {
        return nPhone;
    }

    public void setnPhone(String nPhone) {
        this.nPhone = nPhone;
    }

    public String getnTotal() {
        return nTotal;
    }

    public void setnTotal(String nTotal) {
        this.nTotal = nTotal;
    }

    public String getnName() {
        return nName;
    }

    public void setnName(String nName) {
        this.nName = nName;
    }
}
