package com.smarttodo.ui;


import com.smarttodo.firebase.service.FirebaseAuthentication;
import com.google.firebase.messaging.AndroidConfig.Priority;
import com.smarttodo.firebase.FirebaseConfig;
import com.smarttodo.user.model.User;
import com.smarttodo.user.service.UserService;
import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;
import com.smarttodo.reminder.model.Reminder;
import com.smarttodo.workspace.model.Workspace;
import com.smarttodo.workspace.service.WorkspaceService;

import java.util.Map;
import java.text.SimpleDateFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
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
// class LoginPage extends JFrame {
//     private JTextField emailField;
//     private JPasswordField passwordField;
//     private JButton loginButton, registerButton;

//     public LoginPage() {
//         setTitle("Login");
//         setSize(400, 250);
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setLayout(new GridLayout(4, 2));

//         JLabel emailLabel = new JLabel("Email:");
//         emailField = new JTextField();

//         JLabel passwordLabel = new JLabel("Password:");
//         passwordField = new JPasswordField();

//         loginButton = new JButton("Login");
//         loginButton.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 try {
//                     String email = emailField.getText();
//                     String password = new String(passwordField.getPassword());

//                     // Sử dụng UserService để đăng nhập người dùng
//                     UserService userService = new UserService();
//                     String userId = userService.loginUser(email, password);

//                     if (userId != null) {
//                         // Đăng nhập thành công và UserService đã tạo instance người dùng
//                         Map<String, Object> userDetails = UserService.getUserDetails(userId);
                
//                         // Extract thông tin cần thiết để tạo User instance
//                         String username = (String) userDetails.get("username");
//                         String emailFetched = (String) userDetails.get("email");
//                         String birthday = (String) userDetails.get("birthday");
//                         int gender = ((Long) userDetails.get("gender")).intValue();
//                         String phoneNumber = (String) userDetails.get("phoneNumber");
//                         List<String> workspacesID = (List<String>) userDetails.get("workspacesId");
//                         List<String> reminderIds = (List<String>) userDetails.get("reminderIds");
//                         List<String> assignedTaskIds = (List<String>) userDetails.get("assignedTaskIds");
        
//                         // Tạo User instance và lưu lại nó thành currentUser
//                         User user = UserService.createUserinstance(
//                             userId,
//                             username,
//                             emailFetched,
//                             password,
//                             birthday,
//                             gender,
//                             phoneNumber,
//                             workspacesID,
//                             reminderIds,
//                             assignedTaskIds
//                         );

//                         // Set currentUser trong UserService
//                         UserService.setCurrentUser(user);
//                         System.out.println("User instance created with workspacesId: " + user.getWorkspacesId());
//                         System.out.println("AssignedTaskIds: " + user.getAssignedTaskIds());

//                         // Hiển thị thông báo đăng nhập thành công
//                         JOptionPane.showMessageDialog(null, "Login successful for user: " + user.getUsername());
//                         new HomePage();
//                         dispose();
//                     } else {
//                         JOptionPane.showMessageDialog(null, "Login failed: Invalid credentials");
//                     }
//                 } catch (Exception ex) {
//                     JOptionPane.showMessageDialog(null, "Login failed: " + ex.getMessage());
//                     ex.printStackTrace();
//                 }
//             }
//         });

//         registerButton = new JButton("Register");
//         registerButton.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 new RegistrationPage();
//                 dispose();
//             }
//         });

//         add(emailLabel);
//         add(emailField);
//         add(passwordLabel);
//         add(passwordField);
//         add(loginButton);
//         add(registerButton);

//         setVisible(true);
//     }
// }

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

                    // Đăng nhập người dùng qua UserService
                    String userId = UserService.loginUser(email, password);

                    if (userId != null) {
                        // Fetch thông tin người dùng từ Firestore
                        User currentUser = UserService.getCurrentUser();

                        if (currentUser != null) {
                            System.out.println("User instance created with:");
                            System.out.println("WorkspacesId: " + currentUser.getWorkspacesId());
                            System.out.println("AssignedTaskIds: " + currentUser.getAssignedTaskIds());
                            System.out.println("ReminderIds: " + currentUser.getReminderIds());

                            // Fetch and log tasks and reminders
                            fetchAndLogUserTasks(currentUser);
                            fetchAndLogUserReminders(currentUser);

                            // Hiển thị thông báo đăng nhập thành công
                            JOptionPane.showMessageDialog(null, "Login successful for user: " + currentUser.getUsername());
                            new HomePage();
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to fetch user details after login.");
                        }
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

    /**
     * Fetch and log tasks assigned to the user.
     */
    private void fetchAndLogUserTasks(User user) {
        try {
            List<Task> tasks = UserService.fetchTaskFromFirestore(user);
            for (Task task : tasks) {
                System.out.println("Task fetched: " + task.getTitle() + " (Task ID: " + task.getTaskID() + ")");
            }
        } catch (Exception e) {
            System.err.println("Error while fetching tasks: " + e.getMessage());
        }
    }

    /**
     * Fetch and log reminders assigned to the user.
     */
    private void fetchAndLogUserReminders(User user) {
        try {
            List<Reminder> reminders = UserService.fetchReminderFromFirestore(user);
            for (Reminder reminder : reminders) {
                System.out.println("Reminder fetched: " + reminder.getReminderID() + " for Task: " + reminder.getTaskID());
            }
        } catch (Exception e) {
            System.err.println("Error while fetching reminders: " + e.getMessage());
        }
    }
}



