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
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView entrantNameTextView, entrantEmailTextView;

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantNameTextView = itemView.findViewById(R.id.entrantNameTextView);
            entrantEmailTextView = itemView.findViewById(R.id.entrantEmailTextView);
        }
    }
}
