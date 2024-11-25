package com.example.tigers_lottery.JoinedEvents;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class EntrantDashboardFragment extends Fragment {

    private DatabaseHelper dbHelper;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private ActivityResultLauncher<Intent> qrCodeLauncher;

    public EntrantDashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ActivityResultLauncher for QR code scanning
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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entrant_dashboard_fragment, container, false);

        LinearLayout eventsListLinearLayout = view.findViewById(R.id.linear_layout_events_list);
        dbHelper = new DatabaseHelper(getContext());
        Button joinEventButton = view.findViewById(R.id.join_event_button);

        List<Event> entrantsEvents = new ArrayList<>();

        // Set up the Join Event button to launch the camera for QR code scanning
        joinEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    // Permission already granted
                    initiateQRScanner();
                }
            }
        });

        dbHelper.entrantFetchEvents(new DatabaseHelper.EventsCallback() {
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

                    if (event.getDeclinedEntrants().contains(deviceId)) {
                        eventStatusTextView.setText("Declined");
                    } else if (event.getInvitedEntrants().contains(deviceId)) {
                        eventStatusTextView.setText("Invited");
                    } else if (event.getWaitlistedEntrants().contains(deviceId)) {
                        eventStatusTextView.setText("Waitlisted");
                    } else if (event.getRegisteredEntrants().contains(deviceId)) {
                        eventStatusTextView.setText("Registered");
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

            @Override
            public void onEventFetched(Event event) {
                // Not used here
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error fetching events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
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
     * Navigates to event details based on the scanned QR code result.
     */
    private void navigateToEventDetails(String result) {
        int eventId = Integer.parseInt(result);
        Bundle bundle = new Bundle();
        bundle.putInt("eventId", eventId);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.main_activity_fragment_container);

        Log.d("QRScan", "Current Fragment: " + currentFragment);
        if (currentFragment instanceof EntrantEventDetailsFragment) {
            Log.d("QRScan", "Already on Event Details Fragment.");
            return;
        }

        Log.d("QRScan", "Navigating to Event Details Fragment with eventId: " + eventId);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        EntrantEventDetailsFragment fragment = new EntrantEventDetailsFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.main_activity_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("EntrantDashboardFragment", "onPause called");
    }
}
