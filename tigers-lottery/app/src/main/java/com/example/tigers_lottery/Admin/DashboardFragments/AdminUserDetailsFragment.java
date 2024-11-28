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
 * Fragment for displaying and managing user details for admins.
 * This fragment allows an admin to view detailed information about a user,
 * including their name, email, phone number, date of birth, and profile picture.
 * The admin can also remove the user's profile picture if one exists.
 */
public class AdminUserDetailsFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";
    private String userId;
    private DatabaseHelper dbHelper;
    private User user;

    // UI Components
    private TextView userName;
    private TextView userEmail;
    private TextView userMobile;
    private TextView userDOB;
    private ImageView userPhoto, removeUserProfilePhotoImage;
    private LinearLayout removeUserProfilePhoto;
    private TextView removeUserText;

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @param userId The ID of the user whose details are to be displayed.
     * @return A new instance of AdminUserDetailsFragment.
     */
    public static AdminUserDetailsFragment newInstance(String userId) {
        AdminUserDetailsFragment fragment = new AdminUserDetailsFragment();
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
        userName = view.findViewById(R.id.eventTitle);
        userEmail = view.findViewById(R.id.eventLocation);
        userDOB = view.findViewById(R.id.waitlistOpenDate);
        userMobile = view.findViewById(R.id.waitlistCloseDate);
        userPhoto = view.findViewById(R.id.eventPoster);
        removeUserProfilePhoto = view.findViewById(R.id.runLotteryButton);
        removeUserText = view.findViewById(R.id.runLotteryText);
        removeUserProfilePhotoImage = view.findViewById(R.id.runLotteryImage);

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
        removeUserProfilePhoto.setVisibility(View.GONE);
        removeUserText.setText("Remove User Profile Photo");
        removeUserProfilePhotoImage.setImageResource(R.drawable.placeholder_user_image);

        // Load user details
        loadUserDetails();

        // Setup button listeners
        setupButtonListeners();

        return view;
    }

    /**
     * Fetches user details from the database using the user ID
     * and updates the UI to display the details.
     */
    private void loadUserDetails() {
        dbHelper.fetchUserById(userId, new DatabaseHelper.UsersCallback() {
            @Override
            public void onUsersFetched(List<User> users) {
                // Not used
            }

            @Override
            public void onUserFetched(User fetchedUser) {
                if (fetchedUser != null) {
                    user = fetchedUser;
                    displayUserDetails(user);
                } else {
                    Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error fetching user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Displays user details in the UI components.
     * If a profile picture exists, it is loaded into the ImageView.
     *
     * @param user The user object containing the details to display.
     */
    private void displayUserDetails(User user) {
        userName.setText(user.getFirstName() + " " + user.getLastName());
        userEmail.setText("Email Address: " + user.getEmailAddress());
        if (!Objects.equals(user.getPhoneNumber(), "")) {
            userMobile.setText("Phone Number: " + user.getPhoneNumber());
        } else {
            userMobile.setText("Phone Number: No Phone Number Added");
        }

        Timestamp dobTimestamp = user.getDateOfBirth();
        Date dobDate = dobTimestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(dobDate);
        userDOB.setText("Date of Birth: " + formattedDate);

        // Display the placeholder image initially
        userPhoto.setImageResource(R.drawable.placeholder_user_image);

        // Fetch and display the profile image
        String profileUrl = user.getUserPhoto();
        if (profileUrl != null && !profileUrl.isEmpty() && !profileUrl.equals("NoProfilePhoto")) {
            Glide.with(requireContext())
                    .load(profileUrl)
                    .placeholder(R.drawable.placeholder_user_image)
                    .into(userPhoto);

            removeUserProfilePhoto.setVisibility(View.VISIBLE);
            userPhoto.setOnClickListener(v -> showImagePreviewDialog(profileUrl));
        }
    }

    /**
     * Sets up button listeners for admin actions, including
     * removing the user's profile photo.
     */
    private void setupButtonListeners() {
        removeUserProfilePhoto.setOnClickListener(v -> {
            dbHelper.removeImage("user", userId, new DatabaseHelper.Callback() {
                @Override
                public void onSuccess(String message) {
                    //TODO: Dispatch notification to the user
                    Toast.makeText(getContext(), user.getFirstName() + " " + user.getLastName() + "'s profile picture has been removed", Toast.LENGTH_SHORT).show();
                    removeUserProfilePhoto.setVisibility(View.GONE);
                    userPhoto.setOnClickListener(null);
                    loadUserDetails();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Failed to remove profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Shows a dialog to preview the user's profile image in fullscreen.
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