package com.example.tigers_lottery.Admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tigers_lottery.Admin.DashboardFragments.ListItems.AdminListItemModel;
import com.example.tigers_lottery.Admin.DashboardFragments.ListItems.OnActionListener;
import com.example.tigers_lottery.R;

import java.util.List;

/**
 * RecyclerView Adapter for displaying a list of admin items (users or events).
 * Each item has expandable options and supports actions defined in the OnActionListener.
 */
public class AdminRecyclerViewAdapter extends RecyclerView.Adapter<AdminRecyclerViewAdapter.ItemViewHolder> {

    private List<AdminListItemModel> itemList;
    private OnActionListener actionListener;
    private int expandedPosition = -1;

    /**
     * Constructs an adapter for a list of admin items.
     * @param itemList       The list of items to display in the RecyclerView.
     * @param actionListener Listener to handle actions on the expandable menu.
     */
    public AdminRecyclerViewAdapter(List<AdminListItemModel> itemList, OnActionListener actionListener) {
        this.itemList = itemList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        AdminListItemModel item = itemList.get(position);

        // Load the correct image (user or event)
        boolean isEvent = item.isEvent();
        String photoUrl = item.getProfilePictureUrl();

        if (photoUrl == null || photoUrl.isEmpty()) {
            if (!isEvent) {
                holder.profileImageView.setImageResource(R.drawable.placeholder_user_image); // Event placeholder picture
            } else {
                holder.profileImageView.setImageResource(R.drawable.event_poster_placeholder); // User placeholder picture
            }
        } else {
            // Using Glide to load the actual image
            Glide.with(holder.profileImageView.getContext())
                    .load(photoUrl)
                    .placeholder(!item.isEvent() ? R.drawable.placeholder_user_image : R.drawable.event_poster_placeholder) // Conditional placeholder
                    .into(holder.profileImageView);
        }

        // Set text for item views
        holder.itemName.setText(item.getDisplayName());
        holder.itemSecondaryText.setText(item.getSecondaryText());
        holder.expandableMenuTextView.setText("Actions for " + item.getDisplayName());
        holder.expandableMenuOption1.setText(item.getOption1Text());
        holder.expandableMenuOption2.setText(item.getOption2Text());

        // Expand or collapse the menu based on the current position
        holder.expandableMenuLayout.setVisibility(holder.getAdapterPosition() == expandedPosition ? View.VISIBLE : View.GONE);

        // Toggle menu visibility on options button click
        holder.optionsButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (expandedPosition == adapterPosition) {
                expandedPosition = -1;
            } else {
                int previousExpandedPosition = expandedPosition;
                expandedPosition = adapterPosition;
                notifyItemChanged(previousExpandedPosition);
            }
            notifyItemChanged(adapterPosition);
        });

        // Set click listeners for each option in the expandable menu
        holder.expandableMenuOption1.setOnClickListener(v -> actionListener.onOptionOneClick(item.getUniqueIdentifier()));
        holder.expandableMenuOption2.setOnClickListener(v -> actionListener.onOptionTwoClick(item.getUniqueIdentifier()));
    }

    /**
     * Sets the position of the currently expanded item.
     * @param position The position of the item to expand.
     */
    public void setExpandedPosition(int position) {
        this.expandedPosition = position;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /**
     * ViewHolder class for managing the views within each item.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemSecondaryText, expandableMenuTextView;
        ImageButton optionsButton;
        ImageView profileImageView;
        Button expandableMenuOption1, expandableMenuOption2;
        ConstraintLayout expandableMenuLayout;

        /**
         * Initializes the views within each item in the RecyclerView.
         * @param itemView The item view.
         */
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.textViewUserName);
            itemSecondaryText = itemView.findViewById(R.id.textViewUserEmail);
            optionsButton = itemView.findViewById(R.id.optionsButton);
            profileImageView = itemView.findViewById(R.id.imageViewUserProfile);
            expandableMenuLayout = itemView.findViewById(R.id.expandableMenuLayout);
            expandableMenuTextView = itemView.findViewById(R.id.expandableMenuTextView);
            expandableMenuOption1 = itemView.findViewById(R.id.expandableMenuOption1);
            expandableMenuOption2 = itemView.findViewById(R.id.expandableMenuOption2);
        }
    }
}
