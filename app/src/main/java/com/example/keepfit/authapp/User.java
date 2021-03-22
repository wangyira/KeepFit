package com.example.keepfit.authapp;

public class User {
    public String username, email;

    public User(){

    }

    public String getUsername() {
        return username;
    }

    public User(String username, String email){
        this.username = username;
        this.email = email;
    }


}
