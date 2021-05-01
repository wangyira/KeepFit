package com.example.keepfit.calories;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.example.keepfit.MainActivity;
import com.example.keepfit.StartLivestreamActivity;
import com.example.keepfit.VideoActivity;
import com.example.keepfit.VideoUploadActivity;
import com.example.keepfit.authapp.ProfileActivityEdits;
import com.example.keepfit.authapp.ViewVideos;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.keepfit.R;

import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class CalorieActivity extends AppCompatActivity {

    TextView myTitle;

    Spinner spinner1;

    Spinner spinner2;

    TextView timerTextView; //myTimer

    Button startstop;
    Button pausestart;
    Button submit1;
    Button submit2;
    EditText editTextTime;

    String myCurrentValue;

    TextView TotalCalories;
    Button ClearCalories;

    Button ViewHistory;
    Button DeleteHistory;

    double myTotalCalories;

    ArrayList<String> exerciseTitleList = new ArrayList<String>();
    ArrayList<String> myCaloriesBurnedList = new ArrayList<String>();
    ArrayList<String> myTimeList = new ArrayList<String>();
    ArrayList<TextView> textViews = new ArrayList<TextView>();

    int numExerciseEntries;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    private Calendar calendar = Calendar.getInstance(Locale.getDefault());

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    /*
    DatabaseReference mConditionRef = mRootRef.child("METValues");

    DatabaseReference mConditionRef1 = mConditionRef.child("Aerobic");
    DatabaseReference mConditionRef2 = mConditionRef.child("Anaerobic");
    DatabaseReference mConditionRef3 = mConditionRef.child("Flexibility");
    DatabaseReference mConditionRef4 = mConditionRef.child("Stability");
    */

    DatabaseReference mConditionRef = mRootRef.child("CaloriesTable");

    long startTime = 0;
    long timeInPause = 0;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerMyHandler = new Handler();
    Runnable timerMyRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("%d:%02d", minutes, seconds));

            timerMyHandler.postDelayed(this, 500);
        }
    };

    public class METValue {

        public String username;
        public String myCaloriesBurned;
        public String myTime;
        public String exerciseTitle;
        public String shouldView;

        public METValue() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public METValue(String username, String myCaloriesBurned, String myTime, String exerciseTitle, String shouldView) {
            this.username = username;
            this.myCaloriesBurned = myCaloriesBurned;
            this.myTime = myTime;
            this.exerciseTitle = exerciseTitle;
            this.shouldView = shouldView;

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie);

//        dl = (DrawerLayout)findViewById(R.id.drawer);
//        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);
//
//        dl.addDrawerListener(t);
//        t.syncState();
//
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        nv = (NavigationView)findViewById(R.id.nv);
//        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                int id = item.getItemId();
//                Intent i = new Intent(CalorieActivity.this, ViewVideos.class);
//                switch(id)
//                {
//                    case R.id.account:
//                        startActivity(new Intent(getApplicationContext(), ProfileActivityEdits.class));
//                        break;
//                    case R.id.navviewliked:
//                        i.putExtra("type", "liked");
//                        startActivity(i);
//                        break;
//                    case R.id.navviewdisliked:
//                        i.putExtra("type", "disliked");
//                        startActivity(i);
//                        break;
//                    case R.id.navviewwatched:
//                        i.putExtra("type", "watched");
//                        startActivity(i);
//                        break;
//                    case R.id.navviewuploaded:
//                        i.putExtra("type", "uploaded");
//                        startActivity(i);
//                        break;
//                    default:
//                        return true;
//                }
//                return true;
//
//            }
//        });

        numExerciseEntries = 0;

        //navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set calorie selected
        bottomNavigationView.setSelectedItemId(R.id.nav_calorie);

        //perform itemselectedlistener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                switch (menuItem.getItemId()){
                    case R.id.nav_search:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_calorie:
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

        myTotalCalories = 0;

        timerTextView = (TextView) findViewById(R.id.myTimer);

        startstop = (Button) findViewById(R.id.startstop);
        startstop.setText("start");
        pausestart = (Button) findViewById(R.id.pausestart);
        pausestart.setText("unpause");
        startstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().equals("stop")) {
                    timerMyHandler.removeCallbacks(timerMyRunnable);
                    timeInPause = System.currentTimeMillis() - startTime;
                    b.setText("start");
                    pausestart.setText("unpause");
                } else {
                    startTime = System.currentTimeMillis();
                    timerMyHandler.postDelayed(timerMyRunnable, 0);
                    pausestart.setText("pause");
                    b.setText("stop");
                }
            }
        });
        pausestart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().equals("pause")) { //Start again
                    timerMyHandler.removeCallbacks(timerMyRunnable);
                    timeInPause = System.currentTimeMillis() - startTime;
                    b.setText("unpause");
                    startstop.setText("start");
                } else { //Stop the timer
                    startTime = System.currentTimeMillis() - timeInPause;
                    timerMyHandler.postDelayed(timerMyRunnable, 0);
                    b.setText("pause");
                    startstop.setText("stop");
                }
            }
        });
        submit1 = (Button) findViewById(R.id.submit1);
        submit1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Button b = (Button) v;

                Spinner myFirstSpinner = (Spinner)findViewById((R.id.spinner1));
                Spinner mySecondSpinner = (Spinner)findViewById((R.id.spinner2));

                String myFirstValue = myFirstSpinner.getSelectedItem().toString();
                String mySecondValue = mySecondSpinner.getSelectedItem().toString();

                DatabaseReference myMET = mRootRef.child("METValues").child(myFirstValue).child(mySecondValue);

                Log.d("myTag", myFirstValue);
                Log.d("myTag", mySecondValue);

                myMET.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myCurrentValue = snapshot.getValue().toString();
                        if (myCurrentValue == null){
                            Toast.makeText(CalorieActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("myTag", myCurrentValue);

                        double MET_Exercise = Double.parseDouble(myCurrentValue);

                        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
                        String username = sharedPref.getString("username", null);

                        String myTempWeight = sharedPref.getString("weight", null);

                        double weight = Double.parseDouble(myTempWeight); //lb



                        long millis = timeInPause;
                        int seconds = (int) (millis / 1000);
                        int minutes = seconds / 60;

                        double doubleSeconds = (double) seconds;

                        double time = doubleSeconds / 3600.0;

                        //////////////

                        weight = weight/2.205; //Weight in kg.

                        double hours = doubleSeconds/3600.0; //Time in hours

                        double NewMETValue = MET_Exercise * weight * hours;

                        String myNewMETValue = Double.toString((NewMETValue));
                        String mydoubleSeconds = Double.toString(doubleSeconds);

                        METValue myMETValue = new METValue(username, myNewMETValue, mydoubleSeconds, mySecondValue, "Y");

                        mConditionRef.push().setValue(myMETValue);

                        modifyMostRecent(mySecondValue);

                        Random rand = new Random();
                        TimeZone tz = TimeZone.getDefault();
                        Date currentDate = Calendar.getInstance(tz).getTime();
                        long currentTime = currentDate.getTime();
                        currentTime = currentTime - (1000 * 60 * 60 * 7);
                        long minute = (currentTime / (1000 * 60)) % 60;
                        long hour = (currentTime / (1000 * 60 * 60)) % 24;
                        String AMPM = "AM";
                        if (hour == 0){
                            hour = 12;
                        }
                        else if (hour == 12){
                            AMPM = "PM";
                        }
                        else if (hour > 12) {
                            AMPM = "PM";
                            hour = hour - 12;
                        }
                        String eventName = "";
                        if (minute < 10){
                            eventName = mySecondValue + " at " + hour + ":0" + minute + " " + AMPM;
                        }
                        else{
                            eventName = mySecondValue + " at " + hour + ":" + minute + " " + AMPM;
                        }
                        //String eventName = mySecondValue + " at " + hour + ":" + minute + " " + AMPM;
                        Event e = new Event(Color.argb(rand.nextInt(), rand.nextInt(), rand.nextInt(), rand.nextInt()), currentTime, eventName);

                        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events").child(username);
                        eventRef.push().setValue(e);


                        TotalCalories = (TextView)findViewById((R.id.TotalCalories));
                        myTotalCalories = myTotalCalories + NewMETValue;
                        BigDecimal bd = new BigDecimal(Double.toString(myTotalCalories));
                        myTotalCalories = bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
                        String myDisplayValue = Double.toString(myTotalCalories);
                        TotalCalories.setText(myDisplayValue + " Total Calories Burned!");

                        Context context = getApplicationContext();
                        CharSequence text = "Exercise Recorded!";
                        int duration = Toast.LENGTH_SHORT;
                        startTime=0;
                        timerTextView.setText(String.format("%d:%02d", 0, 0));
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        });
        editTextTime = (EditText) findViewById(R.id.editTextTime);
        submit2 = (Button) findViewById((R.id.submit2));
        submit2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Button b = (Button) v;

                Spinner myFirstSpinner = (Spinner)findViewById((R.id.spinner1));
                Spinner mySecondSpinner = (Spinner)findViewById((R.id.spinner2));

                String myFirstValue = myFirstSpinner.getSelectedItem().toString();
                String mySecondValue = mySecondSpinner.getSelectedItem().toString();

                DatabaseReference myMET = mRootRef.child("METValues").child(myFirstValue).child(mySecondValue);


                myMET.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myCurrentValue = snapshot.getValue().toString();

                        double MET_Exercise = Double.parseDouble(myCurrentValue);

                        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
                        String username = sharedPref.getString("username", null);

                        String myTempWeight = sharedPref.getString("weight", null);

                        double weight = Double.parseDouble(myTempWeight); //lb

                        String myString = editTextTime.getText().toString();

                        String[] myParsedString = myString.split(":");

                        int myMinutes = (Integer.parseInt(myParsedString[0]))*60;
                        int mySeconds = (Integer.parseInt(myParsedString[1]));
                        double doubleSeconds = (double) myMinutes + (double) mySeconds;

                        ////////////////////////////////////////////////

                        weight = weight/2.205; //Weight in kg.

                        double hours = doubleSeconds/3600.0; //Time in hours

                        double NewMETValue = MET_Exercise * weight * hours;

                        String myNewMETValue = Double.toString((NewMETValue));
                        String mydoubleSeconds = Double.toString(doubleSeconds);

                        METValue myMETValue = new METValue(username, myNewMETValue, mydoubleSeconds, mySecondValue, "Y");

                        mConditionRef.push().setValue(myMETValue);

                        modifyMostRecent(mySecondValue);

                        Random rand = new Random();
                        TimeZone tz = TimeZone.getDefault();
                        Date currentDate = Calendar.getInstance(tz).getTime();
                        long currentTime = currentDate.getTime();
                        currentTime = currentTime - (1000 * 60 * 60 * 7);
                        long minute = (currentTime / (1000 * 60)) % 60;
                        long hour = (currentTime / (1000 * 60 * 60)) % 24;
                        String AMPM = "AM";
                        if (hour == 0){
                            hour = 12;
                        }
                        else if (hour == 12){
                            AMPM = "PM";
                        }
                        else if (hour > 12) {
                            AMPM = "PM";
                            hour = hour - 12;
                        }
                        String eventName = "";
                        if (minute < 10){
                            eventName = mySecondValue + " at " + hour + ":0" + minute + " " + AMPM;
                        }
                        else{
                            eventName = mySecondValue + " at " + hour + ":" + minute + " " + AMPM;
                        }
                        //String eventName = mySecondValue + " at " + hour + ":" + minute + " " + AMPM;
                        Event e = new Event(Color.argb(rand.nextInt(), rand.nextInt(), rand.nextInt(), rand.nextInt()), currentTime, eventName);

                        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events").child(username);
                        eventRef.push().setValue(e);

                        TotalCalories = (TextView)findViewById((R.id.TotalCalories));
                        myTotalCalories = myTotalCalories + NewMETValue;
                        BigDecimal bd = new BigDecimal(Double.toString(myTotalCalories));
                        myTotalCalories = bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
                        String myDisplayValue = Double.toString(myTotalCalories);
                        TotalCalories.setText(myDisplayValue);

                        Context context = getApplicationContext();
                        CharSequence text = "Exercise Recorded!";
                        startTime=0;
                        timerTextView.setText(String.format("%d:%02d", 0, 0));
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        ClearCalories = (Button) findViewById((R.id.ClearCalories));
        ClearCalories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;

                myTotalCalories = 0.0;

                TotalCalories = (TextView)findViewById((R.id.TotalCalories));
                TotalCalories.setText("0");

            }
        });


        ViewHistory = findViewById((R.id.viewhistory));

        ViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetViewHistory();
                getExerciseHistory();
            }
        });

        DeleteHistory = findViewById((R.id.deletehistory));

        DeleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteHistory();
            }
        });

        addItemstoArray();
        makeInvisible();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    private void resetViewHistory(){
        numExerciseEntries = 0;
        textViews.clear();
        exerciseTitleList.clear();
        myCaloriesBurnedList.clear();
        myTimeList.clear();
        addItemstoArray();
        makeInvisible();
    }

    private void modifyMostRecent(String myTitle){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("MostRecentTable");
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        ref.child(username).setValue(myTitle);
        /*
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("CaloriesTable");
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        ref.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //resetViewHistory();
                        Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                        if(results!=null){
                            for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                                Log.d("result", "Hello!");
                                entry.getValue().replace("shouldView", "Y", "N");


                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

        */
    }

    //Get Exercise History
    private void getExerciseHistory(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("CaloriesTable");
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        ref.orderByChild("username").equalTo(username).limitToFirst(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //resetViewHistory();
                        Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                        if(results!=null){
                            for(Map.Entry<String, Map<String, String>> entry : results.entrySet()){
                                exerciseTitleList.add(entry.getValue().get("exerciseTitle"));
                                myCaloriesBurnedList.add(entry.getValue().get("myCaloriesBurned"));
                                myTimeList.add(entry.getValue().get("myTime"));
                                numExerciseEntries++;
                            }
                        }
                        if (numExerciseEntries > 10){
                            numExerciseEntries = 10;
                        }
                        displayExerciseHistory();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    //Put On Display Exercise History
    private void displayExerciseHistory(){
        for(int i=0; i < numExerciseEntries*3; i = i + 3) {

            int j = i / 3;

            TextView tv = textViews.get(i);

            tv.setText(exerciseTitleList.get(j));
            tv.setVisibility(View.VISIBLE);

            tv = textViews.get(i+1);

            CharSequence cs1 = myCaloriesBurnedList.get(j);
            tv.setText(cs1);
            tv.setVisibility(View.VISIBLE);

            tv = textViews.get(i+2);

            CharSequence cs2 = myTimeList.get(j);
            tv.setText(cs2);
            tv.setVisibility(View.VISIBLE);

        }
    }

    public void deleteHistory(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("CaloriesTable");
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        ref.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    snapshot1.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void addItemstoArray(){

        textViews.add(findViewById(R.id.hist1txt));
        textViews.add(findViewById(R.id.hist2txt));
        textViews.add(findViewById(R.id.hist3txt));
        textViews.add(findViewById(R.id.hist4txt));
        textViews.add(findViewById(R.id.hist5txt));
        textViews.add(findViewById(R.id.hist6txt));
        textViews.add(findViewById(R.id.hist7txt));
        textViews.add(findViewById(R.id.hist8txt));
        textViews.add(findViewById(R.id.hist9txt));
        textViews.add(findViewById(R.id.hist10txt));
        textViews.add(findViewById(R.id.hist11txt));
        textViews.add(findViewById(R.id.hist12txt));
        textViews.add(findViewById(R.id.hist13txt));
        textViews.add(findViewById(R.id.hist14txt));
        textViews.add(findViewById(R.id.hist15txt));
        textViews.add(findViewById(R.id.hist16txt));
        textViews.add(findViewById(R.id.hist17txt));
        textViews.add(findViewById(R.id.hist18txt));
        textViews.add(findViewById(R.id.hist19txt));
        textViews.add(findViewById(R.id.hist20txt));
        textViews.add(findViewById(R.id.hist21txt));
        textViews.add(findViewById(R.id.hist22txt));
        textViews.add(findViewById(R.id.hist23txt));
        textViews.add(findViewById(R.id.hist24txt));
        textViews.add(findViewById(R.id.hist25txt));
        textViews.add(findViewById(R.id.hist26txt));
        textViews.add(findViewById(R.id.hist27txt));
        textViews.add(findViewById(R.id.hist28txt));
        textViews.add(findViewById(R.id.hist29txt));
        textViews.add(findViewById(R.id.hist30txt));


    }

    private void makeInvisible(){
        for(TextView tv : textViews){
            tv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        timerMyHandler.removeCallbacks(timerMyRunnable);
        Button b = (Button)findViewById(R.id.startstop);
        b.setText("start");
    }




    @Override
    protected void onStart(){
        super.onStart();

        spinner1 = (Spinner)findViewById((R.id.spinner1));
        spinner2 = (Spinner)findViewById((R.id.spinner2));

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                // Get Selected Class name from the list
                String myValue = parent.getItemAtPosition(position).toString();
                switch (myValue)
                {
                    case "Aerobic":
                        // assigning div item list defined in XML to the div Spinner
                        spinner2.setAdapter(new ArrayAdapter<String>(CalorieActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.tagEntries2)));
                        break;

                    case "Anaerobic":
                        spinner2.setAdapter(new ArrayAdapter<String>(CalorieActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.tagEntries3)));
                        break;

                    case "Flexibility":
                        spinner2.setAdapter(new ArrayAdapter<String>(CalorieActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.tagEntries4)));
                        break;

                    case "Stability":
                        spinner2.setAdapter(new ArrayAdapter<String>(CalorieActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.tagEntries5)));
                        break;
                }

                //set divSpinner Visibility to Visible
                spinner2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // can leave this empty
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                // do something upon option selection
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // can leave this empty
            }

        });





    }




}