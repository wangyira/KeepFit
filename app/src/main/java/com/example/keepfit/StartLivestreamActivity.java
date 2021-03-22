package com.example.keepfit;

import android.graphics.Bitmap;
import android.icu.text.SymbolTable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;

import java.io.IOException;
import java.util.Calendar;
import android.util.Log;
import java.util.Arrays;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.keepfit.authapp.ProfileActivityEdits;
import com.example.keepfit.calories.CalorieActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//image
import android.provider.MediaStore;
import android.database.Cursor;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    Button uploadImage;

    Uri imageURI;
    FirebaseAuth mAuth;
    String imageUrl = null;

    LivestreamMember member;
    String[] exerciseTypes = {"","Aerobic","Anaerobic","Flexibility","Stability"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_livestream);

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

        //Set startlivestream selected
        bottomNavigationView.setSelectedItemId(R.id.nav_livestream);

        //perform itemselectedlistener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                switch (menuItem.getItemId()){
                    case R.id.nav_account:
                        startActivity(new Intent(getApplicationContext(),ProfileActivityEdits.class));
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
                        startActivity(new Intent(getApplicationContext(), VideoUploadActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_livestream:
                        return true;
                }
                return false;
            }
        });


//        above copied from MainActivity.java
        title = findViewById(R.id.livestream_title);
        maxPeople = findViewById(R.id.livestream_num_people);
        time = findViewById(R.id.endTime);
        zoom = findViewById(R.id.zoom);

        button = findViewById(R.id.save_button);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Livestream Details");
        uploadImage = findViewById(R.id.upload_image_btn);
        spinner = findViewById(R.id.livestream_exercise_spinner);
        spinner.setOnItemSelectedListener(this);

        member = new LivestreamMember();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, exerciseTypes);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String zoomLink = SaveDetails(selectedType);
                if(zoomLink != "ERROR") {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse(zoomLink));
                    startActivity(viewIntent);
                }
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                System.out.println("before image button click");
                chooseImageFromGallery();
                System.out.println("after image button click");
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

    public void chooseImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        int IMAGE = 2;
        startActivityForResult(galleryIntent, IMAGE);
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
        .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                System.out.println("Sign in error!!!!");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==2){
            if(data != null){
                imageURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    String SaveDetails(String selectedType){
        String titleText = title.getText().toString();
        String peopleText = maxPeople.getText().toString();
        String timeText = time.getText().toString();
        String zoomText = zoom.getText().toString();

        //need to store username

        //check for time inputted correctly, is after current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String[] timeArr = timeText.split(":");

        Log.d("MYINT","value: "+hour+ minute);
        System.out.println("arr: " + Arrays.toString(timeArr));

        if(hour > Integer.parseInt(timeArr[0])){
            Toast.makeText(this, "end time need to be later than the current time", Toast.LENGTH_SHORT).show();
            return "ERROR";
        }
        if(hour == Integer.parseInt(timeArr[0]) && minute > Integer.parseInt(timeArr[1])){
            Toast.makeText(this, "end time need to be later than the current time", Toast.LENGTH_SHORT).show();
            return "ERROR";
        }
        if(titleText.isEmpty()){
            Toast.makeText(this, "title cannot be empty", Toast.LENGTH_SHORT).show();
            return "ERROR";
        }
        if(peopleText.isEmpty()){
            Toast.makeText(this, "max # of people cannot be empty", Toast.LENGTH_SHORT).show();
            return "ERROR";
        }
        if(selectedType == ""){
            Toast.makeText(this, "please select an exercise type", Toast.LENGTH_SHORT).show();
            return "ERROR";
        }
        if(timeText.isEmpty()){
            Toast.makeText(this, "end time cannot be empty", Toast.LENGTH_SHORT).show();
            return "ERROR";
        }
        if(zoomText.isEmpty()) {
            Toast.makeText(this, "zoom room id cannot be empty", Toast.LENGTH_SHORT).show();
            return "ERROR";
        }
        else{
            FirebaseStorage storage = FirebaseStorage.getInstance();
            UUID randomUUID = UUID.randomUUID();
            StorageReference imageRef = storage.getReference("livestream_thumbnail_images/" + randomUUID + ".jpg");
            UploadTask imageUploadTask = imageRef.putFile(imageURI);

            imageUploadTask.addOnSuccessListener(StartLivestreamActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot imageTaskSnapshot) {
                    //imageUrl = imageTaskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                    //System.out.println("image url"+imageUrl);
                    member.setTitle(titleText);
                    member.setMaxNumberOfPeople(Integer.parseInt(peopleText));
                    member.setExerciseType(selectedType);
                    member.setEndTime(timeText);
                    member.setZoomLink(zoomText);
                    //member.setImageUrl(imageUrl);
                    member.setReferenceTitle(randomUUID + ".jpg");
                    String id = databaseReference.push().getKey();
                    databaseReference.child(id).setValue(member);
                }
            });
        }

        return zoomText;
    }
}
