package com.example.tigers_lottery.ProfileViewsEdit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.User;
import com.example.tigers_lottery.utils.Validator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fragment that allows users to edit their personal profile information,
 * such as first name, last name, date of birth, phone number, email, and profile photo.
 * Updates information to Firebase Firestore and uploads the profile photo to Firebase Storage.
 */
public class ProfileEditUserFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private String mParam2;

    // UI components for editing user information
    private EditText editTextEmail, editTextDOB, editTextMobile, editTextFirstName, editTextLastName;

    private Uri imageUri;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    // Selected date components for DOB
    private Integer selectedYear, selectedMonth, selectedDay;
    private Validator validator = new Validator();

    CheckBox enableNotificationsButton;

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
                        ImageView imageView = requireView().findViewById(R.id.userEditProfilePhoto);
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
        enableNotificationsButton = view.findViewById(R.id.checkboxNotificationsEnabled);
        Button saveChangesButton = view.findViewById(R.id.saveChangesUserProfileButton);
        ImageButton editProfilePhotoButton = view.findViewById(R.id.editUserProfilePhoto);
        ImageButton removeUserProfilePictureButton = view.findViewById(R.id.removeUserProfilePictureButton);
        ImageView userEditProfilePhoto = view.findViewById(R.id.userEditProfilePhoto);
        AtomicReference<Boolean> profilePhotoDelete = new AtomicReference<>(false);
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
                enableNotificationsButton.setChecked(user.isNotificationFlag());

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(user.getDateOfBirth().toDate());

                selectedYear = calendar.get(Calendar.YEAR);
                selectedMonth = calendar.get(Calendar.MONTH)+1; // Months are 0-based, so add 1
                selectedDay = calendar.get(Calendar.DAY_OF_MONTH);

                if(user.getUserPhoto() != null && !Objects.equals(user.getUserPhoto(), "NoProfilePhoto")) {
                    Glide.with(requireContext())
                            .load(user.getUserPhoto())
                            .into(userEditProfilePhoto);
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

                if(validator.validatingUserProfileInput(editTextFirstName.getText().toString(), editTextLastName.getText().toString(), editTextEmail.getText().toString(), date, editTextMobile.getText().toString(),requireContext())) {

                    // Update user information in Firestore
                    assert deviceId != null;
                    db.collection("users").document(deviceId)
                            .update(
                                    "first_name", editTextFirstName.getText().toString(),
                                    "last_name", editTextLastName.getText().toString(),
                                    "DOB", date,
                                    "phone_number", editTextMobile.getText().toString(),
                                    "email_address", editTextEmail.getText().toString(),
                                    "notification_flag", enableNotificationsButton.isChecked()
                            )
                            .addOnSuccessListener(aVoid -> Log.d("Firestore Update", "Document updated successfully"))

                if(profilePhotoDelete.get() && imageUri == null) {
                    Bitmap imageBitmap = generateProfilePicture(editTextFirstName.getText().toString(), editTextLastName.getText().toString(), 300);
                    imageUri = saveBitmapToUri(getContext(), imageBitmap);
                }

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

                    if (profilePhotoDelete.get() && imageUri == null) {
                        Bitmap imageBitmap = generateProfilePicture(editTextFirstName.getText().toString(), editTextLastName.getText().toString(), 300);
                        imageUri = saveBitmapToUri(getContext(), imageBitmap);
                    }

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

        removeUserProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Delete user profile photo");
                builder.setMessage("Do you want to delete user profile photo?");

                // Positive button
                builder.setPositiveButton("Confirm", (dialog, which) -> {
                    // Replace this with your actual device ID
                    String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    ;

                    // Firestore: Document reference under device ID
                    DocumentReference docRef = db.collection("users").document(deviceId);

                    // Get the photo URL or path stored in Firestore
                    docRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String photoPath = documentSnapshot.getString("user_photo"); // Adjust to your Firestore field

                            if (!Objects.equals(photoPath, "NoProfilePhoto")) {
                                // Firebase Storage: Reference to the photo
                                assert photoPath != null;
                                StorageReference photoRef = storage.getReferenceFromUrl(photoPath);

                                // Delete the photo
                                photoRef.delete().addOnSuccessListener(aVoid -> {
                                    // Delete Firestore entry if needed
                                    docRef.update("user_photo", "NoProfilePhoto").addOnSuccessListener(aVoid1 -> {
                                        userEditProfilePhoto.setImageResource(R.drawable.baseline_account_circle_24);
                                        imageUri = null;
                                        profilePhotoDelete.set(true);
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
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                });

                // Show the dialog
                builder.show();
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

    public static Bitmap generateProfilePicture(String firstName, String lastName, int size) {
        // Extract initials
        String initials = "";
        if (firstName != null && firstName.length() > 0) {
            initials += firstName.substring(0, 1).toUpperCase();
        }
        if (lastName != null && lastName.length() > 0) {
            initials += lastName.substring(0, 1).toUpperCase();
        }

        // Create a bitmap
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        // Create a canvas to draw on the bitmap
        Canvas canvas = new Canvas(bitmap);

        // Generate a random background color
        int backgroundColor = generateRandomColor();
        canvas.drawColor(backgroundColor);

        // Set up paint for the text
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE); // Text color
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(size / 2f); // Adjust text size

        // Measure text size to center it
        Rect textBounds = new Rect();
        textPaint.getTextBounds(initials, 0, initials.length(), textBounds);
        int x = (bitmap.getWidth() - textBounds.width()) / 2 - textBounds.left;
        int y = (bitmap.getHeight() + textBounds.height()) / 2 - textBounds.bottom;

        // Draw initials on the canvas
        canvas.drawText(initials, x, y, textPaint);

        return bitmap;
    }

    /**
     * Generate a random pastel color.
     *
     * @return A random color as an integer.
     */
    private static int generateRandomColor() {
        float[] hsv = new float[3];
        hsv[0] = (float) (Math.random() * 360); // Hue
        hsv[1] = 0.5f; // Saturation
        hsv[2] = 0.9f; // Value
        return Color.HSVToColor(hsv);
    }

    private static Uri saveBitmapToUri(Context context, Bitmap bitmap) {
        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, "profile_image_" + System.currentTimeMillis() + ".png");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            // Use FileProvider for secure access
            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
