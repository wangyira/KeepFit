package com.example.keepfit;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import com.example.keepfit.authapp.User;

import static org.junit.Assert.assertNotEquals;

import org.junit.Before;

import android.content.Context;
import android.view.View;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FirebaseMainActivityTest {

    private FirebaseFirestore db;
    private String email = "kaitlyn@usc.edu";
    private String username = "kaitlyn";
    private String birthday = "04072000";
    private String gender = "Female";
    private String height = "55";
    private String phonenumber = "9494136186";
    private String weight = "100";
    private String pickey = "https://firebasestorage.googleapis.com/v0/b/keepfit-99e86.appspot.com/o/profilepictures%2Ff831def3-e1be-437c-ba83-5217af6a9354?alt=media&token=77581a6f-76db-4a7b-b1c9-a7ceed2bf66e";
    private String referenceTitle = "f831def3-e1be-437c-ba83-5217af6a9354";

    @Test
    public void checkRegisterActivity() {
        db = FirebaseFirestore.getInstance();
        //checks to make sure that when the user registers an account, their email and username are correctly stored
        DocumentReference docIdRef = db.collection("Users").document(username);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assertEquals(username, document.getString("username"));
                    assertEquals(email, document.getString("email"));
                } else {
                    Log.d("document", "Failed with: ", task.getException());
                }
            }
        });
    }

    @Test
    public void checkCreateProfileActivity() {
        db = FirebaseFirestore.getInstance();
        //checks to make sure that when the user creates their profile (fills in personal info),
        //their information is correctly saved into the UserInformation table
        DocumentReference docIdRef = db.collection("UserInformation").document(username);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assertEquals(birthday, document.getString("birthday"));
                    assertEquals(gender, document.getString("gender"));
                    assertEquals(height, document.getString("height"));
                    assertEquals(phonenumber, document.getString("phonenumber"));
                    assertEquals(weight, document.getString("weight"));
                } else {
                    Log.d("document", "Failed with: ", task.getException());
                }
            }
        });
    }

    @Test
    public void checkProfilePicture() {
        db = FirebaseFirestore.getInstance();
        //checks to make sure that when the user adds a profile picture, it is correctly stored in Firebase
        //when an image is uploaded, the pickey and referenceTitle should be populated in the UserInformation table
        DocumentReference docIdRef = db.collection("UserInformation").document(username);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assertEquals(pickey, document.getString("pickey"));
                    assertEquals(referenceTitle, document.getString("referenceTitle"));
                } else {
                    Log.d("document", "Failed with: ", task.getException());
                }
            }
        });
    }
}
