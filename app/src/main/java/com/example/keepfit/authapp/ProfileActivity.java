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

import com.example.keepfit.MainActivity;
import com.example.keepfit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    private String username;
    private String url;

    private static final String EMAIL = "email";
    private static final String PREF_FILENAME = "main";

    private EditText editTextName, editTextPhoneNumber, editTextGender, editTextWeight, editTextHeight, editTextBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        btnSave = findViewById(R.id.btnSave);
        imageView = findViewById(R.id.imgView);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b!=null) username = (String) b.get("username");

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

        editTextName =  findViewById(R.id.EditTextName);
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
            url = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("profilepictures/"+ url);
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
        String name = editTextName.getText().toString().trim();
        String phonenumber = editTextPhoneNumber.getText().toString().trim();
        String birthday = editTextBirthday.getText().toString().trim();
        String gender = editTextGender.getText().toString().trim();
        String height = editTextHeight.getText().toString();
        String weight = editTextWeight.getText().toString().trim();

        if(name.isEmpty()){
            editTextName.setError("Name is required!");
            editTextName.requestFocus();
            return;
        }
        if(phonenumber.length() != 10){
            editTextPhoneNumber.setError("Valid phone number is required!");
            editTextPhoneNumber.requestFocus();
            return;
        }
        if(!(gender.equalsIgnoreCase("Male") || gender.equalsIgnoreCase("Female")|| gender.equalsIgnoreCase("Non binary"))){
            editTextGender.setError("Enter Male, Female or Non binary!");
            editTextGender.requestFocus();
            return;
        }
        if(birthday.length() != 8) {
            editTextBirthday.setError("Valid birthday is required!");
            editTextBirthday.requestFocus();
            return;
        }

        if(weight.length() < 2){
            editTextWeight.setError("Valid weight is required!");
            editTextWeight.requestFocus();
            return;
        }

        if(height.length() < 2){
            editTextHeight.setError("Valid height is required!");
            editTextHeight.requestFocus();
            return;
        }


        SharedPreferences sharedPreferences = getSharedPreferences(PREF_FILENAME, MODE_PRIVATE);
        String sharedemail = sharedPreferences.getString(EMAIL, "");

//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                //User user = snapshot.getValue(User.class);
//                //username = user.getUsername();
//                username = (String) snapshot.child("username").getValue();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        UserInformation userinfo = new UserInformation(sharedemail, name, phonenumber, gender, birthday, weight, height, pickey, username, url);
        FirebaseDatabase.getInstance().getReference("UserInformation").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userinfo);
        FirebaseDatabase.getInstance().getReference("Likes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("null");
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);

    }
}