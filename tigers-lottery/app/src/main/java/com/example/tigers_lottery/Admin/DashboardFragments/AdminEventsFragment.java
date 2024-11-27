package com.example.tigers_lottery.Admin.DashboardFragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tigers_lottery.Admin.AdminRecyclerViewAdapter;
import com.example.tigers_lottery.Admin.DashboardFragments.ListItems.AdminListItemModel;
import com.example.tigers_lottery.Admin.DashboardFragments.ListItems.OnActionListener;
import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.HostedEvents.OrganizerEventDetailsFragment;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.example.tigers_lottery.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying event information within the admin dashboard.
 * <p>
 * Provides options for viewing and managing events and dynamically retrieves
 * organizer names for each event.
 */
public class AdminEventsFragment extends Fragment implements OnActionListener {

    private AdminRecyclerViewAdapter eventsAdapter;
    private final List<AdminListItemModel> itemList = new ArrayList<>();
    private String globalOrganizerName = "";

    /**
     * Inflates the admin events layout, initializes the RecyclerView adapter, and fetches events.
     *
     * @param inflater           LayoutInflater for inflating the fragment layout.
     * @param container          The container that this fragment will be attached to.
     * @param savedInstanceState Saved state for restoring fragment state.
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_list_fragment, container, false);

        eventsAdapter = new AdminRecyclerViewAdapter(itemList, this);

        TextView title = view.findViewById(R.id.adminListTile);
        title.setText("All Events");

        RecyclerView recyclerView = view.findViewById(R.id.adminRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(eventsAdapter);

        fetchEvents(); // Fetch events and populate the list

        return view;
    }

    /**
     * Fetches all events from the database and dynamically retrieves organizer names for each event.
     * Updates the RecyclerView adapter after each item is added.
     */
    private void fetchEvents() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.fetchAllEvents(new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventsFetched(List<Event> events) {
                itemList.clear(); // Clear the current list to refresh with new data
                for (Event event : events) {
                    // Fetch each organizer’s name asynchronously based on the organizer ID
                    dbHelper.fetchUserById(event.getOrganizerId(), new DatabaseHelper.UsersCallback() {
                        @Override
                        public void onUserFetched(User user) {

                            String organizerName = (user != null)
                                    ? "by " + user.getFacilityName()
                                    : "Organizer Not Found";

                            globalOrganizerName = organizerName;

                            // Add the event item to the list with organizer name
                            itemList.add(new AdminListItemModel(
                                    String.valueOf(event.getEventId()),
                                    event.getEventName(),
                                    organizerName,
                                    event.getPosterUrl(),
                                    "View Event Details",
                                    "Delete Event",
                                    true
                            ));
                            eventsAdapter.notifyDataSetChanged();  // Notify adapter after each addition
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("DatabaseHelper", "Error retrieving organizer for event", e);
                            Toast.makeText(getContext(), "Failed to retrieve organizer", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onUsersFetched(List<User> users) {
                            // Not used for this implementation
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("DatabaseHelper", "Error fetching events", e);
                Toast.makeText(getContext(), "Failed to retrieve events", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEventFetched(Event event) {
                // Not used for this implementation
            }
        });
    }

    /**
     * Handles the click action for the first option in the expandable menu.
     *
     * @param strEventId The event ID in String format.
     */
    @Override
    public void onOptionOneClick(String strEventId) {
        AdminEventDetailsFragment fragment = AdminEventDetailsFragment.newInstance(strEventId, globalOrganizerName);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Handles the click action for the second option in the expandable menu.
     * Deletes the selected event from the database.
     *
     * @param strEventId The event ID in String format.
     */
    @Override
    public void onOptionTwoClick(String strEventId) {
        final Event[] deletedEvent = new Event[1];
        int eventId = Integer.parseInt(strEventId);
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.deleteEvent(eventId, new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventsFetched(List<Event> events) { /* Do nothing */ }
            @Override
            public void onEventFetched(Event event) { deletedEvent[0] = event; }
            @Override
            public void onError(Exception e) { /* Do nothing */ }
        });
        eventsAdapter.setExpandedPosition(-1); // Collapse any expanded menus
        eventsAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Removing Event: " + deletedEvent[0].getEventName(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Creates a new instance of the AdminEntrantsProfilesFragment.
     *
     * @return A new AdminEntrantsProfilesFragment instance.
     */
    public static AdminEntrantsProfilesFragment newInstance() {
        return new AdminEntrantsProfilesFragment();
    }
}