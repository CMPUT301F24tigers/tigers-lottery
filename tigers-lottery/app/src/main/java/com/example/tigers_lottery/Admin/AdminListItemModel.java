package com.example.tigers_lottery.Admin;

/**
 * Model representing an item in the admin list.
 * Each item includes a title and a count value associated with it.
 */
public class AdminListItemModel {
    private String title;
    private int count;

    /**
     * Constructs an AdminListItemModel with a title and count.
     *
     * @param title The title of the list item.
     * @param count The numerical count associated with the list item.
     */
    public AdminListItemModel(String title, int count) {
        this.title = title;
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public int getCount() {
        return count;
    }
}