// Page 3: Home Page
// class HomePage extends JFrame {
//     private JButton logoutButton, addReminderButton, viewRemindersButton, addWorkspaceButton, viewWorkspacesButton;

//     public HomePage() {
//         setTitle("Home Page");
//         setSize(300, 200);
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setLayout(new FlowLayout());

//         logoutButton = new JButton("Logout");
//         logoutButton.addActionListener(new ActionListener() {
//         @Override
//         public void actionPerformed(ActionEvent e) {
//         // Save any necessary user data or state before logging out
//             UserService.setCurrentUser(null); // Clear current user instance
//             new LoginPage();
//             dispose();
//     }
// });


//         addReminderButton = new JButton("Add Reminder");
//         addReminderButton.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 new AddReminderPage();
//                 dispose();
//             }
//         });

//         viewRemindersButton = new JButton("View All Reminders");
//         viewRemindersButton.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 new ViewAllRemindersPage();
//                 dispose();
//             }
//         });

//         addWorkspaceButton = new JButton("Add Workspace");
//         addWorkspaceButton.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 new AddWorkspacePage();
//                 dispose();
//             }
//         });

//         viewWorkspacesButton = new JButton("View All Workspaces");
//         viewWorkspacesButton.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 new ViewAllWorkspacesPage();
//                 dispose();
//             }
//         });

//         add(logoutButton);
//         add(addReminderButton);
//         add(viewRemindersButton);
//         add(addWorkspaceButton);
//         add(viewWorkspacesButton);

//         setVisible(true);
//     }
// }
class HomePage extends JFrame {
    private JButton logoutButton, manageTasksButton, manageRemindersButton, workspaceButton;

    public HomePage() {
        setTitle("Home Page");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Logout Button
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            UserService.setCurrentUser(null); // Clear current user instance
            JOptionPane.showMessageDialog(null, "Logged out successfully.");
            new LoginPage();
            dispose();
        });

        // Workspace Button (For Add and View All Workspaces)
        workspaceButton = new JButton("Workspaces");
        workspaceButton.addActionListener(e -> {
            // Show a menu for workspace options
            String[] options = {"Add Workspace", "View All Workspaces"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Select an option",
                    "Workspace Options",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) { // Add Workspace
                new AddWorkspacePage();
                dispose();
            } else if (choice == 1) { // View All Workspaces
                new ViewAllWorkspacesPage();
                dispose();
            }
        });

        // Manage Tasks Button
        manageTasksButton = new JButton("Manage Tasks");
        manageTasksButton.addActionListener(e -> {
            User currentUser = UserService.getCurrentUser();
            if (currentUser != null && !currentUser.getWorkspacesId().isEmpty()) {
                new ManageTasksPage(currentUser.getWorkspacesId().get(0)); // Open ManageTasksPage for the first workspace
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No workspace available to manage tasks.");
            }
        });

        // Manage Reminders Button
        manageRemindersButton = new JButton("Manage Reminders");
        manageRemindersButton.addActionListener(e -> {
            User currentUser = UserService.getCurrentUser();
            if (currentUser != null && !currentUser.getReminderIds().isEmpty()) {
                new ManageRemindersPage(currentUser.getWorkspacesId().get(0)); // Manage reminders for the first workspace
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No reminders available to manage.");
            }
        });

        // Add buttons to UI
        add(logoutButton);
        add(workspaceButton);
        add(manageTasksButton);
        add(manageRemindersButton);

        setVisible(true);
    }
}




class SelectWorkspacePage extends JFrame {
    private JComboBox<String> workspaceDropdown;
    private JButton confirmButton, backButton;
    private String actionType;

