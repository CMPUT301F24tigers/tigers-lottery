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
 * Fragment that displays a list of user profiles within the admin dashboard.
 * This fragment enables the admin to view, remove profile photos, and delete user profiles.
 */
public class AdminEntrantsProfilesFragment extends Fragment implements OnActionListener {

    private AdminRecyclerViewAdapter userAdapter;
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
        title.setText("All Entrants");

        RecyclerView recyclerView = view.findViewById(R.id.adminRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userAdapter = new AdminRecyclerViewAdapter(itemList, this);
        recyclerView.setAdapter(userAdapter);
        fetchUsers();
        return view;
    }

    /**
     * Fetches all users from the database, populating the RecyclerView with user data.
     * Excludes the current user from the displayed list of users.
     */
    private void fetchUsers() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.fetchAllUsers(new DatabaseHelper.UsersCallback() {
            @Override
            public void onUsersFetched(List<User> users) {
                itemList.clear();
                for (User user : users) {
                    if (!Objects.equals(user.getUserId(), dbHelper.getCurrentUserId())) {
                        String userPhoto = user.getUserPhoto();

                        // Use placeholder if photo is null or empty
                        if (userPhoto == null || userPhoto.isEmpty()) {
                            userPhoto = "placeholder_user_image";
                        }

                        // Add the user to the list and set isEvent to false
                        itemList.add(new AdminListItemModel(
                                user.getUserId(),
                                user.getFirstName() + " " + user.getLastName(),
                                user.getEmailAddress(),
                                userPhoto,
                                "View User Profile",
                                "Remove User Profile"
                                 false
                        ));
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onUserFetched(User user) {
                // Not implemented as it is not needed in this context.
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to retrieve all users", Toast.LENGTH_SHORT).show();
                Log.e("DatabaseHelper", "Error retrieving users", e);
            }
        });
    }

    /**
     * Handles the action to remove a profile photo for a specific user.
     * This method is triggered when the "Remove Profile Photo" option is selected.
     *
     * @param userId The ID of the user whose profile photo will be removed.
     */
    @Override
    public void onOptionOneClick(String userId) {
        AdminUserDetailsFragment userDetailsFragment = AdminUserDetailsFragment.newInstance(userId);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment_container, userDetailsFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Handles the action to view a specific user's profile.
     * This method is triggered when the "View User Profile" option is selected.
     *
     * @param userId The ID of the user whose profile will be viewed.
     */
    @Override
    public void onOptionTwoClick(String userId) {

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.getUser(userId, new DatabaseHelper.UserCallback() {
            @Override
            public void onUserFetched(User user) {
                Toast.makeText(getContext(), "Removing user:  " + user.getFirstName() + " " + user.getLastName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                //do nothing
            }
        });
        dbHelper.removeUser(userId);
        userAdapter.setExpandedPosition(-1);
        userAdapter.notifyDataSetChanged();
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