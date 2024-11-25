package com.example.tigers_lottery.Admin.DashboardFragments;

import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.example.tigers_lottery.models.User;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment for displaying event details for admins.
 * This fragment allows an admin to view detailed information about an event,
 * including its title, description, location, dates, and poster.
 */
public class AdminEventDetailsFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private String eventId;
    private DatabaseHelper dbHelper;
    private Event event;

    // UI Components
    private TextView eventTitle;
    private TextView eventDescription;
    private TextView eventLocation;
    private TextView eventDate;
    private TextView waitlistOpenDate;
    private TextView waitlistCloseDate;
    private TextView waitlistLimit;
    private TextView entrantLimit;
    private TextView organizerNameTv;
    private ImageView eventPoster;
    private Button removeEventPoster;

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @param eventId The ID of the event whose details are to be displayed.
     * @return A new instance of AdminEventDetailsFragment.
     */
    public static AdminEventDetailsFragment newInstance(String eventId, String organizerName) {
        AdminEventDetailsFragment fragment = new AdminEventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is being created.
     * Retrieves the event ID from the arguments and initializes the DatabaseHelper.
     *
     * @param savedInstanceState Saved state of the fragment, if available.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
            if (eventId == null || eventId.isEmpty()) {
                throw new IllegalArgumentException("Event ID must not be null or empty.");
            }
        } else {
            throw new IllegalStateException("Arguments must contain an event ID.");
        }
        dbHelper = new DatabaseHelper(requireContext());
    }

    /**
     * Inflates the layout for this fragment and initializes UI components.
     *
     * @param inflater           LayoutInflater to inflate the layout.
     * @param container          Parent container for the fragment.
     * @param savedInstanceState Saved state of the fragment, if available.
     * @return The root view of the fragment's layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_eventdetails_fragment, container, false);

        // Initialize UI components
        eventTitle = view.findViewById(R.id.eventTitle);
        eventDescription = view.findViewById(R.id.eventDescription);
        eventLocation = view.findViewById(R.id.eventLocation);
        eventDate = view.findViewById(R.id.eventDate);
        waitlistOpenDate = view.findViewById(R.id.waitlistOpenDate);
        waitlistCloseDate = view.findViewById(R.id.waitlistCloseDate);
        eventPoster = view.findViewById(R.id.eventPoster);
        waitlistLimit = view.findViewById(R.id.waitlistLimit);
        entrantLimit = view.findViewById(R.id.entrantLimit);
        organizerNameTv = view.findViewById(R.id.organizerName);

        removeEventPoster = view.findViewById(R.id.runLotteryButton);
        Button viewRegistered = view.findViewById(R.id.viewRegisteredEntrants);
        Button viewDeclined = view.findViewById(R.id.viewDeclinedEntrants);
        Button viewWaitlisted = view.findViewById(R.id.viewWaitlistedEntrants);
        Button viewInvited = view.findViewById(R.id.viewInvitedEntrants);
        Button clearLists = view.findViewById(R.id.clearListsButton);

        clearLists.setVisibility(View.GONE);
        viewInvited.setVisibility(View.GONE);
        viewDeclined.setVisibility(View.GONE);
        viewRegistered.setVisibility(View.GONE);
        viewWaitlisted.setVisibility(View.GONE);

        removeEventPoster.setText("Remove Event Poster");
        removeEventPoster.setVisibility(View.GONE);

        // Load event details
        loadEventDetails();

        // Setup button listeners
        setupButtonListeners();

        return view;
    }

    /**
     * Fetches event details from the database using the event ID
     * and updates the UI to display the details.
     */
    private void loadEventDetails() {
        try {
            int parsedEventId = Integer.parseInt(eventId); // Parse the event ID to an integer
            dbHelper.fetchEventById(parsedEventId, new DatabaseHelper.EventsCallback() {
                @Override
                public void onEventFetched(Event fetchedEvent) {
                    if (fetchedEvent != null) {
                        event = fetchedEvent;
                        displayEventDetails(event);
                    } else {
                        Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                }

                @Override
                public void onEventsFetched(List<Event> events) {
                    // Not used
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(getContext(), "Error fetching event details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid Event ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    /**
     * Displays event details in the UI components.
     * If a poster exists, it is loaded into the ImageView.
     *
     * @param event The event object containing the details to display.
     */
    private void displayEventDetails(Event event) {
        dbHelper.getUser(event.getOrganizerId(), new DatabaseHelper.UserCallback() {
            @Override
            public void onUserFetched(User user) {
                organizerNameTv.setText("Organizer Name: " + user.getFirstName() + " " + user.getLastName());
            }

            @Override
            public void onError(Exception e) {
                //Do nothing
            }
        });

        eventTitle.setText(event.getEventName());
        eventDescription.setText(event.getDescription());
        eventLocation.setText("Location: " + event.getLocation());
        eventDate.setText("Event Date: " + formatDate(event.getEventDate()));
        waitlistOpenDate.setText("Waitlist Open: " + formatDate(event.getWaitlistOpenDate()));
        waitlistCloseDate.setText("Waitlist Close: " + formatDate(event.getWaitlistDeadline()));
        waitlistLimit.setText("Waitlist Limit: " + (event.isWaitlistLimitFlag() ? event.getWaitlistLimit() : "N/A"));
        entrantLimit.setText("Entrant Limit: " + event.getOccupantLimit());

        // Display the placeholder image initially
        eventPoster.setImageResource(R.drawable.placeholder_image_background);

        // Fetch and display the event poster
        String posterUrl = event.getPosterUrl();
        if (posterUrl != null && !posterUrl.isEmpty() && !posterUrl.equals("https://example.com/default-poster.png")) {
            Glide.with(requireContext())
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder_image_background)
                    .into(eventPoster);

            removeEventPoster.setVisibility(View.VISIBLE);
            eventPoster.setOnClickListener(v -> showImagePreviewDialog(posterUrl));
        }
    }

    /**
     * Sets up button listeners for admin actions.
     */
    private void setupButtonListeners() {
        //TODO: Dispatch notification for the organizer
        removeEventPoster.setOnClickListener(v -> {
            dbHelper.removeImage("event", eventId, new DatabaseHelper.Callback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(getContext(), event.getEventName() + "'s event poster has been removed", Toast.LENGTH_SHORT).show();
                    removeEventPoster.setVisibility(View.GONE);
                    eventPoster.setOnClickListener(null);
                    loadEventDetails();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Failed to remove event poster: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Helper method to format a Timestamp to a readable date string.
     *
     * @param timestamp The Timestamp to format.
     * @return A formatted date string.
     */
    private String formatDate(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }

    /**
     * Shows a dialog to preview the user's profile image in fullscreen.
     *
     * @param imageUrl The URL of the profile image to preview.
     */
    private void showImagePreviewDialog(String imageUrl) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.image_preview, null);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        ImageView imagePreview = dialogView.findViewById(R.id.imagePreview);
        Glide.with(requireContext()).load(imageUrl).into(imagePreview);

        ImageView closePreview = dialogView.findViewById(R.id.closePreview);
        closePreview.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}