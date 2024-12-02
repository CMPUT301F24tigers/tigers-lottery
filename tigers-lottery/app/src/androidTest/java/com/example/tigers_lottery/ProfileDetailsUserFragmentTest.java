package com.example.tigers_lottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tigers_lottery.ProfileViewsEdit.ProfileDetailsActivity;
import com.example.tigers_lottery.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileDetailsUserFragmentTest {

    @Rule
    public ActivityScenarioRule<ProfileDetailsActivity> activityScenarioRule =
            new ActivityScenarioRule<>(ProfileDetailsActivity.class);

    @Test
    public void testUserProfileDisplaysCorrectly() {
        // Verify that the user profile fragment is displayed
        onView(withId(R.id.profileDetailsActivityFragment)).check(matches(isDisplayed()));

        // Verify that key elements in the fragment are displayed
        onView(withId(R.id.userName)).check(matches(isDisplayed()));
        onView(withId(R.id.userEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.userMobile)).check(matches(isDisplayed()));
        onView(withId(R.id.userBirthday)).check(matches(isDisplayed()));
        onView(withId(R.id.userProfilePic)).check(matches(isDisplayed()));
    }

    @Test
    public void testEditProfileButtonNavigation() {
        // Click on the edit profile button
        onView(withId(R.id.editUserProfileButton)).perform(click());

        // Verify that the ProfileEditUserFragment is displayed
        onView(withId(R.id.profileEditFacilityFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void testAdminVerifyButtonVisibility() {
        // Verify that the admin verify button is displayed or hidden based on user status
        onView(withId(R.id.adminVerifyBtn)).check(matches(isDisplayed()));
    }


}
