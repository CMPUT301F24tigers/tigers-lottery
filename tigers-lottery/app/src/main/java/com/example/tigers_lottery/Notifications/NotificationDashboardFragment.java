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

    /**
     * Inflates the layout for the notifications screen
     * initializes the dbHelper and its recycler view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the view for the fragment's ui.
     */

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
            /**
             * On finding the notifications sorts them in descending order.
             * @param notifications list of notifications.
             */
            @Override
            public void onNotificationsFetched(List<Notification> notifications) {
                // Sort notifications by timestamp in descending order (latest first)
                notifications.sort((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()));
                notificationList.clear();
                notificationList.addAll(notifications);
                notificationAdapter.notifyDataSetChanged();
            }

            /**
             * Handles error on loading notifications.
             * @param e exception catcher.
             */

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
            /**
             * Required method, no move operations needed.
             * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to.
             * @param viewHolder   The ViewHolder which is being dragged by the user.
             * @param target       The ViewHolder over which the currently active item is being
             *                     dragged.
             * @return false
             */
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // No move operations needed
            }

            /**
             * Handles actions on swiping to remove a notification.
             *
             * @param viewHolder The ViewHolder which has been swiped by the user.
             * @param direction  The direction to which the ViewHolder is swiped. It is one of
             *                   {@link #UP}, {@link #DOWN},
             *                   {@link #LEFT} or {@link #RIGHT}. If your
             *                   {@link #getMovementFlags(RecyclerView, ViewHolder)}
             *                   method
             *                   returned relative flags instead of {@link #LEFT} / {@link #RIGHT};
             *                   `direction` will be relative as well. ({@link #START} or {@link
             *                   #END}).
             */

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
                            /**
                             * Handles action on  dismissing the event by swiping.
                             *
                             * @param transientBottomBar The transient bottom bar which has been dismissed.
                             * @param event The event which caused the dismissal. One of either: {@link
                             *     #DISMISS_EVENT_SWIPE}, {@link #DISMISS_EVENT_ACTION}, {@link #DISMISS_EVENT_TIMEOUT},
                             *     {@link #DISMISS_EVENT_MANUAL} or {@link #DISMISS_EVENT_CONSECUTIVE}.
                             */
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                if (event != DISMISS_EVENT_ACTION) {
                                    // Finalize deletion from Firestore if not undone
                                    dbHelper.deleteNotification(notification.getNotificationId(), new DatabaseHelper.NotificationCallback() {
                                        /**
                                         * Handles success on deleting a notification from the database.
                                         * @param responseMessage to be logged.
                                         */
                                        @Override
                                        public void onSuccess(String responseMessage) {
                                            Log.d(TAG, "Notification permanently deleted: " + responseMessage);
                                        }

                                        /**
                                         * Handles failure on deleting a notification from the database.
                                         * @param errorMessage to be logged.
                                         */

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
