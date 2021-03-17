package com.example.keepfit.authapp;

public class User {

	public String name;
    public String username;
    public String phoneno;
    public String email;
    public String gender;
    public String birthday;
    public String weight;
    public String height;

    public User(){

    }

    public User(String name, String username, String phoneno, String email, String gender, String birthday, String weight, String height){
    	this.name = name;
        this.username = username;
        this.phoneno = phoneno;
        this.email = email;
        this.gender = gender;
        this.birthday = birthday;
        this.weight = weight;
        this.height = height;
    }

    public String getUserName() {
        return name;
    }
    public String getUserUsername() {
        return username;
    }
    public String getUserPhoneno() {
        return phoneno;
    }
    public String getUserEmail() {
        return email;
    }
    public String getUserGender() {
        return gender;
    }
    public String getUserWeight() {
        return weight;
    }
    public String getUserHeight() {
        return height;
    }
    public String getUserBirthday() {
        return birthday;
    }

}
