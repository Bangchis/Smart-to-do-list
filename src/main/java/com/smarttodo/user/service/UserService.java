package com.smarttodo.user.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.smarttodo.firebase.service.FirebaseAuthentication;
import com.smarttodo.reminder.model.Reminder;
import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;

public class UserService {
    // Static variable to hold the current user instance
    public static User currentUser = null;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static String loginUser(String email, String password) throws Exception {
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
                userDetails.containsKey("workspacesId") ? (List<String>) userDetails.get("workspacesId") : new ArrayList<>(),
                userDetails.containsKey("reminderIds") ? (List<String>) userDetails.get("reminderIds") : new ArrayList<>(),
                userDetails.containsKey("assignedTaskIds") ? (List<String>) userDetails.get("assignedTaskIds") : new ArrayList<>()
            );
    
            // Gán giá trị cho currentUser
            UserService.setCurrentUser(user);
            System.out.println("After creating User instance:\nAssignedTaskIds: " + user.getAssignedTaskIds());
    
            // Lấy tất cả các taskId từ các workspace mà người dùng thuộc về
            Firestore db = FirestoreClient.getFirestore();
            List<String> workspaceIds = user.getWorkspacesId();  // Lấy danh sách ID của các workspace mà người dùng thuộc về
    
            // Duyệt qua từng workspace và lấy các task liên quan từ sub-collection "Task"
            for (String workspaceId : workspaceIds) {
                try {
                    // Truy cập sub-collection "Task" trong Workspace
                    ApiFuture<QuerySnapshot> future = db.collection("Workspace")
                            .document(workspaceId)
                            .collection("Task")
                            .get();
    
                    QuerySnapshot querySnapshot = future.get();
                    List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
                    
                    for (DocumentSnapshot document : documents) {
                        if (document.exists()) {
                            Task task = document.toObject(Task.class);
                            System.out.println("Fetched task: " + task.getTitle() + " with ID: " + task.getTaskID());
                        } else {
                            System.err.println("No tasks found in workspace: " + workspaceId);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error fetching tasks for workspace with ID: " + workspaceId + " - " + e.getMessage());
                    e.printStackTrace();
                }
            }
    
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
            System.out.println("AssignedTaskIds: " + currentUser.getAssignedTaskIds()); // Kiểm tra xem assignedTaskIds đã được cập nhật chưa
        } else {
            System.out.println("No user is currently logged in.");
        }
        return currentUser;
    }

    public static User createUserinstance(String userId, String username, String email, String password, String birthday, int gender, String phoneNumber, List<String> workspacesId, List<String> reminderIds, List<String> assignedTaskIds) {
        System.out.println("Creating User instance with: \n" +
            "UserID: " + userId + "\n" +
            "AssignedTaskIds: " + assignedTaskIds);
        return new User(userId, username, email, password, birthday, gender, phoneNumber, assignedTaskIds, workspacesId, reminderIds);
    }

    public static Map<String, Object> getUserDetails(String userId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentSnapshot> future = db.collection("User").document(userId).get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Map<String, Object> userDetails = document.getData();

            // In ra log để debug
            System.out.println("Fetched User Details: " + userDetails);

            // Khởi tạo mặc định nếu các trường không tồn tại
            if (!userDetails.containsKey("assignedTaskIds") || userDetails.get("assignedTaskIds") == null) {
                userDetails.put("assignedTaskIds", new ArrayList<>());
            }

            return userDetails;
        } else {
            throw new Exception("User not found in Firestore");
        }
    }


/////////////////////////////////////////////////////////////////////////////////////////////
public static List<Task> fetchTaskFromFirestore(User user) throws ExecutionException, InterruptedException {
        List<Task> tasks = new ArrayList<>();
        Firestore db = FirestoreClient.getFirestore();

        for (String taskId : user.getAssignedTaskIds()) {
            String workspaceId = findWorkspaceIdForTask(taskId); // Find the workspace ID for the task
            if (workspaceId != null) {
                DocumentReference taskDocRef = db.collection("Workspace")
                        .document(workspaceId)
                        .collection("Task")
                        .document(taskId);

                ApiFuture<DocumentSnapshot> future = taskDocRef.get();
                DocumentSnapshot document = future.get();
                if (document.exists()) {
                    Task task = document.toObject(Task.class);
                    tasks.add(task);
                } else {
                    System.err.println("Task not found: Task ID " + taskId + " in Workspace " + workspaceId);
                }
            } else {
                System.err.println("Workspace ID not found for Task ID: " + taskId);
            }
        }

        return tasks;
    }

    /**
     * Fetch all reminders associated with the user from Firestore.
     *
     * @param user The current logged-in user.
     * @return A list of Reminder objects.
     * @throws ExecutionException, InterruptedException if fetching reminders fails.
     */
    public static List<Reminder> fetchReminderFromFirestore(User user) throws ExecutionException, InterruptedException {
        List<Reminder> reminders = new ArrayList<>();
        Firestore db = FirestoreClient.getFirestore();

        for (String reminderId : user.getReminderIds()) {
            DocumentReference reminderDocRef = db.collection("User")
                    .document(user.getUserId())
                    .collection("reminders")
                    .document(reminderId);

            ApiFuture<DocumentSnapshot> future = reminderDocRef.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                Reminder reminder = document.toObject(Reminder.class);
                reminders.add(reminder);
            } else {
                System.err.println("Reminder not found: Reminder ID " + reminderId);
            }
        }

        return reminders;
    }

    /**
     * Find the workspace ID for a given task ID.
     *
     * @param taskId The task ID.
     * @return The workspace ID containing the task.
     * @throws ExecutionException, InterruptedException if fetching workspace fails.
     */
    public static String findWorkspaceIdForTask(String taskId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection("Workspace").get();
        QuerySnapshot workspaces = future.get();

        for (QueryDocumentSnapshot workspaceDoc : workspaces.getDocuments()) {
            CollectionReference taskCollection = workspaceDoc.getReference().collection("Task");
            ApiFuture<DocumentSnapshot> taskDoc = taskCollection.document(taskId).get();
            if (taskDoc.get().exists()) {
                return workspaceDoc.getId();
            }
        }

        return null;
    }



}
