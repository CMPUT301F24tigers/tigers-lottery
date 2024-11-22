package com.example.tigers_lottery.HostedEvents;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.firebase.firestore.GeoPoint;

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
        fetchEventGeolocation();
    }

    private void fetchEventGeolocation() {
        dbHelper.fetchEventById(eventId, new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventFetched(Event event) {
                if (event != null && event.getGeolocation() != null) {
                    GeoPoint geoPoint = event.getGeolocation();
                    LatLng eventLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 12)); // Zoom to event location
                } else {
                    // Use fallback location if geolocation is unavailable
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fallbackLocation, 12));
                    Toast.makeText(getContext(), "Event location not available. Using fallback location.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onEventsFetched(java.util.List<Event> events) {
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
