package com.smarttodo.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;



public class FirebaseConfig {
    public static void initializeFirebase() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                // Đọc file thông tin xác thực từ đường dẫn của bạn
                FileInputStream serviceAccount = new FileInputStream("src/main/resources/smart-to-do-97045-firebase-adminsdk-bhhg2-c57af36c19.json");
                
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
