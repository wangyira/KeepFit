package com.example.keepfit.authapp;

import android.content.Context;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {
    //private TextView banner
    private Button register;
    private EditText editTextEmail, editTextPassword;
    private static final String EMAIL = "email";
    private static final String PREF_FILENAME = "main";
    private EditText editTextUsername;
    //private ProgressBar progressBar;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String name;
    private String email;
    private String password;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

//        banner = (TextView) findViewById(R.id.banner);
//        banner.setOnClickListener(this);

        editTextUsername = (EditText) findViewById(R.id.username);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        register = (Button) findViewById(R.id.registerUser);
        register.setOnClickListener(this);

//        name = editTextUsername.getText().toString().trim();
//        email = editTextEmail.getText().toString();
//        password = editTextPassword.getText().toString().trim();

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

    protected void registerUsers(){
        name = editTextUsername.getText().toString().trim();
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString().trim();
        if(name.isEmpty()){
            editTextUsername.setError("Username is required!");
            editTextUsername.requestFocus();
            return;
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Users").orderByChild("username").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    editTextUsername.setError("Username in use. Please create a new username!");
                    editTextUsername.requestFocus();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                                                //editor.putString("username", name);
                                                editor.apply();
                                                Intent intent = new Intent(RegisterUser.this, ProfileActivity.class);
                                                intent.putExtra("username", name);
                                                startActivity(intent);

                                                //setContentView(R.layout.activity_profile);
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
