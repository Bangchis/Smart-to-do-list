package com.smarttodo.task.model;

import java.util.Date;
import java.util.List;

// Enums for Priority and Status




public class Task {
    private String taskID; // Firestore-compatible ID
    private String title;
    private String description;
    private Date dueDate; // Date type
    private Priority priority; // Enum: HIGH, MEDIUM, LOW
    private Status status; // Enum: NEW, PENDING, COMPLETED, etc.
    private List<String> tagsname; // Tags associated with the task
    private List<String> assigneesIds; // List of assignee IDs
    private List<String> reminderIds; // List of reminder IDs
    private String workspaceId; // ID of the workspace the task belongs to

    // Constructors
    public Task() {
        // Default constructor
    }

    /**
     * Constructor with all fields.
     */
    public Task(String taskID, String title, String description, Date dueDate, Priority priority, Status status,
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

    //test task 

    public Task(String taskID, String title, String description, String workspaceId) {
        this.taskID = taskID;
        this.title = title;
        this.description = description;
        
        this.workspaceId = workspaceId;
    }

    // Getters and Setters
    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
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

    // Additional methods
    public void addAssignee(String assigneeId) {
        if (!assigneesIds.contains(assigneeId)) {
            assigneesIds.add(assigneeId);
        }
    }

    public void removeAssignee(String assigneeId) {
        assigneesIds.remove(assigneeId);
    }

    public void addReminder(String reminderId) {
        if (!reminderIds.contains(reminderId)) {
            reminderIds.add(reminderId);
        }
    }

    public void removeReminder(String reminderId) {
        reminderIds.remove(reminderId);
    }

    public void updateStatus(Status newStatus) {
        this.status = newStatus;
    }

    public void markComplete() {
        this.status = Status.Completed;
    }

    public void addTagname(String tag) {
        if (!tagsname.contains(tag)) {
            tagsname.add(tag);
        }
    }

    public void removeTagname(String tag) {
        tagsname.remove(tag);
    }

    public void updateDetails(String title, String description) {
        if (title != null && !title.isEmpty()) {
            this.title = title;
        }
        if (description != null && !description.isEmpty()) {
            this.description = description;
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskID='" + taskID + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", status=" + status +
                ", tagsname=" + tagsname +
                ", assigneesIds=" + assigneesIds +
                ", reminderIds=" + reminderIds +
                ", workspaceId='" + workspaceId + '\'' +
                '}';
    }
}