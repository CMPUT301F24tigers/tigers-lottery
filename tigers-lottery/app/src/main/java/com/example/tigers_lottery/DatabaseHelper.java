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
import java.util.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Helper class for interacting with Firebase Firestore database.
 * Manages database operations for the "events" and "users" collections.
 * Provides methods to fetch and add events and users, along with utility methods to get user counts.
 */
public class DatabaseHelper {

    private static final String TAG = "DatabaseHelper";
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;

    /**
     * Constructor for DatabaseHelper.
     * Initializes Firestore instance and sets up references to "events" and "users" collections.
     */
    public DatabaseHelper() {
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events"); // Reference to "events" collection
        usersRef = db.collection("users");   // Reference to "users" collection
    }

    // Callback interfaces for asynchronous Firestore operations
    public interface EventsCallback {
        void onEventsFetched(List<Event> events);
        void onError(Exception e);
    }

    public interface UsersCallback {
        void onUsersFetched(List<User> users);
        void onError(Exception e);
    }

    public interface UserCountCallback {
        void onUserCountFetched(int count);
        void onError(Exception e);
    }

    /**
     * Fetches events for the organizer dashboard, filtering by valid organizer ID.
     *
     * @param callback The callback to handle the list of events or error.
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
                            callback.onError(e);
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
                        callback.onEventsFetched(events);
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
        int uniqueId = 10000 + new Random().nextInt(90000); // Generates a number between 10000 and 99999
        return String.valueOf(uniqueId);
    }
  
    
     /**
     * Fetch all events from the events collection without any conditions.
     *
     * @param event The Event object to be added.
     */
    public void fetchAllEvents(final EventsCallback callback) {
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
        }

  
    /**
     * Fetches all users from the "users" collection.
     *
     * @param callback The callback to handle the list of users or error.
     */
    public void fetchAllUsers(final UsersCallback callback) {
        usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Error fetching users.", e);
                    callback.onError(e);
                    return;
                }

                List<User> users = new ArrayList<>();
                if (value != null) {
                    // Parse documents into User objects and add to users list
                    for (QueryDocumentSnapshot doc : value) {
                        User user = doc.toObject(User.class);
                        users.add(user);
                    }
                }
                callback.onUsersFetched(users);
            }
        });
    }

    /**
     * Adds a new user to Firestore.
     *
     * @param user The User object to be added.
     *              Ensures user ID is set before attempting to add to Firestore.
     */
    public void addUser(User user) {
        if (user.getUserId() == null || user.getUserId().isEmpty() || Objects.equals(user.getUserId(), "NoUserId")) {
            Log.e(TAG, "User ID is required to add a user. Use the Device ID of the user added");
            return;
        }

        usersRef.document(user.getUserId())
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User added with ID: " + user.getUserId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding user", e));
    }

    /**
     * Fetches the number of users in the "users" collection.
     *
     * @param callback The callback to handle the user count or error.
     */
    public void getUserCount(final UserCountCallback callback) {
        usersRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        int userCount = (snapshot != null) ? snapshot.size() : 0;
                        callback.onUserCountFetched(userCount);
                    } else {
                        Log.e(TAG, "Error fetching user count", task.getException());
                        callback.onError(task.getException());
                    }
                });
    }

   
    // Add other methods as needed (e.g., deleteEvent, updateEvent, etc.)
}