package com.smarttodo.workspace.model;

import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;

public class ViewerPermissions implements WorkspaceRolePermissions {

    @Override
    public boolean canEdit(User user, Task task) {
        return false; // Viewer cannot edit tasks
    }

    @Override
    public boolean canCreate(User user) {
        return false; // Viewer cannot create tasks
    }

    @Override
    public boolean canDelete(User user, Task task) {
        return false; // Viewer cannot delete tasks
    }

    @Override
    public boolean canView(User user) {
        return true;  // Viewer can view tasks
    }

    @Override
    public boolean canAddCollaborator(User user) {
        return false; // Viewer cannot add collaborators
    }

    @Override
    public boolean canRemoveCollaborator(User user) {
        return false; // Viewer cannot remove collaborators
    }
}
