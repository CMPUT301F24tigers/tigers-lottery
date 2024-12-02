package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tigers_lottery.Admin.DashboardFragments.AdminFacilityDetailsFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminFacilityDetailsFragmentTest {

    private void launchAdminFacilityDetailsFragment(String userId) {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            // Create and launch the AdminFacilityDetailsFragment with a user ID
            Fragment fragment = AdminFacilityDetailsFragment.newInstance(userId);
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_fragment_container, fragment)
                    .commitNow();
        });
    }

    @Test
    public void testViewsAreDisplayed() throws InterruptedException {
        // Launch the fragment with a mock user ID
        launchAdminFacilityDetailsFragment("7fe4f49610b443dc");


        Thread.sleep(2000);

        // Verify that all expected views are displayed
        onView(withId(R.id.eventTitle)).check(matches(isDisplayed())); // Facility Name
        onView(withId(R.id.eventLocation)).check(matches(isDisplayed())); // Facility Email
        onView(withId(R.id.waitlistOpenDate)).check(matches(isDisplayed())); // Facility Phone
        onView(withId(R.id.waitlistCloseDate)).check(matches(isDisplayed())); // Facility Location
        onView(withId(R.id.eventDate)).check(matches(isDisplayed())); // Facility Owner*/
        onView(withId(R.id.eventPoster)).check(matches(isDisplayed())); // Facility Photo
    }


}
