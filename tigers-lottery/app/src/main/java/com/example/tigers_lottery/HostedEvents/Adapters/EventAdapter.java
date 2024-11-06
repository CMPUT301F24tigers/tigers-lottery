package com.example.tigers_lottery.HostedEvents.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
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

/**
 * Adapter for displaying a list of events held by the organizer.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<Event> events;
    private final Context context;
    private final OnEventOptionSelectedListener optionSelectedListener;
    private final OnEventClickListener eventClickListener;

    /**
     * Constructor for EventAdapter
     *
     * @param context Context in which the adapter is used.
     * @param events EventList to display.
     * @param optionListener Listener for event option selection.
     * @param eventClickListener Listener for event click action.
     */

    // Constructor for the adapter, including both listeners
    public EventAdapter(Context context, List<Event> events, OnEventOptionSelectedListener optionListener, OnEventClickListener eventClickListener) {
        this.context = context;
        this.events = events;
        this.optionSelectedListener = optionListener;
        this.eventClickListener = eventClickListener;
    }

    /**
     * Creates a new ViewHolder for EventView
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return inflated new instance of EventViewHolder.
     */

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Binds the event Name,Date,Id,OrganizerId,waitlistLimit, and Geolocation
     * to the its position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */


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
            PopupMenu popupMenu = new PopupMenu(context, holder.optionsMenu);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.organizer_event_item_menu, popupMenu.getMenu());

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

            popupMenu.show();
        });

        // Set up click listener for the entire event item
        holder.itemView.setOnClickListener(v -> eventClickListener.onEventClick(event));
    }

    /**
     * Gets the amount of events in the view.
     *
     * @return the number of events.
     */

    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * ViewHolder class for holding each item view.
     */

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventId, organizerId, waitlistLimit, eventGeolocation;
        ImageView optionsMenu;

        /**
         * Constructor for EventViewHolder,initializes textViews for all event details.
         *
         * @param itemView represents a single event.
         */

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

    /**
     * Interface for handling edit and delete actions.
     */

    public interface OnEventOptionSelectedListener {
        void onEditSelected(Event event);
        void onDeleteSelected(Event event);
    }

    /**
     * Interface for handling event item clicks.
     */

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }
}
