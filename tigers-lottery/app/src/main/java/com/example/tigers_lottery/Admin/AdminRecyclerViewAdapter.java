package com.example.tigers_lottery.Admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tigers_lottery.Admin.DashboardFragments.ListItems.AdminListItemModel;
import com.example.tigers_lottery.Admin.DashboardFragments.ListItems.OnActionListener;
import com.example.tigers_lottery.R;
import java.util.List;

public class AdminRecyclerViewAdapter extends RecyclerView.Adapter<AdminRecyclerViewAdapter.UserViewHolder> {

    private List<AdminListItemModel> itemList;
    private OnActionListener actionListener;
    private int expandedPosition = -1;

    public AdminRecyclerViewAdapter(List<AdminListItemModel> itemList, OnActionListener actionListener) {
        this.itemList = itemList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        AdminListItemModel item = itemList.get(position);

        holder.userName.setText(item.getDisplayName());
        holder.userEmail.setText(item.getSecondaryText());
        holder.expandableMenuTextView.setText("Actions for " + item.getDisplayName());
        holder.expandableMenuOption1.setText(item.getOption1Text());
        holder.expandableMenuOption2.setText(item.getOption2Text());
        holder.expandableMenuOption3.setText(item.getOption3Text());

        holder.expandableMenuLayout.setVisibility(holder.getAdapterPosition() == expandedPosition ? View.VISIBLE : View.GONE);

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

        holder.expandableMenuOption1.setOnClickListener(v -> actionListener.onOptionOneClick(item.getUniqueIdentifier()));
        holder.expandableMenuOption2.setOnClickListener(v -> actionListener.onOptionTwoClick(item.getUniqueIdentifier()));
        holder.expandableMenuOption3.setOnClickListener(v -> actionListener.onOptionThreeClick(item.getUniqueIdentifier()));
    }

    public void setExpandedPosition(int position) {
        this.expandedPosition = position;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userEmail, expandableMenuTextView;
        ImageButton optionsButton;
        Button expandableMenuOption1, expandableMenuOption2, expandableMenuOption3;
        ConstraintLayout expandableMenuLayout;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textViewUserName);
            userEmail = itemView.findViewById(R.id.textViewUserEmail);
            optionsButton = itemView.findViewById(R.id.optionsButton);
            expandableMenuLayout = itemView.findViewById(R.id.expandableMenuLayout);
            expandableMenuTextView = itemView.findViewById(R.id.expandableMenuTextView);
            expandableMenuOption1 = itemView.findViewById(R.id.expandableMenuOption1);
            expandableMenuOption2 = itemView.findViewById(R.id.expandableMenuOption2);
            expandableMenuOption3 = itemView.findViewById(R.id.expandableMenuOption3);
        }
    }
}