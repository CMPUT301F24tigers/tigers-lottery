package com.example.tigers_lottery.Notifications;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;

public class SendNotificationDialog {

    private final Context context;
    private final int eventId;

    public SendNotificationDialog(Context context, int eventId) {
        this.context = context;
        this.eventId = eventId;
    }

    public void showDialog() {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_send_notification, null);

        // Initialize UI components
        EditText messageInput = dialogView.findViewById(R.id.notificationMessageInput);
        RadioGroup priorityGroup = dialogView.findViewById(R.id.priorityRadioGroup);
        Button cancelButton = dialogView.findViewById(R.id.btnCancel);
        Button sendButton = dialogView.findViewById(R.id.btnSend);

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Handle Cancel Button
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Handle Send Button
        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            String priority;

            // Get selected priority
            int selectedPriorityId = priorityGroup.getCheckedRadioButtonId();
            if (selectedPriorityId == R.id.priorityHigh) {
                priority = "High";
            } else if (selectedPriorityId == R.id.priorityMedium) {
                priority = "Medium";
            } else if (selectedPriorityId == R.id.priorityLow) {
                priority = "Low";
            } else {
                Toast.makeText(context, "Please select a priority", Toast.LENGTH_SHORT).show();
                return;
            }

            if (message.isEmpty()) {
                Toast.makeText(context, "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call backend method to send notifications
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            dbHelper.sendNotificationsToRegisteredEntrants(eventId, message, priority, new DatabaseHelper.NotificationCallback() {
                @Override
                public void onSuccess(String responseMessage) {
                    Toast.makeText(context, "Notification sent successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(context, "Failed to send notification: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Show the dialog
        dialog.show();
    }
}
