package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tigers_lottery.HostedEvents.OrganizerCreateEventFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OrganizerEditEventFragmentTest {
    @Before
    public void setUp() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            Fragment fragment = new OrganizerCreateEventFragment();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_fragment_container, fragment)
                    .commitNow();
        });
    }
    @Test
    public void testViewsAreDisplayed() {
        onView(withId(R.id.photoPlaceholder)).check(matches(isDisplayed()));
        onView(withId(R.id.photoPlaceholderText)).check(matches(isDisplayed()));
        onView(withId(R.id.inputEventName)).check(matches(isDisplayed()));
        onView(withId(R.id.inputEventLocation)).check(matches(isDisplayed()));
        onView(withId(R.id.checkboxGeolocationRequired)).check(matches(isDisplayed()));
        onView(withId(R.id.inputRegistrationOpens)).check(matches(isDisplayed()));
        onView(withId(R.id.inputRegistrationDeadline)).check(matches(isDisplayed()));
        onView(withId(R.id.inputEventDate)).check(matches(isDisplayed()));
        onView(withId(R.id.createEventScrollView)).perform(ViewActions.swipeUp());
        onView(withId(R.id.inputEventDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.checkboxWaitlistLimit)).check(matches(isDisplayed()));
        onView(withId(R.id.checkboxWaitlistLimit)).perform(ViewActions.click());
        onView(withId(R.id.createEventScrollView)).perform(ViewActions.swipeUp());
        onView(withId(R.id.inputWaitlistLimit)).check(matches(isDisplayed()));
        onView(withId(R.id.btnCreateEvent)).check(matches(isDisplayed()));
    }
}
