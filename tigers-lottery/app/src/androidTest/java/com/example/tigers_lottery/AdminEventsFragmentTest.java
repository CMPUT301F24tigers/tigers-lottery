package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AdminEventsFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testRecyclerViewDisplay() throws InterruptedException {
        // Wait for a few seconds before performing actions
        Thread.sleep(2000);

        // Navigate to AdminEventsFragment
        onView(withId(R.id.navigation_admin)).perform(click());
        onView(withId(R.id.btnAllEvents)).perform(click());

        // Check if the RecyclerView in the AdminEventsFragment is displayed
        onView(withId(R.id.adminRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testTitleDisplay() throws InterruptedException {
        // Wait for a few seconds before performing actions
        Thread.sleep(2000);

        // Navigate to AdminEventsFragment
        onView(withId(R.id.navigation_admin)).perform(click());
        onView(withId(R.id.btnAllEvents)).perform(click());

        // Check if the title TextView is displayed and correct
        onView(withId(R.id.adminListTile)).check(matches(isDisplayed()));
        onView(withId(R.id.adminListTile)).check(matches(withText("All Events")));
    }



}
