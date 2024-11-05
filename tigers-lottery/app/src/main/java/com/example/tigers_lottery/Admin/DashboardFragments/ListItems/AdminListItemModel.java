package com.example.tigers_lottery.Admin.DashboardFragments.ListItems;

public class AdminListItemModel {
        private final String uniqueIdentifier;
        private final String displayName;
        private final String secondaryText;
        private final String option1Text;
        private final String option2Text;
        private final String option3Text;

        public AdminListItemModel(String uniqueIdentifier, String displayName, String secondaryText,
                                  String option1Text, String option2Text, String option3Text) {
                this.uniqueIdentifier = uniqueIdentifier;
                this.displayName = displayName;
                this.secondaryText = secondaryText;
                this.option1Text = option1Text;
                this.option2Text = option2Text;
                this.option3Text = option3Text;
        }

        public String getUniqueIdentifier() { return uniqueIdentifier; }
        public String getDisplayName() { return displayName; }
        public String getSecondaryText() { return secondaryText; }
        public String getOption1Text() { return option1Text; }
        public String getOption2Text() { return option2Text; }
        public String getOption3Text() { return option3Text; }
}