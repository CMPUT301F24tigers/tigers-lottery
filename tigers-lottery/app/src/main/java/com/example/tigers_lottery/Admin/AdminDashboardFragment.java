package com.example.tigers_lottery.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tigers_lottery.Admin.DashboardFragments.AdminEntrantProfilesFragment;
import com.example.tigers_lottery.Admin.DashboardFragments.AdminEventsFragment;
import com.example.tigers_lottery.Admin.DashboardFragments.AdminFacilitiesFragment;
import com.example.tigers_lottery.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that displays the main dashboard for admin users.
 * This fragment provides a list of options for admin users, such as viewing entrant profiles,
 * facility profiles, and all events. Selecting an option opens the respective fragment.
 */
public class AdminDashboardFragment extends Fragment {

    /**
     * Inflates the admin dashboard layout and initializes the list of admin actions.
     *
     * @param inflater           LayoutInflater to inflate the layout.
     * @param container          Parent view that this fragment's UI should attach to.
     * @param savedInstanceState Saved state for restoring fragment state.
     * @return The view for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_dashboard_fragment, container, false);

        ListView listView = view.findViewById(R.id.adminListView);

        List<AdminListItemModel> items = new ArrayList<>();
        items.add(new AdminListItemModel("Entrant Profiles", 271));
        items.add(new AdminListItemModel("Facility Profiles", 23));
        items.add(new AdminListItemModel("All Events", 31));

        AdminListAdapter adapter = new AdminListAdapter(requireContext(), items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            AdminListItemModel item = items.get(position);
            Fragment selectedFragment = null;

            switch (item.getTitle()) {
                case "Entrant Profiles":
                    selectedFragment = new AdminEntrantProfilesFragment();
                    break;
                case "Facility Profiles (for final checkpoint)":
                    selectedFragment = new AdminFacilitiesFragment();
                    break;
                case "All Events":
                    selectedFragment = new AdminEventsFragment();
                    break;
            }

            if (selectedFragment != null) {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView2, selectedFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}