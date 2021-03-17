package com.example.keepfit.authapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keepfit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {
    //private TextView banner
    private Button register;
    private EditText editTextName, editTextEmail, editTextPassword;
    private static final String EMAIL = "email";
    private static final String PREF_FILENAME = "main";
    //private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

//        banner = (TextView) findViewById(R.id.banner);
//        banner.setOnClickListener(this);

        editTextName = (EditText) findViewById(R.id.name);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        register = (Button) findViewById(R.id.registerUser);
        register.setOnClickListener(this);

        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.registerUser:
                registerUsers();
                break;
        }
    }

    private void registerUsers(){
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString().trim();

        if(name.isEmpty()){
            editTextName.setError("Name is required!");
            editTextName.requestFocus();
            return;
        }
        if(email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length() < 6){
            editTextPassword.setError("Password must be 6 characters or longer!");
            editTextPassword.requestFocus();
            return;
        }
//        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
//            editTextEmail.setError("Please provide valid email!");
//            editTextEmail.requestFocus();
//            return;
//        }

        //progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(name, email);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegisterUser.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                                //progressBar.setVisibility(View.VISIBLE);
                                                SharedPreferences sharedPreferences = getSharedPreferences(PREF_FILENAME, MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString(EMAIL, email);
                                                editor.commit();
                                                Intent intent = new Intent(RegisterUser.this, ProfileActivity.class);
                                                startActivity(intent);
                                                //redirect user to profile
                                            }
                                            else{
                                                Toast.makeText(RegisterUser.this, "Failed to register! Try again.", Toast.LENGTH_LONG).show();
                                                //progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(RegisterUser.this, "Failed to register! Try again.", Toast.LENGTH_LONG).show();
                            //progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }
}
