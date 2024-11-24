package com.smarttodo.firebase.service;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import com.smarttodo.user.service.UserService;


public class FirebaseAuthentication {

    private static final String FIREBASE_API_KEY = "AIzaSyATUx5uRVgPlAAyAOKqfUOgEks08CKOqrM";  // Thay bằng API Key từ Firebase Console

    // Phương thức để tạo người dùng mới và đồng thời cập nhật thông tin vào Firestore
   

    public static String createUser(String email, String password, String displayName, String username, String birthday, int gender, String phoneNumber) {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setEmailVerified(false)
                .setPassword(password)
                .setDisplayName(displayName)
                .setDisabled(false);
    
        try {
            // Tạo người dùng mới bằng Firebase Authentication
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            System.out.println("Successfully created new user: " + userRecord.getUid());
    
            // Lưu thông tin người dùng vào Firestore
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference userDocRef = db.collection("User").document(userRecord.getUid());
    
            // Tạo các danh sách ban đầu cho reminders và workspaces
            
            List<String> workspaceID = new ArrayList<>();  // Khởi tạo danh sách workspaceID trống
            List<String> reminderListId = new ArrayList<>(Arrays.asList("sampleReminderID"));
            // Đặt thông tin người dùng vào HashMap
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("userID", userRecord.getUid());
            userDetails.put("username", username);
            userDetails.put("email", email);
            userDetails.put("birthday", birthday);
            userDetails.put("gender", gender);
            userDetails.put("phoneNumber", phoneNumber);
            userDetails.put("assignedTasks", new ArrayList<>()); // Khởi tạo danh sách trống
            userDetails.put("reminderIds", reminderListId); // Gán danh sách reminder vào đúng trường
            userDetails.put("workspacesId", workspaceID); // Gán danh sách workspace đúng trường
    
            // Tạo một document mới trong collection "User" với UID làm tên tài liệu
            ApiFuture<WriteResult> future = db.collection("User").document(userRecord.getUid()).set(userDetails);
    
            // Tạo sub-collection "reminders" cho người dùng mới
            Map<String, Object> reminderData = new HashMap<>();
            reminderData.put("reminderID", "sampleReminderID");
            reminderData.put("taskID", "sampleTaskID");
            reminderData.put("recurrencePattern", "None");
            reminderData.put("dueDate", new Date().toString());
            reminderData.put("user", userRecord.getUid());
    
            userDocRef.collection("reminders").document("sampleReminder").set(reminderData);
    
            // Chờ cho đến khi ghi xong vào Firestore và xử lý kết quả
            WriteResult result = future.get();
            System.out.println("User details added to Firestore successfully at: " + result.getUpdateTime());
    
            return userRecord.getUid();  // Trả về UID của người dùng mới
    
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    


    public static String loginUser(String email, String password) throws Exception {
        try {
            // Kiểm tra thông tin đăng nhập
            boolean credentialsValid = verifyUserCredentials(email, password);
            if (credentialsValid) {
                UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
                if (userRecord != null) {
                    System.out.println("Login successful for: " + email);
                    return userRecord.getUid();
                } else {
                    throw new Exception("Invalid credentials");
                }
            } else {
                throw new Exception("Invalid email or password");
            }
        } catch (FirebaseAuthException e) {
            throw new Exception("Login failed: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("Login failed: " + e.getMessage());
        }
    }

    

    

    // Phương thức xác thực thông tin đăng nhập người dùng với email và password
    public static boolean verifyUserCredentials(String email, String password) {
        try {
            // Tạo URL cho Firebase Authentication REST API
            String endpoint = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_API_KEY;
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Tạo payload JSON cho yêu cầu POST
            JsonObject jsonRequest = new JsonObject();
            jsonRequest.addProperty("email", email);
            jsonRequest.addProperty("password", password);
            jsonRequest.addProperty("returnSecureToken", true);

            // Gửi yêu cầu
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(jsonRequest.toString());
            writer.flush();
            writer.close();

            // Kiểm tra mã phản hồi HTTP
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("User authenticated successfully!");
                return true;
            } else {
                System.out.println("Failed to authenticate user. HTTP response code: " + responseCode);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public static void getUserById(String userId) {
        try {
            UserRecord userRecord = FirebaseAuth.getInstance().getUser(userId);
            System.out.println("Successfully fetched user data: " + userRecord.getDisplayName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    
    
    
}

