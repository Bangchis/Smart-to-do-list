package com.smarttodo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;



public class FirebaseConfig {
    public static void initializeFirebase() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                // Đọc file thông tin xác thực từ đường dẫn của bạn
                FileInputStream serviceAccount = new FileInputStream("/mnt/c/Users/Admin/git/repository2/smart-todo-list/src/main/resources/smart-to-do-97045-firebase-adminsdk-bhhg2-71a2f2c322.json");
                
                // Sử dụng builder mới từ FirebaseOptions
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                // Khởi tạo Firebase App
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase App has been initialized.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Firebase App is already initialized.");
        }
    }
}
