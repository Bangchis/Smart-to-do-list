package com.smarttodo.task.model;

public enum Priority {
    HIGH, MEDIUM, LOW;

    @Override
    public String toString() {
        // Customize display name if needed
        switch (this) {
            case HIGH:
                return "High Priority";
            case MEDIUM:
                return "Medium Priority";
            case LOW:
                return "Low Priority";
            default:
                return super.toString();
        }
    }
}
