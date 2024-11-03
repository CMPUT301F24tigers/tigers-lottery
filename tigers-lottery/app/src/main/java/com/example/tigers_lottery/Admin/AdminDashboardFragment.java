package com.example.tigers_lottery.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tigers_lottery.Admin.DashboardFragments.AdminEntrantsProfilesFragment;
import com.example.tigers_lottery.Admin.DashboardFragments.AdminFacilitiesFragment;
import com.example.tigers_lottery.Admin.DashboardFragments.AdminEventsFragment;
import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;

/**
 * Fragment that displays the main dashboard for admin users.
 * This fragment provides buttons for admin users to view entrant profiles,
 * facility profiles, and all events. The number of users (entrant profiles)
 * is dynamically fetched from the database and displayed on the button.
 */
public class AdminDashboardFragment extends Fragment {

    /**
     * Inflates the admin dashboard layout and sets up button listeners for
     * navigating to various admin-specific screens. The number of users is
     * dynamically fetched and displayed on the Entrant Profiles button.
     *
     * @param inflater           LayoutInflater to inflate the fragment layout.
     * @param container          Parent view that this fragment's UI should attach to.
     * @param savedInstanceState Bundle for restoring fragment state.
     * @return The view for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_dashboard_fragment, container, false);

        Button btnEntrantProfiles = view.findViewById(R.id.btnEntrantProfiles);
        Button btnFacilityProfiles = view.findViewById(R.id.btnFacilityProfiles);
        Button btnAllEvents = view.findViewById(R.id.btnAllEvents);

        btnEntrantProfiles.setText("Entrant Profiles (Loading...)");

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.getUserCount(new DatabaseHelper.UserCountCallback() {
            @Override
            public void onUserCountFetched(int count) {
                btnEntrantProfiles.setText("Entrant Profiles (" + count + ")");
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to retrieve user count", Toast.LENGTH_SHORT).show();
                btnEntrantProfiles.setText("Entrant Profiles (Error)");
            }
        });

        btnEntrantProfiles.setOnClickListener(v -> openFragment(new AdminEntrantsProfilesFragment()));
        btnFacilityProfiles.setOnClickListener(v -> openFragment(new AdminFacilitiesFragment()));
        btnAllEvents.setOnClickListener(v -> openFragment(new AdminEventsFragment()));

        return view;
    }

    /**
     * Replaces the current fragment with the provided fragment and adds the transaction to the back stack.
     *
     * @param fragment The fragment to display in the fragment container.
     */
    private void openFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView2, fragment)
                .addToBackStack(null)
                .commit();
    }
}