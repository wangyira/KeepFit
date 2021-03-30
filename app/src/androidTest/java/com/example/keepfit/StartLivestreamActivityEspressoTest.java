package com.example.keepfit;

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

public class StartLivestreamActivityEspressoTest {

    private String title = "Yoga Stretch Livestream";
    private int ppl = 15;
    private String type = "Aerobic";
    private String time = "23:59";
    private String zoom = "https://usc.zoom.us/j/9184535088";

    @Rule
    public ActivityTestRule<StartLivestreamActivity> mainActivityTestRule = new ActivityTestRule<StartLivestreamActivity>(StartLivestreamActivity.class);

    @Test
    public void startLivestreamSession() {
        Espresso.onView(withId(R.id.livestream_title)).perform(typeText(title));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.livestream_num_people)).perform(typeText(Integer.toString(ppl)));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.endTime)).perform(typeText(time));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.zoom)).perform(typeText(zoom));
        Espresso.closeSoftKeyboard();

        LivestreamMember member = new LivestreamMember();;
        member.setTitle(title);
        member.setMaxNumberOfPeople(ppl);
        member.setExerciseType(type);
        member.setEndTime(time);
        member.setZoomLink(zoom);

        Espresso.onView(withText(title)).check(matches(withText(member.getTitle())));
        Espresso.onView(withText(Integer.toString(ppl))).check(matches(withText(Integer.toString(member.getMaxNumberOfPeople()))));
        Espresso.onView(withText(type)).check(matches(withText(member.getExerciseType())));
        Espresso.onView(withText(time)).check(matches(withText(member.getEndTime())));
        Espresso.onView(withText(zoom)).check(matches(withText(member.getZoomLink())));
        Espresso.onView(withId(R.id.save_button)).perform(click());
    }
}