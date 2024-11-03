package com.example.tigers_lottery.HostedEvents;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Toast;
import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class OrganizerDashboardFragment extends Fragment implements EventAdapter.OnEventOptionSelectedListener {

    private static final String TAG = "OrganizerDashboard";
    private RecyclerView eventsRecyclerView;
    private FloatingActionButton fabCreateEvent;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private DatabaseHelper dbHelper;

    public OrganizerDashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.organizer_dashboard_fragment, container, false);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(requireContext());

        // Initialize RecyclerView and FloatingActionButton
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        fabCreateEvent = view.findViewById(R.id.fabCreateEvent);

        // Set up RecyclerView
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize event list and adapter with this fragment as the listener
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(requireContext(), eventList, this); // Pass 'this' as the listener
        eventsRecyclerView.setAdapter(eventAdapter);

        // Load events from DatabaseHelper
        loadEvents();

        // Set onClickListener for fabCreateEvent to open OrganizerCreateEventFragment
        fabCreateEvent.setOnClickListener(v -> {
            // Navigate to OrganizerCreateEventFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView2, new OrganizerCreateEventFragment())
                    .addToBackStack(null) // Allows back navigation
                    .commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEvents(); // Reload events to fetch the latest data when fragment is resumed
    }

    private void loadEvents() {
        dbHelper.organizerFetchEvents(new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventsFetched(List<Event> events) {
                eventList.clear();
                eventList.addAll(events);
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error loading events", e);
            }
        });
    }

    @Override
    public void onEditSelected(Event event) {
        // Navigate to OrganizerEditEventFragment and pass the selected event's data
        OrganizerEditEventFragment editFragment = new OrganizerEditEventFragment();

        // Pass the event details to the edit fragment
        Bundle args = new Bundle();
        args.putSerializable("event", event); // Ensure Event implements Serializable or use Parcelable
        editFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView2, editFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDeleteSelected(Event event) {
        // Perform delete operation in DatabaseHelper and update UI
        dbHelper.deleteEvent(event.getEventId(), new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventsFetched(List<Event> events) {
                Toast.makeText(getContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
                loadEvents(); // Refresh the event list
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to delete event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
