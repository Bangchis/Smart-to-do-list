package com.smarttodo.workspace.model;

public enum WorkspaceRole {
    VIEWER, EDITOR, OWNER;

    // Optionally, you can add methods for each role to check permissions.
    public boolean canEdit() {
        return this == EDITOR || this == OWNER;
    }

    public boolean canCreate() {
        return this == EDITOR || this == OWNER;
    }

    public boolean canDelete() {
        return this == OWNER;
    }

    public boolean canInvite() {
        return this == OWNER;
    }
}
