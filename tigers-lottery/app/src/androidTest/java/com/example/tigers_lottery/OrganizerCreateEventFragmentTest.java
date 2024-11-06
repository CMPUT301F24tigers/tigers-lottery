package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.Espresso;

import com.example.tigers_lottery.HostedEvents.OrganizerCreateEventFragment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class OrganizerCreateEventFragmentTest {


    @Test
    public void testDisplay(){
        FragmentScenario<OrganizerCreateEventFragment> scenario = FragmentScenario.launchInContainer(OrganizerCreateEventFragment.class);
        onView(withId(R.id.organizerCreateEventFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void testCreateEvent() {
        FragmentScenario<OrganizerCreateEventFragment> scenario = FragmentScenario.launchInContainer(OrganizerCreateEventFragment.class);

        onView(withId(R.id.inputEventName)).perform(typeText("Birthday Party"));
        onView(withId(R.id.inputEventLocation)).perform(typeText("Home"));
        onView(withId(R.id.inputRegistrationOpens)).perform(typeText("2024-11-21"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.inputRegistrationDeadline)).perform(typeText("2024-11-26"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.inputEventDate)).perform(typeText("2024-11-29"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.inputEventDescription)).perform(typeText("Celebrate my birthday with friends!"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.inputOccupantLimit)).perform(typeText("50"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.checkboxWaitlistLimit)).perform(click());
        onView(withId(R.id.inputWaitlistLimit)).check(matches(isDisplayed()));
        onView(withId(R.id.inputWaitlistLimit)).perform(typeText("100"));
        Espresso.closeSoftKeyboard();

    }




}
