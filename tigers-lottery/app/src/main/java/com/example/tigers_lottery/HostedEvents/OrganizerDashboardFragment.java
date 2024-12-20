package com.example.tigers_lottery.HostedEvents;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.HostedEvents.Adapters.EventAdapter;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.example.tigers_lottery.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fragment for displaying the view when clicking on hosted events
 * Organizers will be able to see their events and interact with them.
 */

public class OrganizerDashboardFragment extends Fragment implements EventAdapter.OnEventOptionSelectedListener, EventAdapter.OnEventClickListener {

    private static final String TAG = "OrganizerDashboard";
    private RecyclerView eventsRecyclerView;
    private FloatingActionButton fabCreateEvent;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private DatabaseHelper dbHelper;


    /**
     * Empty constructor required.
     */
    public OrganizerDashboardFragment() {}

    /**
     * Inflates the layout for the organizer dashboard screen
     * initializes the database helper, floating action button to create a new event,
     * loads the events held by the organizer.
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
     * @return the view for the fragment's UI.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_dashboard_fragment, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        fabCreateEvent = view.findViewById(R.id.fabCreateEvent);

        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize event list and adapter with this fragment as the listener
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(requireContext(), eventList, this, this); // Register both option listener and click listener
        eventsRecyclerView.setAdapter(eventAdapter);

        loadEvents();

        fabCreateEvent.setOnClickListener(v -> {
           checkFacilityProfileComplete(dbHelper.getCurrentUserId(), new DatabaseHelper.isValidProfileCallback() {
               /**
                * Handles actions after checking if the entrant profile is complete.
                * @param isComplete checks if the profile is complete
                */
               @Override
               public void onProfileCheckComplete(boolean isComplete) {
                   if(isComplete){
                       requireActivity().getSupportFragmentManager().beginTransaction()
                               .replace(R.id.main_activity_fragment_container, new OrganizerCreateEventFragment())
                               .addToBackStack(null)
                               .commit();
                   } else {
                       Toast.makeText(getContext(), "Complete your facility profile with valid details to host an event", Toast.LENGTH_LONG).show();
                   }
               }
           });
        });

        return view;
    }

    /**
     * Called when the fragment is running, reloads the list of events.
     */

    @Override
    public void onResume() {
        super.onResume();
        loadEvents();
    }

    /**
     * Loads events from the database and updates the event list and adapter.
     * Fetches events using the database helper and refreshes the displayed list.
     * Logs an error if fetching fails.
     */


    private void loadEvents() {
        dbHelper.organizerFetchEvents(new DatabaseHelper.EventsCallback() {
            /**
             * Clears the current list displayed and reloads the list.
             * @param events organizer's list of events.
             */
            @Override
            public void onEventsFetched(List<Event> events) {
                Collections.sort(events, new Comparator<Event>() {
                    @Override
                    public int compare(Event event, Event t1) {
                        return event.getEventName().compareToIgnoreCase(t1.getEventName());
                    }
                });
                eventList.clear();
                eventList.addAll(events);
                eventAdapter.notifyDataSetChanged();
            }

            /**
             * Unused, but needed.
             * @param event single event item.
             */
            @Override
            public void onEventFetched(Event event) {
                // Not needed in this context but still must be overridden
            }

            /**
             * Handles error during event loading.
             * @param e exception catcher.
             */

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error loading events", e);
            }
        });
    }

    /**
     * Handles event editing on selection, opens the editEvent fragment
     * passes the event to it.
     *
     * @param event selected to be edited.
     */

    @Override
    public void onEditSelected(Event event) {
        OrganizerEditEventFragment editFragment = new OrganizerEditEventFragment();
        Bundle args = new Bundle();
        args.putInt("eventId", event.getEventId()); // Pass only the event ID; if we pass the whole object we get timestamp not parcelable problems
        editFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment_container, editFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Handles event deletion on selection, calls on the database to delete
     * the event internally.
     *
     * @param event to be deleted.
     */

    @Override
    public void onDeleteSelected(Event event) {
        dbHelper.deleteEvent(event.getEventId(), "organizer", new DatabaseHelper.EventsCallback() {
            /**
             * Clears the current list displayed and reloads the list.
             * @param events organizer's list of events.
             */
            @Override
            public void onEventsFetched(List<Event> events) {
                Toast.makeText(getContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
                loadEvents();
            }

            /**
             * Unused, but needed.
             * @param event single event item.
             */

            @Override
            public void onEventFetched(Event event) {
            }

            /**
             * Handles error during event deletion.
             * @param e exception catcher.
             */

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to delete event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles clicking on an event, opens the the organizerDetailsFragment
     * and displays details for the event.
     *
     * @param event clicked event.
     */

    @Override
    public void onEventClick(Event event) {
        OrganizerEventDetailsFragment detailsFragment = OrganizerEventDetailsFragment.newInstance(event.getEventId());

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment_container, detailsFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Fetches a user document by userId and checks if their facility profile is complete.
     *
     * @param userId   The ID of the user to validate.
     */
    public void checkFacilityProfileComplete(String userId, DatabaseHelper.isValidProfileCallback callback) {
        dbHelper.fetchUserById(userId, new DatabaseHelper.UsersCallback() {
            /**
             * Handles actions on finding users, required but unused.
             * @param users all users.
             */
            @Override
            public void onUsersFetched(List<User> users) {
            }

            /**
             * Handles actions on finding specific user.
             * @param user user whose id is to be validated.
             */

            @Override
            public void onUserFetched(User user) {
                boolean isValid = isValidName(user.getFacilityName())
                        && isValidName(user.getFacilityLocation())
                        && isValidEmailAddress(user.getFacilityEmail());
                callback.onProfileCheckComplete(isValid);
            }

            /**
             * Handles error on finding user.
             * @param e exception catcher.
             */

            @Override
            public void onError(Exception e) {
                Log.d("OrganizerDashboardFragment", "Failed to Fetch user to validate profile completion");
                Toast.makeText(getContext(), "Error Validating Facility Profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Validates the entered email address.
     *
     * @param email to be validated.
     * @return true if the email is valid, false if not.
     */

    public static boolean isValidEmailAddress(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validates the entered name.
     *
     * @param name to be validated.
     * @return true if the name is valid, false if not.
     */

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }
}
