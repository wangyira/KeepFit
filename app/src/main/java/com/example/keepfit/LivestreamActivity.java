package com.example.keepfit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

//import com.example.keepfitvideolist.R;

public class LivestreamActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.livestreams);
        //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://usc.zoom.us/my/elainen"));
        //startActivity(browserIntent);


        String zoomLink = new String();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            zoomLink = extras.getString("zoomLink");
        }

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(zoomLink));
        startActivity(browserIntent);
    }
}