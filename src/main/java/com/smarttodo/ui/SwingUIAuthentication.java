package com.smarttodo.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.smarttodo.config.FirebaseConfig;
import com.smarttodo.config.FirebaseAuthentication;


public class SwingUIAuthentication {
    public static void main(String[] args) {
        FirebaseConfig.initializeFirebase(); // Initialize Firebase here to ensure it's ready for use
        new RegistrationPage();
    }
}

// Page 1: Registration Page
class RegistrationPage extends JFrame {
    private JTextField emailField, usernameField, birthdayField, genderField, phoneNumberField;
    private JPasswordField passwordField;
    private JButton registerButton;

    public RegistrationPage() {
        setTitle("Register New Account");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 2));

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JLabel birthdayLabel = new JLabel("Birthday (YYYY-MM-DD):");
        birthdayField = new JTextField();

        JLabel genderLabel = new JLabel("Gender (1: Male, 2: Female):");
        genderField = new JTextField();

        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        phoneNumberField = new JTextField();

        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String email = emailField.getText();
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());
                    String birthday = birthdayField.getText();
                    int gender = Integer.parseInt(genderField.getText());
                    int phoneNumber = Integer.parseInt(phoneNumberField.getText());

                    // Call FirebaseAuthentication to create new user
                    String userId = FirebaseAuthentication.createUser(email, password, username, username, birthday, gender, phoneNumber);
                    if (userId != null) {
                        JOptionPane.showMessageDialog(null, "User created successfully: " + userId);
                        new LoginPage();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to create user.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numeric values for gender and phone number.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "An error occurred: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        add(emailLabel);
        add(emailField);
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(birthdayLabel);
        add(birthdayField);
        add(genderLabel);
        add(genderField);
        add(phoneNumberLabel);
        add(phoneNumberField);
        add(new JLabel());
        add(registerButton);

        setVisible(true);
    }
}

// Page 2: Login Page
class LoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginPage() {
        setTitle("Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String email = emailField.getText();
                    String password = new String(passwordField.getPassword());

                    // Use FirebaseAuthentication to check user credentials
                    FirebaseAuthentication.loginUser(email, password);
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    new HomePage();
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Login failed: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(new JLabel());
        add(loginButton);

        setVisible(true);
    }
}

// Page 3: Home Page
class HomePage extends JFrame {
    private JButton logoutButton;

    public HomePage() {
        setTitle("Home Page");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Simply go back to LoginPage
                new LoginPage();
                dispose();
            }
        });

        add(logoutButton);

        setVisible(true);
    }
}