    public SelectWorkspacePage(String actionType) {
        this.actionType = actionType; // "Task" hoặc "Reminder"
        setTitle("Select Workspace");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Fetch workspace IDs
        User currentUser = UserService.getCurrentUser();
        List<String> workspaces = currentUser != null ? currentUser.getWorkspacesId() : new ArrayList<>();

        // Dropdown to select workspace
        workspaceDropdown = new JComboBox<>(workspaces.toArray(new String[0]));

        // Confirm Button
        confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedWorkspace = (String) workspaceDropdown.getSelectedItem();
                if (selectedWorkspace != null) {
                    if ("Task".equals(actionType)) {
                        new ManageTasksPage(selectedWorkspace); // Trang quản lý task
                    } else if ("Reminder".equals(actionType)) {
                        new ManageRemindersPage(selectedWorkspace); // Trang quản lý reminder
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "No workspace selected.");
                }
            }
        });

        // Back Button
        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomePage();
                dispose();
            }
        });

        // Add components to UI
        add(new JLabel("Select Workspace:"));
        add(workspaceDropdown);
        add(confirmButton);
        add(backButton);

        setVisible(true);
    }
}


// class AddReminderPage extends JFrame {
//     private JTextField workspaceIDField, taskIDField, recurrencePatternField, dueDateField;
//     private JButton addReminderButton, backButton;

//     public AddReminderPage() {
//         setTitle("Add Reminder");
//         setSize(400, 350);
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setLayout(new GridLayout(5, 2));

//         JLabel workspaceIDLabel = new JLabel("Workspace ID:");
//         workspaceIDField = new JTextField();

//         JLabel taskIDLabel = new JLabel("Task ID:");
//         taskIDField = new JTextField();

//         JLabel recurrencePatternLabel = new JLabel("Recurrence Pattern:");
//         recurrencePatternField = new JTextField();

//         JLabel dueDateLabel = new JLabel("Due Date (YYYY-MM-DD):");
//         dueDateField = new JTextField();

//         addReminderButton = new JButton("Add Reminder");
//         addReminderButton.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 try {
//                     String workspaceID = workspaceIDField.getText();
//                     String taskID = taskIDField.getText();
//                     String recurrencePattern = recurrencePatternField.getText();
//                     String dueDateStr = dueDateField.getText();
//                     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                     Date dueDate = dateFormat.parse(dueDateStr);

//                     // Create Reminder instance
//                     Reminder reminder = Reminder.createReminderInstance(workspaceID, taskID, recurrencePattern, dueDate);

//                     // Add Reminder to Task
//                     User currentUser = UserService.getCurrentUser();
//                     if (currentUser != null) {
//                         currentUser.addReminderToTask(workspaceID, taskID, reminder);
//                         JOptionPane.showMessageDialog(null, "Reminder added successfully.");
//                     } else {
//                         JOptionPane.showMessageDialog(null, "No user is currently logged in.");
//                     }

//                     new HomePage();
//                     dispose();
//                 } catch (ParseException parseException) {
//                     JOptionPane.showMessageDialog(null, "Invalid due date format. Please use YYYY-MM-DD.");
//                 } catch (Exception ex) {
//                     JOptionPane.showMessageDialog(null, "Failed to add reminder: " + ex.getMessage());
//                     ex.printStackTrace();
//                 }
//             }
//         });

//         backButton = new JButton("Back");
//         backButton.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 new HomePage();
//                 dispose();
//             }
//         });

//         add(workspaceIDLabel);
//         add(workspaceIDField);
//         add(taskIDLabel);
//         add(taskIDField);
//         add(recurrencePatternLabel);
//         add(recurrencePatternField);
//         add(dueDateLabel);
//         add(dueDateField);
//         add(addReminderButton);
//         add(backButton);

//         setVisible(true);
//     }
// }


// class ViewAllRemindersPage extends JFrame {
//     private JTextArea remindersArea;
//     private JButton backButton;

//     public ViewAllRemindersPage() {
//         setTitle("All Reminders");
//         setSize(400, 300);
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setLayout(new BorderLayout());

//         remindersArea = new JTextArea();
//         remindersArea.setEditable(false);

//         try {
//             List<String> reminderIds = UserService.getCurrentUser().getReminderIds();
//             for (String reminderId : reminderIds) {
//                 remindersArea.append("Reminder ID: " + reminderId + "\n");
//             }
//         } catch (Exception ex) {
//             remindersArea.setText("Failed to fetch reminders: " + ex.getMessage());
//             ex.printStackTrace();
//         }

//         backButton = new JButton("Back");
//         backButton.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 new HomePage();
//                 dispose();
//             }
//         });

//         add(new JScrollPane(remindersArea), BorderLayout.CENTER);
//         add(backButton, BorderLayout.SOUTH);

//         setVisible(true);
//     }
// }


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
                   
                    Workspace workspace = Workspace.createWorkspaceInstance(workspaceId,name, description);

                    
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






class ManageTasksPage extends JFrame {
    private JList<Task> taskList; // Định nghĩa taskList là JList<Task>
    private DefaultListModel<Task> taskListModel; // Model cho JList<Task>

