package com.example.tigers_lottery.Admin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tigers_lottery.R;

/**
 * Custom view representing a single item in the admin list.
 * This view is designed to display a title and a count associated with an admin item.
 */
public class AdminListItem extends LinearLayout {
    private TextView titleTextView;

    /**
     * Constructor to create an AdminListItem with a specified context.
     *
     * @param context The context in which the view is running.
     */
    public AdminListItem(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The context in which the view is running.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public AdminListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Constructor that is called when inflating a view from XML and applying a style.
     *
     * @param context      The context in which the view is running.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a reference
     *                     to a style resource that supplies default values for the view.
     */
    public AdminListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Initializes the view by inflating the layout and setting up references.
     *
     * @param context The context used to inflate the layout.
     */
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.admin_list_item, this, true);
        titleTextView = findViewById(R.id.itemTitle);
    }

    /**
     * Sets the data for this list item based on the provided {@link AdminListItemModel}.
     *
     * @param model The model containing the title and count for the admin list item.
     */
    public void setData(AdminListItemModel model) {
        //String listItemTitle = model.getTitle() + " (" + model.getCount() + ")";
        String listItemTitle = model.getTitle();
        titleTextView.setText(listItemTitle);
    }
}
