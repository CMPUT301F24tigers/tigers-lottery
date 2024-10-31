package com.example.tigers_lottery;

import android.util.Log;
import androidx.annotation.Nullable;
import com.example.tigers_lottery.models.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
     * Fetch all events from Firestore and pass them to the callback
     */
    public void fetchEvents(final EventsCallback callback) {
        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        String eventId = doc.getString("event_id");
                        String eventName = doc.getString("event_name");
                        boolean geolocation = doc.getBoolean("geolocation") != null && doc.getBoolean("geolocation");
                        String organizerId = doc.getString("organizer_id");
                        String posterUrl = doc.getString("poster_url");
                        Timestamp eventDate = doc.getTimestamp("event_date");
                        Timestamp waitlistOpenDate = doc.getTimestamp("waitlist-open-date");
                        Timestamp waitlistDeadline = doc.getTimestamp("waitlist-deadline");
                        int waitlistLimit = doc.getLong("waitlist_limit") != null ? doc.getLong("waitlist_limit").intValue() : 0;

                        Event event = new Event(eventId, eventName, geolocation, organizerId, posterUrl, eventDate,
                                waitlistOpenDate, waitlistDeadline, waitlistLimit);
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
