package com.example.tigers_lottery;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tigers_lottery.Admin.AdminDashboardFragment;
import com.example.tigers_lottery.HostedEvents.OrganizerDashboardFragment;
import com.example.tigers_lottery.JoinedEvents.EntrantDashboardFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private FragmentManager fragmentManager;

    @Before
    public void setUp() {
        ActivityScenario<MainActivity> scenario = activityScenarioRule.getScenario();
        scenario.onActivity(activity -> fragmentManager = activity.getSupportFragmentManager());
    }

    @Test
    public void testAdminFragmentDisplayed() {
        // Trigger navigation to AdminDashboardFragment
        onView(withId(R.id.navigation_admin)).perform(click());

        // Check if AdminDashboardFragment is displayed
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_activity_fragment_container);
        assertTrue(fragment instanceof AdminDashboardFragment);
    }

    @Test
    public void testOrganizerFragmentDisplayed() {
        // Trigger navigation to OrganizerDashboardFragment
        onView(withId(R.id.navigation_organizer)).perform(click());

        // Check if OrganizerDashboardFragment is displayed
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_activity_fragment_container);
        assertTrue(fragment instanceof OrganizerDashboardFragment);
    }

    @Test
    public void testEntrantFragmentDisplayed() {
        // Trigger navigation to EntrantDashboardFragment
        onView(withId(R.id.navigation_entrant)).perform(click());

        // Check if EntrantDashboardFragment is displayed
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_activity_fragment_container);
        assertTrue(fragment instanceof EntrantDashboardFragment);
    }
}