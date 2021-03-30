package com.example.keepfit;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class FirebaseAuthActivityTest {

    private String id = "4Uy3YUSpFgW78DuTe84CzPxBNpO2";
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users").child(id);
        //checks to make sure that when the user registers an account, their email and username are correctly stored
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                assertEquals(username, map.get("username"));
                assertEquals(email, map.get("email"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Test
    public void checkCreateProfileActivity() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("UserInformation").child(id);
        //checks to make sure that when the user creates their profile (fills in personal info),
        //their information is correctly saved into the UserInformation table
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                assertEquals(birthday, map.get("birthday"));
                assertEquals(gender, map.get("gender"));
                assertEquals(height, map.get("height"));
                assertEquals(phonenumber, map.get("phonenumber"));
                assertEquals(weight, map.get("weight"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Test
    public void checkProfilePicture() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("UserInformation").child(id);
        //checks to make sure that when the user adds a profile picture, it is correctly stored in Firebase
        //when an image is uploaded, the pickey and referenceTitle should be populated in the UserInformation table
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                assertEquals(pickey, map.get("pickey"));
                assertEquals(referenceTitle, map.get("referenceTitle"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
