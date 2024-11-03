package com.example.tigers_lottery.Admin.DashboardFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tigers_lottery.Admin.AdminDashboardFragment;
import com.example.tigers_lottery.Admin.AdminRecyclerViewAdapter;
import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying the entrant profiles within the admin dashboard.
 * This fragment provides a RecyclerView to show a list of user profiles.
 * It includes functionality to navigate back to the main AdminDashboardFragment.
 */
public class AdminEntrantsProfilesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminRecyclerViewAdapter userAdapter;
    private List<User> userList = new ArrayList<>();

    /**
     * Inflates the layout for the entrant profiles screen, initializes the RecyclerView,
     * and sets up the back button to return to the AdminDashboardFragment.
     *
     * @param inflater           LayoutInflater for inflating the fragment layout.
     * @param container          The container this fragment belongs to.
     * @param savedInstanceState Saved state for restoring fragment state.
     * @return The View for the fragment's UI.
     */
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

        // Set up RecyclerView for displaying user profiles
        recyclerView = view.findViewById(R.id.recyclerViewEntrantProfiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userAdapter = new AdminRecyclerViewAdapter(userList);
        recyclerView.setAdapter(userAdapter);

        // Fetch users from the database
        fetchUsers();

        return view;
    }

    /**
     * Hides the back button when the fragment view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ImageButton backButton = requireActivity().findViewById(R.id.globalBackButton);
        backButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Fetches all users from the database and updates the RecyclerView.
     * Utilizes DatabaseHelper's fetchAllUsers method with a callback to handle results.
     * On success, the user list is updated and the adapter is notified.
     * On failure, an error message is displayed.
     */
    private void fetchUsers() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        dbHelper.fetchAllUsers(new DatabaseHelper.UsersCallback() {
            @Override
            public void onUsersFetched(List<User> users) {
                userList.clear();
                userList.addAll(users);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to retrieve all users", Toast.LENGTH_SHORT).show();
                Log.e("DatabaseHelper", "Error retrieving users", e);
            }
        });
    }
}