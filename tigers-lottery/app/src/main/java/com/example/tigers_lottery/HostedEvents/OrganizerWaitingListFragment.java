package com.example.tigers_lottery.HostedEvents;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.HostedEvents.Adapters.EntrantAdapter;
import com.example.tigers_lottery.R;

import java.util.List;

public class OrganizerWaitingListFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView noEntrantsMessage;
    private DatabaseHelper dbHelper;
    private int eventId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_waitinglist_entrants, container, false);
        recyclerView = view.findViewById(R.id.waitlistedEntrantsRecyclerView);
        noEntrantsMessage = view.findViewById(R.id.noEntrantsMessage);

        eventId = getArguments() != null ? getArguments().getInt("event_id") : -1;
        dbHelper = new DatabaseHelper(requireContext());

        fetchWaitlistedEntrants();

        return view;
    }

    private void fetchWaitlistedEntrants() {
        dbHelper.fetchWaitlistedEntrants(eventId, new DatabaseHelper.EntrantsCallback() {
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

            @Override
            public void onError(Exception e) {
                // Handle error (e.g., show Toast message)
            }
        });
    }

    private void setupRecyclerView(List<String> entrants) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new EntrantAdapter(entrants));
    }
}
