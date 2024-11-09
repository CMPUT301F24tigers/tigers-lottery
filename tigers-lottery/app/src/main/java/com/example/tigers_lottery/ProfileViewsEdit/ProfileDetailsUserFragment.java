package com.example.tigers_lottery.ProfileViewsEdit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.utils.DeviceIDHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Fragment that displays user profile details and allows the user to edit their profile information.
 * Loads profile data from Firebase Firestore and displays it in the appropriate UI components.
 */
public class ProfileDetailsUserFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Parameters passed to the fragment
    private String mParam1;
    private String mParam2;

    // UI components for displaying user information
    TextView userNameTextView, userEmailTextView, userMobileTextView, userBirthdayTextView, userLocation;
    Button editProfileButton, adminVerifyBtn;
    ImageView userPhoto;
    DatabaseHelper dbHelper;
    String authCode;

    /**
     * Default constructor for the fragment.
     */
    public ProfileDetailsUserFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileDetailsUserFragment.
     */
    public static ProfileDetailsUserFragment newInstance(String param1, String param2) {
        ProfileDetailsUserFragment fragment = new ProfileDetailsUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is created. Retrieves arguments if provided.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        dbHelper = new DatabaseHelper(requireContext());
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * Initializes the UI components and loads user profile data from Firestore.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_details_user_fragment, container, false);
        userNameTextView = view.findViewById(R.id.userName);
        userEmailTextView = view.findViewById(R.id.userEmail);
        userBirthdayTextView = view.findViewById(R.id.userBirthday);
        userMobileTextView = view.findViewById(R.id.userMobile);
        editProfileButton = view.findViewById(R.id.editUserProfileButton);
        userPhoto = view.findViewById(R.id.userProfilePic);
        adminVerifyBtn = view.findViewById(R.id.adminVerifyBtn);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Retrieve the device ID to uniquely identify the user
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("ProfileDetailsUserFragment", "Device ID: " + deviceId);

        // Fetch user profile data from Firestore
        db.collection("users").document(deviceId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String name = document.getString("first_name") + " " + document.getString("last_name");;
                                userNameTextView.setText(name);
                                userEmailTextView.setText(document.getString("email_address"));
                                userBirthdayTextView.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(document.getTimestamp("DOB").toDate()));
                                userMobileTextView.setText(document.getString("phone_number"));

                                if (document.getString("user_photo") != null) {
                                    loadImageFromFirebase(document.getString("user_photo"), userPhoto);
                                }
                            } else {
                                Log.e("Firestore", "No document found for user");
                            }
                        } else {
                            Log.e("Firestore", "Error getting document: ", task.getException());
                        }
                    }
                });

        // Set listener for the edit profile button to navigate to edit profile fragment
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("deviceId", deviceId);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Fragment transitionedFragment = new ProfileEditUserFragment();
                transitionedFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.profileDetailsActivityFragment, transitionedFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        dbHelper.isAdminUser(deviceId, new DatabaseHelper.VerificationCallback() {
            @Override
            public void onResult(boolean exists) {
                if(exists){
                    adminVerifyBtn.setVisibility(View.GONE);
                } else {
                    adminVerifyBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("ProfileDetailsUserFragment", "Couldn't check if user " + deviceId + " is an Admin");
            }
        });
        setupAdminVerifyButton();

        return view;
    }

    /**
     * Sets up the admin verification button to trigger the admin verification dialog.
     * This button will be used to initiate the process of verifying the user as an admin.
     */
    private void setupAdminVerifyButton() {
        adminVerifyBtn.setOnClickListener(view -> showAdminVerificationDialog());
    }

    /**
     * Generates a random 5-digit code for admin verification.
     *
     * @return A 5-digit random code as a String.
     */
    private String generateAdminCode() {
        int code = (int) (Math.random() * 90000) + 10000; // Generates a random 5-digit number
        return String.valueOf(code);
    }

    /**
     * Displays a dialog for admin verification, allowing the user to enter an authentication code.
     * A random authentication code is generated and stored in the database when the dialog is opened.
     * If the user cancels the dialog, the code is deleted from the database.
     */
    private void showAdminVerificationDialog() {
        authCode = generateAdminCode();
        dbHelper.storeAdminAuthCode(authCode, new DatabaseHelper.Callback() {
            @Override
            public void onSuccess(String message) {
                // Code stored successfully, no action needed here
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure if needed
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Admin Verification");

        // Create an EditText to enter the code
        final EditText input = new EditText(requireContext());
        input.setHint("Enter Admin Authentication Code");
        builder.setView(input);

        builder.setPositiveButton("Verify", (dialog, which) -> {
            String enteredCode = input.getText().toString().trim();
            if (!enteredCode.isEmpty()) {
                verifyAdminCode(enteredCode);
            } else {
                Toast.makeText(getContext(), "Please enter a code", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Delete the generated code from the database if the user cancels
            dbHelper.deleteAdminAuthCode(authCode, new DatabaseHelper.Callback() {
                @Override
                public void onSuccess(String message) {
                    Log.d("ProfileDetailsUserFragment", "Admin code deleted upon cancel");
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("ProfileDetailsUserFragment", "Failed to delete admin code on cancel", e);
                }
            });
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Verifies the entered admin authentication code against the generated code.
     * If the code matches, the user is added to the admin collection.
     * If the code is incorrect, a toast message is shown and the code is deleted from the database.
     *
     * @param enteredCode The code entered by the user for verification.
     */
    private void verifyAdminCode(String enteredCode) {
        if (!Objects.equals(enteredCode, authCode)) {
            Toast.makeText(getContext(), "Invalid Admin Authentication Code", Toast.LENGTH_SHORT).show();

            // Delete the incorrect code from the database
            dbHelper.deleteAdminAuthCode(authCode, new DatabaseHelper.Callback() {
                @Override
                public void onSuccess(String message) {}

                @Override
                public void onFailure(Exception e) {}
            });
        } else {
            Toast.makeText(getContext(), "You are being verified as an Admin", Toast.LENGTH_SHORT).show();
            addUserToAdmins(enteredCode);
        }
    }

    /**
     * Adds the user's device ID to the admin collection in the database, confirming them as an admin.
     * If successful, the admin verification button is hidden and a success message is shown.
     * If adding the user fails, an error message is displayed.
     *
     * @param enteredCode The authentication code that was successfully verified.
     */
    private void addUserToAdmins(String enteredCode) {
        String deviceId = DeviceIDHelper.getDeviceId(requireContext());

        dbHelper.addUserToAdmins(deviceId, enteredCode, new DatabaseHelper.Callback() {
            @Override
            public void onSuccess(String message) {
                Log.e("ProfileDetailsUserFragment", "Adding to admins");
                Toast.makeText(getContext(), "You are now registered as an Admin", Toast.LENGTH_SHORT).show();
                adminVerifyBtn.setVisibility(View.GONE); // Hide the button upon successful verification
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ProfileDetailsUserFragment", "Error adding user to admins", e);
                Toast.makeText(getContext(), "Failed to add admin. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Loads an image from Firebase Storage using the provided link and displays it in an ImageView.
     *
     * @param link URL of the image to load.
     * @param userPhoto The ImageView in which to display the loaded image.
     */
    private void loadImageFromFirebase(String link, ImageView userPhoto) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        Glide.with(requireContext())
                .load(link)
                .into(userPhoto);
    }
}
