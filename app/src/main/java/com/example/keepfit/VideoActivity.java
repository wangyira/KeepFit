package com.example.keepfit;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());*/
        VideoView videoView = findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        //videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" +
        //       R.raw.testvideo));
        //videoView.start();

        String referenceTitle = new String();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            referenceTitle = extras.getString("referenceTitle");
        }

        try{
            findViewById(R.id.videoView).setVisibility(View.VISIBLE);
            final File localFile = File.createTempFile("referenceTitle", "mp4");
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference videoRef = storageRef.child("/videos/" + referenceTitle + ".mp4");
            videoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //Toast.makeText(VideoActivity.this, "Download complete", Toast.LENGTH_LONG).show();
                    videoView.setVideoURI(Uri.fromFile(localFile));
                    videoView.start();
                }
            });
        } catch(Exception e){
            System.out.println("could not download");
        }
    }
}