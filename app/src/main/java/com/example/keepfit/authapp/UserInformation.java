package com.example.keepfit.authapp;


public class UserInformation {

    public String email, name, phonenumber, gender, birthday, weight, height, pickey;

    public UserInformation(){

    }

    public UserInformation(String email, String name, String phonenumber, String gender, String birthday, String weight, String height, String pickey){
        this.email = email;
        this.name = name;
        this.phonenumber = phonenumber;
        this.gender = gender;
        this.birthday = birthday;
        this.weight = weight;
        this.height = height;
        this.pickey = pickey;
    }
}
