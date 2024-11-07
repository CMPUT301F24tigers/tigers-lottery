package com.example.tigers_lottery.Admin.DashboardFragments.ListItems;

/**
 * Interface to define callback actions for items in the admin dashboard list.
 * Provides methods to handle three specific actions (Option 1, Option 2, and Option 3)
 * associated with each item in the list, identified by a unique ID.
 */
public interface OnActionListener {

    /**
     * Callback for the first option action.
     * @param uniqueID The unique identifier for the item associated with this action.
     */
    void onOptionOneClick(String uniqueID);

    /**
     * Callback for the second option action.
     * @param uniqueID The unique identifier for the item associated with this action.
     */
    void onOptionTwoClick(String uniqueID);

    /**
     * Callback for the third option action.
     * @param uniqueID The unique identifier for the item associated with this action.
     */
    void onOptionThreeClick(String uniqueID);
}