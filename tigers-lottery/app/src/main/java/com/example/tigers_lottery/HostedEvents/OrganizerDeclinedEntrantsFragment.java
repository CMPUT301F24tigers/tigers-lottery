package com.example.tigers_lottery.HostedEvents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.HostedEvents.Adapters.EntrantAdapter;
import com.example.tigers_lottery.Notifications.SendNotificationDialog;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Fragment for displaying entrants that have either been cancelled from the
 * waiting list after failing to register by the organizer, or that have willingly declined after getting accepted
 * for the event.
 */

public class OrganizerDeclinedEntrantsFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView noEntrantsMessage;
    private DatabaseHelper dbHelper;
    private int eventId;

    /**
     * Inflates the layout for the list displaying the entrants that have been
     * cancelled for a specific event. Initializes the database Helper.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the cancelled entrants list fragment view.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_declined_entrants, container, false);
        recyclerView = view.findViewById(R.id.declinedEntrantsRecyclerView);
        noEntrantsMessage = view.findViewById(R.id.noEntrantsMessage);
        FloatingActionButton fabSendNotifications = view.findViewById(R.id.fabSendNotifications);

        eventId = getArguments() != null ? getArguments().getInt("event_id") : -1;
        dbHelper = new DatabaseHelper(requireContext());

        fetchDeclinedEntrants();

        // Set up FAB to open the notification dialog
        fabSendNotifications.setOnClickListener(v -> {
            SendNotificationDialog notificationDialog = new SendNotificationDialog(requireContext(), eventId, "declined_entrants");
            notificationDialog.showDialog();
        });

        return view;
    }

    /**
     * Calls on database to find the declined entrants for the specific event
     * If the list is empty, a message is displayed.
     */

    private void fetchDeclinedEntrants() {
        dbHelper.fetchDeclinedEntrants(eventId, new DatabaseHelper.EntrantsCallback() {
            /**
             * Handles actions on finding the entrants corresponding to the list.
             * @param entrantIds ids of the entrants.
             */
            @Override
            public void onEntrantsFetched(List<String> entrantIds) {
                if (entrantIds.isEmpty()) {
                    noEntrantsMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    // Fetch user details for all entrant IDs
                    dbHelper.fetchUsersByIds(entrantIds, new DatabaseHelper.UsersCallback() {
                        /**
                         * Handles actions on finding details of all entrants.
                         * @param users in the list
                         */
                        @Override
                        public void onUsersFetched(List<User> users) {
                            noEntrantsMessage.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            setupRecyclerView(users);
                        }
                        /**
                         * Handles actions on finding user, required dbHelper method, unused.
                         * @param user user.
                         */
                        @Override
                        public void onUserFetched(User user) {
                        }

                        /**
                         * Handles error on list finding.
                         * @param e exception catcher.
                         */

                        @Override
                        public void onError(Exception e) {
                            noEntrantsMessage.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            // Handle error (e.g., show Toast message)
                        }
                    });
                }
            }

            /**
             * Handles error on finding the entrants corresponding to the list.
             * @param e exception catcher.
             */
            @Override
            public void onError(Exception e) {
                noEntrantsMessage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Sets up the recyclerView for the waiting list entrants.
     * @param users list of waiting list entrants.
     */
    private void setupRecyclerView(List<User> users) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new EntrantAdapter(users));
    }
}
