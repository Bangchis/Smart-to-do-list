package com.smarttodo.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import com.smarttodo.firebase.service.FirebaseAuthentication;
import com.smarttodo.firebase.FirebaseConfig;
import com.smarttodo.user.model.User;
import com.smarttodo.user.service.UserService;

public class LoginForm extends JFrame {

    private BufferedImage backgroundImage;

    public LoginForm() {
        FirebaseConfig.initializeFirebase(); // Initialize Firebase

        // Frame settings
        setTitle("Login");
        setMinimumSize(new Dimension(800, 500)); // Set a minimum size for better layout at small sizes
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("/Users/thanhlamnguyen/Downloads/DEV/project OOP/test_oop/src/main/resources/oopbackground.jpeg"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Main panel with GridBagLayout for centering login panel
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    // Scale image to fit the panel size
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        mainPanel.setBackground(Color.WHITE);

        // Login panel in the center
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(255, 255, 255, 200)); // Semi-transparent background
        loginPanel.setPreferredSize(new Dimension(300, 300)); // Set preferred size for the login panel

        // GridBagConstraints for layout positioning
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title label
        JLabel titleLabel = new JLabel("Use your time better");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        loginPanel.add(titleLabel, gbc);

        Font calibriLight = new Font("Calibri Light", Font.PLAIN, 14);
        // Username field
        gbc.gridy++;
        JTextField usernameField = new JTextField(15); // Reduced field width for a balanced look
        usernameField.setFont(calibriLight);
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));
        loginPanel.add(usernameField, gbc);

        // Password field
        gbc.gridy++;
        JPasswordField passwordField = new JPasswordField(15); // Reduced field width for balance
        passwordField.setFont(calibriLight);
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        loginPanel.add(passwordField, gbc);

        // Login button
        gbc.gridy++;
        JButton loginButton = new JButton("ENTER");
        loginButton.setPreferredSize(new Dimension(150, 30)); // Set preferred size for button
        loginButton.setBackground(new Color(34, 47, 62));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginPanel.add(loginButton, gbc);

        // Create account label (No action for now, can be linked to the registration page later)
        gbc.gridy++;
        JLabel createAccountLabel = new JLabel("Create your account");
        createAccountLabel.setForeground(new Color(100, 100, 100));
        loginPanel.add(createAccountLabel, gbc);

        // Center login panel on main panel
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(loginPanel, mainGbc);

        // Add main panel to frame
        add(mainPanel);

        // Button action listener for login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter both username and password.");
                } else {
                    try {
                        // Call the Firebase authentication method
                        String userId = FirebaseAuthentication.loginUser(username, password);

                        if (userId != null) {
                            // Fetch user data
                            User userData = UserService.getUserData(userId);

                            if (userData != null) {
                                // Successful login and user data fetched
                                JOptionPane.showMessageDialog(null, "Login successful! Welcome!");

                                // Open homepage and pass user data
                                new Homepage(userData).setVisible(true);
                                dispose(); // Close the login form
                            } else {
                                // If user data not found
                                JOptionPane.showMessageDialog(null, "Failed to fetch user data.");
                            }
                        } else {
                            // If login fails
                            JOptionPane.showMessageDialog(null, "Invalid username or password.");
                        }
                    } catch (Exception ex) {
                        // Handle Firebase login exceptions
                        JOptionPane.showMessageDialog(null, "Login failed. " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        });
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}
