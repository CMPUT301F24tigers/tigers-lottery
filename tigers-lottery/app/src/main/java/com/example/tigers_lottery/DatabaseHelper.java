package com.example.tigers_lottery;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tigers_lottery.models.*;
import com.example.tigers_lottery.utils.DeviceIDHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


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
    private String currentUserId;
    private StorageReference storageReference;

    /**
     * Constructor for DatabaseHelper.
     * Initializes Firestore instance and sets up references to "events" and "users" collections.
     */
    public DatabaseHelper(Context context) {
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events"); // Reference to "events" collection
        usersRef = db.collection("users");   // Reference to "users" collection
        storageReference = storageReference = FirebaseStorage.getInstance().getReference("profile_images");

        // Retrieve and store the Device ID as the currentUserId
        currentUserId = DeviceIDHelper.getDeviceId(context);

        // Check if the user already exists in Firestore
//        ensureUserExists();
    }

    // Callback interfaces for asynchronous Firestore operations
    public interface EventsCallback {
        void onEventsFetched(List<Event> events);
        void onEventFetched(Event event);
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

    public interface EventDetailsCallback {
        void onEventFetched(Event event);
        void onError(Exception e);
    }

    public interface ProfileCallback {
        void onProfileExists();
        void onProfileNotExists();
    }

    /**
     * Returns the current user's ID, which is the Device ID.
     */
    public String getCurrentUserId() {
        return currentUserId;
    }

//    /**
//     * Checks if the user exists in Firestore. If not, creates a new user document.
//     */
//    private void ensureUserExists() {
//        usersRef.document(currentUserId).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                if (!task.getResult().exists()) {
//                    // If user does not exist, create a new one
//                    User newUser = new User();
//                    newUser.setUserId(currentUserId);
//                    addUser(newUser);
//                } else {
//                    Log.d(TAG, "User already exists in Firestore: " + currentUserId);
//                }
//            } else {
//                Log.e(TAG, "Error checking user existence", task.getException());
//            }
//        });
//    }


    /**
     * Fetches events for the organizer dashboard, showing only the current user's hosted events
     *
     * @param callback The callback to handle the list of events or error.
     */
    public void organizerFetchEvents(final EventsCallback callback) {
        // Fetch events where organizer_id matches the current user's ID
        eventsRef.whereEqualTo("organizer_id", currentUserId)
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
                            Event event = querySnapshot.getDocuments().get(0).toObject(Event.class);

                            // Initialize entrant lists if they are null, this is to ensure fragments later do not encounter null pointer issues
                            if (event != null) {
                                event.setRegisteredEntrants(event.getRegisteredEntrants() != null ? event.getRegisteredEntrants() : new ArrayList<>());
                                event.setWaitlistedEntrants(event.getWaitlistedEntrants() != null ? event.getWaitlistedEntrants() : new ArrayList<>());
                                event.setInvitedEntrants(event.getInvitedEntrants() != null ? event.getInvitedEntrants() : new ArrayList<>());
                                event.setDeclinedEntrants(event.getDeclinedEntrants() != null ? event.getDeclinedEntrants() : new ArrayList<>());
                            }

                            callback.onEventFetched(event);
                        } else {
                            callback.onError(new Exception("Event not found"));
                        }
                    } else {
                        callback.onError(task.getException());
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
     * The @param line was throwing an error so I got rid of it for now --- FIX ALL THAT LATER
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
     * Deletes a particular event
     *
     */
    public void deleteEvent(int eventId, final EventsCallback callback) {
        eventsRef.whereEqualTo("event_id", eventId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    //  event IDs are unique, so we can delete the first document found
                    String documentId = task.getResult().getDocuments().get(0).getId();
                    eventsRef.document(documentId).delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Event deleted successfully");
                                callback.onEventsFetched(null); // Notify deletion success
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "Error deleting event", e);
                                callback.onError(e);
                            });
                } else {
                    callback.onError(new Exception("Event not found"));
                }
            })
            .addOnFailureListener(callback::onError);
    }


    /** Update an event
     *
     * @param event
     * @param callback
     */
    public void updateEvent(Event event, EventsCallback callback) {
        eventsRef.whereEqualTo("event_id", event.getEventId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String documentId = task.getResult().getDocuments().get(0).getId();
                        eventsRef.document(documentId).set(event)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Event updated successfully");
                                    callback.onEventsFetched(null); // Notify success
                                })
                                .addOnFailureListener(callback::onError);
                    } else {
                        callback.onError(new Exception("Event not found"));
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    /** Retrieves the registered entrants list for a particular event ID
     *
     * @param eventId
     * @param callback
     */
    public void fetchRegisteredEntrants(int eventId, final EntrantsCallback callback) {
        eventsRef.whereEqualTo("event_id", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Event event = task.getResult().getDocuments().get(0).toObject(Event.class);
                        callback.onEntrantsFetched(event != null ? event.getRegisteredEntrants() : new ArrayList<>());
                    } else {
                        callback.onEntrantsFetched(new ArrayList<>()); // Return empty list if no data
                    }
                })
                .addOnFailureListener(callback::onError);
    }


    /** Retrieves the waitlisted entrants list for a particular event ID
     *
     * @param eventId
     * @param callback
     */
    public void fetchWaitlistedEntrants(int eventId, final EntrantsCallback callback) {
        eventsRef.whereEqualTo("event_id", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Event event = task.getResult().getDocuments().get(0).toObject(Event.class);
                        callback.onEntrantsFetched(event != null ? event.getWaitlistedEntrants() : new ArrayList<>());
                    } else {
                        callback.onEntrantsFetched(new ArrayList<>()); // Return empty list if no data
                    }
                })
                .addOnFailureListener(callback::onError);
    }


    /** Retrieves the invited entrants list for a particular event id
     *
     * @param eventId
     * @param callback
     */
    public void fetchInvitedEntrants(int eventId, final EntrantsCallback callback) {
        eventsRef.whereEqualTo("event_id", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Event event = task.getResult().getDocuments().get(0).toObject(Event.class);
                        callback.onEntrantsFetched(event != null ? event.getInvitedEntrants() : new ArrayList<>());
                    } else {
                        callback.onEntrantsFetched(new ArrayList<>()); // Return empty list if no data
                    }
                })
                .addOnFailureListener(callback::onError);
    }


    /** Retrieves the declined entrants list for an event id
     *
     * @param eventId
     * @param callback
     */
    public void fetchDeclinedEntrants(int eventId, final EntrantsCallback callback) {
        eventsRef.whereEqualTo("event_id", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Event event = task.getResult().getDocuments().get(0).toObject(Event.class);
                        callback.onEntrantsFetched(event != null ? event.getDeclinedEntrants() : new ArrayList<>());
                    } else {
                        callback.onEntrantsFetched(new ArrayList<>()); // Return empty list if no data
                    }
                })
                .addOnFailureListener(callback::onError);
    }


    // Callback interface for fetching entrants
    public interface EntrantsCallback {
        void onEntrantsFetched(List<String> entrants);
        void onError(Exception e);
    }

    /** Updates invited entrants list after lottery is ran by organizer
     *
     *
     * @param eventId
     * @param invitedEntrants
     * @param callback
     */
    public void updateInvitedEntrantsAndSetLotteryRan(int eventId, List<String> invitedEntrants, final EventsCallback callback) {
        eventsRef.whereEqualTo("event_id", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String documentId = task.getResult().getDocuments().get(0).getId();
                        Event event = task.getResult().getDocuments().get(0).toObject(Event.class);

                        // Check if event is null for safety
                        if (event != null) {
                            List<String> waitlistedEntrants = event.getWaitlistedEntrants();

                            // Remove invitedEntrants from the waitlist
                            waitlistedEntrants.removeAll(invitedEntrants);

                            // Update both lists in Firestore
                            eventsRef.document(documentId)
                                    .update("invited_entrants", invitedEntrants,
                                            "waitlisted_entrants", waitlistedEntrants,
                                            "is_lottery_ran", true)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Lottery ran successfully. Invited entrants updated and removed from waitlist.");
                                        callback.onEventsFetched(null); // Success callback
                                    })
                                    .addOnFailureListener(callback::onError);
                        } else {
                            callback.onError(new Exception("Event not found"));
                        }
                    } else {
                        callback.onError(new Exception("Event not found"));
                    }
                })
                .addOnFailureListener(callback::onError);
    }


    // Helper function to select random entrants
    public List<String> selectRandomEntrants(List<String> entrants, int count) {
        List<String> selectedEntrants = new ArrayList<>();
        Random random = new Random();
        while (selectedEntrants.size() < count && !entrants.isEmpty()) {
            int index = random.nextInt(entrants.size());
            selectedEntrants.add(entrants.remove(index));
        }
        return selectedEntrants;
    }


    /** Listen for changes in the declined entrants list; If someone declines they're added here and we must repick randomly to fill their spot
     *
     * @param eventId
     * @param callback
     */
    public void addDeclineListener(int eventId, DeclineCallback callback) {
        eventsRef.whereEqualTo("event_id", eventId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        Event event = value.getDocuments().get(0).toObject(Event.class);
                        callback.onDeclineDetected(event.getDeclinedEntrants());
                    }
                });
    }

    // Callback interface for handling declines
    public interface DeclineCallback {
        void onDeclineDetected(List<String> declinedEntrants);
        void onError(Exception e);
    }


    /** Update both the waitlisted entrants list as well as the invited entrants list, after someone declines an invitation and someone else is picked to fill that spot
     *
     * @param eventId
     * @param invitedEntrants
     * @param waitlistedEntrants
     * @param callback
     */
    public void updateEntrantsAfterDecline(int eventId, List<String> invitedEntrants, List<String> waitlistedEntrants, final EventsCallback callback) {
        eventsRef.whereEqualTo("event_id", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String documentId = task.getResult().getDocuments().get(0).getId();
                        eventsRef.document(documentId)
                                .update("invited_entrants", invitedEntrants, "waitlisted_entrants", waitlistedEntrants)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Entrants updated successfully.");
                                    callback.onEventsFetched(null); // Notify success
                                })
                                .addOnFailureListener(callback::onError);
                    } else {
                        callback.onError(new Exception("Event not found"));
                    }
                })
                .addOnFailureListener(callback::onError);
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
     */
