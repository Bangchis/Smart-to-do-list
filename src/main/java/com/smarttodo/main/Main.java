package com.smarttodo.main;

import com.smarttodo.firebase.service.FirebaseAuthentication;
import com.smarttodo.ui.LoginForm;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.smarttodo.firebase.FirebaseConfig;

public class Main {
    public static void main(String[] args) {
        // Khởi tạo Firebase
        FirebaseConfig.initializeFirebase();

        // Khởi chạy LoginForm
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginForm.setVisible(true);
        });
    }
}
