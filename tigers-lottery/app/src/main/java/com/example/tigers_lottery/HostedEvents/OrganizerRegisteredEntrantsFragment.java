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

import com.example.tigers_lottery.HostedEvents.Adapters.EntrantAdapter;
import com.example.tigers_lottery.R;

import java.util.ArrayList;
import java.util.List;

public class OrganizerRegisteredEntrantsFragment extends Fragment {

    private RecyclerView registeredEntrantsRecyclerView;
    private EntrantAdapter entrantAdapter;
    private List<String> registeredEntrants;

    public OrganizerRegisteredEntrantsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_registered_entrants, container, false);

        // Retrieve the entrants list from arguments
        registeredEntrants = getArguments() != null ? getArguments().getStringArrayList("entrants_list") : new ArrayList<>();

        registeredEntrantsRecyclerView = view.findViewById(R.id.registeredEntrantsRecyclerView);
        registeredEntrantsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the adapter with the entrants list
        entrantAdapter = new EntrantAdapter(registeredEntrants);
        registeredEntrantsRecyclerView.setAdapter(entrantAdapter);

        return view;
    }
}
