package com.example.keepfit;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.After;

import android.content.Context;
import android.view.View;
import android.widget.VideoView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class VideoActivityTest {
    @Mock
    File localFile;
    VideoView v;

    private String refTitle = "Body_Project_Exercise_Video.4a776594-0054-4c81-b94f-72cd4e8bce05";
    private String prefix = "refTitle";
    private String suffix = "mp4";

    @Before
    public void beforeEach() throws IOException {
        localFile = File.createTempFile(prefix, suffix);
    }

    @Test
    public void checkVideoPlay() {
        try {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference ref = storageRef.child("/videos/" + refTitle + ".mp4");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //check that file downloaded successfully by checking that Uri is not null
                    assertNotNull(Uri.fromFile(localFile));
                }
            });

        }
        catch (Exception e) {
            System.out.println("could not download");
        }
    }
    @After
    public void tearDown() {
        System.setOut(standardOut);
    }

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @Before
    public void beforeEach2() throws IOException {
        localFile = File.createTempFile(prefix, suffix);
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    public void checkVideoPlayFail() {
        try {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference ref = storageRef.child("/videos/" + refTitle + ".mp4");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //video null
                    //v.setVideoURI(null);
                }
            });

        }
        catch (Exception e) {
            //check that system prints error message
            System.out.println("could not download");
            assertEquals("could not download", outputStreamCaptor.toString()
                    .trim());
        }
    }
    @After
    public void tearDown2() {
        System.setOut(standardOut);
    }

}