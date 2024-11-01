package com.example.tigers_lottery.HostedEvents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tigers_lottery.R;

public class OrganizerEventDetailsFragment extends Fragment {
    private final String[] choices = {"Chosen Entrants", "Canceled Entrants", "Entrants in Waiting List",
    "Final List of Entrants", "View QR Code", "Create and Send Message"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.organizer_eventdetails_fragment, container, false);
        ListView listView = view.findViewById(R.id.eventDetailsListView);
        Button editButton = view.findViewById(R.id.eventDetailsEditEvent);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,choices);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((AdapterView<?> parent,View v, int position,long id)->{
            Fragment selectedFragment;
            switch(position){
                case 0:
                    selectedFragment = new OrganizerChosenEntrantsFragment();
                    break;
                case 1:
                    selectedFragment = new OrganizerCanceledEntrantsFragment();
                    break;
                case 2:
                    selectedFragment = new OrganizerWaitingListFragment();
                    break;
                case 3:
                    selectedFragment = new OrganizerFinalListFragment();
                    break;
                case 4:
                    selectedFragment = new OrganizerQRCodeFragment();
                    break;
                case 5:
                    selectedFragment = new OrganizerSendMessageFragment();
                    break;
            }
        } );

        editButton.setOnClickListener(v ->{
            Fragment editFragment = new OrganizerEditEventFragment();
        });
        return view;
    }
}
