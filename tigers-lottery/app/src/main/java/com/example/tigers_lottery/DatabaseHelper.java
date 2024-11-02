package com.example.tigers_lottery;

import android.util.Log;
import androidx.annotation.Nullable;
import com.example.tigers_lottery.models.*;
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
    private CollectionReference usersRef ;

    public DatabaseHelper() {
        // Initialize Firebase Firestore instance and set the reference to the "events" collection
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events"); // Assuming your collection is named "events"
        usersRef = db.collection("users");
    }

    // Callback interface for fetching events
    public interface EventsCallback {
        void onEventsFetched(List<Event> events);
        void onError(Exception e);
    }

    public interface UsersCallback {
        void onUsersFetched(List<User> users);
        void onError(Exception e);
    }

    /**
     * Fetch events for Organizer Dashboard, filtering by valid organizer_id.
     */
    public void organizerFetchEvents(final EventsCallback callback) {
        // Only fetch events where organizer_id is greater than 0
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
                                int eventId = doc.getLong("event_id").intValue();
                                String eventName = doc.getString("event_name");
                                GeoPoint geolocation = doc.getGeoPoint("geolocation");
                                int organizerId = doc.getLong("organizer_id").intValue();
                                String posterUrl = doc.getString("poster_url");
                                Timestamp eventDate = doc.getTimestamp("event_date");
                                Timestamp waitlistOpenDate = doc.getTimestamp("waitlist_open_date");
                                Timestamp waitlistDeadline = doc.getTimestamp("waitlist_deadline");
                                int waitlistLimit = doc.getLong("waitlist_limit").intValue();

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

    public void fetchAllUsers(final UsersCallback callback) {
        CollectionReference usersRef = db.collection("users"); // Reference to the users collection
        usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Error fetching users.", e);
                    callback.onError(e); // Pass error to callback
                    return;
                }

                List<User> users = new ArrayList<>();
                if (value != null) {
                    for (QueryDocumentSnapshot doc : value) {
                        User user = doc.toObject(User.class); // Automatic mapping to User object
                        users.add(user);
                    }
                }
                callback.onUsersFetched(users); // Pass fetched users to callback
            }
        });
    }

    /**
     * Add a new user to Firestore
     * @param user The user object to add to the database
     */
    public void addUser(User user) {
        // Check if user ID is set
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            Log.e(TAG, "User ID is required to add a user.");
            return;
        }

        // Add the user with the userId as the document ID
        usersRef.document(user.getUserId())
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User added with ID: " + user.getUserId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding user", e));
    }

    /*public void fetchAllEvents(final EventsCallback callback) {
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
                        Event event = doc.toObject(Event.class); // Automatic mapping to Event object
                        events.add(event);
                    }
                }
                callback.onEventsFetched(events); // Pass fetched events to callback
            }
        });
    }*/

    // Add other methods as needed (e.g., deleteEvent, updateEvent, etc.)
}
