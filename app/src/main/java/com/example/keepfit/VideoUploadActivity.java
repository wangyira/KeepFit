package com.example.keepfit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.example.keepfit.authapp.ForgotPassword;
import com.example.keepfit.authapp.ProfileActivityEdits;
import com.example.keepfit.calories.CalorieActivity;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class VideoUploadActivity extends AppCompatActivity {
    //private CalendarView calendar;
    private String selectedDate;
    private Calendar calendar = Calendar.getInstance(Locale.getDefault());
    private CompactCalendarView compactCalendarView;
    private boolean shouldShow = false;
    private ActionBar toolbar;
    private Date date;
    private String dateAsString;
    private int completed;


    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoupload);

        //navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set startlivestream selected
        bottomNavigationView.setSelectedItemId(R.id.nav_upload);

        //perform itemselectedlistener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                switch (menuItem.getItemId()){
                    case R.id.nav_account:
                        startActivity(new Intent(getApplicationContext(), ProfileActivityEdits.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_calorie:
                        startActivity(new Intent(getApplicationContext(), CalorieActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_upload:
                        return true;
                    case R.id.nav_livestream:
                        startActivity(new Intent(getApplicationContext(), StartLivestreamActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });


        compactCalendarView = (CompactCalendarView) findViewById(R.id.calendar);

        compactCalendarView.removeAllEvents();

        final ListView eventsListView = findViewById(R.id.eventsList);
        final ToggleButton showCalendarWithAnimationBut = findViewById(R.id.slide_calendar);
        final Button removeAllEventsBut = findViewById(R.id.remove_all_events);
        final Button addEvent = findViewById(R.id.addEvent);
        final TextView displayDateTv = (TextView) findViewById(R.id.clickedDay);
        final List<String> mutableEvents = new ArrayList<>();
        displayDateTv.setVisibility(View.GONE);

        final ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mutableEvents);
        eventsListView.setAdapter(adapter);

        compactCalendarView.setUseThreeLetterAbbreviation(false);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setIsRtl(false);
        compactCalendarView.displayOtherMonthDays(false);

        //loadEvents();
        loadEventsForYear(2021);
        compactCalendarView.invalidate();

        //logEventsByMonth(compactCalendarView);

        // Add event 1 on Sun, 07 Jun 2015 18:20:51 GMT
//        Event ev1 = new Event(Color.GREEN, calendar.getTimeInMillis(), "event1");
//        compactCalendarView.addEvent(ev1);
//
//        // Added event 2 GMT: Sun, 07 Jun 2015 19:10:51 GMT
//        Event ev2 = new Event(Color.GREEN, calendar.getTimeInMillis(), "event2");
//        compactCalendarView.addEvent(ev2);






        // Query for events on Sun, 07 Jun 2015 GMT.
        // Time is not relevant when querying for events, since events are returned by day.
        // So you can pass in any arbitary DateTime and you will receive all events for that day.
        List<Event> events = compactCalendarView.getEvents(calendar.getTimeInMillis()); // can also take a Date object

        // events has size 2 with the 2 events inserted previously
        Log.d("hi", "Events: " + events);


        toolbar = this.getSupportActionBar();
        toolbar.setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        // define a listener to receive callbacks when certain events happen.
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                toolbar.setTitle(dateFormatForMonth.format(dateClicked));
                date = dateClicked;

                List<Event> events = compactCalendarView.getEvents(dateClicked);
                //List<Event> events = getEventsForDay(dateClicked);

                dateAsString = DateFormat.getDateInstance().format(dateClicked);

                displayDateTv.setText("Events on " + dateAsString + ":");
                displayDateTv.setVisibility(View.VISIBLE);

                if(events!=null){
                    mutableEvents.clear();
                    for (Event event : events) {
                        mutableEvents.add((String) event.getData());
                    }
                    adapter.notifyDataSetChanged();
                }


                Log.d("hi", "Day was clicked: " + dateClicked + " with events " + events);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                toolbar.setTitle(dateFormatForMonth.format(firstDayOfNewMonth));
                Log.d("hi", "Month was scrolled to: " + firstDayOfNewMonth);
            }
        });


//        final View.OnClickListener showCalendarOnClickLis = getCalendarShowLis();
//        slideCalendarBut.setOnClickListener(showCalendarOnClickLis);

        final View.OnClickListener exposeCalendarListener = getCalendarExposeLis();
        showCalendarWithAnimationBut.setOnClickListener(exposeCalendarListener);

        compactCalendarView.setAnimationListener(new CompactCalendarView.CompactCalendarAnimationListener() {
            @Override
            public void onOpened() {
            }

            @Override
            public void onClosed() {
            }
        });

        removeAllEventsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.removeAllEvents();
            }
        });

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateAsString ==null || dateAsString.isEmpty()){
                    dateAsString = DateFormat.getDateInstance().format(new Date());
                }
                Intent i = new Intent(VideoUploadActivity.this, CreateWorkout.class);
                i.putExtra("day", dateAsString);
                startActivity(i);
            }
        });

//        calendar = findViewById(R.id.calendar);
//
//        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                selectedDate = Integer.toString(year) + Integer.toString(month) + Integer.toString(dayOfMonth);
//
//            }
//        });

