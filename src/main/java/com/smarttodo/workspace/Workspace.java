package com.smarttodo.workspace;

import java.util.List;
import com.smarttodo.user.User;
import com.smarttodo.task.Task;

public class Workspace {
    private int workspaceID;
    private String name;
    private String description;
    private User owner;
    private List<UserWorkspaceRole> userRoles;
    private List<Task> tasks;

    // Constructor
    public Workspace(int workspaceID, String name, String description, User owner) {
        this.workspaceID = workspaceID;
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    // Getters and Setters
    public int getWorkspaceID() {
        return workspaceID;
    }

    public void setWorkspaceID(int workspaceID) {
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<UserWorkspaceRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserWorkspaceRole> userRoles) {
        this.userRoles = userRoles;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    // Method to get all tasks in the workspace (for system-wide operations)
    public static List<Task> getAllTasks() {
        // Replace with actual logic to retrieve all tasks in the workspace
        return null;
    }
}

class UserWorkspaceRole {
    private User user;
    private Workspace workspace;
    private WorkspaceRole role;

    // Constructor
    public UserWorkspaceRole(User user, Workspace workspace, WorkspaceRole role) {
        this.user = user;
        this.workspace = workspace;
        this.role = role;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public WorkspaceRole getRole() {
        return role;
    }

    public void setRole(WorkspaceRole role) {
        this.role = role;
    }
}

enum WorkspaceRole {
    VIEWER, EDITOR, OWNER
}
