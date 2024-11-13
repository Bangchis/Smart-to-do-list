package com.smarttodo.auth;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseInitialization {

    public static void initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream("path/to/your/firebase-credentials.json");

            FirebaseOptions options = new builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }
}
