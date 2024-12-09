package com.smarttodo.reminder.model;

public enum RecurrencePattern {
    EVERY_DAY("Every Day"),
    EVERY_WEEK("Every Week"),
    EVERY_MONTH("Every Month");

    private final String displayName;

    // Constructor
    RecurrencePattern(String displayName) {
        this.displayName = displayName;
    }

    // Getter for the display name
    public String getDisplayName() {
        return displayName;
    }

    // Optionally, override toString to return the display name directly
    @Override
    public String toString() {
        return displayName;
    }
}
