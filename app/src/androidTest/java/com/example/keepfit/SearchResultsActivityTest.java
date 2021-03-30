package com.example.keepfit;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.rule.ActivityTestRule;

import com.example.keepfit.calories.CalorieActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

public class SearchResultsActivityTest {
    String exercise1 = "Body Project Exercise Video";
    String exercise2 = "no metadata test 1";
    String livestream = "stretch livestream";
    String livestream2 = "workout with me";
    String profile1 = "kaitlyn";
    String profile2 = "ritikadendi10";

    @Rule
    public ActivityTestRule<SearchResultsActivity> mainActivityTestRule = new ActivityTestRule<SearchResultsActivity>(SearchResultsActivity.class);


    @Test
    public void Videos() {

        Espresso.onView(withId(R.id.searchBar)).perform(typeText(exercise1));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.searchButton)).perform(click());
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withId(R.id.item1text)).check(matches(withText(containsString(exercise1))));

        Espresso.onView(withId(R.id.searchBar)).perform(replaceText(exercise2));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.searchButton)).perform(click());
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withId(R.id.item1text)).check(matches(withText(containsString(exercise2))));
    }

    @Test
    public void Livestream() {

        Espresso.onView(withId(R.id.searchBar)).perform(typeText(livestream));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.searchButton)).perform(click());
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withId(R.id.item1text)).check(matches(withText(containsString(livestream))));

        Espresso.onView(withId(R.id.searchBar)).perform(replaceText(livestream2));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.searchButton)).perform(click());
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withId(R.id.item1text)).check(matches(withText(containsString(livestream2))));
    }

    @Test
    public void Profile() {
        Espresso.onView(withId(R.id.searchBar)).perform(typeText(profile1));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.searchButton)).perform(click());
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withId(R.id.searchButton)).perform(click());
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withId(R.id.item1text)).check(matches(withText(containsString(profile1))));

        Espresso.onView(withId(R.id.searchBar)).perform(replaceText(profile2));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.searchButton)).perform(click());
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withId(R.id.searchButton)).perform(click());
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withId(R.id.item1text)).check(matches(withText(containsString(profile2))));
    }

    @Test
    public void Like() {
        Espresso.onView(withId(R.id.searchBar)).perform(typeText(exercise1));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.searchButton)).perform(click());
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withId(R.id.item1like)).perform(click());

        Espresso.onView(withId(R.id.nav_account)).perform(click());

        Espresso.onView(withText("View Liked Videos")).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.viewLikedVideos)).perform(click());

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Espresso.onView(withId(R.id.item1txt)).check(matches(withText(containsString(exercise1))));

    }

    @Test
    public void Dislike() {
        Espresso.onView(withId(R.id.searchBar)).perform(typeText(exercise2));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.searchButton)).perform(click());
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withId(R.id.item1dislike)).perform(click());

        Espresso.onView(withId(R.id.nav_account)).perform(click());

        Espresso.onView(withText("View Disliked Videos")).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.viewDisliked)).perform(click());

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Espresso.onView(withId(R.id.ditem1txt)).check(matches(withText(containsString(exercise2))));

    }

    @Test
    public void ViewUser() {
        Espresso.onView(withId(R.id.searchBar)).perform(replaceText(profile1));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.searchButton)).perform(click());
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withId(R.id.searchButton)).perform(click());

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Espresso.onView(withId(R.id.item1button)).perform(click());

        Espresso.onView(withId(R.id.publicProfileUsername)).check(matches(withText(containsString(profile1))));
    }

    @Test
    public void ViewExerciseType() {
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Espresso.onView(withId(R.id.preset1)).perform(click());
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withId(R.id.preset1)).perform(click());
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withId(R.id.item1button)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.item1text)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.item1username)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.item1like)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.item1dislike)).check(matches(isDisplayed()));
    }

}