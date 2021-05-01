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
import com.example.keepfit.VideoReference;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileActivityEdits extends AppCompatActivity implements DialogExample.DialogExampleListener {

    private Button btnEditName, btnEditPhoneNumber, btnEditBirthday, btnEditGender, btnEditWeight, btnEditHeight, btnChangePass, btnLogout, btnviewLiked, btnviewUploaded, btnviewWatched, btnviewDisliked, btnDeleteAccount, btnWipeWatchHistory;
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

    String myReturnString;

    private FirebaseUser user;
    private DatabaseReference dbreference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
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
//        bottomNavigationView.setSelectedItemId(R.id.nav_search);

        //perform itemselectedlistener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
//                    case R.id.nav_account:
//                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_calorie:
                        startActivity(new Intent(getApplicationContext(), CalorieActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_upload:
                        startActivity(new Intent(getApplicationContext(), VideoUploadActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_livestream:
                        startActivity(new Intent(getApplicationContext(), StartLivestreamActivity.class));
                        overridePendingTransition(0, 0);
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
                                if (task.isSuccessful()) {
                                    deleteAccount(username);
                                    Toast.makeText(ProfileActivityEdits.this, "Account Deleted.", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(ProfileActivityEdits.this, FirebaseMainActivity.class));
                                } else {
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
                    editor.commit();

                    getFollowers();

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


    }

    private void getFollowers(){
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String currentusername = sharedPref.getString("username", null);
        Log.d("username", currentusername);

        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("Following");

        followingRef.child(currentusername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> userfollowing = (Map<String, String>) snapshot.getValue();
                if (userfollowing != null) {
                    followingnumber.setText(String.valueOf(userfollowing.size()));
                    Log.d("following size", currentusername + String.valueOf(userfollowing.size()));
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

}