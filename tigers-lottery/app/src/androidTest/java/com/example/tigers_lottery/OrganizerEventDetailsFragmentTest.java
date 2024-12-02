package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tigers_lottery.HostedEvents.OrganizerDeclinedEntrantsFragment;
import com.example.tigers_lottery.HostedEvents.OrganizerEventDetailsFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OrganizerEventDetailsFragmentTest {
    @Before
    public void setUp() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            Fragment fragment = new OrganizerEventDetailsFragment();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_fragment_container, fragment)
                    .commitNow();
        });
    }
    @Test
    public void testViewsAreDisplayed() {
        onView(withId(R.id.photoPlaceholder)).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionCardView)).check(matches(isDisplayed()));
        onView(withId(R.id.runLotteryButton)).check(matches(isDisplayed()));
        onView(withId(R.id.clearListsButton)).check(matches(isDisplayed()));
        onView(withId(R.id.viewQRCodeButton)).check(matches(isDisplayed()));
        onView(withId(R.id.viewMapButton)).check(matches(isDisplayed()));
        onView(withId(R.id.eventDetailsScrollView)).perform(ViewActions.swipeUp());
        onView(withId(R.id.viewRegisteredEntrants)).check(matches(isDisplayed()));
        onView(withId(R.id.viewDeclinedEntrants)).check(matches(isDisplayed()));
        onView(withId(R.id.viewWaitlistedEntrants)).check(matches(isDisplayed()));
        onView(withId(R.id.viewInvitedEntrants)).check(matches(isDisplayed()));

    }
}
