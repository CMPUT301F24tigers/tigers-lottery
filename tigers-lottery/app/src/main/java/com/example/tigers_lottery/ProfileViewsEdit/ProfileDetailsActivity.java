package com.example.tigers_lottery.ProfileViewsEdit;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.tigers_lottery.MainActivity;
import com.example.tigers_lottery.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * This activity displays user profile details and allows navigation between different profile views,
 * such as user and organizer profiles. It also provides a cancel button to return to the main activity.
 */
public class ProfileDetailsActivity extends AppCompatActivity {

    /**
     * Button to cancel and navigate back to the MainActivity.
     */
    ImageButton cancelButton;

    /**
     * Called when the activity is first created. Initializes the UI components, sets up the bottom
     * navigation for profile sections, and handles navigation item reselection to load specific fragments.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied, otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_details_activity);

        cancelButton = findViewById(R.id.cancel_button);
        loadFragment(new ProfileDetailsUserFragment()); // Load the default profile fragment

        BottomNavigationView bottomNavProfileDetails = findViewById(R.id.nav_profile_details_view);

        // Set up listener to handle reselection of items in the bottom navigation view
        bottomNavProfileDetails.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.navigation_entrant_profile) {
                    selectedFragment = new ProfileDetailsUserFragment();
                } else if (item.getItemId() == R.id.navigation_organizer_profile) {
                    selectedFragment = new ProfileDetailsFacilityFragment();
                }
                loadFragment(selectedFragment);
                return true;
            }
        });

        // Set up listener to handle cancel button click
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
            }
        });
    }

    /**
     * Loads the specified fragment into the fragment container in this activity.
     *
     * @param fragment The fragment to display in the activity's container.
     */
    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profileDetailsActivityFragment, fragment)
                    .commit();
        }
    }
}
