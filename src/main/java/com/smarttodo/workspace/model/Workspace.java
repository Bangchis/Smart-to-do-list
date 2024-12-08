package com.smarttodo.workspace.model;

import java.util.List;

import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;
import com.smarttodo.user.service.UserService;

public class Workspace {
    // Attributes
    private String workspaceID;
    private String name;
    
    private String description;
    private String ownerId;
    private List<String> collaboratorIds;
    private List<Task> tasks;
    private List<String> tags;

    // Constructor
    public Workspace(String workspaceID, String name, String description, String ownerId) {
        this.workspaceID = workspaceID;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
    }

    // Getters and Setters
    public String getWorkspaceID() {
        return workspaceID;
    }

    public void setWorkspaceID(String workspaceID) {
        this.workspaceID = workspaceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return ownerId;
    }

    public void setOwner(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<String> getCollaborators() {
        return collaboratorIds;
    }

    public void setCollaborators(List<String> collaboratorIds) {
        this.collaboratorIds = collaboratorIds;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> taskIds) {
        this.tasks = tasks;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    // Methods
    public void removeTask(int taskID) {
        // Logic to remove a task by taskID
    }

    public void addCollaborator(User user, int role) {
        // Logic to add a collaborator with a specific role
    }

    public void removeCollaborator(int userID) {
        // Logic to remove a collaborator by userID
    }

    public void editTask(Task task) {
        // Logic to edit an existing task
    }

    public void createTask(Task task) {
        // Logic to create a new task
    }

    public static Workspace createWorkspaceInstance(String workspaceId, String name, String description) {
        Workspace workspace = new Workspace(workspaceId, name, description, UserService.getCurrentUser().getUserId());
        System.out.println("Workspace instance created with ID: " + workspaceId);
        return workspace;
    }
    
}


