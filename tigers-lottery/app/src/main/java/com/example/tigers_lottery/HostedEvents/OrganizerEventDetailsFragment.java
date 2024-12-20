package com.example.tigers_lottery.HostedEvents;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.example.tigers_lottery.utils.QRCodeGenerator;
import com.google.firebase.Timestamp;
import com.google.zxing.qrcode.encoder.QRCode;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
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
    private TextView eventTitle, eventDescription, eventLocation, waitlistOpenDate, waitlistCloseDate, eventDate, waitlistLimit, entrantLimit;
    private ImageView eventPoster, viewQRCodeImage;
    private Button viewRegisteredEntrants, viewWaitlistedEntrants, viewInvitedEntrants, viewDeclinedEntrants;
    private LinearLayout runLotteryButton, viewQRCode, viewMapButton, clearListsButton;
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

    @SuppressLint("MissingInflatedId")
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
        entrantLimit = view.findViewById(R.id.entrantLimit);
        viewRegisteredEntrants = view.findViewById(R.id.viewRegisteredEntrants);
        viewWaitlistedEntrants = view.findViewById(R.id.viewWaitlistedEntrants);
        viewInvitedEntrants = view.findViewById(R.id.viewInvitedEntrants);
        viewDeclinedEntrants = view.findViewById(R.id.viewDeclinedEntrants);
        runLotteryButton = view.findViewById(R.id.runLotteryButton);
        eventPoster = view.findViewById(R.id.eventPoster);
        clearListsButton = view.findViewById(R.id.clearListsButton);
        viewQRCode = view.findViewById(R.id.viewQRCodeButton);
        viewMapButton = view.findViewById(R.id.viewMapButton);
        viewQRCodeImage = view.findViewById(R.id.viewQRCodeButtonImage);

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
                    if(event.getQRCode() != null && !(event.getQRCode().isEmpty())){
                        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator(event);
                        qrCodeGenerator.setHashData();
                        Bitmap QRcode = qrCodeGenerator.generateQRCodeFromHashData();
                        viewQRCodeImage.setImageBitmap(QRcode);
                    }else{
                        viewQRCodeImage.setImageResource(R.drawable.default_qr_code);
                    }
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
        String dateSplitEventDate = event.getFormattedEventDate().split(" - ")[0];
        String dateSplitWaitlistOpen = event.getFormattedWaitlistOpenDate().split(" - ")[0];
        String dateSplitWaitlistDeadline = event.getFormattedWaitlistDeadline().split(" - ")[0];
        waitlistOpenDate.setText("Waitlist Open Date: " + dateSplitWaitlistOpen);
        waitlistCloseDate.setText("Waitlist Close Date: " + dateSplitWaitlistDeadline);
        eventDate.setText("Event Date: " + dateSplitEventDate);
        waitlistLimit.setText("Waitlist Limit: " + (event.isWaitlistLimitFlag() ? event.getWaitlistLimit() : "N/A"));
        entrantLimit.setText("Entrant Limit: " + (event.getOccupantLimit()));

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
                    .placeholder(R.drawable.event_poster_placeholder) // find a better background later
                    .into(eventPoster);
        }
    }

    /**
     * Sets up the clear list button as clickable.
     */

    private void setupClearListsButton() {
        Timestamp currentTimestamp = Timestamp.now();

        // Enable the button only if the event date has passed
        if (currentTimestamp.compareTo(event.getEventDate()) >= 0) {
            clearListsButton.setBackgroundResource(R.drawable.square_button_background);
            clearListsButton.setEnabled(true);
            clearListsButton.setOnClickListener(v -> clearEntrantLists());
        } else {
            clearListsButton.setBackgroundResource(R.drawable.square_button_background_disabled);
            clearListsButton.setEnabled(false);
        }
    }

    /**
     * Clears the waitlisted, invited and declined lists (meant for after registration
     * for the event is closed and the event is about to start).
     */

    private void clearEntrantLists() {
        event.setWaitlistedEntrants(new ArrayList<>()); // Clear the lists in the Event object
        event.setInvitedEntrants(new ArrayList<>());
        event.setDeclinedEntrants(new ArrayList<>());

        dbHelper.clearEventLists(event.getEventId(), new DatabaseHelper.EventsCallback() {
            /**
             * On finding the event, and clearing the list, the clear list button is disabled.
             * @param events to be cleared.
             */
            @Override
            public void onEventsFetched(List<Event> events) {
                Toast.makeText(getContext(), "Lists cleared successfully!", Toast.LENGTH_SHORT).show();
                clearListsButton.setEnabled(false); // Disable after clearing
            }

            /**
             * Event fetched, required dbHelper method, unused.
             * @param event event.
             */

            @Override
            public void onEventFetched(Event event) {
                // Not used in this context
            }

            /**
             * Handles error in clearing lists.
             * @param e exception catcher.
             */

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
        viewQRCode.setOnClickListener(v -> openEntrantFragment(new OrganizerQRCodeFragment()));
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
            runLotteryButton.setBackgroundResource(R.drawable.square_button_background_disabled);
            runLotteryButton.setOnClickListener(null);

        } else if (event.getWaitlistDeadline().compareTo(currentTimestamp) <= 0 && !event.isLotteryRan()) {
            // Enable the button if it's past the waitlist deadline and before the event date
            Log.d("LotteryDebug", "Lottery button enabled.");
            runLotteryButton.setEnabled(true);
            runLotteryButton.setBackgroundResource(R.drawable.square_button_background);
            runLotteryButton.setOnClickListener(v -> runLottery());
        } else {
            // Disable button and remove listener if conditions don't allow the lottery to be run
            Log.d("LotteryDebug", "Lottery button disabled.");
            runLotteryButton.setEnabled(false);
            runLotteryButton.setBackgroundResource(R.drawable.square_button_background_disabled);
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

                // Identify new invitees
                List<String> newInvitees = new ArrayList<>(invitedEntrants);
                List<String> lotteryLosers = new ArrayList<>(waitlistedEntrants);
                lotteryLosers.removeAll(newInvitees); // Remove those who were invited
                if (event.getInvitedEntrants() != null) {
                    newInvitees.removeAll(event.getInvitedEntrants());
                }

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
                        runLotteryButton.setBackgroundResource(R.drawable.square_button_background_disabled);
                        event.setLotteryRan(true); // Mark the event's lottery as run

                        // Send notifications to new invitees
                        for (String invitee : newInvitees) {
                            dbHelper.sendLotteryWinNotification(
                                    invitee,
                                    event.getEventId(),
                                    event.getOrganizerId(),
                                    event.getEventName(),
                                    new DatabaseHelper.NotificationCallback() {
                                        /**
                                         * Handles success on sending the notification to entrants.
                                         * @param responseMessage logged message.
                                         */
                                        @Override
                                        public void onSuccess(String responseMessage) {
                                            Log.d("NotificationDebug", "Notification sent successfully: " + responseMessage);
                                        }

                                        /**
                                         * Handles error on sending the notification upon running the lottery.
                                         * @param errorMessage logged message.
                                         */

                                        @Override
                                        public void onFailure(String errorMessage) {
                                            Log.e("NotificationDebug", "Failed to send notification: " + errorMessage);
                                        }
                                    }
                            );
                        }

                        // Send notifications to lottery losers
                        for (String loser : lotteryLosers) {
                            dbHelper.sendLotteryLossNotification(
                                    loser,
                                    event.getEventId(),
                                    event.getOrganizerId(),
                                    event.getEventName(),
                                    new DatabaseHelper.NotificationCallback() {
                                        /**
                                         * Handles success on sending loss notification.
                                         * @param responseMessage logged message.
                                         */
                                        @Override
                                        public void onSuccess(String responseMessage) {
                                            Log.d("NotificationDebug", "Loss notification sent successfully: " + responseMessage);
                                        }

                                        /**
                                         * Handles error on sending loss notification.
                                         * @param errorMessage logged message.
                                         */

                                        @Override
                                        public void onFailure(String errorMessage) {
                                            Log.e("NotificationDebug", "Failed to send loss notification: " + errorMessage);
                                        }
                                    }
                            );
                        }

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
            /**
             * Handles actions on logging the decline from the entrant.
             * @param declinedEntrants entrants that declined.
             */
            @Override
            public void onDeclineDetected(List<String> declinedEntrants) {
                if (isInitialDeclineSync) {
                    Log.d("DeclineDebug", "Initial sync detected. Skipping decline handling.");
                    isInitialDeclineSync = false;
                    lastKnownDeclinedEntrantsSize = declinedEntrants.size();
                    return;
                }

                Log.d("DeclineDebug", "Decline detected. Declined entrants: " + declinedEntrants);
                Log.d("DeclineDebug", "Previous size: " + lastKnownDeclinedEntrantsSize + ", Current size: " + declinedEntrants.size());

                if (declinedEntrants.size() > lastKnownDeclinedEntrantsSize) {
                    lastKnownDeclinedEntrantsSize = declinedEntrants.size();

                    Log.d("DeclineDebug", "Fetching event details for decline handling...");
                    fetchEventDetails(); // Proceed to handle the actual decline
                }
            }


            /**
             * Handles error on handling the decline.
             * @param e exception catcher.
             */

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
                    Log.d("DeclineDebug", "Event details fetched successfully: " + event.getEventName());
                    handleDeclineLogic(event); // Proceed with decline logic
                } else {
                    Log.d("DeclineDebug", "Failed to fetch event details. Event is null.");
                }
            }

            @Override
            public void onEventsFetched(List<Event> events) {
                // Not used
            }

            @Override
            public void onError(Exception e) {
                Log.e("DeclineDebug", "Error fetching event details: ", e);
            }
        });
    }


    /**
     * Handles the decline logic for entrants.
     * @param event to be declined.
     */

    private void handleDeclineLogic(Event event) {
        Log.d("DeclineDebug", "Handling decline for event: " + event.getEventName());

        if (isHandlingDecline) {
            Log.d("DeclineDebug", "Decline logic is already in process. Skipping.");
            return;
        }

        isHandlingDecline = true; // Lock the flag for processing

        try {
            if (event.getInvitedEntrants().size() >= event.getOccupantLimit()) {
                Log.d("DeclineDebug", "Occupant limit reached, no action needed.");
                return;
            }

            List<String> waitlistedEntrants = event.getWaitlistedEntrants();
            Log.d("DeclineDebug", "Waitlisted entrants: " + waitlistedEntrants);

            if (waitlistedEntrants == null || waitlistedEntrants.isEmpty()) {
                Log.d("DeclineDebug", "No waitlisted entrants to invite.");
                return;
            }

            String newInvitee = selectRandomEntrant(waitlistedEntrants);
            if (newInvitee != null) {
                Log.d("DeclineDebug", "New invitee selected: " + newInvitee);
                event.getInvitedEntrants().add(newInvitee);
                waitlistedEntrants.remove(newInvitee);

                dbHelper.updateEntrantsAfterDecline(eventId, event.getInvitedEntrants(), waitlistedEntrants, new DatabaseHelper.EventsCallback() {
                    @Override
                    public void onEventsFetched(List<Event> events) {
                        Log.d("DeclineDebug", "Entrants updated successfully after decline.");
                        dbHelper.sendLotteryWinNotification(
                                newInvitee, eventId, event.getOrganizerId(), event.getEventName(),
                                new DatabaseHelper.NotificationCallback() {
                                    @Override
                                    public void onSuccess(String responseMessage) {
                                        Log.d("NotificationDebug", "Notification sent successfully to: " + newInvitee);
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Log.e("NotificationDebug", "Failed to send notification: " + errorMessage);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onEventFetched(Event event) {
                        // No action needed
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("DeclineDebug", "Failed to update entrants after decline.", e);
                    }
                });
            } else {
                Log.d("DeclineDebug", "No valid invitee could be selected from the waitlist.");
            }
        } catch (Exception e) {
            Log.e("DeclineDebug", "Error while handling decline.", e);
        } finally {
            isHandlingDecline = false; // Reset the flag
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

    /**
     * Opens the map fragment for the event.
     */

    private void openMapFragment() {
        OrganizerMapFragment mapFragment = OrganizerMapFragment.newInstance(eventId);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment_container, mapFragment)
                .addToBackStack(null)
                .commit();
    }


}