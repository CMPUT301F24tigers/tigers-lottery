package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tigers_lottery.HostedEvents.OrganizerDeclinedEntrantsFragment;
import com.example.tigers_lottery.HostedEvents.OrganizerMapFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OrganizerMapFragmentTest {
    @Before
    public void setUp() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            Fragment fragment = new OrganizerMapFragment();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_fragment_container, fragment)
                    .commitNow();
        });
    }
    @Test
    public void testViewsAreDisplayed() {
        onView(withId(R.id.mapView)).check(matches(isDisplayed()));
        onView(withId(R.id.btnLegend)).check(matches(isDisplayed()));
    }
}
