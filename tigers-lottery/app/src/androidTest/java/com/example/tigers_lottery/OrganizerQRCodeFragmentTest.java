package com.example.tigers_lottery;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tigers_lottery.HostedEvents.OrganizerQRCodeFragment;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class OrganizerQRCodeFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private FragmentManager fragmentManager;

    @Before
    public void setUp() {
        ActivityScenario<MainActivity> scenario = activityScenarioRule.getScenario();
        scenario.onActivity(activity -> fragmentManager = activity.getSupportFragmentManager());
    }

    @Test
    public void testOrganizerQRCodeFragmentDisplayed() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            // Launch OrganizerQRCodeFragment
            OrganizerQRCodeFragment fragment = new OrganizerQRCodeFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_activity_fragment_container, fragment)
                    .commitNow();

            // Check if OrganizerQRCodeFragment is displayed
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.main_activity_fragment_container);
            assertTrue(currentFragment instanceof OrganizerQRCodeFragment);
        });

        // Verify the button and image are displayed
        onView(withId(R.id.qrCodeRegenerateButton)).check(matches(isDisplayed()));
        onView(withId(R.id.qrCodeImage)).check(matches(isDisplayed()));
    }


}
