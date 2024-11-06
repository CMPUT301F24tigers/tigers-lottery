package com.example.tigers_lottery.HostedEvents.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tigers_lottery.R;

import java.util.List;

/**
 * Adapter for displaying a list of users in RecyclerView in the Organizer section
 * The organizer does not have access to IDs of users.
 */
public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.EntrantViewHolder> {

    private final List<String> entrantIds;

    /**
     * Constructor for EntrantAdapter
     *
     * @param entrantIds Identify registered users by their entrant Id.
     */

    public EntrantAdapter(List<String> entrantIds) {
        this.entrantIds = entrantIds;
    }

    /**
     * Inflates the entrant holder layout and creates a ViewHolder for it.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return the inflated view.
     */


    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entrant_item, parent, false);
        return new EntrantViewHolder(view);
    }

    /**
     * Binds entrant data to the view based on their position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        String entrantId = entrantIds.get(position);
        holder.entrantIdTextView.setText(entrantId);
    }

    /**
     * Gets the amount of entrantIds; amount of entrants in the view.
     *
     * @return the number of entrants.
     */

    @Override
    public int getItemCount() {
        return entrantIds.size();
    }

    /**
     * ViewHolder class for managing the user item views in the RecyclerView.
     */

    static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView entrantIdTextView;

        /**
         * Constructor for UserViewHolder, Initializes textViews for entrant Ids.
         *
         * @param itemView Represents a single entrant.
         */

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantIdTextView = itemView.findViewById(R.id.entrantIdTextView);
        }
    }
}

