package com.example.keepfit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keepfit.authapp.ProfileActivityEdits;
import com.example.keepfit.authapp.PublicProfile;
import com.example.keepfit.authapp.User;
import com.example.keepfit.authapp.ViewVideos;
import com.example.keepfit.calories.CalorieActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.core.OrderBy;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.min;


public class MainActivity extends AppCompatActivity {

    boolean liked = false;
    boolean disliked = false;
    ArrayList<ImageButton> imageButtons = new ArrayList<ImageButton>();
    ArrayList<TextView> textViews = new ArrayList<TextView>();
    //ArrayList<ImageButton> imageButtonsProfile = new ArrayList<ImageButton>();
    ArrayList<TextView> textViewsProfile = new ArrayList<TextView>();
    ArrayList<ImageButton> likes = new ArrayList<ImageButton>();
    ArrayList<ImageButton> dislikes = new ArrayList<ImageButton>();
    ArrayList<Button> viewCommentButtons = new ArrayList<Button>();
    ArrayList<TextView> numLikes = new ArrayList<TextView>();
    ArrayList<TextView> numDislikes = new ArrayList<TextView>();

    //ArrayList<VideoReference> allVideos  = new ArrayList<VideoReference>();

    //ArrayList<String> videoRefTitles = new ArrayList<String>();
    //ArrayList<String> videoDispTitles = new ArrayList<String>();
    ArrayList<String> lsRefTitles = new ArrayList<String>();
    ArrayList<String> lsDispTitles = new ArrayList<String>();
    ArrayList<String> lsZoomLinks = new ArrayList<String>();

    //ArrayList<String> videoUploadingUser = new ArrayList<String>();
    ArrayList<String> livestreamUploadingUser = new ArrayList<String>();
    //ArrayList<String> videoUploadingUserPic = new ArrayList<String>();
    ArrayList<String> livestreamUplodingUserPic = new ArrayList<String>();

    ArrayList<Comment> videoComments = new ArrayList<Comment>();
    ArrayList<VideoReference> videos = new ArrayList<VideoReference>();

    Button btnviewSearch;
    DatabaseReference ref;

    ArrayList<TextView> searchTextViews = new ArrayList<TextView>();
    ArrayList<Button> searchBtnTextViews = new ArrayList<Button>();
    Button search1;
    ArrayList<String> searchKeywords = new ArrayList<String>();

    //must all add to <= 20
    int numVideos = 0;
    int numLivestreams = 0;

    String userID = new String();
    String myReturnString;


    //String input = new String();

