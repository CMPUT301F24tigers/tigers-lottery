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

import com.example.tigers_lottery.Admin.DashboardFragments.AdminUserDetailsFragment;
import com.example.tigers_lottery.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminUserDetailsFragmentTest {

    /**
     * Helper method to launch the `AdminUserDetailsFragment` with a userId.
     *
     * @param userId The ID of the user to be displayed.
     */
    private void launchAdminUserDetailsFragment(String userId) {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            Fragment fragment = AdminUserDetailsFragment.newInstance(userId);
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_fragment_container, fragment)
                    .commitNow();
        });
    }

    /**
     * Test to verify that all the views in the fragment are displayed correctly.
     */
    @Test
    public void testViewsAreDisplayed() throws InterruptedException {
        // Launch the fragment with a mock user ID
        launchAdminUserDetailsFragment("af7d33e43aac34ba");

        Thread.sleep(2000);

        // Verify that all views are displayed
        onView(withId(R.id.eventTitle)).check(matches(isDisplayed())); // User Name
        onView(withId(R.id.eventLocation)).check(matches(isDisplayed())); // User Email
        onView(withId(R.id.waitlistOpenDate)).check(matches(isDisplayed())); // User DOB
        onView(withId(R.id.waitlistCloseDate)).check(matches(isDisplayed())); // User Mobile
        onView(withId(R.id.eventPoster)).check(matches(isDisplayed())); // User Photo
    }

    /**
     * Test to verify the "Remove Profile Photo" button functionality.
     */

}
