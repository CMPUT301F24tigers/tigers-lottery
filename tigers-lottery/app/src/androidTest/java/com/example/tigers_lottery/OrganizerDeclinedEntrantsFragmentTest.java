package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tigers_lottery.HostedEvents.OrganizerCreateEventFragment;
import com.example.tigers_lottery.HostedEvents.OrganizerDeclinedEntrantsFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

@RunWith(AndroidJUnit4.class)
public class OrganizerDeclinedEntrantsFragmentTest {
    @Before
    public void setUp() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            Fragment fragment = new OrganizerDeclinedEntrantsFragment();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_fragment_container, fragment)
                    .commitNow();
        });
    }
    @Test
    public void testViewsAreDisplayed() {
        onView(withId(R.id.declinedEntrantsTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.fabSendNotifications)).check(matches(isDisplayed()));
        onView(withId(R.id.noEntrantsMessage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

}