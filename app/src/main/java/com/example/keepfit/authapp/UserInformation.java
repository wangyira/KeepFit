package com.example.keepfit.authapp;


public class UserInformation {
    public String email, name, phonenumber, gender, birthday, weight, height, pickey, username, referenceTitle;

    public UserInformation() {}

    public UserInformation(String email, String name, String phonenumber, String gender, String birthday, String weight, String height, String pickey, String username, String referenceTitle){
        if (pickey == null) {
            this.pickey = "https://firebasestorage.googleapis.com/v0/b/keepfit-99e86.appspot.com/o/profilepictures%2Fdefavatar.png?alt=media&token=6e7fd7b9-db1d-49b9-91a8-57c86a79a7df";
            this.referenceTitle = "defavatar.png";
        }
        else {
            this.pickey = pickey;
            this.referenceTitle = referenceTitle;
        }
        this.email = email;
        this.name = name;
        this.phonenumber = phonenumber;
        this.gender = gender;
        this.birthday = birthday;
        this.weight = weight;
        this.height = height;
        //this.pickey = pickey;
        this.username = username;
        //this.referenceTitle = referenceTitle;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeight() {
        return height;
    }

    public String getPickey() {
        return pickey;
    }

    public String getUsername() {
        return username;
    }

    public String getReferenceTitle() {
        return referenceTitle;
    }
}
