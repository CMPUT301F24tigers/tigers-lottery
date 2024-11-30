package com.example.tigers_lottery;

import android.content.Intent;
import android.os.Bundle;

import com.example.tigers_lottery.Admin.AdminDashboardFragment;
import com.example.tigers_lottery.HostedEvents.OrganizerDashboardFragment;
import com.example.tigers_lottery.JoinedEvents.EntrantDashboardFragment;
import com.example.tigers_lottery.Notifications.NotificationDashboardFragment;
import com.example.tigers_lottery.models.User;
import com.example.tigers_lottery.utils.DeviceIDHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.tigers_lottery.ProfileViewsEdit.ProfileDetailsActivity;

import java.util.List;

/**
 * The main activity that acts as a central navigation hub, allowing users to switch between
 * different dashboards (Admin, Entrant, Organizer) using a bottom navigation bar.
 * Users can also access their profile details from this screen.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Button to open the Profile Details Activity.
     */
    ImageButton editProfile;
    private DatabaseHelper dbHelper;
    private TextView notificationBadge;

    /**
     * Called when the activity is first created. Sets up the UI components,
     * checks if the user's profile exists, and configures the bottom navigation bar.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the most recent data, otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        String deviceId = DeviceIDHelper.getDeviceId(this);
        editProfile = findViewById(R.id.profileButton);
        ImageButton notificationButton = findViewById(R.id.notificationButton);
        notificationBadge = findViewById(R.id.notificationBadge);
        notificationButton.setVisibility(View.GONE);

        dbHelper = new DatabaseHelper(this);

        // BottomNavigationView setup
        BottomNavigationView bottomNav = findViewById(R.id.nav_view);
        MenuItem adminMenuItem = bottomNav.getMenu().findItem(R.id.navigation_admin);
        getWindow().setStatusBarColor(0xFF2A334C);


        // Check if the user profile exists; if not, navigate to CreateEntrantProfileActivity
        dbHelper.checkUserExists(new DatabaseHelper.ProfileCallback() {
            @Override
            public void onProfileExists() {
                // Profile exists; no action needed
            }

            @Override
            public void onProfileNotExists() {
                Intent createEntrantProfileActivity = new Intent(getApplicationContext(), CreateEntrantProfileActivity.class);
                startActivity(createEntrantProfileActivity);
            }
        });

        // Initially hide the admin menu item
        if (adminMenuItem != null) {
            adminMenuItem.setVisible(false);
        }

        dbHelper.fetchUserById(deviceId, new DatabaseHelper.UsersCallback() {
            @Override
            public void onUsersFetched(List<User> users) {
                //Do nothing
            }

            @Override
            public void onUserFetched(User user) {
                if(user.isNotificationFlag()){
                    notificationButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Exception e) {
                //Do nothing
            }
        });


        // Check if the device ID exists in the admins collection
        dbHelper.isAdminUser(deviceId, new DatabaseHelper.VerificationCallback() {
            @Override
            public void onResult(boolean exists) {
                Log.e("DatabaseHelper", "Is Admin User: " + exists);

                if (adminMenuItem != null && exists) {
                    adminMenuItem.setVisible(true);
                }

                Log.e("DatabaseHelper", "Admin status verification complete.");
            }

            @Override
            public void onError(Exception e) {
                Log.e("DatabaseHelper", "Error verifying Admin Status", e);
            }
        });

        loadFragment(new EntrantDashboardFragment());

        // Set up listener for bottom navigation to switch between fragments
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.navigation_admin) {
                    selectedFragment = new AdminDashboardFragment();
                } else if (item.getItemId() == R.id.navigation_entrant) {
                    selectedFragment = new EntrantDashboardFragment();
                } else if (item.getItemId() == R.id.navigation_organizer) {
                    selectedFragment = new OrganizerDashboardFragment();
                }
                return loadFragment(selectedFragment);
            }
        });

        // Set listener for profile edit button to navigate to ProfileDetailsActivity
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileDetailsActivity = new Intent(getApplicationContext(), ProfileDetailsActivity.class);
                startActivity(profileDetailsActivity);
            }
        });

        // Handle notification button click
        notificationButton.setOnClickListener(view -> loadFragment(new NotificationDashboardFragment()));

        setupNotificationBadgeListener();

    }

    /**
     * Sets up a real-time listener for unread notification count updates.
     */
    private void setupNotificationBadgeListener() {
        String userId = dbHelper.getCurrentUserId();
        dbHelper.listenForUnreadNotifications(userId, new DatabaseHelper.NotificationCountCallback() {
            @Override
            public void onCountFetched(int count) {
                runOnUiThread(() -> updateNotificationBadge(count));
            }

            @Override
            public void onError(Exception e) {
                //Log.e("Error listening for unread notifications");
            }
        });
    }

    /**
     * Updates the notification badge with the unread notification count.
     *
     * @param count The number of unread notifications.
     */
    private void updateNotificationBadge(int count) {
        if (count > 0) {
            notificationBadge.setVisibility(View.VISIBLE);
            notificationBadge.setText(String.valueOf(count));
        } else {
            notificationBadge.setVisibility(View.GONE);
        }
    }

    /**
     * Replaces the current fragment in the activity's container with the specified fragment.
     *
     * @param fragment The fragment to display.
     * @return true if the fragment was successfully loaded, false otherwise.
     */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_activity_fragment_container, fragment)
                    .addToBackStack(null) // Add this line to manage the back stack
                    .commit();
            return true;
        }
        return false;
    }


}
