package com.smarttodo.task;

import java.util.Date;
import java.util.List;
import com.smarttodo.user.User;

// data types
enum Priority {
    HIGH, MEDIUM, LOW
}

enum Status {
    PENDING, COMPLETED, OVERDUE
}

enum RelationshipType {
     SUBTASK, RELATEDTASK
}

enum ReminderType {
    ONETIME, RECURRING
}

public class Task {
    private int taskID;
    private String title;
    private String description;
    private Date dueDate;
    private Priority priority; // High, Medium, Low
    private Status status; // Pending, Completed, Overdue
    private List<User> assignees;
    private List<Attachment> attachments;
    private List<Reminder> reminders;
    private List<TaskRelationship> relationships;
    private List<Tag> tags;
//    private Workspace workspace;

    // Constructor
    public Task(int taskID, String title, String description, Date dueDate, Priority priority, Status status) {
        this.taskID = taskID;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
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

    public List<User> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<User> assignees) {
        this.assignees = assignees;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    public List<TaskRelationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<TaskRelationship> relationships) {
        this.relationships = relationships;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

//    public Workspace getWorkspace() {
//        return workspace;
//    }
//
//    public void setWorkspace(Workspace workspace) {
//        this.workspace = workspace;
//    }

    // Methods
    /**
     * Adds a new assignee to the task's list of assignees.
     * Ensures the task is assigned to an additional user.
     * @param user The user to be assigned to the task.
     */
    public void addAssignee(User user) {
        this.assignees.add(user);
    }

    /**
     * Removes an assignee from the task's list of assignees based on the user ID.
     * If the user ID is not found, it prints a message indicating so.
     * @param userID The ID of the user to be removed.
     */
    public void removeAssignee(int userID) {
        boolean removed = this.assignees.removeIf(user -> user.getUserID() == userID);
        if (!removed) {
            System.out.println("User with ID " + userID + " not found.");
        } else {
            System.out.println("User with ID " + userID + " has been successfully removed.");
        }
    }

    /**
     * Adds an attachment to the task's list of attachments.
     * Useful for linking documents or files relevant to the task.
     * @param attachment The attachment to be added.
     */
    public void addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
    }

    /**
     * Removes an attachment from the task's list of attachments based on the attachment ID.
     * If the attachment ID is not found, it prints a message indicating so.
     * @param attachmentID The ID of the attachment to be removed.
     */
    public void removeAttachment(int attachmentID) {
        boolean removed = this.attachments.removeIf(attachment -> attachment.getAttachmentID() == attachmentID);
        if (!removed) {
            System.out.println("Attachment with ID " + attachmentID + " not found.");
        } else {
            System.out.println("Attachment with ID " + attachmentID + " has been successfully removed.");
        }
    }

    /**
     * Adds a new reminder to the task.
     * Helps in notifying users about the task at specific times.
     * @param reminder The reminder to be added.
     */
    public void addReminder(Reminder reminder) {
        this.reminders.add(reminder);
    }

    /**
     * Updates the status of the task.
     * This can be used to track the progress of the task.
     * @param newStatus The new status to set for the task.
     */
    public void updateStatus(Status newStatus) {
        this.status = newStatus;
    }

    /**
     * Marks the task as completed by setting the status to 'COMPLETED'.
     */
    public void markComplete() {
        this.status = Status.COMPLETED;
    }

    /**
     * Adds a new tag to the task.
     * Tags are used to categorize or label tasks for easier identification.
     * @param tag The tag to be added to the task.
     */
    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    /**
     * Removes a tag from the task's list of tags based on the tag ID.
     * If the tag ID is not found, it prints a message indicating so.
     * @param tagID The ID of the tag to be removed.
     */
    public void removeTag(int tagID) {
        boolean removed = this.tags.removeIf(tag -> tag.getTagID() == tagID);
        if (!removed) {
            System.out.println("Tag with ID " + tagID + " not found.");
        } else {
            System.out.println("Tag with ID " + tagID + " has been successfully removed.");
        }
    }

    /**
     * Adds a relationship to the task, such as marking another task as a subtask or related task.
     * @param relationship The relationship to be added to the task.
     */
    public void addRelationship(TaskRelationship relationship) {
        this.relationships.add(relationship);
    }

