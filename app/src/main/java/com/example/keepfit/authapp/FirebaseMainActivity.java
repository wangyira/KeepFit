package com.example.keepfit.authapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keepfit.MainActivity;
import com.example.keepfit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class FirebaseMainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView register, forgotPass;
    private Button login;
    private EditText editTextEmail, editTextPassword;
    String username = new String();

    //private ProgressBar progressBar;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        forgotPass = (TextView) findViewById(R.id.forgotPassword);
        forgotPass.setOnClickListener(this);

        login = (Button) findViewById(R.id.loginUser);
        login.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.userEmail);
        editTextPassword = (EditText) findViewById(R.id.password);

        //progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:
                startActivity(new Intent(this, RegisterUser.class));
                break;
            case R.id.loginUser:
                userLogin();
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
                break;

        }


    }

    public void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
//        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
//            editTextEmail.setError("Please enter a valid email!");
//            editTextEmail.requestFocus();
//            return;
//        }
        if(password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            editTextPassword.setError("Password must be at least 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        //progressBar.setVisibility(View.VISIBLE);



        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //redirect to user profile
                    //startActivity(new Intent(FirebaseMainActivity.this, PublicProfile.class));
                    //getUsername(email);
                    //SharedPreferences sharedPreferences = getSharedPreferences("main", MODE_PRIVATE);
                    //SharedPreferences.Editor editor = sharedPreferences.edit();
                    //editor.putString("username", username);
                    //editor.apply();
                    String userId;
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null)
                        userId = user.getUid();
                    else userId = "";
                    DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("UserInformation");

                    dbreference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserInformation info = snapshot.getValue(UserInformation.class);
                            if (info != null) {
                                String weight = info.weight;
                                String username = info.username;
                                String email = info.email;
                                String referenceTitle = info.referenceTitle;

                                SharedPreferences sharedPreferences = getSharedPreferences("main", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("referenceTitle", referenceTitle);
                                editor.putString("weight", weight);
                                editor.putString("username", username);
                                editor.putString("email", email);
                                editor.apply();
                                editor.commit();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    startActivity(new Intent(FirebaseMainActivity.this, MainActivity.class));
                }
                else{
                    Toast.makeText(FirebaseMainActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void getUsername(String email){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("UserInformation");
        myRef.orderByChild("email").equalTo(email)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, String> data = (Map<String, String>) snapshot.getValue();
                        username = data.get("username");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }
}