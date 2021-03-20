package com.example.keepfit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);*/


        populate();

        //set button listeners
        ImageButton searchButton = (ImageButton) findViewById(R.id.searchButton);
        Button preset1 = (Button) findViewById(R.id.preset1);
        Button preset2 = (Button) findViewById(R.id.preset2);
        Button preset3 = (Button) findViewById(R.id.preset3);
        Button preset4 = (Button) findViewById(R.id.preset4);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get input from bar
                EditText searchInputBar = (EditText) findViewById(R.id.searchBar);
                String input = searchInputBar.getText().toString();
                search(input);
            }
        });

        preset1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { search(getString(R.string.tag1)); }
        });

        preset2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { search(getString(R.string.tag2)); }
        });

        preset3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { search(getString(R.string.tag3)); }
        });

        preset4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { search(getString(R.string.tag4)); }
        });
    }

    private void search(String input){

    }

    private void populate(){
        ArrayList<TextView> itemTextViews = addItemstoArrayandMakeInvisible();

        /*DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Video References");
        Query query = database.orderByKey().limitToFirst(20);
        query.on("child_added", function(data)=>{

        });*/

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Video References");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //arraylist to hold all titles to be put on screen
                ArrayList<String> titleList = new ArrayList<String>();

                //map to hold all video references from database
                GenericTypeIndicator<Map<String, Map<String, String>>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Map<String, String> >>() {};
                Map<String, Map<String, String>> allVideosMap = snapshot.getValue(genericTypeIndicator);

                //for each video (entry in allVideosMap), get the reference title and add it to titleList (if there is space)
                Iterator it = allVideosMap.entrySet().iterator();
                for(Map.Entry<String, Map<String, String>> entry : allVideosMap.entrySet()){
                    Map<String, String> videoMap = entry.getValue();
                    for(Map.Entry<String, String> data : videoMap.entrySet()){
                        if(data.getKey().equals("\"reference title\"")){
                            if(titleList.size() < 20) {
                                titleList.add(data.getValue());
                                break;
                            }
                        }
                    }
                    if(titleList.size() >= 20){break;}
                }
                //titleList now contains titles of videos (and images) to be displayed

                //for now, just display the titles
                /*Iterator<String> itTitle = titleList.iterator();
                for(TextView tv : itemTextViews){
                    if(!itTitle.hasNext()){break;}
                    tv.setText(itTitle.toString());
                    tv.setVisibility(View.VISIBLE);
                    itTitle.remove();
                }*/
                int i=0;
                for(String title : titleList){
                    TextView tv = itemTextViews.get(i);
                    tv.setText(title);
                    tv.setVisibility(View.VISIBLE);
                    i++;
                    if(i>20){break;}
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ArrayList<TextView> addItemstoArrayandMakeInvisible(){
        ArrayList<TextView> itemTextViews = new ArrayList<TextView>();
        itemTextViews.add(findViewById(R.id.item1));
        itemTextViews.add(findViewById(R.id.item2));
        itemTextViews.add(findViewById(R.id.item3));
        itemTextViews.add(findViewById(R.id.item4));
        itemTextViews.add(findViewById(R.id.item5));
        itemTextViews.add(findViewById(R.id.item6));
        itemTextViews.add(findViewById(R.id.item7));
        itemTextViews.add(findViewById(R.id.item8));
        itemTextViews.add(findViewById(R.id.item9));
        itemTextViews.add(findViewById(R.id.item10));
        itemTextViews.add(findViewById(R.id.item11));
        itemTextViews.add(findViewById(R.id.item12));
        itemTextViews.add(findViewById(R.id.item13));
        itemTextViews.add(findViewById(R.id.item14));
        itemTextViews.add(findViewById(R.id.item15));
        itemTextViews.add(findViewById(R.id.item16));
        itemTextViews.add(findViewById(R.id.item17));
        itemTextViews.add(findViewById(R.id.item18));
        itemTextViews.add(findViewById(R.id.item19));
        itemTextViews.add(findViewById(R.id.item20));

        for(TextView tv : itemTextViews){
            tv.setVisibility(View.GONE);
        }
        return itemTextViews;
    }


}