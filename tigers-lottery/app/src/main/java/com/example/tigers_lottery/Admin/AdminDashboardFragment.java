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

import com.example.tigers_lottery.Admin.DashboardFragments.*;
import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;

/**
 * The AdminDashboardFragment class displays the main dashboard for admin users.
 * This fragment provides buttons for admin users to view entrant profiles,
 * facility profiles, and all events. The button text for entrant profiles
 * and all events dynamically updates with counts fetched from the database.
 */
public class AdminDashboardFragment extends Fragment {

    /**
     * Inflates the layout for the admin dashboard and sets up button listeners.
     * Fetches and displays counts for entrant profiles and events on respective buttons.
     *
     * @param inflater           LayoutInflater for inflating the fragment layout.
     * @param container          The container that this fragment will be attached to.
     * @param savedInstanceState Bundle for restoring fragment state.
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_dashboard_fragment, container, false);

        Button btnEntrantProfiles = view.findViewById(R.id.btnEntrantProfiles);
        Button btnFacilityProfiles = view.findViewById(R.id.btnFacilityProfiles);
        Button btnAllEvents = view.findViewById(R.id.btnAllEvents);

        btnEntrantProfiles.setText("Entrant Profiles (Loading...)");
        btnAllEvents.setText("All Events (Loading...)");
        btnFacilityProfiles.setText("All Facilities (Loading...)");

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        // Fetch entrant profile count and update button
        dbHelper.getUserCount(new DatabaseHelper.CountCallback() {
            @Override
            public void onCountFetched(int count) {
                int adjustedCount = count - 1;
                btnEntrantProfiles.setText("Entrant Profiles (" + adjustedCount + ")");
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to retrieve user count", Toast.LENGTH_SHORT).show();
                btnEntrantProfiles.setText("Entrant Profiles (Error)");
            }
        });

        // Fetch event count and update button
        dbHelper.getEventCount(new DatabaseHelper.CountCallback() {
            @Override
            public void onCountFetched(int count) {
                btnAllEvents.setText("All Events (" + count + ")");
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to retrieve event count", Toast.LENGTH_SHORT).show();
                btnAllEvents.setText("All Events (Error)");
            }
        });

        // Fetch Active Facility Count and update the button
        dbHelper.getFacilityCount(new DatabaseHelper.CountCallback() {
            @Override
            public void onCountFetched(int count) {
                btnFacilityProfiles.setText("All Facilities (" + count + ")");
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to retrieve facility count", Toast.LENGTH_SHORT).show();
                btnAllEvents.setText("All Facilities (Error)");
            }
        });

        // Set click listeners to navigate to the respective fragments
        btnEntrantProfiles.setOnClickListener(v -> openFragment(AdminEntrantsProfilesFragment.newInstance()));
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
                .replace(R.id.main_activity_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}