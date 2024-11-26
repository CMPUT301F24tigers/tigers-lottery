package com.example.tigers_lottery.HostedEvents;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import com.bumptech.glide.Glide;

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

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import androidx.fragment.app.Fragment;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment for editing an event's details within the organizer dashboard.
 * allows for inputs from the organizer and initializes the dbHelper to find the event's
 * current details
 */
public class OrganizerEditEventFragment extends Fragment {

    private EditText inputEventName, inputEventLocation, inputRegistrationOpens, inputRegistrationDeadline, inputEventDate, inputEventDescription, inputWaitlistLimit, inputOccupantLimit;
    private CheckBox checkboxWaitlistLimit, checkboxGeolocationRequired;
    private Button btnSaveEvent;
    private DatabaseHelper dbHelper;
    private Event event; // Store the event being edited


    private ImageView imagePoster;
    private TextView photoPlaceholderText;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private StorageReference storageReference;
    private boolean isImageUpdated = false;

    /**
     * Required empty public constructor
     */
    public OrganizerEditEventFragment() {
    }

    /**
     * Inflates the layout of the edit event screen, retrieves event details from arguments
     * initializes the databaseHelper, and the button to save changes to the event details
     *
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
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
        btnSaveEvent = view.findViewById(R.id.btnCreateEvent);
        inputOccupantLimit = view.findViewById(R.id.inputOccupantLimit);
        imagePoster = view.findViewById(R.id.photoPlaceholder);
        photoPlaceholderText = view.findViewById(R.id.photoPlaceholderText);
        checkboxGeolocationRequired = view.findViewById(R.id.checkboxGeolocationRequired);

        // Hide occupant limit whilst editing
        TextView labelOccupantLimit = view.findViewById(R.id.LabelOccupantLimit);
        labelOccupantLimit.setVisibility(View.GONE);
        inputOccupantLimit.setVisibility(View.GONE);

        // Ensure waitlist fields are hidden by default
        TextView assignWaitlistLabel = view.findViewById(R.id.assignWaitlistLabel);
        checkboxWaitlistLimit.setVisibility(View.GONE);
        inputWaitlistLimit.setVisibility(View.GONE);
        assignWaitlistLabel.setVisibility(View.GONE);

        // Set button text to "Save"
        btnSaveEvent.setText("Save");

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(requireContext());

        // Retrieve event by ID passed in arguments
        if (getArguments() != null && getArguments().containsKey("eventId")) {
            int eventId = getArguments().getInt("eventId"); // Retrieve eventId as an integer
            fetchEventById(eventId);
        }

        // Set up image picker
        setUpImagePicker();

        // Set click listener for Save button
        btnSaveEvent.setOnClickListener(v -> saveEvent());

        return view;
    }


    /**
     * Finds the event that is to be displayed by the fragment and populates its fields
     * @param eventId for event identification.
     */

    private void fetchEventById(int eventId) {
        dbHelper.fetchEventById(eventId, new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventFetched(Event fetchedEvent) {
                event = fetchedEvent; // Assign fetched event to the local variable
                loadEventData(event); // Populate fields with existing event data
            }

            @Override
            public void onEventsFetched(List<Event> events) {
                // Not used in this fragment, but must be implemented
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to load event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *Sets up the image picker for selecting an image from the device.
     * When the user selects an image, it updates the imagePoster ImageView
     * with the selected image and hides the placeholder text.
     */

    private void setUpImagePicker() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        if (imageUri != null) {
                            Log.d("OrganizerEditEvent", "Image URI selected: " + imageUri.toString());

                            // Load the selected image URI immediately into the ImageView
                            Glide.with(requireContext())
                                    .load(imageUri)
                                    .skipMemoryCache(true)    // Ensures fresh load for immediate feedback
                                    .into(imagePoster);

                            photoPlaceholderText.setVisibility(View.GONE);
                            isImageUpdated = true; // Track that the image was updated
                        }
                    }
                }
        );

