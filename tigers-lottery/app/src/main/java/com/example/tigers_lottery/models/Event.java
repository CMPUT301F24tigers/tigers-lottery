package com.example.tigers_lottery.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Event {
    private int eventId;
    private String eventName;
    private GeoPoint geolocation; // Using Firestore's GeoPoint to handle lat and long for now
    private int organizerId;
    private String posterUrl;
    private Timestamp eventDate;
    private Timestamp waitlistOpenDate;
    private Timestamp waitlistDeadline;
    private int waitlistLimit;

    // Constructor
    public Event(int eventId, String eventName, GeoPoint geolocation, int organizerId, String posterUrl,
                 Timestamp eventDate, Timestamp waitlistOpenDate, Timestamp waitlistDeadline, int waitlistLimit) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.geolocation = geolocation;
        this.organizerId = organizerId;
        this.posterUrl = posterUrl;
        this.eventDate = eventDate;
        this.waitlistOpenDate = waitlistOpenDate;
        this.waitlistDeadline = waitlistDeadline;
        this.waitlistLimit = waitlistLimit;
    }

    // Getters
    public int getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public GeoPoint getGeolocation() { return geolocation; }
    public int getOrganizerId() { return organizerId; }
    public String getPosterUrl() { return posterUrl; }
    public Timestamp getEventDate() { return eventDate; }
    public Timestamp getWaitlistOpenDate() { return waitlistOpenDate; }
    public Timestamp getWaitlistDeadline() { return waitlistDeadline; }
    public int getWaitlistLimit() { return waitlistLimit; }
}
