package com.example.keepfit.authapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.keepfit.MainActivity;
import com.example.keepfit.R;
import com.example.keepfit.StartLivestreamActivity;
import com.example.keepfit.VideoActivity;
import com.example.keepfit.VideoUploadActivity;
import com.example.keepfit.calories.CalorieActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.mikhaellopez.circularimageview.CircularImageView;


public class FollowInfo extends AppCompatActivity {
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference ref;

    private TextView title;

    private FirebaseUser user;
    private DatabaseReference dbreference;
    private String userId;

    String imgLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_follow_info);

        //navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set account selected
//        bottomNavigationView.setSelectedItemId(R.id.nav_search);

        //perform itemselectedlistener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
//                    case R.id.nav_account:
//                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_calorie:
                        startActivity(new Intent(getApplicationContext(), CalorieActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_upload:
                        startActivity(new Intent(getApplicationContext(), VideoUploadActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_livestream:
                        startActivity(new Intent(getApplicationContext(), StartLivestreamActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });


        Bundle b = getIntent().getExtras();
        String type = b.getString("type");
        String usertype = b.getString("usertype");

        title = findViewById(R.id.title);
        if (type.equals("followers")) {
            title.setText("Followers");

            String currentusername;
            if (usertype.equals("personal")) {
                SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
                currentusername = sharedPref.getString("username", null);
            }
            else {
                currentusername = usertype;
            }

            DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("Followers");

            DatabaseReference pictureRef = FirebaseDatabase.getInstance().getReference("UserInformation");


            followersRef.child(currentusername).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<String, String> userfollowers = (Map<String, String>) snapshot.getValue();
                    if (userfollowers != null) {
                        for (Map.Entry entry : userfollowers.entrySet()) {
                            Button myButton = new Button(FollowInfo.this);
                            myButton.setText((CharSequence) entry.getValue());

                            LinearLayout layout2 = new LinearLayout(FollowInfo.this);
                            layout2.setOrientation(LinearLayout.HORIZONTAL);
                            layout2.setGravity(Gravity.CENTER_VERTICAL);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            ImageView myimage = new ImageView(FollowInfo.this);

                            pictureRef.orderByChild("username").equalTo(String.valueOf(entry.getValue())).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                                    if(results != null) {
                                        for (Map.Entry<String, Map<String, String>> entry : results.entrySet()) {
                                            Map<String, String> temp = entry.getValue();
                                            imgLink = temp.get("pickey");
                                            Picasso.get().load(imgLink).transform(new CircleTransform()).into(myimage);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}

                            });

                            myimage.setAdjustViewBounds(true);
                            myimage.setMaxHeight(100);
                            myimage.setMaxWidth(100);
                            myimage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            layout2.addView(myimage, lp);
                            layout2.addView(myButton, lp);

                            myButton.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View view) {
                                    Intent intent = new Intent(FollowInfo.this, PublicProfile.class);
                                    intent.putExtra("username", (CharSequence) entry.getValue());
                                    startActivity(intent);
                                }
                            });

                            LinearLayout ll = (LinearLayout)findViewById(R.id.followlist);
                            ll.addView(layout2, lp);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

        if (type.equals("following")) {
            title.setText("Following");

            String currentusername;
            if (usertype.equals("personal")) {
                SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
                currentusername = sharedPref.getString("username", null);
            }
            else {
                currentusername = usertype;
            }


            DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("Following");

            DatabaseReference pictureRef = FirebaseDatabase.getInstance().getReference("UserInformation");

            followingRef.child(currentusername).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<String, String> userfollowing = (Map<String, String>) snapshot.getValue();
                    if (userfollowing != null) {
                        for (Map.Entry entry : userfollowing.entrySet()) {
                            Button myButton = new Button(FollowInfo.this);
                            myButton.setText((CharSequence) entry.getValue());

                            LinearLayout layout2 = new LinearLayout(FollowInfo.this);
                            layout2.setOrientation(LinearLayout.HORIZONTAL);
                            layout2.setGravity(Gravity.CENTER_VERTICAL);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            ImageView myimage = new ImageView(FollowInfo.this);

                            pictureRef.orderByChild("username").equalTo(String.valueOf(entry.getValue())).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) snapshot.getValue();
                                    if(results != null) {
                                        for (Map.Entry<String, Map<String, String>> entry : results.entrySet()) {
                                            Map<String, String> temp = entry.getValue();
                                            imgLink = temp.get("pickey");
                                            Picasso.get().load(imgLink).transform(new CircleTransform()).into(myimage);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}

                            });

                            myimage.setAdjustViewBounds(true);
                            myimage.setMaxHeight(100);
                            myimage.setMaxWidth(100);
                            myimage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            layout2.addView(myimage, lp);
                            layout2.addView(myButton, lp);

                            myButton.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View view) {
                                    Intent intent = new Intent(FollowInfo.this, PublicProfile.class);
                                    intent.putExtra("username", (CharSequence) entry.getValue());
                                    startActivity(intent);
                                }
                            });

                            LinearLayout ll = (LinearLayout)findViewById(R.id.followlist);
                            ll.addView(layout2, lp);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }


}
