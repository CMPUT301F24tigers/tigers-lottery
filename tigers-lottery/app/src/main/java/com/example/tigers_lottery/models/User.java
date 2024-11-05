package com.example.tigers_lottery.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a User entity in the Tigers Lottery application.
 * This class serves as a Data Transfer Object (DTO) for user data stored in Firestore.
 * It contains various properties that represent user-specific information, such as personal
 * details, contact information, location, facility information, notification settings, and
 * lists of hosted and joined events.
 * Each property in this class is annotated with @PropertyName to map it to the corresponding
 * Firestore field, allowing for seamless data retrieval and storage with Firestore's automatic
 * mapping feature. Default values are set to ensure compatibility with Firestore's requirements
 * for no-argument constructors and non-nullable fields.
 * The following fields are represented:
 * <ul>
 *   <li>userId: A unique identifier for the user, defaulting to "NoUserId" when not set.</li>
 *   <li>firstName, lastName: The user's personal name details.</li>
 *   <li>emailAddress, phoneNumber: The user's contact information.</li>
 *   <li>userGeolocation: The user's location represented as a GeoPoint.</li>
 *   <li>userPhoto: A URL or link to the user's profile photo.</li>
 *   <li>dateOfBirth: The user's date of birth as a Timestamp.</li>
 *   <li>facilityLocation, facilityName, facilityEmail, facilityPhone, facilityPhoto: Information
 *       related to the user's associated facility, if applicable.</li>
 *   <li>notificationFlag: A boolean flag indicating whether notifications are enabled for the user.</li>
 *   <li>hostedEvents, joinedEvents: Lists of event IDs representing events hosted and joined by the user.</li>
 * </ul>
 *
 * This class includes a no-argument constructor for Firestore's automatic mapping.
 */
public class User {

    @PropertyName("user_id")
    private String userId;

    @PropertyName("first_name")
    private String firstName = "Unknown User";

    @PropertyName("last_name")
    private String lastName = "";

    @PropertyName("email_address")
    private String emailAddress = "";

    @PropertyName("phone_number")
    private String phoneNumber = "";

    @PropertyName("user_geolocation")
    private GeoPoint userGeolocation = new GeoPoint(0.0, 0.0);

    @PropertyName("user_photo")
    private String userPhoto = "NoProfilePhoto";

    @PropertyName("DOB")
    private Timestamp dateOfBirth = Timestamp.now();

    @PropertyName("facility_location")
    private String facilityLocation = "";

    @PropertyName("facility_name")
    private String facilityName = "";

    @PropertyName("facility_email")
    private String facilityEmail = "";

    @PropertyName("facility_phone")
    private String facilityPhone = "";

    @PropertyName("facility_photo")
    private String facilityPhoto = "NoFacilityPhoto";

    @PropertyName("notification_flag")
    private boolean notificationFlag = true;

    @PropertyName("hosted_events")
    private List<Integer> hostedEvents = new ArrayList<>();

    @PropertyName("joined_events")
    private List<Integer> joinedEvents = new ArrayList<>();

    // No-argument constructor for Firestore
    public User() {}

    // Getters and Setters
    @PropertyName("user_id")
    public String getUserId() { return userId; }

    @PropertyName("user_id")
    public void setUserId(Object userId) {
        if (userId instanceof String) {
            this.userId = (String) userId;
        } else if (userId instanceof Long) {
            this.userId = String.valueOf(userId); // Convert Long to String
        } else {
            this.userId = "NoUserId"; // Default value if neither String nor Long
        }
    }

    @PropertyName("first_name")
    public String getFirstName() { return firstName; }

    @PropertyName("first_name")
    public void setFirstName(String firstName) { this.firstName = firstName; }

    @PropertyName("last_name")
    public String getLastName() { return lastName; }

    @PropertyName("last_name")
    public void setLastName(String lastName) { this.lastName = lastName; }

    @PropertyName("email_address")
    public String getEmailAddress() { return emailAddress; }

    @PropertyName("email_address")
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    @PropertyName("phone_number")
    public String getPhoneNumber() { return phoneNumber; }

    @PropertyName("phone_number")
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    @PropertyName("user_geolocation")
    public GeoPoint getUserGeolocation() { return userGeolocation; }

    @PropertyName("user_geolocation")
    public void setUserGeolocation(GeoPoint userGeolocation) { this.userGeolocation = userGeolocation; }

    @PropertyName("user_photo")
    public String getUserPhoto() { return userPhoto; }

    @PropertyName("user_photo")
    public void setUserPhoto(String userPhoto) { this.userPhoto = userPhoto; }

    @PropertyName("DOB")
    public Timestamp getDateOfBirth() { return dateOfBirth; }

    @PropertyName("DOB")
    public void setDateOfBirth(Timestamp dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    @PropertyName("facility-location")
    public String getFacilityLocation() { return facilityLocation; }

    @PropertyName("facility-location")
    public void setFacilityLocation(String facilityLocation) { this.facilityLocation = facilityLocation; }

    @PropertyName("facility_name")
    public String getFacilityName() { return facilityName; }

    @PropertyName("facility_name")
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    @PropertyName("facility_email")
    public String getFacilityEmail() { return facilityEmail; }

    @PropertyName("facility_email")
    public void setFacilityEmail(String facilityEmail) { this.facilityEmail = facilityEmail; }

    @PropertyName("facility_phone")
    public String getFacilityPhone() { return facilityPhone; }

    @PropertyName("facility_phone")
    public void setFacilityPhone(String facilityPhone) { this.facilityPhone = facilityPhone; }

    @PropertyName("facility_photo")
    public String getFacilityPhoto() { return facilityPhoto; }

    @PropertyName("facility_photo")
    public void setFacilityPhoto(String facilityPhoto) { this.facilityPhoto = facilityPhoto; }

    @PropertyName("notification_flag")
    public boolean isNotificationFlag() { return notificationFlag; }

    @PropertyName("notification_flag")
    public void setNotificationFlag(boolean notificationFlag) { this.notificationFlag = notificationFlag; }

    @PropertyName("hosted_events")
    public List<Integer> getHostedEvents() { return hostedEvents; }

    @PropertyName("hosted_events")
    public void setHostedEvents(List<Integer> hostedEvents) { this.hostedEvents = hostedEvents; }

    @PropertyName("joined_events")
    public List<Integer> getJoinedEvents() { return joinedEvents; }

    @PropertyName("joined_events")
    public void setJoinedEvents(List<Integer> joinedEvents) { this.joinedEvents = joinedEvents; }
}