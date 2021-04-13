package com.example.keepfit.authapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.keepfit.LivestreamMember;
import com.example.keepfit.MainActivity;
import com.example.keepfit.R;
import com.example.keepfit.VideoActivity;
import com.example.keepfit.StartLivestreamActivity;
import com.example.keepfit.VideoActivity;
import com.example.keepfit.VideoUploadActivity;
import com.example.keepfit.calories.CalorieActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileActivityEdits extends AppCompatActivity implements DialogExample.DialogExampleListener {

    private Button btnEditName, btnEditPhoneNumber, btnEditBirthday, btnEditGender, btnEditWeight, btnEditHeight, btnChangePass, btnLogout, btnviewLiked, btnviewUploaded, btnviewWatched, btnviewDisliked, btnDeleteAccount;
    private ImageView imageView;

    private TextView followingnumber, followersnumber;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference ref;

    //uploaded vidoes
    ArrayList<String> videoRefTitles = new ArrayList<String>();
    ArrayList<String> videoDispTitles = new ArrayList<String>();
    //watched videos
    ArrayList<String> watchedVideoRefTitles = new ArrayList<String>();
    ArrayList<String> watchedVideoDispTitles = new ArrayList<String>();
    //liked videos
    ArrayList<String> videoRefTitles1 = new ArrayList<String>();
    ArrayList<String> videoDispTitles1 = new ArrayList<String>();
    //disliked videos
    ArrayList<String> videoRefTitles2 = new ArrayList<String>();
    ArrayList<String> videoDispTitles2 = new ArrayList<String>();
    ArrayList<ImageButton> imageButtons = new ArrayList<ImageButton>();
    ArrayList<TextView> textViews = new ArrayList<TextView>();
    ArrayList<Button> deleteVideo = new ArrayList<Button>();

    int numUploadedVideos;
    int numWatchedVideos;
    int numLikedVideos;
    int numDislikedVideos;

    String which = "";
    String videoId;
    String refTitle;

    private FirebaseUser user;
    private DatabaseReference dbreference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
            userId = user.getUid();
        else userId = "";
        setContentView(R.layout.activity_edit_profile);
        btnEditName = findViewById(R.id.btnEditName);
        btnEditPhoneNumber = findViewById(R.id.btnEditPhoneNumber);
        btnEditBirthday = findViewById(R.id.btnEditBirthday);
        btnEditGender = findViewById(R.id.btnEditGender);
        btnEditWeight = findViewById(R.id.btnEditWeight);
        btnEditHeight = findViewById(R.id.btnEditHeight);
        btnChangePass = findViewById(R.id.btnchangepass);
        btnLogout = findViewById(R.id.btnlogout);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        btnviewLiked = findViewById(R.id.viewLikedVideos);
        btnviewDisliked = findViewById(R.id.viewDisliked);
        btnviewUploaded = findViewById(R.id.viewUploaded);
        btnviewWatched = findViewById(R.id.viewWatchHistory);

        followingnumber = findViewById(R.id.followingnumber);
        followersnumber = findViewById(R.id.followersnumber);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        numUploadedVideos = 0;
        numWatchedVideos = 0;
        numLikedVideos = 0;
        numDislikedVideos = 0;

        //mAuth = FirebaseAuth.getInstance();

        //navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set account selected
        bottomNavigationView.setSelectedItemId(R.id.nav_account);

        //perform itemselectedlistener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                switch (menuItem.getItemId()){
                    case R.id.nav_account:
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
                        startActivity(new Intent(getApplicationContext(), VideoUploadActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_livestream:
                        startActivity(new Intent(getApplicationContext(), StartLivestreamActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

//        Bundle b = getIntent().getExtras();
//        if(b!=null){
//            String deleting = b.getString("deleting");
//
//            if(deleting.equals("true")){
//                getVideoResultsbyTitle();
//                getIntent().putExtra("deleting", "false");
//            }
//        }


        btnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = "name";
                OpenDialog();

            }
        });

        btnEditPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = "phonenumber";
                OpenDialog();
            }
        });

        btnEditBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = "birthday";
                OpenDialog();
            }
        });

        btnEditGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = "gender";
                OpenDialog();
            }
        });

        btnEditWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = "weight";
                OpenDialog();
            }
        });

        btnEditHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = "height";
                OpenDialog();
            }
        });

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivityEdits.this, ForgotPassword.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivityEdits.this, FirebaseMainActivity.class));
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
                String username = sharedPref.getString("username", null);

                AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileActivityEdits.this);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Deleting this account will result in completely removing your account from the system and your uploaded videos will also be deleted.");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    deleteAccount(username);
                                    Toast.makeText(ProfileActivityEdits.this, "Account Deleted.", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(ProfileActivityEdits.this, FirebaseMainActivity.class));
                                }
                                else{
                                    Toast.makeText(ProfileActivityEdits.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
                dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

        addItemstoArray();
        makeInvisible();


        btnviewLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(findViewById(R.id.item1txt).isShown()){
                    for(int i=0; i < 10; i++){
                        textViews.get(i).setVisibility(View.GONE);
                        imageButtons.get(i).setVisibility(View.GONE);
                    }

                }
                else{
                    search(1);
                }
            }
        });

        btnviewDisliked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(findViewById(R.id.ditem1txt).isShown()){
                    for(int i=20; i < 30; i++){
                        textViews.get(i).setVisibility(View.GONE);
                        imageButtons.get(i).setVisibility(View.GONE);
                    }
                }
                else{
                    search(3);
                }
            }
        });

        btnviewUploaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(findViewById(R.id.uitem1txt).isShown()){
                    for(int i=10; i < 20; i++){
                        textViews.get(i).setVisibility(View.GONE);
                        imageButtons.get(i).setVisibility(View.GONE);
                    }
                    for(int i=0; i < 10; i++){
                        deleteVideo.get(i).setVisibility(View.GONE);
                    }

                }
                else{
                    search(2);
                }
            }
        });

        btnviewWatched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(findViewById(R.id.hitem1txt).isShown()){
                    for(int i=30; i < 40; i++){
                        textViews.get(i).setVisibility(View.GONE);
                        imageButtons.get(i).setVisibility(View.GONE);
                    }
                }
                else{
                    search(4);
                }
            }
        });

        dbreference = FirebaseDatabase.getInstance().getReference("UserInformation");

        final TextView greetingTextView = (TextView) findViewById(R.id.editprofile);
        final TextView nameTextView = (TextView) findViewById(R.id.TextViewName);
        final TextView phoneTextView = (TextView) findViewById(R.id.TextViewPhoneNumber);
        final TextView bdayTextView = (TextView) findViewById(R.id.TextViewBirthday);
        final TextView genderTextView = (TextView) findViewById(R.id.TextViewGender);
        final TextView weightTextView = (TextView) findViewById(R.id.TextViewWeight);
        final TextView heightTextView = (TextView) findViewById(R.id.TextViewHeight);
        final ImageView profilePicture = findViewById(R.id.imgView);
        final TextView usernameTextView = (TextView) findViewById(R.id.TextViewUsername);
        final TextView emailTextView = (TextView) findViewById(R.id.TextViewEmail);

        dbreference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserInformation info = snapshot.getValue(UserInformation.class);
                if (info != null) {
                    String name = info.name;
                    String phone = info.phonenumber;
                    String bday = info.birthday;
                    String gender = info.gender;
                    String weight = info.weight;
                    String height = info.height;
                    String imgLink = info.pickey;
                    String username = info.username;
                    String email = info.email;
                    String referenceTitle = info.referenceTitle;

                    SharedPreferences sharedPreferences = getSharedPreferences("main", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("referenceTitle", referenceTitle);
                    editor.putString("weight", weight);
                    editor.putString("username", username);
                    editor.putString("email", email);
                    editor.apply();
                    //editor.commit();

                    Picasso.get().load(imgLink).into(profilePicture);

                    if (username != null) {
                        usernameTextView.setText("Username: " + username);
                    } else {
                        usernameTextView.setError("Please enter your username.");
                        usernameTextView.requestFocus();
                        return;
                    }

                    if (email != null) {
                        emailTextView.setText("Email: " + email);
                    } else {
                        emailTextView.setError("Please enter your email.");
                        emailTextView.requestFocus();
                        return;
                    }

                    if (name != null) {
                        greetingTextView.setText("Welcome, " + name + "!");
                        nameTextView.setText("Name: " + name);
                    } else {
                        nameTextView.setError("Please enter your name.");
                        nameTextView.requestFocus();
                        return;
                    }

                    if (phone != null) phoneTextView.setText("Phone Number: " + phone);
                    else {
                        phoneTextView.setError("Please enter your phone number.");
                        phoneTextView.requestFocus();
                        return;
                    }
                    if (bday != null)
                        bdayTextView.setText("Birthday: " + bday.substring(0, 2) + "/" + bday.substring(2, 4) + "/" + bday.substring(4));
                    else {
                        bdayTextView.setError("Please enter your birthday.");
                        bdayTextView.requestFocus();
                        return;
                    }
                    if (gender != null) genderTextView.setText("Gender: " + gender);
                    else {
                        genderTextView.setError("Please enter your gender.");
                        genderTextView.requestFocus();
                        return;
                    }
                    if (weight != null) weightTextView.setText("Weight: " + weight + "lbs");
                    else {
                        weightTextView.setError("Please enter your weight.");
                        weightTextView.requestFocus();
                        return;
                    }
                    if (height != null) heightTextView.setText("Height: " + height + "in");
                    else {
                        heightTextView.setError("Please enter your height.");
                        heightTextView.requestFocus();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String currentusername = sharedPref.getString("username", null);

        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("Following");

        followingRef.child(currentusername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> userfollowing = (Map<String, String>) snapshot.getValue();
                if (userfollowing != null) {
                    followingnumber.setText(String.valueOf(userfollowing.size()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("Followers");

        followersRef.child(currentusername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> userfollowers = (Map<String, String>) snapshot.getValue();
                if (userfollowers != null) {
                    followersnumber.setText(String.valueOf(userfollowers.size()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void deleteAccount(String username){
        //remove from UserInformation and Users table
        FirebaseDatabase.getInstance().getReference("UserInformation").child(userId).removeValue();
        FirebaseDatabase.getInstance().getReference("Users").child(userId).removeValue();

        //remove from likes/dislikes
        FirebaseDatabase.getInstance().getReference("Likes").child(username).removeValue();
        FirebaseDatabase.getInstance().getReference("Dislikes").child(username).removeValue();

        //remove from followers/following
        FirebaseDatabase.getInstance().getReference("Followers").child(username).removeValue();
        FirebaseDatabase.getInstance().getReference("Following").child(username).removeValue();

        //remove from follower/following lists of other users
        ref = FirebaseDatabase.getInstance().getReference("Followers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                    String user = entry.getKey();
                    DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("Followers").child(user);
                    newRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds: snapshot.getChildren()){
                                if(ds.getValue().equals(username)){
                                    ds.getRef().removeValue();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //remove from following table
        ref = FirebaseDatabase.getInstance().getReference("Following").child(username);
        ref.getRef().removeValue();

        // delete the user's livestreams
        ref = FirebaseDatabase.getInstance().getReference("Livestream Details");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                    Map<String,String> temp = entry.getValue();
                    String uploader = temp.get("uploadingUser");
                    if(uploader.equals(username)){
                        String key = entry.getKey();
                        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("Livestream Details").child(key);
                        newRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                newRef.getRef().removeValue();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // delete the video from video references and all instances in likes/dislikes
        ref = FirebaseDatabase.getInstance().getReference("Video References");
        ref.orderByChild("uploadingUser").equalTo(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot !=null){
                    Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                    if(results != null){
                        for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                            Map<String,String> temp = entry.getValue();
                            refTitle = temp.get("referenceTitle");
                            //loop thru likes table to remove likes
                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Likes");
                            ref2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                                    for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                                        String user = entry.getKey();
                                        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("Likes").child(user);
                                        newRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot ds: snapshot.getChildren()){
                                                    if(ds.getValue().equals(refTitle)){
                                                        ds.getRef().removeValue();
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            //loop thru dislikes table to remove dislikes
                            ref2 = FirebaseDatabase.getInstance().getReference("Dislikes");
                            ref2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                                    for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                                        String user = entry.getKey();
                                        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("Dislikes").child(user);
                                        newRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot ds: snapshot.getChildren()){
                                                    if(ds.getValue().equals(refTitle)){
                                                        ds.getRef().removeValue();
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            ref = FirebaseDatabase.getInstance().getReference("Video References");
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                                    for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                                        Map<String,String> temp = entry.getValue();
                                        String uploader = temp.get("uploadingUser");
                                        if(uploader.equals(username)){
                                            String key = entry.getKey();
                                            DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("Video References").child(key);
                                            newRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    newRef.getRef().removeValue();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }

                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void openNewActivity(){
        Intent intent = new Intent(this, ScrollingActivity.class);
        startActivity(intent);
    }

    public void OpenDialog() {
        DialogExample exampleDialog = new DialogExample();
        exampleDialog.show(getSupportFragmentManager(), "hi");

    }

    @Override
    public void applyTexts(String edits) {

        FirebaseDatabase.getInstance().getReference("UserInformation").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(which).setValue(edits);
    }

    private void search(int i){
        //liked videos
        if(i == 1){
            getLikedVideos();
        }
        //uploaded videos
        else if(i == 2){
            getVideoResultsbyTitle();
        }
        //disliked videos
        else if (i == 3){
            getDislikedVideos();
        }
        else if (i == 4){
            getWatchedVideoResultsbyTitle();
        }
    }

    private void getDislikedVideos() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("Dislikes");

        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        numDislikedVideos = 0;
        videoRefTitles2.clear();
        videoDispTitles2.clear();

        ref.child(username).limitToFirst(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Map<String, String> results = (Map<String, String>) snapshot.getValue();
                        if(results!=null){
                            for(Map.Entry<String, String> entry : results.entrySet()){
                                videoRefTitles2.add(entry.getValue());
                                ref = database.getReference("Video References");
                                ref.orderByChild("referenceTitle").equalTo(entry.getValue()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds : snapshot.getChildren()) {
                                            videoId = ds.getKey();
                                            //Log.d("TAG", uid);
                                        }
                                        ref.child(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Map <String, String> results = (Map <String, String>) snapshot.getValue();
                                                videoDispTitles2.add(results.get("title"));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    };

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                numDislikedVideos++;
                            }
                        }
                        displayDislikedVideos();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    //uploaded videos
    private void getVideoResultsbyTitle(){
        videoRefTitles.clear();
        videoDispTitles.clear();
        numUploadedVideos = 0;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("Video References");
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        //String uidusername = FirebaseDatabase.getInstance().getReference("UserInformation").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").toString();
        ref.orderByChild("uploadingUser").equalTo(username).limitToFirst(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                        if(results!=null){
                            for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                                videoRefTitles.add(entry.getValue().get("referenceTitle"));
                                videoDispTitles.add(entry.getValue().get("title"));
                                numUploadedVideos++;
                            }
                        }
                        displayResultsUploaded();
                        //numVideos = results.entrySet().size();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    //uploaded videos
    private void getWatchedVideoResultsbyTitle(){
        watchedVideoRefTitles.clear();
        watchedVideoDispTitles.clear();
        numWatchedVideos = 0;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("Video History");

        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);

        ref.child(username).limitToFirst(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, String> results = (Map<String, String>) snapshot.getValue();
                        if(results!=null){
                            for(Map.Entry<String, String> entry : results.entrySet()){
                                Log.e("key",entry.getKey());
                                Log.e("value",entry.getValue());
                                watchedVideoRefTitles.add(entry.getValue());
                                ref = database.getReference("Video References");
                                ref.orderByChild("referenceTitle").equalTo(entry.getValue()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds : snapshot.getChildren()) {
                                            videoId = ds.getKey();
                                        }
                                        ref.child(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Map <String, String> results = (Map <String, String>) snapshot.getValue();
                                                watchedVideoDispTitles.add(results.get("title"));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    };

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                numWatchedVideos++;
                            }
                        }
                        Log.e("numwatchedvideo",String.valueOf(numWatchedVideos));
                        displayWatchedVideos();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    //liked videos
    private void getLikedVideos(){
        videoRefTitles1.clear();
        videoDispTitles1.clear();
        numLikedVideos = 0;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("Likes");

        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);

        //String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ref.child(username).limitToFirst(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, String> results = (Map<String, String>) snapshot.getValue();
                        if(results!=null){
                            for(Map.Entry<String, String> entry : results.entrySet()){
                                videoRefTitles1.add(entry.getValue());
                                ref = database.getReference("Video References");
                                ref.orderByChild("referenceTitle").equalTo(entry.getValue()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds : snapshot.getChildren()) {
                                            videoId = ds.getKey();
                                            //Log.d("TAG", uid);
                                        }
                                        ref.child(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Map <String, String> results = (Map <String, String>) snapshot.getValue();
                                                videoDispTitles1.add(results.get("title"));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    };

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                //videoDispTitles1.add(entry.getValue());
                                numLikedVideos++;
                            }
                        }
                        displayLikedVideos();
                        //numVideos = results.entrySet().size();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    //display uploaded
    private void displayResultsUploaded(){
        for(int i=0; i < numUploadedVideos; i++){
            final int j = i;
            imageButtons.get(i+10).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("myTag", "@@@@@@@");
                    openNewActivityVideo(j);
                }
            });
        }
        //display video results
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        for(int i=0; i < numUploadedVideos; i++) {
            StorageReference imageRef = storageRef.child("/thumbnail_images/" + videoRefTitles.get(i) + ".jpg");
            try {
                final int j = i;
                final File localFile = File.createTempFile(videoRefTitles.get(i), "jpg");
                imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        ImageButton ib = imageButtons.get(j+10);

                        Bitmap bm = imgToBitmap(localFile);
                        ib.setImageBitmap(bm);
                        ib.setVisibility(View.VISIBLE);

                        TextView tv = textViews.get(j+10);
                        tv.setText(videoDispTitles.get(j));
                        tv.setVisibility(View.VISIBLE);

                        Button delete = deleteVideo.get(j);

                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Log.d("ONCLICK", "Clicked Delete");
                                deleteVideo(videoRefTitles.get(j));
                                for(int k =0; k < numUploadedVideos; k++){
                                    Log.d("removing", String.valueOf(k));
                                    imageButtons.get(k+10).setVisibility(View.GONE);
                                    textViews.get(k+10).setVisibility(View.GONE);
                                    deleteVideo.get(k).setVisibility(View.GONE);
                                }

                                //videoRefTitles.clear();
                                //videoDispTitles.clear();
                                Log.d("made it here", String.valueOf(videoRefTitles.size()));
                                //getVideoResultsbyTitle();

                                finish();
                                getIntent().putExtra("deleting", "true");
                                startActivity(getIntent());

//                                videoRefTitles.remove(j);
//                                videoDispTitles.remove(j);

                            }
                        });
                        delete.setVisibility(View.VISIBLE);

                    }
                });
            } catch (Exception e) {
            }
        }
    }

    private void deleteVideo(String refTitle){
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes");
        DatabaseReference dislikeRef = FirebaseDatabase.getInstance().getReference("Dislikes");
        DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference("Video References");

        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) snapshot.getValue();
                for(Map.Entry<String, Map<String, String>> entry : map.entrySet()){
                    String key = entry.getKey();
                    DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("Likes").child(key);
                    newRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds: snapshot.getChildren()){
                                if(ds.getValue().equals(refTitle)){
                                    ds.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dislikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) snapshot.getValue();
                for(Map.Entry<String, Map<String, String>> entry : map.entrySet()){
                    String key = entry.getKey();
                    DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("Dislikes").child(key);
                    newRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds: snapshot.getChildren()){
                                if(ds.getValue().equals(refTitle)){
                                    ds.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        deleteRef.orderByChild("referenceTitle").equalTo(refTitle).limitToFirst(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            ds.getRef().removeValue();
                            numUploadedVideos--;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    //get watched videos
    private void displayWatchedVideos(){
        for(int i=0; i < numWatchedVideos; i++){
            final int j = i;
            imageButtons.get(i+30).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("myTag", "@@@@@@@");
                    openNewActivityVideoWatched(j);
                }
            });
        }
        //display video results
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        for(int i=0; i < numWatchedVideos; i++) {
            StorageReference imageRef = storageRef.child("/thumbnail_images/" + watchedVideoRefTitles.get(i) + ".jpg");
            try {
                final int j = i;
                final File localFile = File.createTempFile(watchedVideoRefTitles.get(i), "jpg");
                imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        ImageButton ib = imageButtons.get(j+30);

                        Bitmap bm = imgToBitmap(localFile);
                        ib.setImageBitmap(bm);
                        ib.setVisibility(View.VISIBLE);

                        TextView tv = textViews.get(j+30);
                        tv.setText(watchedVideoDispTitles.get(j));
                        tv.setVisibility(View.VISIBLE);

                    }
                });
            } catch (Exception e) {
            }
        }
    }

    //get liked videos
    private void displayLikedVideos(){
        for(int i=0; i < numLikedVideos; i++){
            final int j = i;
            imageButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("myTag", "@@@@@@@");
                    openNewActivityVideoLiked(j);
                }
            });
        }
        //display video results
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        for(int i=0; i < numLikedVideos; i++) {
            StorageReference imageRef = storageRef.child("/thumbnail_images/" + videoRefTitles1.get(i) + ".jpg");
            try {
                final int j = i;
                final File localFile = File.createTempFile(videoRefTitles1.get(i), "jpg");
                imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        ImageButton ib = imageButtons.get(j);

                        Bitmap bm = imgToBitmap(localFile);
                        ib.setImageBitmap(bm);
                        ib.setVisibility(View.VISIBLE);

                        TextView tv = textViews.get(j);
                        tv.setText(videoDispTitles1.get(j));
                        tv.setVisibility(View.VISIBLE);

                    }
                });
            } catch (Exception e) {
            }
        }
    }

    //get disliked videos
    private void displayDislikedVideos(){
        for(int i=0; i < numDislikedVideos; i++){
            final int j = i;
            imageButtons.get(i+20).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("myTag", "@@@@@@@");
                    openNewActivityVideoDisliked(j);
                }
            });
        }
        //display video results
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        for(int i=0; i < numDislikedVideos; i++) {
            StorageReference imageRef = storageRef.child("/thumbnail_images/" + videoRefTitles2.get(i) + ".jpg");
            try {
                final int j = i;
                final File localFile = File.createTempFile(videoRefTitles2.get(i), "jpg");
                imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        ImageButton ib = imageButtons.get(j+20);

                        Bitmap bm = imgToBitmap(localFile);
                        ib.setImageBitmap(bm);
                        ib.setVisibility(View.VISIBLE);

                        TextView tv = textViews.get(j+20);
                        tv.setText(videoDispTitles2.get(j));
                        tv.setVisibility(View.VISIBLE);

                    }
                });
            } catch (Exception e) {
            }
        }
    }



    private Bitmap imgToBitmap(File file){
        byte[] bytes = null;
        try{
            FileInputStream fis = new FileInputStream(file);
            //create FileInputStream which obtains input bytes from a file in a file system
            //FileInputStream is meant for reading streams of raw bytes such as image data. For reading streams of characters, consider using FileReader.

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            try {
                for (int readNum; (readNum = fis.read(buf)) != -1;) {
                    //Writes to this byte array output stream
                    bos.write(buf, 0, readNum);
                    System.out.println("read " + readNum + " bytes,");
                }
            } catch (IOException ex) {
                Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
            }

            bytes = bos.toByteArray();
        } catch(FileNotFoundException fnfe){
            AlertDialog.Builder videoDialog = new AlertDialog.Builder(this);
            videoDialog.setTitle("ERROR: fnfe.");

            String[] pictureDialogItems = {
                    "OK"};
            videoDialog.setItems(pictureDialogItems,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

            videoDialog.show();
        }

        ByteArrayInputStream imageStream = null;

        try {
            imageStream = new ByteArrayInputStream(bytes);
            return BitmapFactory.decodeStream(imageStream);
        }
        catch (Exception ex) {
            Log.d("My Activity", "Unable to generate a bitmap: " + ex.getMessage());
            return null;
        }
        finally {
            if (imageStream != null) {
                try { imageStream.close(); }
                catch (Exception ex) {}
            }
        }
    }

    private void addItemstoArray(){
        imageButtons.add(findViewById(R.id.item1btn));
        imageButtons.add(findViewById(R.id.item2btn));
        imageButtons.add(findViewById(R.id.item3btn));
        imageButtons.add(findViewById(R.id.item4btn));
        imageButtons.add(findViewById(R.id.item5btn));
        imageButtons.add(findViewById(R.id.item6btn));
        imageButtons.add(findViewById(R.id.item7btn));
        imageButtons.add(findViewById(R.id.item8btn));
        imageButtons.add(findViewById(R.id.item9btn));
        imageButtons.add(findViewById(R.id.item10btn));

        imageButtons.add(findViewById(R.id.uitem1btn));
        imageButtons.add(findViewById(R.id.uitem2btn));
        imageButtons.add(findViewById(R.id.uitem3btn));
        imageButtons.add(findViewById(R.id.uitem4btn));
        imageButtons.add(findViewById(R.id.uitem5btn));
        imageButtons.add(findViewById(R.id.uitem6btn));
        imageButtons.add(findViewById(R.id.uitem7btn));
        imageButtons.add(findViewById(R.id.uitem8btn));
        imageButtons.add(findViewById(R.id.uitem9btn));
        imageButtons.add(findViewById(R.id.uitem10btn));

        imageButtons.add(findViewById(R.id.ditem1btn));
        imageButtons.add(findViewById(R.id.ditem2btn));
        imageButtons.add(findViewById(R.id.ditem3btn));
        imageButtons.add(findViewById(R.id.ditem4btn));
        imageButtons.add(findViewById(R.id.ditem5btn));
        imageButtons.add(findViewById(R.id.ditem6btn));
        imageButtons.add(findViewById(R.id.ditem7btn));
        imageButtons.add(findViewById(R.id.ditem8btn));
        imageButtons.add(findViewById(R.id.ditem9btn));
        imageButtons.add(findViewById(R.id.ditem10btn));

        imageButtons.add(findViewById(R.id.hitem1btn));
        imageButtons.add(findViewById(R.id.hitem2btn));
        imageButtons.add(findViewById(R.id.hitem3btn));
        imageButtons.add(findViewById(R.id.hitem4btn));
        imageButtons.add(findViewById(R.id.hitem5btn));
        imageButtons.add(findViewById(R.id.hitem6btn));
        imageButtons.add(findViewById(R.id.hitem7btn));
        imageButtons.add(findViewById(R.id.hitem8btn));
        imageButtons.add(findViewById(R.id.hitem9btn));
        imageButtons.add(findViewById(R.id.hitem10btn));


        textViews.add(findViewById(R.id.item1txt));
        textViews.add(findViewById(R.id.item2txt));
        textViews.add(findViewById(R.id.item3txt));
        textViews.add(findViewById(R.id.item4txt));
        textViews.add(findViewById(R.id.item5txt));
        textViews.add(findViewById(R.id.item6txt));
        textViews.add(findViewById(R.id.item7txt));
        textViews.add(findViewById(R.id.item8txt));
        textViews.add(findViewById(R.id.item9txt));
        textViews.add(findViewById(R.id.item10txt));

        textViews.add(findViewById(R.id.uitem1txt));
        textViews.add(findViewById(R.id.uitem2txt));
        textViews.add(findViewById(R.id.uitem3txt));
        textViews.add(findViewById(R.id.uitem4txt));
        textViews.add(findViewById(R.id.uitem5txt));
        textViews.add(findViewById(R.id.uitem6txt));
        textViews.add(findViewById(R.id.uitem7txt));
        textViews.add(findViewById(R.id.uitem8txt));
        textViews.add(findViewById(R.id.uitem9txt));
        textViews.add(findViewById(R.id.uitem10txt));

        textViews.add(findViewById(R.id.ditem1txt));
        textViews.add(findViewById(R.id.ditem2txt));
        textViews.add(findViewById(R.id.ditem3txt));
        textViews.add(findViewById(R.id.ditem4txt));
        textViews.add(findViewById(R.id.ditem5txt));
        textViews.add(findViewById(R.id.ditem6txt));
        textViews.add(findViewById(R.id.ditem7txt));
        textViews.add(findViewById(R.id.ditem8txt));
        textViews.add(findViewById(R.id.ditem9txt));
        textViews.add(findViewById(R.id.ditem10txt));

        textViews.add(findViewById(R.id.hitem1txt));
        textViews.add(findViewById(R.id.hitem2txt));
        textViews.add(findViewById(R.id.hitem3txt));
        textViews.add(findViewById(R.id.hitem4txt));
        textViews.add(findViewById(R.id.hitem5txt));
        textViews.add(findViewById(R.id.hitem6txt));
        textViews.add(findViewById(R.id.hitem7txt));
        textViews.add(findViewById(R.id.hitem8txt));
        textViews.add(findViewById(R.id.hitem9txt));
        textViews.add(findViewById(R.id.hitem10txt));

        deleteVideo.add(findViewById(R.id.uitem1delete));
        deleteVideo.add(findViewById(R.id.uitem2delete));
        deleteVideo.add(findViewById(R.id.uitem3delete));
        deleteVideo.add(findViewById(R.id.uitem4delete));
        deleteVideo.add(findViewById(R.id.uitem5delete));
        deleteVideo.add(findViewById(R.id.uitem6delete));
        deleteVideo.add(findViewById(R.id.uitem7delete));
        deleteVideo.add(findViewById(R.id.uitem8delete));
        deleteVideo.add(findViewById(R.id.uitem9delete));
        deleteVideo.add(findViewById(R.id.uitem10delete));


    }

    private void makeInvisible(){
        for(ImageButton ib : imageButtons){
            ib.setVisibility(View.GONE);
        }
        for(TextView tv : textViews){
            tv.setVisibility(View.GONE);
        }
        for(Button b : deleteVideo){
            b.setVisibility(View.GONE);
        }
    }

    public void openNewActivityVideo(int i){
        String referenceTitle = videoRefTitles.get(i);
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("referenceTitle",referenceTitle);
        startActivity(intent);
    }

    public void openNewActivityVideoWatched(int i){
        String referenceTitle = watchedVideoRefTitles.get(i);
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("referenceTitle",referenceTitle);
        startActivity(intent);
    }

    public void openNewActivityVideoLiked(int i){
        String referenceTitle = videoRefTitles1.get(i);
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("referenceTitle",referenceTitle);
        startActivity(intent);
    }
    public void openNewActivityVideoDisliked(int i){
        String referenceTitle = videoRefTitles2.get(i);
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("referenceTitle",referenceTitle);
        startActivity(intent);
    }

}