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

import com.example.tigers_lottery.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.bumptech.glide.Glide;

import java.util.Objects;

/**
 * Fragment that displays facility profile details and allows the user to edit their profile information.
 * Loads profile data from Firebase Firestore and displays it in the appropriate UI components.
 */
public class ProfileDetailsFacilityFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Parameters passed to the fragment
    private String mParam1;
    private String mParam2;

    // UI components for displaying facility information
    TextView facilityNameTextView, facilityEmailTextView, facilityMobileTextView, facilityLocation;
    Button editProfileButton;
    ImageView facilityPhoto;

    /**
     * Default constructor for the fragment.
     */
    public ProfileDetailsFacilityFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileDetailsFacilityFragment.
     */
    public static ProfileDetailsFacilityFragment newInstance(String param1, String param2) {
        ProfileDetailsFacilityFragment fragment = new ProfileDetailsFacilityFragment();
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
     * Initializes the UI components and loads facility profile data from Firestore.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_details_facilities_fragment, container, false);
        facilityNameTextView = view.findViewById(R.id.facilityName);
        facilityEmailTextView = view.findViewById(R.id.facilityEmail);
        facilityMobileTextView = view.findViewById(R.id.facilityMobile);
        facilityLocation = view.findViewById(R.id.facilityLocation);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        facilityPhoto = view.findViewById(R.id.facilityProfilePic);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Retrieve the device ID to uniquely identify the user
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Fetch user profile data from Firestore
        db.collection("users").document(deviceId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                facilityNameTextView.setText(document.getString("facility_name"));
                                facilityEmailTextView.setText(document.getString("facility_email"));
                                facilityMobileTextView.setText(document.getString("facility_phone"));
                                facilityLocation.setText(document.getString("facility_location"));

                                // Load facility photo if available
                                if (document.getString("facility_photo") != null && !Objects.equals(document.getString("facility_photo"), "NoFacilityPhoto")) {
                                    loadImageFromFirebase(document.getString("facility_photo"), facilityPhoto);
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

                Fragment transitionedFragment = new ProfileEditFacilityFragment();
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
     * @param facilityProfile The ImageView in which to display the loaded image.
     */
    private void loadImageFromFirebase(String link, ImageView facilityProfile) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        Glide.with(requireContext())
                .load(link)
                .into(facilityProfile);
    }
}
