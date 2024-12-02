package com.example.tigers_lottery.Admin.DashboardFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tigers_lottery.Admin.AdminRecyclerViewAdapter;
import com.example.tigers_lottery.Admin.DashboardFragments.ListItems.AdminListItemModel;
import com.example.tigers_lottery.Admin.DashboardFragments.ListItems.OnActionListener;
import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fragment that displays a list of facilities within the admin dashboard.
 * This fragment enables the admin to view facility details or remove facility profiles
 * associated with user accounts.
 */
public class AdminFacilitiesFragment extends Fragment implements OnActionListener {

    private AdminRecyclerViewAdapter facilityAdapter;
    private final List<AdminListItemModel> itemList = new ArrayList<>();

    /**
     * Inflates the layout for this fragment and initializes the RecyclerView with an adapter.
     *
     * @param inflater LayoutInflater to inflate views in the fragment.
     * @param container The parent view that this fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-created from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_list_fragment, container, false);

        TextView title = view.findViewById(R.id.adminListTile);
        title.setText("All Facilities");

        RecyclerView recyclerView = view.findViewById(R.id.adminRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        facilityAdapter = new AdminRecyclerViewAdapter(itemList, this);
        recyclerView.setAdapter(facilityAdapter);
        fetchFacilities();
        return view;
    }

    /**
     * Fetches all facilities from the database, populating the RecyclerView with facility data.
     * Excludes the current user and users with incomplete facility details.
     * Facilities are only displayed if:
     * - The user is not the current user.
     * - Facility name, email, and location fields are not empty.
     */
    private void fetchFacilities() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        dbHelper.fetchAllUsers(new DatabaseHelper.UsersCallback() {
            /**
             * Handles actions on finding all users.
             * @param users the app's users.
             */
            @Override
            public void onUsersFetched(List<User> users) {
                itemList.clear();
                for (User user : users) {
                    if (!Objects.equals(user.getUserId(), dbHelper.getCurrentUserId())
                            && !Objects.equals(user.getFacilityName(), "")
                            && !Objects.equals(user.getFacilityEmail(), "")
                            && !Objects.equals(user.getFacilityLocation(), "")) {
                        itemList.add(new AdminListItemModel(
                                user.getUserId(),
                                user.getFacilityName(),
                                "by " + user.getFirstName() + " " + user.getLastName(),
                                user.getFacilityPhoto(),
                                "View Facility",
                                "Remove Facility",
                                false
                        ));
                    }
                }
                facilityAdapter.notifyDataSetChanged();
            }

            /**
             * Handles actions on finding a specific user, required db helper method, unused.
             * @param user user found.
             */

            @Override
            public void onUserFetched(User user) {
                // Not implemented as it is not needed in this context.
            }

            /**
             * Handles error on retrieving all facilities.
             * @param e exception catcher.
             */

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to retrieve all facilities", Toast.LENGTH_SHORT).show();
                Log.e("DatabaseHelper", "Error retrieving facilities", e);
            }
        });
    }

    /**
     * Navigates to the AdminFacilityDetailsFragment to view detailed information
     * about a specific facility. This method is triggered when the "View Facility" option is selected.
     *
     * @param userId The ID of the user whose facility details will be viewed.
     */
    @Override
    public void onOptionOneClick(String userId) {
        AdminFacilityDetailsFragment facilityDetailsFragment = AdminFacilityDetailsFragment.newInstance(userId);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment_container, facilityDetailsFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Removes the facility profile associated with a user. This method is triggered when
     * the "Remove Facility" option is selected. It clears facility-related fields for the user
     * (e.g., facility name, email, location, phone, and photo) and updates the user's record in the database.
     *
     * @param userId The ID of the user whose facility profile will be removed.
     */
    @Override
    public void onOptionTwoClick(String userId) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        dbHelper.getUser(userId, new DatabaseHelper.UserCallback() {
            /**
             * Handles action on finding specific user, deletes the facility.
             * @param user user associated with the facility.
             */
            @Override
            public void onUserFetched(User user) {
                Toast.makeText(getContext(), "Removing Facility:  " + user.getFacilityName(), Toast.LENGTH_SHORT).show();

                // Update the user in the database
                dbHelper.removeFacility(user, new DatabaseHelper.Callback() {
                    /**
                     * Handles success of the remove facility action
                     * @param message pushed to logs.
                     */
                    @Override
                    public void onSuccess(String message) {
                        Log.d("AdminFacilityFragment", "The Facility profile has been deleted successfully");
                    }

                    /**
                     * Handles failure of the remove facility action.
                     * @param e pushed to logs.
                     */

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("AdminFacilityFragment", "The Facility profile was not deleted: " + e.getMessage());
                    }
                });
            }

            /**
             * Handles error on finding user.
             * @param e exception catcher.
             */

            @Override
            public void onError(Exception e) {
                Log.e("AdminFacilityFragment", "Error fetching user: " + e.getMessage(), e);
            }
        });

        // Collapse expanded RecyclerView item and refresh the adapter
        facilityAdapter.setExpandedPosition(-1);
        facilityAdapter.notifyDataSetChanged();
    }

    /**
     * Creates a new instance of the AdminEntrantsProfilesFragment.
     *
     * @return A new instance of AdminEntrantsProfilesFragment.
     */
    public static AdminEntrantsProfilesFragment newInstance() {
        return new AdminEntrantsProfilesFragment();
    }
}