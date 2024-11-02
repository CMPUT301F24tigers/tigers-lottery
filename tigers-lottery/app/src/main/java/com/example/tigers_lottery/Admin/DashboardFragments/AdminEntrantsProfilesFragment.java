package com.example.tigers_lottery.Admin.DashboardFragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.example.tigers_lottery.Admin.AdminDashboardFragment;
import com.example.tigers_lottery.R;

/**
 * Fragment for displaying entrant profiles within the admin dashboard.
 * Includes functionality for navigating back to the AdminDashboardFragment
 * when the back button is clicked.
 */
public class AdminEntrantsProfilesFragment extends Fragment {

    /**
     * Inflates the layout for the entrant profiles screen and configures the back button.
     *
     * @param inflater           LayoutInflater for inflating the fragment layout.
     * @param container          The container this fragment belongs to.
     * @param savedInstanceState Saved state for restoring fragment state.
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        return view;
    }

    /**
     * Cleans up the view by hiding the back button when the fragment is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ImageButton backButton = requireActivity().findViewById(R.id.globalBackButton);
        backButton.setVisibility(View.INVISIBLE);
    }
}