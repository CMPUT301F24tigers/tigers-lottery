package com.example.tigers_lottery.Admin;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Adapter class for displaying admin list items within a ListView.
 * This adapter binds a list of {@link AdminListItemModel} objects to views of type {@link AdminListItem}.
 */
public class AdminListAdapter extends ArrayAdapter<AdminListItemModel> {

    /**
     * Constructor for creating an instance of the AdminListAdapter.
     *
     * @param context The current context.
     * @param items   List of {@link AdminListItemModel} objects to be displayed.
     */
    public AdminListAdapter(Context context, List<AdminListItemModel> items) {
        super(context, 0, items);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.).
     * This method gets a view that displays the data at the specified position in the data set.
     * If the view is not recycled, it initializes a new  {@link AdminListItem}.
     *
     * @param position    Position of the item within the adapter's data set.
     * @param convertView Existing view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AdminListItemModel model = getItem(position);

        if (convertView == null) {
            convertView = new AdminListItem(getContext());
        }

        ((AdminListItem) convertView).setData(model);

        return convertView;
    }
}