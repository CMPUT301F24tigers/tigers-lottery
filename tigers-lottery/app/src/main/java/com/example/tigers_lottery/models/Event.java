package com.example.tigers_lottery.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.PropertyName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Event {
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
    private List<Integer> registeredEntrants = new ArrayList<>();

    @PropertyName("waitlisted_entrants")
    private List<Integer> waitlistedEntrants = new ArrayList<>();

    @PropertyName("invited_entrants")
    private List<Integer> invitedEntrants = new ArrayList<>();

    @PropertyName("declined_entrants")
    private List<Integer> declinedEntrants = new ArrayList<>();

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

    @PropertyName("description")
    public String getDescription() { return description; }

    @PropertyName("description")
    public void setDescription(String description) { this.description = description; }

    @PropertyName("registered_entrants")
    public List<Integer> getRegisteredEntrants() { return registeredEntrants; }

    @PropertyName("registered_entrants")
    public void setRegisteredEntrants(List<Integer> registeredEntrants) { this.registeredEntrants = registeredEntrants; }

    @PropertyName("waitlisted_entrants")
    public List<Integer> getWaitlistedEntrants() { return waitlistedEntrants; }

    @PropertyName("waitlisted_entrants")
    public void setWaitlistedEntrants(List<Integer> waitlistedEntrants) { this.waitlistedEntrants = waitlistedEntrants; }

    @PropertyName("invited_entrants")
    public List<Integer> getInvitedEntrants() { return invitedEntrants; }

    @PropertyName("invited_entrants")
    public void setInvitedEntrants(List<Integer> invitedEntrants) { this.invitedEntrants = invitedEntrants; }

    @PropertyName("declined_entrants")
    public List<Integer> getDeclinedEntrants() { return declinedEntrants; }

    @PropertyName("declined_entrants")
    public void setDeclinedEntrants(List<Integer> declinedEntrants) { this.declinedEntrants = declinedEntrants; }

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
