package com.example.keepfit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.junit.Test;
import java.util.Map;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class LivestreamActivityTest {
    @Mock
    Bundle b;

    String zoomLink = "https://usc.zoom.us/j/71935379";


    @Test
    public void checkLiveStreamActivityNotNull() {
        //check that given a link, it should be able to parse it as not null
        assertNotNull(Uri.parse(zoomLink));
    }

}
