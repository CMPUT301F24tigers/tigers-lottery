package com.example.tigers_lottery.ProfileViewsEdit;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tigers_lottery.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Locale;

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
    Button editProfileButton;
    ImageView userPhoto;

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
                                userNameTextView.setText(document.getString("first_name") + " " + document.getString("last_name"));
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

        return view;
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
