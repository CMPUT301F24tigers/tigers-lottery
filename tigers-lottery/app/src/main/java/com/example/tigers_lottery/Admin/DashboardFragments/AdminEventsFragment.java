package com.example.tigers_lottery.Admin.DashboardFragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tigers_lottery.Admin.AdminRecyclerViewAdapter;
import com.example.tigers_lottery.Admin.DashboardFragments.ListItems.AdminListItemModel;
import com.example.tigers_lottery.Admin.DashboardFragments.ListItems.OnActionListener;
import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.example.tigers_lottery.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying event information within the admin dashboard.
 * <p>
 * Provides navigation to return to the AdminDashboardFragment when the back button is clicked.
 */
public class AdminEventsFragment extends Fragment implements OnActionListener {

    private AdminRecyclerViewAdapter eventsAdapter;
    private final List<AdminListItemModel> itemList = new ArrayList<>();

    /**
     * Inflates the admin events layout and configures the back button for navigation.
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

        RecyclerView recyclerView = view.findViewById(R.id.adminRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchEvents();

        return view;
    }

    private void fetchEvents() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        final String[] organizerName = new String[1];
        dbHelper.fetchAllEvents(new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventsFetched(List<Event> events) {
                itemList.clear();
                for (Event event : events) {
                    /*dbHelper.fetchUserById(event.getOrganizerId(), new DatabaseHelper.UsersCallback() {
                        @Override
                        public void onUsersFetched(List<User> users) {
                            //Do nothing
                        }

                        @Override
                        public void onUserFetched(User user) {
                            organizerName[0] = "by " + user.getFirstName() + " " + user.getLastName();
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(getContext(), "Failed to retrieve organizer for Event", Toast.LENGTH_SHORT).show();
                            Log.e("DatabaseHelper", "Error retriveing organizer for event", e);
                        }
                    });*/
                    itemList.add(new AdminListItemModel(
                            String.valueOf(event.getEventId()),
                            event.getEventName(),
                            "bob",//organizerName[0],  // Or other event-specific properties
                            "View Event Details",
                            "Delete Event",
                            ""
                    ));
                }
                eventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onEventFetched(Event event) {
                // Not needed for this implementation
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to retrieve all events", Toast.LENGTH_SHORT).show();
                Log.e("DatabaseHelper", "Error retrieving events", e);
            }
        });


    }
    // Implement the action listener methods for handling button clicks
    @Override
    public void onOptionOneClick(String strEventId) {
        int eventId = Integer.parseInt(strEventId);
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        Toast.makeText(getContext(), "Viewing Event details for event " + eventId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOptionTwoClick(String strEventId) {
        int eventId = Integer.parseInt(strEventId);
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.deleteEvent(eventId, new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventsFetched(List<Event> events) {
                //do nothing
            }

            @Override
            public void onEventFetched(Event event) {
                //do nothing
            }

            @Override
            public void onError(Exception e) {
                //do nothing
            }
        });
        eventsAdapter.setExpandedPosition(-1);
        eventsAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Removing Event " + eventId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOptionThreeClick(String userId) {
        // Handle "Remove User" action
        // Example: Call a function to delete the user from the database
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.removeUser(userId);
        eventsAdapter.setExpandedPosition(-1);
        eventsAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Removing user " + userId, Toast.LENGTH_SHORT).show();
    }

    public static AdminEntrantsProfilesFragment newInstance() {
        return new AdminEntrantsProfilesFragment();
    }


}