    public ManageTasksPage(String workspaceId) {
        setTitle("Manage Tasks");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Fetch tasks từ Firestore
        List<Task> tasks = WorkspaceService.viewAllTasks(workspaceId);

        // Khởi tạo DefaultListModel và JList<Task>
        taskListModel = new DefaultListModel<>();
        for (Task task : tasks) {
            taskListModel.addElement(task); // Thêm task vào model
        }

        taskList = new JList<>(taskListModel); // Gán model vào JList

        // Tùy chỉnh hiển thị JList chỉ hiển thị tiêu đề Task
        taskList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Task task = (Task) value; // Ép kiểu value về Task
                return super.getListCellRendererComponent(list, task.getTitle(), index, isSelected, cellHasFocus);
            }
        });

        // Đặt JList vào JScrollPane và thêm vào giao diện
        JScrollPane taskScrollPane = new JScrollPane(taskList);
        add(taskScrollPane, BorderLayout.CENTER);

        // Thêm các nút chức năng (Add, Edit, Delete Task)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Add Task Button
        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(e -> new AddTaskDialog(workspaceId, this));
        buttonPanel.add(addTaskButton);

        // Edit Task Button
        JButton editTaskButton = new JButton("Edit Task");
        editTaskButton.addActionListener(e -> {
            Task selectedTask = taskList.getSelectedValue(); // Lấy Task được chọn
            if (selectedTask != null) {
                new EditTaskDialog(workspaceId, selectedTask, this);
            } else {
                JOptionPane.showMessageDialog(null, "Please select a task to edit.");
            }
        });
        buttonPanel.add(editTaskButton);

        // Delete Task Button
        JButton deleteTaskButton = new JButton("Delete Task");
        deleteTaskButton.addActionListener(e -> {
            Task selectedTask = taskList.getSelectedValue(); // Lấy Task được chọn
            if (selectedTask != null) {
                WorkspaceService.deleteTaskFromWorkspace(workspaceId, selectedTask.getTaskID());
                refreshTaskList(workspaceId); // Cập nhật lại danh sách task
            } else {
                JOptionPane.showMessageDialog(null, "Please select a task to delete.");
            }
        });
        buttonPanel.add(deleteTaskButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Method để refresh task list sau khi thêm, sửa hoặc xóa
    public void refreshTaskList(String workspaceId) {
        List<Task> tasks = WorkspaceService.viewAllTasks(workspaceId);
        taskListModel.clear();
        for (Task task : tasks) {
            taskListModel.addElement(task);
        }
    }
}


class ManageRemindersPage extends JFrame {
    private String workspaceId;
    private String taskId;
    private JList<Reminder> remindersList;
    private DefaultListModel<Reminder> remindersModel;


    public ManageRemindersPage(String workspaceId) {
        this.workspaceId = workspaceId;

        setTitle("Manage Reminders");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    public ManageRemindersPage(String workspaceId, String taskId) {
        this.workspaceId = workspaceId;
        this.taskId = taskId;

        setTitle("Manage Reminders");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Reminders for Task ID: " + taskId);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Reminders List
        remindersModel = new DefaultListModel<>();
        remindersList = new JList<>(remindersModel);
        remindersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(remindersList);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Add Reminder Button
        JButton addReminderButton = new JButton("Add Reminder");
        addReminderButton.addActionListener(e -> {
            new AddReminderDialog(workspaceId, taskId, this);
        });
        buttonPanel.add(addReminderButton);

        // Edit Reminder Button
        JButton editReminderButton = new JButton("Edit Reminder");
        editReminderButton.addActionListener(e -> {
            Reminder selectedReminder = remindersList.getSelectedValue();
            if (selectedReminder != null) {
                new EditReminderDialog(workspaceId, taskId, selectedReminder, this);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a reminder to edit.");
            }
        });
        buttonPanel.add(editReminderButton);

        // Delete Reminder Button
        JButton deleteReminderButton = new JButton("Delete Reminder");
        deleteReminderButton.addActionListener(e -> {
            Reminder selectedReminder = remindersList.getSelectedValue();
            if (selectedReminder != null) {
                int confirmation = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this reminder?",
                        "Delete Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirmation == JOptionPane.YES_OPTION) {
                    WorkspaceService.removeReminder(workspaceId, taskId, selectedReminder.getReminderID());
                    refreshReminderList();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a reminder to delete.");
            }
        });
        buttonPanel.add(deleteReminderButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load reminders
        refreshReminderList();

        setVisible(true);
    }

    public void refreshReminderList() {
        remindersModel.clear();
        List<Reminder> reminders = WorkspaceService.getRemindersForTask(workspaceId, taskId);
        for (Reminder reminder : reminders) {
            remindersModel.addElement(reminder);
        }
    }
}