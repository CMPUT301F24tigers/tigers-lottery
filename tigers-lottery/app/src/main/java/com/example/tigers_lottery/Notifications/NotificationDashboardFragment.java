package com.example.tigers_lottery.Notifications;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.Notifications.Adapters.NotificationAdapter;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Notification;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display the user's notifications dashboard.
 */
public class NotificationDashboardFragment extends Fragment {

    private static final String TAG = "NotificationDashboard";
    private RecyclerView notificationsRecyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_dashboard, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        notificationsRecyclerView = view.findViewById(R.id.notificationRecyclerView);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize notification list and adapter
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(requireContext(), notificationList);
        notificationsRecyclerView.setAdapter(notificationAdapter);

        loadNotifications();

        // Attach swipe-to-delete functionality
        setupSwipeToDelete();

        return view;
    }

    /**
     * Loads notifications for the current user.
     */
    private void loadNotifications() {
        String currentUserId = dbHelper.getCurrentUserId();

        dbHelper.fetchNotificationsForUser(currentUserId, new DatabaseHelper.NotificationsCallback() {
            @Override
            public void onNotificationsFetched(List<Notification> notifications) {
                // Sort notifications by timestamp in descending order (latest first)
                notifications.sort((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()));
                notificationList.clear();
                notificationList.addAll(notifications);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error loading notifications", e);
            }
        });
    }

    /**
     * Sets up swipe-to-delete functionality for notifications.
     */
    private void setupSwipeToDelete() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // No move operations needed
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Notification notification = notificationList.get(position);

                // Temporarily remove the notification from the list
                notificationList.remove(position);
                notificationAdapter.notifyItemRemoved(position);

                // Show Snackbar for undo
                Snackbar.make(notificationsRecyclerView, "Notification deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> {
                            // Restore the notification to the list
                            notificationList.add(position, notification);
                            notificationAdapter.notifyItemInserted(position);
                        })
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                if (event != DISMISS_EVENT_ACTION) {
                                    // Finalize deletion from Firestore if not undone
                                    dbHelper.deleteNotification(notification.getNotificationId(), new DatabaseHelper.NotificationCallback() {
                                        @Override
                                        public void onSuccess(String responseMessage) {
                                            Log.d(TAG, "Notification permanently deleted: " + responseMessage);
                                        }

                                        @Override
                                        public void onFailure(String errorMessage) {
                                            Log.e(TAG, "Failed to delete notification: " + errorMessage);
                                        }
                                    });
                                }
                            }
                        })
                        .show();
            }

        });

        itemTouchHelper.attachToRecyclerView(notificationsRecyclerView);
    }
}
