package com.example.tigers_lottery.utils;

import android.content.Context;
import android.util.Patterns;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class Validator {

    /**
     * Validates the user's profile input fields: first name, last name, email, date of birth, and phone number.
     *
     * @param firstName The user's first name.
     * @param lastName  The user's last name.
     * @param email     The user's email address.
     * @param date      The user's date of birth.
     * @param phone     The user's phone number (optional).
     * @param context   The Android context for showing toasts.
     * @return true if all inputs are valid, false otherwise.
     */
    public boolean validatingUserProfileInput(String firstName, String lastName, String email, Date date, String phone, Context context) {
        if (!isValidName(firstName, "First name", context)) return false;
        if (!isValidName(lastName, "Last name", context)) return false;
        if (!isValidEmail(email, context)) return false;
        if (!isValidDateOfBirth(date, context)) return false;
        if (!isValidPhoneNumber(phone, context)) return false;

        return true;
    }

    /**
     * Validates the Facility's profile input fields: facility name, email, location, phone number, and optional phone.
     *
     * @param facilityName The facility name.
     * @param email        The facility's email address.
     * @param location     The location of the facility.
     * @param phone        The facility's phone number (optional).
     * @param context      The Android context for showing toasts.
     * @return true if all inputs are valid, false otherwise.
     */
    public boolean validatingFacilityProfileInput(String facilityName, String email, String location, String phone, Context context) {
        if (!isValidName(facilityName, "Facility name", context)) return false;
        if (!isValidEmail(email, context)) return false;
        if (!isValidName(location, "Facility location", context)) return false;
        if (!isValidPhoneNumber(phone, context)) return false;

        return true;
    }

    /**
     * Validates a name input field.
     *
     * @param name    The name to validate.
     * @param field   The name of the field (e.g., "First name", "Last name").
     * @param context The Android context for showing toasts.
     * @return true if the name is valid, false otherwise.
     */
    private boolean isValidName(String name, String field, Context context) {
        if (name == null || name.trim().isEmpty()) {
            Toast.makeText(context, field + " cannot be null or empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Validates an email address.
     *
     * @param email   The email to validate.
     * @param context The Android context for showing toasts.
     * @return true if the email is valid, false otherwise.
     */
    private boolean isValidEmail(String email, Context context) {
        if (email == null || email.trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Validates a date of birth to ensure the user is at least 14 years old.
     *
     * @param date    The date of birth to validate.
     * @param context The Android context for showing toasts.
     * @return true if the date of birth is valid, false otherwise.
     */
    private boolean isValidDateOfBirth(Date date, Context context) {
        if (date == null) {
            Toast.makeText(context, "Date of birth cannot be null.", Toast.LENGTH_SHORT).show();
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -14);
        Date minDob = calendar.getTime();

        if (!date.before(minDob)) {
            Toast.makeText(context, "You must be at least 14 years old.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Validates a phone number.
     * A phone number is optional, but if provided, it must have exactly 10 digits.
     *
     * @param phone   The phone number to validate.
     * @param context The Android context for showing toasts.
     * @return true if the phone number is valid or empty, false otherwise.
     */
    private boolean isValidPhoneNumber(String phone, Context context) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Optional field; valid if not provided
        }

        // Check if the phone number has exactly 10 digits
        if (!phone.matches("\\d{10}")) {
            Toast.makeText(context, "Phone number must have exactly 10 digits.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Validates if a string is null or empty.
     *
     * @param input The string to validate.
     * @return true if the string is not null or empty, false otherwise.
     */
    public boolean isNotNullOrEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    /**
     * Validates if a date is not null and not in the future.
     *
     * @param date The date to validate.
     * @return true if the date is valid, false otherwise.
     */
    public boolean isValidDate(Date date) {
        if (date == null) return false;
        return !date.after(new Date());
    }
}