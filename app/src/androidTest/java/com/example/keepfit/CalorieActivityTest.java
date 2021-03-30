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
public class CalorieActivityTest {
    private String id = "-MWwqIKZ04aG-U950MZm";
    private String expectedExercise = "Jogging";
    private double expectedCalories = 45.351473922902485;
    private Long expectedTime = 1200L;
    private String expectedUser = "kaitlyn";
    @Test
    public void checkCalorieData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("CaloriesTable").child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                //check that values in livestream details exist
                assertEquals(expectedExercise, map.get("exerciseTitle"));
                assertEquals(expectedUser, map.get("username"));
                assertEquals(expectedCalories, map.get("myCaloriesBurned"));
                assertEquals(expectedTime, map.get("myTime"));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
