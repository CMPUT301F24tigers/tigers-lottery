package com.example.tigers_lottery.Admin;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.tigers_lottery.Admin.DashboardFragments.AdminEntrantsProfilesFragment;
import com.example.tigers_lottery.Admin.DashboardFragments.AdminFacilitiesFragment;
import com.example.tigers_lottery.Admin.DashboardFragments.AdminEventsFragment;
import com.example.tigers_lottery.R;

/**
 * Fragment that displays the main dashboard for admin users.
 * This fragment provides buttons for admin users to view entrant profiles,
 * facility profiles, and all events.
 */
public class AdminDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_dashboard_fragment, container, false);

        Button btnEntrantProfiles = view.findViewById(R.id.btnEntrantProfiles);
        Button btnFacilityProfiles = view.findViewById(R.id.btnFacilityProfiles);
        Button btnAllEvents = view.findViewById(R.id.btnAllEvents);

        btnEntrantProfiles.setOnClickListener(v -> openFragment(new AdminEntrantsProfilesFragment()));
        btnFacilityProfiles.setOnClickListener(v -> openFragment(new AdminFacilitiesFragment()));
        btnAllEvents.setOnClickListener(v -> openFragment(new AdminEventsFragment()));

        return view;
    }

    private void openFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView2, fragment)
                .addToBackStack(null)
                .commit();
    }
}