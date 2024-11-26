package com.smarttodo.reminder.model;

import com.smarttodo.user.model.User;
import com.smarttodo.user.service.UserService;

import java.util.Date;
import java.util.UUID;



import com.smarttodo.user.model.User;
import com.smarttodo.user.service.UserService;

import java.util.Date;
import java.util.UUID;

public class Reminder {
    private String reminderID;
    private String taskID;
    private String workspaceID; // Liên kết với Workspace
    private String recurrencePattern; // Mô hình lặp lại, nếu có
    private Date dueDate;
    private User user;

    // Default Constructor
    public Reminder() {
        System.out.println("Default Reminder instance created.");
    }

    // Full Constructor
    public Reminder(String reminderID, String taskID, String workspaceID, String recurrencePattern, Date dueDate, User user) {
        this.reminderID = reminderID;
        this.taskID = taskID;
        this.workspaceID = workspaceID;
        this.recurrencePattern = recurrencePattern;
        this.dueDate = dueDate;
        this.user = user;
        System.out.println("Reminder created with details: " +
                "\nReminderID: " + reminderID +
                "\nTaskID: " + taskID +
                "\nWorkspaceID: " + workspaceID +
                "\nRecurrencePattern: " + recurrencePattern +
                "\nDueDate: " + dueDate +
                "\nUserID: " + (user != null ? user.getUserId() : "No User"));
    }

    // Getters and Setters
    public String getReminderID() {
        return reminderID;
    }

    public void setReminderID(String reminderID) {
        this.reminderID = reminderID;
        System.out.println("ReminderID updated to: " + reminderID);
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
        System.out.println("TaskID updated to: " + taskID);
    }

    public String getWorkspaceID() {
        return workspaceID;
    }

    public void setWorkspaceID(String workspaceID) {
        this.workspaceID = workspaceID;
        System.out.println("WorkspaceID updated to: " + workspaceID);
    }

    public String getRecurrencePattern() {
        return recurrencePattern;
    }

    public void setRecurrencePattern(String recurrencePattern) {
        this.recurrencePattern = recurrencePattern;
        System.out.println("RecurrencePattern updated to: " + recurrencePattern);
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
        System.out.println("DueDate updated to: " + dueDate);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        System.out.println("User updated to: " + (user != null ? user.getUserId() : "No User"));
    }

    // Methods
    public void schedule() {
        System.out.println("Scheduling reminder with ID: " + reminderID);
        // Implement scheduling logic here
    }

    public void edit(String newRecurrencePattern, Date newDueDate) {
        System.out.println("Editing reminder with ID: " + reminderID);
        this.recurrencePattern = newRecurrencePattern;
        this.dueDate = newDueDate;
        System.out.println("Reminder updated with new RecurrencePattern: " + newRecurrencePattern + " and new DueDate: " + newDueDate);
    }

    public void cancel() {
        System.out.println("Cancelling reminder with ID: " + reminderID);
        // Implement cancellation logic here
    }

    public void sendReminder() {
        System.out.println("Sending reminder with ID: " + reminderID);
        // Implement sending reminder logic here
    }

    // Static Factory Method
    public static Reminder createReminderInstance(String taskID, String workspaceID, String recurrencePattern, Date dueDate) {
        // Ensure a user is logged in
        User currentUser = UserService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in.");
        }

        // Generate unique ID for the reminder
        String reminderID = UUID.randomUUID().toString();

        // Create a new reminder instance
        Reminder newReminder = new Reminder(reminderID, taskID, workspaceID, recurrencePattern, dueDate, currentUser);
        System.out.println("New reminder created for TaskID: " + taskID + " in WorkspaceID: " + workspaceID);
        return newReminder;
    }


    public Reminder(String taskID, String recurrencePattern, Date dueDate) {
        this.reminderID = UUID.randomUUID().toString(); // Tạo reminderID tự động
        this.taskID = taskID;
        this.recurrencePattern = recurrencePattern;
        this.dueDate = dueDate;
        this.user = UserService.getCurrentUser(); // Lấy người dùng hiện tại
        System.out.println("Reminder created with TaskID: " + taskID + ", RecurrencePattern: " + recurrencePattern + ", DueDate: " + dueDate);
    }
}
