package com.example.keepfit.authapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.keepfit.MainActivity;
import com.example.keepfit.R;
import com.example.keepfit.StartLivestreamActivity;
import com.example.keepfit.VideoActivity;
import com.example.keepfit.calories.CalorieActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ProfileActivityEdits extends AppCompatActivity implements DialogExample.DialogExampleListener {

    private Button btnChoose, btnUpload, btnEditName, btnEditPhoneNumber, btnEditBirthday, btnEditGender, btnEditWeight, btnEditHeight, btnChangePass, btnLogout;
    private ImageView imageView;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseStorage storage;
    StorageReference storageReference;
    String pickey;

    String which = "";

    ImageButton video;

    private FirebaseUser user;
    private DatabaseReference dbreference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        btnEditName = findViewById(R.id.btnEditName);
        btnEditPhoneNumber = findViewById(R.id.btnEditPhoneNumber);
        btnEditBirthday = findViewById(R.id.btnEditBirthday);
        btnEditGender = findViewById(R.id.btnEditGender);
        btnEditWeight = findViewById(R.id.btnEditWeight);
        btnEditHeight = findViewById(R.id.btnEditHeight);
        btnChangePass = findViewById(R.id.btnchangepass);
        btnLogout = findViewById(R.id.btnlogout);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //mAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set search selected
        bottomNavigationView.setSelectedItemId(R.id.nav_account);

        //perform itemselectedlistener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                switch (menuItem.getItemId()){
                    case R.id.nav_account:
                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_calorie:
                        startActivity(new Intent(getApplicationContext(), CalorieActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_upload:
                        startActivity(new Intent(getApplicationContext(), VideoActivity.class));
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

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        btnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = "name";
                OpenDialog();

            }
        });

        btnEditPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = "phonenumber";
                OpenDialog();
            }
        });

        btnEditBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = "birthday";
                OpenDialog();
            }
        });

        btnEditGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = "gender";
                OpenDialog();
            }
        });

        btnEditWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = "weight";
                OpenDialog();
            }
        });

        btnEditHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = "height";
                OpenDialog();
            }
        });

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivityEdits.this, ForgotPassword.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivityEdits.this, FirebaseMainActivity.class));
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        dbreference = FirebaseDatabase.getInstance().getReference("UserInformation");
        userId = user.getUid();

        final TextView greetingTextView = (TextView) findViewById(R.id.editprofile);
        final TextView nameTextView = (TextView) findViewById(R.id.TextViewName);
        final TextView phoneTextView = (TextView) findViewById(R.id.TextViewPhoneNumber);
        final TextView bdayTextView = (TextView) findViewById(R.id.TextViewBirthday);
        final TextView genderTextView = (TextView) findViewById(R.id.TextViewGender);
        final TextView weightTextView = (TextView) findViewById(R.id.TextViewWeight);
        final TextView heightTextView = (TextView) findViewById(R.id.TextViewHeight);
        final ImageView profilePicture = findViewById(R.id.imgView);
        final TextView usernameTextView = (TextView) findViewById(R.id.TextViewUsername);
        final TextView emailTextView = (TextView) findViewById(R.id.TextViewEmail);

        dbreference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserInformation info = snapshot.getValue(UserInformation.class);
                if (info != null) {
                    String name = info.name;
                    String phone = info.phonenumber;
                    String bday = info.birthday;
                    String gender = info.gender;
                    String weight = info.weight;
                    String height = info.height;
                    String imgLink = info.pickey;
                    String username = info.username;
                    String email = info.email;

                    Picasso.get().load(imgLink).into(profilePicture);

                    if (username != null) {
                        usernameTextView.setText("Username: " + username);
                    } else {
                        usernameTextView.setError("Please enter your username.");
                        usernameTextView.requestFocus();
                        return;
                    }

                    if (email != null) {
                        emailTextView.setText("Email: " + email);
                    } else {
                        emailTextView.setError("Please enter your email.");
                        emailTextView.requestFocus();
                        return;
                    }

                    if (name != null) {
                        greetingTextView.setText("Welcome, " + name + "!");
                        nameTextView.setText("Name: " + name);
                    } else {
                        nameTextView.setError("Please enter your name.");
                        nameTextView.requestFocus();
                        return;
                    }

                    if (phone != null) phoneTextView.setText("Phone Number: " + phone);
                    else {
                        phoneTextView.setError("Please enter your phone number.");
                        phoneTextView.requestFocus();
                        return;
                    }
                    if (bday != null)
                        bdayTextView.setText("Birthday: " + bday.substring(0, 2) + "/" + bday.substring(2, 4) + "/" + bday.substring(4));
                    else {
                        bdayTextView.setError("Please enter your birthday.");
                        bdayTextView.requestFocus();
                        return;
                    }
                    if (gender != null) genderTextView.setText("Gender: " + gender);
                    else {
                        genderTextView.setError("Please enter your gender.");
                        genderTextView.requestFocus();
                        return;
                    }
                    if (weight != null) weightTextView.setText("Weight: " + weight + "lbs");
                    else {
                        weightTextView.setError("Please enter your weight.");
                        weightTextView.requestFocus();
                        return;
                    }
                    if (height != null) heightTextView.setText("Height: " + height + "in");
                    else {
                        heightTextView.setError("Please enter your height.");
                        heightTextView.requestFocus();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        video = (ImageButton) findViewById(R.id.imageButton);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(video)
                openNewActivity();
            }
        });
    }

    public void openNewActivity(){
        Intent intent = new Intent(this, ScrollingActivity.class);
        startActivity(intent);
    }

    public void OpenDialog() {
        DialogExample exampleDialog = new DialogExample();
        exampleDialog.show(getSupportFragmentManager(), "hi");

    }

    @Override
    public void applyTexts(String edits) {

        FirebaseDatabase.getInstance().getReference("UserInformation").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(which).setValue(edits);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("profilepictures/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivityEdits.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    pickey =String.valueOf(downloadUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivityEdits.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
}