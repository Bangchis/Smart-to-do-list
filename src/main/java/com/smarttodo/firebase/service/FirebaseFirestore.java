package com.smarttodo.firebase.service;

import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseFirestore {

    private static Firestore firestoreInstance = null;

    // Private constructor to prevent instantiation
    private FirebaseFirestore() {
    }

    public static Firestore getInstance() {
        if (firestoreInstance == null) {
            synchronized (FirebaseFirestore.class) {
                if (firestoreInstance == null) {
                    try {
                        // Initialize FirebaseApp only once
                        if (FirebaseApp.getApps().isEmpty()) {
                            FileInputStream serviceAccount = new FileInputStream("path/to/your/serviceAccountKey.json");

                            FirebaseOptions options = new FirebaseOptions.Builder()
                                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                    .setProjectId("your-project-id") // Replace with your Firebase project ID
                                    .build();

                            FirebaseApp.initializeApp(options);
                        }

                        // Get the Firestore instance
                        firestoreInstance = FirestoreClient.getFirestore();
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException("Error initializing Firestore: " + e.getMessage());
                    }
                }
            }
        }
        return firestoreInstance;
    }
}
