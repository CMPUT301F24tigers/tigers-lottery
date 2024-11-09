package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserProfileFragmentsActionsTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testAEditProfileTest(){
        onView(withId(R.id.navigation_entrant)).perform(click());
        onView(withId(R.id.profileButton)).perform(click());
        onView(withId(R.id.editUserProfileButton)).perform(click());
        onView(withId(R.id.editTextUserProfileFirstName)).perform(clearText(), typeText("Test"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextUserProfileLastName)).perform(clearText(), typeText("Test"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextUserProfileEmail)).perform(clearText(), typeText("test@testers.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextUserProfileDOB)).perform(click());
        onView(withText("OK")).perform(click());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextUserProfileMobile)).perform(clearText(), typeText("587-707-6428"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.saveChangesUserProfileButton)).perform(click());
    }

    @Test
    public void testBEditFacilityTest(){
        onView(withId(R.id.navigation_entrant)).perform(click());
        onView(withId(R.id.profileButton)).perform(click());
        onView(withId(R.id.navigation_organizer_profile)).perform(click());
        onView(withId(R.id.editProfileButton)).perform(click());
        onView(withId(R.id.editTextName)).perform(clearText(),typeText("Test Facility"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextEmail)).perform(clearText(),typeText("facility@gmail.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextMobile)).perform(clearText(), typeText("800-321-2343"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextLocation)).perform(clearText(),typeText("Test location"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.saveChangesButton)).perform(click());
    }

    @Test
    public void testCClearFieldsEntrantTest(){
        onView(withId(R.id.navigation_entrant)).perform(click());
        onView(withId(R.id.profileButton)).perform(click());
        onView(withId(R.id.editUserProfileButton)).perform(click());
        onView(withId(R.id.editTextUserProfileFirstName)).perform(clearText());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextUserProfileLastName)).perform(clearText());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextUserProfileEmail)).perform(clearText());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextUserProfileDOB)).perform(click());
        onView(withText("OK")).perform(click());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextUserProfileMobile)).perform(clearText());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.saveChangesUserProfileButton)).perform(click());
    }

    @Test
    public void testDClearFieldsFacilityTest(){
        onView(withId(R.id.navigation_entrant)).perform(click());
        onView(withId(R.id.profileButton)).perform(click());
        onView(withId(R.id.navigation_organizer_profile)).perform(click());
        onView(withId(R.id.editProfileButton)).perform(click());
        onView(withId(R.id.editTextName)).perform(clearText());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextEmail)).perform(clearText());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextMobile)).perform(clearText());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextLocation)).perform(clearText());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.saveChangesButton)).perform(click());
    }
}
