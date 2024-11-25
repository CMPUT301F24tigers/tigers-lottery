package com.example.tigers_lottery.Notifications.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Notification;

import java.util.List;

/**
 * Adapter for displaying a list of notifications in the notifications dashboard.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<Notification> notifications;
    private final Context context;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        // Set the notification type (title) and message
        holder.notificationType.setText(notification.getType());
        holder.notificationMessage.setText(notification.getMessage());

        // Set the priority text
        String priority = notification.getPriority();
        holder.notificationPriority.setText(priority + " Priority");

        // Dynamically change the background color for priority
        int backgroundColor;
        switch (priority.toLowerCase()) {
            case "high":
                backgroundColor = Color.RED;
                break;
            case "medium":
                backgroundColor = Color.YELLOW;
                holder.notificationPriority.setTextColor(Color.BLACK); // Adjust text color for visibility
                break;
            case "low":
                backgroundColor = Color.GREEN;
                break;
            default:
                backgroundColor = Color.GRAY; // Default color
        }

        // Apply priority background color dynamically
        GradientDrawable priorityBackground = (GradientDrawable) holder.notificationPriority.getBackground();
        priorityBackground.setColor(backgroundColor);

        // Handle unread notifications (add blue border)
        if (!notification.isReadStatus()) { // If unread
            GradientDrawable unreadBorder = new GradientDrawable();
            unreadBorder.setStroke(8, Color.BLUE); // Blue border for unread
            unreadBorder.setCornerRadius(16); // Optional: Match the card's rounded corners
            holder.cardView.setBackground(unreadBorder);
        } else {
            // Remove border for read notifications
            holder.cardView.setBackground(null);
        }
    }


    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationType, notificationMessage, notificationPriority;
        CardView cardView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationType = itemView.findViewById(R.id.notificationType);
            notificationMessage = itemView.findViewById(R.id.notificationMessage);
            notificationPriority = itemView.findViewById(R.id.notificationPriority);
            cardView = itemView.findViewById(R.id.notificationCardView);
        }
    }
}
