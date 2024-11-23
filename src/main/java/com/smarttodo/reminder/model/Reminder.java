package com.smarttodo.reminder.model;

import com.smarttodo.user.model.User;

import java.util.Date;

public class Reminder {
    private String reminderID;
    private String taskID;
    private String recurrencePattern; // if recurring
    private Date dueDate;
    private User user;

    // Constructors
    public Reminder() {}

    public Reminder(String reminderID, String taskID, String recurrencePattern, Date dueDate, User user) {
        this.reminderID = reminderID;
        this.taskID = taskID;
        this.recurrencePattern = recurrencePattern;
        this.dueDate = dueDate;
        this.user = user;
    }

    // Getters and Setters
    public String getReminderID() {
        return reminderID;
    }

    public void setReminderID(String reminderID) {
        this.reminderID = reminderID;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
