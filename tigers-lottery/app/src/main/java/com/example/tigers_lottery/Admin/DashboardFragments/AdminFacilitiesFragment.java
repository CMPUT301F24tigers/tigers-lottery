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
 * Fragment for displaying facility-related information within the admin dashboard.
 * This fragment includes navigation to return to the main AdminDashboardFragment via a back button.
 */
public class AdminFacilitiesFragment extends Fragment {

    /**
     * Inflates the layout for facility information and configures the back button to navigate back to the admin dashboard.
     *
     * @param inflater           LayoutInflater for inflating the fragment's layout.
     * @param container          Container for the fragment UI.
     * @param savedInstanceState Saved instance state for restoring fragment state.
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_facilities_fragment, container, false);

        ImageButton backButton = requireActivity().findViewById(R.id.globalBackButton);
        backButton.setVisibility(View.VISIBLE);

        // Set click listener for the back button
        backButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView2, new AdminDashboardFragment())
                    .commit();
            backButton.setVisibility(View.INVISIBLE);
        });

        return view;
    }

    /**
     * Hides the back button when the fragment is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hide the back button if fragment is destroyed
        ImageButton backButton = requireActivity().findViewById(R.id.globalBackButton);
        backButton.setVisibility(View.INVISIBLE);
    }
}