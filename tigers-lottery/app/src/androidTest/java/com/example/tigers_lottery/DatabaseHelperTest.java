package com.example.tigers_lottery;

import static org.junit.Assert.*;

/*
    TO START THE FIREBASE EMULATOR, DO "firebase emulators:start"
 */
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tigers_lottery.models.Event;
import com.example.tigers_lottery.models.Notification;
import com.example.tigers_lottery.models.User;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {
    private static FirebaseFirestore db;
    private DatabaseHelper databaseHelper;
    private String testCollectionName;

    @BeforeClass
    public static void globalSetUp() {
        // Initialize Firestore emulator
        db = FirebaseFirestore.getInstance();
        db.useEmulator("10.0.2.2", 8080); // Firestore emulator instance
    }

    @Before
    public void setUp() {
        // Create a new instance of DatabaseHelper
        databaseHelper = new DatabaseHelper(ApplicationProvider.getApplicationContext());

        // Generate a unique collection name for this test
        testCollectionName = generateTestCollectionName(Thread.currentThread().getStackTrace()[2].getMethodName());

        // Clear any leftover data from the collection
        clearCollection(testCollectionName);
    }

    @After
    public void tearDown() {
        // Clean up the collection after each test
        if (testCollectionName != null) {
            clearCollection(testCollectionName);
        }
    }

    private void clearCollection(String collectionName) {
        try {
            QuerySnapshot snapshot = Tasks.await(db.collection(collectionName).get());
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                Tasks.await(doc.getReference().delete());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while clearing collection: " + collectionName, e);
        }
    }

    private String generateTestCollectionName(String testName) {
        return "test_" + testName + "_" + System.currentTimeMillis();
    }


    private Event createMockEvent() {
        Event mockEvent = new Event();
        mockEvent.setEventId(1);
        mockEvent.setEventName("Mock Event");
        mockEvent.setGeolocation(new GeoPoint(37.7749, -122.4194)); // Example: San Francisco coordinates
        mockEvent.setOrganizerId("mock_organizer_id");
        mockEvent.setPosterUrl("https://example.com/mock-poster.png");
        mockEvent.setEventDate(Timestamp.now());
        mockEvent.setWaitlistOpenDate(Timestamp.now());
        mockEvent.setWaitlistDeadline(Timestamp.now());
        mockEvent.setWaitlistLimit(50);
        mockEvent.setWaitlistLimitFlag(true);
        mockEvent.setLocation("Mock Event Location");
        mockEvent.setDescription("This is a mock event description.");
        mockEvent.setRegisteredEntrants(List.of("user1", "user2"));
        mockEvent.setWaitlistedEntrants(List.of("user3", "user4"));
        mockEvent.setInvitedEntrants(List.of("user5", "user6"));
        mockEvent.setDeclinedEntrants(List.of("user7"));
        mockEvent.setOccupantLimit(100);
        mockEvent.setLotteryRan(false);
        mockEvent.setQRCode("mockQRCodeData");
        mockEvent.setGeolocationRequired(false);
        return mockEvent;
    }

    private Notification createMockNotification() {
        Notification mockNotification = new Notification();
        mockNotification.setNotificationId(101);
        mockNotification.setUserId("testUser");
        mockNotification.setEventId(202);
        mockNotification.setSenderId("organizer123");
        mockNotification.setType("lottery_win");
        mockNotification.setCategory("events");
        mockNotification.setPriority("high");
        mockNotification.setMessage("Congratulations! You won the lottery for the event.");
        mockNotification.setTimestamp(Timestamp.now());
        mockNotification.setReadStatus(false);
        mockNotification.setScheduledTime(Timestamp.now());
        mockNotification.setLocationMetadata(new GeoPoint(37.7749, -122.4194)); // Example: San Francisco coordinates
        mockNotification.setMetadata(Map.of(
                "event_name", "Mock Event"
        ));
        mockNotification.setActionUrl("https://example.com/event-details");
        return mockNotification;
    }

    private User createMockUser() {
        User mockUser = new User();

        mockUser.setUserId("testUser123");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmailAddress("johndoe@example.com");
        mockUser.setPhoneNumber("1234567890");
        mockUser.setUserGeolocation(new GeoPoint(40.7128, -74.0060)); // Example: New York City coordinates
        mockUser.setUserPhoto("https://example.com/profile_photo.jpg");
        mockUser.setDateOfBirth(Timestamp.now());
        mockUser.setFacilityLocation("123 Main St, New York, NY");
        mockUser.setFacilityName("Example Facility");
        mockUser.setFacilityEmail("facility@example.com");
        mockUser.setFacilityPhone("0987654321");
        mockUser.setFacilityPhoto("https://example.com/facility_photo.jpg");
        mockUser.setNotificationFlag(true);
        mockUser.setHostedEvents(List.of(101, 102, 103)); // Event IDs hosted by the user
        mockUser.setJoinedEvents(List.of(201, 202, 203)); // Event IDs the user has joined
        return mockUser;
    }


    @Test
    public void testOrganizerFetchEvents() throws Exception {
        // Setup: Use a hardcoded organizer ID
        String hardcodedOrganizerId = "organizer_test_456";

        // Create mock events with the hardcoded organizer ID
        Event mockEvent1 = createMockEvent();
        mockEvent1.setEventId(201);
        mockEvent1.setOrganizerId(hardcodedOrganizerId);
        mockEvent1.setEventName("Hardcoded Organizer Event 1");

        Event mockEvent2 = createMockEvent();
        mockEvent2.setEventId(202);
        mockEvent2.setOrganizerId(hardcodedOrganizerId);
        mockEvent2.setEventName("Hardcoded Organizer Event 2");

        // Create an unrelated event
        Event unrelatedEvent = createMockEvent();
        unrelatedEvent.setEventId(203);
        unrelatedEvent.setOrganizerId("other_organizer");
        unrelatedEvent.setEventName("Unrelated Event");

        // Insert mock events into Firestore
        Tasks.await(db.collection("events").document("event_201").set(mockEvent1));
        Tasks.await(db.collection("events").document("event_202").set(mockEvent2));
        Tasks.await(db.collection("events").document("event_203").set(unrelatedEvent));

        // Wait a short delay to ensure Firestore processes the insertion
        Thread.sleep(200);

        CountDownLatch latch = new CountDownLatch(1);
        final List<Event>[] fetchedEvents = new List[1];

        // Test: Query events for the hardcoded organizer ID
        db.collection("events").whereEqualTo("organizer_id", hardcodedOrganizerId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        fetchedEvents[0] = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Event event = doc.toObject(Event.class);
                            fetchedEvents[0].add(event);
                        }
                    } else {
                        fail("Error fetching events: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                    latch.countDown();
                });

        // Wait for the async operation to complete
        latch.await();

        // Assert: Verify the fetched events match the organizer's events
        assertNotNull("Fetched events list should not be null", fetchedEvents[0]);
        assertEquals("Fetched events list size should be 2", 2, fetchedEvents[0].size());

        List<String> eventNames = new ArrayList<>();
        for (Event event : fetchedEvents[0]) {
            eventNames.add(event.getEventName());
        }

        assertTrue("Fetched events should contain 'Hardcoded Organizer Event 1'", eventNames.contains("Hardcoded Organizer Event 1"));
        assertTrue("Fetched events should contain 'Hardcoded Organizer Event 2'", eventNames.contains("Hardcoded Organizer Event 2"));
        assertFalse("Fetched events should not contain 'Unrelated Event'", eventNames.contains("Unrelated Event"));
    }


    @Test
    public void testFetchEventById() throws Exception {
        // Setup: Add a mock event with a specific event ID
        Event mockEvent = createMockEvent();
        mockEvent.setEventId(456); // Use the event_id that fetchEventById will query

        // Insert the mock event into the "events" collection (not testCollectionName since fetchEventById uses eventsRef)
        Tasks.await(db.collection("events").document(String.valueOf(mockEvent.getEventId())).set(mockEvent));

        // Wait a short delay to ensure Firestore processes the insertion
        Thread.sleep(200);

        // Test: Fetch the event by ID
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] success = {false};

        databaseHelper.fetchEventById(mockEvent.getEventId(), new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventFetched(Event fetchedEvent) {
                // Assertions to verify the fetched event matches the mock event
                assertNotNull("Fetched event should not be null", fetchedEvent);
                assertEquals("Fetched event ID should match", mockEvent.getEventId(), fetchedEvent.getEventId());
                assertEquals("Fetched event name should match", mockEvent.getEventName(), fetchedEvent.getEventName());
                assertEquals("Fetched organizer ID should match", mockEvent.getOrganizerId(), fetchedEvent.getOrganizerId());
                assertEquals("Fetched registered entrants should match", mockEvent.getRegisteredEntrants(), fetchedEvent.getRegisteredEntrants());
                assertEquals("Fetched waitlisted entrants should match", mockEvent.getWaitlistedEntrants(), fetchedEvent.getWaitlistedEntrants());
                assertEquals("Fetched invited entrants should match", mockEvent.getInvitedEntrants(), fetchedEvent.getInvitedEntrants());
                assertEquals("Fetched declined entrants should match", mockEvent.getDeclinedEntrants(), fetchedEvent.getDeclinedEntrants());
                success[0] = true;
                latch.countDown();
            }

            @Override
            public void onEventsFetched(List<Event> events) {
                fail("Expected onEventFetched to be called, not onEventsFetched.");
            }

            @Override
            public void onError(Exception e) {
                fail("Error fetching event: " + e.getMessage());
                latch.countDown();
            }
        });

        // Wait for the callback to complete
        latch.await();

        // Assert that the test was successful
        assertTrue("The event was not fetched successfully", success[0]);
    }


    @Test
    public void testCreateEvent() throws Exception {
        // Setup: Create a unique event
        Event testEvent = createMockEvent();
        testEvent.setEventName("Unique Test Event");
        testEvent.setOrganizerId("unique_organizer_789");

        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] success = {false};

        // Test: Call createEvent
        databaseHelper.createEvent(testEvent, new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventsFetched(List<Event> events) {
                // Assertions: Verify the event creation
                assertNotNull("Event list should not be null", events);
                assertEquals("Event list should contain one event", 1, events.size());

                Event createdEvent = events.get(0);
                assertNotNull("Created event should not be null", createdEvent);
                assertEquals("Event name should match", testEvent.getEventName(), createdEvent.getEventName());
                assertEquals("Organizer ID should match", testEvent.getOrganizerId(), createdEvent.getOrganizerId());
                assertNotNull("Event ID should not be null", createdEvent.getEventId());
                assertNotNull("QR code hash data should not be null", createdEvent.getQRCode());

                success[0] = true;
                latch.countDown();
            }

            @Override
            public void onEventFetched(Event event) {
                fail("Expected onEventsFetched to be called, not onEventFetched.");
            }

            @Override
            public void onError(Exception e) {
                fail("Error creating event: " + e.getMessage());
                latch.countDown();
            }
        });

        // Wait for the async operation to complete
        latch.await();

        // Validation: Verify the event was added to Firestore
        QuerySnapshot snapshot = Tasks.await(db.collection("events").whereEqualTo("event_name", "Unique Test Event").get());
        assertFalse("Event should exist in Firestore", snapshot.isEmpty());

        Event firestoreEvent = snapshot.getDocuments().get(0).toObject(Event.class);
        assertNotNull("Firestore event should not be null", firestoreEvent);
        assertEquals("Firestore event name should match", "Unique Test Event", firestoreEvent.getEventName());
        assertEquals("Firestore organizer ID should match", "unique_organizer_789", firestoreEvent.getOrganizerId());
        assertNotNull("Firestore QR code hash data should not be null", firestoreEvent.getQRCode());

        // Assert that the test passed
        assertTrue("Event creation test failed", success[0]);
    }


    @Test
    public void testDeleteEvent() throws Exception {
        // Setup: Create and insert a unique event
        Event testEvent = createMockEvent();
        testEvent.setEventId(789); // Unique event ID
        testEvent.setEventName("Delete Test Event");
        testEvent.setOrganizerId("test_organizer_789");

        // Insert the mock event into Firestore
        Tasks.await(db.collection("events").document(String.valueOf(testEvent.getEventId())).set(testEvent));

        // Wait a short delay to ensure Firestore processes the insertion
        Thread.sleep(200);

        // Validation: Ensure the event exists before deletion
        QuerySnapshot preDeleteSnapshot = Tasks.await(db.collection("events").whereEqualTo("event_id", 789).get());
        assertFalse("Event should exist in Firestore before deletion", preDeleteSnapshot.isEmpty());

        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] deletionSuccess = {false};

        // Test: Call deleteEvent
        databaseHelper.deleteEvent(testEvent.getEventId(), new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventsFetched(List<Event> events) {
                // Successful deletion should trigger this callback
                deletionSuccess[0] = true;
                latch.countDown();
            }

            @Override
            public void onEventFetched(Event event) {
                fail("Expected onEventsFetched for deletion success, not onEventFetched.");
            }

            @Override
            public void onError(Exception e) {
                fail("Deletion failed: " + e.getMessage());
                latch.countDown();
            }
        });

        // Wait for the async operation to complete
        latch.await();

        // Validation: Verify the event is deleted from Firestore
        QuerySnapshot postDeleteSnapshot = Tasks.await(db.collection("events").whereEqualTo("event_id", 789).get());
        assertTrue("Event should not exist in Firestore after deletion", postDeleteSnapshot.isEmpty());

        // Assert that the deletion was successful
        assertTrue("Deletion callback was not triggered successfully", deletionSuccess[0]);
    }


    @Test
    public void testUpdateEvent() throws Exception {
        // Setup: Create and insert a unique event
        Event originalEvent = createMockEvent();
        originalEvent.setEventId(456); // Unique event ID
        originalEvent.setEventName("Original Event Name");
        originalEvent.setLocation("Original Location");

        // Insert the mock event into Firestore
        Tasks.await(db.collection("events").document(String.valueOf(originalEvent.getEventId())).set(originalEvent));

        // Wait a short delay to ensure Firestore processes the insertion
        Thread.sleep(200);

        // Validation: Ensure the original event exists before the update
        QuerySnapshot preUpdateSnapshot = Tasks.await(db.collection("events").whereEqualTo("event_id", 456).get());
        assertFalse("Original event should exist in Firestore before the update", preUpdateSnapshot.isEmpty());
        Event fetchedOriginalEvent = preUpdateSnapshot.getDocuments().get(0).toObject(Event.class);
        assertNotNull("Fetched original event should not be null", fetchedOriginalEvent);
        assertEquals("Original event name should match", "Original Event Name", fetchedOriginalEvent.getEventName());
        assertEquals("Original location should match", "Original Location", fetchedOriginalEvent.getLocation());

        // Step 2: Update the event
        Event updatedEvent = createMockEvent(); // Create a new event object
        updatedEvent.setEventId(456); // Use the same event ID
        updatedEvent.setEventName("Updated Event Name");
        updatedEvent.setLocation("Updated Location");

        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] updateSuccess = {false};

        // Test: Call updateEvent
        databaseHelper.updateEvent(updatedEvent, new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventsFetched(List<Event> events) {
                // Successful update should trigger this callback
                updateSuccess[0] = true;
                latch.countDown();
            }

            @Override
            public void onEventFetched(Event event) {
                fail("Expected onEventsFetched for update success, not onEventFetched.");
            }

            @Override
            public void onError(Exception e) {
                fail("Update failed: " + e.getMessage());
                latch.countDown();
            }
        });

        // Wait for the async operation to complete
        latch.await();

        // Assert that the update callback was successful
        assertTrue("Update callback was not triggered successfully", updateSuccess[0]);

        // Validation: Verify the event is updated in Firestore
        QuerySnapshot postUpdateSnapshot = Tasks.await(db.collection("events").whereEqualTo("event_id", 456).get());
        assertFalse("Updated event should exist in Firestore after the update", postUpdateSnapshot.isEmpty());
        Event fetchedUpdatedEvent = postUpdateSnapshot.getDocuments().get(0).toObject(Event.class);
        assertNotNull("Fetched updated event should not be null", fetchedUpdatedEvent);
        assertEquals("Updated event name should match", "Updated Event Name", fetchedUpdatedEvent.getEventName());
        assertEquals("Updated location should match", "Updated Location", fetchedUpdatedEvent.getLocation());
    }

    @Test
    public void testIsQrCodeValid() throws Exception {
        // Setup: Create and insert a mock event with a valid QR code
        Event testEvent = createMockEvent();
        testEvent.setEventId(12345); // Unique event ID
        testEvent.setEventName("QR Code Test Event");

        String validHashedData = "12345:1633036800000"; // 5-digit event ID and a 13-digit timestamp
        testEvent.setQRCode(validHashedData);

        // Insert the event into Firestore
        Tasks.await(db.collection("events").document(String.valueOf(testEvent.getEventId())).set(testEvent));

        // Wait a short delay to ensure Firestore processes the insertion
        Thread.sleep(200);

        // CountDownLatch to manage asynchronous operations
        CountDownLatch latch = new CountDownLatch(4);

        // Test Case 1: Valid QR code
        databaseHelper.isQrCodeValid(validHashedData, isValid -> {
            assertTrue("Valid QR code should return true", isValid);
            latch.countDown();
        });

        // Test Case 2: Invalid QR code (incorrect hashed data)
        String invalidHashedData = "12345:invalidTimestamp";
        databaseHelper.isQrCodeValid(invalidHashedData, isValid -> {
            assertFalse("Invalid QR code with incorrect hashed data should return false", isValid);
            latch.countDown();
        });

        // Test Case 3: Null hashed data
        databaseHelper.isQrCodeValid(null, isValid -> {
            assertFalse("Null hashed data should return false", isValid);
            latch.countDown();
        });

        // Test Case 4: Empty hashed data
        databaseHelper.isQrCodeValid("", isValid -> {
            assertFalse("Empty hashed data should return false", isValid);
            latch.countDown();
        });

        // Wait for all async operations to complete
        latch.await();
    }


    @Test
    public void testFetchRegisteredEntrants() throws Exception {
        // Setup: Create and insert a unique event with registered entrants
        Event testEvent = createMockEvent();
        testEvent.setEventId(56789); // Unique event ID
        testEvent.setEventName("Fetch Entrants Test Event");
        testEvent.setRegisteredEntrants(List.of("entrant1", "entrant2", "entrant3")); // Registered entrants

        // Insert the event into Firestore
        Tasks.await(db.collection("events").document(String.valueOf(testEvent.getEventId())).set(testEvent));

        // Wait a short delay to ensure Firestore processes the insertion
        Thread.sleep(200);

        // CountDownLatch to manage asynchronous operations
        CountDownLatch latch = new CountDownLatch(3);

        // Test Case 1: Valid event with registered entrants
        databaseHelper.fetchRegisteredEntrants(testEvent.getEventId(), new DatabaseHelper.EntrantsCallback() {
            @Override
            public void onEntrantsFetched(List<String> entrants) {
                assertNotNull("Entrants list should not be null", entrants);
                assertEquals("Entrants list size should match", 3, entrants.size());
                assertTrue("Entrants list should contain 'entrant1'", entrants.contains("entrant1"));
                assertTrue("Entrants list should contain 'entrant2'", entrants.contains("entrant2"));
                assertTrue("Entrants list should contain 'entrant3'", entrants.contains("entrant3"));
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                fail("Error fetching entrants: " + e.getMessage());
                latch.countDown();
            }
        });

        // Test Case 2: Event with no registered entrants
        Event noEntrantsEvent = createMockEvent();
        noEntrantsEvent.setEventId(67890); // Unique event ID
        noEntrantsEvent.setEventName("No Entrants Test Event");
        noEntrantsEvent.setRegisteredEntrants(new ArrayList<>()); // No registered entrants

        // Insert the event into Firestore
        Tasks.await(db.collection("events").document(String.valueOf(noEntrantsEvent.getEventId())).set(noEntrantsEvent));

        // Fetch the registered entrants for the event with no entrants
        databaseHelper.fetchRegisteredEntrants(noEntrantsEvent.getEventId(), new DatabaseHelper.EntrantsCallback() {
            @Override
            public void onEntrantsFetched(List<String> entrants) {
                assertNotNull("Entrants list should not be null for event with no entrants", entrants);
                assertEquals("Entrants list size should be 0 for event with no entrants", 0, entrants.size());
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                fail("Error fetching entrants for event with no entrants: " + e.getMessage());
                latch.countDown();
            }
        });

        // Test Case 3: Non-existent event
        databaseHelper.fetchRegisteredEntrants(99999, new DatabaseHelper.EntrantsCallback() { // Non-existent event ID
            @Override
            public void onEntrantsFetched(List<String> entrants) {
                assertNotNull("Entrants list should not be null for non-existent event", entrants);
                assertEquals("Entrants list size should be 0 for non-existent event", 0, entrants.size());
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                fail("Error fetching entrants for non-existent event: " + e.getMessage());
                latch.countDown();
            }
        });

        // Wait for all async operations to complete
        latch.await();
    }


    @Test
    public void testFetchWaitlistedEntrants() throws Exception {
        // Setup: Create and insert a unique event with waitlisted entrants
        Event testEvent = createMockEvent();
        testEvent.setEventId(98765); // Unique event ID
        testEvent.setEventName("Fetch Waitlist Test Event");
        testEvent.setWaitlistedEntrants(List.of("waitlist1", "waitlist2", "waitlist3")); // Waitlisted entrants

        // Insert the event into Firestore
        Tasks.await(db.collection("events").document(String.valueOf(testEvent.getEventId())).set(testEvent));

        // Wait a short delay to ensure Firestore processes the insertion
        Thread.sleep(200);

        // CountDownLatch to manage asynchronous operations
        CountDownLatch latch = new CountDownLatch(3);

        // Test Case 1: Valid event with waitlisted entrants
        databaseHelper.fetchWaitlistedEntrants(testEvent.getEventId(), new DatabaseHelper.EntrantsCallback() {
            @Override
            public void onEntrantsFetched(List<String> entrants) {
                assertNotNull("Entrants list should not be null", entrants);
                assertEquals("Entrants list size should match", 3, entrants.size());
                assertTrue("Entrants list should contain 'waitlist1'", entrants.contains("waitlist1"));
                assertTrue("Entrants list should contain 'waitlist2'", entrants.contains("waitlist2"));
                assertTrue("Entrants list should contain 'waitlist3'", entrants.contains("waitlist3"));
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                fail("Error fetching waitlisted entrants: " + e.getMessage());
                latch.countDown();
            }
        });

        // Test Case 2: Event with no waitlisted entrants
        Event noWaitlistEvent = createMockEvent();
        noWaitlistEvent.setEventId(87654); // Unique event ID
        noWaitlistEvent.setEventName("No Waitlist Test Event");
        noWaitlistEvent.setWaitlistedEntrants(new ArrayList<>()); // No waitlisted entrants

        // Insert the event into Firestore
        Tasks.await(db.collection("events").document(String.valueOf(noWaitlistEvent.getEventId())).set(noWaitlistEvent));

        // Fetch the waitlisted entrants for the event with no entrants
        databaseHelper.fetchWaitlistedEntrants(noWaitlistEvent.getEventId(), new DatabaseHelper.EntrantsCallback() {
            @Override
            public void onEntrantsFetched(List<String> entrants) {
                assertNotNull("Entrants list should not be null for event with no waitlisted entrants", entrants);
                assertEquals("Entrants list size should be 0 for event with no waitlisted entrants", 0, entrants.size());
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                fail("Error fetching waitlisted entrants for event with no entrants: " + e.getMessage());
                latch.countDown();
            }
        });

        // Test Case 3: Non-existent event
        databaseHelper.fetchWaitlistedEntrants(99999, new DatabaseHelper.EntrantsCallback() { // Non-existent event ID
            @Override
            public void onEntrantsFetched(List<String> entrants) {
                assertNotNull("Entrants list should not be null for non-existent event", entrants);
                assertEquals("Entrants list size should be 0 for non-existent event", 0, entrants.size());
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                fail("Error fetching waitlisted entrants for non-existent event: " + e.getMessage());
                latch.countDown();
            }
        });

        // Wait for all async operations to complete
        latch.await();
    }


    @Test
    public void testFetchInvitedEntrants() throws Exception {
        // Setup: Create and insert a unique event with invited entrants
        Event testEvent = createMockEvent();
        testEvent.setEventId(34567); // Unique event ID
        testEvent.setEventName("Fetch Invited Entrants Test Event");
        testEvent.setInvitedEntrants(List.of("invitee1", "invitee2", "invitee3")); // Invited entrants

        // Insert the event into Firestore
        Tasks.await(db.collection("events").document(String.valueOf(testEvent.getEventId())).set(testEvent));

        // Wait a short delay to ensure Firestore processes the insertion
        Thread.sleep(200);

        // CountDownLatch to manage asynchronous operations
        CountDownLatch latch = new CountDownLatch(3);

        // Test Case 1: Valid event with invited entrants
        databaseHelper.fetchInvitedEntrants(testEvent.getEventId(), new DatabaseHelper.EntrantsCallback() {
            @Override
            public void onEntrantsFetched(List<String> entrants) {
                assertNotNull("Entrants list should not be null", entrants);
                assertEquals("Entrants list size should match", 3, entrants.size());
                assertTrue("Entrants list should contain 'invitee1'", entrants.contains("invitee1"));
                assertTrue("Entrants list should contain 'invitee2'", entrants.contains("invitee2"));
                assertTrue("Entrants list should contain 'invitee3'", entrants.contains("invitee3"));
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                fail("Error fetching invited entrants: " + e.getMessage());
                latch.countDown();
            }
        });

        // Test Case 2: Event with no invited entrants
        Event noInviteesEvent = createMockEvent();
        noInviteesEvent.setEventId(54321); // Unique event ID
        noInviteesEvent.setEventName("No Invited Entrants Test Event");
        noInviteesEvent.setInvitedEntrants(new ArrayList<>()); // No invited entrants

        // Insert the event into Firestore
        Tasks.await(db.collection("events").document(String.valueOf(noInviteesEvent.getEventId())).set(noInviteesEvent));

        // Fetch the invited entrants for the event with no invitees
        databaseHelper.fetchInvitedEntrants(noInviteesEvent.getEventId(), new DatabaseHelper.EntrantsCallback() {
            @Override
            public void onEntrantsFetched(List<String> entrants) {
                assertNotNull("Entrants list should not be null for event with no invited entrants", entrants);
                assertEquals("Entrants list size should be 0 for event with no invited entrants", 0, entrants.size());
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                fail("Error fetching invited entrants for event with no invitees: " + e.getMessage());
                latch.countDown();
            }
        });

        // Test Case 3: Non-existent event
        databaseHelper.fetchInvitedEntrants(99999, new DatabaseHelper.EntrantsCallback() { // Non-existent event ID
            @Override
            public void onEntrantsFetched(List<String> entrants) {
                assertNotNull("Entrants list should not be null for non-existent event", entrants);
                assertEquals("Entrants list size should be 0 for non-existent event", 0, entrants.size());
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                fail("Error fetching invited entrants for non-existent event: " + e.getMessage());
                latch.countDown();
            }
        });

        // Wait for all async operations to complete
        latch.await();
    }


    @Test
    public void testFetchDeclinedEntrants() throws Exception {
        // Setup: Create and insert a unique event with declined entrants
        Event testEvent = createMockEvent();
        testEvent.setEventId(45678); // Unique event ID
        testEvent.setEventName("Fetch Declined Entrants Test Event");
        testEvent.setDeclinedEntrants(List.of("declined1", "declined2", "declined3")); // Declined entrants

        // Insert the event into Firestore
        Tasks.await(db.collection("events").document(String.valueOf(testEvent.getEventId())).set(testEvent));

        // Wait a short delay to ensure Firestore processes the insertion
        Thread.sleep(200);

        // CountDownLatch to manage asynchronous operations
        CountDownLatch latch = new CountDownLatch(3);

        // Test Case 1: Valid event with declined entrants
        databaseHelper.fetchDeclinedEntrants(testEvent.getEventId(), new DatabaseHelper.EntrantsCallback() {
            @Override
            public void onEntrantsFetched(List<String> entrants) {
                assertNotNull("Entrants list should not be null", entrants);
                assertEquals("Entrants list size should match", 3, entrants.size());
                assertTrue("Entrants list should contain 'declined1'", entrants.contains("declined1"));
                assertTrue("Entrants list should contain 'declined2'", entrants.contains("declined2"));
                assertTrue("Entrants list should contain 'declined3'", entrants.contains("declined3"));
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                fail("Error fetching declined entrants: " + e.getMessage());
                latch.countDown();
            }
        });

        // Test Case 2: Event with no declined entrants
        Event noDeclinedEvent = createMockEvent();
        noDeclinedEvent.setEventId(65432); // Unique event ID
        noDeclinedEvent.setEventName("No Declined Entrants Test Event");
        noDeclinedEvent.setDeclinedEntrants(new ArrayList<>()); // No declined entrants

        // Insert the event into Firestore
        Tasks.await(db.collection("events").document(String.valueOf(noDeclinedEvent.getEventId())).set(noDeclinedEvent));

        // Fetch the declined entrants for the event with no declined entrants
        databaseHelper.fetchDeclinedEntrants(noDeclinedEvent.getEventId(), new DatabaseHelper.EntrantsCallback() {
            @Override
            public void onEntrantsFetched(List<String> entrants) {
                assertNotNull("Entrants list should not be null for event with no declined entrants", entrants);
                assertEquals("Entrants list size should be 0 for event with no declined entrants", 0, entrants.size());
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                fail("Error fetching declined entrants for event with no declined entrants: " + e.getMessage());
                latch.countDown();
            }
        });

        // Test Case 3: Non-existent event
        databaseHelper.fetchDeclinedEntrants(99999, new DatabaseHelper.EntrantsCallback() { // Non-existent event ID
            @Override
            public void onEntrantsFetched(List<String> entrants) {
                assertNotNull("Entrants list should not be null for non-existent event", entrants);
                assertEquals("Entrants list size should be 0 for non-existent event", 0, entrants.size());
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                fail("Error fetching declined entrants for non-existent event: " + e.getMessage());
                latch.countDown();
            }
        });

        // Wait for all async operations to complete
        latch.await();
    }


    @Test
    public void testUpdateInvitedEntrantsAndSetLotteryRan() throws Exception {
        // Setup: Create a mock event
        Event testEvent = createMockEvent();
        testEvent.setEventId(55678);
        testEvent.setEventName("Lottery Test Event");
        testEvent.setWaitlistedEntrants(new ArrayList<>(List.of("user1", "user2", "user3")));
        testEvent.setInvitedEntrants(new ArrayList<>());
        testEvent.setLotteryRan(false);

        // Insert the mock event into Firestore
        db.collection("events").document(String.valueOf(testEvent.getEventId())).set(testEvent)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // List of users who won the lottery
                        List<String> invitedEntrants = List.of("user1", "user2");

                        // Test the method
                        databaseHelper.updateInvitedEntrantsAndSetLotteryRan(testEvent.getEventId(), invitedEntrants, new DatabaseHelper.EventsCallback() {
                            @Override
                            public void onEventsFetched(List<Event> events) {
                                db.collection("events").document(String.valueOf(testEvent.getEventId())).get()
                                        .addOnCompleteListener(fetchTask -> {
                                            if (fetchTask.isSuccessful()) {
                                                Event updatedEvent = fetchTask.getResult().toObject(Event.class);

                                                assertNotNull("Updated event should not be null", updatedEvent);
                                                assertTrue("Lottery should be marked as run", updatedEvent.isLotteryRan());
                                                assertEquals("Invited entrants should match", invitedEntrants, updatedEvent.getInvitedEntrants());
                                                assertEquals("Remaining waitlisted entrants should be correct", List.of("user3"), updatedEvent.getWaitlistedEntrants());
                                            } else {
                                                fail("Error fetching updated event: " + fetchTask.getException().getMessage());
                                            }
                                        });
                            }

                            @Override
                            public void onEventFetched(Event event) {
                                fail("This test does not fetch a single event.");
                            }

                            @Override
                            public void onError(Exception e) {
                                fail("Unexpected error: " + e.getMessage());
                            }
                        });
                    } else {
                        fail("Error setting up initial event: " + task.getException().getMessage());
                    }
                });
    }



}



