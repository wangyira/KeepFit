package com.example.keepfit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ContentScrollingActivity extends AppCompatActivity{
        ImageButton video;
        ImageButton livestream;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.content_scrolling);

            //retrieve thumbnail image from firebase
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Video References");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<String> titleList = new ArrayList<String>();
                    //Map<String, Map<String, String>> valueMap = (Map<String, Map<String, String>>) snapshot.getValue(Map.class);
                    GenericTypeIndicator<Map<String, Map<String, String>>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Map<String, String> >>() {};
                    Map<String, Map<String, String>> valueMap = snapshot.getValue(genericTypeIndicator);
                    Iterator it = valueMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        Map<String, String> videoMap = (Map<String, String>) pair.getValue();
                        Iterator itVid = videoMap.entrySet().iterator();
                        String title = null;
                        while (itVid.hasNext()) {
                            Map.Entry<String, String> metaPair = (Map.Entry<String, String>) itVid.next();
                            if (metaPair.getKey() == "reference title") {
                                titleList.add((String) metaPair.getValue());
                                break;
                            }
                            itVid.remove();
                        }
                        it.remove(); // avoids a ConcurrentModificationException
                    }
                    //titleList now contains titles of all videos (and images)
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    Iterator<String> tnIt = titleList.iterator();
                    while(tnIt.hasNext()){
                        StorageReference imageRef = storageRef.child("thumbnail images/" + tnIt.next().toString());
                        try{
                            final File localFile = File.createTempFile( tnIt.toString(), "jpg");
                            imageRef.getFile(localFile).addOnSuccessListener(
                                    (OnSuccessListener) (TaskSnapshot) -> {
                                        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton1);
                                        Bitmap bm = imgToBitmap(localFile);
                                        imageButton.setImageBitmap(bm);
                                    });
                        }catch(Exception e){
                            AlertDialog.Builder videoDialog = new AlertDialog.Builder(ContentScrollingActivity.this);
                            videoDialog.setTitle("ERROR.");

                            String[] pictureDialogItems = {
                                    "OK"};
                            videoDialog.setItems(pictureDialogItems,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });

                            videoDialog.show();
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            video = (ImageButton) findViewById(R.id.imageButton1);
            video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if(video)
                    openNewActivityVideo();
                }
            });
            livestream = (ImageButton) findViewById(R.id.imageButton2);
            livestream.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if(livestream)
                    openNewActivityLivestream();
                }
            });
        }

        public void openNewActivityVideo(){
            Intent intent = new Intent(this, ScrollingActivity.class);
            startActivity(intent);
        }
        public void openNewActivityLivestream(){
            Intent intent = new Intent(this, LivestreamActivity.class);
            startActivity(intent);
        }

        private Bitmap imgToBitmap(File file){
            byte[] bytes = null;
            try{
                FileInputStream fis = new FileInputStream(file);
                //create FileInputStream which obtains input bytes from a file in a file system
                //FileInputStream is meant for reading streams of raw bytes such as image data. For reading streams of characters, consider using FileReader.

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                try {
                    for (int readNum; (readNum = fis.read(buf)) != -1;) {
                        //Writes to this byte array output stream
                        bos.write(buf, 0, readNum);
                        System.out.println("read " + readNum + " bytes,");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ContentScrollingActivity.class.getName()).log(Level.SEVERE, null, ex);
                }

                bytes = bos.toByteArray();
            } catch(FileNotFoundException fnfe){
                AlertDialog.Builder videoDialog = new AlertDialog.Builder(this);
                videoDialog.setTitle("ERROR: fnfe.");

                String[] pictureDialogItems = {
                        "OK"};
                videoDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                videoDialog.show();
            }

            ByteArrayInputStream imageStream = null;

            try {
                imageStream = new ByteArrayInputStream(bytes);
                return BitmapFactory.decodeStream(imageStream);
            }
            catch (Exception ex) {
                Log.d("My Activity", "Unable to generate a bitmap: " + ex.getMessage());
                return null;
            }
            finally {
                if (imageStream != null) {
                    try { imageStream.close(); }
                    catch (Exception ex) {}
                }
            }
        }
}