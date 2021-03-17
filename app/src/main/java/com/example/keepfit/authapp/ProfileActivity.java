package com.example.keepfit.authapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.keepfit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private Button btnChoose, btnUpload, btnSave;
    private ImageView imageView;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    private FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;
    String pickey;

    private static final String EMAIL = "email";
    private static final String PREF_FILENAME = "main";

    private EditText editTextUsername, editTextPhoneNumber, editTextGender, editTextWeight, editTextHeight, editTextBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        btnSave = findViewById(R.id.btnSave);
        imageView = findViewById(R.id.imgView);

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

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();

        editTextUsername =  findViewById(R.id.EditTextUsername);
        editTextPhoneNumber = findViewById(R.id.EditTextPhoneNo);
        editTextBirthday = findViewById(R.id.EditTextBirthday);
        editTextGender = findViewById(R.id.EditTextGender);
        editTextWeight = findViewById(R.id.EditTextWeight);
        editTextHeight = findViewById(R.id.EditTextHeight);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfile();
            }
        });

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
                            Toast.makeText(ProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ProfileActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void uploadProfile() {
        String username = editTextUsername.getText().toString().trim();
        String phonenumber = editTextPhoneNumber.getText().toString().trim();
        String birthday = editTextBirthday.getText().toString().trim();
        String gender = editTextGender.getText().toString().trim();
        String height = editTextHeight.getText().toString();
        String weight = editTextWeight.getText().toString().trim();

        if(username.isEmpty()){
            editTextUsername.setError("Username is required!");
            editTextUsername.requestFocus();
            return;
        }
        if(phonenumber.length() < 10){
            editTextPhoneNumber.setError("Valid phone number is required!");
            editTextPhoneNumber.requestFocus();
            return;
        }
        if(birthday.length() < 8) {
            editTextBirthday.setError("Valid Birthday is required!");
            editTextBirthday.requestFocus();
            return;
        }
        /*
        if((gender != "Male") && (gender != "Female") && (gender != "Non binary")){
            editTextGender.setError("Enter Male, Female or Non binary!");
            editTextGender.requestFocus();
            return;
        }

        if(weight.matches("[0-9]+") && weight.length() > 1){
            editTextWeight.setError("Valid Weight is required!");
            editTextWeight.requestFocus();
            return;
        }

        if(height.matches("[0-9]+") && height.length() > 1 && height.length() < 3){
            editTextHeight.setError("Valid Height is required!");
            editTextHeight.requestFocus();
            return;
        }
        */

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_FILENAME, MODE_PRIVATE);
        String sharedemail = sharedPreferences.getString(EMAIL, "");

        UserInformation userinfo = new UserInformation(sharedemail, username, phonenumber, gender, birthday, weight, height, pickey);
        FirebaseDatabase.getInstance().getReference("UserInformation").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userinfo);
        Intent intent = new Intent(ProfileActivity.this, FirebaseMainActivity.class);
        startActivity(intent);

    }
}