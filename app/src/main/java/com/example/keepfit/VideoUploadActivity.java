package com.example.keepfit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.keepfit.authapp.ProfileActivityEdits;
import com.example.keepfit.calories.CalorieActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

//https://demonuts.com/pick-video-gallery-camera-android/
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class VideoUploadActivity extends AppCompatActivity {

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    Uri videoURI;
    Uri imageURI;

    private String title = null;
    private String tag = null;
    private String difficulty = null;
    private String time = null;
    private String videoPath = null;
    private String imagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoupload);

        //navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set upload selected
        bottomNavigationView.setSelectedItemId(R.id.nav_upload);

        //perform itemselectedlistener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                switch (menuItem.getItemId()){
                    case R.id.nav_account:
                        startActivity(new Intent(getApplicationContext(), ProfileActivityEdits.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_calorie:
                        startActivity(new Intent(getApplicationContext(), CalorieActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_upload:
                        return true;
                    case R.id.nav_livestream:
                        startActivity(new Intent(getApplicationContext(), StartLivestreamActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        //https://demonuts.com/pick-video-gallery-camera-android/
        Button getVideobtn = (Button) findViewById(R.id.selectVideoButton);
        Button uploadbtn = (Button) findViewById(R.id.uploadVideoButton);
        Button getImagebtn = (Button) findViewById(R.id.selectImageButton);
        Spinner tagSpinner = (Spinner) findViewById(R.id.tagDropDown);
        Spinner diffSpinner = (Spinner) findViewById(R.id.difficultyDropDown);

        //SET VISIBILITY
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        findViewById(R.id.success_message).setVisibility(View.GONE);
        findViewById(R.id.videoView).setVisibility(View.GONE);


        getVideobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideoFromGallery();
            }
        });
        getImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery();
            }
        });
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                tag = (String) parent.getItemAtPosition(pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        diffSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                difficulty = (String) parent.getItemAtPosition(pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //checks if the required values have been entered
    //returns 1 if valid or 0 if something is missing
    private boolean validValues() {
        //get values
        EditText mEdit = (EditText) findViewById(R.id.titleInput);
        title = mEdit.getText().toString();
        mEdit = (EditText) findViewById(R.id.editTextTime2);
        time = mEdit.getText().toString();

        if (title == null || title.length() < 1 || title.length() > 60) {
            AlertDialog.Builder titleDialog = new AlertDialog.Builder(this);
            titleDialog.setTitle("Please enter a title between 1-60 characters in length.");
            String[] pictureDialogItems = {"OK"};
            titleDialog.setItems(pictureDialogItems,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            titleDialog.show();
            return false;
        } else if (videoPath == null) {
            AlertDialog.Builder videoDialog = new AlertDialog.Builder(this);
            videoDialog.setTitle("Please select a video.");

            String[] pictureDialogItems = {
                    "OK"};
            videoDialog.setItems(pictureDialogItems,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

            videoDialog.show();
            return false;
        } else if (imagePath == null) {
            AlertDialog.Builder videoDialog = new AlertDialog.Builder(this);
            videoDialog.setTitle("Please select a thumbnail image.");

            String[] pictureDialogItems = {
                    "OK"};
            videoDialog.setItems(pictureDialogItems,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

            videoDialog.show();
            return false;
        } else if (time.length() > 6 || (time.length() > 0 && time.length() < 4)) {
            AlertDialog.Builder titleDialog = new AlertDialog.Builder(this);
            titleDialog.setTitle("Please enter a time of the form min:sec, i.e. 00:05 ");
            String[] pictureDialogItems = {"OK"};
            titleDialog.setItems(pictureDialogItems,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            titleDialog.show();
            return false;
        }
        return true;
    }

    private void upload() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        Button uploadButton = (Button) findViewById(R.id.uploadVideoButton);
        TextView successMessage = (TextView) findViewById(R.id.success_message);
        UUID randomUUID = UUID.randomUUID();

        if (!validValues()) {
            return;
        }
        String noSpaceTitle = title.replaceAll("\\s", "_");

        //upload video to storage
        File file = new File(videoPath);
        StorageReference videoRef = storage.getReference("videos/" + noSpaceTitle + "." + randomUUID + ".mp4");
        /*StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("title", title)
                .setCustomMetadata("tag", tag)
                .setCustomMetadata("difficulty", difficulty)
                .setCustomMetadata("time", time)
                .build();*/


        progressBar.setVisibility(View.VISIBLE);
        uploadButton.setEnabled(false);

        //UploadTask uploadTask = videoRef.putFile(videoURI, metadata);
        UploadTask uploadTask = videoRef.putFile(videoURI);
        uploadTask.addOnSuccessListener(VideoUploadActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                StorageReference imageRef = storage.getReference("thumbnail_images/" + noSpaceTitle + "." + randomUUID + ".jpg");
                StorageMetadata metadataI = new StorageMetadata.Builder()
                        .setCustomMetadata("title", title)
                        .setCustomMetadata("video reference", url)
                        .build();

                UploadTask imageUploadTask = imageRef.putFile(imageURI, metadataI);
                imageUploadTask.addOnSuccessListener(VideoUploadActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot imageTaskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        uploadButton.setEnabled(true);
                        successMessage.setVisibility(View.VISIBLE);

                        String imageUrl = imageTaskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                        //add reference to video in realtime database
                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference mRootRef = database.getReference("");
                        DatabaseReference mVideosRef = mRootRef.child("Video References");

                        Map<String, String> vidRef = new HashMap<String, String>();
                        vidRef.put("time", time);
                        vidRef.put("difficulty", difficulty);
                        vidRef.put("tag", tag);
                        vidRef.put("title", title);
                        vidRef.put("reference title", noSpaceTitle + "." + randomUUID.toString());

                        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
                        String username = sharedPref.getString("username", null);
                        vidRef.put("uploadingUser", username);

                        mVideosRef.push().setValue(vidRef);
                    }
                });
            }
        });
    }

    /*private void download(){
        try{
            findViewById(R.id.videoView).setVisibility(View.VISIBLE);
            final File localFile = File.createTempFile("testing1", "mp4");
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference videoRef = storageRef.child("/videos/no_metadata_test_1.4ce2db05-b0b2-4f6c-b5db-00d09356665c.mp4");
            videoRef.getFile(localFile).addOnSuccessListener(
                    (OnSuccessListener) (TaskSnapshot) -> {
                        Toast.makeText(VideoUploadActivity.this, "Download complete", Toast.LENGTH_LONG).show();
                        final VideoView videoView = (VideoView) findViewById(R.id.videoView);
                        videoView.setVideoURI(Uri.fromFile(localFile));
                        videoView.start();
                    }
            );
        } catch(Exception e){
            System.out.println("could not download");
        }
    }*/

    public void chooseVideoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        int VIDEO = 1;
        startActivityForResult(galleryIntent, VIDEO);
    }

    public void chooseImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        int IMAGE = 2;
        startActivityForResult(galleryIntent, IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("result", "" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("what", "gale");

        if (requestCode == 1) {
            if (data != null) {
                videoURI = data.getData();

                videoPath = getPath(videoURI);
            }
        } else if (requestCode == 2) {
            if (data != null) {
                imageURI = data.getData();
                imagePath = getPath(imageURI);
            }
        }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

}