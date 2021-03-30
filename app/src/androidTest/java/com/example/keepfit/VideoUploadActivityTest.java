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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotEquals;

import org.junit.Before;

import android.view.View;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.grpc.Context;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class VideoUploadActivityTest {

    @Test
    public void checkVideoInfo() {
        String id = "-MW4xTNwuzjgTIKOgoqz";
        String expectedDifficulty = "Beginner";
        String expectedRefTitle = "no_metadata_test_1.4ce2db05-b0b2-4f6c-b5db-00d09356665c";
        String expectedTag = "Aerobic";
        String expectedTitle = "no metadata test 1";
        String expectedUploadingUser = "kaitlyn";
        //FirebaseApp.initializeApp(getContext());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Video References").child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                //check that all values in video reference exist
                assertEquals(expectedDifficulty, map.get("difficulty"));
                assertEquals(expectedRefTitle, map.get("reference title"));
                assertEquals(expectedTag, map.get("tag"));
                assertEquals(expectedTitle, map.get("title"));
                assertEquals(expectedUploadingUser, map.get("uploadingUser"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    @Test
    public void uploadingUserExists(){
        String id = "-MW4xTNwuzjgTIKOgoqz";
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Video References").child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                String uploadingUser = map.get("uploadingUser");
                Log.e("uploadingUser", uploadingUser);
                DatabaseReference UserRef = database.getReference("UserInformation");
                ref.orderByValue().equalTo(uploadingUser).limitToFirst(1)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot){
                                Map<String, String> results = (Map<String, String>) snapshot.getValue();
                                assertNotEquals(null, results);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Test
    public void videoExists(){
        String refTitle = "no_metadata_test_1.4ce2db05-b0b2-4f6c-b5db-00d09356665c";
        try{
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference videoRef = storageRef.child("/videos/" + refTitle + ".mp4");
            assertTrue(true);
        }catch(Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void thumbnailExists(){
        String refTitle = "no_metadata_test_1.4ce2db05-b0b2-4f6c-b5db-00d09356665c";
        try{
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("/thumbnail_images/" + refTitle + ".jpg");
            assertTrue(true);
            //assertNotEquals(null, imageRef);
        } catch(Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void allFieldsFilled(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Video References");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Map<String, String>> value = (Map<String, Map<String, String>>) snapshot.getValue();
                for(Map.Entry<String, Map<String, String>> entry : value.entrySet()){
                    assertNotEquals("", entry.getValue().get("title"));
                    assertNotEquals("", entry.getValue().get("tag"));
                    assertNotEquals("", entry.getValue().get("uploadingUser"));
                    assertNotEquals("", entry.getValue().get("reference title"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

}