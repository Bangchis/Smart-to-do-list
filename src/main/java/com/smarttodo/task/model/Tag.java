package com.smarttodo.task.model;

import java.util.Date;
import java.util.List;
import com.smarttodo.user.model.User;
import com.smarttodo.reminder.model.Reminder;
import com.smarttodo.workspace.model.Workspace;


class Tag {
    private int tagID;
    private String tagName;

    // Constructor
    public Tag(int tagID, String tagName) {
        this.tagID = tagID;
        this.tagName = tagName;
    }

    // Getters and Setters
    public int getTagID() {
        return tagID;
    }

    public void setTagID(int tagID) {
        this.tagID = tagID;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
