package com.example.tigers_lottery;

import android.util.Log;
import androidx.annotation.Nullable;
import com.example.tigers_lottery.models.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static final String TAG = "DatabaseHelper";
    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    public DatabaseHelper() {
        // Initialize Firebase Firestore instance and set the reference to the "events" collection
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events"); // Assuming your collection is named "events"
    }

    // Callback interface for fetching events
    public interface EventsCallback {
        void onEventsFetched(List<Event> events);
        void onError(Exception e);
    }

    /**
     * Fetch events for Organizer Dashboard, filtering by valid organizer_id.
     */
    public void organizerFetchEvents(final EventsCallback callback) {
        // Only fetch events where organizer_id is greater than 0
        // NOTE: we will change this once we have UserID or device Id stored
        eventsRef.whereGreaterThan("organizer_id", 0)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Error fetching events.", e);
                            callback.onError(e); // Pass error to callback
                            return;
                        }

                        List<Event> events = new ArrayList<>();
                        if (value != null) {


                            for (QueryDocumentSnapshot doc : value) {
                                // Use Firestore's automatic mapping to convert document to Event object
                                // If we update schema, change the relevant DTO class instead
                                Event event = doc.toObject(Event.class);
                                events.add(event);
                            }
                        }
                        callback.onEventsFetched(events); // Pass fetched events to callback
                    }
                });
    }



    /**
     * Add a new event to Firestore
     */
    public void addEvent(Event event) {
        eventsRef.add(event)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Event added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding event", e));
    }

    // Add other methods as needed (e.g., deleteEvent, updateEvent, etc.)
}