    /**
     * Removes a relationship from the task's list of relationships based on the relationship ID.
     * If the relationship ID is not found, it prints a message indicating so.
     * @param relationshipID The ID of the relationship to be removed.
     */
    public void removeRelationship(int relationshipID) {
        boolean removed = this.relationships.removeIf(relationship -> relationship.getRelationshipID() == relationshipID);
        if (!removed) {
            System.out.println("Relationship with ID " + relationshipID + " not found.");
        } else {
            System.out.println("Relationship with ID " + relationshipID + " has been successfully removed.");
        }
    }
}

class Attachment {
    // Constructor
    public Attachment(int attachmentID, String fileName, String fileType, Date uploadedDate, User uploadedBy) {
        this.attachmentID = attachmentID;
        this.fileName = fileName;
        this.fileType = fileType;
        this.uploadedDate = uploadedDate;
        this.uploadedBy = uploadedBy;
    }
    private int attachmentID;
    private String fileName;
    private String fileType;
    private Date uploadedDate;
    private User uploadedBy;

    // Constructor, getters và setters
    public int getAttachmentID() {
        return attachmentID;
    }

    public void open() {
        // Mở file
    }

    public void download() {
        // Tải xuống file
    }

    public void delete() {
        // Xóa file
    }
}

class Reminder {
    
    private int reminderID;
    private Date reminderTime;
    private boolean recurring;
    private User user;
    private ReminderType reminderType;
    private String recurrencePattern;

  // Constructor
    public Reminder(int reminderID, Date reminderTime, boolean recurring, User user, ReminderType reminderType, String recurrencePattern) {
        this.reminderID = reminderID;
        this.reminderTime = reminderTime;
        this.recurring = recurring;
        this.user = user;
        this.reminderType = reminderType;
        this.recurrencePattern = recurrencePattern;
    }

    // Constructor, getters và setters
    /**
     * Edits the reminder details, allowing modification of the reminder type, recurrence pattern, and reminder time.
     * @param newType The new type of the reminder.
     * @param newRecurrencePattern The new recurrence pattern for the reminder.
     * @param newReminderTime The new time for the reminder.
     */
    public void edit(ReminderType newType, String newRecurrencePattern, Date newReminderTime) {
        this.reminderType = newType;
        this.recurrencePattern = newRecurrencePattern;
        this.reminderTime = newReminderTime;
    }

    /**
     * Schedules or updates the recurrence pattern and reminder time for the reminder.
     * @param newRecurrencePattern The new recurrence pattern for the reminder.
     * @param newReminderTime The new time for the reminder.
     */
    public void schedule(String newRecurrencePattern, Date newReminderTime) {
        this.recurrencePattern = newRecurrencePattern;
        this.reminderTime = newReminderTime;
    }

    /**
     * Cancels the reminder by removing it from the task's list of reminders.
     * @param task The task from which the reminder is to be removed.
     */
    public void cancel(Task task) {
        boolean removed = task.getReminders().removeIf(reminder -> reminder.getReminderID() == this.reminderID);
        if (!removed) {
            System.out.println("Reminder with ID " + this.reminderID + " not found in the task.");
        } else {
            System.out.println("Reminder with ID " + this.reminderID + " has been successfully removed from the task.");
        }
    }

    public int getReminderID() {
        return reminderID;
    }
}

class TaskRelationship {
    // Constructor
    public TaskRelationship(int relationshipID, RelationshipType type, Task sourceTask, Task targetTask) {
        this.relationshipID = relationshipID;
        this.type = type;
        this.sourceTask = sourceTask;
        this.targetTask = targetTask;
    }
    private int relationshipID;
    private RelationshipType type; // SubTask, RelatedTask
    private Task sourceTask;
    private Task targetTask;

    // Constructor, getters và setters
    public int getRelationshipID() {
        return relationshipID;
    }
}

class Tag {
    
    private int tagID;
    private String name;

    // Constructor, getters và setters
    public int getTagID() {
        return tagID;
    }

  // Constructor
    public Tag(int tagID, String name) {
        this.tagID = tagID;
        this.name = name;
    }
    /**
     * Edits the tag details.
     */
    
    /**
     * Cancels or removes the tag.
     */
    private void cancel(int tagID) {
        // Hủy thẻ tag
    }
}

