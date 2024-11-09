package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class OrganizerDashboardFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testDisplay() {
        // Click on the "Hosted Events" navigation item to reach the OrganizerDashboardFragment
        onView(withId(R.id.navigation_organizer)).perform(click());

        // Check if the RecyclerView in the OrganizerDashboardFragment is displayed
        onView(withId(R.id.eventsRecyclerView)).check(matches(isDisplayed()));
    }

    @Test

    public void testSearchFunctionality () {

        onView(withId(R.id.navigation_organizer)).perform(click());

        onView(withId(R.id.searchView)).perform(click());

        onView(withId(R.id.searchView)).check(matches(isDisplayed()));

    }
}


