package com.example.tigers_lottery.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.PropertyName;
import com.google.zxing.qrcode.encoder.QRCode;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Class representing an event in the Tigers Lottery application.
 * This class holds details about an event, such as its name, organizer, location, date,
 * and lists of entrants in various states (registered, waitlisted, invited, declined).
 * It also includes various flags for managing the event's registration process and its waitlist.
 * The properties in this class are annotated with @PropertyName for seamless mapping to Firestore fields.
 *
 * <ul>
 *   <li>eventId: A unique identifier for the event defaulting to -1.</li>
 *   <li>eventName: The name of the event defaulting to "Unnamed Event".</li>
 *   <li>geolocation: The geographical location of the event, represented as a GeoPoint defaulting to(0.0, 0.0).</li>
 *   <li>organizerId: The unique identifier of the event's organizer defaulting to "".</li>
 *   <li>posterUrl: The URL for the event's poster image defaulting to "https://example.com/default-poster.png".</li>
 *   <li>eventDate: The date and time of the event defaulting to current timestamp.</li>
 *   <li>waitlistOpenDate: The date when the waitlist opens for the event defaulting to current timestamp.</li>
 *   <li>waitlistDeadline: The deadline for the waitlist defaulting to current timestamp.</li>
 *   <li>waitlistLimit: The maximum number of people allowed on the waitlist defaulting to 100.</li>
 *   <li>waitlistLimitFlag: A flag indicating whether a limit is applied to the waitlist defaulting to false.</li>
 *   <li>location: The location of the event defaulting to "Location not specified".</li>
 *   <li>description: Description of the event defaulting to"No description available".</li>
 *   <li>registeredEntrants: List of IDs representing entrants who are registered for the event.</li>
 *   <li>waitlistedEntrants: List of IDs representing entrants who are waitlisted for the event.</li>
 *   <li>invitedEntrants: List of IDs representing entrants who have been invited to the event.</li>
 *   <li>declinedEntrants: List of IDs representing entrants who have declined the event invitation.</li>
 *   <li>occupantLimit: The maximum number of people allowed to attend the event defaulting to 0.</li>
 *   <li>isLotteryRan: A flag indicating whether the lottery for the event has been run defaulting to false.</li>
 * </ul>
 */

public class Event implements Serializable {
    @PropertyName("event_id")
    private int eventId = -1;

    @PropertyName("event_name")
    private String eventName = "Unnamed Event";

    @PropertyName("geolocation")
    private GeoPoint geolocation = new GeoPoint(0.0, 0.0);

    @PropertyName("organizer_id")
    private String organizerId = "";

    @PropertyName("poster_url")
    private String posterUrl = "https://example.com/default-poster.png";

    @PropertyName("event_date")
    private Timestamp eventDate = Timestamp.now();

    @PropertyName("waitlist_open_date")
    private Timestamp waitlistOpenDate = Timestamp.now();

    @PropertyName("waitlist_deadline")
    private Timestamp waitlistDeadline = Timestamp.now();

    @PropertyName("waitlist_limit")
    private int waitlistLimit = 100;

    @PropertyName("waitlist_limit_flag")
    private boolean waitlistLimitFlag = false;

    @PropertyName("location")
    private String location = "Location not specified";

    @PropertyName("description")
    private String description = "No description available";

    @PropertyName("registered_entrants")
    private List<String> registeredEntrants = new ArrayList<>();

    @PropertyName("waitlisted_entrants")
    private List<String> waitlistedEntrants = new ArrayList<>();

    @PropertyName("invited_entrants")
    private List<String> invitedEntrants = new ArrayList<>();

    @PropertyName("declined_entrants")
    private List<String> declinedEntrants = new ArrayList<>();

    @PropertyName("occupant_limit")
    private int occupantLimit = 0;

    @PropertyName("is_lottery_ran")
    private boolean isLotteryRan = false;

    @PropertyName("QR_hash_data")
    private String QRCode;

    @PropertyName("geolocation_required")
    private boolean geolocationRequired = false; // Default to false


    // No-argument constructor
    public Event() {}

    // Getters and setters
    @PropertyName("event_id")
    public int getEventId() { return eventId; }

    @PropertyName("event_id")
    public void setEventId(int eventId) { this.eventId = eventId; }

    @PropertyName("event_name")
    public String getEventName() { return eventName; }

    @PropertyName("event_name")
    public void setEventName(String eventName) { this.eventName = eventName; }

    @PropertyName("geolocation")
    public GeoPoint getGeolocation() { return geolocation; }

