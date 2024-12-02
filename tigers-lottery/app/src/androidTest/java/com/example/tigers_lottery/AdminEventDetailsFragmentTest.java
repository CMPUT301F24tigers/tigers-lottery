package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.tigers_lottery.Admin.DashboardFragments.AdminEventDetailsFragment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AdminEventDetailsFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private void launchAdminEventDetailsFragment() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            Fragment fragment = new AdminEventDetailsFragment();
            Bundle args = new Bundle();
            args.putString("event_id", "10350");
            fragment.setArguments(args);
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_fragment_container, fragment)
                    .commitNow();
        });
    }

    @Test
    public void testAdminEventDetailsViewsDisplayed() throws InterruptedException {
        // Launch the AdminEventDetailsFragment
        launchAdminEventDetailsFragment();

        Thread.sleep(2000);

        // Verify that views are displayed
        onView(withId(R.id.eventTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.eventDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.eventLocation)).check(matches(isDisplayed()));
        onView(withId(R.id.eventDate)).check(matches(isDisplayed()));
        onView(withId(R.id.waitlistOpenDate)).check(matches(isDisplayed()));
        onView(withId(R.id.waitlistCloseDate)).check(matches(isDisplayed()));
        onView(withId(R.id.waitlistLimit)).check(matches(isDisplayed()));
        onView(withId(R.id.entrantLimit)).check(matches(isDisplayed()));
        onView(withId(R.id.organizerName)).check(matches(isDisplayed()));
        onView(withId(R.id.eventPoster)).check(matches(isDisplayed()));
    }


}
