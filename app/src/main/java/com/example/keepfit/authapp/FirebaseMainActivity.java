package com.example.keepfit.authapp;

import android.content.Intent;
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

import com.example.keepfit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseMainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView register, forgotPass;
    private Button login;
    private EditText editTextEmail, editTextPassword;

    //private ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

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

        }


    }

    private void userLogin() {
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
                    startActivity(new Intent(FirebaseMainActivity.this, ProfileActivityEdits.class));
                }
                else{
                    Toast.makeText(FirebaseMainActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}