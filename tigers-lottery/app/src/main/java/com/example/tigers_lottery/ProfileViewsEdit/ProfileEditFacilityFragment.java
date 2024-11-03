package com.example.tigers_lottery.ProfileViewsEdit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tigers_lottery.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

/**
 * Fragment that allows users to edit their facility profile information.
 * Includes fields for editing name, email, phone number, location, and profile photo.
 * Updates information to Firebase Firestore and uploads the profile photo to Firebase Storage.
 */
public class ProfileEditFacilityFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // UI components for editing facility information
    EditText nameEditText, emailEditText, mobileEditText, locationEditText;
    Button saveChangesButton;
    ImageButton editProfilePhotoButton;

    private Uri imageUri;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    /**
     * Default constructor for the fragment.
     */
    public ProfileEditFacilityFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileEditFacilityFragment.
     */
    public static ProfileEditFacilityFragment newInstance(String param1, String param2) {
        ProfileEditFacilityFragment fragment = new ProfileEditFacilityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is created. Retrieves arguments if provided.
     * Sets up the ActivityResultLauncher for selecting a profile photo from the gallery.
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

        // Set up launcher for selecting an image from the gallery
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        ImageView imageView = getView().findViewById(R.id.profilePhoto);
                        imageView.setImageURI(imageUri); // Display selected image
                    }
                });
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * Initializes the UI components, sets up listeners for saving changes and selecting a profile photo,
     * and updates user information and image to Firebase.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_edit_facility_fragment, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("profile_images/" + UUID.randomUUID().toString());

        nameEditText = view.findViewById(R.id.editTextName);
        emailEditText = view.findViewById(R.id.editTextEmail);
        mobileEditText = view.findViewById(R.id.editTextMobile);
        locationEditText = view.findViewById(R.id.editTextLocation);
        saveChangesButton = view.findViewById(R.id.saveChangesButton);
        editProfilePhotoButton = view.findViewById(R.id.editProfilePhoto);

        String deviceId = getArguments().getString("deviceId");

        // Listener to save changes to Firestore and Firebase Storage
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update user information in Firestore
                db.collection("users").document(deviceId)
                        .update(
                                "facility_name", nameEditText.getText().toString(),
                                "facility_phone", mobileEditText.getText().toString(),
                                "facility_email", emailEditText.getText().toString(),
                                "facility_location", locationEditText.getText().toString(),
                                "facility_photo", imageUri
                        )
                        .addOnSuccessListener(aVoid -> Log.d("Firestore Update", "Document updated successfully"))
                        .addOnFailureListener(e -> Log.w("Firestore Update", "Error updating document", e));

                // Upload image to Firebase Storage if a new image is selected
                if (imageUri != null) {
                    imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL after the image upload
                        imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            String downloadUrl = downloadUri.toString();

                            // Save download URL to Firestore
                            DocumentReference docRef = db.collection("Users").document(deviceId);
                            docRef.update("Facility Photo", downloadUrl).addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Image uploaded and URL saved to Firestore", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to save URL to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

                // Navigate back to ProfileDetailsFacilityFragment after saving
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment transitionedFragment = new ProfileDetailsFacilityFragment();

                fragmentTransaction.replace(R.id.profileDetailsActivityFragment, transitionedFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

        });

        // Listener to open image picker for selecting a new profile photo
        editProfilePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });

        return view;
    }

}
