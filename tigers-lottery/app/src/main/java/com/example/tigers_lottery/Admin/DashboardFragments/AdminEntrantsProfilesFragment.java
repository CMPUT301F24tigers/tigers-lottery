package com.example.tigers_lottery.Admin.DashboardFragments;
// AdminEntrantsProfilesFragment.java
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tigers_lottery.Admin.AdminDashboardFragment;
import com.example.tigers_lottery.Admin.AdminRecyclerViewAdapter;
import com.example.tigers_lottery.Admin.AdminRecyclerViewAdapter;
import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.User;
import java.util.ArrayList;
import java.util.List;

public class AdminEntrantsProfilesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminRecyclerViewAdapter userAdapter;
    private List<User> userList = new ArrayList<>(); // Placeholder for user data

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_entrants_profiles_fragment, container, false);

        ImageButton backButton = requireActivity().findViewById(R.id.globalBackButton);
        backButton.setVisibility(View.VISIBLE);

        // Set click listener for back button
        backButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView2, new AdminDashboardFragment())
                    .commit();
            backButton.setVisibility(View.INVISIBLE);
        });

        recyclerView = view.findViewById(R.id.recyclerViewEntrantProfiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter with empty list or fetched data
        userAdapter = new AdminRecyclerViewAdapter(userList);
        recyclerView.setAdapter(userAdapter);

        // Fetch users from database and update UI
        fetchUsers();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ImageButton backButton = requireActivity().findViewById(R.id.globalBackButton);
        backButton.setVisibility(View.INVISIBLE);
    }

    private void fetchUsers() {
        DatabaseHelper dbHelper = new DatabaseHelper();

        dbHelper.fetchAllUsers(new DatabaseHelper.UsersCallback() {
            @Override
            public void onUsersFetched(List<User> users) {
                userList.clear();
                userList.addAll(users);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                // Handle error (e.g., show a toast or log the error)
            }
        });
    }
}