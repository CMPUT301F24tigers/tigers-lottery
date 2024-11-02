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
        // Get the current event object at the specified position in the list
        Event event = events.get(position);

        // Set the event name in the corresponding TextView
        holder.eventName.setText(event.getEventName());

        // Format the event date
        if (event.getEventDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault());
            String formattedDate = dateFormat.format(event.getEventDate().toDate()); // Convert Timestamp to formatted date string
            holder.eventDate.setText("Event Date: " + formattedDate);
        } else {
            holder.eventDate.setText("Date not available");
        }

        // Set eventId in the TextView for event ID, converting to a string
        holder.eventId.setText("Event ID: " + String.valueOf(event.getEventId()));

        // Set organizerId in a separate TextView, also converting to a string
        holder.organizerId.setText("Organizer ID (for test purposes): " + String.valueOf(event.getOrganizerId()));

        // Set waitlist limit in the TextView, converting to a string
        holder.waitlistLimit.setText("Waitlist Limit: " + String.valueOf(event.getWaitlistLimit()));

        // Display geolocation if available, showing both latitude and longitude
        if (event.getGeolocation() != null) {
            double latitude = event.getGeolocation().getLatitude();
            double longitude = event.getGeolocation().getLongitude();
            holder.eventGeolocation.setText("Location: " + latitude + "°, " + longitude + "°");
        } else {
            holder.eventGeolocation.setText("Location: N/A");  // Show N/A if geolocation is null
        }
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
