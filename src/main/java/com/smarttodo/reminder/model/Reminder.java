package com.smarttodo.reminder.model;

import com.smarttodo.user.model.User;

import java.util.Date;

public class Reminder {
    private String reminderID;
    private String taskID;
    private String recurrencePattern; // if recurring
    private Date dueDate;
    private String title;

    // Constructors
    public Reminder() {}

    public Reminder(String reminderID, String taskID, String recurrencePattern, Date dueDate, String title) {
        this.reminderID = reminderID;
        this.taskID = taskID;
        this.recurrencePattern = recurrencePattern;
        this.dueDate = dueDate;
        this.title = title;
    }

    // Getters and Setters
    public String getReminderID() {
        return reminderID;
    }

    public void setReminderID(String reminderID) {
        this.reminderID = reminderID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getRecurrencePattern() {
        return recurrencePattern;
    }

    public void setRecurrencePattern(String recurrencePattern) {
        this.recurrencePattern = recurrencePattern;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    // Methods (to be implemented later)
    public void schedule() {
        // Logic to be added later
    }

    public void edit() {
        // Logic to be added later
    }

    public void cancel() {
        // Logic to be added later
    }

    public void sendReminder() {
        // Logic to be added later
    }
}
