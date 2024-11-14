package com.smarttodo;

import com.smarttodo.config.FirebaseConfig;
import com.smarttodo.config.FirebaseAuthentication;

public class Main {
    public static void main(String[] args) {
        // Khởi tạo Firebase
        FirebaseConfig.initializeFirebase();

        // Tạo một người dùng mới
          // Bước 2: Tạo người dùng mới và lưu vào Firestore
          String userId = FirebaseAuthentication.createUser(
            "user22@example.com",
            "strong2-password",
            "Jane2 Doe",
            "2janedoe",     // username
            "1990-01-01",  // birthday
            1,             // gender: giả sử 1 là Nam, 2 là Nữ
            123456789      // phoneNumber
    );

    if (userId != null) {
        System.out.println("User created successfully with UID: " + userId);
    } else {
        System.out.println("Failed to create user.");
    }

        // Lấy thông tin người dùng bằng ID (dùng UID mà bạn đã có)
        FirebaseAuthentication.getUserById(userId);
    }
}
