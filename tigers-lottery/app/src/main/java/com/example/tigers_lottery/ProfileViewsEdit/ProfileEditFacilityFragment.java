package com.example.tigers_lottery.ProfileViewsEdit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fragment that allows users to edit their facility profile information.
 * Includes fields for editing name, email, phone number, location, and profile photo.
 * Updates information to Firebase Firestore and uploads the profile photo to Firebase Storage.
 */
public class ProfileEditFacilityFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private String mParam1;
    private String mParam2;

    // UI components for editing facility information
    EditText nameEditText, emailEditText, mobileEditText, locationEditText;
    Button saveChangesButton;
    ImageButton editProfilePhotoButton;
    ImageView facilityEditProfilePhoto;

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
                        ImageView imageView = getView().findViewById(R.id.facilityEditProfilePhoto);
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
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("profile_images/" + UUID.randomUUID().toString());

        facilityEditProfilePhoto = view.findViewById(R.id.facilityEditProfilePhoto);
        nameEditText = view.findViewById(R.id.editTextName);
        emailEditText = view.findViewById(R.id.editTextEmail);
        mobileEditText = view.findViewById(R.id.editTextMobile);
        locationEditText = view.findViewById(R.id.editTextLocation);
        saveChangesButton = view.findViewById(R.id.saveChangesButton);
        editProfilePhotoButton = view.findViewById(R.id.editProfilePhoto);
        ImageButton removeFacilityProfilePictureButton = view.findViewById(R.id.removeFacilityProfilePictureButton);

        String deviceId = getArguments().getString("deviceId");

        dbHelper.getUser(deviceId, new DatabaseHelper.UserCallback() {
            @Override
            public void onUserFetched(User user) {
                nameEditText.setText(user.getFacilityName());
                emailEditText.setText(user.getFacilityEmail());
                mobileEditText.setText(user.getFacilityPhone());
                locationEditText.setText(user.getFacilityLocation());

                if(user.getFacilityPhoto() != null && !Objects.equals(user.getFacilityPhoto(), "NoFacilityPhoto")) {
                    Glide.with(requireContext())
                            .load(user.getFacilityPhoto())
                            .into(facilityEditProfilePhoto);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });

        // Listener to save changes to Firestore and Firebase Storage
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!validatingFacilityProfileInput(getContext(), nameEditText.getText().toString(), emailEditText.getText().toString(), locationEditText.getText().toString(), mobileEditText.getText().toString())) {
                    Toast.makeText(getContext(), "Every field must be filled!", Toast.LENGTH_LONG).show();
                    return;
                }

                // Update user information in Firestore
                db.collection("users").document(deviceId)
                        .update(
                                "facility_name", nameEditText.getText().toString(),
                                "facility_phone", mobileEditText.getText().toString(),
                                "facility_email", emailEditText.getText().toString(),
                                "facility_location", locationEditText.getText().toString()
//                                "facility_photo", imageUri
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
                            DocumentReference docRef = db.collection("users").document(deviceId);
                            docRef.update("facility_photo", downloadUrl).addOnSuccessListener(aVoid -> {
//                                Toast.makeText(getContext(), "Image uploaded and URL saved to Firestore", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
//                                Toast.makeText(getContext(), "Failed to save URL to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        });
                    }).addOnFailureListener(e -> {
//                        Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

        removeFacilityProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Delete facility profile photo");
                builder.setMessage("Do you want to delete facility profile photo?");

                // Positive button
                builder.setPositiveButton("Confirm", (dialog, which) -> {
                    // Replace this with your actual device ID
                    String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);;

                    // Firestore: Document reference under device ID
                    DocumentReference docRef = db.collection("users").document(deviceId);

                    // Get the photo URL or path stored in Firestore
                    docRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String photoPath = documentSnapshot.getString("facility_photo"); // Adjust to your Firestore field

                            if (!Objects.equals(photoPath, "NoFacilityPhoto")) {
                                // Firebase Storage: Reference to the photo
                                assert photoPath != null;
                                StorageReference photoRef = storage.getReferenceFromUrl(photoPath);

                                // Delete the photo
                                photoRef.delete().addOnSuccessListener(aVoid -> {
                                    // Delete Firestore entry if needed
                                    docRef.update("facility_photo", "NoFacilityPhoto").addOnSuccessListener(aVoid1 -> {
                                        facilityEditProfilePhoto.setImageResource(R.drawable.baseline_account_circle_24);
                                    }).addOnFailureListener(e -> {

                                    });

                                }).addOnFailureListener(e -> {

                                });
                            } else {

                            }
                        } else {

                        }
                    }).addOnFailureListener(e -> {

                    });
                });

                // Negative button
                builder.setNegativeButton("Cancel", (dialog, which) -> {});

                // Show the dialog
                builder.show();
            }
        });

        return view;
    }

    public boolean validatingFacilityProfileInput(Context context, String name, String email, String location, String mobile) {
        return (!Objects.equals(name, "") && !Objects.equals(email, "") && !Objects.equals(location, "") && mobile != null);
    }
    public static boolean validateEmail(String email) {
        if (email == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
