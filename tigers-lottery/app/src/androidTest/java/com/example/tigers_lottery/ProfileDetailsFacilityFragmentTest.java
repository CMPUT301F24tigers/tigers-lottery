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
public class ProfileDetailsFacilityFragmentTest {

    @Rule
    public ActivityScenarioRule<ProfileDetailsActivity> activityScenarioRule =
            new ActivityScenarioRule<>(ProfileDetailsActivity.class);

    /**
     * Test to verify that the ProfileDetailsFacilityFragment is displayed by default.
     */
    @Test
    public void testFacilityFragmentDefaultDisplay() {
        // Check if a unique view in ProfileDetailsFacilityFragment is displayed
        onView(withId(R.id.facilityName)).check(matches(isDisplayed()));
    }

    /**
     * Test to verify the navigation when the edit profile button is clicked.
     */
    @Test
    public void testEditProfileButtonNavigation() throws InterruptedException {
        // Click on the edit profile button


        Thread.sleep(2000);




        onView(withId(R.id.editProfileButton)).perform(click());

        // Check if the ProfileEditFacilityFragment is displayed (using a unique view ID in the fragment)
        onView(withId(R.id.profileEditFacilityFragment)).check(matches(isDisplayed()));
    }
}
