package com.example.tigers_lottery.Notifications.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.Notifications.NotificationDialog;
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

        // Set notification details
        holder.notificationType.setText(notification.getType());
        holder.notificationMessage.setText(notification.getMessage());

        // Priority
        String priority = notification.getPriority();
        holder.notificationPriority.setText(priority + " Priority");
        // ... Add your priority color logic here ...

        // Handle click to show dialog
        holder.itemView.setOnClickListener(view -> {
            // Show the dialog
            new NotificationDialog(context, notification, updatedNotification -> {
                // Update read status in database
                notification.setReadStatus(true);
                notifyItemChanged(position);

                // Update Firestore
                DatabaseHelper dbHelper = new DatabaseHelper(context);
                dbHelper.updateNotificationReadStatus(notification);
            }).showDialog();
        });

        // Unread indicator (blue border)
        if (!notification.isReadStatus()) {
            holder.frameLayout.setBackgroundResource(R.drawable.unread_notifications_border);
        } else {
            holder.frameLayout.setBackground(null);
        }
    }




    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationType, notificationMessage, notificationPriority;
        CardView cardView;
        FrameLayout frameLayout;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationType = itemView.findViewById(R.id.notificationType);
            notificationMessage = itemView.findViewById(R.id.notificationMessage);
            notificationPriority = itemView.findViewById(R.id.notificationPriority);
            cardView = itemView.findViewById(R.id.notificationCardView);
            frameLayout = itemView.findViewById(R.id.notificationFrameView);
        }
    }
}
