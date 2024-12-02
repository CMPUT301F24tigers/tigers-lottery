package com.example.tigers_lottery.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.tigers_lottery.models.Event;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.time.Instant;

/**
 * Utility class to generate a qrCode for an event, an event's qrCode is generated uniquely
 * using the event's id and their timestamp as the hash data. This ensures that every qrCode remains
 * unique, and new qrCodes for the same event also maintain uniqueness.
 */

public class QRCodeGenerator {
    private String currentHashData;
    private Event event;

    /**
     * Constructor that initializes the QRCodeGenerator with an event.
     *
     * @param event The Event object used to generate QR codes.
     */
    public QRCodeGenerator(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null.");
        }
        this.event = event;
        this.currentHashData = event.getQRCode();
    }

    /**
     * Generates and sets hash data using the event ID and current timestamp.
     *
     */
    public void setHashData() {
        long timestamp = System.currentTimeMillis(); // Current timestamp in milliseconds
        currentHashData = event.getEventId() + ":" + timestamp;
    }

    /**
     * Retrieves the current hash data.
     *
     * @return The current hash data as a String.
     */
    public String getHashData() {
        if (currentHashData == null || currentHashData.isEmpty()) {
            throw new IllegalStateException("Hash data is not set. Call setHashData() first.");
        }
        return currentHashData;
    }

    /**
     * Decodes the hash data to retrieve the event ID.
     *
     * @param hashData The hash data to decode.
     * @return The original event ID.
     */
    public int decodeHash(String hashData) {
        if (hashData == null || hashData.isEmpty()) {
            throw new IllegalArgumentException("Hash data is empty or null.");
        }

        String[] parts = hashData.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid hash format. Expected 'eventId:timestamp'.");
        }

        return Integer.parseInt(parts[0]); // Return the event ID
    }

    /**
     * Generates a QR code as a Bitmap from the current hash data.
     *
     * @return A Bitmap containing the QR code.
     */
    public Bitmap generateQRCode() {
        if (currentHashData == null || currentHashData.isEmpty()) {
            throw new IllegalStateException("Hash data is not set. Call setHashData() first.");
        }

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(currentHashData, BarcodeFormat.QR_CODE, 300, 300);
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating QR code", e);
        }
    }

    /**
     * Generates a QR code as a Bitmap based on custom input data.
     *
     * @return A Bitmap containing the QR code.
     */
    public Bitmap generateQRCodeFromHashData() {
        if (currentHashData == null || currentHashData.isEmpty()) {
            throw new IllegalArgumentException("Hashed data cannot be null or empty.");
        }

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(currentHashData, BarcodeFormat.QR_CODE, 300, 300);
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating QR code from input", e);
        }
    }
}