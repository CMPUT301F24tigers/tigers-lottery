package com.example.tigers_lottery.JoinedEvents;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntrantDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntrantDashboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatabaseHelper dbHelper;

    public EntrantDashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EntrantDashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EntrantDashboardFragment newInstance(String param1, String param2) {
        EntrantDashboardFragment fragment = new EntrantDashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.entrant_dashboard_fragment, container, false);

        LinearLayout eventsListLinearLayout = view.findViewById(R.id.linear_layout_events_list);
        dbHelper = new DatabaseHelper(getContext());

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        List<Event> entrantsEvents = new ArrayList<>();

        dbHelper.entrantFetchEvents(new DatabaseHelper.EventsCallback() {
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onEventsFetched(List<Event> events) {
                entrantsEvents.clear();
                entrantsEvents.addAll(events);
                String deviceId = dbHelper.getCurrentUserId();

                for (Event event : entrantsEvents) {
                    View eventView = inflater.inflate(R.layout.entrant_dashboard_event_item, eventsListLinearLayout, false);

                    TextView eventNameTextView = eventView.findViewById(R.id.eventItemName);
                    TextView eventDateTextView = eventView.findViewById(R.id.eventItemTime);
                    TextView eventLocationTextView = eventView.findViewById(R.id.eventItemLocation);
                    TextView eventStatusTextView = eventView.findViewById(R.id.eventItemStatus);
                    ImageView eventPhoto = eventView.findViewById(R.id.eventItemPhoto);

                    eventNameTextView.setText(event.getEventName());
                    eventLocationTextView.setText(event.getLocation());
                    eventDateTextView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(event.getEventDate().toDate()));

                    if(event.getDeclinedEntrants().contains(deviceId)) {
                        eventStatusTextView.setText("Declined");
                    } else if(event.getInvitedEntrants().contains(deviceId)) {
                        eventStatusTextView.setText("Invited");
                    } else if(event.getWaitlistedEntrants().contains(deviceId)) {
                        eventStatusTextView.setText("Waitlisted");
                    } else if(event.getRegisteredEntrants().contains(deviceId)) {
                        eventStatusTextView.setText("Registered");
                    }
                    eventView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("eventId", event.getEventId());

                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            Fragment transitionedFragment = new EntrantEventDetailsFragment();
                            transitionedFragment.setArguments(bundle);

                            fragmentTransaction.replace(R.id.main_activity_fragment_container, transitionedFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    });

                    eventsListLinearLayout.addView(eventView);
                }
            }

            @Override
            public void onEventFetched(Event event) {

            }

            @Override
            public void onError(Exception e) {

            }
        });

        return view;
    }
}