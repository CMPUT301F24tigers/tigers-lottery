package com.example.tigers_lottery.Notifications;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.Notifications.Adapters.NotificationAdapter;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Notification;

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

        return view;
    }

    /**
     * Loads notifications for the current user.
     */
    private void loadNotifications() {
        String currentUserId = dbHelper.getCurrentUserId(); // Implement this method to get the logged-in user's ID

        dbHelper.fetchNotificationsForUser(currentUserId, new DatabaseHelper.NotificationsCallback() {
            @Override
            public void onNotificationsFetched(List<Notification> notifications) {
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
}
