package com.example.tigers_lottery.HostedEvents;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fragment used by the organizer to view their event's details.
 */
public class OrganizerEventDetailsFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private int eventId;
    private DatabaseHelper dbHelper;
    private Event event;
    private boolean isHandlingDecline = false;
    private int lastKnownDeclinedEntrantsSize = 0;
    private boolean isInitialDeclineSync = true;

    // UI Components
    private TextView eventTitle, eventDescription, eventLocation, waitlistOpenDate, waitlistCloseDate, eventDate, waitlistLimit;
    private ImageView eventPoster;
    private Button viewRegisteredEntrants, viewWaitlistedEntrants, viewInvitedEntrants, viewDeclinedEntrants, runLotteryButton, clearListsButton, viewMapButton;

    /**
     * Required empty public constructor.
     */

    public OrganizerEventDetailsFragment() {}

    /**
     * Creates a new instance of the organizer's event details identifying the event
     * to be seen by its id.
     *
     * @param eventId to identify which event to setup the details page for.
     * @return fragment of the specific event's details
     */

    public static OrganizerEventDetailsFragment newInstance(int eventId) {
        OrganizerEventDetailsFragment fragment = new OrganizerEventDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes the databaseHelper and retrieves the arguments for the event using its
     * eventID.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getInt(ARG_EVENT_ID);
        }
        dbHelper = new DatabaseHelper(requireContext());
    }

    /**
     * Inflates the layout of the event details screen, runs the button listeners
     * and loads the event details.
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
        eventPoster = view.findViewById(R.id.eventPoster);
        clearListsButton = view.findViewById(R.id.clearListsButton);
        viewMapButton = view.findViewById(R.id.viewMapButton);

        // Fetch and display event details
        loadEventDetails();

        // Set up button click listeners for entrant lists
        setupButtonListeners();

        return view;
    }

    /**
     * Loads the event details using the databaseHelper instance and the eventId.
     */

    private void loadEventDetails() {
        dbHelper.fetchEventById(eventId, new DatabaseHelper.EventsCallback() {
            /**
             * Handles event fetching from db, if loaded successfully the lottery
             * button becomes available.
             *
             * @param fetchedEvent whose details are to be taken
             */
            @Override
            public void onEventFetched(Event fetchedEvent) {
                if (fetchedEvent != null) {
                    event = fetchedEvent;
                    displayEventDetails(event);
                    setupLotteryButton();
                    setupDeclineListener();
                    setupClearListsButton();
                    Log.d("EventDetails", "Event data loaded successfully");
                } else {
                    Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }

            /**
             * Unused, required.
             * @param events organizer's list of events.
             */

            @Override
            public void onEventsFetched(List<Event> events) {
                // Not used here
            }

            /**
             * Handles error during event detail loading.
             * @param e exception catcher for event creation.
             */

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error fetching event details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("EventDetails", "Error fetching event details", e); // Log error
            }
        });
    }

    /**
     * Sets the event's details to the specific fields, and displays them.
     * Required entrant lists are initialized if they are null. If the lottery is ran;
     * Invited entrants is initialized, otherwise it is untouched.
     *
     * @param event whose details have to be displayed
     */


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

        // Load the poster image if URL is available
        if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
            Glide.with(this)
                    .load(event.getPosterUrl())
                    .placeholder(R.drawable.placeholder_image_background) // find a better background later
                    .into(eventPoster);
        }
    }

    private void setupClearListsButton() {
        Timestamp currentTimestamp = Timestamp.now();

        // Enable the button only if the event date has passed
        if (currentTimestamp.compareTo(event.getEventDate()) >= 0) {
            clearListsButton.setEnabled(true);
            clearListsButton.setOnClickListener(v -> clearEntrantLists());
        } else {
            clearListsButton.setEnabled(false);
        }
    }

    private void clearEntrantLists() {
        event.setWaitlistedEntrants(new ArrayList<>()); // Clear the lists in the Event object
        event.setInvitedEntrants(new ArrayList<>());
        event.setDeclinedEntrants(new ArrayList<>());

        dbHelper.clearEventLists(event.getEventId(), new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventsFetched(List<Event> events) {
                Toast.makeText(getContext(), "Lists cleared successfully!", Toast.LENGTH_SHORT).show();
                clearListsButton.setEnabled(false); // Disable after clearing
            }

            @Override
            public void onEventFetched(Event event) {
                // Not used in this context
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error clearing lists: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Button listeners for getting to entrant list screens tied to the event.
     */
    private void setupButtonListeners() {
        viewRegisteredEntrants.setOnClickListener(v -> openEntrantFragment(new OrganizerRegisteredEntrantsFragment()));
        viewWaitlistedEntrants.setOnClickListener(v -> openEntrantFragment(new OrganizerWaitingListFragment()));
        viewInvitedEntrants.setOnClickListener(v -> openEntrantFragment(new OrganizerInvitedEntrantsFragment()));
        viewDeclinedEntrants.setOnClickListener(v -> openEntrantFragment(new OrganizerDeclinedEntrantsFragment()));
        viewMapButton.setOnClickListener(v -> openMapFragment());
    }

    /**
     * Sets up the lottery button, the lottery is set to run automatically if it is
     * exactly 1 day from the event and the lottery hasn't been ran. The button is enabled
     * if the waiting list has been closed and the event has not happened.
     */

    private void setupLotteryButton() {
        Timestamp currentTimestamp = Timestamp.now();
        Timestamp oneDayBeforeEvent = new Timestamp(event.getEventDate().getSeconds() - 86400, 0); // Subtract 1 day (86400 seconds)

        // Log state for debugging purposes
        Log.d("LotterySetup", "Current timestamp: " + currentTimestamp);
        Log.d("LotterySetup", "Event date: " + event.getEventDate());
        Log.d("LotterySetup", "One day before event: " + oneDayBeforeEvent);
        Log.d("LotterySetup", "Is lottery already run? " + event.isLotteryRan());

        // Log the waitlist deadline comparison
        Log.d("LotteryDebug", "Waitlist Deadline: " + event.getWaitlistDeadline());
        Log.d("LotteryDebug", "Is current time after waitlist deadline? " + (currentTimestamp.compareTo(event.getWaitlistDeadline()) >= 0));


        // Run the lottery automatically if we're exactly 1 day before the event and lottery hasn't run
        if (currentTimestamp.compareTo(oneDayBeforeEvent) >= 0 &&
                currentTimestamp.compareTo(event.getEventDate()) < 0 &&
                !event.isLotteryRan()) {

            Log.d("LotteryDebug", "Automatic lottery is being triggered!");
            // Run the lottery automatically
            runLottery();

            // Alternatively, if manual triggering is also intended, keep button enabled
            runLotteryButton.setEnabled(false);
            runLotteryButton.setOnClickListener(null);

        } else if (event.getWaitlistDeadline().compareTo(currentTimestamp) <= 0 && !event.isLotteryRan()) {
            // Enable the button if it's past the waitlist deadline and before the event date
            Log.d("LotteryDebug", "Lottery button enabled.");
            runLotteryButton.setEnabled(true);
            runLotteryButton.setOnClickListener(v -> runLottery());
        } else {
            // Disable button and remove listener if conditions don't allow the lottery to be run
            Log.d("LotteryDebug", "Lottery button disabled.");
            runLotteryButton.setEnabled(false);
            runLotteryButton.setOnClickListener(null);
        }
    }

    /**
     * Runs the lottery system fetching from the list of waitlisted entrants under the limit.
     * The database is then updated with the invited entrants and the lottery is marked as ran.
     */


    private void runLottery() {
        Log.d("LotteryDebug", "runLottery called.");
        Log.d("LotteryDebug", "Current state of isLotteryRan: " + event.isLotteryRan());


        if (event.isLotteryRan()) {
            Log.d("LotteryDebug", "Lottery already run, exiting method.");
            Toast.makeText(getContext(), "Lottery has already been run for this event.", Toast.LENGTH_SHORT).show();
            return; // Exit if the lottery has already been run
        }

        dbHelper.fetchWaitlistedEntrants(event.getEventId(), new DatabaseHelper.EntrantsCallback() {
            /**
             * Selects random entrants with the databaseHelper using
             * @param waitlistedEntrants list of entrants in the waitingList for the event.
             */
            @Override
            public void onEntrantsFetched(List<String> waitlistedEntrants) {
                Log.d("LotteryDebug", "Fetched waitlisted entrants: " + waitlistedEntrants);
                int occupantLimit = event.getOccupantLimit();
                List<String> invitedEntrants = dbHelper.selectRandomEntrants(waitlistedEntrants, occupantLimit);
                Log.d("LotteryDebug", "Selected invited entrants: " + invitedEntrants);


                // Update Firestore with invited entrants and mark lottery as run
                dbHelper.updateInvitedEntrantsAndSetLotteryRan(event.getEventId(), invitedEntrants, new DatabaseHelper.EventsCallback() {
                    /**
                     * The runLottery button is disabled and the lottery is marked as ran.
                     * @param events organizer's events
                     */
                    @Override
                    public void onEventsFetched(List<Event> events) {
                        Log.d("LotteryDebug", "Lottery successfully updated in Firestore.");
                        Toast.makeText(getContext(), "Lottery run successfully!", Toast.LENGTH_SHORT).show();
                        runLotteryButton.setEnabled(false); // Disable button after running
                        event.setLotteryRan(true); // Mark the event's lottery as run
                    }

                    /**
                     * Unused, required.
                     * @param event selected.
                     */

                    @Override
                    public void onEventFetched(Event event) { }

                    /**
                     * Handles error when running the lottery.
                     * @param e exception catcher for event creation.
                     */

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getContext(), "Error running lottery: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            /**
             * Handles error when fetching the waitlist.
             * @param e exception catcher for waitlist fetching.
             */

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error fetching waitlisted entrants", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles the decline choice for the entrants.
     */
    private void setupDeclineListener() {
        dbHelper.addDeclineListener(eventId, new DatabaseHelper.DeclineCallback() {
            @Override
            public void onDeclineDetected(List<String> declinedEntrants) {
                // Skip handling during the initial listener sync
                if (isInitialDeclineSync) {
                    Log.d("DeclineDebug", "Initial sync detected. Skipping decline handling.");
                    isInitialDeclineSync = false; // Set flag to false after the first sync
                    lastKnownDeclinedEntrantsSize = declinedEntrants.size(); // Update known size
                    return;
                }

                Log.d("DeclineDebug", "Decline detected. Declined entrants: " + declinedEntrants);
                Log.d("DeclineDebug", "Previous size: " + lastKnownDeclinedEntrantsSize + ", Current size: " + declinedEntrants.size());
                Log.d("DeclineDebug", "isHandlingDecline flag: " + isHandlingDecline);

                if (!isHandlingDecline && declinedEntrants.size() > lastKnownDeclinedEntrantsSize) {
                    isHandlingDecline = true;
                    lastKnownDeclinedEntrantsSize = declinedEntrants.size(); // Update the known size
                    fetchEventDetails(); // Proceed to handle the actual decline
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("DeclineDebug", "Error in decline listener: ", e);
            }
        });
    }

    /**
     * Helper function to fetch event details,this is so that we can
     * handle the case of finding someone new to invite when an entrant declines an invitation
     */
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
        Log.d("DeclineDebug", "Handling decline for event: " + event.getEventName());
        Log.d("DeclineDebug", "Current invited entrants: " + event.getInvitedEntrants());
        Log.d("DeclineDebug", "Current waitlisted entrants: " + event.getWaitlistedEntrants());
        Log.d("DeclineDebug", "Occupant limit: " + event.getOccupantLimit());
        if (event.getInvitedEntrants().size() < event.getOccupantLimit()) {
            List<String> waitlistedEntrants = event.getWaitlistedEntrants();

            if (!waitlistedEntrants.isEmpty()) {
                String newInvitee = selectRandomEntrant(waitlistedEntrants);
                Log.d("DeclineDebug", "Selected new invitee: " + newInvitee);
                event.getInvitedEntrants().add(newInvitee);
                waitlistedEntrants.remove(newInvitee);

                // Update Firestore with the modified lists
                dbHelper.updateEntrantsAfterDecline(eventId, event.getInvitedEntrants(), waitlistedEntrants, new DatabaseHelper.EventsCallback() {
                    @Override
                    public void onEventsFetched(List<Event> events) {
                        Log.d("DeclineDebug", "Entrants updated after handling decline.");
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
                Log.d("DeclineDebug", "No waitlisted entrants to invite.");
                isHandlingDecline = false; // No more waitlisted entrants to add, reset the flag
            }
        } else {
            Log.d("DeclineDebug", "Occupant limit reached, no action needed.");
            isHandlingDecline = false; // Occupant limit is met, reset the flag
        }
    }


    /**
     * Helper function to select a random entrant from waitlistedEntrants
     * (similar to the method in db helper but this is simpler as its just for 1 entrant
     * @param waitlistedEntrants list of entrants in the waitlist
     * @return id of the chosen waitlisted entrant
     */
    private String selectRandomEntrant(List<String> waitlistedEntrants) {
        int index = new Random().nextInt(waitlistedEntrants.size());
        return waitlistedEntrants.get(index);
    }

    /**
     * Opens the entrant fragment and passes the event ID as argument
     * @param fragment to be opened.
     */

    private void openEntrantFragment(Fragment fragment) {
        Bundle args = new Bundle();
        args.putInt("event_id", eventId); // Pass only eventId
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openMapFragment() {
        OrganizerMapFragment mapFragment = OrganizerMapFragment.newInstance(eventId);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment_container, mapFragment)
                .addToBackStack(null)
                .commit();
    }


}