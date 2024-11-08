package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.tigers_lottery.HostedEvents.OrganizerDashboardFragment;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
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
        onView(withId(R.id.inputEventName)).perform(typeText("0000A - Event Running Test"));
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
        onView(withText("0000A - Event Running Test")).perform(ViewActions.click());
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
        onView(withText("0000A - Event Running Test")).perform(ViewActions.click());

        onView(withId(R.id.viewRegisteredEntrants)).perform(click());
        onView(withId(R.id.registeredEntrantsTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.registeredEntrantsRecyclerView)).check(matches((withEffectiveVisibility(GONE))));
    }

    @Test
    public void testDWaitlistedEntrantsTest() {
        onView(withId(R.id.navigation_organizer)).perform(click());
        onView(withText("0000A - Event Running Test")).perform(ViewActions.click());

        onView(withId(R.id.viewWaitlistedEntrants)).perform(click());
        onView(withId(R.id.waitlistedEntrantsTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.waitlistedEntrantsRecyclerView)).check(matches(withEffectiveVisibility(GONE)));
    }

    @Test
    public void testEInvitedEntrantsTest() {
        onView(withId(R.id.navigation_organizer)).perform(click());
        onView(withText("0000A - Event Running Test")).perform(ViewActions.click());
        onView(withId(R.id.viewInvitedEntrants)).perform(click());
        onView(withId(R.id.invitedEntrantsTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.invitedEntrantsRecyclerView)).check(matches(withEffectiveVisibility(GONE)));
    }

    @Test
    public void testFEditEvent() {
        onView(withId(R.id.navigation_organizer)).perform(click());
        onView(atPositionOnView(0,R.id.optionsMenu)).perform(click());
        onView(withText("Edit")).perform(click());
        onView(withId(R.id.inputEventLocation)).perform(clearText());
        onView(withId(R.id.inputEventLocation)).perform(typeText("Brand New Event Location"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.inputEventDescription)).perform(scrollTo());
    }

    @Test
    public void testGDeleteEvent() {
        onView(withId(R.id.navigation_organizer)).perform(click());
        onView(atPositionOnView(0,R.id.optionsMenu)).perform(click());
        onView(withText("Delete")).perform(click());
    }

    public static Matcher<View> atPositionOnView(final int position, final int targetViewId) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("at position " + position + " on view with id " + targetViewId);
            }

            @Override
            public boolean matchesSafely(View view) {
                View rootView = view.getRootView();
                RecyclerView recyclerView = rootView.findViewById(R.id.eventsRecyclerView);
                if (recyclerView == null || recyclerView.getAdapter() == null) {
                    return false;
                }
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    return false;
                }

                View targetView = viewHolder.itemView.findViewById(targetViewId);
                return view.equals(targetView);
            }
        };
    }


}