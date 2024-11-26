package com.smarttodo.ui;


import com.smarttodo.firebase.service.FirebaseAuthentication;
import com.smarttodo.firebase.FirebaseConfig;
import com.smarttodo.user.model.User;
import com.smarttodo.user.service.UserService;
import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;
import com.smarttodo.reminder.model.Reminder;
import com.smarttodo.workspace.model.Workspace;

import java.util.Map;
import java.text.SimpleDateFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Date;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.UUID;


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
    private JButton registerButton, signupButton;

    public RegistrationPage() {
        setTitle("Register New Account");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 2));

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
                    String birthdayStr = birthdayField.getText();
                    int gender = Integer.parseInt(genderField.getText());
                    String phoneNumber = phoneNumberField.getText();

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
                    if (gender != 1 && gender != 2) {
                        JOptionPane.showMessageDialog(null, "Gender must be 1 (Male) or 2 (Female).");
                        return;
                    }

                    // Validate birthday format
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    dateFormat.setLenient(false);
                    Date birthday;
                    try {
                        birthday = dateFormat.parse(birthdayStr);
                    } catch (ParseException parseException) {
                        JOptionPane.showMessageDialog(null, "Invalid birthday format. Please use YYYY-MM-DD.");
                        return;
                    }

                    // Call FirebaseAuthentication to create new user
                    String userId = FirebaseAuthentication.createUser(email, password, username, username, birthdayStr, gender,phoneNumber);
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

        signupButton = new JButton("Signup");
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginPage();
                dispose();
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
        add(registerButton);
        add(signupButton);

        setVisible(true);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

// Page 2: Login Page
// Page 2: Login Page
class LoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public LoginPage() {
        setTitle("Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

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

                    // Sử dụng UserService để đăng nhập người dùng
                    UserService userService = new UserService();
                    String userId = userService.loginUser(email, password);

                    if (userId != null) {
                        // Đăng nhập thành công và UserService đã tạo instance người dùng
                        Map<String, Object> userDetails = UserService.getUserDetails(userId);
                
                        // Extract thông tin cần thiết để tạo User instance
                        String username = (String) userDetails.get("username");
                        String emailFetched = (String) userDetails.get("email");
                        String birthday = (String) userDetails.get("birthday");
                        int gender = ((Long) userDetails.get("gender")).intValue();
                        String phoneNumber = (String) userDetails.get("phoneNumber");
                        List<String> workspacesID = (List<String>) userDetails.get("workspacesId");
                        List<Task> assignedTasks = (List<Task>) userDetails.get("assignedTasks");
                        List<String> reminderIds = (List<String>) userDetails.get("reminderIds");
        
                        // Tạo User instance và lưu lại nó thành currentUser
                        User user = UserService.createUserinstance(
                            userId,
                            username,
                            emailFetched,
                            password,
                            birthday,
                            gender,
                            phoneNumber,
                            assignedTasks,
                            workspacesID,
                            reminderIds
                        );
        
                        // Set currentUser trong UserService
                        UserService.setCurrentUser(user);
        
                        // Hiển thị thông báo đăng nhập thành công
                        JOptionPane.showMessageDialog(null, "Login successful for user: " + user.getUsername());
                        new HomePage();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Login failed: Invalid credentials");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Login failed: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

            
        });

        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegistrationPage();
                dispose();
            }
        });

        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(registerButton);

        setVisible(true);
    }
}

// Page 3: Home Page
class HomePage extends JFrame {
    private JButton logoutButton, addReminderButton, viewRemindersButton, addWorkspaceButton, viewWorkspacesButton;

    public HomePage() {
        setTitle("Home Page");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        // Save any necessary user data or state before logging out
            UserService.setCurrentUser(null); // Clear current user instance
            new LoginPage();
            dispose();
    }
});


        addReminderButton = new JButton("Add Reminder");
        addReminderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddReminderPage();
                dispose();
            }
        });

        viewRemindersButton = new JButton("View All Reminders");
        viewRemindersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewAllRemindersPage();
                dispose();
            }
        });

        addWorkspaceButton = new JButton("Add Workspace");
        addWorkspaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddWorkspacePage();
                dispose();
            }
        });

        viewWorkspacesButton = new JButton("View All Workspaces");
        viewWorkspacesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewAllWorkspacesPage();
                dispose();
            }
        });

        add(logoutButton);
        add(addReminderButton);
        add(viewRemindersButton);
        add(addWorkspaceButton);
        add(viewWorkspacesButton);

        setVisible(true);
    }
}


class AddReminderPage extends JFrame {
    private JTextField taskIDField, recurrencePatternField, dueDateField;
    private JButton addReminderButton, backButton;

