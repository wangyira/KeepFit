package com.example.keepfit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.keepfit.authapp.ProfileActivityEdits;
import com.example.keepfit.authapp.PublicProfile;
import com.example.keepfit.calories.CalorieActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;



public class MainActivity extends AppCompatActivity {

    ArrayList<ImageButton> imageButtons = new ArrayList<ImageButton>();
    ArrayList<TextView> textViews = new ArrayList<TextView>();
    ArrayList<ImageButton> imageButtonsProfile = new ArrayList<ImageButton>();
    ArrayList<TextView> textViewsProfile = new ArrayList<TextView>();
    ArrayList<Button> likes = new ArrayList<Button>();
    ArrayList<Button> dislikes = new ArrayList<Button>();


    ArrayList<String> videoRefTitles = new ArrayList<String>();
    ArrayList<String> videoDispTitles = new ArrayList<String>();
    ArrayList<String> lsRefTitles = new ArrayList<String>();
    ArrayList<String> lsDispTitles = new ArrayList<String>();
    ArrayList<String> lsZoomLinks = new ArrayList<String>();

    ArrayList<String> videoUploadingUser = new ArrayList<String>();
    ArrayList<String> livestreamUploadingUser = new ArrayList<String>();
    ArrayList<String> videoUploadingUserPic = new ArrayList<String>();
    ArrayList<String> livestreamUplodingUserPic = new ArrayList<String>();

    //must all add to <= 20
    int numVideos = 0;
    int numLivestreams = 0;

    String userID = new String();

    //String input = new String();

    //ReentrantLock lock = new ReentrantLock();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set search selected
        bottomNavigationView.setSelectedItemId(R.id.nav_search);

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



