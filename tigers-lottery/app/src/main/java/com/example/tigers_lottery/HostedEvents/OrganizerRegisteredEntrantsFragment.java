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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Fragment for displaying entrants that have joined the waiting list for the specific event,
 * and have accepted the invitation to join the event.
 */
public class OrganizerRegisteredEntrantsFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView noEntrantsMessage;
    private DatabaseHelper dbHelper;
    private int eventId;

    /**
     * Inflates the layout for the list displaying the entrants that have
     * joined the registered list for a specific event. Initializes the databaseHelper.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the waitingList entrants list fragment view.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_registered_entrants, container, false);
        recyclerView = view.findViewById(R.id.registeredEntrantsRecyclerView);
        noEntrantsMessage = view.findViewById(R.id.noEntrantsMessage);
        FloatingActionButton fabSendNotifications = view.findViewById(R.id.fabSendNotifications); // Floating Action Button

        // Retrieve eventId from arguments
        eventId = getArguments() != null ? getArguments().getInt("event_id") : -1;
        dbHelper = new DatabaseHelper(requireContext());

        fetchRegisteredEntrants();

        // Open the Send Notification Dialog on FAB click
        fabSendNotifications.setOnClickListener(v -> {
            SendNotificationDialog notificationDialog = new SendNotificationDialog(requireContext(), eventId, "registered_entrants");
            notificationDialog.showDialog();
        });

        return view;
    }


    /**
     * Calls on the database to find the entrants in the registered for the specific event.
     * If the list empty, a message is displayed.
     */


    private void fetchRegisteredEntrants() {
        dbHelper.fetchRegisteredEntrants(eventId, new DatabaseHelper.EntrantsCallback() {
            @Override
            public void onEntrantsFetched(List<String> entrants) {
                if (entrants.isEmpty()) {
                    noEntrantsMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    noEntrantsMessage.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    setupRecyclerView(entrants);
                }
            }

            /**
             * Handles error when fetching the registered entrants.
             * @param e exception catcher.
             */
            @Override
            public void onError(Exception e) {
                // Handle error (e.g., show Toast message)
            }
        });
    }

    /**
     * Sets up the recyclerView for the registered entrants.
     * @param entrants list of registered entrants.
     */

    private void setupRecyclerView(List<String> entrants) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new EntrantAdapter(entrants));
    }
}
