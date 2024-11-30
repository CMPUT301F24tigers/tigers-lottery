package com.example.tigers_lottery.HostedEvents.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.User;

import java.util.List;

/**
 * Adapter for displaying a list of users in RecyclerView in the Organizer section.
 */
public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.EntrantViewHolder> {

    private final List<User> users;

    /**
     * Constructor for the entrant adapter.
     * @param users users in the entrant adapter.
     */

    public EntrantAdapter(List<User> users) {
        this.users = users;
    }

    /**
     * Creates the view holder for the entrant list items.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return the view holder for entrants.
     */

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entrant_item, parent, false);
        return new EntrantViewHolder(view);
    }

    /**
     * Binds the entrant items and populates the required fields.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        User user = users.get(position);
        holder.entrantNameTextView.setText(user.getFirstName() + " " + user.getLastName());
        holder.entrantEmailTextView.setText("Email: " + user.getEmailAddress());
    }

    /**
     *
     * @return the count of items in the list.
     */

    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * ViewHolder class for managing the views within each item.
     */

    static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView entrantNameTextView, entrantEmailTextView;

        /**
         * Initializes the views within each item in the recycler view.
         * @param itemView The item view.
         */

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantNameTextView = itemView.findViewById(R.id.entrantNameTextView);
            entrantEmailTextView = itemView.findViewById(R.id.entrantEmailTextView);
        }
    }
}
