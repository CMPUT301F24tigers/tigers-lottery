package com.example.tigers_lottery.JoinedEvents;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.JoinedEvents.EntrantDashboardFragment;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.GeoPoint;

import com.example.tigers_lottery.models.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import java.util.Locale;
import java.util.Objects;

/**
 *Fragment used by the entrant to view an event's details.
 */

public class EntrantEventDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatabaseHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;
    private String userLocation;

    /**
     * Required empty public constructor.
     */
    public EntrantEventDetailsFragment() {
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
     * Inflates the fragment's layout and initializes the UI components.
     * Retrieves event details from the database and updates the UI accordingly.
     * It also handles user-specific event status, handles depending on the waitlist the user is a
     * part of.
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entrant_event_details_fragment, container, false);
        //DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        Bundle args = getArguments();
        dbHelper = new DatabaseHelper(requireContext());
        String deviceId = dbHelper.getCurrentUserId();
        ImageButton backButton = view.findViewById(R.id.eventDetailsBackButton);
        Button eventDetailsButton = view.findViewById(R.id.eventDetailsButton);
        TextView eventTextViewName = view.findViewById(R.id.eventDetailsTextViewName);
        TextView eventTextViewDescription = view.findViewById(R.id.eventDetailsTextViewDetails);
        TextView eventTextViewWaitingList = view.findViewById(R.id.eventDetailsTextViewWaitingList);
        TextView eventTextViewLocation = view.findViewById(R.id.eventDetailsTextViewLocation);
        TextView eventTextViewDate = view.findViewById(R.id.eventDetailsTextViewDate);
        TextView eventTextViewRegistrationOpenDate = view.findViewById(R.id.eventDetailsTextViewRegistrationOpenDate);
        TextView eventTextViewRegistrationDeadline = view.findViewById(R.id.eventDetailsTextViewRegistrationDeadline);
        TextView eventTextViewStatus = view.findViewById(R.id.eventDetailsTextViewStatus);
        ImageView eventDetailsImageView = view.findViewById(R.id.eventDetailsImageView);
        TextView eventOrganizerName = view.findViewById(R.id.eventDetailsOrganizerName);
        TextView eventOrganizerEmail = view.findViewById(R.id.eventDetailsFacilityEmail);
        TextView eventOrganizerNumber = view.findViewById(R.id.eventDetailsFacilityNumber);
        eventDetailsButton.setVisibility(View.INVISIBLE);



        assert args != null;
        dbHelper.fetchEventById(args.getInt("eventId"), new DatabaseHelper.EventsCallback() {
            /**
             * Finds the event by its id and populates its fields accordingly.
             * @param event whose details are to be displayed.
             */
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onEventFetched(Event event) {

                eventTextViewName.setText(event.getEventName());
                eventTextViewDescription.setText("Description: " + event.getDescription());
                eventTextViewLocation.setText("Location: " + event.getLocation());
                eventTextViewDate.setText("Date: " + new SimpleDateFormat("yyyy-MM-dd").format(event.getEventDate().toDate()));
                eventTextViewRegistrationDeadline.setText("Registration Deadline: " + new SimpleDateFormat("yyyy-MM-dd").format(event.getWaitlistDeadline().toDate()));
                eventTextViewRegistrationOpenDate.setText("Registration Opens: " + new SimpleDateFormat("yyyy-MM-dd").format(event.getWaitlistOpenDate().toDate()));
                String organizerId = event.getOrganizerId();
                dbHelper.fetchUserById(organizerId, new DatabaseHelper.UsersCallback() {
                    /**
                     * Required dbHelper method, unused.
                     * @param users users.
                     */
                    @Override
                    public void onUsersFetched(List<User> users) {
                        // do nothing.
                    }

                    /**
                     * On finding the organizer using their id, populates the facility name, email,
                     * and phone number of the event.
                     *
                     * @param user organizer for the event.
                     */

                    @Override
                    public void onUserFetched(User user) {
                        eventOrganizerName.setText("Hosted by: "+ user.getFacilityName());
                        eventOrganizerEmail.setText("Facility Email: " + user.getFacilityEmail());
                        eventOrganizerNumber.setText("Phone number: "+ (Objects.equals(user.getFacilityPhone(), "") ? "N/A" : user.getFacilityPhone()));
                    }

                    /**
                     * Required dbHelper method, unused.
                     * @param e exception catcher.
                     */

                    @Override
                    public void onError(Exception e) {
                        // do nothing.
                    }
                });

                if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
                    Glide.with(getContext())
                            .load(event.getPosterUrl())
                            .placeholder(R.drawable.event_poster_placeholder)
                            .into(eventDetailsImageView);
                }

                eventDetailsButton.setText("Join Waitlist");

                if(event.getWaitlistLimit() > 0) {
                    eventTextViewWaitingList.setText("Waiting List: " + event.getWaitlistedEntrants().size() + "/" + event.getWaitlistLimit());
                }else {
                    eventTextViewWaitingList.setText("Waiting List: " + event.getWaitlistedEntrants().size() + " entrants");
                }

                GradientDrawable statusBackground = (GradientDrawable) eventTextViewStatus.getBackground();
                if(event.getRegisteredEntrants().contains(deviceId)) {
                    statusBackground.setColor(0xFF008080);
                    eventTextViewStatus.setText("Status: Registered");
                    eventDetailsButton.setVisibility(View.INVISIBLE);
                }else if(event.getInvitedEntrants().contains(deviceId)) {
                    statusBackground.setColor(0xFFB080B0);
                    eventTextViewStatus.setText("Status: Invited");
                    eventDetailsButton.setText("Accept/Decline Invitation");
                    eventDetailsButton.setVisibility(View.VISIBLE);
                }else if(event.getDeclinedEntrants().contains(deviceId)) {
                    statusBackground.setColor(0xFF8B0000);
                    eventTextViewStatus.setText("Status: Declined");
                    eventDetailsButton.setVisibility(View.INVISIBLE);
                }else if(event.getWaitlistedEntrants().contains(deviceId)) {
                    statusBackground.setColor(0xFF696969);
                    eventTextViewStatus.setText("Status: Waitlisted");
                    eventDetailsButton.setText("Leave Waiting List");
                    eventDetailsButton.setVisibility(View.VISIBLE);
                } else{eventDetailsButton.setVisibility(View.VISIBLE);}

                if(event.isGeolocationRequired()) {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }else {
                        getCurrentLocation();
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putInt("eventId", event.getEventId());

                eventDetailsButton.setOnClickListener(v->{
                        if (!event.getWaitlistedEntrants().contains(deviceId) && !event.getRegisteredEntrants().contains(deviceId) && !event.getInvitedEntrants().contains(deviceId) && !event.getDeclinedEntrants().contains(deviceId)) {
                            // User is NOT on the waitlist, Join Waitlist functionality
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                            if(!event.isGeolocationRequired()) {
                                builder.setTitle("Join Waitlist");
                                builder.setMessage("Are you sure you want to join the waitlist for this event?");
                            }else {
                                if(userLocation == null) {
                                    Toast.makeText(requireContext(), "Still getting location...", Toast.LENGTH_SHORT).show();
                                    getCurrentLocation();
                                    return;
                                }

                                builder.setTitle("Warning! Geolocation required!");
                                TextView messageTextView = new TextView(getContext());
                                messageTextView.setText("You are joining event from " + userLocation + ". Event is in " + event.getLocation() + ". Do you want to join the event?");
                                messageTextView.setPadding(50, 30, 50, 30);
                                messageTextView.setVerticalScrollBarEnabled(true);

                                // Wrap the TextView in a ScrollView
                                ScrollView scrollView = new ScrollView(getContext());
                                scrollView.addView(messageTextView);

                                // Set the ScrollView as the dialog content
                                builder.setView(scrollView);
                            }

                            builder.setMessage("Are you sure you want to join the waitlist for this event?");

                            // Confirm joining
                            builder.setPositiveButton("Join", (dialog, which) -> {
                                dbHelper.addEntrantWaitlist(event.getEventId(), deviceId, new DatabaseHelper.EventsCallback() {
                                    /**
                                     * Finds the event to be joined, and adds the user to its waiting list.
                                     * @param updatedEvent event to be joined.
                                     */
                                    @Override
                                    public void onEventFetched(Event updatedEvent) {
                                        // Successfully joined the waitlist, update UI
                                        eventTextViewStatus.setTextColor(0xFF0000FF);
                                        eventTextViewStatus.setText("Status: Waitlisted");
                                        eventDetailsButton.setText("Leave Waitlist"); // Change button text dynamically

                                        // Notify the user
                                        new AlertDialog.Builder(getContext())
                                                .setTitle("Success")
                                                .setMessage("You have successfully joined waitlist!")
                                                .setPositiveButton("OK", (dialog1, which1)->{

                                                })
                                                .create()
                                                .show();
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                        Fragment transitionedFragment = new EntrantDashboardFragment();

                                        fragmentTransaction.replace(R.id.main_activity_fragment_container, transitionedFragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    }

                                    /**
                                     * Required dbHelper method, unused.
                                     * @param events events.
                                     */

                                    @Override
                                    public void onEventsFetched(List<Event> events) {
                                        // Not used here
                                    }

                                    /**
                                     * Handles error on joining the waiting list for the event.
                                     * @param e exception catcher.
                                     */

                                    @Override
                                    public void onError(Exception e) {
                                        // Handle errors
                                        new AlertDialog.Builder(getContext())
                                                .setTitle("Error")
                                                .setMessage("Could not join waitlist: " + e.getMessage())
                                                .setPositiveButton("OK", null)
                                                .create()
                                                .show();
                                    }
                                });
                            });

                            // Cancel action
                            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                            builder.create().show();

                        } else if(event.getWaitlistedEntrants().contains(deviceId)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Confirmation");
                            builder.setMessage("Are you sure you want to leave waiting list?");

                            // Set the Proceed button
                            builder.setPositiveButton("Proceed", (dialog, which) -> {
                                dbHelper.entrantLeaveWaitingList(event.getEventId(), new DatabaseHelper.StatusCallback() {
                                    /**
                                     * On updating the user to leave the waiting list, pops back into the entrant dashboard.
                                     */
                                    @Override
                                    public void onStatusUpdated() {
                                        eventDetailsButton.setVisibility(View.INVISIBLE);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                        Fragment transitionedFragment = new EntrantDashboardFragment();

                                        fragmentTransaction.replace(R.id.main_activity_fragment_container, transitionedFragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    }

                                    /**
                                     * Handles error on leaving the waiting list.
                                     * @param e exception catcher.
                                     */

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
                                    /**
                                     * Sends the user back to the entrant dashboard upon accepting an invitation.
                                     */
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

                                    /**
                                     * Handles error on accepting an invitation.
                                     * @param e exception catcher.
                                     */

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                            });

                            // Set the Cancel button
                            builder.setNegativeButton("No", (dialog, which) -> {
                                dbHelper.entrantAcceptDeclineInvitation(event.getEventId(), "decline", new DatabaseHelper.StatusCallback() {
                                    /**
                                     * Sends user back to the entrant dashboard on declining the invitation.
                                     */
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

                                    /**
                                     * Handles error on declining the invitation.
                                     * @param e exception catcher.
                                     */

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                            });

                            // Show the AlertDialog
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                });

            }

            /**
             * Required dbHelper method, unused.
             * @param events
             */

            @Override
            public void onEventsFetched(List<Event> events) {
            }

            /**
             * Handles error on finding the event.
             * @param e exception catcher.
             */

            @Override
            public void onError(Exception e) {

            }
        });
        /**
         * Back button for the entrant event details screen.
         */

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


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // Check if permissions are granted
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Permissions are granted, proceed with fetching location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            // Get latitude and longitude
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Convert to GeoPoint
                            GeoPoint geoPoint = new GeoPoint(latitude, longitude);

                            // Update the geolocation in the database
                            dbHelper.updateUserGeolocation(geoPoint, new DatabaseHelper.StatusCallback() {
                                @Override
                                public void onStatusUpdated() {
                                    Log.d("GeolocationUpdate", "Geolocation updated successfully in Firestore.");
                                    Toast.makeText(requireContext(), "Geolocation updated successfully!", Toast.LENGTH_SHORT).show();

                                    // Optionally convert latitude and longitude to an address
                                    getAddressFromLatLng(latitude, longitude);
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e("GeolocationUpdateError", "Failed to update geolocation: " + e.getMessage());
                                    Toast.makeText(requireContext(), "Failed to update geolocation!", Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Convert to address
                            getAddressFromLatLng(latitude, longitude);
                        } else {
                            Log.e("LocationError", "Location is null");
                        }
                    });
        } else {
            // Permissions are not granted, request permissions
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }


    private void getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            // Get address list from Geocoder
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Construct the address string
                String fullAddress = address.getAddressLine(0); // Full address
                String city = address.getLocality();           // City
                String state = address.getAdminArea();         // State
                String country = address.getCountryName();     // Country
                String postalCode = address.getPostalCode();   // Postal code

                userLocation = city + ", " + state + ", " + country;
                // Log or use the address
                Log.d("Address", "User Full Address: " + fullAddress);
                Log.d("Address", "User City: " + city + ", State: " + state + ", Country: " + country + ", Postal Code: " + postalCode);
            } else {
                Log.e("GeocoderError", "No address found for the location");
            }
        } catch (IOException e) {
            Log.e("GeocoderException", "Error getting address from latitude/longitude", e);
        }
    }

}