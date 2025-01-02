package com.smarttodo.workspace.model;

import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;

public interface WorkspaceRolePermissions {

    boolean canEdit(User user, Task task);     // Check if user can edit a task
    boolean canCreate(User user);              // Check if user can create a task
    boolean canDelete(User user, Task task);   // Check if user can delete a task
    boolean canView(User user);                // Check if user can view tasks
    boolean canAddCollaborator(User user);     // Check if user can add collaborators
    boolean canRemoveCollaborator(User user);  // Check if user can remove collaborators
}
