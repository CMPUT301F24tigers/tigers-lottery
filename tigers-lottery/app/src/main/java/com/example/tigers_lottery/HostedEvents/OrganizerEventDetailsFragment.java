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
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrganizerEventDetailsFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private int eventId;
    private DatabaseHelper dbHelper;
    private Event event;
    private boolean isHandlingDecline = false;
    private int lastKnownDeclinedEntrantsSize = 0;

    // UI Components
    private TextView eventTitle, eventDescription, eventLocation, waitlistOpenDate, waitlistCloseDate, eventDate, waitlistLimit;
    private ImageView eventPoster;
    private Button viewRegisteredEntrants, viewWaitlistedEntrants, viewInvitedEntrants, viewDeclinedEntrants, runLotteryButton;

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
        runLotteryButton = view.findViewById(R.id.runLotteryButton);

        // Fetch and display event details
        loadEventDetails();

        // Set up button click listeners for entrant lists
        setupButtonListeners();

        return view;
    }

    private void loadEventDetails() {
        dbHelper.fetchEventById(eventId, new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventFetched(Event fetchedEvent) {
                if (fetchedEvent != null) {
                    event = fetchedEvent;
                    displayEventDetails(event);
                    setupLotteryButton();
                    setupDeclineListener();
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

        // Initialize entrant lists only if they are null and avoid altering the invitedEntrants if isLotteryRan is false
        if (event.getRegisteredEntrants() == null) {
            event.setRegisteredEntrants(new ArrayList<>());
        }
        if (event.getWaitlistedEntrants() == null) {
            event.setWaitlistedEntrants(new ArrayList<>());
        }
        if (event.getDeclinedEntrants() == null) {
            event.setDeclinedEntrants(new ArrayList<>());
        }

        // If the lottery has already been run, ensure invitedEntrants is initialized; otherwise, leave it untouched
        if (event.isLotteryRan()) {
            if (event.getInvitedEntrants() == null) {
                event.setInvitedEntrants(new ArrayList<>());
            }
        }
    }



    private void setupButtonListeners() {
        viewRegisteredEntrants.setOnClickListener(v -> openEntrantFragment(new OrganizerRegisteredEntrantsFragment()));
        viewWaitlistedEntrants.setOnClickListener(v -> openEntrantFragment(new OrganizerWaitingListFragment()));
        viewInvitedEntrants.setOnClickListener(v -> openEntrantFragment(new OrganizerInvitedEntrantsFragment()));
        viewDeclinedEntrants.setOnClickListener(v -> openEntrantFragment(new OrganizerDeclinedEntrantsFragment()));
    }


    private void setupLotteryButton() {
        Timestamp currentTimestamp = Timestamp.now();
        Timestamp oneDayBeforeEvent = new Timestamp(event.getEventDate().getSeconds() - 86400, 0); // Subtract 1 day (86400 seconds)

        // Log state for debugging purposes
        Log.d("LotterySetup", "Current timestamp: " + currentTimestamp);
        Log.d("LotterySetup", "Event date: " + event.getEventDate());
        Log.d("LotterySetup", "One day before event: " + oneDayBeforeEvent);
        Log.d("LotterySetup", "Is lottery already run? " + event.isLotteryRan());

        // Run the lottery automatically if we're exactly 1 day before the event and lottery hasn't run
        if (currentTimestamp.compareTo(oneDayBeforeEvent) >= 0 &&
                currentTimestamp.compareTo(event.getEventDate()) < 0 &&
                !event.isLotteryRan()) {

            // Run the lottery automatically
            runLottery();

            // Alternatively, if manual triggering is also intended, keep button enabled
            runLotteryButton.setEnabled(false);
            runLotteryButton.setOnClickListener(null);

        } else if (event.getWaitlistDeadline().compareTo(currentTimestamp) <= 0 && !event.isLotteryRan()) {
            // Enable the button if it's past the waitlist deadline and before the event date
            runLotteryButton.setEnabled(true);
            runLotteryButton.setOnClickListener(v -> runLottery());
        } else {
            // Disable button and remove listener if conditions don't allow the lottery to be run
            runLotteryButton.setEnabled(false);
            runLotteryButton.setOnClickListener(null);
        }
    }



    private void runLottery() {
        if (event.isLotteryRan()) {
            Toast.makeText(getContext(), "Lottery has already been run for this event.", Toast.LENGTH_SHORT).show();
            return; // Exit if the lottery has already been run
        }

        dbHelper.fetchWaitlistedEntrants(event.getEventId(), new DatabaseHelper.EntrantsCallback() {
            @Override
            public void onEntrantsFetched(List<String> waitlistedEntrants) {
                int occupantLimit = event.getOccupantLimit();
                List<String> invitedEntrants = dbHelper.selectRandomEntrants(waitlistedEntrants, occupantLimit);

                // Update Firestore with invited entrants and mark lottery as run
                dbHelper.updateInvitedEntrantsAndSetLotteryRan(event.getEventId(), invitedEntrants, new DatabaseHelper.EventsCallback() {
                    @Override
                    public void onEventsFetched(List<Event> events) {
                        Toast.makeText(getContext(), "Lottery run successfully!", Toast.LENGTH_SHORT).show();
                        runLotteryButton.setEnabled(false); // Disable button after running
                        event.setLotteryRan(true); // Mark the event's lottery as run
                    }

                    @Override
                    public void onEventFetched(Event event) { }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getContext(), "Error running lottery: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error fetching waitlisted entrants", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupDeclineListener() {
        dbHelper.addDeclineListener(eventId, new DatabaseHelper.DeclineCallback() {
            @Override
            public void onDeclineDetected(List<String> declinedEntrants) {
                // Only trigger decline handling if the declined entrants size has increased
                if (!isHandlingDecline && declinedEntrants.size() > lastKnownDeclinedEntrantsSize) {
                    isHandlingDecline = true;
                    lastKnownDeclinedEntrantsSize = declinedEntrants.size(); // Update the known size
                    fetchEventDetails(); // Proceed to handle the actual decline
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("DeclineListener", "Error in handling declines: ", e);
            }
        });
    }


    // Helper function to fetch event details, this is so that we can handle the case of finding someone new to invite when an entrant declines an invitation
    private void fetchEventDetails() {
        dbHelper.fetchEventById(eventId, new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventFetched(Event event) {
                if (event != null) {
                    handleDeclineLogic(event);
                }
            }

            @Override
            public void onEventsFetched(List<Event> events) {
                // Not needed here
            }

            @Override
            public void onError(Exception e) {
                Log.e("EventDetails", "Error fetching updated event details", e);
            }
        });
    }


    private void handleDeclineLogic(Event event) {
        if (event.getInvitedEntrants().size() < event.getOccupantLimit()) {
            List<String> waitlistedEntrants = event.getWaitlistedEntrants();

            if (!waitlistedEntrants.isEmpty()) {
                String newInvitee = selectRandomEntrant(waitlistedEntrants);
                event.getInvitedEntrants().add(newInvitee);
                waitlistedEntrants.remove(newInvitee);

                // Update Firestore with the modified lists
                dbHelper.updateEntrantsAfterDecline(eventId, event.getInvitedEntrants(), waitlistedEntrants, new DatabaseHelper.EventsCallback() {
                    @Override
                    public void onEventsFetched(List<Event> events) {
                        Log.d("EventUpdate", "Entrants updated after handling decline.");
                        isHandlingDecline = false; // Reset the flag once the update is complete
                    }

                    @Override
                    public void onEventFetched(Event event) {
                        // Not needed
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("EventUpdate", "Failed to update entrants.", e);
                        isHandlingDecline = false; // Ensure flag is reset even if an error occurs
                    }
                });
            } else {
                isHandlingDecline = false; // No more waitlisted entrants to add, reset the flag
            }
        } else {
            isHandlingDecline = false; // Occupant limit is met, reset the flag
        }
    }


    // Helper function to select a random entrant from waitlistedEntrants (similar to the method in db helper but this is simpler as its just for 1 entrant
    private String selectRandomEntrant(List<String> waitlistedEntrants) {
        int index = new Random().nextInt(waitlistedEntrants.size());
        return waitlistedEntrants.get(index);
    }


    private void openEntrantFragment(Fragment fragment) {
        Bundle args = new Bundle();
        args.putInt("event_id", eventId); // Pass only eventId
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}
