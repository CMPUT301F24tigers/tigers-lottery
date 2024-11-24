package com.example.tigers_lottery.ProfileViewsEdit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Fragment that allows users to edit their personal profile information,
 * such as first name, last name, date of birth, phone number, email, and profile photo.
 * Updates information to Firebase Firestore and uploads the profile photo to Firebase Storage.
 */
public class ProfileEditUserFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam2;

    // UI components for editing user information
    private EditText editTextEmail, editTextDOB, editTextMobile, editTextFirstName, editTextLastName;

    private Uri imageUri;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    // Selected date components for DOB
    private Integer selectedYear, selectedMonth, selectedDay;

    /**
     * Default constructor for the fragment.
     */
    public ProfileEditUserFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileEditUserFragment.
     */
    public static ProfileEditUserFragment newInstance(String param1, String param2) {
        ProfileEditUserFragment fragment = new ProfileEditUserFragment();
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
            String mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Set up launcher for selecting an image from the gallery
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        ImageView imageView = requireView().findViewById(R.id.userProfilePhoto);
                        imageView.setImageURI(imageUri); // Display selected image
                    }
                });
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * Initializes the UI components, sets up listeners for saving changes and selecting a profile photo,
     * and updates user information and image to Firebase.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_edit_user_fragment, container, false);

        // Initialize UI components
        editTextFirstName = view.findViewById(R.id.editTextUserProfileFirstName);
        editTextLastName = view.findViewById(R.id.editTextUserProfileLastName);
        editTextEmail = view.findViewById(R.id.editTextUserProfileEmail);
        editTextDOB = view.findViewById(R.id.editTextUserProfileDOB);
        editTextMobile = view.findViewById(R.id.editTextUserProfileMobile);
        Button saveChangesButton = view.findViewById(R.id.saveChangesUserProfileButton);
        ImageButton editProfilePhotoButton = view.findViewById(R.id.editUserProfilePhoto);
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("profile_images/" + UUID.randomUUID().toString());

        assert getArguments() != null;
        String deviceId = getArguments().getString("deviceId");

        dbHelper.getUser(deviceId, new DatabaseHelper.UserCallback() {
            @Override
            public void onUserFetched(User user) {
                editTextFirstName.setText(user.getFirstName());
                editTextLastName.setText(user.getLastName());
                editTextEmail.setText(user.getEmailAddress());
                editTextDOB.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(user.getDateOfBirth().toDate()));
                editTextMobile.setText(user.getPhoneNumber());

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(user.getDateOfBirth().toDate());

                selectedYear = calendar.get(Calendar.YEAR);
                selectedMonth = calendar.get(Calendar.MONTH)+1; // Months are 0-based, so add 1
                selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
            }

            @Override
            public void onError(Exception e) {

            }
        });

        // Listener to save changes to Firestore and Firebase Storage
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = null;

                if (selectedMonth != null && selectedDay != null && selectedYear != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                    calendar.set(Calendar.MONTH, selectedMonth-1);
                    calendar.set(Calendar.YEAR, selectedYear);

                    // Set time components to zero to represent only the date
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    date = calendar.getTime();
                }

                if(validatingUserProfileInput(editTextFirstName.getText().toString(), editTextLastName.getText().toString(), editTextEmail.getText().toString(), date)) {
                    Toast.makeText(getContext(), "Every field except for Phone Number must be filled!", Toast.LENGTH_LONG).show();
                    return;
                }
                
                // Update user information in Firestore
                assert deviceId != null;
                db.collection("users").document(deviceId)
                        .update(
                                "first_name", editTextFirstName.getText().toString(),
                                "last_name", editTextLastName.getText().toString(),
                                "DOB", date,
                                "phone_number", editTextMobile.getText().toString(),
                                "email_address", editTextEmail.getText().toString()
                        )
                        .addOnSuccessListener(aVoid -> Log.d("Firestore Update", "Document updated successfully"))
                        .addOnFailureListener(e -> Log.w("Firestore Update", "Error updating document", e));

                // Upload image to Firebase Storage if a new image is selected
                if (imageUri != null) {
                    imageRef.putFile(imageUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                    String downloadUrl = downloadUri.toString();
                                    DocumentReference docRef = db.collection("users").document(deviceId);
                                    docRef.update("user_photo", downloadUrl)
                                            .addOnSuccessListener(aVoid -> Log.d("Firestore Update", "Document updated successfully"))
                                            .addOnFailureListener(e -> Log.w("Firestore Update", "Error updating document", e));
                                });
                            })
                            .addOnFailureListener(e -> Log.w("Firestore Update", "Error updating document", e));
                }

                // Navigate back to ProfileDetailsUserFragment after saving
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment transitionedFragment = new ProfileDetailsUserFragment();

                fragmentTransaction.replace(R.id.profileDetailsActivityFragment, transitionedFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

        });

        // Listener to open DatePickerDialog when clicking on DOB field
        editTextDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
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

    /**
     * Opens a DatePickerDialog for selecting a date and updates the DOB EditText
     * with the selected date in "DD/MM/YYYY" format.
     */
    private void openDatePicker() {
        // Use the current date as the default date in the picker
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Save selected day, month, and year
                    this.selectedYear = selectedYear;
                    this.selectedMonth = selectedMonth + 1; // Month is 0-indexed in Calendar
                    this.selectedDay = selectedDay;

                    // Update the TextView with the selected date in "DD/MM/YYYY" format
                    String dateText = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    editTextDOB.setText(dateText);
                }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    public boolean validatingUserProfileInput(String firstName, String lastName, String email, Date date) {
        return (Objects.equals(firstName, "") || Objects.equals(lastName, "") || Objects.equals(email, "") || date == null);
    }
}
