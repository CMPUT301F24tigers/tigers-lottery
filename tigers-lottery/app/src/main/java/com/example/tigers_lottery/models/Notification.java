package com.example.tigers_lottery.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.Map;

/**
 * Represents a Notification entity in the Tigers Lottery application.
 * This class serves as a Data Transfer Object (DTO) for notification data stored in Firestore.
 * It includes details about the notification's recipient, type, message, and additional metadata.
 *
 * Fields:
 * - notificationId: Unique identifier for the notification.
 * - userId: The recipient's user ID.
 * - eventId: Associated event ID, if applicable.
 * - senderId: ID of the sender (organizer/admin), if applicable.
 * - type: The type of notification (e.g., lottery_win, reminder).
 * - category: High-level grouping for notifications (events, system, reminders).
 * - priority: Priority level of the notification (high, medium, low).
 * - message: The content of the notification.
 * - timestamp: The time when the notification was created.
 * - readStatus: Whether the notification has been read.
 * - scheduledTime: When to deliver the notification (useful for reminders).
 * - locationMetadata: Optional geolocation data tied to the notification.
 * - metadata: Additional context as a map (e.g., event details, sender info).
 * - actionUrl: A deep link to app screens or content related to the notification.
 */
public class Notification implements Serializable {

    @PropertyName("notification_id")
    private int notificationId;

    @PropertyName("user_id")
    private String userId;

    @PropertyName("event_id")
    private int eventId;

    @PropertyName("sender_id")
    private String senderId;

    @PropertyName("type")
    private String type;

    @PropertyName("category")
    private String category;

    @PropertyName("priority")
    private String priority;

    @PropertyName("message")
    private String message;

    @PropertyName("timestamp")
    private Timestamp timestamp;

    @PropertyName("read_status")
    private boolean readStatus;

    @PropertyName("scheduled_time")
    private Timestamp scheduledTime;

    @PropertyName("location_metadata")
    private GeoPoint locationMetadata;

    @PropertyName("metadata")
    private Map<String, Object> metadata;

    @PropertyName("action_url")
    private String actionUrl;

    // No-argument constructor for Firestore
    public Notification() {}

    // Getters and Setters
    @PropertyName("notification_id")
    public int getNotificationId() {
        return notificationId;
    }

    @PropertyName("notification_id")
    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    @PropertyName("user_id")
    public String getUserId() {
        return userId;
    }

    @PropertyName("user_id")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @PropertyName("event_id")
    public int getEventId() {
        return eventId;
    }

    @PropertyName("event_id")
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    @PropertyName("sender_id")
    public String getSenderId() {
        return senderId;
    }

    @PropertyName("sender_id")
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    @PropertyName("type")
    public String getType() {
        return type;
    }

    @PropertyName("type")
    public void setType(String type) {
        this.type = type;
    }

    @PropertyName("category")
    public String getCategory() {
        return category;
    }

    @PropertyName("category")
    public void setCategory(String category) {
        this.category = category;
    }

    @PropertyName("priority")
    public String getPriority() {
        return priority;
    }

    @PropertyName("priority")
    public void setPriority(String priority) {
        this.priority = priority;
    }

    @PropertyName("message")
    public String getMessage() {
        return message;
    }

    @PropertyName("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @PropertyName("timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @PropertyName("timestamp")
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @PropertyName("read_status")
    public boolean isReadStatus() {
        return readStatus;
    }

    @PropertyName("read_status")
    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    @PropertyName("scheduled_time")
    public Timestamp getScheduledTime() {
        return scheduledTime;
    }

    @PropertyName("scheduled_time")
    public void setScheduledTime(Timestamp scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    @PropertyName("location_metadata")
    public GeoPoint getLocationMetadata() {
        return locationMetadata;
    }

    @PropertyName("location_metadata")
    public void setLocationMetadata(GeoPoint locationMetadata) {
        this.locationMetadata = locationMetadata;
    }

    @PropertyName("metadata")
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @PropertyName("metadata")
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @PropertyName("action_url")
    public String getActionUrl() {
        return actionUrl;
    }

    @PropertyName("action_url")
    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
}
