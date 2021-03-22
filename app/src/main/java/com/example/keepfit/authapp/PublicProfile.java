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
import android.util.Log;
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
    private String uid;
    private String pfpLink;
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

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserInformation");
        reference.orderByChild("username").equalTo(userProfileToDisplay).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    uid = ds.getKey();
                    Log.d("TAG", uid);
                }
                reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserInformation userInfo = snapshot.getValue(UserInformation.class);
                        nameToDisplay = userInfo.name;
                        pfpLink = userInfo.pickey;
                        Picasso.get().load(pfpLink).into(pfp);
                        name.setText(nameToDisplay);
                        username.setText(userProfileToDisplay);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}