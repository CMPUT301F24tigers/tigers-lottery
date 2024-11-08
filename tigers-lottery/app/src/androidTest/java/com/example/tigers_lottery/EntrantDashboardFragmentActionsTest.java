package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.is;

import android.widget.EditText;

import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EntrantDashboardFragmentActionsTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);
    private FragmentManager fragmentManager;


    @Before
    public void setUp() {
        ActivityScenario<MainActivity> scenario = activityScenarioRule.getScenario();
        scenario.onActivity(activity -> fragmentManager = activity.getSupportFragmentManager());
    }

    @Test
    public void testAJoinWaitlist(){
        onView(withId(R.id.navigation_entrant)).perform(click());
        String testEvent = "28523";
        onView(withId(R.id.join_event_button)).perform(click());

        onView(withText("Enter an eventId")).check(matches(isDisplayed()));

        onView(withClassName(is(EditText.class.getName()))).perform(typeText(testEvent));

        onView(withText("Ok")).perform(click());

    }

    @Test
    public void testBGetEventDetails() throws InterruptedException {
        onView(withId(R.id.navigation_entrant)).perform(click());
        Thread.sleep(2000);
        onView(withText("EntrantDashboardTestEvent")).perform(ViewActions.click());
        onView(withId(R.id.eventDetailsTextViewName)).check(matches(isDisplayed()));
        onView(withId(R.id.eventDetailsBackButton)).check(matches(isDisplayed()));

    }

    @Test
    public void testCLeaveWaitingList() throws InterruptedException {
        onView(withId(R.id.navigation_entrant)).perform(click());
        Thread.sleep(2000);
        onView(withText("EntrantDashboardTestEvent")).perform(ViewActions.click());
        Thread.sleep(2000);
        onView(withId(R.id.eventDetailsButton)).perform(click());
        onView(withText("Confirmation")).check(matches(isDisplayed()));
        onView(withText("Proceed")).perform(click());
    }
}
