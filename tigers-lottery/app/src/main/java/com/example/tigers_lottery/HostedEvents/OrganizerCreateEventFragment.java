package com.example.tigers_lottery.HostedEvents;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrganizerCreateEventFragment extends Fragment {

    private EditText inputEventName, inputEventLocation, inputRegistrationOpens, inputRegistrationDeadline, inputEventDate, inputEventDescription, inputWaitlistLimit, inputOccupantLimit;
    private CheckBox checkboxWaitlistLimit;
    private Button btnCreateEvent;
    private DatabaseHelper dbHelper;
    private ImageView imagePoster;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private StorageReference storageReference;
    private TextView photoPlaceholderText;



    public OrganizerCreateEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_createevent_fragment, container, false);

        // Initialize views
        inputEventName = view.findViewById(R.id.inputEventName);
        inputEventLocation = view.findViewById(R.id.inputEventLocation);
        inputRegistrationOpens = view.findViewById(R.id.inputRegistrationOpens);
        inputRegistrationDeadline = view.findViewById(R.id.inputRegistrationDeadline);
        inputEventDate = view.findViewById(R.id.inputEventDate);
        inputEventDescription = view.findViewById(R.id.inputEventDescription);
        checkboxWaitlistLimit = view.findViewById(R.id.checkboxWaitlistLimit);
        inputWaitlistLimit = view.findViewById(R.id.inputWaitlistLimit);
        btnCreateEvent = view.findViewById(R.id.btnCreateEvent);
        inputOccupantLimit = view.findViewById(R.id.inputOccupantLimit);
        imagePoster = view.findViewById(R.id.photoPlaceholder);
        photoPlaceholderText = view.findViewById(R.id.photoPlaceholderText);


        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(requireContext());

        // Set up ActivityResultLauncher for image selection
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imagePoster.setImageURI(imageUri); // Display selected image in placeholder
                        photoPlaceholderText.setVisibility(View.GONE); // Hide placeholder text when image is selected
                    }
                });

        // Set click listener to open image picker when poster placeholder is clicked
        imagePoster.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });

        // Show/hide waitlist limit input based on checkbox
        checkboxWaitlistLimit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            inputWaitlistLimit.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Set click listener for Create Event button
        btnCreateEvent.setOnClickListener(v -> createEvent());

        return view;
    }

    private void createEvent() {
        // Capture user input
        String eventName = inputEventName.getText().toString().trim();
        String eventLocation = inputEventLocation.getText().toString().trim();
        String registrationOpens = inputRegistrationOpens.getText().toString().trim();
        String registrationDeadline = inputRegistrationDeadline.getText().toString().trim();
        String eventDate = inputEventDate.getText().toString().trim();
        String eventDescription = inputEventDescription.getText().toString().trim();
        boolean assignWaitlistLimit = checkboxWaitlistLimit.isChecked();
        int waitlistLimit = 0;
        String occupantLimitText = inputOccupantLimit.getText().toString().trim();
        int occupantLimit = 100;

        boolean isValid = true;

        // Input validation for each field
        if (eventName.isEmpty()) {
            inputEventName.setError("Event Name is required");
            isValid = false;
        }

        if (eventLocation.isEmpty()) {
            inputEventLocation.setError("Location is required");
            isValid = false;
        }

        if (registrationOpens.isEmpty() || !isValidDateFormat(registrationOpens)) {
            inputRegistrationOpens.setError("Enter a valid date (YYYY-MM-DD)");
            isValid = false;
        }

        if (registrationDeadline.isEmpty() || !isValidDateFormat(registrationDeadline)) {
            inputRegistrationDeadline.setError("Enter a valid date (YYYY-MM-DD)");
            isValid = false;
        }

        if (eventDate.isEmpty() || !isValidDateFormat(eventDate)) {
            inputEventDate.setError("Enter a valid date (YYYY-MM-DD)");
            isValid = false;
        }

        // Parse waitlist limit if checkbox is selected
        if (assignWaitlistLimit) {
            String waitlistLimitText = inputWaitlistLimit.getText().toString().trim();
            if (!TextUtils.isEmpty(waitlistLimitText)) {
                try {
                    waitlistLimit = Integer.parseInt(waitlistLimitText);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid waitlist limit", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        if (!TextUtils.isEmpty(occupantLimitText)) {
            try {
                occupantLimit = Integer.parseInt(occupantLimitText);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid occupant limit", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // If basic validation fails, stop event creation
        if (!isValid) {
            return;
        }

        // Convert date strings to Timestamp
        Timestamp registrationOpensTimestamp = convertToTimestamp(registrationOpens);
        Timestamp registrationDeadlineTimestamp = convertToTimestamp(registrationDeadline);
        Timestamp eventDateTimestamp = convertToTimestamp(eventDate);
        Timestamp currentTimestamp = Timestamp.now();

        // Date-based validations
        if (registrationOpensTimestamp.compareTo(currentTimestamp) <= 0) {
            inputRegistrationOpens.setError("Registration Open Date must be in the future");
            return;
        }

        if (registrationOpensTimestamp.compareTo(registrationDeadlineTimestamp) >= 0) {
            inputRegistrationOpens.setError("Registration Open Date must be before Registration Deadline");
            inputRegistrationDeadline.setError("Registration Deadline must be after Registration Open Date");
            return;
        }

        if (registrationDeadlineTimestamp.compareTo(eventDateTimestamp) >= 0) {
            inputRegistrationDeadline.setError("Registration Deadline must be before Event Date");
            inputEventDate.setError("Event Date must be after Registration Deadline");
            return;
        }

        if (registrationOpensTimestamp.compareTo(eventDateTimestamp) >= 0) {
            inputRegistrationOpens.setError("Registration Open Date must be before Event Date");
            inputEventDate.setError("Event Date must be after Waitlist Open Date");
            return;
        }


        // Create and populate the Event object
        Event event = new Event();
        event.setEventName(eventName);
        event.setLocation(eventLocation);
        event.setWaitlistOpenDate(registrationOpensTimestamp);
        event.setWaitlistDeadline(registrationDeadlineTimestamp);
        event.setEventDate(eventDateTimestamp);
        event.setDescription(eventDescription);
        event.setWaitlistLimitFlag(assignWaitlistLimit);
        event.setWaitlistLimit(assignWaitlistLimit ? waitlistLimit : 0);
        event.setOccupantLimit(occupantLimit);

        // Set organizer ID from Device ID (current user ID)
        String organizerId = dbHelper.getCurrentUserId(); // Retrieve Device ID
        event.setOrganizerId(organizerId); // Set as organizer ID

        if (imageUri != null) {
            dbHelper.uploadPosterImageToFirebase(imageUri, new DatabaseHelper.UploadCallback() {
                @Override
                public void onUploadSuccess(String downloadUrl) {
                    event.setPosterUrl(downloadUrl);  // Set the poster URL
                    saveEvent(event);  // Save event with poster URL
                }

                @Override
                public void onUploadFailure(Exception e) {
                    Toast.makeText(getContext(), "Failed to upload poster image", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Proceed without a poster URL, still save the event
            photoPlaceholderText.setVisibility(View.VISIBLE);
            saveEvent(event);
        }

    }

    // Helper method to save the event
    private void saveEvent(Event event) {
        dbHelper.createEvent(event, new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventsFetched(List<Event> events) {
                Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                // Navigate back to the dashboard
                requireActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onEventFetched(Event event) {
                // Not used in this fragment
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error creating event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidDateFormat(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        format.setLenient(false);
        try {
            format.parse(date);
            return true;  // Date format is valid
        } catch (ParseException e) {
            return false;  // Date format is invalid
        }
    }

    private Timestamp convertToTimestamp(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date parsedDate = format.parse(date);
            return new Timestamp(parsedDate);
        } catch (ParseException e) {
            Log.e("DateConversion", "Invalid date format, using current time as fallback", e);
            return Timestamp.now();  // Default to current time if parsing fails
        }
    }



}

