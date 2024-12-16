package com.smarttodo.workspace.model;

import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;

public class EditorPermissions implements WorkspaceRolePermissions {

    @Override
    public boolean canEdit(User user, Task task) {
        return true;  // Editor can edit tasks
    }

    @Override
    public boolean canCreate(User user) {
        return true;  // Editor can create tasks
    }

    @Override
    public boolean canDelete(User user, Task task) {
        return false; // Editor cannot delete tasks
    }

    @Override
    public boolean canView(User user) {
        return true;  // Editor can view tasks
    }

    @Override
    public boolean canAddCollaborator(User user) {
        return false; // Editor cannot add collaborators
    }

    @Override
    public boolean canRemoveCollaborator(User user) {
        return false; // Editor cannot remove collaborators
    }
}
