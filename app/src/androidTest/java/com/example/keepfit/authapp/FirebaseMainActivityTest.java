package com.example.keepfit.authapp;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;
import com.example.keepfit.R;

public class FirebaseMainActivityTest {
//    @Rule
//    public ActivityScenarioRule<FirebaseMainActivity> activityRule =
//            new ActivityScenarioRule<>(FirebaseMainActivity.class);

    @Rule
    public ActivityTestRule<FirebaseMainActivity> mainActivityTestRule = new ActivityTestRule<FirebaseMainActivity>(FirebaseMainActivity.class);

    @Test
    public void listGoesOverTheFold() {
//        onView(withText("Hello world!")).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.welcome)).check(matches(withText("Welcome !")));
    }

//    @Before
//    public void setUp() throws Exception {
//    }
//
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void onCreate() {
//    }
//
//    @Test
//    public void onClick() {
//    }
}