        String input = new String();
        //get input
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            input = extras.getString("input");
        }

        //set searchbar to have input in it (that user searched)
        ((EditText) findViewById(R.id.searchBar)).setText("");

        addItemstoArray();
        makeInvisible();

        //set button listeners
        ImageButton searchButton = (ImageButton) findViewById(R.id.searchButton);
        Button preset1 = (Button) findViewById(R.id.preset1);
        Button preset2 = (Button) findViewById(R.id.preset2);
        Button preset3 = (Button) findViewById(R.id.preset3);
        Button preset4 = (Button) findViewById(R.id.preset4);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetValues();
                //get input from bar
                EditText searchInputBar = (EditText) findViewById(R.id.searchBar);
                String inputa = searchInputBar.getText().toString();
                makeInvisible();
                search(inputa);
            }
        });


        preset1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetValues();
                String inputa = getString(R.string.tag1);
                makeInvisible();
                search(inputa);
            }
        });

        preset2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetValues();
                Log.d("myTag", "%%%%%%");
                String inputa = getString(R.string.tag2);
                makeInvisible();
                search(inputa);
            }
        });

        preset3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetValues();
                String inputa = getString(R.string.tag3);
                makeInvisible();
                search(inputa);
            }
        });

        preset4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetValues();
                String inputa = getString(R.string.tag4);
                makeInvisible();
                search(inputa);
            }
        });

        getVideos();
        //getLivestreams();

    }

    private void search(String input){
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("input",input);
        startActivity(intent);
    }

    private void getVideos(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Video References");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //map to hold all video references from database
                GenericTypeIndicator<Map<String, Map<String, String>>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Map<String, String>>>() {
                };
                Map<String, Map<String, String>> allVideosMap = snapshot.getValue(genericTypeIndicator);

                //for each video (entry in allVideosMap), get the reference title and add it to titleList (if there is space)
                //Iterator it = allVideosMap.entrySet().iterator();
                for (Map.Entry<String, Map<String, String>> entry : allVideosMap.entrySet()) {
                    Map<String, String> videoMap = entry.getValue();
                    if (numVideos < (20 - numLivestreams)) {
                        videoRefTitles.add(videoMap.get("reference title"));
                        videoDispTitles.add(videoMap.get("title"));
                        String uploadingUser = videoMap.get("uploadingUser");
                        //getVideoUploadingUserPic(uploadingUser);
                        videoUploadingUser.add(uploadingUser);
                        numVideos++;
                    } else {
                        break;
                    }
                }
                //displayResults();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
        getLivestreams();
    }

    private void getLivestreams(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Livestream Details");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                    for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                        if(numLivestreams < 20) {
                            Log.d("entering Livestream", entry.getValue().get("referenceTitle"));
                            lsRefTitles.add(entry.getValue().get("referenceTitle"));
                            lsDispTitles.add(entry.getValue().get("title"));
                            lsZoomLinks.add(entry.getValue().get("zoomLink"));

                            String uploadingUser = (String) entry.getValue().get("uploadingUser");
                            livestreamUploadingUser.add(uploadingUser);
                            //getLivestreamUploadingUserPic(uploadingUser);

                            numLivestreams++;
                        }
                        else{break;}
                    }
                displayResults();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void displayResults(){
        for(int i=0; i < 20; i++){
            final int j = i;
            imageButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("myTag", "@@@@@@@");
                    if(j < numVideos){
                        openNewActivityVideo(j);
                    } else{
                        openNewActivityLivestream(j);
                    }

                }
            });
        }
        //display video results
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        for(int i=0; i < numVideos; i++) {
            Log.d("Printing Video", "Video: " + videoDispTitles.get(i));
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

                        Button bp = likes.get(j);
                        //change color

                        bp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("ONCLICK", "in ON click");
                                like(videoRefTitles.get(j));
                            }
                        });
                        bp.setVisibility(View.VISIBLE);

                        //String ProfilerefTitle = videoUploadingUserPic.get(j);
                        //Log.d("ProfilerefTitle", ProfilerefTitle);
                        //StorageReference imageRefProf = storageRef.child("/profilepictures/" + ProfilerefTitle);

                        ImageButton ibp = imageButtonsProfile.get(j);
                        /*try{
                            final File localFileP = File.createTempFile(ProfilerefTitle, "jpg");
                            imageRefProf.getFile(localFileP).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bmp = imgToBitmap(localFileP);
                                    ibp.setImageBitmap(bmp);

                                }
                            });
                        }catch (Exception e){ }*/
                        ibp.setVisibility(View.VISIBLE);

                        TextView tvp = textViewsProfile.get(j);
                        tvp.setText(videoUploadingUser.get(j));

                        tvp.setVisibility(View.VISIBLE);

                        TextView tv = textViews.get(j);
                        tv.setText(videoDispTitles.get(j));
                        tv.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
            }
        }

        //display livestreams
        for(int i=numVideos; i < numVideos+numLivestreams; i++) {
            Log.d("storage", "!&&&&&&&&" + lsRefTitles.size() + ", " + numLivestreams);
            StorageReference lsimageRef = storageRef.child("/livestream_thumbnail_images/" + lsRefTitles.get(i - numVideos));
            try {
                final int j = i;
                Log.d("storage", "!aaaaaaaa " + (i - numVideos) + ", " + lsRefTitles.get(i-numVideos));
                if(numLivestreams>0){
                    final File localFile = File.createTempFile(lsRefTitles.get(i - numVideos), "jpg");

                    lsimageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            ImageButton ib = imageButtons.get(j);

                            Bitmap bm = imgToBitmap(localFile);
                            ib.setImageBitmap(bm);
                            ib.setVisibility(View.VISIBLE);

                            //String ProfilerefTitle = videoUploadingUserPic.get(j);

                            //StorageReference imageRefProf = storageRef.child("/profilepictures/" + ProfilerefTitle);

                            ImageButton ibp = imageButtonsProfile.get(j);
                            /*try{
                                final File localFileP = File.createTempFile(ProfilerefTitle, "jpg");
                                imageRefProf.getFile(localFileP).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                        Bitmap bmp = imgToBitmap(localFileP);
                                        ibp.setImageBitmap(bmp);

                                    }
                                });
                            }catch (Exception e){ }*/
                            ibp.setVisibility(View.VISIBLE);

                            TextView tvp = textViewsProfile.get(j);
                            tvp.setText(livestreamUploadingUser.get(j-numVideos));
                            tvp.setVisibility(View.VISIBLE);

                            TextView tv = textViews.get(j);
                            tv.setText("LIVESTREAM: " + lsDispTitles.get(j - numVideos));
                            tv.setVisibility(View.VISIBLE);
                        }
                    });
                }

            } catch (Exception e) {
                Log.d("error", e.getMessage());
            }
        }
    }

    private void addItemstoArray(){
        imageButtons.add(findViewById(R.id.item1button));
        imageButtons.add(findViewById(R.id.item2button));
        imageButtons.add(findViewById(R.id.item3button));
        imageButtons.add(findViewById(R.id.item4button));
        imageButtons.add(findViewById(R.id.item5button));
        imageButtons.add(findViewById(R.id.item6button));
        imageButtons.add(findViewById(R.id.item7button));
        imageButtons.add(findViewById(R.id.item8button));
        imageButtons.add(findViewById(R.id.item9button));
        imageButtons.add(findViewById(R.id.item10button));
        imageButtons.add(findViewById(R.id.item11button));
        imageButtons.add(findViewById(R.id.item12button));
        imageButtons.add(findViewById(R.id.item13button));
        imageButtons.add(findViewById(R.id.item14button));
        imageButtons.add(findViewById(R.id.item15button));
        imageButtons.add(findViewById(R.id.item16button));
        imageButtons.add(findViewById(R.id.item17button));
        imageButtons.add(findViewById(R.id.item18button));
        imageButtons.add(findViewById(R.id.item19button));
        imageButtons.add(findViewById(R.id.item20button));

        textViews.add(findViewById(R.id.item1text));
        textViews.add(findViewById(R.id.item2text));
        textViews.add(findViewById(R.id.item3text));
        textViews.add(findViewById(R.id.item4text));
        textViews.add(findViewById(R.id.item5text));
        textViews.add(findViewById(R.id.item6text));
        textViews.add(findViewById(R.id.item7text));
        textViews.add(findViewById(R.id.item8text));
        textViews.add(findViewById(R.id.item9text));
        textViews.add(findViewById(R.id.item10text));
        textViews.add(findViewById(R.id.item11text));
        textViews.add(findViewById(R.id.item12text));
        textViews.add(findViewById(R.id.item13text));
        textViews.add(findViewById(R.id.item14text));
        textViews.add(findViewById(R.id.item15text));
        textViews.add(findViewById(R.id.item16text));
        textViews.add(findViewById(R.id.item17text));
        textViews.add(findViewById(R.id.item18text));
        textViews.add(findViewById(R.id.item19text));
        textViews.add(findViewById(R.id.item20text));

        imageButtonsProfile.add(findViewById(R.id.item1user));
        imageButtonsProfile.add(findViewById(R.id.item2user));
        imageButtonsProfile.add(findViewById(R.id.item3user));
        imageButtonsProfile.add(findViewById(R.id.item4user));
        imageButtonsProfile.add(findViewById(R.id.item5user));
        imageButtonsProfile.add(findViewById(R.id.item6user));
        imageButtonsProfile.add(findViewById(R.id.item7user));
        imageButtonsProfile.add(findViewById(R.id.item8user));
        imageButtonsProfile.add(findViewById(R.id.item9user));
        imageButtonsProfile.add(findViewById(R.id.item10user));
        imageButtonsProfile.add(findViewById(R.id.item11user));
        imageButtonsProfile.add(findViewById(R.id.item12user));
        imageButtonsProfile.add(findViewById(R.id.item13user));
        imageButtonsProfile.add(findViewById(R.id.item14user));
        imageButtonsProfile.add(findViewById(R.id.item15user));
        imageButtonsProfile.add(findViewById(R.id.item16user));
        imageButtonsProfile.add(findViewById(R.id.item17user));
        imageButtonsProfile.add(findViewById(R.id.item18user));
        imageButtonsProfile.add(findViewById(R.id.item19user));
        imageButtonsProfile.add(findViewById(R.id.item20user));

        textViewsProfile.add(findViewById(R.id.item1username));
        textViewsProfile.add(findViewById(R.id.item2username));
        textViewsProfile.add(findViewById(R.id.item3username));
        textViewsProfile.add(findViewById(R.id.item4username));
        textViewsProfile.add(findViewById(R.id.item5username));
        textViewsProfile.add(findViewById(R.id.item6username));
        textViewsProfile.add(findViewById(R.id.item7username));
        textViewsProfile.add(findViewById(R.id.item8username));
        textViewsProfile.add(findViewById(R.id.item9username));
        textViewsProfile.add(findViewById(R.id.item10username));
        textViewsProfile.add(findViewById(R.id.item11username));
        textViewsProfile.add(findViewById(R.id.item12username));
        textViewsProfile.add(findViewById(R.id.item13username));
        textViewsProfile.add(findViewById(R.id.item14username));
        textViewsProfile.add(findViewById(R.id.item15username));
        textViewsProfile.add(findViewById(R.id.item16username));
        textViewsProfile.add(findViewById(R.id.item17username));
        textViewsProfile.add(findViewById(R.id.item18username));
        textViewsProfile.add(findViewById(R.id.item19username));
        textViewsProfile.add(findViewById(R.id.item20username));

        likes.add(findViewById(R.id.item1like));
        likes.add(findViewById(R.id.item2like));
        likes.add(findViewById(R.id.item3like));
        likes.add(findViewById(R.id.item4like));
        likes.add(findViewById(R.id.item5like));
        likes.add(findViewById(R.id.item6like));
        likes.add(findViewById(R.id.item7like));
        likes.add(findViewById(R.id.item8like));
        likes.add(findViewById(R.id.item9like));
        likes.add(findViewById(R.id.item10like));
        likes.add(findViewById(R.id.item11like));
        likes.add(findViewById(R.id.item12like));
        likes.add(findViewById(R.id.item13like));
        likes.add(findViewById(R.id.item14like));
        likes.add(findViewById(R.id.item15like));
        likes.add(findViewById(R.id.item16like));
        likes.add(findViewById(R.id.item17like));
        likes.add(findViewById(R.id.item18like));
        likes.add(findViewById(R.id.item19like));
        likes.add(findViewById(R.id.item20like));

    }

    private void makeInvisible(){
        for(ImageButton ib : imageButtons){
            ib.setVisibility(View.GONE);
        }
        for(TextView tv : textViews){
            tv.setVisibility(View.GONE);
        }
        for(ImageButton ib : imageButtonsProfile){
            ib.setVisibility(View.GONE);
        }
        for(TextView tv : textViewsProfile){
            tv.setVisibility(View.GONE);
        }
        for(Button l : likes){
            l.setVisibility(View.GONE);
        }
    }

    public void openNewActivityVideo(int i){
        String referenceTitle = videoRefTitles.get(i);
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("referenceTitle",referenceTitle);
        startActivity(intent);
    }
    public void openNewActivityLivestream(int i){
        //REMOVE THIS!!!
        /*if(i >= numVideos+numLivestreams){
            String zoomLink = "https://google.com";
            Intent intent = new Intent(this, LivestreamActivity.class);
            intent.putExtra("zoomLink", zoomLink);
            startActivity(intent);
            return;
        }*/


        String zoomLink = lsZoomLinks.get(i-numVideos);
        Intent intent = new Intent(this, LivestreamActivity.class);
        intent.putExtra("zoomLink", zoomLink);
        startActivity(intent);
    }

    private void getLivestreamUploadingUserPic(String username){
        DatabaseReference picRef = FirebaseDatabase.getInstance().getReference("UserInformation");
        picRef.orderByChild("username").equalTo(username)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, String> map = (Map<String, String>) snapshot.getValue();
                        livestreamUplodingUserPic.add(map.get("referenceTitle"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

    }

    private void getVideoUploadingUserPic(String username){
        Log.d("username", username);
        DatabaseReference picRef = FirebaseDatabase.getInstance().getReference("UserInformation");
        picRef.orderByChild("username").equalTo(username)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("key", (String) snapshot.getKey());
                        Map<String, String> map = (Map<String, String>) snapshot.getValue();
                        Log.d("value", "value: " + map.get("referenceTitle"));
                        videoUploadingUserPic.add(map.get("referenceTitle"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

    }

    private void resetValues(){
        numVideos=0;
        numLivestreams=0;

        videoRefTitles = new ArrayList<String>();
        videoDispTitles = new ArrayList<String>();
        lsRefTitles = new ArrayList<String>();
        lsDispTitles = new ArrayList<String>();
        lsZoomLinks = new ArrayList<String>();

        videoUploadingUser = new ArrayList<String>();
        livestreamUploadingUser = new ArrayList<String>();
        videoUploadingUserPic = new ArrayList<String>();
        livestreamUplodingUserPic = new ArrayList<String>();

    }

    private void like(String refTitle){
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        //String userID = getUserID(username);
        //getUserID(username);
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes").child(username);
        likeRef.push().setValue(refTitle);
        //userID = new String();
        //check if refTitle already in likeRef
        /*likeRef.orderByValue().equalTo(refTitle)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("null", "onDataChange");
                        Log.d("key", snapshot.getKey());
                        if(snapshot.getKey()=="likes"){
                            Log.d("null", "null");
                            likeRef.push().setValue(refTitle);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });*/
        /*likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("null", "onDataChange");
                Log.d("key", snapshot.getKey());
                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                if(map==null){
                    Log.d("here", "made it to here a");
                    likeRef.push().setValue(refTitle);
                }
                else if(map.size()==0){
                    Log.d("here", "made it to here b");
                    likeRef.push().setValue(refTitle);
                }
                else{
                    boolean found = false;
                    for(Map.Entry<String, String> entry : map.entrySet()){
                        Log.d("Key: ", entry.getKey());
                        //Log.d("Value: ", entry.getValue());
                        Log.d("refTitle: ", refTitle);
                        if(entry.getValue().equals(refTitle)){
                            found = true;
                            Log.d("here", "made it to here c");
                            break;
                        }
                    }
                    if(!found){
                        Log.d("here", "made it to here");
                        likeRef.push().setValue(refTitle); }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        //Pair<String, String> pair = new Pair<String, String>(videoRefTitles.get(j), null);
        //likeRef.push().setValue(refTitle);
    }

    private void getUserID(String username){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInformation");
        userRef.orderByChild("username").equalTo(username)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userID = snapshot.getKey();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
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
                //Logger.getLogger(MainActivity.this.class.getName()).log(Level.SEVERE, null, ex);
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
}