//        Calendar cal = Calendar.getInstance();
//        Intent intent = new Intent(Intent.ACTION_EDIT);
//        intent.setType("vnd.android.cursor.item/event");
//        intent.putExtra("beginTime", cal.getTimeInMillis());
//        intent.putExtra("allDay", true);
//        intent.putExtra("rrule", "FREQ=YEARLY");
//        intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
//        intent.putExtra("title", "A Test Event from android app");
//        startActivity(intent);



    }

    @NonNull
    private View.OnClickListener getCalendarShowLis() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!compactCalendarView.isAnimating()) {
                    if (shouldShow) {
                        compactCalendarView.showCalendar();
                    } else {
                        compactCalendarView.hideCalendar();
                    }
                    shouldShow = !shouldShow;
                }
            }
        };
    }

    @NonNull
    private View.OnClickListener getCalendarExposeLis() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!compactCalendarView.isAnimating()) {
                    if (shouldShow) {
                        compactCalendarView.showCalendarWithAnimation();
                    } else {
                        compactCalendarView.hideCalendarWithAnimation();
                    }
                    shouldShow = !shouldShow;
                }
            }
        };
    }

    private void openCalendarOnCreate(View v) {
        final RelativeLayout layout = v.findViewById(R.id.main_content);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                compactCalendarView.showCalendarWithAnimation();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        // Set to current day on resume to set calendar to latest day
        // toolbar.setTitle(dateFormatForMonth.format(new Date()));
    }

    private void loadEvents() {
        addEvents(-1, -1);
        addEvents(Calendar.DECEMBER, -1);
        addEvents(Calendar.AUGUST, -1);
    }

    private void loadEventsForYear(int year) {
//        for(int i = 0; i <12; i++){
//            addEvents(i, year);
//        }
        addEvents(3, 2021);
//        addEvents(Calendar.DECEMBER, year);
//        addEvents(Calendar.AUGUST, year);
    }

    private void logEventsByMonth(CompactCalendarView compactCalendarView) {
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, Calendar.AUGUST);
        List<String> dates = new ArrayList<>();
        for (Event e : compactCalendarView.getEventsForMonth(new Date())) {
            dates.add(dateFormatForDisplaying.format(e.getTimeInMillis()));
        }
        Log.d("hi", "Events for Aug with simple date formatter: " + dates);
        Log.d("hi", "Events for Aug month using default local and timezone: " + compactCalendarView.getEventsForMonth(calendar.getTime()));
    }

    private void addEvents(int month, int year) {
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = calendar.getTime();
        calendar.setTime(firstDayOfMonth);
        if (month > -1) {
            calendar.set(Calendar.MONTH, month);
        }
        if (year > -1) {
            calendar.set(Calendar.ERA, GregorianCalendar.AD);
            calendar.set(Calendar.YEAR, year);
        }
        calendar.add(Calendar.DATE, 0);
        setToMidnight(calendar);
        long timeInMillis = calendar.getTimeInMillis();
        getEvents(timeInMillis, 0);
        //List<Event> events = getEvents(timeInMillis, 0);

        //compactCalendarView.addEvents(events);
//        for (int i = 0; i < 6; i++) {
//            calendar.setTime(firstDayOfMonth);
//            if (month > -1) {
//                calendar.set(Calendar.MONTH, month);
//            }
//            if (year > -1) {
//                calendar.set(Calendar.ERA, GregorianCalendar.AD);
//                calendar.set(Calendar.YEAR, year);
//            }
//            calendar.add(Calendar.DATE, i);
//            setToMidnight(calendar);
//            long timeInMillis = calendar.getTimeInMillis();
//
//            List<Event> events = getEvents(timeInMillis, i);
//
//            compactCalendarView.addEvents(events);
//        }
    }

    private void getEvents(long timeInMillis, int day) {
        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        List<Event> addedEvents = new ArrayList<>();
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("Events");
        eventsRef.child(username)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot != null) {
                            Log.d("numberEntries", String.valueOf(snapshot.getChildrenCount()));
                            for(DataSnapshot child:snapshot.getChildren()){
                                int color = 0;
                                long time = 0;
                                String data = "";
                                for(DataSnapshot child2 : child.getChildren()){
                                    //Log.d("child3", child3.toString());
                                    if(child2.getKey().equals("color")){
                                        color = Integer.parseInt(child2.getValue().toString());
                                    }
                                    else if(child2.getKey().equals("data")){
                                        data = child2.getValue().toString();
                                    }
                                    else if(child2.getKey().equals("timeInMillis")){
                                        time = Long.parseLong(child2.getValue().toString());
                                    }
                                }
                                Event e = new Event(color, time, data);
                                addedEvents.add(e);
                                Log.d("here", "adding an event from firebase " + String.valueOf(e));
                            }
                            Log.d("numberEventsInside", String.valueOf(addedEvents.size()));
                            compactCalendarView.addEvents(addedEvents);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

//        List<Event> results = new ArrayList<>();
//
//        for(Event e: addedEvents){
//            if(((int) (e.getTimeInMillis() / (1000*60*60*24))) == (int) (timeInMillis / (1000*60*60*24))){
//                results.add(e);
//                Log.d("added event", e.getData().toString());
//            }
//        }
//        Event newEvent = new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis));
//        addedEvents.add(newEvent);
//        Log.d("manualAdd", String.valueOf(newEvent));
        //Log.d("numberEvents", String.valueOf(addedEvents.size()));
        //compactCalendarView.addEvents(addedEvents);
        //return addedEvents;
    }


    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}