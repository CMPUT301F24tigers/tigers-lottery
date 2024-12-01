package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tigers_lottery.Admin.DashboardFragments.AdminFacilitiesFragment;
import com.example.tigers_lottery.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminFacilitiesFragmentTest {

    private void launchAdminFacilitiesFragment() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            Fragment fragment = new AdminFacilitiesFragment();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_fragment_container, fragment)
                    .commitNow();
        });
    }

    @Test
    public void testRecyclerViewIsDisplayed() {
        // Launch the fragment
        launchAdminFacilitiesFragment();

        // Verify that the RecyclerView is displayed
        onView(withId(R.id.adminRecyclerView)).check(matches(isDisplayed()));
    }




}
