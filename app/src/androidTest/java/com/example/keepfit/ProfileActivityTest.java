package com.example.keepfit;
import android.content.Intent;
import android.content.SharedPreferences;
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
public class ProfileActivityTest {
    private String username = "kaitlyn";

    @Test
    public void getLikedVideosActivity() {
        String videoReferenceTitle = "no_metadata_test_1.4ce2db05-b0b2-4f6c-b5db-00d09356665c";

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Likes");

        ref.child(username)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, String> results = (Map<String, String>) snapshot.getValue();
                        if(results!=null){
                            for(Map.Entry<String, String> entry : results.entrySet()){
                                assertEquals(videoReferenceTitle, entry.getValue());
                                }
                            }
                        }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Test
    public void getUploadedVideosActivity(){
        String vidReferenceTitle = "no_metadata_test_1.4ce2db05-b0b2-4f6c-b5db-00d09356665c";
        String vidDisplayTitle = "no metadata test 1";

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Video References");

        ref.orderByChild("uploadingUser").equalTo(username)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                        if(results!=null){
                            for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                                assertEquals(vidReferenceTitle, entry.getValue().get("reference title"));
                                assertEquals(vidDisplayTitle, entry.getValue().get("title"));
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }
}
