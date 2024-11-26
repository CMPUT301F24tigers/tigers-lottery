package com.example.tigers_lottery.Admin.DashboardFragments.ListItems;

/**
 * Model class representing an item in the admin dashboard list.
 * Each item can represent a user or an event.
 */
public class AdminListItemModel {
        private final String uniqueIdentifier;
        private final String displayName;
        private final String secondaryText;
        private final String profilePictureUrl; // For user profile picture
        private final String option1Text;
        private final String option2Text;
        private final boolean isEvent;         // Differentiates between user and event

        /**
         * Constructor for users.
         *
         * @param uniqueIdentifier  A unique ID for the user.
         * @param displayName       The primary display name for the user.
         * @param secondaryText     Additional text information for the user.
         * @param profilePictureUrl URL for the user's profile picture.
         * @param option1Text       Label for the first action option.
         * @param option2Text       Label for the second action option.
         * @param isEvent
         */
        public AdminListItemModel(String uniqueIdentifier, String displayName, String secondaryText,
                                  String profilePictureUrl, String option1Text, String option2Text, boolean isEvent) {
                this.uniqueIdentifier = uniqueIdentifier;
                this.displayName = displayName;
                this.secondaryText = secondaryText;
                this.profilePictureUrl = profilePictureUrl;
                this.option1Text = option1Text;
                this.option2Text = option2Text;
                this.isEvent = isEvent; // Default to false for users
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

        public String getProfilePictureUrl() {
                return profilePictureUrl;
        }

        public String getOption1Text() {
                return option1Text;
        }

        public String getOption2Text() {
                return option2Text;
        }

        public boolean isEvent() {
                return isEvent;
        }
}
