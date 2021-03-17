package com.example.keepfit.authapp;

import com.google.firebase.storage.StorageReference;

public class UserInformation {

    public String email, username, phonenumber, gender, birthday, weight, height, pickey;

    public UserInformation(){

    }

    public UserInformation(String email, String username, String phonenumber, String gender, String birthday, String weight, String height, String pickey){
        this.email = email;
        this.username = username;
        this.phonenumber = phonenumber;
        this.gender = gender;
        this.birthday = birthday;
        this.weight = weight;
        this.height = height;
        this.pickey = pickey;
    }
}
