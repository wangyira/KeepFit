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
import com.google.firebase.firestore.FirebaseFirestore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class VideoUploadActivityTest {

    private String id = "-MW4xTNwuzjgTIKOgoqz";
    private String expectedDifficulty = "Beginner";
    //private String expectedRefTitle = "no_metadata_test_1.4ce2db05-b0b2-4f6c-b5db-00d09356665c";
    //private String expectedTag = "Aerobic";
    //private String expectedTitle = "no metadata test 1";
    //private String expectedUploadingUser = "ritikadendi1";

    @Test
    public void checkVideoInfo() {
        //FirebaseApp.initializeApp(getContext());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Video References").child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                //check that all values in video reference exist
                assertEquals(expectedDifficulty, map.get("difficulty"));
                //assertEquals(expectedRefTitle, map.get("reference title"));
                //assertEquals(expectedTag, map.get("tag"));
                //assertEquals(expectedTitle, map.get("title"));
                //assertEquals(expectedUploadingUser, map.get("uploadingUser"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }
}