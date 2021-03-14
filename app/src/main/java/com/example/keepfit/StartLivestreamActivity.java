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

public class StartLivestreamActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button button;
    Spinner spinner;
    TextView textView;
    DatabaseReference databaseReference;
    String selectedType;
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

//        textView = findViewById(R.id.livestream_type);
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
//        textView.setText(selectedType);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    void SaveDetails(String selectedType){
        if(selectedType == ""){
            Toast.makeText(this, "please select a type", Toast.LENGTH_SHORT).show();
        }
        else{
            member.setExerciseType(selectedType);
            String id = databaseReference.push().getKey();
            databaseReference.child(id).setValue(member);
            Toast.makeText(this, "livestream started", Toast.LENGTH_SHORT).show();
        }

    }
}