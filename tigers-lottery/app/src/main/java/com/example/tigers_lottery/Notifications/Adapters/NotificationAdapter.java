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

    /**
     * Initializes the notification adapter.
     *
     * @param context context of the current state of the activity.
     * @param notifications list of notifications.
     */
    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    /**
     * Creates a view holder for the notification items.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return the view holder.
     */

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    /**
     *  Binds the notification items' details to the view and the holder.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        // Set notification details
        holder.notificationType.setText(notification.getType());
        holder.notificationMessage.setText(notification.getMessage());

        // Priority
        String priority = notification.getPriority();
        holder.notificationPriority.setText(priority + " Priority");

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
                holder.notificationPriority.setTextColor(Color.BLACK);
                break;
            default:
                backgroundColor = Color.GRAY;
        }

        GradientDrawable priorityBackground = (GradientDrawable) holder.notificationPriority.getBackground();
        priorityBackground.setColor(backgroundColor);

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

    /**
     * Gets the amount of items in the notification list.
     *
     * @return number of notification.
     */

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    /**
     * Initializes the view for the notification holder screen.
     */

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationType, notificationMessage, notificationPriority;
        CardView cardView;
        FrameLayout frameLayout;

        /**
         * Assigns the notification fields to their required layouts.
         * @param itemView view
         */

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
