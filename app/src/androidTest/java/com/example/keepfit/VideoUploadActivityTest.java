package com.example.keepfit;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;
import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import org.junit.Rule;

import java.util.HashMap;
import java.util.Map;

public class VideoUploadActivityTest {
    String title = "yoga video";
    String time = "0:30";
    String difficulty = "Beginner";
    String tag = "Aerobic";

    @Rule
    public ActivityTestRule<VideoUploadActivity> mainActivityTestRule = new ActivityTestRule<VideoUploadActivity>(VideoUploadActivity.class);

    @Test
    public void upload() {
        Espresso.onView(withId(R.id.titleInput)).perform(typeText(title));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.editTextTime2)).perform(typeText(time)); //optional time
        Espresso.closeSoftKeyboard();

        Map<String, String> vidRef = new HashMap<String, String>();
        vidRef.put("time", time);
        vidRef.put("difficulty", difficulty);
        vidRef.put("tag", tag);
        vidRef.put("title", title);

        Espresso.onView(withText(title)).check(matches(withText(vidRef.get("title"))));
        Espresso.onView(withText(time)).check(matches(withText(vidRef.get("time"))));
        Espresso.onView(withText(tag)).check(matches(withText(vidRef.get("tag"))));
        Espresso.onView(withText(difficulty)).check(matches(withText(vidRef.get("difficulty"))));

        Espresso.onView(withId(R.id.uploadVideoButton)).perform(click());
    }
}