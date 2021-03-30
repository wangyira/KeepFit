package com.example.keepfit;

import android.net.Uri;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class StartLivestreamActivityTest {
    private String expectedZoomLink = "https://usc.zoom.us/j/719353793";
    private String id = "-MWLAiEuugDAtXWCjBvx";
    private String expectedUser = "ritikadendi1";
    private String expectedExercise = "Anaerobic";
    private String expectedEndtime = "16:30";
    private Long expectedMaxppl = 25L;
    private String expectedRefTitle = "09ffaefd-02e1-4c12-ad88-190adb88a05a.jpg";
    private String expectedTitle = "workout with me";

    @Test
    public void checkLiveStream() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Livestream Details").child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                //check that values in livestream details exist
                assertEquals(expectedZoomLink, map.get("zoomLink"));
                assertEquals(expectedUser, map.get("uploadingUser"));
                assertEquals(expectedEndtime, map.get("endTime"));
                assertEquals(expectedExercise, map.get("exerciseType"));
                assertEquals(expectedMaxppl, map.get("maxNumberOfPeople"));
                assertEquals(expectedRefTitle, map.get("referenceTitle"));
                assertEquals(expectedTitle, map.get("title"));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
