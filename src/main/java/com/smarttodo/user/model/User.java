package com.smarttodo.user.model;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.smarttodo.reminder.model.Reminder;
import com.smarttodo.user.service.UserService;

import java.util.ArrayList;
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
    protected List<String> assignedTaskIds; // Updated to List<String> assignedTaskIds
    protected List<String> workspacesId;
    protected List<String> reminderIds;

    // Constructors
    public User(String userId, String username, String email, String password, String birthday, int gender, String phoneNumber, List<String> assignedTaskIds, List<String> workspacesID, List<String> reminderIds) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.assignedTaskIds = assignedTaskIds != null ? new ArrayList<>(assignedTaskIds) : new ArrayList<>(); // Update assignedTaskIds initialization
    
        // Kiểm tra và gán workspacesId
        if (workspacesID != null) {
            this.workspacesId = new ArrayList<>(workspacesID);
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

    public List<String> getAssignedTaskIds() {
        return assignedTaskIds;
    }

    public void setAssignedTaskIds(List<String> assignedTaskIds) {
        this.assignedTaskIds = assignedTaskIds;
    }

    public void addAssignedTaskId(String taskId) {
        if (taskId != null && !taskId.isEmpty()) {
            if (this.assignedTaskIds == null) {
                this.assignedTaskIds = new ArrayList<>();
            }
            if (!this.assignedTaskIds.contains(taskId)) {
                this.assignedTaskIds.add(taskId);
                System.out.println("Assigned task added: " + taskId);
            }
        }
    }

    public List<String> getWorkspacesId() {
        return workspacesId;
    }

    public void setWorkspacesId(List<String> workspacesId) {
        this.workspacesId = workspacesId;
    }

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
            reminderDetails.put("workspaceID", reminder.getWorkspaceID());
            reminderDetails.put("recurrencePattern", reminder.getRecurrencePattern());
            reminderDetails.put("dueDate", reminder.getDueDate().toString());
            reminderDetails.put("userId", reminder.getReminderID());
    
            // Add reminder to Firestore sub-collection under User
            DocumentReference userDocRef = db.collection("User").document(this.userId);
            ApiFuture<WriteResult> future = userDocRef.collection("reminders").document(reminderId).set(reminderDetails);
            future.get(); // Wait for the operation to complete
    
            // Add reminderId to user's reminderIds list
            this.reminderIds.add(reminderId);
    
            // Update reminderIds list in Firestore
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("reminderIds", this.reminderIds);
            userDocRef.update(updateData).get(); // Save changes to Firestore
    
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

                // Update workspaceIds list in Firestore
                try {
                    Firestore db = FirestoreClient.getFirestore();
                    DocumentReference userDocRef = db.collection("User").document(this.userId);
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("workspacesId", this.workspacesId);
                    userDocRef.update(updateData).get(); // Save changes to Firestore

                    // Update currentUser
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

            // Add workspace to Firestore collection
            DocumentReference workspaceDocRef = db.collection("Workspace").document(workspaceId);
            workspaceDocRef.set(workspaceDetails).get();

            // Tạo sub-collection "Task" cho Workspace (chưa có task ban đầu)
            workspaceDocRef.collection("Task").document("exampleTaskId").set(new HashMap<>());

            // Add workspaceId to user's workspacesId list and update Firestore
            addWorkspacesId(workspaceId);
        } catch (Exception e) {
            System.err.println("Error while creating workspace: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addTaskToUser(String userId, String taskId) {
        try {
            // Lấy đối tượng User hiện tại
            User user = UserService.getCurrentUser();
            if (user == null || !user.getUserId().equals(userId)) {
                throw new IllegalStateException("Invalid user instance or no user logged in.");
            }
    
            // Kiểm tra xem taskId đã tồn tại trong assignedTaskIds chưa, nếu chưa thì thêm vào
            List<String> assignedTaskIds = user.getAssignedTaskIds();
            if (!assignedTaskIds.contains(taskId)) {
                assignedTaskIds.add(taskId);
    
                // Cập nhật assignedTaskIds vào Firestore
                Firestore db = FirestoreClient.getFirestore();
                DocumentReference userDocRef = db.collection("User").document(userId);
    
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("assignedTaskIds", assignedTaskIds);
                
                // Cập nhật assignedTaskIds trong Firestore
                ApiFuture<WriteResult> future = userDocRef.update(updateData);
                future.get(); // Đợi kết quả cập nhật
                
                // Cập nhật User instance hiện tại
                user.setAssignedTaskIds(assignedTaskIds);
                UserService.setCurrentUser(user);
    
                // Log để debug
                System.out.println("Successfully assigned Task ID: " + taskId + " to User ID: " + userId);
                System.out.println("Updated assignedTaskIds: " + assignedTaskIds);
            } else {
                System.out.println("Task ID: " + taskId + " is already assigned to User ID: " + userId);
            }
        } catch (Exception e) {
            System.err.println("Error adding Task to User: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void addReminderToTask(String workspaceID, String taskID, Reminder reminder) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            String reminderID = UUID.randomUUID().toString(); // Tạo ID duy nhất cho Reminder
            reminder.setReminderID(reminderID);
    
            // Thiết lập chi tiết Reminder
            Map<String, Object> reminderDetails = new HashMap<>();
            reminderDetails.put("reminderID", reminderID);
            reminderDetails.put("taskID", taskID);
            reminderDetails.put("workspaceID", workspaceID);
            reminderDetails.put("recurrencePattern", reminder.getRecurrencePattern());
            reminderDetails.put("dueDate", reminder.getDueDate().toString());
            reminderDetails.put("userID", this.userId);
    
            // Thêm Reminder vào sub-collection "reminders" của User
            DocumentReference userDocRef = db.collection("User").document(this.userId);
            ApiFuture<WriteResult> future = userDocRef.collection("reminders").document(reminderID).set(reminderDetails);
            future.get(); // Chờ ghi thành công
    
            // Cập nhật danh sách reminderIds của User
            if (this.reminderIds == null) {
                this.reminderIds = new ArrayList<>();
            }
            this.reminderIds.add(reminderID);
    
            // Cập nhật reminderIds vào Firestore
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("reminderIds", this.reminderIds);
            userDocRef.update(updateData).get(); // Cập nhật User trong Firestore
    
            // Log để debug
            System.out.println("Reminder added successfully to Task ID: " + taskID + " in Workspace ID: " + workspaceID);
            System.out.println("Updated reminderIds: " + this.reminderIds);
    
        } catch (Exception e) {
            System.err.println("Error adding Reminder to Task: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

}
