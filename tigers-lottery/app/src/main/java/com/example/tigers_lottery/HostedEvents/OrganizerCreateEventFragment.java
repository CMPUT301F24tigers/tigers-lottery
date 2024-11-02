package com.example.tigers_lottery.HostedEvents;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrganizerCreateEventFragment extends Fragment {

    private EditText inputEventName, inputEventLocation, inputRegistrationOpens, inputRegistrationDeadline, inputEventDate, inputEventDescription, inputWaitlistLimit;
    private CheckBox checkboxWaitlistLimit;
    private Button btnCreateEvent;
    private DatabaseHelper dbHelper;

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

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper();

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

        // Validation for required fields
        if (TextUtils.isEmpty(eventName) || TextUtils.isEmpty(eventLocation) || TextUtils.isEmpty(registrationOpens) ||
                TextUtils.isEmpty(registrationDeadline) || TextUtils.isEmpty(eventDate)) {
            Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
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

        // Convert dates to Timestamp objects
        Timestamp registrationOpensTimestamp = parseDateToTimestamp(registrationOpens);
        Timestamp registrationDeadlineTimestamp = parseDateToTimestamp(registrationDeadline);
        Timestamp eventDateTimestamp = parseDateToTimestamp(eventDate);

        if (registrationOpensTimestamp == null || registrationDeadlineTimestamp == null || eventDateTimestamp == null) {
            Toast.makeText(getContext(), "Invalid date format. Please use YYYY-MM-DD", Toast.LENGTH_SHORT).show();
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

        // Save the event using DatabaseHelper
        dbHelper.createEvent(event, new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventsFetched(List<Event> events) {
                Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                // Navigate back to the dashboard
                requireActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error creating event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to convert date string to Timestamp
    private Timestamp parseDateToTimestamp(String dateStr) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr);
            return new Timestamp(date);
        } catch (Exception e) {
            return null; // Invalid date format
        }
    }
}
