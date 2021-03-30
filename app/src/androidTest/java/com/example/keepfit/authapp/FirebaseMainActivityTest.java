package com.example.keepfit.authapp;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import com.example.keepfit.MainActivity;
import com.example.keepfit.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

import static androidx.test.espresso.intent.Intents.intended;

import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

public class FirebaseMainActivityTest {
//    @Rule
//    public ActivityScenarioRule<FirebaseMainActivity> activityRule =
//            new ActivityScenarioRule<>(FirebaseMainActivity.class);

    @Rule
    public ActivityTestRule<FirebaseMainActivity> mainActivityTestRule = new ActivityTestRule<FirebaseMainActivity>(FirebaseMainActivity.class);

    /*
    @Rule
    public IntentsTestRule<FirebaseMainActivity> intentsTestRule =
            new IntentsTestRule<>(FirebaseMainActivity.class);


    @Test
    public void listGoesOverTheFold() {
//        onView(withText("Hello world!")).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.welcome)).check(matches(withText("Welcome !")));
    }
    */


    @Test
    public void LogInProperly() {
//        onView(withText("Hello world!")).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.userEmail)).perform(typeText("kaitlyn@usc.edu"));
        Espresso.onView(withId(R.id.password)).perform(typeText("kaitlyn"));

        Espresso.onView(withId(R.id.loginUser)).perform(click());

        try {
            Thread.sleep(5000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        Espresso.onView(withId(R.id.TextViewUsername)).check(matches(withText(containsString("kaitlyn"))));

        //Espresso.onView(withId(R.id.editprofile)).check(matches(isDisplayed()));

        //intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void RegisterProperly() {
//        onView(withText("Hello world!")).check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.register)).perform(click());

        try {
            Thread.sleep(3000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        Espresso.onView(withId(R.id.username)).perform(typeText("testuser1"));
        Espresso.onView(withId(R.id.password)).perform(typeText("testuser1"));
        Espresso.onView(withId(R.id.email)).perform(typeText("testuser1@gmail.com"));

        Espresso.onView(withId(R.id.registerUser)).perform(click());

        try {
            Thread.sleep(3000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        Espresso.onView(withId(R.id.createprofile)).check(matches(isDisplayed()));

        //Espresso.onView(withId(R.id.editprofile)).check(matches(isDisplayed()));

        //intended(hasComponent(MainActivity.class.getName()));
    }


    @Test
    public void FinishPersonalProfile() {
//        onView(withText("Hello world!")).check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.register)).perform(click());

        try {
            Thread.sleep(3000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        Espresso.onView(withId(R.id.username)).perform(typeText("testuser2"));
        Espresso.onView(withId(R.id.password)).perform(typeText("testuser2"));
        Espresso.onView(withId(R.id.email)).perform(typeText("testuser2@gmail.com"));

        Espresso.onView(withId(R.id.registerUser)).perform(click());

        try {
            Thread.sleep(8000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        Espresso.onView(withId(R.id.EditTextName)).perform(typeText("Test User"));

        Espresso.onView(withId(R.id.EditTextPhoneNo)).perform(typeText("1234567890"));

        Espresso.onView(withId(R.id.EditTextGender)).perform(typeText("Male"));

        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());

        Espresso.onView(withId(R.id.EditTextBirthday)).perform(typeText("09232000"));

        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());

        Espresso.onView(withId(R.id.EditTextWeight)).perform(typeText("120"));

        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());

        Espresso.onView(withId(R.id.EditTextHeight)).perform(typeText("70"));

        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());

        Espresso.onView(withId(R.id.btnSave)).perform(click());

        try {
            Thread.sleep(5000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        Espresso.onView(withId(R.id.searchButton)).check(matches(isDisplayed()));


    }

    @Test
    public void LoginLogout() {
//        onView(withText("Hello world!")).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.userEmail)).perform(typeText("ritika10@usc.edu"));
        Espresso.onView(withId(R.id.password)).perform(typeText("ritika"));

        Espresso.onView(withId(R.id.loginUser)).perform(click());

        try {
            Thread.sleep(5000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        Espresso.onView(withId(R.id.btnlogout)).perform(click());

        try {
            Thread.sleep(5000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        Espresso.onView(withId(R.id.loginUser)).check(matches(isDisplayed()));

        //intended(hasComponent(MainActivity.class.getName()));
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