    //ReentrantLock lock = new ReentrantLock();

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dl = (DrawerLayout)findViewById(R.id.drawer);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Intent i = new Intent(MainActivity.this, ViewVideos.class);
                switch(id)
                {
                    case R.id.account:
                        startActivity(new Intent(getApplicationContext(), ProfileActivityEdits.class));
                        break;
                    case R.id.navviewliked:
                        i.putExtra("type", "liked");
                        startActivity(i);
                        break;
                    case R.id.navviewdisliked:
                        i.putExtra("type", "disliked");
                        startActivity(i);
                        break;
                    case R.id.navviewwatched:
                        i.putExtra("type", "watched");
                        startActivity(i);
                        break;
                    case R.id.navviewuploaded:
                        i.putExtra("type", "uploaded");
                        startActivity(i);
                        break;
                    default:
                        return true;
                }
                return true;

            }
        });


        //navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set search selected
        bottomNavigationView.setSelectedItemId(R.id.nav_search);

        //perform itemselectedlistener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                switch (menuItem.getItemId()){
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

        btnviewSearch = findViewById(R.id.searchHistoryBtn);

        searchTextViews.add(findViewById(R.id.searchText1));
        searchTextViews.add(findViewById(R.id.searchText2));
        searchTextViews.add(findViewById(R.id.searchText3));
        searchTextViews.add(findViewById(R.id.searchText4));
        searchTextViews.add(findViewById(R.id.searchText5));

        searchBtnTextViews.add(findViewById(R.id.searchBtn1));
        searchBtnTextViews.add(findViewById(R.id.searchBtn2));
        searchBtnTextViews.add(findViewById(R.id.searchBtn3));
        searchBtnTextViews.add(findViewById(R.id.searchBtn4));
        searchBtnTextViews.add(findViewById(R.id.searchBtn5));

        search1 = findViewById(R.id.searchBtn1);

        //hide the 5 search keyword and button by default
        for(TextView tv : searchTextViews){
            tv.setVisibility(View.GONE);
        }
        for(Button bt : searchBtnTextViews){
            bt.setVisibility(View.GONE);
        }

        btnviewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(findViewById(R.id.searchText1).isShown()){
                    for(TextView tv : searchTextViews){
                        tv.setVisibility(View.GONE);
                    }
                    for(Button bt : searchBtnTextViews){
                        bt.setVisibility(View.GONE);
                    }
                }
                else{
                    getSearchHistory();
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetValues();
                //get input from bar
                EditText searchInputBar = (EditText) findViewById(R.id.searchBar);
                String inputa = searchInputBar.getText().toString();
                Spinner searchTypeSpinner = (Spinner) findViewById(R.id.searchType);
                String searchType = searchTypeSpinner.getSelectedItem().toString();
                makeInvisible();
                search(inputa, searchType);
            }
        });


        preset1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetValues();
                String inputa = getString(R.string.tag1);
                makeInvisible();
                search(inputa, "Tag");
            }
        });

        preset2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetValues();
                Log.d("myTag", "%%%%%%");
                String inputa = getString(R.string.tag2);
                makeInvisible();
                search(inputa, "Tag");
            }
        });

        preset3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetValues();
                String inputa = getString(R.string.tag3);
                makeInvisible();
                search(inputa, "Tag");
            }
        });

        preset4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetValues();
                String inputa = getString(R.string.tag4);
                makeInvisible();
                search(inputa, "Tag");
            }
        });

        //getVideos();
        getExerciseType();
        getTopVideos();
        //getLivestreams();

    }

    private void getExerciseType(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("MostRecentTable");
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        myReturnString = "0";
        ref.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!= null){
                    myReturnString = snapshot.getValue().toString();
                    Context context = getApplicationContext();
                    CharSequence text = "Welcome, based on your exercise history, we suggest you do " + myReturnString;
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    myReturnString = "0";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    //search history
    private void getSearchHistory(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("SearchHistory");
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);


        ref.orderByChild("username").equalTo(username).limitToFirst(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Map<String, Map<String, String>>> results = (Map<String, Map<String, Map<String, String>>>) snapshot.getValue();

                        if(results!=null){
                            for(Map.Entry<String, Map<String, Map<String, String>>> entry : results.entrySet()){
                                Log.e("entry key",entry.getKey());
                                Map<String, String> searchMap = entry.getValue().get("searchHistory");
                                /*for (Map.Entry<String, String> pair : searchMap.entrySet()) {
                                    Log.e("searchkey",pair.getKey());
                                    Log.e("searchval",pair.getValue());
                                    //Log.e("pair", pair);
                                    searchKeywords.add(pair.getValue());
                                }*/
                                if(searchMap != null) {
                                    Log.e("searchMap size", String.valueOf(searchMap.size()));

                                    for (int z = 1; z <= searchMap.size(); z++) {
                                        String s = "Item " + z;
                                        Log.e("s", s);
                                        searchKeywords.add(searchMap.get(s));

                                    }
                                    Log.e("array size", String.valueOf(searchKeywords.size()));
                                }
                            }

                            populateSearchHistory();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void populateSearchHistory(){
        int numOfKeywords = min(5, searchKeywords.size());
        Log.e("numOfKeywords",String.valueOf(numOfKeywords));

        for(int i=0; i < numOfKeywords; i++){
            final String word = searchKeywords.get(i);
            Log.d("word ", String.valueOf(i) + " " + word);

            searchTextViews.get(i).setVisibility(View.VISIBLE);
            searchTextViews.get(i).setText(searchKeywords.get(i));
            searchBtnTextViews.get(i).setVisibility(View.VISIBLE);
            searchBtnTextViews.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numVideos=0;
                    numLivestreams=0;
//                    numProfiles=0;
                    resetValues();

                    //get input from search history
                    EditText searchInputBar = (EditText) findViewById(R.id.searchBar);
                    searchInputBar.setText(word);

                    makeInvisible();
                    Log.d("searchkeyword1 ", word);
                    search(word, "All");
                }
            });
        }
        searchKeywords.clear();
    }

    private void search(String input, String searchType){
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("input",input);
        intent.putExtra("searchType", searchType);
        startActivity(intent);
    }

    private void getTopVideos(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Video References");
        myRef.orderByChild("numLikes").limitToLast(20-numLivestreams).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()){
                    VideoReference video = child.getValue(VideoReference.class);
                    //videoRefTitles.add(video.getReferenceTitle());
                    //Log.e("refTitle", video.getReferenceTitle());
                    //videoDispTitles.add(video.getTitle());
                    //videoUploadingUser.add(video.getUploadingUser());
                    //videoAllowComments.add(video.getCommentsAllowed());
                    videos.add(video);
                    numVideos++;
                    //Log.e("numVideos", "" + numVideos);
                }
                getLivestreams();

                //flip order
                //flipOrderVideoDispTitles();
                //flipOrderVideoRefTitles();
                //flipOrderVideoUploadingUser();
                flipOrderVideos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    /*private void getVideos(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Video References");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()){
                    VideoReference video = child.getValue(VideoReference.class);
                    if(numVideos < (20-numLivestreams)) {
                        videoRefTitles.add(video.getReferenceTitle());
                        videoDispTitles.add(video.getTitle());
                        videoUploadingUser.add(video.getUploadingUser());
                        numVideos++;
                    }
                    else{break;}
                }
                //displayResults();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
        //getLivestreams();
    }*/

    private void getLivestreams(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Livestream Details");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()){
                    LivestreamMember livestream = child.getValue(LivestreamMember.class);
                    if(numLivestreams < 20) {
                        lsRefTitles.add(livestream.getReferenceTitle());
                        lsDispTitles.add(livestream.getTitle());
                        lsZoomLinks.add(livestream.getZoomLink());

                        String uploadingUser = (String) livestream.getUploadingUser();
                        livestreamUploadingUser.add(uploadingUser);

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
        Log.e("displayResults", "numVideos="+numVideos+", numLivestreams="+numLivestreams);
        for(int i=0; i < 20; i++){
            final int j = i;
            imageButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.e("button clicked", "i="+j + ", name: " + videos.get(j).getTitle());
                    if(j < numVideos){ openNewActivityVideo(j); }
                    else{ openNewActivityLivestream(j); }
                }
            });
        }
        //display video results
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        //Log.e("NUM VIDEOS", ""+numVideos);
        for(int i=0; i < numVideos; i++) {
            //Log.e("i", ""+i);
            //Log.d("Printing Video", "Video: " + videoDispTitles.get(i));
            StorageReference imageRef = storageRef.child("/thumbnail_images/" + videos.get(i).getReferenceTitle() + ".jpg");
            try {
                final int j = i;
                final File localFile = File.createTempFile(videos.get(i).getReferenceTitle(), "jpg");
                imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        ImageButton ib = imageButtons.get(j);

                        Bitmap bm = imgToBitmap(localFile);
                        ib.setImageBitmap(bm);
                        ib.setVisibility(View.VISIBLE);
                        ImageButton bp = likes.get(j);
                        //change color
                        bp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                liked = false;
                                like(j);
                                //   bp.setBackgroundColor(getResources().getColor(R.color.black));
                                //bp.setText("Liked");
                                //bp.setTextSize(9);
                                //bp.setBackgroundColor(getResources().getColor(R.color.orange_500));
                                bp.setColorFilter(Color.argb(1, 243, 91, 4));
                            }
                        });

                        bp.setVisibility(View.VISIBLE);
                        //depending on whether the button is liked or not, change button appearance
                        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
                        String username = sharedPref.getString("username", null);
                        //Log.e("user", username);
                        Log.e("videoRefTitle", videos.get(j).getReferenceTitle());
                        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes").child(username);
                        likeRef.orderByValue().equalTo(videos.get(j).getReferenceTitle()).limitToFirst(1)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //Log.e("likedval", String.valueOf(likeRef.orderByValue().equalTo(videoRefTitles.get(j)).limitToFirst(1)));
                                        for(DataSnapshot child : snapshot.getChildren()) {
                                            Log.e("key", child.getKey());
                                            Log.e("val", (String) child.getValue());
                                            if (child.getKey() != null) {
                                                Log.e("LIKED", "LIKED");
                                                //bp.setText("Liked");
                                                //bp.setTextSize(9);
                                                //bp.setBackgroundColor(getResources().getColor(R.color.orange_500));
                                                bp.setColorFilter(Color.argb(1, 243, 91, 4));
                                            } else {
                                                Log.e("LIKE", "LIKE");
                                                //bp.setText("Like");
                                                bp.setBackgroundColor(getResources().getColor(R.color.yellow_200));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
                                });
                        bp.setVisibility(View.VISIBLE); //show like button for all videos available

                        ImageButton dl = dislikes.get(j);
                        //change color
                        dl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                disliked = false;
                                dislike(j);
                                //   bp.setBackgroundColor(getResources().getColor(R.color.black));
                                //dl.setText("Disliked");
                                //dl.setTextSize(9);
                                //dl.setBackgroundColor(getResources().getColor(R.color.orange_500));
                                dl.setColorFilter(Color.argb(1, 243, 91, 4));
                            }
                        });
                        dl.setVisibility(View.VISIBLE);
                        //depending on whether the button is disliked or not, change button appearance
                        DatabaseReference dislikeRef = FirebaseDatabase.getInstance().getReference("Dislikes").child(username);
                        dislikeRef.orderByValue().equalTo(videos.get(j).getReferenceTitle()).limitToFirst(1)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot child : snapshot.getChildren()) {
                                            Log.e("key", child.getKey());
                                            Log.e("val", (String) child.getValue());
                                            if (child.getKey() != null) {
                                                //dl.setText("Disliked");
                                                //dl.setTextSize(9);
                                                //dl.setBackgroundColor(getResources().getColor(R.color.orange_500));
                                                dl.setColorFilter(Color.argb(1, 243, 91, 4));
                                            } else {
                                                //dl.setText("Dislike");
                                                dl.setBackgroundColor(getResources().getColor(R.color.yellow_200));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
                                });
                        dl.setVisibility(View.VISIBLE); //show dislike button for all videos available



                        //show num likes
                        TextView numLikesTV = numLikes.get(j);
                        Log.e("videos j=" + j, "" + videos.get(j).getTitle());
                        Log.e("videos j=" + j, "" + videos.get(j).getNumLikes());
                        numLikesTV.setText(""+videos.get(j).getNumLikes());
                        numLikesTV.setVisibility(View.VISIBLE);

                        //show num dislikes
                        TextView numDislikesTV = numDislikes.get(j);
                        numDislikesTV.setText(""+videos.get(j).getNumDislikes());
                        numDislikesTV.setVisibility(View.VISIBLE);


                        //String ProfilerefTitle = videoUploadingUserPic.get(j);
                        //Log.d("ProfilerefTitle", ProfilerefTitle);
                        //StorageReference imageRefProf = storageRef.child("/profilepictures/" + ProfilerefTitle);

                        //ImageButton ibp = imageButtonsProfile.get(j);
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
                        //ibp.setVisibility(View.VISIBLE);

                        TextView tvp = textViewsProfile.get(j);
                        tvp.setText(videos.get(j).getUploadingUser());

                        tvp.setVisibility(View.VISIBLE);

                        TextView tv = textViews.get(j);
                        tv.setText(videos.get(j).getTitle());
                        tv.setVisibility(View.VISIBLE);

                        //display view comments button
                        Button vc = viewCommentButtons.get(j);
                        vc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                redirectToComments(j);
                            }
                        });
                        vc.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
            }
        }

        //display livestreams
        for(int i=numVideos; i < numVideos+numLivestreams; i++) {
            StorageReference lsimageRef = storageRef.child("/livestream_thumbnail_images/" + lsRefTitles.get(i - numVideos));
            try {
                final int j = i;
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

                            //ImageButton ibp = imageButtonsProfile.get(j);
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
                            //ibp.setVisibility(View.VISIBLE);

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

        /*imageButtonsProfile.add(findViewById(R.id.item1user));
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
        imageButtonsProfile.add(findViewById(R.id.item20user));*/

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

        dislikes.add(findViewById(R.id.item1dislike));
        dislikes.add(findViewById(R.id.item2dislike));
        dislikes.add(findViewById(R.id.item3dislike));
        dislikes.add(findViewById(R.id.item4dislike));
        dislikes.add(findViewById(R.id.item5dislike));
        dislikes.add(findViewById(R.id.item6dislike));
        dislikes.add(findViewById(R.id.item7dislike));
        dislikes.add(findViewById(R.id.item8dislike));
        dislikes.add(findViewById(R.id.item9dislike));
        dislikes.add(findViewById(R.id.item10dislike));
        dislikes.add(findViewById(R.id.item11dislike));
        dislikes.add(findViewById(R.id.item12dislike));
        dislikes.add(findViewById(R.id.item13dislike));
        dislikes.add(findViewById(R.id.item14dislike));
        dislikes.add(findViewById(R.id.item15dislike));
        dislikes.add(findViewById(R.id.item16dislike));
        dislikes.add(findViewById(R.id.item17dislike));
        dislikes.add(findViewById(R.id.item18dislike));
        dislikes.add(findViewById(R.id.item19dislike));
        dislikes.add(findViewById(R.id.item20dislike));

        viewCommentButtons.add(findViewById(R.id.item1ViewComments));
        viewCommentButtons.add(findViewById(R.id.item2ViewComments));
        viewCommentButtons.add(findViewById(R.id.item3ViewComments));
        viewCommentButtons.add(findViewById(R.id.item4ViewComments));
        viewCommentButtons.add(findViewById(R.id.item5ViewComments));
        viewCommentButtons.add(findViewById(R.id.item6ViewComments));
        viewCommentButtons.add(findViewById(R.id.item7ViewComments));
        viewCommentButtons.add(findViewById(R.id.item8ViewComments));
        viewCommentButtons.add(findViewById(R.id.item9ViewComments));
        viewCommentButtons.add(findViewById(R.id.item10ViewComments));
        viewCommentButtons.add(findViewById(R.id.item11ViewComments));
        viewCommentButtons.add(findViewById(R.id.item12ViewComments));
        viewCommentButtons.add(findViewById(R.id.item13ViewComments));
        viewCommentButtons.add(findViewById(R.id.item14ViewComments));
        viewCommentButtons.add(findViewById(R.id.item15ViewComments));
        viewCommentButtons.add(findViewById(R.id.item16ViewComments));
        viewCommentButtons.add(findViewById(R.id.item17ViewComments));
        viewCommentButtons.add(findViewById(R.id.item18ViewComments));
        viewCommentButtons.add(findViewById(R.id.item19ViewComments));
        viewCommentButtons.add(findViewById(R.id.item20ViewComments));


        numLikes.add(findViewById(R.id.numLikes1));
        numLikes.add(findViewById(R.id.numLikes2));
        numLikes.add(findViewById(R.id.numLikes3));
        numLikes.add(findViewById(R.id.numLikes4));
        numLikes.add(findViewById(R.id.numLikes5));
        numLikes.add(findViewById(R.id.numLikes6));
        numLikes.add(findViewById(R.id.numLikes7));
        numLikes.add(findViewById(R.id.numLikes8));
        numLikes.add(findViewById(R.id.numLikes9));
        numLikes.add(findViewById(R.id.numLikes10));
        numLikes.add(findViewById(R.id.numLikes11));
        numLikes.add(findViewById(R.id.numLikes12));
        numLikes.add(findViewById(R.id.numLikes13));
        numLikes.add(findViewById(R.id.numLikes14));
        numLikes.add(findViewById(R.id.numLikes15));
        numLikes.add(findViewById(R.id.numLikes16));
        numLikes.add(findViewById(R.id.numLikes17));
        numLikes.add(findViewById(R.id.numLikes18));
        numLikes.add(findViewById(R.id.numLikes19));
        numLikes.add(findViewById(R.id.numLikes20));


        numDislikes.add(findViewById(R.id.numDislikes1));
        numDislikes.add(findViewById(R.id.numDislikes2));
        numDislikes.add(findViewById(R.id.numDislikes3));
        numDislikes.add(findViewById(R.id.numDislikes4));
        numDislikes.add(findViewById(R.id.numDislikes5));
        numDislikes.add(findViewById(R.id.numDislikes6));
        numDislikes.add(findViewById(R.id.numDislikes7));
        numDislikes.add(findViewById(R.id.numDislikes8));
        numDislikes.add(findViewById(R.id.numDislikes9));
        numDislikes.add(findViewById(R.id.numDislikes10));
        numDislikes.add(findViewById(R.id.numDislikes11));
        numDislikes.add(findViewById(R.id.numDislikes12));
        numDislikes.add(findViewById(R.id.numDislikes13));
        numDislikes.add(findViewById(R.id.numDislikes14));
        numDislikes.add(findViewById(R.id.numDislikes15));
        numDislikes.add(findViewById(R.id.numDislikes16));
        numDislikes.add(findViewById(R.id.numDislikes17));
        numDislikes.add(findViewById(R.id.numDislikes18));
        numDislikes.add(findViewById(R.id.numDislikes19));
        numDislikes.add(findViewById(R.id.numDislikes20));


    }

    private void makeInvisible(){
        for(TextView tv : searchTextViews){
            tv.setVisibility(View.GONE);
        }
        for(Button bt : searchBtnTextViews){
            bt.setVisibility(View.GONE);
        }
        for(ImageButton ib : imageButtons){
            ib.setVisibility(View.GONE);
        }
        for(TextView tv : textViews){
            tv.setVisibility(View.GONE);
        }
        /*for(ImageButton ib : imageButtonsProfile){
            ib.setVisibility(View.GONE);
        }*/
        for(TextView tv : textViewsProfile){
            tv.setVisibility(View.GONE);
        }
        for(ImageButton l : likes){
            l.setVisibility(View.GONE);
        }
        for(ImageButton dl : dislikes){
            dl.setVisibility(View.GONE);
        }
        for(Button vc : viewCommentButtons){
            vc.setVisibility(View.GONE);
        }
        for(TextView nl : numLikes){
            nl.setVisibility(View.GONE);
        }
        for(TextView ndl : numDislikes){
            ndl.setVisibility(View.GONE);
        }
    }

    public void openNewActivityVideo(int i){
        //Log.e("passing to VideoAct", "title: " + videoRefTitles.get(i));
        String referenceTitle = videos.get(i).getReferenceTitle();
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("referenceTitle",referenceTitle);
        startActivity(intent);
    }
    public void openNewActivityLivestream(int i){
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

    /*private void getVideoUploadingUserPic(String username){
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

    }*/

    private void resetValues(){
        numVideos=0;
        numLivestreams=0;

        //videoRefTitles = new ArrayList<String>();
        //videoDispTitles = new ArrayList<String>();
        lsRefTitles = new ArrayList<String>();
        lsDispTitles = new ArrayList<String>();
        lsZoomLinks = new ArrayList<String>();

        //videoUploadingUser = new ArrayList<String>();
        livestreamUploadingUser = new ArrayList<String>();
        //videoUploadingUserPic = new ArrayList<String>();
        livestreamUplodingUserPic = new ArrayList<String>();

    }

    private void sortVideosByNumLikes(){

    }

    private void like(int j){
        String refTitle = videos.get(j).getReferenceTitle();
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        Log.e("clicked-video", refTitle);
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes").child(username);
        readDataLike(new MyCallback() {
            @Override
            public void onCallback(Boolean liked) {
                if(liked == true) {
                    Log.e("alreadyliked", "already liked - do nothing");
                }
                else if(liked == false){
                    Log.e("likedyes", "liked successful");
                    likeRef.push().setValue(refTitle);
                    //update NumLikes
                    //find video according to refTitle
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Video References");
                    userRef.orderByChild("referenceTitle").equalTo(refTitle).limitToFirst(1)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot child : snapshot.getChildren()){
                                        VideoReference video = child.getValue(VideoReference.class);
                                        int numLikesTemp = video.getNumLikes();
                                        numLikesTemp++;
                                        String keyTemp = child.getKey();
                                        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Video References").child(child.getKey()).child("numLikes");
                                        //ref.setValue(numLikes);

                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Video References").child(keyTemp).child("numLikes");
                                        ref.setValue(numLikesTemp);
                                        numLikes.get(j).setText("" + numLikesTemp);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { }
                            });
                }
            }
        }, refTitle);




    }

    public interface MyCallback {
        void onCallback(Boolean liked);
    }

    public void readDataLike(MyCallback myCallback, String refTitle) {
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes").child(username);
        likeRef.orderByValue().equalTo(refTitle).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                if (child.getKey() != null) {
                                    //if liked, don't add it
                                    Log.e("key", child.getKey());
                                    Log.e("val", (String) child.getValue());
                                    liked = true;
                                    Log.e("liked?", String.valueOf(liked));
                                    myCallback.onCallback(liked);

                                }
                            }
                        }
                        else {
                            liked = false;
                            myCallback.onCallback(liked);
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }
    public interface MyCallback2 {
        void onCallback(Boolean disliked);
    }

    private void dislike(int j){
        String refTitle = videos.get(j).getReferenceTitle();
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        Log.e("clicked-video", refTitle);
        DatabaseReference dislikeRef = FirebaseDatabase.getInstance().getReference("Dislikes").child(username);
        readDataDislike(new MyCallback2() {
            @Override
            public void onCallback(Boolean disliked) {
                if(disliked == true) {
                    Log.e("alreadydisliked", "already disliked - do nothing");
                }
                else if(disliked == false){
                    Log.e("dislikedyes", "disliked successful");
                    dislikeRef.push().setValue(refTitle);
                    //update NumLikes
                    //find video according to refTitle
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Video References");
                    userRef.orderByChild("referenceTitle").equalTo(refTitle).limitToFirst(1)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot child : snapshot.getChildren()){
                                        VideoReference video = child.getValue(VideoReference.class);
                                        int numDislikesTemp = video.getNumDislikes();
                                        numDislikesTemp++;
                                        String keyTemp = child.getKey();
                                        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Video References").child(child.getKey()).child("numLikes");
                                        //ref.setValue(numLikes);

                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Video References").child(keyTemp).child("numDislikes");
                                        ref.setValue(numDislikesTemp);
                                        numDislikes.get(j).setText("" + numDislikesTemp);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { }
                            });
                }
            }
        }, refTitle);



    }
    public void readDataDislike(MyCallback2 myCallback2, String refTitle) {
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        DatabaseReference dislikeRef = FirebaseDatabase.getInstance().getReference("Dislikes").child(username);
        dislikeRef.orderByValue().equalTo(refTitle).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                if (child.getKey() != null) {
                                    //if liked, don't add it
                                    Log.e("key", child.getKey());
                                    Log.e("val", (String) child.getValue());
                                    disliked = true;
                                    Log.e("liked?", String.valueOf(disliked));
                                    myCallback2.onCallback(disliked);

                                }
                            }
                        }
                        else {
                            disliked = false;
                            myCallback2.onCallback(disliked);
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
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

    /*private void flipOrderVideoRefTitles(){
        ArrayList<String> tempRefTitles = new ArrayList<String>();
        for(String entry : videoRefTitles){
            tempRefTitles.add(entry);
        }

        videoRefTitles.clear();
        for(int i=tempRefTitles.size()-1; i >=0; i--){
            videoRefTitles.add(tempRefTitles.get(i));
        }
    }

    private void flipOrderVideoDispTitles(){
        ArrayList<String> tempRefTitles = new ArrayList<String>();
        for(String entry : videoDispTitles){
            tempRefTitles.add(entry);
        }

        videoDispTitles.clear();
        for(int i=tempRefTitles.size()-1; i >=0; i--){
            videoDispTitles.add(tempRefTitles.get(i));
        }
    }

    private void flipOrderVideoUploadingUser(){
        ArrayList<String> tempRefTitles = new ArrayList<String>();
        for(String entry : videoUploadingUser){
            tempRefTitles.add(entry);
        }

        videoUploadingUser.clear();
        for(int i=tempRefTitles.size()-1; i >=0; i--){
            videoUploadingUser.add(tempRefTitles.get(i));
        }
    }*/

    private void flipOrderVideos(){
        ArrayList<VideoReference> tempVideos = new ArrayList<VideoReference>();
        for(VideoReference entry : videos){
            tempVideos.add(entry);
        }

        videos.clear();
        for(int i=tempVideos.size()-1; i>=0; i--){
            videos.add(tempVideos.get(i));
        }
    }

    private void redirectToComments(int i){
        String refTitle = videos.get(i).getReferenceTitle();
        //if comments are allowed
        if(videos.get(i).getCommentsAllowed()){
            //get video from refTitle
            DatabaseReference videosRef = FirebaseDatabase.getInstance().getReference("Video References");
            videosRef.orderByChild("referenceTitle").equalTo(refTitle)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String key = "";
                            for(DataSnapshot child : snapshot.getChildren()){
                                VideoReference video = child.getValue(VideoReference.class);
                                key = child.getKey();
                            }

                            //send key to activity
                            Intent intent = new Intent(getApplicationContext(), ViewCommentsActivity.class);
                            intent.putExtra("key", key);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
        }
        else{
            Toast.makeText(MainActivity.this, "Sorry. Comments are disabled for this video.", Toast.LENGTH_LONG).show();
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
