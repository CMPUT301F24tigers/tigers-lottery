package com.example.tigers_lottery;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.example.tigers_lottery.Admin.DashboardFragments.AdminEntrantsProfilesFragment;
import com.example.tigers_lottery.Admin.DashboardFragments.AdminEventsFragment;
import com.example.tigers_lottery.Admin.DashboardFragments.AdminFacilitiesFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AdminDashboardFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private FragmentManager fragmentManager;

    @Before
    public void setUp() {
        ActivityScenario<MainActivity> scenario = activityScenarioRule.getScenario();
        scenario.onActivity(activity -> fragmentManager = activity.getSupportFragmentManager());
        onView(withId(R.id.navigation_admin)).perform(click());
    }

    @Test
    public void testEntrantProfilesFragmentDisplayed() {
        onView(withText("Entrant Profiles")).perform(click());
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainerView2);
        assertTrue(fragment instanceof AdminEntrantsProfilesFragment);
    }

    @Test
    public void testFacilityProfilesFragmentDisplayed() {
        onView(withText("Facility Profiles")).perform(click());
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainerView2);
        assertTrue(fragment instanceof AdminFacilitiesFragment);
    }

    @Test
    public void testAllEventsFragmentDisplayed() {
        onView(withText("All Events")).perform(click());
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainerView2);
        assertTrue(fragment instanceof AdminEventsFragment);
    }
}