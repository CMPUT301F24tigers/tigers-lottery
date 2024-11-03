package com.example.tigers_lottery.HostedEvents.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tigers_lottery.R;

import java.util.List;

public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.EntrantViewHolder> {

    private final List<String> entrantIds;

    public EntrantAdapter(List<String> entrantIds) {
        this.entrantIds = entrantIds;
    }

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entrant_item, parent, false);
        return new EntrantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        String entrantId = entrantIds.get(position);
        holder.entrantIdTextView.setText(entrantId);
    }

    @Override
    public int getItemCount() {
        return entrantIds.size();
    }

    static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView entrantIdTextView;

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantIdTextView = itemView.findViewById(R.id.entrantIdTextView);
        }
    }
}

