package com.smarttodo.ui;

import javax.swing.*;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.smarttodo.firebase.FirebaseConfig;
import com.smarttodo.firebase.service.FirebaseAuthentication;
import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;
import com.smarttodo.user.service.UserService;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;  // For generic List


public class rf extends JFrame {

    private JTextField emailField, usernameField, genderField, phoneNumberField;
    private JPasswordField passwordField;
    private JSpinner birthdaySpinner;
    private JButton registerButton, loginButton;

    public rf() {

        FirebaseConfig.initializeFirebase();
        // Frame settings
        setTitle("Register");
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);

        // Left panel for the registration form
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(new Color(255, 255, 255, 150)); // Semi-transparent white
        leftPanel.setBounds(0, 0, getWidth() / 2, getHeight());

        // Registration panel
        JPanel registrationPanel = new JPanel(new GridBagLayout());
        registrationPanel.setBackground(new Color(255, 255, 255, 200)); // Opaque white background

        // Title label
        JLabel titleLabel = new JLabel("Register", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));

        // GridBagLayout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 20, 10, 20); // Add padding
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // Add components to the registration panel
        gbc.gridy = 0;
        registrationPanel.add(titleLabel, gbc);

        // Username field
        gbc.gridy++;
        usernameField = new JTextField();
        usernameField.setFont(new Font("Calibri", Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));
        registrationPanel.add(usernameField, gbc);

        // Phone number field
        gbc.gridy++;
        phoneNumberField = new JTextField();
        phoneNumberField.setFont(new Font("Calibri", Font.PLAIN, 16));
        phoneNumberField.setBorder(BorderFactory.createTitledBorder("Phone Number"));
        registrationPanel.add(phoneNumberField, gbc);

        // Email field
        gbc.gridy++;
        emailField = new JTextField();
        emailField.setFont(new Font("Calibri", Font.PLAIN, 16));
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));
        registrationPanel.add(emailField, gbc);

        // Date of Birth field using JSpinner
        gbc.gridy++;
        JLabel dobLabel = new JLabel("Date of Birth (YYYY-MM-DD)");
        registrationPanel.add(dobLabel, gbc);

        // Set up SpinnerDateModel for Date of Birth
        birthdaySpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor birthdayEditor = new JSpinner.DateEditor(birthdaySpinner, "yyyy-MM-dd");
        birthdaySpinner.setEditor(birthdayEditor);
        birthdaySpinner.setFont(new Font("Calibri", Font.PLAIN, 16));

        gbc.gridy++;
        registrationPanel.add(birthdaySpinner, gbc);

        // Gender ComboBox (for 1 = Male, 2 = Female)
        gbc.gridy++;
        genderField = new JTextField();
        genderField.setFont(new Font("Calibri", Font.PLAIN, 16));
        genderField.setBorder(BorderFactory.createTitledBorder("Gender (1 = Male, 2 = Female)"));
        registrationPanel.add(genderField, gbc);

        // Password field
        gbc.gridy++;
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Calibri", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        registrationPanel.add(passwordField, gbc);

        // Register button
        gbc.gridy++;
        registerButton = new JButton("REGISTER");
        registerButton.setPreferredSize(new Dimension(150, 40));
        registerButton.setBackground(new Color(34, 47, 62));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);
        registrationPanel.add(registerButton, gbc);

        // Login button (switch to login page)
        gbc.gridy++;
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(150, 40));
        loginButton.setBackground(new Color(34, 47, 62));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        registrationPanel.add(loginButton, gbc);

        // Add action listeners
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String phone = phoneNumberField.getText();
                String gender = genderField.getText();  // Assume gender is entered as a string '1' or '2'
                Date dob = (Date) birthdaySpinner.getValue(); // Get the selected date from JSpinner

                // Convert the Date object to a String
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dobString = sdf.format(dob); // Format the date to a string

                if (username.isEmpty() || password.isEmpty() || email.isEmpty() || dob == null || phone.isEmpty() || gender.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Make sure you filled in all required fields.");
                    return;
                }

                // Validate email format
                if (!isValidEmail(email)) {
                    JOptionPane.showMessageDialog(null, "Invalid email format. Please enter a valid email.");
                    return;
                }

                // Validate password length
                if (password.length() < 8) {
                    JOptionPane.showMessageDialog(null, "Password must be at least 8 characters long.");
                    return;
                }

                // Validate gender value (must be 1 or 2)
                int genderInt = Integer.parseInt(gender);
                if (genderInt != 1 && genderInt != 2) {
                    JOptionPane.showMessageDialog(null, "Gender must be 1 (Male) or 2 (Female).");
                    return;
                }

                


// Attempt to create the user in Firebase
String userId = FirebaseAuthentication.createUser(email, password, "Lam", username, dobString, genderInt, phone);

if (userId != null) {
    // Successful user creation
    System.out.println("User created successfully with ID: " + userId);
    JOptionPane.showMessageDialog(null, "User created successfully: " + userId);

    try {
        // Attempt to log the user in immediately after registration
        String loginUserId = FirebaseAuthentication.loginUser(email, password);
        
        if (loginUserId != null) {
            // Login successful, loginUserId will contain the user ID
            System.out.println("User logged in successfully with ID: " + loginUserId);
            JOptionPane.showMessageDialog(null, "Login successful. Welcome to the Homepage!");

            // Fetch user details from Firestore (or another source)
            Map<String, Object> userDetails = UserService.getUserDetails(loginUserId);
            
            // Create User instance with the fetched data
            User loggedInUser = new User(
                loginUserId,
                (String) userDetails.get("username"),
                (String) userDetails.get("email"),
                password,  // You should securely handle the password
                (String) userDetails.get("birthday"),
                ((Long) userDetails.get("gender")).intValue(),
                (String) userDetails.get("phoneNumber"),
                (List<Task>) userDetails.getOrDefault("assignedTasks", new ArrayList<>()),
                (List<String>) userDetails.getOrDefault("workspacesId", new ArrayList<>()),
                (List<String>) userDetails.getOrDefault("reminderIds", new ArrayList<>())
            );
            
            // Open the homepage and pass the User object
            Home ui = new Home(loggedInUser);  // Pass User object
            ui.setVisible(true);
            
            dispose(); // Close the login form
        } else {
            // If login fails, show an error message
            JOptionPane.showMessageDialog(null, "Login failed. Please try again.");
        }
    } catch (Exception error) {
        // Handle any exception that occurs during login
        System.out.println("Error during login: " + error.getMessage());
        JOptionPane.showMessageDialog(null, "Login failed. Please try again.");
    }

} else {
    // If user creation failed
    System.out.println("Failed to create user. No user ID returned.");
    JOptionPane.showMessageDialog(null, "Failed to create user.");
}




            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginPage();  // Open LoginPage when clicking Login button
                dispose();
            }
        });

        // Add registration panel to the left panel
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.fill = GridBagConstraints.BOTH;
        leftGbc.weightx = 1.0;
        leftGbc.weighty = 1.0;
        leftPanel.add(registrationPanel, leftGbc);

        // Add left panel to the main panel
        mainPanel.add(leftPanel);

        // Add the main panel to the frame
        add(mainPanel);

        // Adjust size dynamically
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                leftPanel.setBounds(0, 0, getWidth() / 2, getHeight());
            }
        });
    }

    // Validate email format using regex
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            rf rf = new rf();
            rf.setVisible(true);
        });
    }
}
