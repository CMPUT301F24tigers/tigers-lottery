package com.example.tigers_lottery.HostedEvents;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;

import java.util.ArrayList;
import java.util.List;

public class OrganizerEventDetailsFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private int eventId;
    private DatabaseHelper dbHelper;
    private Event event;

    // UI Components
    private TextView eventTitle, eventDescription, eventLocation, waitlistOpenDate, waitlistCloseDate, eventDate, waitlistLimit;
    private ImageView eventPoster;
    private Button viewRegisteredEntrants, viewWaitlistedEntrants, viewInvitedEntrants, viewDeclinedEntrants;

    public OrganizerEventDetailsFragment() {}

    public static OrganizerEventDetailsFragment newInstance(int eventId) {
        OrganizerEventDetailsFragment fragment = new OrganizerEventDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getInt(ARG_EVENT_ID);
        }
        dbHelper = new DatabaseHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_eventdetails_fragment, container, false);

        // Initialize UI components
        eventTitle = view.findViewById(R.id.eventTitle);
        eventDescription = view.findViewById(R.id.eventDescription);
        eventLocation = view.findViewById(R.id.eventLocation);
        waitlistOpenDate = view.findViewById(R.id.waitlistOpenDate);
        waitlistCloseDate = view.findViewById(R.id.waitlistCloseDate);
        eventDate = view.findViewById(R.id.eventDate);
        waitlistLimit = view.findViewById(R.id.waitlistLimit);
        viewRegisteredEntrants = view.findViewById(R.id.viewRegisteredEntrants);
        viewWaitlistedEntrants = view.findViewById(R.id.viewWaitlistedEntrants);
        viewInvitedEntrants = view.findViewById(R.id.viewInvitedEntrants);
        viewDeclinedEntrants = view.findViewById(R.id.viewDeclinedEntrants);

        // Fetch and display event details
        loadEventDetails();

        // Set up button click listeners for entrant lists
        setupButtonListeners();

        return view;
    }

    private void loadEventDetails() {
        dbHelper.fetchEventById(eventId, new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventFetched(Event event) {
                if (event != null) {
                    displayEventDetails(event);
                    Log.d("EventDetails", "Event data loaded successfully"); // Log for debugging
                } else {
                    Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }

            @Override
            public void onEventsFetched(List<Event> events) {
                // Not used here
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error fetching event details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("EventDetails", "Error fetching event details", e); // Log error
            }
        });
    }


    private void displayEventDetails(Event event) {
        eventTitle.setText(event.getEventName());
        eventDescription.setText(event.getDescription());
        eventLocation.setText("Location: " + event.getLocation());
        waitlistOpenDate.setText("Waitlist Open Date: " + event.getFormattedWaitlistOpenDate());
        waitlistCloseDate.setText("Waitlist Close Date: " + event.getFormattedWaitlistDeadline());
        eventDate.setText("Event Date: " + event.getFormattedEventDate());
        waitlistLimit.setText("Waitlist Limit: " + (event.isWaitlistLimitFlag() ? event.getWaitlistLimit() : "N/A"));
    }

    private void setupButtonListeners() {
        viewRegisteredEntrants.setOnClickListener(v -> openEntrantFragment(new OrganizerRegisteredEntrantsFragment(), event.getRegisteredEntrants()));
        viewWaitlistedEntrants.setOnClickListener(v -> openEntrantFragment(new OrganizerWaitingListFragment(), event.getWaitlistedEntrants()));
        viewInvitedEntrants.setOnClickListener(v -> openEntrantFragment(new OrganizerInvitedEntrantsFragment(), event.getInvitedEntrants()));
        viewDeclinedEntrants.setOnClickListener(v -> openEntrantFragment(new OrganizerDeclinedEntrantsFragment(), event.getDeclinedEntrants()));
    }

    private void openEntrantFragment(Fragment fragment, List<String> entrants) {
        Bundle args = new Bundle();
        args.putStringArrayList("entrants_list", new ArrayList<>(entrants));
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView2, fragment)
                .addToBackStack(null)
                .commit();
    }
}
