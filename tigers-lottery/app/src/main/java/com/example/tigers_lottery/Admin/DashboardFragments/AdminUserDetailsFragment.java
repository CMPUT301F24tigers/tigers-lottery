package com.example.tigers_lottery.Admin.DashboardFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
    private ImageView userPhoto;
    private Button removeUserProfilePhoto;

    public static AdminUserDetailsFragment newInstance(String userId) {
        AdminUserDetailsFragment fragment = new AdminUserDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }
        dbHelper = new DatabaseHelper(requireContext());
    }

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

        //UI components to hide
        TextView description = view.findViewById(R.id.eventDescription);
        Button viewRegistered = view.findViewById(R.id.viewRegisteredEntrants);
        Button viewDeclined =  view.findViewById(R.id.viewDeclinedEntrants);
        Button viewWaitlisted = view.findViewById(R.id.viewWaitlistedEntrants);
        Button viewInvited = view.findViewById(R.id.viewInvitedEntrants);
        Button clearLists = view.findViewById(R.id.clearListsButton);
        clearLists.setVisibility(View.GONE);
        viewInvited.setVisibility(View.GONE);
        viewDeclined.setVisibility(View.GONE);
        viewRegistered.setVisibility(View.GONE);
        viewWaitlisted.setVisibility(View.GONE);
        description.setVisibility(View.GONE);
        removeUserProfilePhoto.setVisibility(View.GONE);


        // Adjust button texts for admin actions
        removeUserProfilePhoto.setText("Remove User Profile Photo");

        // Load user details
        loadUserDetails();

        // Setup button listeners
        setupButtonListeners();

        return view;
    }

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

    private void displayUserDetails(User user) {
        userName.setText(user.getFirstName() + " " + user.getLastName());
        userEmail.setText("Email Address: " + user.getEmailAddress());
        if (!Objects.equals(user.getPhoneNumber(), "")){
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
        userPhoto.setImageResource(R.drawable.placeholder_user_image); // Placeholder image

        // Fetch and display the profile image
        dbHelper.getUserProfileImage(user.getUserId(), new DatabaseHelper.Callback() {
            @Override
            public void onSuccess(String imageUrl) {
                if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("NoProfilePhoto")) {
                    // Load the image into the ImageView
                    Glide.with(requireContext())
                            .load(imageUrl)
                            .into(userPhoto);
                    // Make ImageView clickable
                    userPhoto.setOnClickListener(v -> showImagePreviewDialog(imageUrl));

                    // Show the "Remove Profile Photo" button
                    removeUserProfilePhoto.setVisibility(View.VISIBLE);
                } else {
                    // If no image exists, keep the placeholder and hide the button
                    userPhoto.setImageResource(R.drawable.placeholder_user_image);
                    userPhoto.setOnClickListener(null);
                    removeUserProfilePhoto.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                userPhoto.setImageResource(R.drawable.placeholder_user_image);
                removeUserProfilePhoto.setVisibility(View.GONE);
                userPhoto.setOnClickListener(null);
                Toast.makeText(getContext(), "Failed to load profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupButtonListeners() {
        //TODO:Dispatch notification to user
        removeUserProfilePhoto.setOnClickListener(v -> {
            dbHelper.removeUserProfileImage(userId, new DatabaseHelper.Callback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(getContext(), user.getFirstName() + " " + user.getLastName() + "'s profile picture has been removed", Toast.LENGTH_SHORT).show();
                    loadUserDetails();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Failed to remove profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

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
