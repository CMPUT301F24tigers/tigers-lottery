package com.example.tigers_lottery.HostedEvents;

import android.os.Bundle;
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
import android.widget.Toast;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrganizerEditEventFragment extends Fragment {

    private EditText inputEventName, inputEventLocation, inputRegistrationOpens, inputRegistrationDeadline, inputEventDate, inputEventDescription, inputWaitlistLimit;
    private CheckBox checkboxWaitlistLimit;
    private Button btnSaveEvent;
    private DatabaseHelper dbHelper;
    private Event event; // Store the event being edited

    public OrganizerEditEventFragment() {
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
        btnSaveEvent = view.findViewById(R.id.btnCreateEvent);

        // Set button text to "Save"
        btnSaveEvent.setText("Save");

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(requireContext());

        // Retrieve event from arguments
        if (getArguments() != null && getArguments().getSerializable("event") != null) {
            event = (Event) getArguments().getSerializable("event");
            loadEventData(event); // Populate fields with existing event data
        }

        // Set click listener for Save button
        btnSaveEvent.setOnClickListener(v -> saveEvent());

        return view;
    }

    private void loadEventData(Event event) {
        // Populate input fields with event data
        inputEventName.setText(event.getEventName());
        inputEventLocation.setText(event.getLocation());
        inputRegistrationOpens.setText(formatTimestamp(event.getWaitlistOpenDate()));
        inputRegistrationDeadline.setText(formatTimestamp(event.getWaitlistDeadline()));
        inputEventDate.setText(formatTimestamp(event.getEventDate()));
        inputEventDescription.setText(event.getDescription());
        checkboxWaitlistLimit.setChecked(event.isWaitlistLimitFlag());
        inputWaitlistLimit.setText(String.valueOf(event.getWaitlistLimit()));
        inputWaitlistLimit.setVisibility(event.isWaitlistLimitFlag() ? View.VISIBLE : View.GONE);
    }

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

        // Set the updated values to the event object
        event.setEventName(eventName);
        event.setLocation(eventLocation);
        event.setWaitlistOpenDate(convertToTimestamp(registrationOpens));
        event.setWaitlistDeadline(convertToTimestamp(registrationDeadline));
        event.setEventDate(convertToTimestamp(eventDate));
        event.setDescription(eventDescription);
        event.setWaitlistLimitFlag(assignWaitlistLimit);
        event.setWaitlistLimit(assignWaitlistLimit ? waitlistLimit : 0);

        dbHelper.updateEvent(event, new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventsFetched(List<Event> events) {
                // Display success message
                Toast.makeText(getContext(), "Event updated successfully", Toast.LENGTH_SHORT).show();

                // Navigate back to OrganizerDashboardFragment
                requireActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onEventFetched(Event event) {
            }

            @Override
            public void onError(Exception e) {
            }
        });

    }

    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(timestamp.toDate());
    }

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

}
