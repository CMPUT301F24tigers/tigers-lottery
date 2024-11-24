package com.example.tigers_lottery.Admin.DashboardFragments.ListItems;

/**
 * Model class representing an item in the admin dashboard list.
 * Each item has a unique identifier, display name, secondary text, and two options
 * for actions that can be taken on the item.
 */
public class AdminListItemModel {
        private final String uniqueIdentifier;
        private final String displayName;
        private final String secondaryText;
        private final String option1Text;
        private final String option2Text;

        /**
         * Constructor to initialize all fields of the AdminListItemModel.
         *
         * @param uniqueIdentifier A unique ID for the item.
         * @param displayName      The primary display name for the item.
         * @param secondaryText    Additional text information for the item.
         * @param option1Text      Label for the first action option.
         * @param option2Text      Label for the second action option.
         */
        public AdminListItemModel(String uniqueIdentifier, String displayName, String secondaryText,
                                  String option1Text, String option2Text) {
                this.uniqueIdentifier = uniqueIdentifier;
                this.displayName = displayName;
                this.secondaryText = secondaryText;
                this.option1Text = option1Text;
                this.option2Text = option2Text;
        }

        public String getUniqueIdentifier() {
                return uniqueIdentifier;
        }

        public String getDisplayName() {
                return displayName;
        }

        public String getSecondaryText() {
                return secondaryText;
        }

        public String getOption1Text() {
                return option1Text;
        }

        public String getOption2Text() {
                return option2Text;
        }
}