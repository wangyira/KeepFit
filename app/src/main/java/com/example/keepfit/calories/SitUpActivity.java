package com.example.keepfit.calories;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keepfit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class SitUpActivity extends AppCompatActivity {

    Button Back;

    TextView timerTextView2; //myTimer
    TextView SitUpInstructions;

    Button startstop2;
    Button pausestart2;

    Button SubmitSitUp;

    long startTime = 0;
    long timeInPause = 0;

    Double myTotalCalories;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef = mRootRef.child("CaloriesTable");

    private Calendar calendar = Calendar.getInstance(Locale.getDefault());

    public class METValue2 {

        public String username;
        public String myCaloriesBurned;
        public String myTime;
        public String exerciseTitle;
        public String shouldView;

        public METValue2() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public METValue2(String username, String myCaloriesBurned, String myTime, String exerciseTitle, String shouldView) {
            this.username = username;
            this.myCaloriesBurned = myCaloriesBurned;
            this.myTime = myTime;
            this.exerciseTitle = exerciseTitle;
            this.shouldView = shouldView;

        }

    }

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerMyHandler = new Handler();
    Runnable timerMyRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView2.setText(String.format("%d:%02d", minutes, seconds));
            timerMyHandler.postDelayed(this, 500);

            if (seconds < 5 ){
                SitUpInstructions.setText("Sit up. Avoid lifting your legs.");
            }
            else if (seconds < 10){
                SitUpInstructions.setText("Lay back down to the ground. Do it with control.");
            }
            else if (seconds < 15){
                SitUpInstructions.setText("Sit back up again. No gain without pain!");
            }
            else if (seconds < 20){
                SitUpInstructions.setText("Lay back down to the ground.");
            }
            else if (seconds < 25){
                SitUpInstructions.setText("Sit back up again. You're almost done!");
            }
            else if (seconds < 30){
                SitUpInstructions.setText("Lay back down.");
            }
            else{
                SitUpInstructions.setText("You're done!");
                timerMyHandler.removeCallbacks(timerMyRunnable);
                startstop2.setText("start");
                pausestart2.setText("unpause");
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_sit_up);

        Back = (Button) findViewById(R.id.BackButton);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), CalorieActivity.class);
                intent.putExtra("TotalCalories", myTotalCalories);
                startActivity(intent);
                overridePendingTransition(0,0);

            }
        });

        timerTextView2 = (TextView) findViewById(R.id.myTimer2);
        SitUpInstructions = (TextView) findViewById(R.id.SitUpInstructions);

        startstop2 = (Button) findViewById(R.id.startstop2);
        startstop2.setText("start");
        pausestart2 = (Button) findViewById(R.id.pausestart2);
        pausestart2.setText("unpause");
        startstop2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().equals("stop")) {
                    timerMyHandler.removeCallbacks(timerMyRunnable);
                    timeInPause = System.currentTimeMillis() - startTime;
                    b.setText("start");
                    pausestart2.setText("unpause");
                } else {
                    startTime = System.currentTimeMillis();
                    timerMyHandler.postDelayed(timerMyRunnable, 0);
                    pausestart2.setText("pause");
                    b.setText("stop");
                }
            }
        });
        pausestart2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().equals("pause")) { //Start again
                    timerMyHandler.removeCallbacks(timerMyRunnable);
                    timeInPause = System.currentTimeMillis() - startTime;
                    b.setText("unpause");
                    startstop2.setText("start");
                } else { //Stop the timer
                    startTime = System.currentTimeMillis() - timeInPause;
                    timerMyHandler.postDelayed(timerMyRunnable, 0);
                    b.setText("pause");
                    startstop2.setText("stop");
                }
            }
        });


        SubmitSitUp = (Button) findViewById(R.id.SubmitSitUp);
        SubmitSitUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Button b = (Button) v;


                SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
                String username = sharedPref.getString("username", null);

                String myTempWeight = sharedPref.getString("weight", null);

                double weight = Double.parseDouble(myTempWeight); //lb

                //long millis = timeInPause;
                int seconds = 30;
                int minutes = seconds / 60;

                double doubleSeconds = (double) seconds;

                double time = doubleSeconds / 3600.0;

                weight = weight/2.205; //Weight in kg.

                double hours = doubleSeconds/3600.0; //Time in hours

                double NewMETValue = 3.8 * weight * hours;

                String myNewMETValue = Double.toString((NewMETValue));
                String mydoubleSeconds = Double.toString(doubleSeconds);

                METValue2 myMETValue = new METValue2(username, myNewMETValue, mydoubleSeconds, "Sit-Up", "Y");

                mConditionRef.push().setValue(myMETValue);

                Bundle myBundle = getIntent().getExtras();
                myTotalCalories = myBundle.getDouble("TotalCalories") + NewMETValue;


                modifyMostRecent2("Sit-Up");

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
                    eventName = "Sit-Up" + " at " + hour + ":0" + minute + " " + AMPM;
                }
                else{
                    eventName = "Sit-Up" + " at " + hour + ":" + minute + " " + AMPM;
                }
                //String eventName = mySecondValue + " at " + hour + ":" + minute + " " + AMPM;
                Event e = new Event(Color.argb(rand.nextInt(), rand.nextInt(), rand.nextInt(), rand.nextInt()), currentTime, eventName);

                DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events").child(username);
                eventRef.push().setValue(e);


                Context context = getApplicationContext();
                CharSequence text = "Exercise Recorded!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });

    }

    private void modifyMostRecent2(String myTitle){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("MostRecentTable");
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        ref.child(username).setValue(myTitle);

    }

    @Override
    public void onPause() {
        super.onPause();
        timerMyHandler.removeCallbacks(timerMyRunnable);
        Button b = (Button)findViewById(R.id.startstop2);
        b.setText("start");
    }
}