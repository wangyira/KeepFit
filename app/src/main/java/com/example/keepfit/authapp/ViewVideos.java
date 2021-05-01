package com.example.keepfit.authapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keepfit.MainActivity;
import com.example.keepfit.R;
import com.example.keepfit.StartLivestreamActivity;
import com.example.keepfit.VideoActivity;
import com.example.keepfit.VideoUploadActivity;
import com.example.keepfit.calories.CalorieActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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

public class ViewVideos extends AppCompatActivity {
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference ref;

    //uploaded videos
    ArrayList<String> videoRefTitles = new ArrayList<String>();
    ArrayList<String> videoDispTitles = new ArrayList<String>();

    ArrayList<ImageButton> imageButtons = new ArrayList<ImageButton>();
    ArrayList<TextView> textViews = new ArrayList<TextView>();
    ArrayList<Button> deleteVideo = new ArrayList<Button>();

    int numVideos;

    String which = "";
    String videoId;

    private FirebaseUser user;
    private DatabaseReference dbreference;
    private String userId;

    private Button btnWipeWatchHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null)
            userId = user.getUid();
        else userId = "";

        setContentView(R.layout.activity_viewvideos);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        numVideos = 0;


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

        addItemstoArray();
        makeInvisible();

        Bundle b = getIntent().getExtras();
        String type = b.getString("type");
        btnWipeWatchHistory = findViewById(R.id.wipeWatchHistory);

        if (type.equals("liked")) {
            getLikedVideos();
            btnWipeWatchHistory.setVisibility(View.GONE);
        }
        if (type.equals("disliked")) {
            getDislikedVideos();
            btnWipeWatchHistory.setVisibility(View.GONE);
        }
        if (type.equals("watched")) {
            getWatchedVideoResultsbyTitle();
            btnWipeWatchHistory.setVisibility(View.VISIBLE);

            btnWipeWatchHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wipeHistory();
                }
            });
        }
        if (type.equals("uploaded")) {
            getVideoResultsbyTitle();
            btnWipeWatchHistory.setVisibility(View.GONE);
        }


        dbreference = FirebaseDatabase.getInstance().getReference("UserInformation");




    }

    private void viewHistory(){

        videoRefTitles.clear();
        numVideos = 0;

        if(findViewById(R.id.uitem1txt).isShown()){
            for(int i=0; i < 10; i++){
                textViews.get(i).setVisibility(View.GONE);
                imageButtons.get(i).setVisibility(View.GONE);
            }
        }
        else{
            search(4);
        }
    }

    private void wipeHistory(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        DatabaseReference ref = database.getReference("Video History").child(username);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    snapshot1.getRef().removeValue();
                }
                viewHistory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        numVideos = 0;
        videoRefTitles.clear();
        videoDispTitles.clear();

        ref.child(username).limitToFirst(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, String> results = (Map<String, String>) snapshot.getValue();
                        //Log.d("getting disliked", String.valueOf(results.size()));
                        if(results!=null){
                            for(Map.Entry<String, String> entry : results.entrySet()){
                                videoRefTitles.add(entry.getValue());
                                ref = database.getReference("Video References");
                                ref.orderByChild("referenceTitle").equalTo(entry.getValue()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot2) {

                                        for(DataSnapshot ds : snapshot2.getChildren()) {
                                            videoId = ds.getKey();
                                            //Log.d("TAG", uid);
                                        }
                                        ref.child(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                                Map <String, String> results = (Map <String, String>) snapshot3.getValue();
                                                videoDispTitles.add(results.get("title"));
                                                Log.d("num disliked videos", String.valueOf(numVideos) + "  " + String.valueOf(videoRefTitles.size()) + "  " + String.valueOf(videoDispTitles.size()));
                                                if(numVideos == videoRefTitles.size() && numVideos == videoDispTitles.size())
                                                    displayDislikedVideos(videoDispTitles);
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
                                numVideos++;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    //uploaded videos
    private void getVideoResultsbyTitle(){
        videoRefTitles.clear();
        videoDispTitles.clear();
        numVideos = 0;

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
                        //Log.d("getting uploaded", String.valueOf(results.size()));
                        if(results!=null){
                            for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                                videoRefTitles.add(entry.getValue().get("referenceTitle"));
                                videoDispTitles.add(entry.getValue().get("title"));
                                numVideos++;
                            }
                        }
                        displayResultsUploaded();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    //watched videos
    private void getWatchedVideoResultsbyTitle(){
        videoRefTitles.clear();
        videoDispTitles.clear();
        numVideos = 0;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("Video History");

        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);


        ref.child(username).limitToFirst(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, String> results = (Map<String, String>) snapshot.getValue();
                        //Log.d("getting watched", String.valueOf(results.size()));
                        if(results!=null){
                            //final CountDownLatch latch = new CountDownLatch(results.entrySet().size());
                            //final CountDownLatch l2 = new CountDownLatch(1);
                            for(Map.Entry<String, String> entry : results.entrySet()){
                                //final AtomicBoolean done = new AtomicBoolean(false);
                                numVideos++;
                                Log.e("key",entry.getKey());
                                Log.e("value",entry.getValue());
                                videoRefTitles.add(entry.getValue());
                                ref = database.getReference("Video References");
                                ref.orderByChild("referenceTitle").equalTo(entry.getValue()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
//                                        VideoReference video = snapshot2.getValue(VideoReference.class);
//                                        watchedVideoDispTitles.add(video.getTitle());
                                        for(DataSnapshot ds : snapshot2.getChildren()) {
                                            videoId = ds.getKey();
                                        }
                                        //Log.d("in datachange", "here");
                                        ref.child(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                                Map <String, String> results = (Map <String, String>) snapshot3.getValue();
                                                videoDispTitles.add(results.get("title"));
                                                //done.set(true);
                                                //Log.d("here", "here");
                                                if(numVideos == videoRefTitles.size() && numVideos == videoDispTitles.size())
                                                    displayWatchedVideos();
                                                //Log.d("num watched videos", String.valueOf(numWatchedVideos) + "  " + String.valueOf(watchedVideoRefTitles.size()) + "  " + String.valueOf(watchedVideoDispTitles.size()));
                                                //Log.d("latch", String.valueOf(latch));
                                                //latch.countDown();
                                                //Log.d("latch after", String.valueOf(latch));
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
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    //liked videos
    private void getLikedVideos(){
        videoRefTitles.clear();
        videoDispTitles.clear();
        numVideos = 0;

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
                        //Log.d("getting liked", String.valueOf(results.size()));
                        if(results!=null){
                            for(Map.Entry<String, String> entry : results.entrySet()){
                                videoRefTitles.add(entry.getValue());
                                ref = database.getReference("Video References");
                                ref.orderByChild("referenceTitle").equalTo(entry.getValue()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot2) {

                                        for(DataSnapshot ds : snapshot2.getChildren()) {
                                            videoId = ds.getKey();
                                            //Log.d("TAG", uid);
                                        }
                                        ref.child(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                                Map <String, String> results = (Map <String, String>) snapshot3.getValue();
                                                videoDispTitles.add(results.get("title"));
                                                //Log.d("num Liked videos", String.valueOf(numLikedVideos) + "  " + String.valueOf(videoRefTitles1.size()) + "  " + String.valueOf(videoDispTitles1.size()));
                                                if(numVideos == videoRefTitles.size() && numVideos == videoDispTitles.size())
                                                    displayLikedVideos(videoDispTitles);
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
                                numVideos++;

                            }
                        }
//                        try {
//                            Thread.sleep(5000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        //Log.d("num liked videos", String.valueOf(numLikedVideos) + "  " + String.valueOf(videoRefTitles1.size()) + "  " + String.valueOf(videoDispTitles1.size()));
                        //displayLikedVideos();
                        //numVideos = results.entrySet().size();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    //display uploaded
    private void displayResultsUploaded(){
        Log.d("num uploaded", String.valueOf(numVideos));
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

                        Button delete = deleteVideo.get(j);

                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Log.d("ONCLICK", "Clicked Delete");
                                deleteVideo(videoRefTitles.get(j));
                                for(int k =0; k < numVideos; k++){
                                    Log.d("removing", String.valueOf(k));
                                    imageButtons.get(k).setVisibility(View.GONE);
                                    textViews.get(k).setVisibility(View.GONE);
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
                            numVideos--;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    //get watched videos
    private void displayWatchedVideos(){
        for(int i=0; i < numVideos; i++){
            final int j = i;
            imageButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("myTag", "@@@@@@@");
                    openNewActivityVideoWatched(j);
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

    //get liked videos
    private void displayLikedVideos(ArrayList<String> displayTitles){
        for(int i=0; i < numVideos; i++){
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
                        tv.setText(displayTitles.get(j));
                        tv.setVisibility(View.VISIBLE);

                    }
                });
            } catch (Exception e) {
            }
        }
    }

    //get disliked videos
    private void displayDislikedVideos(ArrayList<String> displayTitles){
        for(int i=0; i < numVideos; i++){
            final int j = i;
            imageButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("myTag", "@@@@@@@");
                    openNewActivityVideoDisliked(j);
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
                        tv.setText(displayTitles.get(j));
                        tv.setVisibility(View.VISIBLE);
                        Log.d("end of display", "" + displayTitles.get(j));

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
        String referenceTitle = videoRefTitles.get(i);
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("referenceTitle",referenceTitle);
        startActivity(intent);
    }

    public void openNewActivityVideoLiked(int i){
        String referenceTitle = videoRefTitles.get(i);
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("referenceTitle",referenceTitle);
        startActivity(intent);
    }
    public void openNewActivityVideoDisliked(int i){
        String referenceTitle = videoRefTitles.get(i);
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("referenceTitle",referenceTitle);
        startActivity(intent);
    }
}
