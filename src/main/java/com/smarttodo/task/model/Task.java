package com.smarttodo.task.model;

import java.util.Date;
import java.util.List;

// Enums for Priority and Status
enum Priority {
    HIGH, MEDIUM, LOW
}

enum Status {
    New, Pending, Todo, Completed, Overdue, Archived
}

public class Task {
    private int taskID;
    private String title;
    private String description;
    private Date dueDate;
    private Priority priority; // Enum: High, Medium, Low
    private Status status; // Enum: New, Pending, Todo, Completed, Overdue, Archived
    private List<String> tagsname; // Updated to tagsname
    private List<String> assigneesIds; // List of assignee IDs
    private List<String> reminderIds;  // List of reminder IDs
    private String workspaceId;        // Workspace ID

    // Constructors
    public Task() {}

    /**
     * Constructor with all fields
     */
    public Task(int taskID, String title, String description, Date dueDate, Priority priority, Status status,
                List<String> tagsname, List<String> assigneesIds, List<String> reminderIds, String workspaceId) {
        this.taskID = taskID;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.tagsname = tagsname;
        this.assigneesIds = assigneesIds;
        this.reminderIds = reminderIds;
        this.workspaceId = workspaceId;
    }

    // Getters and Setters
    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getTagsname() {
        return tagsname;
    }

    public void setTagsname(List<String> tagsname) {
        this.tagsname = tagsname;
    }

    public List<String> getAssigneesIds() {
        return assigneesIds;
    }

    public void setAssigneesIds(List<String> assigneesIds) {
        this.assigneesIds = assigneesIds;
    }

    public List<String> getReminderIds() {
        return reminderIds;
    }

    public void setReminderIds(List<String> reminderIds) {
        this.reminderIds = reminderIds;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    // Methods (Updated to match the attributes)

    /**
     * Adds a new assignee to the task by ID.
     */
    public void addAssignee(String assigneeId) {
        if (!assigneesIds.contains(assigneeId)) {
            assigneesIds.add(assigneeId);
        }
    }

    /**
     * Removes an assignee from the task by ID.
     */
    public void removeAssignee(String assigneeId) {
        assigneesIds.remove(assigneeId);
    }

    /**
     * Adds a new reminder to the task by ID.
     */
    public void addReminder(String reminderId) {
        if (!reminderIds.contains(reminderId)) {
            reminderIds.add(reminderId);
        }
    }

    /**
     * Updates the status of the task.
     */
    public void updateStatus(Status newStatus) {
        this.status = newStatus;
    }

    /**
     * Marks the task as completed.
     */
    public void markComplete() {
        this.status = Status.Completed;
    }

    /**
     * Adds a tag to the task.
     */
    public void addTagname(String tag) {
        if (!tagsname.contains(tag)) {
            tagsname.add(tag);
        }
    }

    /**
     * Removes a tag from the task.
     */
    public void removeTagname(String tag) {
        tagsname.remove(tag);
    }

    /**
     * Updates details of the task (e.g., title or description).
     */
    public void updateDetails(String title, String description) {
        if (title != null && !title.isEmpty()) {
            this.title = title;
        }
        if (description != null && !description.isEmpty()) {
            this.description = description;
        }
    }
}
