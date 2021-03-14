package com.example.keepfit;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class StartLivestreamActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button button;
    Spinner spinner;
    DatabaseReference databaseReference;

    TextView title;
    TextView maxPeople;
    String selectedType;
    TextView time;
    TextView zoom;

    LivestreamMember member;

    String[] exerciseTypes = {"","Aerobic","Anaerobic","Flexibility","Stability"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_livestream);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

//        above copied from MainActivity.java
        title = findViewById(R.id.livestream_title);
        maxPeople = findViewById(R.id.livestream_num_people);
        time = findViewById(R.id.endTime);
        zoom = findViewById(R.id.zoom);

        button = findViewById(R.id.save_button);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Livestream Details");
        spinner = findViewById(R.id.livestream_exercise_spinner);
        spinner.setOnItemSelectedListener(this);

        member = new LivestreamMember();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, exerciseTypes);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SaveDetails(selectedType);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedType = spinner.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    void SaveDetails(String selectedType){
        String titleText = title.getText().toString();
        String peopleText = maxPeople.getText().toString();
        String timeText = time.getText().toString();
        String zoomText = zoom.getText().toString();

        //need to store username

        //check for time inputted correctly, is after current time

        //check zoom id is 9 digit long?
        


        if(titleText.isEmpty()){
            Toast.makeText(this, "title cannot be empty", Toast.LENGTH_SHORT).show();
        }
        if(peopleText.isEmpty()){
            Toast.makeText(this, "max # of people cannot be empty", Toast.LENGTH_SHORT).show();
        }
        if(selectedType == ""){
            Toast.makeText(this, "please select an exercise type", Toast.LENGTH_SHORT).show();
        }
        if(timeText.isEmpty()){
            Toast.makeText(this, "end time cannot be empty", Toast.LENGTH_SHORT).show();
        }
        if(zoomText.isEmpty()) {
            Toast.makeText(this, "zoom room id cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else{
            member.setTitle(titleText);
            member.setMaxNumberOfPeople(Integer.parseInt(peopleText));
            member.setExerciseType(selectedType);
            member.setEndTime(timeText);
            member.setZoomRoomId(zoomText);

            String id = databaseReference.push().getKey();
            databaseReference.child(id).setValue(member);
            Toast.makeText(this, "livestream started", Toast.LENGTH_SHORT).show();
        }

    }
}