package com.smarttodo.workspace.model;

import java.util.List;

import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;

import java.util.List;
import com.smarttodo.user.model.User;
import com.smarttodo.task.model.Task;






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

    public void changeRole(WorkspaceRole role) {

    }

}


enum WorkspaceRole {
    VIEWER, EDITOR, OWNER
}
