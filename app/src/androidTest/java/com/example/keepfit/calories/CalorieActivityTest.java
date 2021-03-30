package com.example.keepfit.calories;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import com.example.keepfit.R;

import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

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
    public void uploadExerciseTimeToCalculateCalorie() {
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