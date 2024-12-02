package com.example.tigers_lottery;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tigers_lottery.ProfileViewsEdit.ProfileDetailsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileEditUserFragmentTest {

    @Rule
    public ActivityScenarioRule<ProfileDetailsActivity> activityScenarioRule =
            new ActivityScenarioRule<>(ProfileDetailsActivity.class);

    @Test
    public void testProfileEditUserFragmentLoads() {
        // Navigate to ProfileDetailsUserFragment
        onView(withId(R.id.navigation_entrant_profile)).perform(click());

        // Click on the "Edit Profile" button to navigate to ProfileEditUserFragment
        onView(withId(R.id.editUserProfileButton)).perform(click());

        // Verify that the ProfileEditUserFragment is displayed
        onView(withId(R.id.profileEditFacilityFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void testUIComponentsAreDisplayed() {
        // Navigate to ProfileDetailsUserFragment
        onView(withId(R.id.navigation_entrant_profile)).perform(click());

        // Click on the "Edit Profile" button to navigate to ProfileEditUserFragment
        onView(withId(R.id.editUserProfileButton)).perform(click());

        // Verify that all UI components are displayed
        onView(withId(R.id.editTextUserProfileFirstName)).check(matches(isDisplayed())); // First name field
        onView(withId(R.id.editTextUserProfileLastName)).check(matches(isDisplayed()));  // Last name field
        onView(withId(R.id.editTextUserProfileEmail)).check(matches(isDisplayed()));     // Email field
        onView(withId(R.id.editTextUserProfileDOB)).check(matches(isDisplayed()));       // DOB field
        onView(withId(R.id.editTextUserProfileMobile)).check(matches(isDisplayed()));    // Mobile field
        onView(withId(R.id.checkboxNotificationsEnabled)).check(matches(isDisplayed())); // Notifications checkbox
        onView(withId(R.id.userEditProfilePhoto)).check(matches(isDisplayed()));         // Profile photo
        onView(withId(R.id.editUserProfilePhoto)).check(matches(isDisplayed()));         // Edit photo button
        onView(withId(R.id.removeUserProfilePictureButton)).check(matches(isDisplayed())); // Remove photo button
        onView(withId(R.id.saveChangesUserProfileButton)).check(matches(isDisplayed())); // Save changes button
    }

    @Test
    public void testEditProfileFunctionality() {
        // Navigate to ProfileDetailsUserFragment
        onView(withId(R.id.navigation_entrant_profile)).perform(click());

        // Click on the "Edit Profile" button to navigate to ProfileEditUserFragment
        onView(withId(R.id.editUserProfileButton)).perform(click());

        // Verify the "Edit User Profile" button works correctly
        onView(withId(R.id.editUserProfilePhoto)).check(matches(isDisplayed()));
        onView(withId(R.id.removeUserProfilePictureButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testSaveProfileEdits() {
        // Navigate to ProfileDetailsUserFragment
        onView(withId(R.id.navigation_entrant_profile)).perform(click());

        // Click on the "Edit Profile" button to navigate to ProfileEditUserFragment
        onView(withId(R.id.editUserProfileButton)).perform(click());

        // Verify Save Button Click
        onView(withId(R.id.saveChangesUserProfileButton)).perform(click());

        // Ensure Save Navigates Back
        onView(withId(R.id.profileDetailsActivityFragment)).check(matches(isDisplayed()));
    }
}
