package com.example.tigers_lottery.Admin.DashboardFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.User;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Fragment for displaying and managing facility details for admins.
 * This fragment allows an admin to view detailed information about a facility,
 * including the facility's name, email, phone, location, and profile picture.
 * Admins can also remove the facility's profile picture if one exists.
 */
public class AdminFacilityDetailsFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";
    private String userId;
    private DatabaseHelper dbHelper;
    private User user;

    // UI Components
    private TextView facilityName;
    private TextView facilityEmail;
    private TextView facilityPhone;
    private TextView facilityLocation;
    private TextView facilityOwner;
    private ImageView facilityPhoto, removeFacilityPhotoImage;
    private LinearLayout removeFacilityPhoto;
    private TextView removeFacilityPhotoText;

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @param userId The ID of the user whose facility details are to be displayed.
     * @return A new instance of AdminFacilityDetailsFragment.
     */
    public static AdminFacilityDetailsFragment newInstance(String userId) {
        AdminFacilityDetailsFragment fragment = new AdminFacilityDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is being created.
     * Retrieves the user ID from the arguments and initializes the DatabaseHelper.
     *
     * @param savedInstanceState Saved state of the fragment, if available.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }
        dbHelper = new DatabaseHelper(requireContext());
    }

    /**
     * Inflates the layout for this fragment and initializes UI components.
     * Hides unrelated UI components that are not relevant to facilities.
     *
     * @param inflater           LayoutInflater to inflate the layout.
     * @param container          Parent container for the fragment.
     * @param savedInstanceState Saved state of the fragment, if available.
     * @return The root view of the fragment's layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_eventdetails_fragment, container, false);

        // Initialize UI components
        facilityName = view.findViewById(R.id.eventTitle);
        facilityEmail = view.findViewById(R.id.eventLocation);
        facilityPhone = view.findViewById(R.id.waitlistOpenDate);
        facilityLocation = view.findViewById(R.id.waitlistCloseDate);
        facilityOwner = view.findViewById(R.id.eventDate);
        facilityPhoto = view.findViewById(R.id.eventPoster);
        removeFacilityPhoto = view.findViewById(R.id.runLotteryButton);
        removeFacilityPhotoText =view.findViewById(R.id.runLotteryText);
        removeFacilityPhotoImage = view.findViewById(R.id.runLotteryImage);

        // Hide unrelated UI components
        TextView description = view.findViewById(R.id.eventDescription);
        Button viewRegistered = view.findViewById(R.id.viewRegisteredEntrants);
        Button viewDeclined = view.findViewById(R.id.viewDeclinedEntrants);
        Button viewWaitlisted = view.findViewById(R.id.viewWaitlistedEntrants);
        Button viewInvited = view.findViewById(R.id.viewInvitedEntrants);
        LinearLayout clearLists = view.findViewById(R.id.clearListsButton);
        LinearLayout viewQRCode = view.findViewById(R.id.viewQRCodeButton);
        LinearLayout viewMap = view.findViewById(R.id.viewMapButton);

        clearLists.setVisibility(View.GONE);
        viewInvited.setVisibility(View.GONE);
        viewDeclined.setVisibility(View.GONE);
        viewRegistered.setVisibility(View.GONE);
        viewWaitlisted.setVisibility(View.GONE);
        description.setVisibility(View.GONE);
        viewQRCode.setVisibility(View.GONE);
        viewMap.setVisibility(View.GONE);
        removeFacilityPhoto.setVisibility(View.GONE);

        removeFacilityPhotoText.setText("Remove Facility Profile Photo");

        // Load facility details
        loadFacilityDetails();

        // Setup button listeners
        setupButtonListeners();

        return view;
    }

    /**
     * Fetches facility details from the database using the user ID
     * and updates the UI to display the details.
     * If the facility is not found, the fragment navigates back to the previous screen.
     */
    private void loadFacilityDetails() {
        dbHelper.fetchUserById(userId, new DatabaseHelper.UsersCallback() {
            @Override
            public void onUsersFetched(List<User> users) {
                // Not used in this context.
            }

            @Override
            public void onUserFetched(User fetchedUser) {
                if (fetchedUser != null) {
                    user = fetchedUser;
                    displayFacilityDetails(user);
                } else {
                    Toast.makeText(getContext(), "Facility not found", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error fetching facility details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Displays facility details in the UI components.
     * If a profile picture exists, it is loaded into the ImageView using Glide.
     *
     * @param user The user object containing the facility details to display.
     */
    private void displayFacilityDetails(User user) {
        facilityName.setText(user.getFacilityName());
        facilityEmail.setText("Email Address: " + user.getFacilityEmail());
        if (!Objects.equals(user.getPhoneNumber(), "")) {
            facilityPhone.setText("Phone Number: " + user.getFacilityPhone());
        } else {
            facilityPhone.setText("Phone Number: No Phone Number Added");
        }

        facilityLocation.setText("Facility Location: " + user.getFacilityLocation());
        facilityOwner.setText("Facility Owner: " + user.getFirstName() + " " + user.getLastName());

        // Display the placeholder image initially
        facilityPhoto.setImageResource(R.drawable.image_facility_placeholder);

        // Fetch and display the profile image
        String facilityUrl = user.getFacilityPhoto();
        if (facilityUrl != null && !facilityUrl.isEmpty() && !facilityUrl.equals("NoFacilityPhoto")) {
            Glide.with(requireContext())
                    .load(facilityUrl)
                    .placeholder(R.drawable.image_facility_placeholder)
                    .into(facilityPhoto);

            removeFacilityPhoto.setVisibility(View.VISIBLE);
            removeFacilityPhotoImage.setImageResource(R.drawable.placeholder_user_image);
            facilityPhoto.setOnClickListener(v -> showImagePreviewDialog(facilityUrl));
        }
    }

    /**
     * Sets up button listeners for admin actions.
     * Currently, this includes removing the facility's profile photo.
     */
    private void setupButtonListeners() {
        removeFacilityPhoto.setOnClickListener(v -> {
            dbHelper.removeImage("facility", userId, new DatabaseHelper.Callback() {
                @Override
                public void onSuccess(String message) {
                    // Dispatch a notification to the admin if necessary
                    Toast.makeText(getContext(), user.getFacilityName() + "'s facility profile photo has been removed", Toast.LENGTH_SHORT).show();
                    removeFacilityPhoto.setVisibility(View.GONE);
                    facilityPhoto.setOnClickListener(null);
                    loadFacilityDetails();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Failed to remove facility profile photo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Shows a dialog to preview the facility's profile image in fullscreen.
     *
     * @param imageUrl The URL of the profile image to preview.
     */
    private void showImagePreviewDialog(String imageUrl) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.image_preview, null);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        ImageView imagePreview = dialogView.findViewById(R.id.imagePreview);
        Glide.with(requireContext()).load(imageUrl).into(imagePreview);

        ImageView closePreview = dialogView.findViewById(R.id.closePreview);
        closePreview.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}