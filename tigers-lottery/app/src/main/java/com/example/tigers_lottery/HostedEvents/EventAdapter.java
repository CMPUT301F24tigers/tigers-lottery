package com.example.tigers_lottery.HostedEvents;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> events;

    // Constructor for the adapter, taking in the list of events
    public EventAdapter(List<Event> events) {
        this.events = events;
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

        // Set the event name in the corresponding TextView
        holder.eventName.setText(event.getEventName());

        // Use the formatted date strings from the helper methods
        holder.eventDate.setText("Event Date: " + event.getFormattedEventDate());
        holder.eventId.setText("Event ID: " + event.getEventId());
        holder.organizerId.setText("Organizer ID: " + event.getOrganizerId());
        holder.waitlistLimit.setText("Waitlist Limit: " + event.getWaitlistLimit());
        holder.eventGeolocation.setText("Location: " + event.getGeolocation());
    }


    @Override
    public int getItemCount() {
        return events.size();
    }

    // ViewHolder class to hold each item view
    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventLocation, eventDate, eventId, organizerId, waitlistLimit, eventGeolocation;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventId = itemView.findViewById(R.id.eventId);
            organizerId = itemView.findViewById(R.id.organizerId);
            waitlistLimit = itemView.findViewById(R.id.waitlistLimit);
            eventGeolocation = itemView.findViewById(R.id.eventGeolocation);
        }
    }

}