//     * @param user The User object to be added.
//     *              Ensures user ID is set before attempting to add to Firestore.
//     */
    public void addUser(HashMap<String, Object> userData, Uri imageUri) {
//        if (user.getUserId() == null || user.getUserId().isEmpty() || Objects.equals(user.getUserId(), "NoUserId")) {
//            Log.e(TAG, "User ID is required to add a user. Use the Device ID of the user added");
//            return;
//        }

//        usersRef.document(user.getUserId())
//                .set(user)
//                .addOnSuccessListener(aVoid -> Log.d(TAG, "User added with ID: " + user.getUserId()))
//                .addOnFailureListener(e -> Log.w(TAG, "Error adding user", e));
        User newUser = new User();
        newUser.setUserId(currentUserId);
        usersRef.document(currentUserId).set(newUser);

        usersRef.document(currentUserId).update(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.w(TAG, "Successfully added new user");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Adding new user failed");
                });

        if(imageUri != null) {
            uploadImageToFirebase(imageUri, "user_photo");
        }
    }


    /**
     * Chekc if the user exists in the database
     *
     * @param
     */
    public void checkUserExists(ProfileCallback callback) {
        usersRef.document(currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                callback.onProfileExists(); // Document exists
                            } else {
                                callback.onProfileNotExists(); // Document does not exist
                            }
                        } else {
                            Log.e(TAG, "Failed to fetch user data");
                        }
                    }
                });
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

    /**
     * Uploads the URI image data to firebase storage and update the link in users document
     * @param imageUri the URI data of the image
     * @param field the name of the field to update the firebase storage image link
     */
    private void uploadImageToFirebase(Uri imageUri, String field) {
        // Generate a unique name for the image using UUID
        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference fileRef = storageReference.child(fileName);

        // Upload the image
        fileRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL for the uploaded image
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                                usersRef.document(currentUserId).update(field, downloadUrl);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}