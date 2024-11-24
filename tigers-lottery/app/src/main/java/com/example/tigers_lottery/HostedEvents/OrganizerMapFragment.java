package com.example.tigers_lottery.HostedEvents;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.example.tigers_lottery.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrganizerMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_EVENT_ID = "event_id";
    private int eventId;

    private MapView mapView;
    private GoogleMap googleMap;
    private DatabaseHelper dbHelper; // Database helper for fetching event data

    private LatLng fallbackLocation = new LatLng(40.7128, -74.0060); // Default: New York City

    public static OrganizerMapFragment newInstance(int eventId) {
        OrganizerMapFragment fragment = new OrganizerMapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getInt(ARG_EVENT_ID);
        }
        dbHelper = new DatabaseHelper(requireContext()); // Initialize the database helper
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_map_fragment, container, false);

        // Initialize MapView
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this); // Asynchronously initialize the map

        // Setup legend button
        Button btnLegend = view.findViewById(R.id.btnLegend);
        btnLegend.setOnClickListener(v -> showLegend());

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;

        // Enable basic map interactions
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);

        // Fetch and set the starting location
        fetchEventGeolocationAndMarkers();
    }

    private void fetchEventGeolocationAndMarkers() {
        dbHelper.fetchEventById(eventId, new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventFetched(Event event) {
                if (event != null && event.getGeolocation() != null) {
                    GeoPoint geoPoint = event.getGeolocation();
                    LatLng eventLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

                    // Move camera to event location
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 12));

                    // Fetch entrant geolocations and add markers
                    fetchAndAddEntrantMarkers(event, eventLocation);
                } else {
                    // Use fallback location if geolocation is unavailable
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fallbackLocation, 12));
                    Toast.makeText(getContext(), "Event location not available. Using fallback location.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onEventsFetched(List<Event> events) {
                // Not used
            }

            @Override
            public void onError(Exception e) {
                // On error, use the fallback location
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fallbackLocation, 12));
                Toast.makeText(getContext(), "Failed to load event location. Using fallback location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAndAddEntrantMarkers(Event event, LatLng eventLocation) {
        // Collect entrant IDs from all categories
        Set<String> allEntrantIds = new HashSet<>();
        allEntrantIds.addAll(event.getRegisteredEntrants());
        allEntrantIds.addAll(event.getWaitlistedEntrants());
        allEntrantIds.addAll(event.getInvitedEntrants());
        allEntrantIds.addAll(event.getDeclinedEntrants());

        dbHelper.fetchUsersByIds(new ArrayList<>(allEntrantIds), new DatabaseHelper.UsersCallback() {
            @Override
            public void onUsersFetched(List<User> users) {
                // Add markers for each user based on their category
                for (User user : users) {
                    if (user.getUserGeolocation() != null) {
                        LatLng userLocation = new LatLng(
                                user.getUserGeolocation().getLatitude(),
                                user.getUserGeolocation().getLongitude()
                        );

                        if (event.getRegisteredEntrants().contains(user.getUserId())) {
                            addMarker(userLocation, "Registered Entrant", BitmapDescriptorFactory.HUE_GREEN);
                        } else if (event.getWaitlistedEntrants().contains(user.getUserId())) {
                            addMarker(userLocation, "Waitlisted Entrant", BitmapDescriptorFactory.HUE_YELLOW);
                        } else if (event.getInvitedEntrants().contains(user.getUserId())) {
                            addMarker(userLocation, "Invited Entrant", BitmapDescriptorFactory.HUE_ORANGE);
                        } else if (event.getDeclinedEntrants().contains(user.getUserId())) {
                            addMarker(userLocation, "Declined Entrant", BitmapDescriptorFactory.HUE_RED);
                        }
                    } else {
                        Log.w("OrganizerMapFragment", "Missing geolocation for user: " + user.getUserId());
                    }
                }
            }

            @Override
            public void onUserFetched(User user) {
                // No implementation needed for this context
            }

            @Override
            public void onError(Exception e) {
                Log.e("OrganizerMapFragment", "Error fetching user data", e);
            }
        });
    }

    private void addMarker(LatLng location, String title, float hue) {
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(hue))
            );
        }
    }

    private void showLegend() {
        String legendText = "Marker Colors and Entrant Categories:\n\n" +
                "• Green - Registered Entrants\n" +
                "• Yellow - Waitlisted Entrants\n" +
                "• Orange - Invited Entrants\n" +
                "• Red - Declined Entrants";

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Legend")
                .setMessage(legendText)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
