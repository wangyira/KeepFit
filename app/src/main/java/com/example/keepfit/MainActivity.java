package com.example.keepfit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.keepfit.authapp.ProfileActivityEdits;
import com.example.keepfit.calories.CalorieActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    ArrayList<ImageButton> imageButtons = new ArrayList<ImageButton>();
    ArrayList<ImageButton> imageButtonsProfile = new ArrayList<ImageButton>(); //for profile pic of user who uploaded vid
    ArrayList<TextView> textViews = new ArrayList<TextView>();
    ArrayList<TextView> textViewsProfile = new ArrayList<TextView>(); //for username of person who uploaded vid
    ArrayList<Button> likes = new ArrayList<Button>(); //for clicks

    ArrayList<String> referenceTitleList = new ArrayList<String>();
    ArrayList<String> displayTitleList = new ArrayList<String>();
    ArrayList<String> livestreamReferenceTitleList = new ArrayList<String>();
    ArrayList<String> livestreamDisplayTitleList = new ArrayList<String>();
    ArrayList<String> livestreamZoomLinks = new ArrayList<String>();

    int numVideos = 0;
    int numLivestreams = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);

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
                        startActivity(new Intent(getApplicationContext(), VideoActivity.class));
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

        populate();


        //set button listeners
        ImageButton searchButton = (ImageButton) findViewById(R.id.searchButton);
        Button preset1 = (Button) findViewById(R.id.preset1);
        Button preset2 = (Button) findViewById(R.id.preset2);
        Button preset3 = (Button) findViewById(R.id.preset3);
        Button preset4 = (Button) findViewById(R.id.preset4);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get input from bar
                EditText searchInputBar = (EditText) findViewById(R.id.searchBar);
                String input = searchInputBar.getText().toString();
                search(input);
            }
        });


        preset1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { search(getString(R.string.tag1)); }
        });

        preset2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { search(getString(R.string.tag2)); }
        });

        preset3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { search(getString(R.string.tag3)); }
        });

        preset4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { search(getString(R.string.tag4)); }
        });
    }

    private void search(String input){
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("input",input);
        startActivity(intent);
    }

    private void populate(){
        addItemstoArrayandMakeInvisible();
        //get livestreams
        FirebaseDatabase databasel = FirebaseDatabase.getInstance();
        DatabaseReference myRefl = databasel.getReference("Livestream Details");
        myRefl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotl) {
                //map to hold all livestream references from database
                HashMap<String, HashMap> lsmAL = new HashMap<String, HashMap>();
                lsmAL = (HashMap<String, HashMap>) snapshotl.getValue();
                for(Map.Entry<String, HashMap> livestream: lsmAL.entrySet()){
                    if(numLivestreams < 20){
                        Log.d("myTag", "#####");
                        livestreamReferenceTitleList.add((String) livestream.getValue().get("reference title"));
                        livestreamDisplayTitleList.add((String) livestream.getValue().get("title"));
                        livestreamZoomLinks.add((String) livestream.getValue().get("zoomLink"));
                        Log.d("myTag", "#####");
                        numLivestreams++;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


        //get videos
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Video References");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //map to hold all video references from database
                GenericTypeIndicator<Map<String, Map<String, String>>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Map<String, String> >>() {};
                Map<String, Map<String, String>> allVideosMap = snapshot.getValue(genericTypeIndicator);

                //for each video (entry in allVideosMap), get the reference title and add it to titleList (if there is space)
                //Iterator it = allVideosMap.entrySet().iterator();
                for(Map.Entry<String, Map<String, String>> entry : allVideosMap.entrySet()){
                    Map<String, String> videoMap = entry.getValue();
                    for(Map.Entry<String, String> data : videoMap.entrySet()){
                        if(data.getKey().equals("reference title")){

                            if(numVideos < (20-numLivestreams)) {
                                referenceTitleList.add(data.getValue());
                                numVideos++;
                            }
                        }
                        else if(data.getKey().equals("title")){
                            displayTitleList.add(data.getValue());
                        }
                    }
                    if(numVideos >= (20-numLivestreams)){break;}
                }
                //titleList now contains titles of videos (and images) to be displayed
                //livestream titleLists contain titles of livestreams to be displayed

                displayThumbnails();

                //titleList now contains titles of all videos (and images)
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                for(int i=0; i < numVideos; i++){
                    StorageReference imageRef = storageRef.child("/thumbnail_images/" + referenceTitleList.get(i)+".jpg");
                    try{
                        final int j=i;
                        final File localFile = File.createTempFile(referenceTitleList.get(i), "jpg");
                        imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                ImageButton ib = imageButtons.get(j);

                                Bitmap bm = imgToBitmap(localFile);
                                ib.setImageBitmap(bm);
                                ib.setVisibility(View.VISIBLE);

                                TextView tv = textViews.get(j);
                                tv.setText(displayTitleList.get(j));
                                tv.setVisibility(View.VISIBLE);
                            }
                        });
                    } catch(Exception e) { }
                }
                Log.d("myTag", numVideos + " " + numLivestreams);
                for(int i=numVideos; i < numVideos+numLivestreams; i++){

                    StorageReference imageRef = storageRef.child("/livestream_thumbnail_images/" + livestreamReferenceTitleList.get(i-numVideos) + ".jpg");
                    try{
                        final int j=i;
                        final File localFile = File.createTempFile(livestreamReferenceTitleList.get(i-numVideos), "jpg");
                        imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                ImageButton ib = imageButtons.get(j);

                                Bitmap bm = imgToBitmap(localFile);
                                ib.setImageBitmap(bm);
                                ib.setVisibility(View.VISIBLE);

                                TextView tv = textViews.get(j);
                                tv.setText("LIVESTREAM: " + livestreamDisplayTitleList.get(j-numVideos));
                                tv.setVisibility(View.VISIBLE);
                            }
                        });
                    } catch(Exception e){ }
                }



                for(int i=0; i < numVideos+numLivestreams; i++){
                    final int j = i;
                    imageButtons.get(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(j < numVideos){
                                openNewActivityVideo(j);
                            } else{
                                openNewActivityLivestream(j);
                            }

                        }
                    });
                }

                //REMOVE THIS
                imageButtons.get(numVideos).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openNewActivityLivestream(numVideos);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    private void displayThumbnails(){

        //REMOVE THIS!!!!!!!!!!
        StorageReference storageRefa = FirebaseStorage.getInstance().getReference();
        StorageReference livestreamRef = storageRefa.child("/livestream_thumbnail_images/6257ec4b-736d-45dd-af2e-313fa7b30b4a.jpg");
        try{
            final File llocalFile = File.createTempFile("6257ec4b-736d-45dd-af2e-313fa7b30b4a", "jpg");
            livestreamRef.getFile(llocalFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    ImageButton ib = imageButtons.get(numVideos);
                    Bitmap bm = imgToBitmap(llocalFile);
                    ib.setImageBitmap(bm);
                    ib.setVisibility(View.VISIBLE);

                    TextView tv = textViews.get(numVideos);
                    tv.setText("LIVESTREAM : workout with me");
                    tv.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e){ }






        //titleList now contains titles of all videos (and images)
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        for(int i=0; i < numVideos; i++){
            StorageReference imageRef = storageRef.child("/thumbnail_images/" + referenceTitleList.get(i)+".jpg");
            try{
                final int j=i;
                final File localFile = File.createTempFile(referenceTitleList.get(i), "jpg");
                imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        ImageButton ib = imageButtons.get(j);

                        Bitmap bm = imgToBitmap(localFile);
                        ib.setImageBitmap(bm);
                        ib.setVisibility(View.VISIBLE);

                        TextView tv = textViews.get(j);
                        tv.setText(displayTitleList.get(j));
                        tv.setVisibility(View.VISIBLE);
                    }
                });
            } catch(Exception e) { }
        }
        Log.d("myTag", numVideos + " " + numLivestreams);
        for(int i=numVideos; i < numVideos+numLivestreams; i++){

            StorageReference imageRef = storageRef.child("/livestream_thumbnail_images/" + livestreamReferenceTitleList.get(i-numVideos) + ".jpg");
            try{
                final int j=i;
                final File localFile = File.createTempFile(livestreamReferenceTitleList.get(i-numVideos), "jpg");
                imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        ImageButton ib = imageButtons.get(j);

                        Bitmap bm = imgToBitmap(localFile);
                        ib.setImageBitmap(bm);
                        ib.setVisibility(View.VISIBLE);

                        TextView tv = textViews.get(j);
                        tv.setText("LIVESTREAM: " + livestreamDisplayTitleList.get(j-numVideos));
                        tv.setVisibility(View.VISIBLE);
                    }
                });
            } catch(Exception e){ }
        }
    }


    private void addItemstoArrayandMakeInvisible(){
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
        String referenceTitle = referenceTitleList.get(i);
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


        String zoomLink = livestreamZoomLinks.get(i-numVideos);
        Intent intent = new Intent(this, LivestreamActivity.class);
        intent.putExtra("zoomLink", zoomLink);
        startActivity(intent);
    }
    public void openNewActivityProfile(int i){
        //Todo: start Profile activity for user who uploaded video
    }
    private void like(int i){
        //TODO: set likes in user database

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

}
