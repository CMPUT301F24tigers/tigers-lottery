package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.tigers_lottery.HostedEvents.OrganizerDashboardFragment;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrganizerDashboardFragmentActionsTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);
    private FragmentManager fragmentManager;

    @Before
    public void setUp() {
        ActivityScenario<MainActivity> scenario = activityScenarioRule.getScenario();
        scenario.onActivity(activity -> fragmentManager = activity.getSupportFragmentManager());
    }

    @Test
    public void testACreateEventTest() {
        onView(withId(R.id.navigation_organizer)).perform(click());
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_activity_fragment_container);
        assertTrue(fragment instanceof OrganizerDashboardFragment);
        onView(withId(R.id.fabCreateEvent)).perform(click());
        onView(withId(R.id.inputEventName)).perform(typeText("Test Running Event"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.inputEventLocation)).perform(typeText("Home"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.inputRegistrationOpens)).perform(typeText("2024-12-21"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.inputRegistrationDeadline)).perform(typeText("2024-12-26"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.inputEventDate)).perform(typeText("2024-12-29"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.inputEventDescription)).perform(typeText("Delete this event after test finishes."));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.inputOccupantLimit)).perform(typeText("50"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.checkboxWaitlistLimit)).perform(click());
        onView(withId(R.id.inputWaitlistLimit)).check(matches(isDisplayed()));
        onView(withId(R.id.inputWaitlistLimit)).perform(typeText("100"));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.btnCreateEvent)).perform(click());
    }

    @Test
    public void testBEventDetailsTest() {
        onView(withId(R.id.navigation_organizer)).perform(click());
        onView(withText("Test Running Event")).perform(ViewActions.click());
        onView(withId(R.id.eventTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.photoPlaceholder)).check(matches(isDisplayed()));
        onView(withId(R.id.eventPoster)).check(matches(isDisplayed()));
        onView(withId(R.id.eventDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.eventLocation)).check(matches(isDisplayed()));
        onView(withId(R.id.waitlistOpenDate)).check(matches(isDisplayed()));
        onView(withId(R.id.waitlistCloseDate)).check(matches(isDisplayed()));
        onView(withId(R.id.eventDate)).check(matches(isDisplayed()));
        onView(withId(R.id.waitlistLimit)).check(matches(isDisplayed()));
        onView(withId(R.id.runLotteryButton)).check(matches(isDisplayed()));
        onView(withId(R.id.viewRegisteredEntrants)).check(matches(isDisplayed()));
        onView(withId(R.id.viewInvitedEntrants)).check(matches(isDisplayed()));
        onView(withId(R.id.viewDeclinedEntrants)).check(matches(isDisplayed()));

    }

    @Test
    public void testCRegisteredEntrantsTest() {
        onView(withId(R.id.navigation_organizer)).perform(click());
        onView(withText("Test Running Event")).perform(ViewActions.click());

        onView(withId(R.id.viewRegisteredEntrants)).perform(click());
        onView(withId(R.id.registeredEntrantsTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.registeredEntrantsRecyclerView)).check(matches((withEffectiveVisibility(GONE))));
    }

    @Test
    public void testDWaitlistedEntrantsTest() {
        onView(withId(R.id.navigation_organizer)).perform(click());
        onView(withText("Test Running Event")).perform(ViewActions.click());

        onView(withId(R.id.viewWaitlistedEntrants)).perform(click());
        onView(withId(R.id.waitlistedEntrantsTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.waitlistedEntrantsRecyclerView)).check(matches(withEffectiveVisibility(GONE)));
    }

    @Test
    public void testEInvitedEntrantsTest() {
        onView(withId(R.id.navigation_organizer)).perform(click());
        onView(withText("Test Running Event")).perform(ViewActions.click());
        onView(withId(R.id.viewInvitedEntrants)).perform(click());
        onView(withId(R.id.invitedEntrantsTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.invitedEntrantsRecyclerView)).check(matches(withEffectiveVisibility(GONE)));
    }


/*
    @Test
    public void deleteEvent(){
        onView(withId(R.id.navigation_organizer)).perform(click());

        onView(withId(R.id.eventsRecyclerView)).atPositionOnView(0, R.id.eventMenu)
                        .perform(click());
        onView(withText("Delete")).perform(ViewActions.click());
    }

}
*/
}
