package com.smarttodo.user.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.smarttodo.task.model.Task; // Nếu class Task nằm trong package này
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
    
            // Chuyển đổi birthday từ Timestamp về Date nếu cần
            if (userDetails.containsKey("birthday")) {
                Object birthdayValue = userDetails.get("birthday");
                if (birthdayValue instanceof Timestamp) {
                    Timestamp timestamp = (Timestamp) birthdayValue;
                    Date birthday = timestamp.toDate();
                    userDetails.put("birthday", birthday);
                }
            }
    
            // Khởi tạo mặc định nếu các trường không tồn tại và đảm bảo kiểu dữ liệu chính xác
            if (!userDetails.containsKey("workspacesId") || userDetails.get("workspacesId") == null) {
                userDetails.put("workspacesId", new ArrayList<String>());
            } else {
                // Chuyển đổi sang List<String> nếu cần thiết
                List<Object> workspaceIds = (List<Object>) userDetails.get("workspacesId");
                List<String> workspaceIdStrings = new ArrayList<>();
                for (Object id : workspaceIds) {
                    workspaceIdStrings.add(String.valueOf(id));
                }
                userDetails.put("workspacesId", workspaceIdStrings);
            }
    
            if (!userDetails.containsKey("reminderIds") || userDetails.get("reminderIds") == null) {
                userDetails.put("reminderIds", new ArrayList<String>());
            } else {
                // Chuyển đổi sang List<String> nếu cần thiết
                List<Object> reminderIds = (List<Object>) userDetails.get("reminderIds");
                List<String> reminderIdStrings = new ArrayList<>();
                for (Object id : reminderIds) {
                    reminderIdStrings.add(String.valueOf(id));
                }
                userDetails.put("reminderIds", reminderIdStrings);
            }
    
            if (!userDetails.containsKey("assignedTasks") || userDetails.get("assignedTasks") == null) {
                userDetails.put("assignedTasks", new ArrayList<Task>());
            } else {
                // Nếu `assignedTasks` là một danh sách, có thể bạn cần xử lý chuyển đổi kiểu ở đây.
                // Đây là trường hợp phức tạp vì cần chuyển từ dữ liệu Firestore sang đối tượng Task.
                // Để đơn giản, chúng ta có thể để lại như cũ nếu bạn chưa có logic cụ thể để chuyển đổi.
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
        // Đăng nhập người dùng bằng Firebase Authentication
        String userId = FirebaseAuthentication.loginUser(email, password);
        
        if (userId != null) {
            // Fetch dữ liệu người dùng từ Firestore để tạo đối tượng User
            Map<String, Object> userDetails = getUserDetails(userId);
    
            // Extract all details needed for creating a User instance
            String username = (String) userDetails.get("username");
            String fetchedPassword = password; // Không cần fetch mật khẩu từ Firestore vì nó đã có từ đăng nhập
            String emailFetched = (String) userDetails.get("email");
            String birthday = (String) userDetails.get("birthday");
            int gender = ((Long) userDetails.get("gender")).intValue();
            String phoneNumber = (String) userDetails.get("phoneNumber");
    
            // Thêm việc kiểm tra nếu workspacesId có tồn tại trong userDetails
            List<String> workspacesID = userDetails.containsKey("workspacesId") ? 
                                        (List<String>) userDetails.get("workspacesId") : new ArrayList<>();
    
            // Thêm việc kiểm tra nếu assignedTasks có tồn tại trong userDetails
            List<Task> assignedTasks = userDetails.containsKey("assignedTasks") ? 
                                        (List<Task>) userDetails.get("assignedTasks") : new ArrayList<>();
                                        
            List<String> reminderIds = userDetails.containsKey("reminderIds") ? 
                                        (List<String>) userDetails.get("reminderIds") : new ArrayList<>();
    
            // Create User instance
            User user = createUserinstance(
                userId,
                username,
                emailFetched,
                fetchedPassword,
                birthday,
                gender,
                phoneNumber,
                assignedTasks,
                workspacesID,
                reminderIds
            );
    
            // Save user instance as current user
            currentUser = user;
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

    // Method to get the current user instance
    public static User getCurrentUser() {
        if (currentUser != null) {
            System.out.println("Current User Details:");
            System.out.println("UserID: " + currentUser.getUserId());
            System.out.println("Username: " + currentUser.getUsername());
            System.out.println("Email: " + currentUser.getEmail());
            System.out.println("PhoneNumber: " + currentUser.getPhoneNumber());
            // Add any other information you want to print here
        } else {
            System.out.println("No user is currently logged in.");
        }
        return currentUser;
    }

    


    // Method to create a User instance
public static User createUserinstance(String userId, String username, String email, String password, String birthday, int gender, String phoneNumber, List<Task> assignedTasks, List<String> workspacesId, List<String> reminderIds) {
    // Kiểm tra và khởi tạo danh sách nếu null
    if (assignedTasks == null) {
        assignedTasks = new ArrayList<>();
    }
    if (workspacesId == null) {
        workspacesId = new ArrayList<>();
    }
    if (reminderIds == null) {
        reminderIds = new ArrayList<>();
    }

    return new User(userId, username, email, password, birthday, gender, phoneNumber, assignedTasks, workspacesId, reminderIds);
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
   
    
}
