package com.example.keepfit;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.example.keepfit.authapp.ProfileActivityEdits;
import com.example.keepfit.calories.CalorieActivity;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateWorkout extends AppCompatActivity {

    EditText exerciseName, exerciseTime;
    Button submitExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);

        //navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set startlivestream selected
        bottomNavigationView.setSelectedItemId(R.id.nav_upload);

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
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_calorie:
                        startActivity(new Intent(getApplicationContext(), CalorieActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_upload:
                        return true;
                    case R.id.nav_livestream:
                        startActivity(new Intent(getApplicationContext(), StartLivestreamActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        exerciseName = findViewById(R.id.exerciseName);
        exerciseTime = findViewById(R.id.exerciseTime);
        exerciseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CreateWorkout.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        exerciseTime.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, true); //24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        submitExercise = findViewById(R.id.submitExercise);
        submitExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addExerciseToCalendar();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void addExerciseToCalendar() throws ParseException {
        String exerciseInfo = exerciseName.getText().toString().trim();
        String time = exerciseTime.getText().toString().trim();

        Bundle b = getIntent().getExtras();
        String day = b.getString("day");
        String dateTime = day + " " + time;
        Log.d("datetime", dateTime);
        //Apr 23, 2021 10:30
        SimpleDateFormat format = new SimpleDateFormat("MMM DD, yyyy hh:mm", Locale.getDefault());

        Date date = format.parse(dateTime);
        long dateInMillis = date.getTime();

        Event e = new Event((int)(Math.random() * 0x1000000), dateInMillis, exerciseInfo);

        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events").child(username);
        eventRef.push().setValue(e);

        startActivity(new Intent(getApplicationContext(), VideoUploadActivity.class));
        overridePendingTransition(0,0);

    }
}