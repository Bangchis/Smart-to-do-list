package com.smarttodo.workspace.model;

import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;

public class OwnerPermissions implements WorkspaceRolePermissions {

    @Override
    public boolean canEdit(User user, Task task) {
        return true;  // Owner can edit tasks
    }

    @Override
    public boolean canCreate(User user) {
        return true;  // Owner can create tasks
    }

    @Override
    public boolean canDelete(User user, Task task) {
        return true;  // Owner can delete tasks
    }

    @Override
    public boolean canView(User user) {
        return true;  // Owner can view tasks
    }

    @Override
    public boolean canAddCollaborator(User user) {
        return true;  // Owner can add collaborators
    }

    @Override
    public boolean canRemoveCollaborator(User user) {
        return true;  // Owner can remove collaborators
    }
}
