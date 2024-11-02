package com.example.tigers_lottery.Admin;

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
 * Adapter for displaying a list of users in a RecyclerView within the admin section.
 * Binds user data (first name, last name, and email) to each item view for display.
 */
public class AdminRecyclerViewAdapter extends RecyclerView.Adapter<AdminRecyclerViewAdapter.UserViewHolder> {

    private List<User> userList;

    /**
     * Constructor for AdminRecyclerViewAdapter.
     *
     * @param userList List of User objects to be displayed in the RecyclerView.
     */
    public AdminRecyclerViewAdapter(List<User> userList) {
        this.userList = userList;
    }

    /**
     * Inflates the item layout and creates a ViewHolder for it.
     *
     * @param parent   The ViewGroup into which the new view will be added.
     * @param viewType The view type of the new View.
     * @return A new UserViewHolder that holds the view for each item.
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_user_item, parent, false);
        return new UserViewHolder(view);
    }

    /**
     * Binds data to the view elements in each item based on the position.
     *
     * @param holder   The ViewHolder that holds the item view.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText(user.getFirstName() + " " + user.getLastName());
        holder.userEmail.setText(user.getEmailAddress());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The number of items in the user list.
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * ViewHolder class for managing the user item views in the RecyclerView.
     */
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userEmail;

        /**
         * Constructor for UserViewHolder, initializing the text views for displaying
         * user name and email.
         *
         * @param itemView The view representing a single user item.
         */
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textViewUserName);
            userEmail = itemView.findViewById(R.id.textViewUserEmail);
        }
    }
}