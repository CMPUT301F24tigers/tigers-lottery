package com.example.tigers_lottery.Admin.DashboardFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class AdminEntrantsProfilesFragment extends Fragment implements OnActionListener {

    private AdminRecyclerViewAdapter userAdapter;
    private final List<AdminListItemModel> itemList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_list_fragment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.adminRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userAdapter = new AdminRecyclerViewAdapter(itemList, this);
        recyclerView.setAdapter(userAdapter);
        fetchUsers();
        return view;
    }

    private void fetchUsers() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.fetchAllUsers(new DatabaseHelper.UsersCallback() {
            @Override
            public void onUsersFetched(List<User> users) {
                itemList.clear();
                for (User user : users) {
                    if (!Objects.equals(user.getUserId(), dbHelper.getCurrentUserId())) {
                        itemList.add(new AdminListItemModel(
                                user.getUserId(),
                                user.getFirstName() + " " + user.getLastName(),
                                user.getUserId(), //TODO Chathila: Change this to email later
                                "Remove Profile Photo",
                                "View User Profile",
                                "Remove User"
                        ));
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onUserFetched( User user){
                //Do nothing
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to retrieve all users", Toast.LENGTH_SHORT).show();
                Log.e("DatabaseHelper", "Error retrieving users", e);
            }
        });
    }

    // Implement the action listener methods for handling button clicks
    @Override
    public void onOptionOneClick(String userId) {
        // Handle "Remove Profile Photo" action for the specified user
        // Example: Call a function to remove the user's profile photo from the database
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        Toast.makeText(getContext(), "Removing profile photo for user " + userId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOptionTwoClick(String userId) {
        // Handle "View User Profile" action
        // Example: Navigate to the user profile detail view
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        Toast.makeText(getContext(), "Viewing profile for user " + userId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOptionThreeClick(String userId) {
        // Handle "Remove User" action
        // Example: Call a function to delete the user from the database
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.removeUser(userId);
        userAdapter.setExpandedPosition(-1);
        userAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Removing user " + userId, Toast.LENGTH_SHORT).show();
    }

    public static AdminEntrantsProfilesFragment newInstance() {
        return new AdminEntrantsProfilesFragment();
    }
}