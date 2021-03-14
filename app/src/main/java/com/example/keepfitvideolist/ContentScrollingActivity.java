package com.example.keepfitvideolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ContentScrollingActivity extends AppCompatActivity{
        ImageButton video;
        ImageButton livestream;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.content_scrolling);
            video = (ImageButton) findViewById(R.id.imageButton);
            video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if(video)
                    openNewActivity();
                }
            });
            livestream = (ImageButton) findViewById(R.id.imageButton2);
            livestream.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if(livestream)
                    openNewActivity2();
                }
            });
        }
        public void openNewActivity(){
            Intent intent = new Intent(this, ScrollingActivity.class);
            startActivity(intent);
        }
        public void openNewActivity2(){
            Intent intent = new Intent(this, LivestreamActivity.class);
            startActivity(intent);
        }
}