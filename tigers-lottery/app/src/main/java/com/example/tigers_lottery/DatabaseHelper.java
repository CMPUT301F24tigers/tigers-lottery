package com.example.tigers_lottery;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tigers_lottery.models.Event;
import com.example.tigers_lottery.models.User;
import com.example.tigers_lottery.models.Notification;
import com.example.tigers_lottery.utils.DeviceIDHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.admin.v1.Index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;


/**
 * Helper class for interacting with Firebase Firestore database.
 * Manages database operations for the "events" and "users" collections.
 * Provides methods to fetch and add events and users, along with utility methods to get user counts.
 *
 * ===========================
 * DatabaseHelper Method Index
 * ===========================
 *
 * User Management:
 * - getCurrentUserId(): Retrieves the ID of the current user based on their device ID.
 * - isAdminUser(String userId, VerificationCallback callback): Checks if a user is an admin.
 * - addUserToAdmins(String deviceId, String authCode, Callback callback): Adds a user to the 'admins' collection.
 * - removeUser(String userId): Removes a user and cleans up associated events and records.
 * - fetchAllUsers(UsersCallback callback): Retrieves all users from the database.
 * - fetchUserById(String userId, UsersCallback callback): Retrieves user details by their user ID.
 * - checkUserExists(ProfileCallback callback): Checks if a user exists in the database.
 * - getUserCount(CountCallback callback): Fetches the number of users in the database.
 * - addUser(HashMap<String, Object> userData, Uri imageUri): Adds a new user to the database.

 * Event Management:
 * - organizerFetchEvents(EventsCallback callback): Fetches events created by the current user.
 * - fetchEventById(int eventId, EventsCallback callback): Retrieves event details by its ID.
 * - createEvent(Event event, EventsCallback callback): Creates a new event with a unique ID.
 * - deleteEvent(int eventId, EventsCallback callback): Deletes an event and cleans up associated data.
 * - updateEvent(Event event, EventsCallback callback): Updates event details in the database.
 * - fetchAllEvents(EventsCallback callback): Fetches all events from the database.
 * - getEventCount(CountCallback callback): Retrieves the total number of events.

 * Entrants Management:
 * - fetchRegisteredEntrants(int eventId, EntrantsCallback callback): Fetches registered entrants for an event.
 * - fetchWaitlistedEntrants(int eventId, EntrantsCallback callback): Fetches waitlisted entrants for an event.
 * - fetchInvitedEntrants(int eventId, EntrantsCallback callback): Fetches invited entrants for an event.
 * - fetchDeclinedEntrants(int eventId, EntrantsCallback callback): Fetches declined entrants for an event.
 * - addEntrantWaitlist(int eventId, String userId, EventsCallback callback): Adds a user to the waitlist of an event.
 * - entrantFetchEvents(EventsCallback callback): Fetches all events the user is associated with.

 * Image Upload:
 * - uploadImageToFirebase(Uri imageUri, String field): Uploads a user profile image to Firebase.
 * - uploadPosterImageToFirebase(Uri imageUri, UploadCallback callback): Uploads an event poster image to Firebase.
 * - removeImage(String type, String userId, Callback callback) : removes a user profile photo from firebase

 * Admin Authentication:
 * - storeAdminAuthCode(String generatedCode, Callback callback): Stores an admin authentication code.
 * - deleteAdminAuthCode(String code, Callback callback): Deletes an admin authentication code.

 * Lottery & Invitation:
 * - updateInvitedEntrantsAndSetLotteryRan(int eventId, List<String> invitedEntrants, EventsCallback callback): Updates invited entrants after a lottery is run.
 * - entrantAcceptDeclineInvitation(int eventId, String action, StatusCallback callback): Handles user acceptance or decline of an event invitation.

 * Event Cleanup:
 * - clearEventLists(int eventId, EventsCallback callback): Clears all lists (waitlisted, invited, declined) for an event.
 * - removeEventFromUserRecords(int eventId, String organizerId, EventsCallback callback): Cleans up event data for users after an event is deleted.

 * Utility Methods:
 * - generateUniqueEventId(): Generates a unique ID for events.
 * - selectRandomEntrants(List<String> entrants, int count): Randomly selects a given number of entrants from a list.

 * Callbacks:
 * - Various callback interfaces like EventsCallback, UsersCallback, StatusCallback, etc., are defined for handling asynchronous operations.
 */
public class DatabaseHelper {

    private static final String TAG = "DatabaseHelper";
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private CollectionReference adminsRef;
    private CollectionReference adminAuthCodesRef;
    private CollectionReference notificationsRef;

    private String currentUserId;
    private StorageReference storageReference;

    /**
     * Constructor for DatabaseHelper. Initializes Firestore instance
     * and sets up references to "events" and "users" collections.
     * @param context context for the helper's usage.
     */
    public DatabaseHelper(Context context) {
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events"); // Reference to "events" collection
        usersRef = db.collection("users");   // Reference to "users" collection
        adminsRef = db.collection("admins"); //Reference to the "admin: collection
        adminAuthCodesRef = db.collection("admin_auth_codes"); //Reference to the "admin_auth_codes" collection
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");
        notificationsRef = db.collection("notifications"); // Reference to notifications collection

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
        void onUserFetched(User user);
        void onError(Exception e);
    }