        imagePoster.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });
    }



    /**
     * Loads the stored event data and populates the edit text fields.
     * @param event to be edited.
     */
    private void loadEventData(Event event) {
        // Populate input fields with event data
        inputEventName.setText(event.getEventName());
        inputEventName.setTextColor(0xFFFFFFFF);
        inputEventLocation.setText(event.getLocation());
        inputEventLocation.setTextColor(0xFFFFFFFF);
        inputRegistrationOpens.setText(formatTimestamp(event.getWaitlistOpenDate()));
        inputRegistrationOpens.setTextColor(0xFFFFFFFF);
        inputRegistrationDeadline.setText(formatTimestamp(event.getWaitlistDeadline()));
        inputRegistrationDeadline.setTextColor(0xFFFFFFFF);
        inputEventDate.setText(formatTimestamp(event.getEventDate()));
        inputEventDate.setTextColor(0xFFFFFFFF);
        inputEventDescription.setText(event.getDescription());
        inputEventDescription.setTextColor(0xFFFFFFFF);

        // Disable checkbox to ensure it doesn’t re-enable the waitlist limit field
        checkboxWaitlistLimit.setChecked(false);
        checkboxWaitlistLimit.setEnabled(false);

        // Hide the waitlist limit input field regardless of any external listener
        inputWaitlistLimit.setVisibility(View.GONE);

        checkboxGeolocationRequired.setChecked(event.isGeolocationRequired());

        // Display existing poster only if no new image was picked
        if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
            loadImageIntoImageView(event.getPosterUrl(), imagePoster);
            photoPlaceholderText.setVisibility(View.GONE);
        } else {
            photoPlaceholderText.setVisibility(View.VISIBLE);
        }

    }

    public void loadImageIntoImageView(String imageUrl, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .skipMemoryCache(true)
                .placeholder(R.drawable.placeholder_image_background) // find a better background later
                .into(imageView);
    }

    /**
     * Validates inputs and saves the entries, event is then repopulated
     * and updated.
     */
    private void saveEvent() {
        // Capture updated values from user input
        String eventName = inputEventName.getText().toString().trim();
        String eventLocation = inputEventLocation.getText().toString().trim();
        String registrationOpens = inputRegistrationOpens.getText().toString().trim();
        String registrationDeadline = inputRegistrationDeadline.getText().toString().trim();
        String eventDate = inputEventDate.getText().toString().trim();
        String eventDescription = inputEventDescription.getText().toString().trim();
        boolean assignWaitlistLimit = checkboxWaitlistLimit.isChecked();
        int waitlistLimit = 0;
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

        // Log the geocoded location
        Log.d("Geolocation", "Resolved location: Latitude = " + geocodedLocation.latitude + ", Longitude = " + geocodedLocation.longitude);

        // Set the updated values to the event object
        event.setEventName(eventName);
        event.setLocation(eventLocation);
        event.setWaitlistOpenDate(convertToTimestamp(registrationOpens));
        event.setWaitlistDeadline(convertToTimestamp(registrationDeadline));
        event.setEventDate(convertToTimestamp(eventDate));
        event.setDescription(eventDescription);
        event.setWaitlistLimitFlag(assignWaitlistLimit);
        event.setWaitlistLimit(assignWaitlistLimit ? waitlistLimit : 0);
        event.setGeolocationRequired(geolocationRequired);

        // Set geolocation (convert LatLng to GeoPoint)
        event.setGeolocation(new com.google.firebase.firestore.GeoPoint(geocodedLocation.latitude, geocodedLocation.longitude));

        if (isImageUpdated && imageUri != null) {
            dbHelper.uploadPosterImageToFirebase(imageUri, new DatabaseHelper.UploadCallback() {
                @Override
                public void onUploadSuccess(String downloadUrl) {
                    event.setPosterUrl(downloadUrl); // Update Event with new poster URL

                    // Load the new poster URL after upload
                    Glide.with(requireContext())
                            .load(downloadUrl)
                            .skipMemoryCache(true)
                            .into(imagePoster);

                    updateEventInDatabase(); // Save event details with new poster URL
                    isImageUpdated = false;
                }

                @Override
                public void onUploadFailure(Exception e) {
                    Toast.makeText(getContext(), "Failed to upload poster image", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            updateEventInDatabase(); // Save event without updating the poster
        }

    }

    /**
     * Updates the event's fields using dbHelper.
     */

    private void updateEventInDatabase() {
        dbHelper.updateEvent(event, new DatabaseHelper.EventsCallback() {
            /**
             * Navigates back to the dashboard and displays the organizer's events
             * @param events list of events from the organizer.
             */
            @Override
            public void onEventsFetched(List<Event> events) {
                Toast.makeText(getContext(), "Event updated successfully", Toast.LENGTH_SHORT).show();
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
             * Handles error during event updating.
             * @param e exception catcher for event updating.
             */

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to update event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Converts the date from timestamp to string format.
     * @param timestamp format of the date.
     * @return string version of the date.
     */

    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(timestamp.toDate());
    }

    /**
     * Converts the date from string to timestamp format.
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
            Log.e("DateConversion", "Invalid date format", e);
            return Timestamp.now(); // Fallback to current time
        }
    }

    /**
     * Validates the date format inputted
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


}