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
 * Fragment that displays the main dashboard for admin users.
 * This fragment provides buttons for admin users to view entrant profiles,
 * facility profiles, and all events. The number of users (entrant profiles)
 * is dynamically fetched from the database and displayed on the button.
 */

public class AdminDashboardFragment extends Fragment {

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
                int adjustedCount = count - 1;
                btnEntrantProfiles.setText("Entrant Profiles (" + adjustedCount + ")");
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to retrieve user count", Toast.LENGTH_SHORT).show();
                btnEntrantProfiles.setText("Entrant Profiles (Error)");
            }
        });

        // Navigate to respective fragments when buttons are clicked
        btnEntrantProfiles.setOnClickListener(v -> openFragment(AdminEntrantsProfilesFragment.newInstance()));
        btnFacilityProfiles.setOnClickListener(v -> openFragment(new AdminFacilitiesFragment()));
        btnAllEvents.setOnClickListener(v -> openFragment(new AdminEventsFragment()));

        return view;
    }

    private void openFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}