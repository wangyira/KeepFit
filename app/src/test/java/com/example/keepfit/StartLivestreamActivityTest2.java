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
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;

public class StartLivestreamActivityTest2 {
    @Test
    public void checkLiveStream() {
        //assertEquals("ERROR", StartLivestreamActivity.SaveDetails());
    }
}
