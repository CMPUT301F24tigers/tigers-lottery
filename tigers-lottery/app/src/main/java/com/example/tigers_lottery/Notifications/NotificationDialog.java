package com.example.tigers_lottery.Notifications;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Notification;

import java.util.Objects;


/**
 * Dialog shown on clicking a notification to get an enlarged view of the notification.
 */
public class NotificationDialog {

    private final Context context;
    private final Notification notification;
    private final OnNotificationReadCallback callback;


    /**
     * Notification dialog constructor.
     *
     * @param context current context.
     * @param notification to be shown.
     * @param callback callback.
     */
    public NotificationDialog(Context context, Notification notification, OnNotificationReadCallback callback) {
        this.context = context;
        this.notification = notification;
        this.callback = callback;
    }

    /**
     * Shows the dialog for the notification and populates it with the notifications details.
     */
    public void showDialog() {
        // Inflate the custom dialog layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.notification_dialog, null);

        // Find and populate views
        TextView titleView = dialogView.findViewById(R.id.notificationDialogTitle);
        TextView messageView = dialogView.findViewById(R.id.notificationDialogMessage);
        TextView timestampView = dialogView.findViewById(R.id.notificationDialogTimestamp);
        TextView categoryView = dialogView.findViewById(R.id.notificationDialogCategory);
        TextView priorityView = dialogView.findViewById(R.id.notificationDialogPriority);
        TextView eventNameView = dialogView.findViewById(R.id.notificationDialogEventName);

        // Set notification data
        titleView.setText(notification.getType());
        messageView.setText(notification.getMessage());
        timestampView.setText("Sent: " + notification.getTimestamp().toDate().toString());
        categoryView.setText("Category: " + notification.getCategory());

        // Set priority with color coding
        String priority = notification.getPriority();
        priorityView.setText(priority + " Priority");
        switch (priority.toLowerCase()) {
            case "high":
                priorityView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
                break;
            case "medium":
                priorityView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_light));
                priorityView.setTextColor(context.getResources().getColor(android.R.color.black));
                break;
            case "low":
                priorityView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                break;
        }

        // Get event name from metadata
        String eventName = (String) notification.getMetadata().get("event_name");
        eventNameView.setText("Event Name: " + (eventName != null ? eventName : "N/A"));

        if (Objects.equals(eventName, "admin")) {
            eventNameView.setText("");
        }

        // Build and show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        // Mark notification as read
        callback.onNotificationRead(notification);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Callback interface for marking notification as read.
     */
    public interface OnNotificationReadCallback {
        void onNotificationRead(Notification notification);
    }
}
