package com.example.keepfit.authapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keepfit.MainActivity;
import com.example.keepfit.R;
import com.example.keepfit.StartLivestreamActivity;
import com.example.keepfit.VideoActivity;
import com.example.keepfit.VideoUploadActivity;
import com.example.keepfit.calories.CalorieActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PublicProfile extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    //private FirebaseUser user;

    private ImageView pfp;
    private TextView username, name, followingnumber, followersnumber;
    private ToggleButton followbutton;

    private String userProfileToDisplay;
    private String nameToDisplay;
    private String uid;
    private String pfpLink;

    ArrayList<String> videoRefTitles = new ArrayList<String>();
    ArrayList<String> videoDispTitles = new ArrayList<String>();
    ArrayList<ImageButton> imageButtons = new ArrayList<ImageButton>();
    ArrayList<TextView> textViews = new ArrayList<TextView>();
    int numVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);

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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        pfp = findViewById(R.id.profilePic);
        name = findViewById(R.id.publicProfileName);
        username = findViewById(R.id.publicProfileUsername);
        followbutton = findViewById(R.id.followbutton);
        followingnumber = findViewById(R.id.followingnumber);
        followersnumber = findViewById(R.id.followersnumber);


        userProfileToDisplay = getIntent().getStringExtra("username");
        numVideos = 0;
        addItemstoArray();
        makeInvisible();
        search();

        //username of the person whose profile we are displaying -- send a string called "username" when creating intent

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserInformation");
        reference.orderByChild("username").equalTo(userProfileToDisplay).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    uid = ds.getKey();
                    Log.d("TAG", uid);
                }
                reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserInformation userInfo = snapshot.getValue(UserInformation.class);
                        nameToDisplay = userInfo.name;
                        pfpLink = userInfo.pickey;
                        Picasso.get().load(pfpLink).into(pfp);
                        name.setText(nameToDisplay);
                        username.setText(userProfileToDisplay);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
                if (userfollowing == null) {
                    followbutton.setChecked(false);
                }
                else {
                    for (Map.Entry<String, String> entry : userfollowing.entrySet()) {
                        if (entry.getValue().equals(userProfileToDisplay)) {
                            followbutton.setChecked(true);

                            break;
                        } else {
                            followbutton.setChecked(false);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        followingRef.child(userProfileToDisplay).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> userfollowing = (Map<String, String>) snapshot.getValue();
                if (userfollowing == null) {
                    followingnumber.setText("0");
                }
                else {
                    followingnumber.setText(String.valueOf(userfollowing.size()));
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("Followers");

        followersRef.child(userProfileToDisplay).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> userfollowers = (Map<String, String>) snapshot.getValue();
                if (userfollowers == null) {
                    followersnumber.setText("0");
                }
                else {
                    followersnumber.setText(String.valueOf(userfollowers.size()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        followbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (followbutton.isChecked()) {
                    Toast.makeText(getApplicationContext(), "User Followed", Toast.LENGTH_SHORT).show();
                    DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("Following").child(currentusername);
                    followingRef.push().setValue(userProfileToDisplay);
                    DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("Followers").child(userProfileToDisplay);
                    followersRef.push().setValue(currentusername);
                }
                else {
                    Toast.makeText(getApplicationContext(), "User Unfollowed", Toast.LENGTH_SHORT).show();
                    DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("Following").child(currentusername);
                    DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("Followers").child(userProfileToDisplay);
                    followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                if (ds.getValue().equals(userProfileToDisplay))
                                    ds.getRef().removeValue();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                if (ds.getValue().equals(currentusername))
                                    ds.getRef().removeValue();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    private void search(){
        getVideoResultsbyTitle();
    }

    private void getVideoResultsbyTitle(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Video References");
        ref.orderByChild("uploadingUser").equalTo(userProfileToDisplay).limitToFirst(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                        if(results!=null){
                            for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                                if(numVideos < 10){
                                    videoRefTitles.add(entry.getValue().get("reference title"));
                                    videoDispTitles.add(entry.getValue().get("title"));
                                    numVideos++;
                                }
                                else break;
                            }
                        }
                        displayResults();
                        //numVideos = results.entrySet().size();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void displayResults(){
        for(int i=0; i < numVideos; i++){
            final int j = i;
            imageButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("myTag", "@@@@@@@");
                    openNewActivityVideo(j);
                }
            });
        }
        //display video results
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        for(int i=0; i < numVideos; i++) {
            StorageReference imageRef = storageRef.child("/thumbnail_images/" + videoRefTitles.get(i) + ".jpg");
            try {
                final int j = i;
                final File localFile = File.createTempFile(videoRefTitles.get(i), "jpg");
                imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        ImageButton ib = imageButtons.get(j);

                        Bitmap bm = imgToBitmap(localFile);
                        ib.setImageBitmap(bm);
                        ib.setVisibility(View.VISIBLE);

                        TextView tv = textViews.get(j);
                        tv.setText(videoDispTitles.get(j));
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
        imageButtons.add(findViewById(R.id.it1btn));
        imageButtons.add(findViewById(R.id.it2btn));
        imageButtons.add(findViewById(R.id.it3btn));
        imageButtons.add(findViewById(R.id.it4btn));
        imageButtons.add(findViewById(R.id.it5btn));
        imageButtons.add(findViewById(R.id.it6btn));
        imageButtons.add(findViewById(R.id.it7btn));
        imageButtons.add(findViewById(R.id.it8btn));
        imageButtons.add(findViewById(R.id.it9btn));
        imageButtons.add(findViewById(R.id.it10btn));
//        imageButtons.add(findViewById(R.id.item11button));
//        imageButtons.add(findViewById(R.id.item12button));
//        imageButtons.add(findViewById(R.id.item13button));
//        imageButtons.add(findViewById(R.id.item14button));
//        imageButtons.add(findViewById(R.id.item15button));
//        imageButtons.add(findViewById(R.id.item16button));
//        imageButtons.add(findViewById(R.id.item17button));
//        imageButtons.add(findViewById(R.id.item18button));
//        imageButtons.add(findViewById(R.id.item19button));
//        imageButtons.add(findViewById(R.id.item20button));

        textViews.add(findViewById(R.id.it1txt));
        textViews.add(findViewById(R.id.it2txt));
        textViews.add(findViewById(R.id.it3txt));
        textViews.add(findViewById(R.id.it4txt));
        textViews.add(findViewById(R.id.it5txt));
        textViews.add(findViewById(R.id.it6txt));
        textViews.add(findViewById(R.id.it7txt));
        textViews.add(findViewById(R.id.it8txt));
        textViews.add(findViewById(R.id.it9txt));
        textViews.add(findViewById(R.id.it10txt));
//        textViews.add(findViewById(R.id.item11text));
//        textViews.add(findViewById(R.id.item12text));
//        textViews.add(findViewById(R.id.item13text));
//        textViews.add(findViewById(R.id.item14text));
//        textViews.add(findViewById(R.id.item15text));
//        textViews.add(findViewById(R.id.item16text));
//        textViews.add(findViewById(R.id.item17text));
//        textViews.add(findViewById(R.id.item18text));
//        textViews.add(findViewById(R.id.item19text));
//        textViews.add(findViewById(R.id.item20text));

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
}