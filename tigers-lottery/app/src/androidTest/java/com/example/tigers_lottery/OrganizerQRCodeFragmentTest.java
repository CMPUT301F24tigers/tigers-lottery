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

import com.example.tigers_lottery.HostedEvents.OrganizerDeclinedEntrantsFragment;
import com.example.tigers_lottery.HostedEvents.OrganizerQRCodeFragment;
import com.example.tigers_lottery.HostedEvents.OrganizerWaitingListFragment;

import org.junit.Before;
import org.junit.Test;

public class OrganizerQRCodeFragmentTest {
    @Before
    public void setUp() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            Fragment fragment = new OrganizerQRCodeFragment();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_fragment_container, fragment)
                    .commitNow();
        });
    }
    @Test
    public void testViewsAreDisplayed() {
        onView(withId(R.id.qrCodeDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.qrCodeText)).check(matches(isDisplayed()));
        onView(withId(R.id.qrCodeCardView)).check(matches(isDisplayed()));
        onView(withId(R.id.qrCodeRegenerateButton)).check(matches(isDisplayed()));
    }
}
