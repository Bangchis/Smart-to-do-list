package com.smarttodo.user.model;

import com.google.api.client.util.DateTime;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.smarttodo.reminder.model.Reminder;
import com.smarttodo.task.model.Task;
import com.smarttodo.user.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class User {
    private String userId;
    private String username;
    private String email;
    private String password;
    private String birthday;
    private int gender;
    private String phoneNumber;
    protected List<Task> assignedTasks;
    protected List<String> workspacesId;
    protected List<String> reminderIds;

    // Constructors
    public User(String userId, String username, String email, String password, String birthday, int gender, String phoneNumber, List<Task> assignedTasks, List<String> workspacesID, List<String> reminderIds) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.assignedTasks = assignedTasks;
    
        // Kiểm tra và gán workspacesId
        if (workspacesID != null) {
            this.workspacesId = new ArrayList<>(workspacesID); // Đảm bảo copy đúng giá trị, tránh thay đổi không mong muốn
        } else {
            this.workspacesId = new ArrayList<>();
        }
    
        this.reminderIds = reminderIds != null ? new ArrayList<>(reminderIds) : new ArrayList<>();
    }
    

    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Task> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(List<Task> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    public List<String> getWorkspacesId() {
        return workspacesId;
    }

    public void setWorkspacesId(List<String> workspacesId) {
        this.workspacesId = workspacesId;
    }

    //##################################################

    public List<String> getReminderIds() {
        return reminderIds;
    }

    public void setReminderIds(List<String> reminderIds) {
        this.reminderIds = reminderIds;
    }

    // Method to add a reminder and save it to Firestore
    
    public void addReminder(Reminder reminder) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            String reminderId = UUID.randomUUID().toString();
            reminder.setReminderID(reminderId);
            
            // Create reminder details map
            Map<String, Object> reminderDetails = new HashMap<>();
            reminderDetails.put("reminderID", reminder.getReminderID());
            reminderDetails.put("taskID", reminder.getTaskID());
            reminderDetails.put("recurrencePattern", reminder.getRecurrencePattern());
            reminderDetails.put("dueDate", reminder.getDueDate().toString());
            reminderDetails.put("user", reminder.getUser().getUserId());
            
            // Add reminder to Firestore sub-collection
            DocumentReference userDocRef = db.collection("User").document(this.userId);
            ApiFuture<WriteResult> future = userDocRef.collection("reminders").document(reminderId).set(reminderDetails);
            future.get(); // Wait for the operation to complete
    
            // Add reminderId to user's reminderIds list
            this.reminderIds.add(reminderId);
    
            // Update reminderIds list in Firestore
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("reminderIds", this.reminderIds);
            userDocRef.update(updateData).get(); // Lưu lại thay đổi vào Firestore
    
            // Update currentUser
            UserService.setCurrentUser(this);
            System.out.println("Updated currentUser with new reminderIds: " + this.reminderIds);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void addWorkspacesId(String workspaceId) {
        if (workspaceId != null && !workspaceId.isEmpty()) {
            if (this.workspacesId == null) {
                this.workspacesId = new ArrayList<>();
            }
            if (!this.workspacesId.contains(workspaceId)) {
                this.workspacesId.add(workspaceId);
    
                // Cập nhật danh sách workspaceIds trong Firestore
                try {
                    Firestore db = FirestoreClient.getFirestore();
                    DocumentReference userDocRef = db.collection("User").document(this.userId);
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("workspacesId", this.workspacesId);
                    userDocRef.update(updateData).get(); // Lưu lại thay đổi vào Firestore
    
                    // Cập nhật currentUser
                    UserService.setCurrentUser(this);
                    System.out.println("Updated currentUser with new workspacesId: " + this.workspacesId);
                } catch (Exception e) {
                    System.err.println("Error updating workspacesId list: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    

    

    

public void createnewWorkspace(String workspaceId, String name, String description) {
    try {
        Firestore db = FirestoreClient.getFirestore();

        // Create workspace details map
        Map<String, Object> workspaceDetails = new HashMap<>();
        workspaceDetails.put("workspaceID", workspaceId);
        workspaceDetails.put("name", name);
        workspaceDetails.put("description", description);
        workspaceDetails.put("ownerId", this.userId);
        workspaceDetails.put("collaboratorIds", new ArrayList<String>());
        workspaceDetails.put("taskIds", new ArrayList<String>());

        // Add workspace to Firestore collection
        DocumentReference workspaceDocRef = db.collection("Workspace").document(workspaceId);
        ApiFuture<WriteResult> future = workspaceDocRef.set(workspaceDetails);
        future.get(); // Wait for the operation to complete

        // Add workspaceId to user's workspacesId list and update Firestore
        addWorkspacesId(workspaceId);

    } catch (Exception e) {
        System.err.println("Error while creating workspace: " + e.getMessage());
        e.printStackTrace();
    }
}


}