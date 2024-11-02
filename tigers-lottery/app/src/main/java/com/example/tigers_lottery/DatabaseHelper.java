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
import java.util.Random;

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
     * Fetch a single event by its eventId.
     */
    public void fetchEventById(int eventId, final EventsCallback callback) {
        eventsRef.whereEqualTo("event_id", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Every event Id should be unique, so we can just take the first document found
                            Event event = querySnapshot.getDocuments().get(0).toObject(Event.class);
                            List<Event> events = new ArrayList<>();
                            events.add(event);
                            callback.onEventsFetched(events); // Pass the event as a single-item list
                        } else {
                            Log.w(TAG, "No event found with the specified eventId.");
                            callback.onError(new Exception("Event not found"));
                        }
                    } else {
                        Log.w(TAG, "Error fetching event by eventId", task.getException());
                        callback.onError(task.getException()); // Pass error to callback
                    }
                });
    }

    /**
     * Create new event
     */
    public void createEvent(Event event, EventsCallback callback) {
        String uniqueEventId = generateUniqueEventId();

        // Set the generated event_id in the Event DTO
        event.setEventId(Integer.parseInt(uniqueEventId));

        // Try adding the event with the event_id as the document ID
        eventsRef.document(uniqueEventId)
                .set(event)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Event created successfully with event_id: " + uniqueEventId);
                    // Notify success by passing the created event
                    callback.onEventsFetched(List.of(event));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating event", e);
                    // Retry if there's a duplicate ID collision (very unlikely)
                    if (e.getMessage().contains("already exists")) {
                        // Retry by regenerating event_id
                        createEvent(event, callback);
                    } else {
                        callback.onError(e);
                    }
                });
    }

    /**
     * Helper function to generate unique ID.
     */
    private String generateUniqueEventId() {
        long timestamp = System.currentTimeMillis();
        int randomSuffix = new Random().nextInt(1000); // Random number between 0-999
        return String.valueOf(timestamp) + String.valueOf(randomSuffix);
    }

    // Add other methods as needed (e.g., deleteEvent, updateEvent, etc.)
}
