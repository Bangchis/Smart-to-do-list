package com.smarttodo.workspace.model;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import com.smarttodo.reminder.model.Reminder;
import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;
import com.smarttodo.user.service.UserService;

public class Workspace {
    // Attributes
    private String workspaceID;
    private String name;
    private String description;
    private String ownerId;
    private Map<User, WorkspaceRole> userRoles;  // Map to store user roles
    private List<Task> tasks;
    private List<String> tags;
    private List<Reminder> reminders;

    // Constructor
    public Workspace(String workspaceID, String name, String description, String ownerId) {
        this.workspaceID = workspaceID;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.userRoles = new HashMap<>();  // Initialize the userRoles map
        this.tasks = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.reminders = new ArrayList<>();
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

    public Map<User, WorkspaceRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Map<User, WorkspaceRole> userRoles) {
        this.userRoles = userRoles;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    // Methods to manage users and roles in workspace
    public void addUserWithRole(User user, WorkspaceRole role) {
        userRoles.put(user, role);
    }

    public void removeUser(User user) {
        userRoles.remove(user);
    }

    public void changeUserRole(User user, WorkspaceRole newRole) {
        if (userRoles.containsKey(user)) {
            userRoles.put(user, newRole);
        }
    }

    public WorkspaceRole getUserRole(User user) {
        return userRoles.getOrDefault(user, WorkspaceRole.VIEWER); // Default to VIEWER if no role assigned
    }

    // Method to fetch the user's role in the workspace
    public WorkspaceRole getUserRoleInWorkspace(User user) {
        return userRoles.getOrDefault(user, WorkspaceRole.VIEWER); // Default to VIEWER if no role assigned
    }

    // Methods for task management
    public void removeTask(int taskID) {
        // Logic to remove a task by taskID
    }

    public void addCollaborator(User user, WorkspaceRole role) {
        // Only owners or editors can add collaborators
        WorkspaceRole userRole = getUserRole(user);
        if (userRole == WorkspaceRole.OWNER || userRole == WorkspaceRole.EDITOR) {
            addUserWithRole(user, role);
        } else {
            System.out.println("Only owners or editors can add collaborators.");
        }
    }

    public void removeCollaborator(User user) {
        // Only owners can remove collaborators
        if (getUserRole(user) == WorkspaceRole.OWNER) {
            removeUser(user);
        } else {
            System.out.println("Only owners can remove collaborators.");
        }
    }

    public void editTask(Task task) {
        // Logic to edit an existing task
    }

    public void createTask(Task task) {
        // Logic to create a new task
    }

    // Static method to create a workspace instance
    public static Workspace createWorkspaceInstance(String workspaceId, String name, String description) {
        Workspace workspace = new Workspace(workspaceId, name, description, UserService.getCurrentUser().getUserId());

        // Set the current user as the owner
        User owner = UserService.getCurrentUser();
        workspace.addUserWithRole(owner, WorkspaceRole.OWNER);

        System.out.println("Workspace instance created with ID: " + workspaceId);
        return workspace;
    }

    // Check if user is the owner of the workspace
    public boolean isOwner(User user) {
        return ownerId.equals(user.getUserId());
    }

    // Check if user has the required role for a task action (edit, create, etc.)
    public boolean canEdit(User user) {
        WorkspaceRole role = getUserRole(user);
        return role == WorkspaceRole.EDITOR || role == WorkspaceRole.OWNER;
    }

    public boolean canView(User user) {
        WorkspaceRole role = getUserRole(user);
        return role == WorkspaceRole.OWNER || role == WorkspaceRole.EDITOR || role == WorkspaceRole.VIEWER;
    }

    // Additional method to check if a user has permission to view the workspace
    public boolean canAccessWorkspace(User user) {
        WorkspaceRole role = getUserRole(user);
        return role != null && (role == WorkspaceRole.OWNER || role == WorkspaceRole.EDITOR || role == WorkspaceRole.VIEWER);
    }
}
