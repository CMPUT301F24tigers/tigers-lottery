package com.example.tigers_lottery.HostedEvents;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Toast;
import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.HostedEvents.Adapters.EventAdapter;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class OrganizerDashboardFragment extends Fragment implements EventAdapter.OnEventOptionSelectedListener, EventAdapter.OnEventClickListener {

    private static final String TAG = "OrganizerDashboard";
    private RecyclerView eventsRecyclerView;
    private FloatingActionButton fabCreateEvent;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private DatabaseHelper dbHelper;

    public OrganizerDashboardFragment() {}

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
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_fragment_container, new OrganizerCreateEventFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEvents();
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
            public void onEventFetched(Event event) {
                // Not needed in this context but still must be overridden
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error loading events", e);
            }
        });
    }

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

    @Override
    public void onDeleteSelected(Event event) {
        dbHelper.deleteEvent(event.getEventId(), new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventsFetched(List<Event> events) {
                Toast.makeText(getContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
                loadEvents();
            }

            @Override
            public void onEventFetched(Event event) {
                // Not needed in this context but must be overridden
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to delete event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEventClick(Event event) {
        OrganizerEventDetailsFragment detailsFragment = OrganizerEventDetailsFragment.newInstance(event.getEventId());

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment_container, detailsFragment)
                .addToBackStack(null)
                .commit();
    }
}
