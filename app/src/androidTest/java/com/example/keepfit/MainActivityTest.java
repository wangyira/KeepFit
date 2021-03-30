package com.example.keepfit;

import android.app.Instrumentation;
import android.content.Intent;

import org.hamcrest.Matcher;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import org.junit.Rule;


public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private String livestreamTitle = "workout with me";

    @Test
    public void onCreate() {
        try {
            Thread.sleep(3000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        onView(withId(R.id.item1button)).check(matches(isDisplayed()));
        onView(withId(R.id.item1text)).check(matches(isDisplayed()));
        onView(withId(R.id.item1username)).check(matches(isDisplayed()));
        onView(withId(R.id.item1like)).check(matches(isDisplayed()));
        onView(withId(R.id.item1dislike)).check(matches(isDisplayed()));
    }

    @Test
    public void openNewActivityVideo() {
        try {
            Thread.sleep(5000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        onView(withId(R.id.item1button)).perform(click());
        try {
            Thread.sleep(4000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        onView(withId(R.id.textViewVideo)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void openNewActivityLivestream() {
        onView(withId(R.id.searchBar)).perform(typeText(livestreamTitle));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.searchButton)).perform(click());

        onView(withId(R.id.item1text)).check(matches(withText(containsString(livestreamTitle))));

        Intents.init();
        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_VIEW), hasData("https://usc.zoom.us/j/9184535088"));
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        onView(withId(R.id.item1button)).perform(click());
        intended(expectedIntent);
        Intents.release();
    }
}