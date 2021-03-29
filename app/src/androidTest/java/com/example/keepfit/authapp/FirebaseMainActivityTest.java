//package com.example.keepfit.authapp;
//
//import androidx.test.espresso.Espresso;
//
//import org.junit.Rule;
//import org.junit.Test;
//
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import androidx.test.rule.ActivityTestRule;
//
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//import static org.junit.Assert.*;
//import com.example.keepfit.R;
//
//public class FirebaseMainActivityTest {
//    @Rule
//    public ActivityTestRule<FirebaseMainActivity> mainActivityTestRule = new ActivityTestRule<FirebaseMainActivity>(FirebaseMainActivity.class);
//
//    @Test
//    public void listGoesOverTheFold() {
//        Espresso.onView(withId(R.id.welcome)).check(matches(withText("Welcome !")));
//    }
//
//}