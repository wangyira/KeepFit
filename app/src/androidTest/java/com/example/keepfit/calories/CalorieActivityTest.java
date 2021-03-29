package com.example.keepfit.calories;

import android.util.Log;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import com.example.keepfit.authapp.FirebaseMainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import com.example.keepfit.R;

import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class CalorieActivityTest {
    private String time = "20:00";
    private double calorieCount;
    private Integer weight = 100; //in pounds
    private Integer runMET = 3;

    @Rule
    public ActivityTestRule<CalorieActivity> mainActivityTestRule = new ActivityTestRule<CalorieActivity>(CalorieActivity.class);

    @Test
    public void onCreate() {
        Espresso.onView(withId(R.id.editTextTime)).perform(typeText(time));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.submit2)).perform(click());

        String[] myParsedString = time.split(":");
        double myMinutes = (Integer.parseInt(myParsedString[0]));
        double mySeconds = (Integer.parseInt(myParsedString[1]));

        //calculate calorie
        calorieCount = (weight / 2.205) * runMET * ((myMinutes * 60 + mySeconds) / 3600.0);
        String calorieStr = Double.toString(calorieCount);

        BigDecimal bd = new BigDecimal(calorieStr);
        double myTotalCalories = bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
        String myDisplayValue = Double.toString(myTotalCalories);

        Espresso.onView(withId(R.id.TotalCalories)).check(matches(withText(containsString(myDisplayValue))));
    }
}