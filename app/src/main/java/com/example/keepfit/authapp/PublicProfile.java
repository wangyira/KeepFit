package com.example.keepfit.authapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.keepfit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.squareup.picasso.Picasso;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class PublicProfile extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    //private FirebaseUser user;

    private ImageView pfp;
    private TextView username, name;

    private String userProfileToDisplay;
    private String nameToDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        pfp = findViewById(R.id.profilePic);
        name = findViewById(R.id.publicProfileName);
        username = findViewById(R.id.publicProfileUsername);

        userProfileToDisplay = getIntent().getStringExtra("username");

        //username of the person whose profile we are displaying -- send a string called "username" when creating intent

        DatabaseReference reference = firebaseDatabase.getReference("UserInformation");
        Query query = reference.orderByChild("username").equalTo(userProfileToDisplay);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserInformation userInfo = snapshot.getValue(UserInformation.class);
                nameToDisplay = userInfo.getName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference getImage = databaseReference.child("pickey");

        getImage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String link = dataSnapshot.getValue(String.class);
                Picasso.get().load(link).into(pfp);
            }
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // we are showing that error message in toast
                //Toast.makeText(PublicProfile.this, "Error Loading Image", Toast.LENGTH_SHORT).show();
            }
        });

        name.setText(nameToDisplay);
        username.setText(userProfileToDisplay);
    }
}