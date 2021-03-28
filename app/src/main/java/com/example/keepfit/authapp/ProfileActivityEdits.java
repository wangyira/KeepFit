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

    private Button btnEditName, btnEditPhoneNumber, btnEditBirthday, btnEditGender, btnEditWeight, btnEditHeight, btnChangePass, btnLogout, btnviewLiked, btnviewUploaded, btnviewDisliked;
    private ImageView imageView;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference ref;

    //uploaded vidoes
    ArrayList<String> videoRefTitles = new ArrayList<String>();
    ArrayList<String> videoDispTitles = new ArrayList<String>();
    //liked videos
    ArrayList<String> videoRefTitles1 = new ArrayList<String>();
    ArrayList<String> videoDispTitles1 = new ArrayList<String>();
    //disliked videos
    ArrayList<String> videoRefTitles2 = new ArrayList<String>();
    ArrayList<String> videoDispTitles2 = new ArrayList<String>();
    ArrayList<ImageButton> imageButtons = new ArrayList<ImageButton>();
    ArrayList<TextView> textViews = new ArrayList<TextView>();

    int numUploadedVideos;
    int numLikedVideos;
    int numDislikedVideos;

    String which = "";
    String videoId;

    private FirebaseUser user;
    private DatabaseReference dbreference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        btnEditName = findViewById(R.id.btnEditName);
        btnEditPhoneNumber = findViewById(R.id.btnEditPhoneNumber);
        btnEditBirthday = findViewById(R.id.btnEditBirthday);
        btnEditGender = findViewById(R.id.btnEditGender);
        btnEditWeight = findViewById(R.id.btnEditWeight);
        btnEditHeight = findViewById(R.id.btnEditHeight);
        btnChangePass = findViewById(R.id.btnchangepass);
        btnLogout = findViewById(R.id.btnlogout);

        btnviewLiked = findViewById(R.id.viewLikedVideos);
        btnviewDisliked = findViewById(R.id.viewDisliked);
        btnviewUploaded = findViewById(R.id.viewUploaded);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        numUploadedVideos = 0;
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

//        btnChoose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseImage();
//            }
//        });
//
//        btnUpload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadImage();
//            }
//        });

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

        addItemstoArray();
        makeInvisible();


        btnviewLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(1);
            }
        });

        btnviewDisliked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(3);
            }
        });

        btnviewUploaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(2);
            }
        });


        user = FirebaseAuth.getInstance().getCurrentUser();
        dbreference = FirebaseDatabase.getInstance().getReference("UserInformation");
        userId = user.getUid();

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
    }

    private void getDislikedVideos() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("Dislikes");

        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);

        ref.child(username).limitToFirst(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, String> results = (Map<String, String>) snapshot.getValue();
                        if(results!=null){
                            for(Map.Entry<String, String> entry : results.entrySet()){
                                videoRefTitles2.add(entry.getValue());
                                ref = database.getReference("Video References");
                                ref.orderByChild("reference title").equalTo(entry.getValue()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                videoRefTitles.add(entry.getValue().get("reference title"));
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

    //liked videos
    private void getLikedVideos(){
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
                                ref.orderByChild("reference title").equalTo(entry.getValue()).addListenerForSingleValueEvent(new ValueEventListener() {
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


    }

    private void makeInvisible(){
        for(ImageButton ib : imageButtons){
            ib.setVisibility(View.GONE);
        }
        for(TextView tv : textViews){
            tv.setVisibility(View.GONE);
        }
    }

    public void openNewActivityVideo(int i){
        String referenceTitle = videoRefTitles.get(i);
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