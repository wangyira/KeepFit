package com.example.keepfit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;



public class SearchResultsActivity extends AppCompatActivity {

    ArrayList<ImageButton> imageButtons = new ArrayList<ImageButton>();
    ArrayList<TextView> textViews = new ArrayList<TextView>();

    ArrayList<String> videoRefTitles = new ArrayList<String>();
    ArrayList<String> videoDispTitles = new ArrayList<String>();
    ArrayList<String> lsRefTitles = new ArrayList<String>();
    ArrayList<String> lsDispTitles = new ArrayList<String>();
    ArrayList<String> lsZoomLinks = new ArrayList<String>();
    ArrayList<String> profileRefs = new ArrayList<String>();
    ArrayList<String> profileUsernames = new ArrayList<String>();

    //must all add to <= 20
    int numProfiles = 0;
    int numVideos = 0;
    int numLivestreams = 0;

    //String input = new String();

    //ReentrantLock lock = new ReentrantLock();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        String input = new String();
        //get input
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            input = extras.getString("input");
        }

        //set searchbar to have input in it (that user searched)
        ((EditText) findViewById(R.id.searchBar)).setText(input);

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
                numVideos=0;
                numLivestreams=0;
                numProfiles=0;
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
                numVideos=0;
                numLivestreams=0;
                numProfiles=0;
                String inputa = getString(R.string.tag1);
                makeInvisible();
                search(inputa);
            }
        });

        preset2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numVideos=0;
                numLivestreams=0;
                numProfiles=0;
                Log.d("myTag", "%%%%%%");
                String inputa = getString(R.string.tag2);
                makeInvisible();
                search(inputa);
            }
        });

        preset3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numVideos=0;
                numLivestreams=0;
                numProfiles=0;
                String inputa = getString(R.string.tag3);
                makeInvisible();
                search(inputa);
            }
        });

        preset4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numVideos=0;
                numLivestreams=0;
                numProfiles=0;
                String inputa = getString(R.string.tag4);
                makeInvisible();
                search(inputa);
            }
        });

        search(input);
    }

    private void search(String input){
        //get all search results
        getProfileResults(input);
        getVideoResultsbyTitle(input);
        if(input.equals(getString(R.string.tag1))
                || input.equals(getString(R.string.tag2))
                || input.equals(getString(R.string.tag3))
                || input.equals(getString(R.string.tag4)) ){
            getLivestreamResultsbyTag(input);
            getVideoResultsbyTag(input);
        }
        getLivestreamResultsbyTitle(input);
    }


    private void getProfileResults(String input){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("UserInformation");
        //ref.orderByChild("username").startAt(input).endAt(input+"\uf8ff").limitToFirst(20)
        ref.orderByChild("username").equalTo(input).limitToFirst(20)
        //ref.orderByChild("username").equalTo(input).limitToFirst(20)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("myTag", "555555");
                        Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                        if(results!=null){
                            for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                                if(numProfiles < 3) {
                                    Log.d("myTag", "####555555");

                                    Map<String, String> val = entry.getValue();
                                    profileRefs.add(val.get("referenceTitle"));
                                    profileUsernames.add(val.get("username"));
                                    numProfiles++;
                                }
                                else{break;}
                            }
                        }
                        displayResults();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void getVideoResultsbyTag(String input){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Video References");
        ref.orderByChild("tag").equalTo(input).limitToFirst(20)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("myTag", "44444");
                        Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                        if(results!=null){
                            for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                                if(numVideos < (20-numLivestreams-numProfiles)) {
                                    Log.d("myTag", "###44444");
                                    videoRefTitles.add(entry.getValue().get("reference title"));
                                    videoDispTitles.add(entry.getValue().get("title"));
                                    numVideos++;
                                }
                                else{break;}
                            }
                        }
                        displayResults();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void getVideoResultsbyTitle(String input){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Video References");
        //ref.orderByChild("title").startAt(input).endAt(input+"\uf8ff").limitToFirst(20)
        ref.orderByChild("title").equalTo(input).limitToFirst(20)
                //ref.orderByChild("username").equalTo(input).limitToFirst(20)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("myTag", "33333");
                        Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                        if(results!=null){
                            for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                                if(numVideos < (20-numProfiles-numLivestreams)) {
                                    Log.d("myTag", "###33333");
                                    videoRefTitles.add(entry.getKey());
                                    videoDispTitles.add(entry.getValue().get("title"));
                                    numVideos++;
                                }
                                else{break;}
                            }
                        }
                        displayResults();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void getLivestreamResultsbyTag(String input){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Livestream Details");
        ref.orderByChild("tag").equalTo(input).limitToFirst(7)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("myTag", "22222");
                        Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                        if(results!=null){
                            for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                                if(numLivestreams < (20-numProfiles)) {
                                    Log.d("myTag", "###22222");
                                    lsRefTitles.add(entry.getValue().get("reference title"));
                                    lsDispTitles.add(entry.getValue().get("title"));
                                    lsZoomLinks.add(entry.getValue().get("zoomLink"));
                                    numLivestreams++;
                                }
                                else{break;}
                            }
                        }
                        displayResults();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
        displayResults();
    }

    private void getLivestreamResultsbyTitle(String input){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Livestream Details");
        //ref.orderByChild("title").startAt(input).endAt(input+"\uf8ff").limitToFirst(7)
        ref.orderByChild("title").equalTo(input).limitToFirst(7)
                //ref.orderByChild("username").equalTo(input).limitToFirst(20)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("myTag", "11111");
                        Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                        if(results!=null){
                            for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                                if(numLivestreams < (20-numProfiles) && numLivestreams < 10) {
                                    Log.d("myTag", "####11111");
                                    lsRefTitles.add(entry.getValue().get("referenceTitle"));
                                    lsDispTitles.add(entry.getValue().get("title"));
                                    lsZoomLinks.add(entry.getValue().get("zoomLink"));
                                    numLivestreams++;
                                }
                                else{break;}
                            }
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
                    } else if ( j < numVideos+numLivestreams){
                        openNewActivityLivestream(j);
                    } else {
                        openNewActivityUser(j);
                    }

                }
            });
        }
        Log.d("myTag", numVideos + ", " + numLivestreams + ", " + numProfiles);
        //display video results
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        for(int i=0; i < numVideos; i++) {
            Log.d("storage", "^^^^^^");
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

        for(int i=numVideos; i < numVideos+numLivestreams; i++) {
            Log.d("storage", "&&&&&&&&");
            StorageReference lsimageRef = storageRef.child("/livestream_thumbnail_images/" + lsRefTitles.get(i - numVideos));
            try {
                final int j = i;
                Log.d("storage", "aaaaaaaa " + (i - numVideos) + ", " + lsRefTitles.get(i-numVideos));
                if(numLivestreams>0){
                    final File localFile = File.createTempFile(lsRefTitles.get(i - numVideos), "jpg");

                    lsimageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            ImageButton ib = imageButtons.get(j);

                            Bitmap bm = imgToBitmap(localFile);
                            ib.setImageBitmap(bm);
                            ib.setVisibility(View.VISIBLE);

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

        for(int i=numVideos+numLivestreams; i < numVideos+numLivestreams+numProfiles; i++){
            StorageReference PimageRef = storageRef.child("/profilepictures/" + profileRefs.get(i-numVideos-numLivestreams) );

            try{
                final int j=i;
                final File localFile = File.createTempFile(profileRefs.get(i-numVideos-numLivestreams), "jpg");
                PimageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        ImageButton ib = imageButtons.get(j);

                        Bitmap bm = imgToBitmap(localFile);
                        ib.setImageBitmap(bm);
                        ib.setVisibility(View.VISIBLE);

                        TextView tv = textViews.get(j);
                        tv.setText("USER: " + profileUsernames.get(j-numVideos-numLivestreams));
                        tv.setVisibility(View.VISIBLE);
                    }
                });
            } catch(Exception e){ }
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
    public void openNewActivityLivestream(int i){
        //REMOVE THIS!!!
        /*if(i >= numVideos+numLivestreams){
            String zoomLink = "https://google.com";
            Intent intent = new Intent(this, LivestreamActivity.class);
            intent.putExtra("zoomLink", zoomLink);
            startActivity(intent);
            return;
        }*/


        String zoomLink = lsRefTitles.get(i-numVideos);
        Intent intent = new Intent(this, LivestreamActivity.class);
        intent.putExtra("zoomLink", zoomLink);
        startActivity(intent);
    }
    public void openNewActivityUser(int i){
        //OPEN USER PROFILE
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