    public AddReminderPage() {
        setTitle("Add Reminder");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        JLabel taskIDLabel = new JLabel("Task ID:");
        taskIDField = new JTextField();

        JLabel recurrencePatternLabel = new JLabel("Recurrence Pattern:");
        recurrencePatternField = new JTextField();

        JLabel dueDateLabel = new JLabel("Due Date (YYYY-MM-DD):");
        dueDateField = new JTextField();

        addReminderButton = new JButton("Add Reminder");
        addReminderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String taskID = taskIDField.getText();
                    String recurrencePattern = recurrencePatternField.getText();
                    String dueDateStr = dueDateField.getText();
                    Date dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(dueDateStr);

                    // Gọi phương thức createReminderInstance của User để tạo Reminder instance
                    Reminder reminder = UserService.createReminderInstance(taskID, recurrencePattern, dueDate);
                    
                    // Gọi phương thức addReminder của User để lưu Reminder vào Firestore
                    UserService.getCurrentUser().addReminder(reminder);
                    JOptionPane.showMessageDialog(null, "Reminder added successfully.");
                    
                    new HomePage();
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Failed to add reminder: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomePage();
                dispose();
            }
        });

        add(taskIDLabel);
        add(taskIDField);
        add(recurrencePatternLabel);
        add(recurrencePatternField);
        add(dueDateLabel);
        add(dueDateField);
        add(addReminderButton);
        add(backButton);

        setVisible(true);
    }
}

class ViewAllRemindersPage extends JFrame {
    private JTextArea remindersArea;
    private JButton backButton;

    public ViewAllRemindersPage() {
        setTitle("All Reminders");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        remindersArea = new JTextArea();
        remindersArea.setEditable(false);

        try {
            List<String> reminderIds = UserService.getCurrentUser().getReminderIds();
            for (String reminderId : reminderIds) {
                remindersArea.append("Reminder ID: " + reminderId + "\n");
            }
        } catch (Exception ex) {
            remindersArea.setText("Failed to fetch reminders: " + ex.getMessage());
            ex.printStackTrace();
        }

        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomePage();
                dispose();
            }
        });

        add(new JScrollPane(remindersArea), BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);

        setVisible(true);
    }
}


class AddWorkspacePage extends JFrame {
    private JTextField nameField, descriptionField;
    private JButton addWorkspaceButton, backButton;

    public AddWorkspacePage() {
        setTitle("Add Workspace");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        JLabel nameLabel = new JLabel("Workspace Name:");
        nameField = new JTextField();

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionField = new JTextField();

        addWorkspaceButton = new JButton("Add Workspace");
        addWorkspaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText();
                    String description = descriptionField.getText();
                    String workspaceId = UUID.randomUUID().toString();
                    // Call WorkspaceService to create a new Workspace instance and save to Firestore
                   
                    Workspace workspace = UserService.createWorkspaceInstance(workspaceId,name, description);

                    
                    UserService.getCurrentUser().createnewWorkspace(workspaceId,name,description);
                    UserService.getCurrentUser().addWorkspacesId(workspaceId);

                    JOptionPane.showMessageDialog(null, "Workspace added successfully.");
                    new HomePage();
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Failed to add workspace: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomePage();
                dispose();
            }
        });

        add(nameLabel);
        add(nameField);
        add(descriptionLabel);
        add(descriptionField);
        add(addWorkspaceButton);
        add(backButton);

        setVisible(true);
    }
}

class ViewAllWorkspacesPage extends JFrame {
    private JTextArea workspacesArea;
    private JButton backButton;

    public ViewAllWorkspacesPage() {
        setTitle("All Workspaces");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        workspacesArea = new JTextArea();
        workspacesArea.setEditable(false);

        try {
            User currentUser = UserService.getCurrentUser();
            if (currentUser == null) {
                throw new NullPointerException("No user is currently logged in. Please log in again.");
            }

            List<String> workspaceIds = currentUser.getWorkspacesId();
            if (workspaceIds == null || workspaceIds.isEmpty()) {
                workspacesArea.append("No workspaces found.\n");
            } else {
                for (String workspaceId : workspaceIds) {
                    workspacesArea.append("Workspace ID: " + workspaceId + "\n");
                }
            }
        } catch (NullPointerException npe) {
            workspacesArea.setText("Error: " + npe.getMessage());
            npe.printStackTrace();
        } catch (Exception ex) {
            workspacesArea.setText("Failed to fetch workspaces: " + ex.getMessage());
            ex.printStackTrace();
        }

        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomePage();
                dispose();
            }
        });

        add(new JScrollPane(workspacesArea), BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);

        setVisible(true);
    }
}
