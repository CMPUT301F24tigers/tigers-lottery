package com.example.tigers_lottery;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import com.example.tigers_lottery.HostedEvents.OrganizerInvitedEntrantsFragment;
import com.example.tigers_lottery.MainActivity;
import com.example.tigers_lottery.R;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;


import android.os.Bundle;


@RunWith(AndroidJUnit4.class)
public class OrganizerInvitedEntrantsFragmentTest {


    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);


    private FragmentManager fragmentManager;


    @Before
    public void setUp() {
        ActivityScenario<MainActivity> scenario = activityScenarioRule.getScenario();
        scenario.onActivity(activity -> fragmentManager = activity.getSupportFragmentManager());
    }


    @Test
    public void testOrganizerInvitedEntrantsFragmentDisplayed() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            // Create an instance of OrganizerInvitedEntrantsFragment with an event ID argument
            OrganizerInvitedEntrantsFragment fragment = new OrganizerInvitedEntrantsFragment();
            Bundle args = new Bundle();
            args.putInt("event_id", 123); // Example event ID
            fragment.setArguments(args);


            // Launch the fragment
            fragmentManager.beginTransaction()
                    .replace(R.id.main_activity_fragment_container, fragment)
                    .commitNow();


            // Check if OrganizerInvitedEntrantsFragment is displayed c
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.main_activity_fragment_container);
            assertTrue(currentFragment instanceof OrganizerInvitedEntrantsFragment);
        });


        // Add additional Espresso checks if necessary to verify UI behavior, such as:
        // onView(withId(R.id.invitedEntrantsRecyclerView)).check(matches(isDisplayed()));
    }
}


