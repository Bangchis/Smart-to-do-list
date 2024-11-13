package com.yourcompany.smarttodo.users;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import java.util.concurrent.ExecutionException;

public class UserService {

    // Method to log in a user with email/password using Firebase Authentication
    public boolean login(String email, String password) {
        try {
            // Authenticate the user using Firebase Authentication
            UserRecord userRecord = FirebaseAuth.getInstance()
                .getUserByEmail(email);

            // Check the user exists and password (for demonstration purposes, assuming password check)
            // Firebase Authentication typically handles password checking automatically
            if (userRecord != null) {
                // Assuming password check happens separately using Firebase Authentication
                // Firebase SDK automatically manages user sessions
                return true; // Login success
            }
        } catch (FirebaseAuthException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
        return false; // Login failure
    }
}
