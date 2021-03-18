package com.example.keepfit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

//import com.example.keepfitvideolist.R;

public class LivestreamActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.livestreams);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://usc.zoom.us/my/elainen"));
        startActivity(browserIntent);
    }
}
