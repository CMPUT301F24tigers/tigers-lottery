package com.example.tigers_lottery.HostedEvents.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Adapter for displaying a list of users in RecyclerView in the Organizer section.
 */
public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.EntrantViewHolder> {

    private final List<User> users;
    private Set<Integer> selected = new HashSet<>();

    public EntrantAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entrant_item, parent, false);
        return new EntrantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        User user = users.get(position);
        holder.entrantNameTextView.setText(user.getFirstName() + " " + user.getLastName());
        holder.entrantEmailTextView.setText("Email: " + user.getEmailAddress());
        holder.layout.setOnClickListener(v -> {
            if (selected.contains(position)) {
                selected.remove(position); // Deselect if already selected
            } else {
                selected.add(position); // Select if not selected
            }
            notifyItemChanged(position); // Notify the adapter to update the clicked item
        });
        if (selected.contains(position)) {
            holder.layout.setBackgroundResource(R.color.lighter_purple); // or any selection indicator
        } else {
            holder.layout.setBackgroundResource(R.color.secondary_background_cards); // default background
        }


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public Set<Integer> getSelected() {return selected;}

    static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView entrantNameTextView, entrantEmailTextView;
        CardView cardView;
        LinearLayout layout;

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantNameTextView = itemView.findViewById(R.id.entrantNameTextView);
            entrantEmailTextView = itemView.findViewById(R.id.entrantEmailTextView);
            cardView = itemView.findViewById(R.id.entrantCardView);
            layout = itemView.findViewById(R.id.entrantLinearLayout);
        }
    }
}
