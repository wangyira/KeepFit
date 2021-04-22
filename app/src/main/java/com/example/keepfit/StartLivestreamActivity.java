package com.example.keepfit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.icu.text.SymbolTable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import android.util.Log;
import java.util.Arrays;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.keepfit.authapp.ProfileActivityEdits;
import com.example.keepfit.calories.CalorieActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//image
import android.provider.MediaStore;
import android.database.Cursor;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

public class StartLivestreamActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button button;
    Spinner spinner;
    DatabaseReference databaseReference;

    TextView title;
    TextView maxPeople;
    String selectedType;
    TextView time;
    TextView zoom;
    Button uploadImage;

    Uri imageURI;
    FirebaseAuth mAuth;
    String imageUrl = null;

    LivestreamMember member;
    String[] exerciseTypes = {"Aerobic","Anaerobic","Flexibility","Stability"};

    //upload video
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    Uri videoURI;
    Uri imageURI2;

    private String title2 = null;
    private String tag = null;
    private String difficulty = null;
    private String time2 = null;
    private String videoPath = null;
    private String imagePath = null;
    private Boolean allowComments = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_livestream);

        //navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set startlivestream selected
        bottomNavigationView.setSelectedItemId(R.id.nav_livestream);

        //perform itemselectedlistener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                switch (menuItem.getItemId()){
                    case R.id.nav_account:
                        startActivity(new Intent(getApplicationContext(),ProfileActivityEdits.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_calorie:
                        startActivity(new Intent(getApplicationContext(), CalorieActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_upload:
                        startActivity(new Intent(getApplicationContext(), VideoUploadActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_livestream:
                        return true;
                }
                return false;
            }
        });


//        above copied from MainActivity.java
        title = findViewById(R.id.livestream_title);
        maxPeople = findViewById(R.id.livestream_num_people);
        time = findViewById(R.id.endTime);
        zoom = findViewById(R.id.zoom);

        button = findViewById(R.id.save_button);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Livestream Details");
        uploadImage = findViewById(R.id.upload_image_btn);
        spinner = findViewById(R.id.livestream_exercise_spinner);
        spinner.setOnItemSelectedListener(this);

        member = new LivestreamMember();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, exerciseTypes);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String  zoomLink = SaveDetails(selectedType);
                if(zoomLink != "ERROR") {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse(zoomLink));
                    startActivity(viewIntent);
                }
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                System.out.println("before image button click");
                chooseImageFromGallery();
                System.out.println("after image button click");
            }
        });

        //upload video
        //https://demonuts.com/pick-video-gallery-camera-android/
        Button getVideobtn = (Button) findViewById(R.id.selectVideoButton);
        Button uploadbtn = (Button) findViewById(R.id.uploadVideoButton);
        Button getImagebtn = (Button) findViewById(R.id.selectImageButton);
        Spinner tagSpinner = (Spinner) findViewById(R.id.tagDropDown);
        Spinner diffSpinner = (Spinner) findViewById(R.id.difficultyDropDown);

        //SET VISIBILITY
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        findViewById(R.id.success_message).setVisibility(View.GONE);
//        findViewById(R.id.videoView).setVisibility(View.GONE);


        getVideobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideoFromGallery();
            }
        });
        getImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery2();
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedType = spinner.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void chooseImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        int IMAGE = 3;
        startActivityForResult(galleryIntent, IMAGE);
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
        .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                System.out.println("Sign in error!!!!");
            }
        });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode==2){
//            if(data != null){
//                imageURI = data.getData();
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    String SaveDetails(String selectedType){
        String titleText = title.getText().toString();
        String peopleText = maxPeople.getText().toString();
        String timeText = time.getText().toString();
        String zoomText = zoom.getText().toString();

        //need to store username

        //check for time inputted correctly, is after current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String[] timeArr = timeText.split(":");

        Log.d("MYINT","value: "+hour+ minute);
        System.out.println("arr: " + Arrays.toString(timeArr));

        if(hour > Integer.parseInt(timeArr[0])){
            Toast.makeText(this, "end time need to be later than the current time", Toast.LENGTH_SHORT).show();
            return "ERROR";
        }
        if(hour == Integer.parseInt(timeArr[0]) && minute > Integer.parseInt(timeArr[1])){
            Toast.makeText(this, "end time need to be later than the current time", Toast.LENGTH_SHORT).show();
            return "ERROR";
        }
        if(titleText.isEmpty()){
            Toast.makeText(this, "title cannot be empty", Toast.LENGTH_SHORT).show();
            return "ERROR";
        }
        if(peopleText.isEmpty()){
            Toast.makeText(this, "max # of people cannot be empty", Toast.LENGTH_SHORT).show();
            return "ERROR";
        }
        if(selectedType == ""){
            Toast.makeText(this, "please select an exercise type", Toast.LENGTH_SHORT).show();
            return "ERROR";
        }
        if(timeText.isEmpty()){
            Toast.makeText(this, "end time cannot be empty", Toast.LENGTH_SHORT).show();
            return "ERROR";
        }
        if(zoomText.isEmpty()) {
            Toast.makeText(this, "zoom room id cannot be empty", Toast.LENGTH_SHORT).show();
            return "ERROR";
        }
        else{
            FirebaseStorage storage = FirebaseStorage.getInstance();
            UUID randomUUID = UUID.randomUUID();
            StorageReference imageRef = storage.getReference("livestream_thumbnail_images/" + randomUUID + ".jpg");

            if(imageURI != null) {
                UploadTask imageUploadTask = imageRef.putFile(imageURI);
                imageUploadTask.addOnSuccessListener(StartLivestreamActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot imageTaskSnapshot) {
                        member.setTitle(titleText);
                        member.setMaxNumberOfPeople(Integer.parseInt(peopleText));
                        member.setExerciseType(selectedType);
                        member.setEndTime(timeText);
                        member.setZoomLink(zoomText);
                        member.setReferenceTitle(randomUUID + ".jpg");

                        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
                        String username = sharedPref.getString("username", null);
                        member.setUploadingUser(username);

                        String id = databaseReference.push().getKey();
                        databaseReference.child(id).setValue(member);
                    }
                });
            }
            else{
                Toast.makeText(this, "please upload a thumbnail image", Toast.LENGTH_SHORT).show();
                return "ERROR";
            }


        }

        return zoomText;
    }


    //upload video
    //checks if the required values have been entered
    //returns 1 if valid or 0 if something is missing
    private boolean validValues() {
        //get values
        EditText mEdit = (EditText) findViewById(R.id.titleInput);
        title2 = mEdit.getText().toString();
        mEdit = (EditText) findViewById(R.id.editTextTime2);
        time2 = mEdit.getText().toString();
        
        CheckBox cb = (CheckBox) findViewById(R.id.allowCommentsCheckBox);
        allowComments = cb.isChecked();

        if (title2 == null || title2.length() < 1 || title2.length() > 60) {
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
        } else if (time2.length() > 6 || (time2.length() > 0 && time2.length() < 4)) {
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

        public void upload() {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            Button uploadButton = (Button) findViewById(R.id.uploadVideoButton);
            TextView successMessage = (TextView) findViewById(R.id.success_message);
            UUID randomUUID = UUID.randomUUID();

            if (!validValues()) {
                return;
            }
            String noSpaceTitle = title2.replaceAll("\\s", "_");

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
            uploadTask.addOnSuccessListener(StartLivestreamActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                    StorageReference imageRef = storage.getReference("thumbnail_images/" + noSpaceTitle + "." + randomUUID + ".jpg");
                    StorageMetadata metadataI = new StorageMetadata.Builder()
                            .setCustomMetadata("title2", title2)
                            .setCustomMetadata("video reference", url)
                            .build();

                    UploadTask imageUploadTask = imageRef.putFile(imageURI2, metadataI);
                    imageUploadTask.addOnSuccessListener(StartLivestreamActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
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

                            /*Map<String, String> vidRef = new HashMap<String, String>();
                            vidRef.put("time", time);
                            vidRef.put("difficulty", difficulty);
                            vidRef.put("tag", tag);
                            vidRef.put("title", title);
                            vidRef.put("reference title", noSpaceTitle + "." + randomUUID.toString());
                            vidRef.put("numLikes", "0");*/

                            SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
                            String username = sharedPref.getString("username", null);

                            VideoReference vidRef = new VideoReference(difficulty, 0, noSpaceTitle + "." + randomUUID.toString(), tag, time2, title2, username, allowComments);

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

        public void chooseImageFromGallery2() {
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
                    Log.e("videoURI",videoURI.toString());
                    Log.e("videodata",data.toString());
                    videoPath = getPath(videoURI);
                }
            } else if (requestCode == 2) {
                if (data != null) {
                    imageURI2 = data.getData();
                    Log.e("imageURI2", ""+imageURI2);
                    imagePath = getPath(imageURI2);
                }
            }
            else if(requestCode==3){
                if(data != null){
                    imageURI = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
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
