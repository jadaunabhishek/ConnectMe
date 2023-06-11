package com.sociallogin.connectme;

public class readwriteuserdata5 {
    public String NAME, EMAIL, PHONE, AGE, PASSWORD;

    public readwriteuserdata5(){};

    public readwriteuserdata5(String textname, String textemail, String textphone, String textage, String textpassword) {

        this.PHONE = textphone;
        this.NAME = textname;
        this.EMAIL = textemail;
        this.AGE = textage;
        this.PASSWORD = textpassword;
    }
}
