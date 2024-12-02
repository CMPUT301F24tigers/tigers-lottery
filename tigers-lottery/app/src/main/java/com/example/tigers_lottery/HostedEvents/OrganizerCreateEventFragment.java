package com.example.tigers_lottery.HostedEvents;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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

import androidx.fragment.app.Fragment;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Fragment used by the organizer to create a new event.
 */

public class OrganizerCreateEventFragment extends Fragment {

    private EditText inputEventName, inputEventLocation, inputEventDescription, inputWaitlistLimit, inputOccupantLimit;
    private TextView inputRegistrationOpens, inputRegistrationDeadline, inputEventDate;
    private CheckBox checkboxWaitlistLimit, checkboxGeolocationRequired;
    private Button btnCreateEvent;
    private DatabaseHelper dbHelper;
    private ImageView imagePoster;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private StorageReference storageReference;
    private TextView photoPlaceholderText;


    /**
     * Required empty public constructor
     */

    public OrganizerCreateEventFragment() {
    }

    /**
     * Inflates the layout of the create event screen, allows
     * for inputs from the organizer and initializes the dbHelper.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the view for the fragment's UI.
     */

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
        checkboxGeolocationRequired = view.findViewById(R.id.checkboxGeolocationRequired);


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
        btnCreateEvent.setOnClickListener(
                v -> createEvent()

        );

        inputRegistrationOpens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(inputRegistrationOpens);
            }
        });

        inputRegistrationDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(inputRegistrationDeadline);
            }
        });

        inputEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(inputEventDate);
            }
        });

        return view;
    }

    /**
     * Creates a new event using the input parameters and validates the entries
     * Event is then populated and organizerId is set from current userId
     */


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
        boolean geolocationRequired = checkboxGeolocationRequired.isChecked();

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
            Toast.makeText(getContext(), "Registration open date is empty!", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (registrationDeadline.isEmpty() || !isValidDateFormat(registrationDeadline)) {
            Toast.makeText(getContext(),"Registration deadline date is empty!", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (eventDate.isEmpty() || !isValidDateFormat(eventDate)) {
            Toast.makeText(getContext(), "Event date is empty!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(),"Registration Open Date must be in the future", Toast.LENGTH_SHORT).show();
            return;
        }

        if (registrationOpensTimestamp.compareTo(registrationDeadlineTimestamp) >= 0) {
            Toast.makeText(getContext(), "Registration Open Date must be before Registration Deadline",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (registrationDeadlineTimestamp.compareTo(eventDateTimestamp) >= 0) {
            Toast.makeText(getContext(), "Registration Deadline must be before Event Date",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (registrationOpensTimestamp.compareTo(eventDateTimestamp) >= 0) {
            Toast.makeText(getContext(), "Registration Open Date must be before Event Date",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate address
        if (!validateAddress(eventLocation)) {
            inputEventLocation.setError("Please enter a valid address.");
            return;
        }

        // Convert validated address to LatLng
        LatLng geocodedLocation = getLatLngFromAddress(eventLocation);

        if (geocodedLocation == null) {
            inputEventLocation.setError("Failed to resolve address to location.");
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
        event.setGeolocationRequired(geolocationRequired);
        // Set geolocation (convert LatLng to GeoPoint)
        event.setGeolocation(new com.google.firebase.firestore.GeoPoint(geocodedLocation.latitude, geocodedLocation.longitude));
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
                    if (e instanceof StorageException) {
                        StorageException se = (StorageException) e;
                        Log.e("FirebaseStorage", "Error Code: " + se.getErrorCode());
                        Log.e("FirebaseStorage", "Cause: " + se.getCause());
                        Log.e("FirebaseStorage", "Message: " + se.getMessage());
                    }
                    Toast.makeText(getContext(), "Failed to upload poster image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            });
        } else {
            // Proceed without a poster URL, still save the event
            photoPlaceholderText.setVisibility(View.VISIBLE);
            saveEvent(event);
        }

    }

    /**
     * Helper method to save the event made.
     * @param event to be saved.
     */
    private void saveEvent(Event event) {
        dbHelper.createEvent(event, new DatabaseHelper.EventsCallback() {
            /**
             * Navigates back to the dashboard and displays the organizer's events.
             * @param events list of events from the organizer.
             */
            @Override
            public void onEventsFetched(List<Event> events) {
                Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            }

            /**
             * Unused in this fragment.
             * @param event single event.
             */

            @Override
            public void onEventFetched(Event event) {
            }

            /**
             * Handles error during event creation.
             * @param e exception catcher for event creation.
             */

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error creating event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Validates the date format inputted.
     * @param date in the format "YYYY-MM-DD"
     * @return true if the date is valid. false otherwise.
     */

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

    /**
     * Converts the date from string into timestamp format.
     *
     * @param date the valid date.
     * @return a timestamp format of the date.
     */

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

    private boolean validateAddress(String address) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            return addresses != null && !addresses.isEmpty();
        } catch (IOException e) {
            Log.e("GeocoderError", "Error validating address", e);
            return false;
        }
    }

    private LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            Log.d("Geocoder", "Resolving address: " + address);
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                Log.d("Geocoder", "Resolved LatLng: Latitude = " + location.getLatitude() + ", Longitude = " + location.getLongitude());
                return new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                Log.w("Geocoder", "No results for address: " + address);
            }
        } catch (IOException e) {
            Log.e("GeocoderError", "Error resolving address to LatLng", e);
        }
        return null;
    }

    private void openDatePicker(TextView dateField) {
        // Use the current date as the default date in the picker
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Save selected day, month, and year
//                    this.selectedYear = selectedYear;
//                    this.selectedMonth = selectedMonth + 1; // Month is 0-indexed in Calendar
//                    this.selectedDay = selectedDay;
                    String dateText = String.format(Locale.getDefault(), "%d-%02d-%02d", selectedYear, selectedMonth+1, selectedDay);
                    dateField.setText(dateText);
                }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

}

