package com.smarttodo.user.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.smarttodo.task.model.Task; // Nếu class Task nằm trong package này
import com.google.api.client.util.DateTime;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.smarttodo.firebase.service.FirebaseAuthentication;
import com.smarttodo.reminder.model.Reminder;
import com.smarttodo.user.model.User;
import com.smarttodo.workspace.model.Workspace;
import com.google.cloud.Timestamp;

public class UserService {
    // Static variable to hold the current user instance
    private static User currentUser = null;
    
    public static Map<String, Object> getUserDetails(String userId) throws Exception { 
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentSnapshot> future = db.collection("User").document(userId).get();
        DocumentSnapshot document = future.get();
    
        if (document.exists()) {
            Map<String, Object> userDetails = document.getData();
    
            // Kiểm tra và in ra để xác minh dữ liệu
            System.out.println("Fetched User Details: " + userDetails);
    
            if (!userDetails.containsKey("workspacesId") || userDetails.get("workspacesId") == null) {
                userDetails.put("workspacesId", new ArrayList<>());
            } else {
                List<Object> workspaceIds = (List<Object>) userDetails.get("workspacesId");
                List<String> workspaceIdStrings = new ArrayList<>();
                for (Object id : workspaceIds) {
                    workspaceIdStrings.add(String.valueOf(id));
                }
                userDetails.put("workspacesId", workspaceIdStrings);
            }
    
            if (!userDetails.containsKey("reminderIds") || userDetails.get("reminderIds") == null) {
                userDetails.put("reminderIds", new ArrayList<>());
            } else {
                List<Object> reminderIds = (List<Object>) userDetails.get("reminderIds");
                List<String> reminderIdStrings = new ArrayList<>();
                for (Object id : reminderIds) {
                    reminderIdStrings.add(String.valueOf(id));
                }
                userDetails.put("reminderIds", reminderIdStrings);
            }
    
            if (!userDetails.containsKey("assignedTasks") || userDetails.get("assignedTasks") == null) {
                userDetails.put("assignedTasks", new ArrayList<>());
            }
    
            return userDetails;
        } else {
            throw new Exception("User not found in Firestore");
        }
    }
    

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    
    


    public String loginUser(String email, String password) throws Exception { 
        String userId = FirebaseAuthentication.loginUser(email, password);
        
        if (userId != null) {
            // Fetch dữ liệu người dùng từ Firestore để tạo đối tượng User
            Map<String, Object> userDetails = getUserDetails(userId);
    
            // Create User instance
            User user = createUserinstance(
                userId,
                (String) userDetails.get("username"),
                (String) userDetails.get("email"),
                password,
                (String) userDetails.get("birthday"),
                ((Long) userDetails.get("gender")).intValue(),
                (String) userDetails.get("phoneNumber"),
                userDetails.containsKey("assignedTasks") ? (List<Task>) userDetails.get("assignedTasks") : new ArrayList<>(),
                userDetails.containsKey("workspacesId") ? (List<String>) userDetails.get("workspacesId") : new ArrayList<>(),
                userDetails.containsKey("reminderIds") ? (List<String>) userDetails.get("reminderIds") : new ArrayList<>()
            );
    
            // Gán giá trị cho currentUser
            UserService.setCurrentUser(user);
            System.out.println("After creating User instance:\nReminderIds: " + user.getReminderIds());
    
            return userId;
        } else {
            throw new Exception("Login failed: Invalid credentials");
        }
    }
    
    
    
    
    


    // Method to register a new user
    public String registerUser(String email, String password, String username, String birthday, int gender, String phoneNumber) {
        return FirebaseAuthentication.createUser(email, password, username, username, birthday, gender, phoneNumber);
    }

    

    // Method to check if a user is currently logged in
    public static boolean isUserLoggedIn() {
        return currentUser != null;
    }

    public static User getCurrentUser() {
        if (currentUser != null) {
            System.out.println("Current User Details:");
            System.out.println("UserID: " + currentUser.getUserId());
            System.out.println("Username: " + currentUser.getUsername());
            System.out.println("WorkspacesId: " + currentUser.getWorkspacesId()); // Kiểm tra xem workspacesId đã được cập nhật chưa
        } else {
            System.out.println("No user is currently logged in.");
        }
        return currentUser;
    }
    

    // Method to create a User instance
    public static User createUserinstance(String userId, String username, String email, String password, String birthday, int gender, String phoneNumber, List<Task> assignedTasks, List<String> workspacesID, List<String> reminderIds) {
        // Debug: in ra workspacesID để chắc chắn nó có giá trị trước khi truyền vào constructor
        System.out.println("WorkspacesID before creating instance: " + workspacesID);
    
        return new User(userId, username, email, password, birthday, gender, phoneNumber, assignedTasks, workspacesID, reminderIds);
    }
    


    public static Reminder createReminderInstance(String taskID, String recurrencePattern, Date dueDate) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in.");
        }
        String reminderID = UUID.randomUUID().toString(); // Generate a unique ID for the reminder
        return new Reminder(reminderID, taskID, recurrencePattern, dueDate, currentUser);
    
    }

    public static Workspace createWorkspaceInstance(String workspaceId, String name, String description) {
        
        return new Workspace( workspaceId, name, description, currentUser.getUserId());
    }

    public static User getUserData(String userId) throws Exception {
        // Fetch user details from Firestore
        Map<String, Object> userDetails = getUserDetails(userId);
    
        // Create User instance from fetched details
        return createUserinstance(
            userId,
            (String) userDetails.get("username"),
            (String) userDetails.get("email"),
            (String) userDetails.get("password"), // Password handling needs to be secured
            (String) userDetails.get("birthday"),
            ((Long) userDetails.get("gender")).intValue(),
            (String) userDetails.get("phoneNumber"),
            userDetails.containsKey("assignedTasks") ? (List<Task>) userDetails.get("assignedTasks") : new ArrayList<>(),
            userDetails.containsKey("workspacesId") ? (List<String>) userDetails.get("workspacesId") : new ArrayList<>(),
            userDetails.containsKey("reminderIds") ? (List<String>) userDetails.get("reminderIds") : new ArrayList<>()
        );
    }
    
   
    
}
