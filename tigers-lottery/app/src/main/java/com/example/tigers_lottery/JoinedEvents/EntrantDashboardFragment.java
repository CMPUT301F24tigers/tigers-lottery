package com.example.tigers_lottery.JoinedEvents;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.tigers_lottery.DatabaseHelper;
import android.Manifest; // Add this
import com.example.tigers_lottery.HostedEvents.OrganizerEventDetailsFragment;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.example.tigers_lottery.models.User;
import com.example.tigers_lottery.utils.DeviceIDHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Fragment for displaying the view when clicking joined events
 * Entrants will be able to see any events they are in a list for and interact with them.
 */

public class EntrantDashboardFragment extends Fragment {

    private DatabaseHelper dbHelper;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private ActivityResultLauncher<Intent> qrCodeLauncher;

    /**
     * Required empty constructor.
     */

    public EntrantDashboardFragment() {
    }

    /**
     * Initializes the QRCode launcher and saves the instance state for the user's actions.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        qrCodeLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                IntentResult intentResult = IntentIntegrator.parseActivityResult(
                        IntentIntegrator.REQUEST_CODE,
                        result.getResultCode(),
                        result.getData()
                );
                if (intentResult != null && intentResult.getContents() != null) {
                    String eventId = intentResult.getContents();
                    Toast.makeText(getContext(), "Scanned Event ID: " + eventId, Toast.LENGTH_SHORT).show();
                    Log.d("QRScan", "Navigating to event details with eventId: " + eventId);
                    navigateToEventDetails(eventId);
                } else {
                    Toast.makeText(getContext(), "Scan Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * Inflates the layout for the entrant dashboard screen, initializes the database helper,
     * floating action button to join an event, and loads the events joined by the entrant.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the view for the fragment's ui.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entrant_dashboard_fragment, container, false);

        LinearLayout eventsListLinearLayout = view.findViewById(R.id.linear_layout_events_list);
        dbHelper = new DatabaseHelper(getContext());
        FloatingActionButton joinEventButton = view.findViewById(R.id.join_event_button);

        List<Event> entrantsEvents = new ArrayList<>();

        joinEventButton.setOnClickListener(v-> {
                checkUserProfileComplete(dbHelper.getCurrentUserId(), new DatabaseHelper.isValidProfileCallback() {
                    /**
                     * On checking if the profile is complete, grants user permission
                     * @param isComplete true if the profile is complete false if not.
                     */
                    @Override
                    public void onProfileCheckComplete(boolean isComplete) {
                        if(isComplete){
                            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.CAMERA},
                                        CAMERA_PERMISSION_REQUEST_CODE);
                            } else {
                                // Permission already granted
                                initiateQRScanner();
                            }
                        } else {
                            Toast.makeText(getContext(), "Complete your profile with valid details to join the event", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        });


        dbHelper.entrantFetchEvents(new DatabaseHelper.EventsCallback() {
            /**
             * On finding the entrant's events; populates the required fields.
             * @param events all events in the database.
             */
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
                    String dateSplit = event.getFormattedEventDate().split(" - ")[0];
                    eventDateTextView.setText(dateSplit);
                    if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
                        Glide.with(requireContext())
                                .load(event.getPosterUrl())
                                .placeholder(R.drawable.event_poster_placeholder)
                                .into(eventPhoto);
                    }

                    GradientDrawable statusBackground = (GradientDrawable) eventStatusTextView.getBackground();

                    if (event.getDeclinedEntrants().contains(deviceId)) {
                        statusBackground.setColor(0xFF8B0000);
                        eventStatusTextView.setText("Status: Declined");
                    } else if (event.getInvitedEntrants().contains(deviceId)) {
                        statusBackground.setColor(0xFFB080B0);
                        eventStatusTextView.setText("Status: Invited");
                    } else if (event.getWaitlistedEntrants().contains(deviceId)) {
                        statusBackground.setColor(0xFF696969);
                        eventStatusTextView.setText("Status: Waitlisted");
                    } else if (event.getRegisteredEntrants().contains(deviceId)) {
                        statusBackground.setColor(0xFF008080);
                        eventStatusTextView.setText("Status: Registered");
                    }

                    eventView.setOnClickListener(view -> {
                        Bundle bundle = new Bundle();
                        bundle.putInt("eventId", event.getEventId());

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        Fragment transitionedFragment = new EntrantEventDetailsFragment();
                        transitionedFragment.setArguments(bundle);

                        fragmentTransaction.replace(R.id.main_activity_fragment_container, transitionedFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    });

                    eventsListLinearLayout.addView(eventView);
                }
            }

            /**
             * Required dbHelper method, unused.
             * @param event event.
             */

            @Override
            public void onEventFetched(Event event) {
                // Not used here
            }

            /**
             * Handles error on finding events.
             * @param e exception catcher.
             */

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error fetching events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    /**
     * validates the entrant's date of birth.
     * @param dob entrant's date of birth.
     * @return true if valid, false if not.
     */

    private static boolean isValidDateOfBirth(Timestamp dob) {
        if (dob == null) {
            return false;
        }

        Date dateOfBirth = dob.toDate();
        Calendar calendar = Calendar.getInstance();

        // DOB the user is at least 14 years old
        calendar.add(Calendar.YEAR, -14);
        Date minAdultDob = calendar.getTime();
        return dateOfBirth.before(minAdultDob);
    }

    /**
     * Validates the entrant's email address.
     * @param email email.
     * @return true if valid, false if not.
     */

    public static boolean isValidEmailAddress(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validates the entrant's name.
     * @param name name
     * @return true if valid, false if not.
     */

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && !"Unknown User".equals(name);
    }

    /**
     * Fetches a user document by userId and checks if their profile is complete.
     *
     * @param userId   The ID of the user to validate.
     */
    public void checkUserProfileComplete(String userId, DatabaseHelper.isValidProfileCallback callback) {
        dbHelper.fetchUserById(userId, new DatabaseHelper.UsersCallback() {
            /**
             * Required dbHelper method, unused.
             * @param users users.
             */
            @Override
            public void onUsersFetched(List<User> users) {
            }

            /**
             * Finds the specific user in the database and checks their details' validity.
             * @param user current user.
             */

            @Override
            public void onUserFetched(User user) {
                boolean isValid = isValidName(user.getFirstName())
                        && isValidName(user.getLastName())
                        && isValidEmailAddress(user.getEmailAddress())
                        && isValidDateOfBirth(user.getDateOfBirth());
                callback.onProfileCheckComplete(isValid);
            }

            /**
             * Handles error on finding user's details.
             * @param e exception catcher.
             */

            @Override
            public void onError(Exception e) {
                Log.d("EntrantDashboardFragment", "Failed to Fetch user to validate profile completion");
                Toast.makeText(getContext(), "Error Validating User Profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Initiates the QR code scanner for joining events.
     */
    private void initiateQRScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this); // Use forSupportFragment
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR Code to Join an Event");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);

        Intent intent = integrator.createScanIntent();
        qrCodeLauncher.launch(intent); // Launch the scanner
    }

    /**
     * Navigates to the event details of the scanned QRCode's event.
     * @param result eventId.
     */

    private void navigateToEventDetails(String result) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        dbHelper.isQrCodeValid(result, isValid -> {
            if (isValid) {
                // If the QR code is valid, navigate to the event details page
                int eventIdForQR = Integer.parseInt(result.substring(0, 5));
                Bundle bundleForQR = new Bundle();
                bundleForQR.putInt("eventId", eventIdForQR);

                Fragment currentFragment = fragmentManager.findFragmentById(R.id.main_activity_fragment_container);
                if (currentFragment instanceof EntrantEventDetailsFragment) {
                    return; // If already on the details page, do nothing
                }

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment fragment = new EntrantEventDetailsFragment();
                fragment.setArguments(bundleForQR);
                transaction.replace(R.id.main_activity_fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                // If the QR code is invalid, show a toast and reload the current fragment
                Toast.makeText(getActivity(), "Invalid QR Code to Join an Event", Toast.LENGTH_SHORT).show();

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.main_activity_fragment_container);

                // Reload the same fragment
                if (currentFragment instanceof EntrantDashboardFragment) {
                    transaction.detach(currentFragment);
                    transaction.attach(currentFragment);
                } else {
                    transaction.replace(R.id.main_activity_fragment_container, new EntrantDashboardFragment());
                }
                transaction.commit();
            }
        });
    }

}
