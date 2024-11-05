package com.example.tigers_lottery.JoinedEvents;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;

import java.text.SimpleDateFormat;
import java.util.List;

public class EntrantEventDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatabaseHelper dbHelper;

    public EntrantEventDetailsFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entrant_event_details_fragment, container, false);
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        Bundle args = getArguments();
        String deviceId = dbHelper.getCurrentUserId();
        ImageButton backButton = view.findViewById(R.id.eventDetailsBackButton);
        Button eventDetailsButton = view.findViewById(R.id.eventDetailsButton);
        TextView eventTextViewName = view.findViewById(R.id.eventDetailsTextViewName);
        TextView eventTextViewDescription = view.findViewById(R.id.eventDetailsTextViewDetails);
        TextView eventTextViewWaitingList = view.findViewById(R.id.eventDetailsTextViewWaitingList);
        TextView eventTextViewLocation = view.findViewById(R.id.eventDetailsTextViewLocation);
        TextView eventTextViewDate = view.findViewById(R.id.eventDetailsTextViewDate);
        TextView eventTextViewRegistrationDeadline = view.findViewById(R.id.eventDetailsTextViewRegistrationDeadline);
        TextView eventTextViewStatus = view.findViewById(R.id.eventDetailsTextViewStatus);

        assert args != null;
        dbHelper.fetchEventById(args.getInt("eventId"), new DatabaseHelper.EventsCallback() {
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onEventFetched(Event event) {

                eventTextViewName.setText(event.getEventName());
                eventTextViewDescription.setText("Description: " + event.getDescription());
                eventTextViewLocation.setText("Location: " + event.getLocation());
                eventTextViewDate.setText("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(event.getEventDate().toDate()));
                eventTextViewRegistrationDeadline.setText("Registration Deadline: : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(event.getWaitlistDeadline().toDate()));

                if(event.getWaitlistLimit() > 0) {
                    eventTextViewWaitingList.setText("Waiting List: " + event.getWaitlistedEntrants().size() + "/" + event.getWaitlistLimit());
                }else {
                    eventTextViewWaitingList.setText("Waiting List: " + event.getWaitlistedEntrants().size() + " entrants");
                }

                if(event.getRegisteredEntrants().contains(deviceId)) {
                    eventTextViewStatus.setText("Status: Registered");
                    eventDetailsButton.setVisibility(View.INVISIBLE);
                }else if(event.getInvitedEntrants().contains(deviceId)) {
                    eventTextViewStatus.setText("Status: Invited");
                    eventDetailsButton.setText("Accept/Decline Invitation");
                }else if(event.getDeclinedEntrants().contains(deviceId)) {
                    eventTextViewStatus.setText("Status: Declined");
                    eventDetailsButton.setVisibility(View.INVISIBLE);
                }else if(event.getWaitlistedEntrants().contains(deviceId)) {
                    eventTextViewStatus.setText("Status: Waitlisted");
                    eventDetailsButton.setText("Leave Waiting List");
                }


                Bundle bundle = new Bundle();
                bundle.putInt("eventId", event.getEventId());

                eventDetailsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(event.getWaitlistedEntrants().contains(deviceId)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Confirmation");
                            builder.setMessage("Are you sure you want to leave waiting list?");

                            // Set the Proceed button
                            builder.setPositiveButton("Proceed", (dialog, which) -> {
                                dbHelper.entrantLeaveWaitingList(event.getEventId(), new DatabaseHelper.StatusCallback() {
                                    @Override
                                    public void onStatusUpdated() {
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                        Fragment transitionedFragment = new EntrantDashboardFragment();

                                        fragmentTransaction.replace(R.id.main_activity_fragment_container, transitionedFragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                            });

                            // Set the Cancel button
                            builder.setNegativeButton("Cancel", (dialog, which) -> {
                                // Dismiss the dialog when the user cancels
                                dialog.dismiss();
                            });

                            // Show the AlertDialog
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }else if(event.getInvitedEntrants().contains(deviceId)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Accept/Decline Invitation");
                            builder.setMessage("Do you want to accept the invitation");

                            // Set the Proceed button
                            builder.setPositiveButton("Yes", (dialog, which) -> {
                                dbHelper.entrantAcceptDeclineInvitation(event.getEventId(), "accept", new DatabaseHelper.StatusCallback() {
                                    @Override
                                    public void onStatusUpdated() {
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                        Fragment transitionedFragment = new EntrantEventDetailsFragment();
                                        transitionedFragment.setArguments(bundle);

                                        fragmentTransaction.replace(R.id.main_activity_fragment_container, transitionedFragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                            });

                            // Set the Cancel button
                            builder.setNegativeButton("No", (dialog, which) -> {
                                dbHelper.entrantAcceptDeclineInvitation(event.getEventId(), "decline", new DatabaseHelper.StatusCallback() {
                                    @Override
                                    public void onStatusUpdated() {
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                        Fragment transitionedFragment = new EntrantEventDetailsFragment();
                                        transitionedFragment.setArguments(bundle);

                                        fragmentTransaction.replace(R.id.main_activity_fragment_container, transitionedFragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                            });

                            // Show the AlertDialog
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });

            }

            @Override
            public void onEventsFetched(List<Event> events) {

            }

            @Override
            public void onError(Exception e) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Fragment transitionedFragment = new EntrantDashboardFragment();

                fragmentTransaction.replace(R.id.main_activity_fragment_container, transitionedFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}