    public interface UserCallback {
        void onUserFetched(User user);
        void onError(Exception e);
    }

    public interface CountCallback {
        void onCountFetched(int count);
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

    public interface StatusCallback {
        void onStatusUpdated();
        void onError(Exception e);
    }

    public interface Callback {
        void onSuccess(String message);
        void onFailure(Exception e);
    }

    // Callback interface for verifying the admin code
    public interface VerificationCallback {
        void onResult(boolean exists);
        void onError(Exception e);
    }

    // callback for upload functionality
    public interface UploadCallback {
        void onUploadSuccess(String downloadUrl);
        void onUploadFailure(Exception e);
    }

    /**
     * Callback interface for handling notifications fetching results.
     */
    public interface NotificationsCallback {
        void onNotificationsFetched(List<Notification> notifications);
        void onError(Exception e);
    }

    /**
     * Callback interface for fetching entrant lists.
     */
    public interface EntrantsListsCallback {
        void onEntrantsListsFetched(List<String> registered, List<String> waitlisted, List<String> invited, List<String> declined);
        void onError(Exception e);
    }

    /**
     * Fetches notifications for the current user based on their user_id.
     *
     * @param userId  The ID of the logged-in user.
     * @param callback The callback to handle the list of notifications or error.
     */
    public void fetchNotificationsForUser(String userId, final NotificationsCallback callback) {
        notificationsRef.whereEqualTo("user_id", userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Error fetching notifications.", e);
                            callback.onError(e);
                            return;
                        }

                        List<Notification> notifications = new ArrayList<>();
                        if (value != null) {
                            for (QueryDocumentSnapshot doc : value) {
                                Notification notification = doc.toObject(Notification.class);
                                notifications.add(notification);
                            }
                        }
                        callback.onNotificationsFetched(notifications); // Pass fetched notifications to callback
                    }
                });
    }


    /**
     * Fetch entrant lists (registered, waitlisted, invited, declined) for an event by its ID.
     *
     * @param eventId         The ID of the event.
     * @param callback        Callback to return the entrant lists or handle errors.
     */
    public void fetchEventEntrants(int eventId, EntrantsListsCallback callback) {
        db.collection("events")
                .document(String.valueOf(eventId))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> registeredEntrants = (List<String>) documentSnapshot.get("registeredEntrants");
                        List<String> waitlistedEntrants = (List<String>) documentSnapshot.get("waitlistedEntrants");
                        List<String> invitedEntrants = (List<String>) documentSnapshot.get("invitedEntrants");
                        List<String> declinedEntrants = (List<String>) documentSnapshot.get("declinedEntrants");

                        callback.onEntrantsListsFetched(
                                registeredEntrants != null ? registeredEntrants : new ArrayList<>(),
                                waitlistedEntrants != null ? waitlistedEntrants : new ArrayList<>(),
                                invitedEntrants != null ? invitedEntrants : new ArrayList<>(),
                                declinedEntrants != null ? declinedEntrants : new ArrayList<>()
                        );
                    } else {
                        callback.onError(new Exception("Event not found"));
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Fetch user details for a list of user IDs.(Another method has similar name, but only works for 1 user id)
     * This is used in the map functionality
     *
     * @param userIds   List of user IDs to fetch.
     * @param callback  Callback to return user details or handle errors.
     */
    public void fetchUsersByIds(List<String> userIds, UsersCallback callback) {
        if (userIds.isEmpty()) {
            callback.onUsersFetched(new ArrayList<>());
            return;
        }

        db.collection("users")
                .whereIn("user_id", userIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> users = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        User user = document.toObject(User.class);
                        users.add(user);
                    }
                    callback.onUsersFetched(users);
                })
                .addOnFailureListener(callback::onError);
    }


    /**
     * Checks if a user with the given userId exists in the admins collection.
     *
     * @param userId The ID of the user to check.
     * @param callback A callback that returns true if the user exists, false otherwise.
     */
    public void isAdminUser(String userId, final VerificationCallback callback) {
        Log.e("DatabaseHelper", "Checking admin status for userId: " + userId); // Log userId being checked
        adminsRef.document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Log.e("DatabaseHelper", "User is admin: " + userId); // Log if user is admin
                            callback.onResult(true); // User exists in admins collection
                        } else {
                            Log.e("DatabaseHelper", "User is not admin: " + userId); // Log if user is not admin
                            callback.onResult(false); // User does not exist in admins collection
                        }
                    } else {
                        Log.e("DatabaseHelper", "Error checking admin status for userId: " + userId, task.getException());
                        callback.onError(task.getException());
                    }
                });
    }

    /**
     * Stores a generated admin code in the 'admin_auth_codes' collection.
     *
     * @param generatedCode The code to store in Firestore.
     * @param callback Callback to handle success or failure.
     */
    public void storeAdminAuthCode(String generatedCode, final Callback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

        adminAuthCodesRef.document(generatedCode)
                .set(data)
                .addOnSuccessListener(aVoid -> callback.onSuccess("Admin Auth Code Stored"))
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Adds a user to the 'admins' collection in Firestore.
     * Deletes the admin auth code from 'admin_auth_codes' after successful admin addition.
     *
     * @param deviceId The device ID to add as an admin.
     * @param authCode The admin auth code to delete after verification.
     * @param callback Callback to handle success or failure.
     */
    public void addUserToAdmins(String deviceId, String authCode, final Callback callback) {
        adminsRef.document(deviceId).set(new HashMap<>())
                .addOnSuccessListener(aVoid -> {
                    deleteAdminAuthCode(authCode, new Callback() {
                        @Override
                        public void onSuccess(String message) {
                            Log.d("DatabaseHelper", "User " + deviceId +" has been added as an Admin");
                            callback.onSuccess("User successfully added as Admin");
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.d("DatabaseHelper", "Failed to add User " + deviceId +" as an Admin", e);
                        }
                    });
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * This method removes unused admin authentication codes
     * @param code - auth that needs to be removed
     * @param callback - Callback to handle success or failure.
     */
    public void deleteAdminAuthCode(String code, final Callback callback) {
        adminAuthCodesRef.document(code)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess("Admin code deleted successfully"))
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Get the userId of the person running an instance of the app.
     *
     * @return user's id
     */
    public String getCurrentUserId() {
        return currentUserId;
    }


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
     * Finds an event by its eventId.
     *
     * @param eventId event to be found.
     * @param callback handles success/failure of the fetching operation.
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
     * Creates a new unique event. Hardcoded test code may be removed in the future.
     * The new event is given a unique 5 digit identification.
     *
     * @param event to be created
     * @param callback handles success/failure of event creation.
     */
    public void createEvent(Event event, EventsCallback callback) {
        String uniqueEventId;
        if(event.getEventName().equals("0000A - Event Running Test")){
            event.setEventId(00000);
            uniqueEventId = "00000";
        } else {
            uniqueEventId = generateUniqueEventId();
            // Set the generated event_id in the Event DTO
            event.setEventId(Integer.parseInt(uniqueEventId));
        }

        // Try adding the event with the event_id as the document ID
        eventsRef.document(uniqueEventId)
                .set(event)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Event created successfully with event_id: " + uniqueEventId);

                    // Update the organizer's hosted events after creating the event
                    updateHostedEventsForOrganizer(event.getOrganizerId(), event.getEventId());

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
     * Update the hosted events field for a user
     * when they create an event and become the organizer of it
     *
     * @param organizerId organizer that made the event.
     * @param eventId event created.
     */
    private void updateHostedEventsForOrganizer(String organizerId, int eventId) {
        usersRef.document(organizerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the current list of hosted events
                        List<Integer> hostedEvents = (List<Integer>) documentSnapshot.get("hosted_events");
                        if (hostedEvents == null) {
                            hostedEvents = new ArrayList<>();
                        }
                        // Add the new event ID to the list
                        hostedEvents.add(eventId);

                        // Update the user's document with the new list of hosted events
                        usersRef.document(organizerId)
                                .update("hosted_events", hostedEvents)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Successfully updated hosted events for organizer"))
                                .addOnFailureListener(e -> Log.e(TAG, "Error updating hosted events", e));
                    } else {
                        Log.e(TAG, "Organizer document not found");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch organizer document", e));
    }

    /**
     * Helper method to find the amount of events.
     *
     * @param callback handles success/failure of event count operation.
     */

    public void getEventCount(final CountCallback callback) {
        eventsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int eventCount = task.getResult().size();
                callback.onCountFetched(eventCount);
            } else {
                callback.onError(task.getException());
            }
        });
    }


    /**
     * Fetches all events from the events collection without any conditions.
     *
     * @param callback handles success/failure of the fetching process.
     */
    public void fetchAllEvents(final EventsCallback callback) {
         eventsRef.get()
                 .addOnCompleteListener(task -> {
                     if (task.isSuccessful()) {
                         List<Event> events = new ArrayList<>();
                         for (QueryDocumentSnapshot doc : task.getResult()) {
                             Event event = doc.toObject(Event.class);
                             events.add(event);
                         }
                         callback.onEventsFetched(events); // Pass the list of events to the callback
                     } else {
                         callback.onError(task.getException()); // Handle any errors
                     }
                 });
     }

    /**
     * Deletes a particular event from the database.
     *
     * @param eventId to be deleted.
     * @param callback handles success/failure of deletion.
     */
    public void deleteEvent(int eventId, final EventsCallback callback) {
        eventsRef.whereEqualTo("event_id", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Retrieve the event document to get the organizer ID
                        DocumentSnapshot eventDoc = task.getResult().getDocuments().get(0);
                        Event event = eventDoc.toObject(Event.class);

                        if (event != null) {
                            String organizerId = event.getOrganizerId(); // Get the organizer ID

                            // Proceed to delete the event document
                            eventsRef.document(eventDoc.getId()).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Event deleted successfully");

                                        // Remove event ID from user records, including organizer’s hosted_events
                                        removeEventFromUserRecords(eventId, organizerId, callback);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error deleting event", e);
                                        callback.onError(e);
                                    });
                        } else {
                            callback.onError(new Exception("Event data missing"));
                        }
                    } else {
                        callback.onError(new Exception("Event not found"));
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Removes event from an organizer's hosted events, and from an entrant's joined events
     *
     * @param eventId event to be removed from records.
     * @param organizerId organizer hosting the event.
     * @param callback handles success/failure of removal.
     */
    private void removeEventFromUserRecords(int eventId, String organizerId, final EventsCallback callback) {
        // Update organizer's hosted_events list
        usersRef.document(organizerId).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                User organizer = document.toObject(User.class);
                if (organizer != null && organizer.getHostedEvents().contains(eventId)) {
                    organizer.getHostedEvents().remove((Integer) eventId);
                    usersRef.document(organizerId).update("hosted_events", organizer.getHostedEvents());
                }
            }
        });

        // Update each user's joined_events list
        usersRef.whereArrayContains("joined_events", eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot userDoc : task.getResult().getDocuments()) {
                    User user = userDoc.toObject(User.class);
                    if (user != null) {
                        List<Integer> joinedEvents = user.getJoinedEvents();
                        if (joinedEvents.contains(eventId)) {
                            joinedEvents.remove((Integer) eventId);
                            usersRef.document(user.getUserId()).update("joined_events", joinedEvents);
                        }
                    }
                }
                callback.onEventsFetched(null); // Notify once all updates complete
            } else {
                callback.onError(new Exception("Error updating users' joined_events lists"));
            }
        });
    }



    /**
     * Updates an event
     *
     * @param event event to be updated
     * @param callback handles success/failure of updating.
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

    /**
     * Retrieves the registered entrants list for a particular event ID
     *
     * @param eventId event fetching operation is done on.
     * @param callback handles success/failure of the fetching operation.
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


    /**
     * Retrieves the waitlisted entrants list for a particular event ID
     *
     * @param eventId event operation is done on.
     * @param callback handles success/failure of fetching operation.
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


    /**
     * Retrieves the invited entrants list for a particular event id
     *
     * @param eventId event operation is done on.
     * @param callback handles success/failure of fetching operation.
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


    /**
     * Retrieves the declined entrants list for an event id
     *
     * @param eventId event operation is done on.
     * @param callback handles success/failure of fetching operation.
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

    /**
     * Updates invited entrants list after lottery is ran by organizer
     *
     * @param eventId event to be updated.
     * @param invitedEntrants entrants that have won the lottery.
     * @param callback handles success/failure of the update process.
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


    /**
     * Helper function to select random entrants.
     *
     * @param entrants to choose from.
     * @param count amount of entrants to choose.
     * @return entrants that were selected
     */
    public List<String> selectRandomEntrants(List<String> entrants, int count) {
        List<String> selectedEntrants = new ArrayList<>();
        Random random = new Random();
        while (selectedEntrants.size() < count && !entrants.isEmpty()) {
            int index = random.nextInt(entrants.size());
            selectedEntrants.add(entrants.remove(index));
        }
        return selectedEntrants;
    }


    /**
     * Listen for changes in the declined entrants list;
     * If someone declines they're added here and we must re-pick randomly to fill their spot
     *
     * @param eventId event whose declined list changed.
     * @param callback handles success/failure of the listener.
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


    /**
     * Update both the waitlisted entrants list as well as the invited entrants list, after someone declines an invitation and someone else is picked to fill that spot
     *
     * @param eventId to be updated
     * @param invitedEntrants list of invited entrants
     * @param waitlistedEntrants list of waitlisted entrants
     * @param callback handles success/failure of the update operation.
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

    public void removeUserFromInvitedEntrants(Integer eventId, String userId) {
        eventsRef.whereEqualTo("event_id", eventId).get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                querySnapshot.getDocuments().forEach(documentSnapshot -> {
                    List<String> invitedEntrants = (List<String>) documentSnapshot.get("invited_entrants");
                    if (invitedEntrants != null && invitedEntrants.contains(userId)) {
                        invitedEntrants.remove(userId);
                        documentSnapshot.getReference().update("invited_entrants", invitedEntrants)
                                .addOnSuccessListener(aVoid -> Log.d("DatabaseHelper", "User " + userId + " removed from invited entrants for event " + eventId))
                                .addOnFailureListener(e -> Log.e("DatabaseHelper", "Failed to remove user " + userId + " from invited entrants for event " + eventId, e));
                    }
                });
            }
        }).addOnFailureListener(e -> Log.e("DatabaseHelper", "Error fetching event document", e));
    }

    public void removeUserFromWaitlistedEntrants(Integer eventId, String userId) {
        eventsRef.whereEqualTo("event_id", eventId).get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                querySnapshot.getDocuments().forEach(documentSnapshot -> {
                    List<String> waitlistedEntrants = (List<String>) documentSnapshot.get("waitlisted_entrants");
                    if (waitlistedEntrants != null && waitlistedEntrants.contains(userId)) {
                        waitlistedEntrants.remove(userId);
                        documentSnapshot.getReference().update("waitlisted_entrants", waitlistedEntrants)
                                .addOnSuccessListener(aVoid -> Log.d("DatabaseHelper", "User " + userId + " removed from waitlisted entrants for event " + eventId))
                                .addOnFailureListener(e -> Log.e("DatabaseHelper", "Failed to remove user " + userId + " from waitlisted entrants for event " + eventId, e));
                    }
                });
            }
        }).addOnFailureListener(e -> Log.e("DatabaseHelper", "Error fetching event document", e));
    }

    /**
     * Removes a user from the registered entrants list of an event.
     *
     * @param eventId event the user is a part of.
     * @param userId user to be removed from the registered entrants list.
     */

    public void removeUserFromRegisteredEntrants(Integer eventId, String userId) {
        eventsRef.whereEqualTo("event_id", eventId).get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                querySnapshot.getDocuments().forEach(documentSnapshot -> {
                    List<String> registeredEntrants = (List<String>) documentSnapshot.get("registered_entrants");
                    if (registeredEntrants != null && registeredEntrants.contains(userId)) {
                        registeredEntrants.remove(userId);
                        documentSnapshot.getReference().update("registered_entrants", registeredEntrants)
                                .addOnSuccessListener(aVoid -> Log.d("DatabaseHelper", "User " + userId + " removed from registered entrants for event " + eventId))
                                .addOnFailureListener(e -> Log.e("DatabaseHelper", "Failed to remove user " + userId + " from registered entrants for event " + eventId, e));
                    }
                });
            }
        }).addOnFailureListener(e -> Log.e("DatabaseHelper", "Error fetching event document", e));
    }

    /**
     * Removes a user from the declined entrants list of an event.
     *
     * @param eventId event the user is a part of.
     * @param userId user to be removed from the registered entrants list.
     */

    public void removeUserFromDeclinedEntrants(Integer eventId, String userId) {
        eventsRef.whereEqualTo("event_id", eventId).get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                querySnapshot.getDocuments().forEach(documentSnapshot -> {
                    List<String> declinedEntrants = (List<String>) documentSnapshot.get("declined_entrants");
                    if (declinedEntrants != null && declinedEntrants.contains(userId)) {
                        declinedEntrants.remove(userId);
                        documentSnapshot.getReference().update("declined_entrants", declinedEntrants)
                                .addOnSuccessListener(aVoid -> Log.d("DatabaseHelper", "User " + userId + " removed from declined entrants for event " + eventId))
                                .addOnFailureListener(e -> Log.e("DatabaseHelper", "Failed to remove user " + userId + " from declined entrants for event " + eventId, e));
                    }
                });
            }
        }).addOnFailureListener(e -> Log.e("DatabaseHelper", "Error fetching event document", e));
    }


    /**
     * Clears the waitlisted, invited, and declined entrants lists for a given event.
     * Also removes this event ID from each user's joined events list.
     *
     * @param eventId   The ID of the event whose lists need to be cleared.
     * @param callback  The EventsCallback to handle success or failure.
     */
    public void clearEventLists(int eventId, EventsCallback callback) {
        // Reference to the event document in Firestore
        DocumentReference eventRef = eventsRef.document(String.valueOf(eventId));

        // Fetch the event first to access the lists before clearing them
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                if (event != null) {
                    // Collect user IDs from all lists to update their joined_events
                    List<String> userIdsToUpdate = new ArrayList<>();
                    userIdsToUpdate.addAll(event.getWaitlistedEntrants());
                    userIdsToUpdate.addAll(event.getInvitedEntrants());
                    userIdsToUpdate.addAll(event.getDeclinedEntrants());

                    // Clear the lists in Firestore
                    eventRef.update("waitlisted_entrants", new ArrayList<>(),
                                    "invited_entrants", new ArrayList<>(),
                                    "declined_entrants", new ArrayList<>())
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Successfully cleared event lists for event ID: " + eventId);

                                // Remove the event from each user's joined_events list
                                for (String userId : userIdsToUpdate) {
                                    removeEventFromJoinedEvents(userId, eventId);
                                }

                                callback.onEventsFetched(null);  // Trigger callback on success
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error clearing event lists for event ID: " + eventId, e);
                                callback.onError(e);  // Trigger error callback
                            });
                } else {
                    callback.onError(new Exception("Event data could not be parsed."));
                }
            } else {
                callback.onError(new Exception("Event does not exist."));
            }
        }).addOnFailureListener(e -> {
            callback.onError(e);  // Trigger error if fetching event fails
        });
    }



    /**
     * Removes a specific event ID from a user's joined events list.
     *
     * @param userId  The ID of the user whose joined events need updating.
     * @param eventId The ID of the event to remove from the user's joined events.
     */
    private void removeEventFromJoinedEvents(String userId, int eventId) {
        DocumentReference userRef = usersRef.document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    List<Integer> joinedEvents = user.getJoinedEvents();

                    // Remove the eventId if it exists in the joined_events list
                    if (joinedEvents.contains(eventId)) {
                        joinedEvents.remove(Integer.valueOf(eventId));  // Explicitly remove by Integer value
                        userRef.update("joined_events", joinedEvents)
                                .addOnSuccessListener(aVoid ->
                                        Log.d(TAG, "Successfully removed event ID " + eventId + " from user " + userId + "'s joined events."))
                                .addOnFailureListener(e ->
                                        Log.e(TAG, "Error updating joined events for user " + userId, e));
                    }
                }
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching user for joined events update", e);
        });
    }


    // User Calls
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
     * Adds a user to the firestore database along with their data.
     *
     * @param userData user object to be added
     * @param imageUri user's photo
     */
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
     * Grabs a user from the users collection by the userID
     * @param userId - Device of that linked user
     * @param callback - callback interface for users
     */
    public void fetchUserById(String userId, final UsersCallback callback) {
        usersRef.whereEqualTo("user_id", userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();

                        if (documents != null && !documents.isEmpty()) {
                            if (documents.size() == 1) {
                                // If exactly one user is found, map it to the User class
                                User user = documents.get(0).toObject(User.class);
                                callback.onUserFetched(user);
                            } else {
                                // If multiple users are found for the same ID, return placeholder user
                                User user = new User();
                                user.setFirstName("No Organizer Found");
                                callback.onUserFetched(user);
                            }
                        } else {
                            // No documents found; return placeholder user
                            User user = new User();
                            user.setFirstName("No Organizer Found");
                            callback.onUserFetched(user);
                        }
                    } else {
                        callback.onError(task.getException());
                    }
                });
    }

    /**
     * Check if the user exists in the database.
     *
     * @param callback handles success/failure of checking if a user exists.
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
    public void getUserCount(final CountCallback callback) {
        usersRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        int userCount = (snapshot != null) ? snapshot.size() : 0;
                        callback.onCountFetched(userCount);
                    } else {
                        Log.e(TAG, "Error fetching user count", task.getException());
                        callback.onError(task.getException());
                    }
                });
    }

    /**
     * Removes a user's profile image or an event poster from Firebase Storage
     * and sets the corresponding Firestore field to a default value.
     *
     * @param type     The type of entity ("user" or "event").
     * @param id       The ID of the entity whose image should be removed.
     * @param callback Callback to handle the success or failure of the operation.
     */
    public void removeImage(String type, String id, Callback callback) {
        String normalizedType = type.toLowerCase(Locale.ROOT);
        boolean isUserRef;
        CollectionReference activeRef;

        if (normalizedType.equals("user")) {
            isUserRef = true;
            activeRef = usersRef;
        } else if (normalizedType.equals("event")) {
            isUserRef = false;
            activeRef = eventsRef;
        } else {
            callback.onFailure(new Exception("Invalid type. Must be 'user' or 'event'."));
            return;
        }

        // Fetch the document from Firestore
        activeRef.document(id).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String imageUrl = documentSnapshot.getString(isUserRef ? "user_photo" : "poster_url");

                // Check if the image URL is valid or already default
                if (imageUrl == null || imageUrl.isEmpty() || imageUrl.equals("NoProfilePhoto") ||
                        imageUrl.equals("https://example.com/default-poster.png")) {
                    callback.onFailure(new Exception("No profile photo or poster to remove."));
                    return;
                }

                // Delete the image from Firebase Storage
                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                photoRef.delete().addOnSuccessListener(aVoid -> {
                    // Update Firestore with the default value after deletion
                    String defaultImage = isUserRef ? "NoProfilePhoto" : "https://example.com/default-poster.png";
                    activeRef.document(id)
                            .update(isUserRef ? "user_photo" : "poster_url", defaultImage)
                            .addOnSuccessListener(unused -> callback.onSuccess(
                                    (isUserRef ? "Profile photo " : "Event poster ") + "was removed successfully."))
                            .addOnFailureListener(e -> {
                                Log.e("DatabaseHelper", "Error updating Firestore: " + e.getMessage(), e);
                                callback.onFailure(e);
                            });
                }).addOnFailureListener(e -> {
                    Log.e("DatabaseHelper", "Error deleting image from Storage: " + e.getMessage(), e);
                    callback.onFailure(e);
                });
            } else {
                callback.onFailure(new Exception("Document not found."));
            }
        }).addOnFailureListener(e -> {
            Log.e("DatabaseHelper", "Error fetching document: " + e.getMessage(), e);
            callback.onFailure(e);
        });
    }

    public void getUser(String userID, UserCallback callback) {
        usersRef.document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = document.toObject(User.class);

                        callback.onUserFetched(user);
                    } else {
                        Log.d("Firestore", "No such user exists");
                    }
                } else {
                    Log.d("Firestore", "Error getting user", task.getException());
                }
            }
        });
    }

    /**
     * This method removed a user from the DB
     *
     * @param userId - The user ID (device ID) of the person to be removed
     */
    public void removeUser(String userId) {
        // Removing the user if they are an admin
        adminsRef.document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                adminsRef.document(userId).delete()
                        .addOnSuccessListener(aVoid ->
                                Log.d("DatabaseHelper", "User " + userId + " removed from admins collection"))
                        .addOnFailureListener(e -> Log.e("DatabaseHelper", "Failed to remove user from admins collection", e));
            } else {
                Log.d("DatabaseHelper", "User " + userId + " does not exist in admins collection or task failed.");
            }
        });

        //Remove user profile photo from storage
        removeImage("user", userId, new Callback() {
            @Override
            public void onSuccess(String message) {
                Log.d("DatabaseHelper", "User " + userId + message);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("DatabaseHelper", Objects.requireNonNull(e.getMessage()));
            }
        });


        // Removing a user from the users collection
        usersRef.document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                List<Integer> joinedEvents = null;
                List<Integer> hostedEvents = null;

                List<Long> joinedEventsLong = (List<Long>) task.getResult().get("joined_events");
                if (joinedEventsLong != null) {
                    joinedEvents = new ArrayList<>();
                    for (Long eventId : joinedEventsLong) {
                        joinedEvents.add(eventId.intValue()); // Convert Long to Integer
                    }
                }

                List<Long> hostedEventsLong = (List<Long>) task.getResult().get("hosted_events");
                if (hostedEventsLong != null) {
                    hostedEvents = new ArrayList<>();
                    for (Long eventId : hostedEventsLong) {
                        hostedEvents.add(eventId.intValue()); // Convert Long to Integer
                    }
                }

                // Removing user from joined events
                if (joinedEvents != null) {
                    for (Integer eventId : joinedEvents) {
                        removeUserFromWaitlistedEntrants(eventId, userId);
                        removeUserFromInvitedEntrants(eventId, userId);
                        removeUserFromRegisteredEntrants(eventId, userId);
                        removeUserFromDeclinedEntrants(eventId, userId);
                    }
                }

                // Removing hosted events
                if (hostedEvents != null) {
                    for (Integer eventId : hostedEvents) {
                        deleteEvent(eventId, new EventsCallback() {
                            @Override
                            public void onEventsFetched(List<Event> events) {
                                Log.d("DatabaseHelper", "Event " + eventId + " deleted as part of user removal.");
                            }

                            @Override
                            public void onEventFetched(Event event) {
                                // No action needed here
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("DatabaseHelper", "Failed to delete event " + eventId, e);
                            }
                        });
                    }
                }

                // Finally, delete user from users collection
                usersRef.document(userId).delete()
                        .addOnSuccessListener(aVoid -> Log.d("DatabaseHelper", "User " + userId + " removed from users collection"))
                        .addOnFailureListener(e -> Log.e("DatabaseHelper", "Failed to remove user from users collection", e));
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

    /**
     * Uploads image to firebase storage and passes url back through callback
     *
     * @param imageUri URI of image to be uploaded.
     * @param callback methods for handling success/failure of upload.
     */
    public void uploadPosterImageToFirebase(Uri imageUri, UploadCallback callback) {
        String fileName = "event_posters/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference fileRef = storageReference.child(fileName);

        // Upload the image to Firebase Storage
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    callback.onUploadSuccess(downloadUrl);  // Pass the URL back to the callback
                }))
                .addOnFailureListener(e -> {
                    callback.onUploadFailure(e);  // Pass the error back to the callback
                });
    }

    /**
     * Gets the events that the user is apart of, for any list that the user is part of
     * for any event, that event is passed to callback.
     *
     * @param callback methods for handling success/failure.
     */

    public void entrantFetchEvents(EventsCallback callback) {
        List<Event> entrantsEvents = new ArrayList<>();

        db.collection("events")
                .whereArrayContains("invited_entrants", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            entrantsEvents.add(event);
                        }
                    }

                    db.collection("events")
                            .whereArrayContains("registered_entrants", currentUserId)
                            .get()
                            .addOnCompleteListener(secondTask -> {
                                if (secondTask.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : secondTask.getResult()) {
                                        Event event = document.toObject(Event.class);
                                        if (!entrantsEvents.contains(event)) { // Avoid duplicates
                                            entrantsEvents.add(event);
                                        }
                                    }
                                }

                                db.collection("events")
                                        .whereArrayContains("declined_entrants", currentUserId)
                                        .get()
                                        .addOnCompleteListener(third -> {
                                            if (third.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : third.getResult()) {
                                                    Event event = document.toObject(Event.class);
                                                    if (!entrantsEvents.contains(event)) { // Avoid duplicates
                                                        entrantsEvents.add(event);
                                                    }
                                                }
                                            }
                                            db.collection("events")
                                                    .whereArrayContains("waitlisted_entrants", currentUserId)
                                                    .get()
                                                    .addOnCompleteListener(fourth -> {
                                                        if (fourth.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : fourth.getResult()) {
                                                                Event event = document.toObject(Event.class);
                                                                if (!entrantsEvents.contains(event)) { // Avoid duplicates
                                                                    entrantsEvents.add(event);
                                                                }
                                                            }
                                                        }
                                                        callback.onEventsFetched(entrantsEvents);
                                                    });
                                        });
                            });
                });
    }

    /**
     * Removes the current user from a waiting list for an event.
     *
     * @param eventId event that the entrant should be removed from the waiting list of.
     * @param callback handles success/failure of waiting list removal for an entrant.
     */

    public void entrantLeaveWaitingList(int eventId, StatusCallback callback) {
        eventsRef.document(Integer.toString(eventId)).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> waitlisted = (List<String>) documentSnapshot.get("waitlisted_entrants");

                // Step 2: Check if the value exists in the sourceList
                if (waitlisted != null && waitlisted.contains(currentUserId)) {
                    // Remove from sourceList and add to targetList
                    waitlisted.remove(currentUserId);

                    // Step 3: Update Firestore with the modified lists
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("waitlisted_entrants", waitlisted);

                    eventsRef.document(Integer.toString(eventId)).update(updates)
                            .addOnSuccessListener(aVoid -> {
                                // Successfully updated the lists
                                Log.d("Firestore", "Value moved successfully.");

                                usersRef.document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                User user = document.toObject(User.class);

                                                if (user != null) {
                                                    List<Integer> joinedEvents = user.getJoinedEvents();
                                                    if (joinedEvents.contains(eventId)) {
                                                        joinedEvents.remove((Integer) eventId);
                                                        usersRef.document(currentUserId)
                                                                .update("joined_events", joinedEvents)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    callback.onStatusUpdated();
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    Log.e(TAG,"Failed to modify users joined events list", e);
                                                                    callback.onError(e);
                                                                });
                                                    }
                                                }
                                            } else {
                                                Log.d("Firestore", "No such user exists");
                                            }
                                        } else {
                                            Log.d("Firestore", "Error getting user", task.getException());
                                        }
                                    }
                                });;
                            })
                            .addOnFailureListener(e -> {
                                // Failed to update
                                Log.e("Firestore", "Error updating document", e);
                                callback.onError(e);
                            });
                } else {
                    Log.d("Firestore", "Value not found in source list.");
                }
            } else {
                Log.d("Firestore", "Document does not exist.");
            }
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Error retrieving document");
        });
    }

    /**
     * Handles the actions for an entrant to accept or decline an invitation for an event
     * after being a part of the event and winning the lottery.
     *
     * @param eventId event that the user won the lottery for
     * @param action accept or decline from the user.
     * @param callback handles success/failure of add/decline operation.
     */

    public void entrantAcceptDeclineInvitation(int eventId, String action, StatusCallback callback) {
        eventsRef.document(Integer.toString(eventId)).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> invited = (List<String>) documentSnapshot.get("invited_entrants");
                String targetField;
                List<String> targetList = new ArrayList<>();

                if(Objects.equals(action, "accept")) {
                    targetField = "registered_entrants";
                    targetList = (List<String>) documentSnapshot.get("registered_entrants");
                }else {
                    targetField = "declined_entrants";
                    targetList = (List<String>) documentSnapshot.get("declined_entrants");
                }

                // Step 2: Check if the value exists in the sourceList
                if (invited != null && invited.contains(currentUserId)) {
                    // Remove from sourceList and add to targetList
                    invited.remove(currentUserId);
                    targetList.add(currentUserId);

                    // Step 3: Update Firestore with the modified lists
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("invited_entrants", invited);
                    updates.put(targetField, targetList);

                    eventsRef.document(Integer.toString(eventId)).update(updates)
                            .addOnSuccessListener(aVoid -> {
                                // Successfully updated the lists
                                Log.d("Firestore", "Value moved successfully.");
                                callback.onStatusUpdated();
                            })
                            .addOnFailureListener(e -> {
                                // Failed to update
                                Log.e("Firestore", "Error updating document", e);
                                callback.onError(e);
                            });
                } else {
                    Log.d("Firestore", "Value not found in source list.");
                }
            } else {
                Log.d("Firestore", "Document does not exist.");
            }
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Error retrieving document");
        });
    }

    /**
     * Method to add an entrant to a waitlist using the eventId of the event.
     * User is added to waitlist if they are not already a part of it.
     *
     * @param eventId to be joined
     * @param userId current user that wants to join the waitlist
     * @param callback handles the results of the method
     */
    public void addEntrantWaitlist(int eventId, String userId, EventsCallback callback) {
        fetchEventById(eventId, new EventsCallback() {
            @Override
            public void onEventsFetched(List<Event> events) {
                // Required but unused in this context
            }

            @Override
            public void onEventFetched(Event event) {
                List<String> waitlistedEntrants = event.getWaitlistedEntrants();
                List<String> declinedEntrants = event.getDeclinedEntrants();

                // Check if user is already in waitlisted or declined entrants
                if (waitlistedEntrants.contains(userId)) {
                    callback.onError(new Exception("User is already on the waitlist."));
                    return;
                }

                if (declinedEntrants.contains(userId)) {
                    callback.onError(new Exception("User has declined and cannot be added to the waitlist."));
                    return;
                }

                // Check if waitlist limit has been reached
                if (event.getWaitlistLimit() > 0 && waitlistedEntrants.size() >= event.getWaitlistLimit()) {
                    callback.onError(new Exception("Waitlist limit has been reached."));
                    return;
                }

                // Add the user to the waitlist
                waitlistedEntrants.add(userId);
                eventsRef.document(Integer.toString(eventId))
                        .update("waitlisted_entrants", waitlistedEntrants)
                        .addOnSuccessListener(aVoid -> {
                            // Now add the event ID to the user's joined_events list as an int
                            DocumentReference userRef = usersRef.document(userId);
                            userRef.get().addOnSuccessListener(userSnapshot -> {
                                if (userSnapshot.exists()) {
                                    List<Long> joinedEvents = (List<Long>) userSnapshot.get("joined_events");
                                    if (joinedEvents == null) {
                                        joinedEvents = new ArrayList<>();
                                    }

                                    // Add the eventId as an int if not already present
                                    if (!joinedEvents.contains((long) eventId)) {
                                        joinedEvents.add((long) eventId);
                                        userRef.update("joined_events", joinedEvents)
                                                .addOnSuccessListener(unused -> callback.onEventFetched(event))
                                                .addOnFailureListener(callback::onError);
                                    } else {
                                        callback.onEventFetched(event); // Already in the joined events, proceed without error
                                    }
                                } else {
                                    callback.onError(new Exception("User document not found."));
                                }
                            }).addOnFailureListener(callback::onError);
                        })
                        .addOnFailureListener(callback::onError);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e); // Handle errors if fetchEventById fails
            }
        });
    }
}