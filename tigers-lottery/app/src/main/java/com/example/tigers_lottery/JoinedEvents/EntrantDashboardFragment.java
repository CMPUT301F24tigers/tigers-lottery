package com.example.tigers_lottery.JoinedEvents;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    /**
     * Called when the fragment is created. If being recreated saved state is restored
     * Retrieves arguments passed into the fragment
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Inflate the view and populate the layout with the events the entrant has joined.
     * dbHelper and button to join event also initialized.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the view.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.entrant_dashboard_fragment, container, false);

        LinearLayout eventsListLinearLayout = view.findViewById(R.id.linear_layout_events_list);
        dbHelper = new DatabaseHelper(getContext());
        Button joinEventButton = view.findViewById(R.id.join_event_button);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        List<Event> entrantsEvents = new ArrayList<>();

        joinEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entrantIdInput();
            }
        });


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

    /**
     * Handles the input for an event by the entrant. Invalid Ids do not have any effect.
     */
    private void entrantIdInput(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter an eventId");

        String deviceId = dbHelper.getCurrentUserId();
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String entered = input.getText().toString();
                if(!entered.isEmpty()){
                    try{
                        int eventId = Integer.parseInt(entered);
                        dbHelper.addEntrantWaitlist(eventId, deviceId, new DatabaseHelper.EventsCallback() {
                            /**
                             * Required, unused.
                             * @param events unused.
                             */
                            @Override
                            public void onEventsFetched(List<Event> events) {

                            }

                            /**
                             * Pops a message up if the entrant joined the waiting list successfully
                             * @param event to be joined
                             */
                            @Override
                            public void onEventFetched(Event event) {
                                Toast.makeText(getActivity(), "Worked!", Toast.LENGTH_SHORT).show();


                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                Fragment transitionedFragment = new EntrantDashboardFragment();

                                fragmentTransaction.replace(R.id.main_activity_fragment_container, transitionedFragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();

                            }

                            /**
                             * Error handling for an invalid event id.
                             * @param e exception catcher.
                             */

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(getActivity(), "Didn't Work!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (NumberFormatException e){
                }} else{

                    }
            }
        }); builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }
}