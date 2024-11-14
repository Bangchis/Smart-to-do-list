package com.smarttodo.user;

import java.util.Date;
import java.util.List;
import com.smarttodo.task.Task;


public class User {
    private int userID;
    private String username;
    private String email;
    private String password;
    private int phoneNumber;
    private int gender;
    private Date dateOfBirth;
    private List<Task> assignedTasks;

    // Constructor
    public User(int userID, String username, String email, String password, int phoneNumber, int gender, Date dateOfBirth) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    
    // Getters and Setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

//    public List<Task> getAssignedTasks() {
//        return assignedTasks;
//    }
//
//    public void setAssignedTasks(List<Task> assignedTasks) {
//        this.assignedTasks = assignedTasks;
//    }
//
//    // Methods
//    public void createWorkspace() {
//        // Method to create a workspace (to be implemented)
//    }
//
//    public void addReminders() {
//        // Method to add reminders (to be implemented)
//    }
//
//    public void viewNotifications() {
//        // Method to view notifications (to be implemented)
//    }
}
