package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tigers_lottery.ProfileViewsEdit.ProfileDetailsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileDetailsActivityTest {

    @Rule
    public ActivityScenarioRule<ProfileDetailsActivity> activityScenarioRule =
            new ActivityScenarioRule<>(ProfileDetailsActivity.class);

    @Test
    public void testDefaultFragmentDisplay() {
        // Verify that the default user profile fragment is displayed
        onView(withId(R.id.profileDetailsActivityFragment)).check(matches(isDisplayed()));
    }


    @Test
    public void testCancelButtonNavigation() {
        // Click on the cancel button
        onView(withId(R.id.cancel_button)).perform(click());

        // Verify that the main activity is displayed
        onView(withId(R.id.main_activity_fragment_container)).check(matches(isDisplayed()));
    }
}
