package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class EntrantDashboardFragmentTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testDisplay() {
        onView(withId(R.id.navigation_entrant)).perform(click());
        // Check if the RecyclerView in the EntrantDashboardFragment is displayed
        onView(withId(R.id.scrollViewEntrants)).check(matches(isDisplayed()));
    }
    @Test
    public void testButtonDisplay () {

        onView(withId(R.id.navigation_entrant)).perform(click());

        onView(withId(R.id.join_event_button)).check(matches(isDisplayed()));

    }
}

