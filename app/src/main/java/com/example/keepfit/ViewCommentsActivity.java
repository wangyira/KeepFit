package com.example.keepfit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class ViewCommentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comments);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        //get Key
        String key = new String();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            key = extras.getString("key");
        }

        final String finalKey = key;

        displayComments(key);

        Button post = (Button) findViewById(R.id.postButton);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.commentInput);
                String input = et.getText().toString();
                makeComment(finalKey, input);
                et.setText("");
            }
        });

    }


    private void displayComments(String videoKey){
        //get comments in time order
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Log.e("VideoKey!!!", ""+videoKey);
        DatabaseReference myRef = database.getReference("Video References").child(videoKey).child("comments");
        myRef.orderByChild("postedTime").limitToLast(1000).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("onDataChange", "onDataChange");
                for(DataSnapshot child : snapshot.getChildren()){
                    Comment c = child.getValue(Comment.class);
                    //videoComments.add(c);
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.commentsLayout);
                    TextView tv = new TextView(ViewCommentsActivity.this);
                    String str = "" + c.getPostingUsername() + " | " + c.getPostedTime() + " | " + c.getText();
                    tv.setText(str);
                    linearLayout.addView(tv);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void makeComment(String videoKey, String text){
        Date date = new Date();
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        Comment comment = new Comment(text, username, date);

        DatabaseReference videosRef = FirebaseDatabase.getInstance().getReference("Video References").child(videoKey).child("comments");
        videosRef.push().setValue(comment);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.commentsLayout);
        TextView tv = new TextView(ViewCommentsActivity.this);
        String str = "" + comment.getPostingUsername() + " | " + comment.getPostedTime() + " | " + comment.getText();
        tv.setText(str);
        linearLayout.addView(tv);
    }

}