    @PropertyName("geolocation")
    public void setGeolocation(GeoPoint geolocation) { this.geolocation = geolocation; }

    @PropertyName("organizer_id")
    public String getOrganizerId() { return organizerId; }

    @PropertyName("organizer_id")
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    @PropertyName("poster_url")
    public String getPosterUrl() { return posterUrl; }

    @PropertyName("poster_url")
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    @PropertyName("event_date")
    public Timestamp getEventDate() { return eventDate; }

    @PropertyName("event_date")
    public void setEventDate(Timestamp eventDate) { this.eventDate = eventDate; }

    @PropertyName("waitlist_open_date")
    public Timestamp getWaitlistOpenDate() { return waitlistOpenDate; }

    @PropertyName("waitlist_open_date")
    public void setWaitlistOpenDate(Timestamp waitlistOpenDate) { this.waitlistOpenDate = waitlistOpenDate; }

    @PropertyName("waitlist_deadline")
    public Timestamp getWaitlistDeadline() { return waitlistDeadline; }

    @PropertyName("waitlist_deadline")
    public void setWaitlistDeadline(Timestamp waitlistDeadline) { this.waitlistDeadline = waitlistDeadline; }

    @PropertyName("waitlist_limit")
    public int getWaitlistLimit() { return waitlistLimit; }

    @PropertyName("waitlist_limit")
    public void setWaitlistLimit(int waitlistLimit) { this.waitlistLimit = waitlistLimit; }

    @PropertyName("waitlist_limit_flag")
    public boolean isWaitlistLimitFlag() { return waitlistLimitFlag; }

    @PropertyName("waitlist_limit_flag")
    public void setWaitlistLimitFlag(boolean waitlistLimitFlag) { this.waitlistLimitFlag = waitlistLimitFlag; }

    @PropertyName("location")
    public String getLocation() { return location; }

    @PropertyName("location")
    public void setLocation(String location) { this.location = location; }

    @PropertyName("QR_hash_data")
    public void setQRCode(String QRCode) {this.QRCode = QRCode;}

    @PropertyName("QR_hash_data")
    public String getQRCode() {return QRCode; }

    @PropertyName("description")
    public String getDescription() { return description; }

    @PropertyName("description")
    public void setDescription(String description) { this.description = description; }

    @PropertyName("registered_entrants")
    public List<String> getRegisteredEntrants() { return registeredEntrants; }

    @PropertyName("registered_entrants")
    public void setRegisteredEntrants(List<String> registeredEntrants) { this.registeredEntrants = registeredEntrants; }

    @PropertyName("waitlisted_entrants")
    public List<String> getWaitlistedEntrants() { return waitlistedEntrants; }

    @PropertyName("waitlisted_entrants")
    public void setWaitlistedEntrants(List<String> waitlistedEntrants) { this.waitlistedEntrants = waitlistedEntrants; }

    @PropertyName("invited_entrants")
    public List<String> getInvitedEntrants() { return invitedEntrants; }

    @PropertyName("invited_entrants")
    public void setInvitedEntrants(List<String> invitedEntrants) { this.invitedEntrants = invitedEntrants; }

    @PropertyName("declined_entrants")
    public List<String> getDeclinedEntrants() { return declinedEntrants; }

    @PropertyName("declined_entrants")
    public void setDeclinedEntrants(List<String> declinedEntrants) { this.declinedEntrants = declinedEntrants; }

    @PropertyName("occupant_limit")
    public int getOccupantLimit() { return occupantLimit; }

    @PropertyName("occupant_limit")
    public void setOccupantLimit(int occupantLimit) { this.occupantLimit = occupantLimit; }

    @PropertyName("is_lottery_ran")
    public boolean isLotteryRan() { return isLotteryRan; }

    @PropertyName("is_lottery_ran")
    public void setLotteryRan(boolean lotteryRan) { isLotteryRan = lotteryRan; }

    @PropertyName("geolocation_required")
    public boolean isGeolocationRequired() { return geolocationRequired; }

    @PropertyName("geolocation_required")
    public void setGeolocationRequired(boolean geolocationRequired) { this.geolocationRequired = geolocationRequired; }


    // Helper methods to format Timestamps
    public String getFormattedEventDate() {
        return formatTimestamp(eventDate);
    }

    public String getFormattedWaitlistOpenDate() {
        return formatTimestamp(waitlistOpenDate);
    }

    public String getFormattedWaitlistDeadline() {
        return formatTimestamp(waitlistDeadline);
    }

    // Utility method to format Timestamp
    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault());
            return dateFormat.format(timestamp.toDate());
        }
        return "Date not available";
    }
}
