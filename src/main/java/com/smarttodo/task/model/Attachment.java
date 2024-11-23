package com.smarttodo.task.model;


import java.util.Date;
import java.util.List;
import com.smarttodo.user.model.User;
import com.smarttodo.reminder.model.Reminder;
import com.smarttodo.workspace.model.Workspace;


class Attachment {
    private int attachmentID;
    private String fileType;
    private String fileName;
    private int fileSize;
    private String fileURL;
    private User uploadedBy;
    private Date uploadDate;

    // Constructor
    public Attachment(int attachmentID, String fileType, String fileName, int fileSize, String fileURL, User uploadedBy, Date uploadDate) {
        this.attachmentID = attachmentID;
        this.fileType = fileType;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileURL = fileURL;
        this.uploadedBy = uploadedBy;
        this.uploadDate = uploadDate;
    }

    // Getters and Setters
    public int getAttachmentID() {
        return attachmentID;
    }

    public void setAttachmentID(int attachmentID) {
        this.attachmentID = attachmentID;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    // Methods (to be implemented later)
    public void open() {
        // Logic to be added later
    }

    public void download() {
        // Logic to be added later
    }

    public void delete() {
        // Logic to be added later
    }
}