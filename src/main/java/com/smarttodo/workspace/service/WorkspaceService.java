package com.smarttodo.workspace.service;

import com.smarttodo.workspace.model.Workspace;
import com.smarttodo.workspace.model.WorkspaceRole;
import com.smarttodo.user.model.User;


public class WorkspaceService {

    // Add a user to a workspace with a specific role
    public void addUserToWorkspace(Workspace workspace, User user, WorkspaceRole role) {
        // Only owners can add new users with roles
        if (workspace.isOwner(user)) {
            workspace.addUserWithRole(user, role);
        } else {
            System.out.println("Only the owner can add users with roles.");
        }
    }

    // Change a user's role in a workspace
    public void changeUserRoleInWorkspace(Workspace workspace, User user, WorkspaceRole newRole) {
        // Only owners can change roles
        if (workspace.isOwner(user)) {
            workspace.changeUserRole(user, newRole);
        } else {
            System.out.println("Only the owner can change roles.");
        }
    }

    // Get the role of a user in a workspace
    public WorkspaceRole getUserRoleInWorkspace(Workspace workspace, User user) {
        return workspace.getUserRole(user);
    }
}
