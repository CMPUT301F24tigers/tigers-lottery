package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tigers_lottery.ProfileViewsEdit.ProfileDetailsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileEditFacilityFragmentTest {

    @Rule
    public ActivityScenarioRule<ProfileDetailsActivity> activityScenarioRule =
            new ActivityScenarioRule<>(ProfileDetailsActivity.class);

    @Test
    public void testProfileEditFacilityFragmentLoads() {
        // Navigate to ProfileDetailsFacilityFragment
        onView(withId(R.id.navigation_organizer_profile)).perform(click());

        // Click on the "Edit Profile" button to load ProfileEditFacilityFragment
        onView(withId(R.id.editProfileButton)).perform(click());

        // Verify that ProfileEditFacilityFragment is loaded
        onView(withId(R.id.profileEditFacilityFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void testUIComponentsAreDisplayed() {
        // Navigate to ProfileDetailsFacilityFragment
        onView(withId(R.id.navigation_organizer_profile)).perform(click());

        // Click on the "Edit Profile" button to load ProfileEditFacilityFragment
        onView(withId(R.id.editProfileButton)).perform(click());

        // Verify that all UI components are displayed
        onView(withId(R.id.editTextName)).check(matches(isDisplayed())); // Name field
        onView(withId(R.id.editTextEmail)).check(matches(isDisplayed())); // Email field
        onView(withId(R.id.editTextMobile)).check(matches(isDisplayed())); // Mobile field
        onView(withId(R.id.editTextLocation)).check(matches(isDisplayed())); // Location field
        onView(withId(R.id.facilityEditProfilePhoto)).check(matches(isDisplayed())); // Profile photo
        onView(withId(R.id.editProfilePhoto)).check(matches(isDisplayed())); // Edit photo button
        onView(withId(R.id.removeFacilityProfilePictureButton)).check(matches(isDisplayed())); // Remove photo button
        onView(withId(R.id.saveChangesButton)).check(matches(isDisplayed())); // Save changes button
    }

    @Test
    public void testEditFieldsAndSaveChanges() {
        // Navigate to ProfileDetailsFacilityFragment
        onView(withId(R.id.navigation_organizer_profile)).perform(click());

        // Click on the "Edit Profile" button to load ProfileEditFacilityFragment
        onView(withId(R.id.editProfileButton)).perform(click());

        // Enter new values into the profile fields
        onView(withId(R.id.editTextName)).perform(replaceText("New Facility Name"));
        onView(withId(R.id.editTextEmail)).perform(replaceText("facility@example.com"));
        onView(withId(R.id.editTextMobile)).perform(replaceText("1234567890"));
        onView(withId(R.id.editTextLocation)).perform(replaceText("New Location"));

        // Verify that the entered text is correctly displayed
        onView(withId(R.id.editTextName)).check(matches(withText("New Facility Name")));
        onView(withId(R.id.editTextEmail)).check(matches(withText("facility@example.com")));
        onView(withId(R.id.editTextMobile)).check(matches(withText("1234567890")));
        onView(withId(R.id.editTextLocation)).check(matches(withText("New Location")));

        // Click the Save Changes button
        onView(withId(R.id.saveChangesButton)).perform(click());

        // Verify that the ProfileDetailsFacilityFragment is displayed after saving
        onView(withId(R.id.profileDetailsActivityFragment)).check(matches(isDisplayed()));
    }


}
