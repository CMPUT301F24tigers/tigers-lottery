package com.example.tigers_lottery.HostedEvents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> events;
    private Context context;
    private OnEventOptionSelectedListener optionSelectedListener;

    // Constructor for the adapter
    public EventAdapter(Context context, List<Event> events, OnEventOptionSelectedListener listener) {
        this.context = context;
        this.events = events;
        this.optionSelectedListener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);

        // Set the event details
        holder.eventName.setText(event.getEventName());
        holder.eventDate.setText("Event Date: " + event.getFormattedEventDate());
        holder.eventId.setText("Event ID: " + event.getEventId());
        holder.organizerId.setText("Organizer ID: " + event.getOrganizerId());
        holder.waitlistLimit.setText("Waitlist Limit: " + event.getWaitlistLimit());
        holder.eventGeolocation.setText("Location: " + event.getGeolocation());

        // Set up the options menu for each item
        holder.optionsMenu.setOnClickListener(view -> {
            // Create a PopupMenu for the options
            PopupMenu popupMenu = new PopupMenu(context, holder.optionsMenu);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.organizer_event_item_menu, popupMenu.getMenu());

            // Handle menu item clicks
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_edit) {
                    optionSelectedListener.onEditSelected(event);
                    return true;
                } else if (item.getItemId() == R.id.action_delete) {
                    optionSelectedListener.onDeleteSelected(event);
                    return true;
                }
                return false;
            });

            // Show the popup menu
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    // ViewHolder class to hold each item view
    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventId, organizerId, waitlistLimit, eventGeolocation;
        ImageView optionsMenu;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventId = itemView.findViewById(R.id.eventId);
            organizerId = itemView.findViewById(R.id.organizerId);
            waitlistLimit = itemView.findViewById(R.id.waitlistLimit);
            eventGeolocation = itemView.findViewById(R.id.eventGeolocation);
            optionsMenu = itemView.findViewById(R.id.optionsMenu);
        }
    }

    // Interface for handling edit and delete actions
    public interface OnEventOptionSelectedListener {
        void onEditSelected(Event event);
        void onDeleteSelected(Event event);
    }
}
