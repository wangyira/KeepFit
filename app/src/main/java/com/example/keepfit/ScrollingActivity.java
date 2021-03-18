package com.example.keepfit;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
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

        try{
            findViewById(R.id.videoView).setVisibility(View.VISIBLE);
            final File localFile = File.createTempFile("testing1", "mp4");
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference videoRef = storageRef.child("/videos/no_metadata_test_1.4ce2db05-b0b2-4f6c-b5db-00d09356665c.mp4");
            videoRef.getFile(localFile).addOnSuccessListener(
                    (OnSuccessListener) (TaskSnapshot) -> {
                        Toast.makeText(ScrollingActivity.this, "Download complete", Toast.LENGTH_LONG).show();
                        //final VideoView videoView = (VideoView) findViewById(R.id.videoView);
                        videoView.setVideoURI(Uri.fromFile(localFile));
                        videoView.start();
                    }
            );
        } catch(Exception e){
            System.out.println("could not download");
        }

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}