package com.smarttodo.ui;

import com.smarttodo.firebase.FirebaseConfig;
import com.smarttodo.firebase.service.FirebaseFirestore;
import com.smarttodo.reminder.model.Reminder;
import com.smarttodo.reminder.service.ReminderService;
import com.smarttodo.task.model.Priority;
import com.smarttodo.task.model.Status;
import com.smarttodo.task.model.Task;
import com.smarttodo.ui.Home.AIPopupPanel.SuggestedTask;
import com.smarttodo.user.model.User;
import com.smarttodo.user.service.UserService;
import com.smarttodo.workspace.model.Workspace;
import com.smarttodo.workspace.model.WorkspaceRole;
import com.smarttodo.workspace.service.WorkspaceService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;


import javax.swing.*;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.File;

import java.util.List;
import java.util.ArrayList;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Home extends JFrame {

    private User currentUser;
    public interface OnWorkspaceDeleteListener {
        void onWorkspaceDelete(String workspaceId);
    }

    public Home(User user) {
        FirebaseConfig.initializeFirebase();
        this.currentUser = user;

        // Frame settings
        setTitle("Client Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Get the screen size (width and height)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width - 200;
        int screenHeight = screenSize.height - 200;

        // Set the frame to full screen (maximized)
        setSize(screenWidth, screenHeight);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Main layout for the frame
        setLayout(new BorderLayout());

        // Left sidebar panel (using Sidebar class)
		Sidebar sidebar = new Sidebar(currentUser, new Sidebar.OnWorkspaceSwitchListener() {
			@Override
			public void onWorkspaceSwitch(String viewName, String workspaceId) {
				// Call the switchContentView method in the Home class
				switchContentView(viewName, workspaceId); // 'Home.this' refers to the outer Home instance
			}
		});
		add(sidebar, BorderLayout.WEST);
		


        // Right main panel with CardLayout for switching views
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(new Color(30, 30, 30));

        // Homepage panel
        JPanel homepagePanel = createHomepagePanel();
        contentPanel.add(homepagePanel, "Home");

        // Workspace panel (initially empty or placeholder)
        JPanel workspacePanel = createWorkspacePanel("d68bbe29-5915-4e05-9aa8-76ee68094fd4", sidebar);
        contentPanel.add(workspacePanel, "Workspace");

        // Add the content panel to the main frame
        add(contentPanel, BorderLayout.CENTER);

        // Add a component listener to handle window resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateSidebarWidth();
            }
        });

        // Initial sidebar width update
        updateSidebarWidth();
    }

    // Update the sidebar width based on the current window width
    private void updateSidebarWidth() {
        int newWidth = getWidth() / 6; // Sidebar width is 1/6th of the window width
        revalidate(); // Revalidate the layout to update the sidebar size
    }

   
    




    private JPanel createHomepagePanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
    
        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername(), JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
    
        // Reminders panel
        JPanel remindersPanel = new JPanel();
        remindersPanel.setLayout(new BoxLayout(remindersPanel, BoxLayout.Y_AXIS)); // Stack vertically
        remindersPanel.setBackground(new Color(30, 30, 30));
    
        // Add heading "Reminders"
        JLabel remindersHeading = new JLabel("Reminders");
        remindersHeading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        remindersHeading.setForeground(Color.WHITE);
        remindersHeading.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10)); // Margin for the heading
        remindersPanel.add(remindersHeading);
    
        // Container for the scrollable part of the reminders
        JPanel scrollableContainer = new JPanel();
        scrollableContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20)); // Align buttons to center
        scrollableContainer.setBackground(new Color(30, 30, 30));
    
        // Fetch reminders asynchronously
        ReminderService reminderService = new ReminderService(currentUser);
        WorkspaceService workspaceService = new WorkspaceService();
        List<Reminder> reminders = null;
        try {
            // Clear the existing reminders list
            reminders = reminderService.fetchReminders();
        
            // Fetch the list of workspace IDs from currentUser
            List<String> workspaceIds = currentUser.getWorkspacesId();
        
            if (workspaceIds != null && !workspaceIds.isEmpty()) {
                // Iterate over each workspace ID
                for (String workspaceId : workspaceIds) {
                    // Fetch reminders for the current workspace
                    List<Reminder> workspaceReminders = workspaceService.fetchWorkspaceReminders(workspaceId);
        
                    if (workspaceReminders != null) {
                        reminders.addAll(workspaceReminders); // Add to the main reminders list
                    }
                }
            } else {
                System.out.println("No workspaces found for the current user.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        

        System.out.println(reminders);
        // Add reminder buttons (if any reminders exist)
        if (reminders != null) {
            Color[] colors = {
                new Color(70, 130, 180), // Steel Blue
                new Color(220, 20, 60),  // Crimson
                new Color(128, 0, 128),  // Purple
                new Color(255, 215, 0),  // Gold
                new Color(0, 128, 0)     // Green
            };
    
            // Create reminder buttons dynamically based on fetched reminders
            for (int i = 0; i < Math.min(reminders.size(), 5); i++) {
                Reminder reminder = reminders.get(i);
                JButton reminderButton = new JButton("Reminder: " + reminder.getTaskID());
                reminderButton.setPreferredSize(new Dimension(120, 100)); // Size for each reminder button
                reminderButton.setBackground(colors[i % colors.length]); // Cycle through colors
                reminderButton.setForeground(Color.WHITE);
                reminderButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
                reminderButton.setFocusPainted(false);
                scrollableContainer.add(reminderButton);
            }
        }
    
        // Set the preferred size of the scrollable container
        scrollableContainer.setPreferredSize(new Dimension(600, 120));  // 600px width to fit 5 buttons with 20px gaps
    
        // Scroll panel for reminders (only horizontal scroll)
        JScrollPane scrollPane = new JScrollPane(scrollableContainer,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,  // No vertical scrollbar
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  // Horizontal scrollbar always
    
        remindersPanel.add(scrollPane);
    
        // Set a fixed height for the reminders panel (height can be adjusted)
        remindersPanel.setPreferredSize(new Dimension(800, 300));
    
        // Add the reminders panel to the main panel
        mainPanel.add(remindersPanel, BorderLayout.CENTER);
    
        // Calendar panel container: Create a panel for calendar views
        JPanel calendarPanelContainer = new JPanel(new BorderLayout());
        calendarPanelContainer.setBackground(new Color(30, 30, 30));
    
        // Initialize CalendarManager and pass reminders
        CalendarManager calendarManager = new CalendarManager(reminders);
        calendarPanelContainer.add(calendarManager, BorderLayout.CENTER); // Add CalendarManager panel
    
        // Add the calendar panel container to the main panel
        mainPanel.add(calendarPanelContainer, BorderLayout.SOUTH);
    
        // Add panels to the main frame
        add(mainPanel, BorderLayout.CENTER);
    
        return mainPanel;
    }
    


    
    private List<AIPopupPanel.SuggestedTask> executePythonScript(String tasksJson) {
        List<AIPopupPanel.SuggestedTask> tasks = new ArrayList<>();
        String pythonScriptPath = "src/main/resources/ai.py";
    
        try {
            // Tạo ProcessBuilder để chạy Python script
            ProcessBuilder pb = new ProcessBuilder("python3", pythonScriptPath);
            Process process = pb.start();
    
            // Gửi JSON qua stdin
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                System.out.println("Sending tasks JSON via stdin: " + tasksJson);
                writer.write(tasksJson);
                writer.flush();
            }
    
            // Đọc stdout từ Python script
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder outputBuilder = new StringBuilder();
            String line;
            while ((line = stdoutReader.readLine()) != null) {
                outputBuilder.append(line.trim()); // Loại bỏ khoảng trắng không mong muốn
            }
    
            // Đọc stderr từ Python script
            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorBuilder = new StringBuilder();
            String errLine;
            while ((errLine = stderrReader.readLine()) != null) {
                errorBuilder.append(errLine).append("\n");
            }
    
            int exitCode = process.waitFor();
    
            if (exitCode == 0) {
                // Parse JSON từ stdout
                String jsonStr = outputBuilder.toString().trim();
                System.out.println("Python script output: " + jsonStr);
    
                // Loại bỏ các ký tự không mong muốn nếu có
                if (jsonStr.startsWith("```json")) {
                    jsonStr = jsonStr.substring("```json".length()).trim();
                }
                if (jsonStr.endsWith("```")) {
                    jsonStr = jsonStr.substring(0, jsonStr.lastIndexOf("```")).trim();
                }
    
                // Đảm bảo JSON là một mảng
                if (!jsonStr.startsWith("[") || !jsonStr.endsWith("]")) {
                    throw new JSONException("Invalid JSON format. Expected JSONArray.");
                }
    
                // Parse JSON array
                JSONArray tasksArray = new JSONArray(jsonStr);
                for (int i = 0; i < tasksArray.length(); i++) {
                    JSONObject taskObj = tasksArray.getJSONObject(i);
    
                    String title = taskObj.optString("title", "");
                    String description = taskObj.optString("description", "");
                    String priority = taskObj.optString("priority", "");
    
                    // Parse tagsname as a list of strings
                    List<String> tagsname = new ArrayList<>();
                    JSONArray tagsArray = taskObj.optJSONArray("tagsname");
                    if (tagsArray != null) {
                        for (int j = 0; j < tagsArray.length(); j++) {
                            tagsname.add(tagsArray.getString(j));
                        }
                    }
    
                    String dueDate = taskObj.has("dueDate") ? taskObj.getJSONObject("dueDate").optString("seconds", "N/A") : "N/A";
    
                    AIPopupPanel.SuggestedTask st = new AIPopupPanel.SuggestedTask(title, description, tagsname, priority, dueDate);
                    tasks.add(st);
                }
            } else {
                System.err.println("Python script exited with code: " + exitCode);
                if (errorBuilder.length() > 0) {
                    System.err.println("STDERR: " + errorBuilder.toString());
                }
            }
    
        } catch (JSONException e) {
            System.err.println("JSON Parsing Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return tasks;
    }
    

    
    


    // Bổ sung Map để lưu trữ tasksPanel
private final Map<String, JPanel> workspaceTasksPanels = new HashMap<>();

private JPanel createWorkspacePanel(String workspaceId, Sidebar sidebar) {
    JPanel panel = new JPanel();

    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical stacking
    panel.setBackground(new Color(30, 30, 30));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    Firestore db = FirestoreClient.getFirestore();
    DocumentReference workspaceDocRef = db.collection("Workspace").document(workspaceId);
    ApiFuture<DocumentSnapshot> future = workspaceDocRef.get();

    try {
        DocumentSnapshot workspaceDoc = future.get();
        if (workspaceDoc.exists()) {
            String workspaceName = workspaceDoc.getString("name");
            String workspaceDescription = workspaceDoc.getString("description");

            // Create the App Bar
            JPanel appBar = new JPanel();
            appBar.setLayout(new BorderLayout());
            appBar.setBackground(new Color(50, 50, 50));
            appBar.setPreferredSize(new Dimension(panel.getWidth(), 50));
            appBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            String currentUserId = currentUser.getUserId();
            Map<String, String> userRoles = (Map<String, String>) workspaceDoc.get("userRoles");
            String userRole = userRoles.getOrDefault(currentUserId, "VIEWER");

            JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
            rightButtonPanel.setOpaque(false);

            JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
            leftButtonPanel.setOpaque(false);

            if ("OWNER".equals(userRole)) {
                JButton deleteWorkspaceButton = new JButton("Delete Workspace");
                deleteWorkspaceButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                deleteWorkspaceButton.setForeground(Color.BLACK);
                deleteWorkspaceButton.setBackground(new Color(60, 60, 60));
                deleteWorkspaceButton.setFocusPainted(false);
                deleteWorkspaceButton.addActionListener(e -> {
                    DeleteWorkspace(workspaceId);
                    sidebar.fetchWorkspaces();
                });
                

                rightButtonPanel.add(deleteWorkspaceButton);
            }

            int baseButtonSize = 50; // Kích thước nút '+'
            if (!"VIEWER".equals(userRole)) {

                JButton addCollaboratorButton = new JButton("Add Collaborator");
                addCollaboratorButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                addCollaboratorButton.setForeground(Color.BLACK);
                addCollaboratorButton.setBackground(new Color(60, 60, 60));
                addCollaboratorButton.setFocusPainted(false);
            
                addCollaboratorButton.addActionListener(e -> {
                    // Show dialog to add a collaborator
                    showAddCollaboratorDialog(workspaceId);
                });
            
                rightButtonPanel.add(addCollaboratorButton);

            // Nút thêm task
            JButton addTaskButton = new JButton("+");
            addTaskButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            addTaskButton.setForeground(Color.BLACK);
            addTaskButton.setBackground(new Color(60, 60, 60));
            addTaskButton.setFocusPainted(false);
            addTaskButton.setPreferredSize(new Dimension(baseButtonSize, baseButtonSize));
            addTaskButton.addActionListener(e -> openAddTaskDialog(workspaceId));

            rightButtonPanel.add(addTaskButton);

                // Nút ASK AI
            JButton askAIButton = new JButton("ASK AI");
            askAIButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            askAIButton.setForeground(Color.BLACK);
            askAIButton.setBackground(new Color(60, 60, 60));
            askAIButton.setFocusPainted(false);

            int askAIWidth = (int) (baseButtonSize * 3);
            askAIButton.setPreferredSize(new Dimension(askAIWidth, baseButtonSize));

            // Khi nhấn nút ASK AI
            askAIButton.addActionListener(e -> {
                AIPopupPanel aiPanel = new AIPopupPanel();

                // Tạo dialog "Loading..." với hiệu ứng xoay
                LoadingDialog loadingDialog = new LoadingDialog((Frame) SwingUtilities.getWindowAncestor(panel));

                // Tạo SwingWorker để tải dữ liệu trên luồng nền
                SwingWorker<List<AIPopupPanel.SuggestedTask>, Void> worker = new SwingWorker<List<AIPopupPanel.SuggestedTask>, Void>() {
                    private String tasksJson; // Biến lưu JSON task

                    @Override
                    protected List<AIPopupPanel.SuggestedTask> doInBackground() throws Exception {
                        // Lấy dữ liệu từ Firestore
                        CollectionReference tasksRef = db.collection("Workspace").document(workspaceId).collection("Task");
                        ApiFuture<QuerySnapshot> future = tasksRef.get();
                        QuerySnapshot querySnapshot = future.get();

                        // Tạo JSON Array chứa danh sách các task
                        JSONArray tasksArray = new JSONArray();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            JSONObject taskObject = new JSONObject(document.getData());
                            tasksArray.put(taskObject);
                        }

                        // Gán JSON Array vào chuỗi
                        tasksJson = tasksArray.toString(2); // Format đẹp với indent = 2
                        System.out.println("Generated Tasks JSON: " + tasksJson);

                        // Gửi JSON qua stdin và nhận danh sách suggested tasks
                        return executePythonScript(tasksJson);
                    }

                    @Override
                    protected void done() {
                        // Đóng dialog loading
                        loadingDialog.dispose();
                        try {
                            // Lấy kết quả từ Python script
                            List<AIPopupPanel.SuggestedTask> suggestedTasks = get();

                            // Hiển thị các task trong AI Popup Panel
                            boolean success = aiPanel.loadTasks(suggestedTasks, workspaceId);

                            if (success) {
                                // Nếu load thành công, hiển thị dialog AI
                                JDialog aiDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(panel), "AI Suggestions", true);
                                aiDialog.getContentPane().add(aiPanel, BorderLayout.CENTER);
                                aiDialog.pack();
                                aiDialog.setLocationRelativeTo(panel);
                                aiDialog.setVisible(true);
                            } else {
                                // Nếu không có dữ liệu hoặc API call lỗi
                                JOptionPane.showMessageDialog(panel,
                                        "No suggestions available or API call failed.",
                                        "No Data",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(panel,
                                    "An error occurred while loading suggestions.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };

                // Chạy worker và hiển thị loading dialog
                SwingUtilities.invokeLater(() -> {
                    worker.execute();
                    loadingDialog.setVisible(true); // Hiển thị dialog Loading trong khi worker đang chạy
                });
            });
            rightButtonPanel.add(askAIButton);
            }

            

            

            appBar.add(rightButtonPanel, BorderLayout.EAST);
            panel.add(appBar);

            panel.add(Box.createVerticalStrut(20));

            // Display the workspace name (editable on click)
JTextField workspaceNameField = new JTextField(workspaceName);
workspaceNameField.setFont(new Font("Segoe UI", Font.BOLD, 24));
workspaceNameField.setForeground(Color.WHITE);
workspaceNameField.setBackground(Color.BLACK);
workspaceNameField.setBorder(BorderFactory.createEmptyBorder());
workspaceNameField.setEditable(false);

// Set fixed size for the name field
workspaceNameField.setPreferredSize(new Dimension(400, 40));
workspaceNameField.setMaximumSize(new Dimension(400, 40));

// Add a MouseListener to enable editing on click
workspaceNameField.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        workspaceNameField.setEditable(true);
        workspaceNameField.setForeground(Color.WHITE);
        workspaceNameField.setBackground(Color.LIGHT_GRAY);
    }
});

// Save the updated name on Enter key press or focus lost
workspaceNameField.addActionListener(e -> saveUpdatedWorkspaceName(workspaceNameField.getText(), workspaceId));
workspaceNameField.addFocusListener(new java.awt.event.FocusAdapter() {
    @Override
    public void focusLost(java.awt.event.FocusEvent e) {
        saveUpdatedWorkspaceName(workspaceNameField.getText(), workspaceId);
    }
});

// Display the workspace description (editable on click)
JTextArea workspaceDescriptionArea = new JTextArea(workspaceDescription);
workspaceDescriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 18));
workspaceDescriptionArea.setForeground(Color.WHITE);
workspaceDescriptionArea.setBackground(Color.BLACK);
workspaceDescriptionArea.setBorder(BorderFactory.createEmptyBorder());
workspaceDescriptionArea.setLineWrap(true);
workspaceDescriptionArea.setWrapStyleWord(true);
workspaceDescriptionArea.setEditable(false);

// Set fixed size for the description area
workspaceDescriptionArea.setPreferredSize(new Dimension(400, 100));
workspaceDescriptionArea.setMaximumSize(new Dimension(400, 100));

// Add a MouseListener to enable editing on click
workspaceDescriptionArea.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        workspaceDescriptionArea.setEditable(true);
        workspaceDescriptionArea.setForeground(Color.WHITE);
        workspaceDescriptionArea.setBackground(Color.LIGHT_GRAY);
    }
});

// Save the updated description on focus lost
workspaceDescriptionArea.addFocusListener(new java.awt.event.FocusAdapter() {
    @Override
    public void focusLost(java.awt.event.FocusEvent e) {
        saveUpdatedWorkspaceDescription(workspaceDescriptionArea.getText(), workspaceId);
    }
});

// Add components to the panel
panel.add(workspaceNameField);
panel.add(Box.createVerticalStrut(10));
panel.add(workspaceDescriptionArea);
panel.add(Box.createVerticalStrut(20));

            // Bọc tasksPanel vào JScrollPane
            JPanel tasksPanel = new JPanel();
            tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
            tasksPanel.setBackground(new Color(30, 30, 30));
            
            JScrollPane scrollPane = new JScrollPane(tasksPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(null);

            panel.add(scrollPane); // Thêm JScrollPane vào panel chính

            // Lưu tasksPanel vào Map
            workspaceTasksPanels.put(workspaceId, tasksPanel);

            // Lắng nghe thay đổi từ Firestore
            CollectionReference tasksRef = db.collection("Workspace").document(workspaceId).collection("Task");
            tasksRef.addSnapshotListener((querySnapshot, error) -> {
                if (error != null) {
                    System.err.println("Error fetching tasks: " + error.getMessage());
                    return;
                }

                SwingUtilities.invokeLater(() -> {
                    tasksPanel.removeAll();

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Task task = document.toObject(Task.class);
                            createTaskTile(task, tasksPanel, userRole);
                        }
                    } else {
                        JLabel noTasksLabel = new JLabel("No tasks available for this workspace.", JLabel.CENTER);
                        noTasksLabel.setFont(new Font("Segoe UI", Font.ITALIC, 18));
                        noTasksLabel.setForeground(Color.GRAY);
                        tasksPanel.add(noTasksLabel);
                    }

                    tasksPanel.revalidate();
                    tasksPanel.repaint();
                });
            });
        }

    } catch (InterruptedException | ExecutionException ex) {
        ex.printStackTrace();
    }

    return panel;
}


   
    

    public static class AIPopupPanel extends JPanel {
        private JPanel tasksContainer; // Panel chứa danh sách tasks
        private final Map<String, JPanel> workspaceTasksPanels = new HashMap<>();

        public AIPopupPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(new Color(30, 30, 30)); // nền giống workspace
    
            // Tiêu đề (header)
            JLabel headerLabel = new JLabel("HERE ARE SUGGESTIONS BASED ON YOUR HABITS", JLabel.CENTER);
            headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            headerLabel.setForeground(Color.WHITE);
            headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
            add(headerLabel);
            add(Box.createVerticalStrut(20));
    
            // Panel chứa tasks
            tasksContainer = new JPanel();
            tasksContainer.setLayout(new BoxLayout(tasksContainer, BoxLayout.Y_AXIS));
            tasksContainer.setBackground(new Color(30, 30, 30));
    
            // JScrollPane bọc quanh tasksContainer
            JScrollPane scrollPane = new JScrollPane(tasksContainer,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
            scrollPane.getViewport().setBackground(new Color(30, 30, 30));
            scrollPane.setBorder(null);
    
            add(scrollPane);
            setPreferredSize(new Dimension(300, 400));
        }
        
        private JPanel getTasksPanel(String workspaceId) {
            return workspaceTasksPanels.get(workspaceId);
        }

        private static void reloadWorkspaceTasks(String workspaceId, JPanel tasksPanel, String userRole) {
                    Firestore db = FirestoreClient.getFirestore();
                    CollectionReference tasksRef = db.collection("Workspace").document(workspaceId).collection("Task");
                
                    tasksRef.get().addListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ApiFuture<QuerySnapshot> future = tasksRef.get();
                                QuerySnapshot querySnapshot = future.get();
                
                                SwingUtilities.invokeLater(() -> {
                                    tasksPanel.removeAll(); // Xóa các task cũ khỏi giao diện
                
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        for (QueryDocumentSnapshot document : querySnapshot) {
                                            Task task = document.toObject(Task.class);
                                            createTaskTile(task, tasksPanel, userRole);
                                                                            }
                                                                        } else {
                                                                            JLabel noTasksLabel = new JLabel("No tasks available for this workspace.", JLabel.CENTER);
                                                                            noTasksLabel.setFont(new Font("Segoe UI", Font.ITALIC, 18));
                                                                            noTasksLabel.setForeground(Color.GRAY);
                                                                            tasksPanel.add(noTasksLabel);
                                                                        }
                                                    
                                                                        tasksPanel.revalidate();
                                                                        tasksPanel.repaint();
                                                                    });
                                                                } catch (Exception ex) {
                                                                    ex.printStackTrace();
                                                                    JOptionPane.showMessageDialog(null, "Error reloading tasks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                                                }
                                                            }
                                                        }, null);
                                                    }
                                                    
                                                    
                                                    public boolean loadTasks(List<SuggestedTask> suggestedTasks, String workspaceId) {
                                                        // Xóa các task cũ nếu có
                                                        tasksContainer.removeAll();
                                                    
                                                        if (suggestedTasks.isEmpty()) {
                                                            // Không có tasks -> Hiển thị thông báo
                                                            JLabel noTasksLabel = new JLabel("No suggestions available.", JLabel.CENTER);
                                                            noTasksLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                                                            noTasksLabel.setForeground(Color.WHITE);
                                                            tasksContainer.add(noTasksLabel);
                                                    
                                                            tasksContainer.revalidate();
                                                            tasksContainer.repaint();
                                                            return false; // Không load được task
                                                        } else {
                                                            // Hiển thị các tasks dạng panel
                                                            for (SuggestedTask t : suggestedTasks) {
                                                                tasksContainer.add(Box.createVerticalStrut(10));
                                                    
                                                                // Tạo panel chính cho task
                                                                JPanel taskPanel = new JPanel();
                                                                taskPanel.setLayout(new BorderLayout());
                                                                taskPanel.setBackground(new Color(45, 45, 45));
                                                                taskPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
                                                                taskPanel.setMaximumSize(new Dimension(800, 150)); // Đặt chiều cao tăng lên
                                                                taskPanel.setPreferredSize(new Dimension(800, 150)); // Đảm bảo kích thước ổn định
                                                    
                                                                // Tạo panel trái chứa thông tin task
                                                                JPanel taskInfoPanel = new JPanel();
                                                                taskInfoPanel.setLayout(new BoxLayout(taskInfoPanel, BoxLayout.Y_AXIS));
                                                                taskInfoPanel.setBackground(new Color(45, 45, 45));
                                                                taskInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding bên trong
                                                    
                                                                JLabel titleLabel = new JLabel(t.title);
                                                                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                                                                titleLabel.setForeground(Color.WHITE);
                                                    
                                                                JLabel descriptionLabel = new JLabel("<html><div style='width: 600px;'>" + t.description + "</div></html>");
                                                                descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                                                                descriptionLabel.setForeground(Color.LIGHT_GRAY);
                                                    
                                                                String dueDateStr;
                                                                if (t.dueDate != null) {
                                                                    // Chuyển đổi từ giây sang mili giây
                                                                    Date date = new Date(Long.parseLong(t.dueDate) * 1000L);
                                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                                    dueDateStr = sdf.format(date);
                                                                } else {
                                                                    dueDateStr = "N/A";
                                                                }

                                                                JLabel dueDateLabel = new JLabel("Due: " + dueDateStr);
                                                                dueDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                                                                dueDateLabel.setForeground(Color.GRAY);
                                                    
                                                                JLabel priorityLabel = new JLabel("Priority: " + t.priority);
                                                                priorityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                                                                priorityLabel.setForeground(getPriorityColor(t.priority));
                                                    
                                                                JPanel tagsPanel = new JPanel();
                                                                tagsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                                                                tagsPanel.setBackground(new Color(45, 45, 45));
                                                                tagsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Thêm padding giữa tags và các thành phần khác
                                                    
                                                                if (t.tagsname != null && !t.tagsname.isEmpty()) {
                                                                    for (String tag : t.tagsname) {
                                                                        JLabel tagLabel = new JLabel(tag);
                                                                        tagLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                                                                        tagLabel.setForeground(Color.WHITE);
                                                                        tagLabel.setOpaque(true);
                                                                        tagLabel.setBackground(new Color(0, 0, 128));
                                                                        tagLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                                                                        tagsPanel.add(tagLabel);
                                                                    }
                                                                } else {
                                                                    JLabel noTagLabel = new JLabel("No Tags");
                                                                    noTagLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                                                                    noTagLabel.setForeground(Color.GRAY);
                                                                    tagsPanel.add(noTagLabel);
                                                                }
                                                    
                                                                // Thêm thông tin task vào panel trái
                                                                taskInfoPanel.add(titleLabel);
                                                                taskInfoPanel.add(Box.createVerticalStrut(5));
                                                                taskInfoPanel.add(descriptionLabel);
                                                                taskInfoPanel.add(Box.createVerticalStrut(5));
                                                                taskInfoPanel.add(dueDateLabel);
                                                                taskInfoPanel.add(Box.createVerticalStrut(5));
                                                                taskInfoPanel.add(priorityLabel);
                                                                taskInfoPanel.add(Box.createVerticalStrut(5));
                                                                taskInfoPanel.add(tagsPanel);
                                                    
                                                                // Tạo nút "+"
                                                                JButton addButton = new JButton("+");
                                                                addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
                                                                addButton.setForeground(Color.WHITE);
                                                                addButton.setBackground(new Color(0, 128, 0)); // Màu xanh
                                                                addButton.setFocusPainted(false);
                                                                addButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                                                                addButton.setPreferredSize(new Dimension(40, 40)); // Kích thước nút cộng
                                                    
                                                                addButton.addActionListener(e -> {
                                                                    System.out.println("Add button clicked for task: " + t.title);
                                                                
                                                                    // Tạo đối tượng Task mới từ SuggestedTask
                                                                    Task newTask = new Task();
                                                                    newTask.setTaskID(UUID.randomUUID().toString());
                                                                    newTask.setTitle(t.title);
                                                                    newTask.setDescription(t.description);
                                                                    newTask.setTagsname(t.tagsname);
                                                                    newTask.setWorkspaceId(workspaceId);
                                                                
                                                                    // Xử lý priority với chuẩn hóa
                                                                    String normalizedPriority = t.priority.trim().toUpperCase().replace(" ", "_");
                                                                    try {
                                                                        newTask.setPriority(Priority.valueOf(normalizedPriority));
                                                                    } catch (IllegalArgumentException ex) {
                                                                        System.err.println("Invalid priority value for task: " + t.priority);
                                                                        JOptionPane.showMessageDialog(null, "Invalid priority value. Cannot add task.", "Error", JOptionPane.ERROR_MESSAGE);
                                                                        return;
                                                                    }
                                                                
                                                                    // Parse dueDate từ chuỗi
                                                                    try {
                                                                        if (t.dueDate != null) {
                                                                            long dueDateSeconds = Long.parseLong(t.dueDate);
                                                                            newTask.setDueDate(new Date(dueDateSeconds * 1000));
                                                                        }
                                                                    } catch (NumberFormatException ex) {
                                                                        System.err.println("Error parsing due date for task: " + t.title);
                                                                        newTask.setDueDate(null);
                                                                    }
                                                                
                                                                    // Lưu task vào Firestore
                                                                    saveTaskChanges(
                                                                        newTask,
                                                                        newTask.getTitle(),
                                                                        newTask.getDescription(),
                                                                        newTask.getDueDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(newTask.getDueDate()) : "",
                                                                        newTask.getPriority().toString(),
                                                                        "New",
                                                                        newTask.getTagsname()
                                                                    );
                                                                
                                                                    // Xóa task khỏi giao diện AI Popup
                                                                    tasksContainer.remove(taskPanel);
                                                                    tasksContainer.revalidate();
                                                                    tasksContainer.repaint();
                                                                
                                                                    SwingUtilities.invokeLater(() -> {
                                                                        // Lấy tasksPanel từ HashMap
                                                                        JPanel tasksPanel = workspaceTasksPanels.get(workspaceId);
                                                                        if (tasksPanel != null) {
                                                                            reloadWorkspaceTasks(workspaceId, tasksPanel, "Owner"); // Gọi phương thức từ instance hiện tại
                                                                        } else {
                                                                            System.err.println("Error: Tasks panel for workspace " + workspaceId + " not found.");
                                                                        }
                                                                    });
                                                                });
                                                                
                                                    
                                                                // Thêm panel trái vào giữa và nút "+" vào phải
                                                                taskPanel.add(taskInfoPanel, BorderLayout.CENTER);
                                                                taskPanel.add(addButton, BorderLayout.EAST);
                                                    
                                                                // Thêm task panel vào container
                                                                tasksContainer.add(taskPanel);
                                                            }
                                                    
                                                            tasksContainer.add(Box.createVerticalStrut(10));
                                                            tasksContainer.revalidate();
                                                            tasksContainer.repaint();
                                                            return true; // Load thành công
                                                        }
                                                    }
                                                    
                                                                
                                            
                                                                
                                                                
                                                                            
                                                    
                                                                    private Color getPriorityColor(String priority) {
                                                                        switch (priority.toUpperCase()) {
                                                                            case "HIGH":
                                                                                return Color.RED;
                                                                            case "MEDIUM":
                                                                                return Color.ORANGE;
                                                                            case "LOW":
                                                                                return Color.GREEN;
                                                                            default:
                                                                                return Color.GRAY;
                                                                        }
                                                                    }
                                                                
                                                                    // Lớp chứa dữ liệu cho một suggested task
                                                                    static class SuggestedTask {
                                                                        String title;
                                                                        String description;
                                                                        List<String> tagsname;
                                                                        String priority;
                                                                        String dueDate;
                                                                
                                                                        SuggestedTask(String title, String description, List<String> tagsname, String priority, String dueDate) {
                                                                            this.title = title;
                                                                            this.description = description;
                                                                            this.tagsname = tagsname;
                                                                            this.priority = priority;
                                                                            this.dueDate = dueDate;
                                                                        }
                                                                    }
                                                                }
                                                                        
                                                                        
                                                                        
                                                                    
                                                                        class LoadingSpinner extends JPanel {
                                                                            private int angle = 0; // Góc xoay hiện tại
                                                                            private Timer timer;
                                                                        
                                                                            public LoadingSpinner() {
                                                                                // Tạo Timer để cập nhật góc xoay và vẽ lại
                                                                                timer = new Timer(50, e -> {
                                                                                    angle += 10; // Tăng góc xoay
                                                                                    if (angle >= 360) {
                                                                                        angle = 0;
                                                                                    }
                                                                                    repaint(); // Yêu cầu vẽ lại
                                                                                });
                                                                                timer.start();
                                                                            }
                                                                        
                                                                            @Override
                                                                            protected void paintComponent(Graphics g) {
                                                                                super.paintComponent(g);
                                                                                Graphics2D g2d = (Graphics2D) g;
                                                                        
                                                                                // Chống răng cưa
                                                                                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                                                        
                                                                                // Lấy kích thước panel
                                                                                int width = getWidth();
                                                                                int height = getHeight();
                                                                        
                                                                                // Tính tâm và bán kính
                                                                                int centerX = width / 2;
                                                                                int centerY = height / 2;
                                                                                int radius = Math.min(width, height) / 4;
                                                                        
                                                                                // Tạo hiệu ứng xoay
                                                                                g2d.setColor(Color.GRAY);
                                                                                g2d.setStroke(new BasicStroke(6));
                                                                        
                                                                                for (int i = 0; i < 12; i++) {
                                                                                    float alpha = (i + angle / 30) % 12 / 12f; // Hiệu ứng mờ dần
                                                                                    g2d.setColor(new Color(0, 0, 0, alpha));
                                                                        
                                                                                    double theta = Math.toRadians(i * 30 + angle);
                                                                                    int x1 = (int) (centerX + radius * Math.cos(theta));
                                                                                    int y1 = (int) (centerY + radius * Math.sin(theta));
                                                                        
                                                                                    int x2 = (int) (centerX + (radius - 10) * Math.cos(theta));
                                                                                    int y2 = (int) (centerY + (radius - 10) * Math.sin(theta));
                                                                        
                                                                                    g2d.drawLine(x1, y1, x2, y2);
                                                                                }
                                                                            }
                                                                        }
                                                                        
                                                                        // Sử dụng LoadingSpinner trong dialog Loading
                                                                        class LoadingDialog extends JDialog {
                                                                            public LoadingDialog(Frame owner) {
                                                                                super(owner, "Loading", true);
                                                                                setLayout(new BorderLayout());
                                                                        
                                                                                LoadingSpinner spinner = new LoadingSpinner();
                                                                                spinner.setPreferredSize(new Dimension(100, 100));
                                                                                add(spinner, BorderLayout.CENTER);
                                                                        
                                                                                JLabel label = new JLabel("Loading suggestions, please wait...", JLabel.CENTER);
                                                                                label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                                                                                add(label, BorderLayout.SOUTH);
                                                                        
                                                                                pack();
                                                                                setLocationRelativeTo(owner);
                                                                            }
                                                                        } 
                                                                    
                                                                        
                                                                    
                                                                    /**
                                                                     * Opens a dialog to add a new task with tags.
                                                                     */
                                                                    private void openAddTaskDialog(String workspaceId) {
                                                                        // Create a new task object for the new task
                                                                        Task newTask = new Task();
                                                                        newTask.setWorkspaceId(workspaceId); // Provide workspace ID
                                                                    
                                                                        // Create a dialog for adding a new task
                                                                        JDialog addDialog = new JDialog((Frame) null, "Add Task", true);
                                                                        addDialog.setSize(500, 600);
                                                                        addDialog.setLocationRelativeTo(null);
                                                                        addDialog.setLayout(new BorderLayout());
                                                                    
                                                                        // Create a panel for form inputs
                                                                        JPanel formPanel = new JPanel();
                                                                        formPanel.setLayout(new GridBagLayout());
                                                                        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                                                                        GridBagConstraints gbc = new GridBagConstraints();
                                                                        gbc.insets = new Insets(5, 5, 5, 5);
                                                                        gbc.fill = GridBagConstraints.HORIZONTAL;
                                                                    
                                                                        // Title
                                                                        JLabel titleLabel = new JLabel("Title:");
                                                                        JTextField titleField = new JTextField(20);
                                                                        gbc.gridx = 0;
                                                                        gbc.gridy = 0;
                                                                        formPanel.add(titleLabel, gbc);
                                                                        gbc.gridx = 1;
                                                                        formPanel.add(titleField, gbc);
                                                                    
                                                                        // Description
                                                                        JLabel descriptionLabel = new JLabel("Description:");
                                                                        JTextArea descriptionArea = new JTextArea(5, 20);
                                                                        descriptionArea.setLineWrap(true);
                                                                        descriptionArea.setWrapStyleWord(true);
                                                                        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
                                                                        gbc.gridx = 0;
                                                                        gbc.gridy = 1;
                                                                        formPanel.add(descriptionLabel, gbc);
                                                                        gbc.gridx = 1;
                                                                        formPanel.add(descriptionScroll, gbc);
                                                                    
                                                                        // Due Date
                                                                        JLabel dueDateLabel = new JLabel("Due Date (yyyy-MM-dd):");
                                                                        JTextField dueDateField = new JTextField(20);
                                                                        gbc.gridx = 0;
                                                                        gbc.gridy = 2;
                                                                        formPanel.add(dueDateLabel, gbc);
                                                                        gbc.gridx = 1;
                                                                        formPanel.add(dueDateField, gbc);
                                                                    
                                                                        // Priority
                                                                        JLabel priorityLabel = new JLabel("Priority:");
                                                                        JComboBox<Priority> priorityCombo = new JComboBox<>(Priority.values());
                                                                        gbc.gridx = 0;
                                                                        gbc.gridy = 3;
                                                                        formPanel.add(priorityLabel, gbc);
                                                                        gbc.gridx = 1;
                                                                        formPanel.add(priorityCombo, gbc);
                                                                    
                                                                        // Status
                                                                        JLabel statusLabel = new JLabel("Status:");
                                                                        JComboBox<Status> statusCombo = new JComboBox<>(Status.values());
                                                                        gbc.gridx = 0;
                                                                        gbc.gridy = 4;
                                                                        formPanel.add(statusLabel, gbc);
                                                                        gbc.gridx = 1;
                                                                        formPanel.add(statusCombo, gbc);
                                                                    
                                                                        // Tags
                                                                        JLabel tagsLabel = new JLabel("Tags:");
                                                                        List<String> existingTags = fetchWorkspaceTags(newTask.getWorkspaceId());
                                                                        DefaultListModel<String> tagListModel = new DefaultListModel<>();
                                                                        for (String tag : existingTags) {
                                                                            tagListModel.addElement(tag);
                                                                        }
                                                                        JList<String> existingTagsList = new JList<>(tagListModel);
                                                                        existingTagsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                                                                        JScrollPane tagsScrollPane = new JScrollPane(existingTagsList);
                                                                    
                                                                        JTextField newTagField = new JTextField(15);
                                                                        JButton addTagButton = new JButton("Add Tag");
                                                                    
                                                                        JPanel tagsPanel = new JPanel(new BorderLayout());
                                                                        tagsPanel.add(tagsScrollPane, BorderLayout.CENTER);
                                                                    
                                                                        JPanel addTagPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                                                                        addTagPanel.add(new JLabel("New Tag:"));
                                                                        addTagPanel.add(newTagField);
                                                                        addTagPanel.add(addTagButton);
                                                                        tagsPanel.add(addTagPanel, BorderLayout.SOUTH);
                                                                    
                                                                        gbc.gridx = 0;
                                                                        gbc.gridy = 5;
                                                                        formPanel.add(tagsLabel, gbc);
                                                                        gbc.gridx = 1;
                                                                        formPanel.add(tagsPanel, gbc);
                                                                    
                                                                        // Add form panel to dialog
                                                                        addDialog.add(formPanel, BorderLayout.CENTER);
                                                                    
                                                                        // Create Save and Gen AI buttons
                                                                        JButton saveButton = new JButton("Save");
                                                                        JButton genAIButton = new JButton("Gen AI");
                                                                    
                                                                        // Action listener for Save button
                                                                        saveButton.addActionListener(e -> {
                                                                            String newTitle = titleField.getText().trim();
                                                                            String newDescription = descriptionArea.getText().trim();
                                                                            
                                                                            String newDueDateStr = dueDateField.getText().trim(); // Input in "YYYY-MM-DD"
                                                    
                                                                            String newPriority = ((Priority) priorityCombo.getSelectedItem()).toString();
                                                                            String newStatus = ((Status) statusCombo.getSelectedItem()).name();
                                                                        
                                                                            // Process tags
                                                                            List<String> selectedTags = existingTagsList.getSelectedValuesList();
                                                                            String newTag = newTagField.getText().trim();
                                                                            if (!newTag.isEmpty() && !selectedTags.contains(newTag)) {
                                                                                selectedTags.add(newTag);
                                                                            }
                                                                        
                                                                            if (newTitle.isEmpty()) {
                                                                                JOptionPane.showMessageDialog(addDialog, "Title cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                                                                                return;
                                                                            }
                                                                        
                                                                            try {
                                                                                // Save task to Firestore
                                                                                saveTaskChanges(newTask, newTitle, newDescription, newDueDateStr, newPriority, newStatus, selectedTags);
                                                                        
                                                                                // If a due date is provided, create a reminder
                                                                                if (!newDueDateStr.isEmpty()) {
                                                                                    try {
                                                                                        // Parse the due date from the input string
                                                                                        Date dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(newDueDateStr);
                                                                                        String recurrencePattern = "NONE"; // Adjust as needed

                                                                                        // Create a Reminder instance
                                                                                        Reminder reminder = UserService.createReminderInstance(
                                                                                            newTask.getTaskID(),
                                                                                            recurrencePattern,
                                                                                            dueDate,
                                                                                            currentUser,
                                                                                            newTitle
                                                                                        );

                                                                                        // Add the reminder to Firestore using WorkspaceService
                                                                                        WorkspaceService workspaceService = new WorkspaceService();

                                                                                        // Use workspace ID from the current task or context
                                                                                        String reminderId = reminder.getReminderID(); // Replace with reminder's unique ID

                                                                                        // Prepare reminder data for Firestore
                                                                                        Map<String, Object> reminderData = new HashMap<>();
                                                                                        reminderData.put("taskID", reminder.getTaskID());
                                                                                        reminderData.put("recurrencePattern", reminder.getRecurrencePattern());
                                                                                        reminderData.put("dueDate", reminder.getDueDate());
                                                                                        reminderData.put("createdBy", currentUser.getUserId());
                                                                                        reminderData.put("title", reminder.getTitle());

                                                                                        // Call the addReminderToWorkspace function
                                                                                        boolean success = workspaceService.addReminderToWorkspace(workspaceId, reminderId, reminderData);

                                                                                        if (success) {
                                                                                            System.out.println("Reminder added successfully to workspace: " + workspaceId);
                                                                                        } else {
                                                                                            System.out.println("Failed to add reminder to workspace: " + workspaceId);
                                                                                        }



                                                                                    } catch (Exception er) {
                                                                                        er.printStackTrace();
                                                                                        System.err.println("Error while adding reminder: " + er.getMessage());
                                                                                    }
                                                                                }

                                                                        
                                                                                addDialog.dispose();
                                                                                JOptionPane.showMessageDialog(null, "Task and reminder saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                                                            } catch (Exception ex) {
                                                                                ex.printStackTrace();
                                                                                JOptionPane.showMessageDialog(addDialog, "Error saving task or reminder: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                                                            }
                                                                        });
                                                                        

                                                                    
                                                                        
                                                                     
                                                                            // Action listener for Gen AI button
                                                                            genAIButton.addActionListener(e -> {
                                                                                String inputTitle = titleField.getText().trim();

                                                                                if (inputTitle.isEmpty()) {
                                                                                    JOptionPane.showMessageDialog(addDialog, "Title cannot be empty for Gen AI.", "Input Error", JOptionPane.ERROR_MESSAGE);
                                                                                    return;
                                                                                }

                                                                                // Tạo JDialog để hiển thị spinner
                                                                                JDialog loadingDialog = new JDialog((Frame) null, "Generating AI Task", true);
                                                                                JPanel loadingPanel = new JPanel();
                                                                                loadingPanel.setLayout(new BorderLayout());
                                                                                JLabel loadingLabel = new JLabel("Generating task with AI, please wait...", SwingConstants.CENTER);
                                                                                JLabel spinnerLabel = new JLabel(new ImageIcon("src/main/resources/Animation - 1734265581861.gif")); // Đường dẫn đến spinner GIF
                                                                                spinnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                                                                loadingPanel.add(loadingLabel, BorderLayout.NORTH);
                                                                                loadingPanel.add(spinnerLabel, BorderLayout.CENTER);
                                                                                loadingDialog.getContentPane().add(loadingPanel);
                                                                                loadingDialog.setSize(300, 150);
                                                                                loadingDialog.setLocationRelativeTo(addDialog);

                                                                                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                                                                                    @Override
                                                                                    protected Void doInBackground() throws Exception {
                                                                                        ProcessBuilder processBuilder = new ProcessBuilder("python3", "src/main/resources/addtask_by_ai.py");
                                                                                        processBuilder.redirectErrorStream(true);
                                                                                        Process process = processBuilder.start();

                                                                                        // Gửi inputTitle tới script Python qua stdin
                                                                                        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                                                                                            JSONObject jsonInput = new JSONObject();
                                                                                            jsonInput.put("title", inputTitle);
                                                                                            writer.write(jsonInput.toString());
                                                                                            writer.flush();
                                                                                        }

                                                                                        // Đọc kết quả JSON từ script Python
                                                                                        StringBuilder jsonOutput = new StringBuilder();
                                                                                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                                                                                            String line;
                                                                                            while ((line = reader.readLine()) != null) {
                                                                                                jsonOutput.append(line);
                                                                                            }
                                                                                        }

                                                                                        // Làm sạch đầu ra JSON
                                                                                        String rawOutput = jsonOutput.toString().trim();
                                                                                        System.out.println("Raw API Output: " + rawOutput);

                                                                                        // Loại bỏ các phần không hợp lệ, ví dụ: `"json`
                                                                                        if (rawOutput.startsWith("```json")) {
                                                                                            rawOutput = rawOutput.substring(rawOutput.indexOf("{"), rawOutput.lastIndexOf("}") + 1);
                                                                                        }

                                                                                        System.out.println("Cleaned API Output: " + rawOutput);

                                                                                        // Parse JSON để lấy các trường cần thiết
                                                                                        JSONObject aiGeneratedTask = new JSONObject(rawOutput);
                                                                                        String newDescription = aiGeneratedTask.getString("description");
                                                                                        String newPriority = aiGeneratedTask.getString("priority").toUpperCase();

                                                                                        // Chuyển đổi priority về dạng hợp lệ
                                                                                        switch (newPriority) {
                                                                                            case "HIGH":
                                                                                            case "HIGH PRIORITY":
                                                                                                newPriority = "HIGH";
                                                                                                break;
                                                                                            case "MEDIUM":
                                                                                            case "MEDIUM PRIORITY":
                                                                                                newPriority = "MEDIUM";
                                                                                                break;
                                                                                            case "LOW":
                                                                                            case "LOW PRIORITY":
                                                                                                newPriority = "LOW";
                                                                                                break;
                                                                                            default:
                                                                                                throw new IllegalArgumentException("Invalid priority value: " + newPriority);
                                                                                        }

                                                                                        // Tags
                                                                                        JSONArray tagsArray = aiGeneratedTask.getJSONArray("tagsname");
                                                                                        List<String> tags = new ArrayList<>();
                                                                                        for (int i = 0; i < tagsArray.length(); i++) {
                                                                                            tags.add(tagsArray.getString(i));
                                                                                        }

                                                                                        // Parse dueDate
                                                                                        JSONObject dueDateJson = aiGeneratedTask.getJSONObject("dueDate");
                                                                                        long dueDateSeconds = dueDateJson.getLong("seconds");
                                                                                        Date newDueDate = new Date(dueDateSeconds * 1000);

                                                                                        // Lưu Task vào Firestore
                                                                                        saveTaskChanges(newTask, inputTitle, newDescription, new SimpleDateFormat("yyyy-MM-dd").format(newDueDate), newPriority, "New", tags);
                                                                                        // Create reminder
                                                                                        Reminder reminder = UserService.createReminderInstance(newTask.getTaskID(), "NONE", newDueDate, currentUser, inputTitle);
                                                                                        currentUser.addReminder(reminder);
                                                                                        return null;
                                                                                    }

                                                                                    @Override
                                                                                    protected void done() {
                                                                                        // Ẩn spinner sau khi hoàn thành
                                                                                        loadingDialog.dispose();

                                                                                        try {
                                                                                            get(); // Kiểm tra lỗi trong doInBackground()
                                                                                            JOptionPane.showMessageDialog(addDialog, "Task generated and saved successfully.", "Gen AI Success", JOptionPane.INFORMATION_MESSAGE);
                                                                                            addDialog.dispose();
                                                                                        } catch (Exception ex) {
                                                                                            ex.printStackTrace();
                                                                                            JOptionPane.showMessageDialog(addDialog, "Failed to generate task: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                                                                        }
                                                                                    }
                                                                                };

                                                                                // Hiển thị spinner và chạy worker
                                                                                SwingUtilities.invokeLater(() -> {
                                                                                    loadingDialog.setVisible(true); // Hiển thị spinner
                                                                                    worker.execute(); // Bắt đầu xử lý AI
                                                                                });
                                                                            });

                                                                    
                                                                    

                                                                        
                                                                    
                                                                        // Add buttons to button panel
                                                                        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                                                                        buttonPanel.add(genAIButton);
                                                                        buttonPanel.add(saveButton);
                                                                    
                                                                        // Add button panel to dialog
                                                                        addDialog.add(buttonPanel, BorderLayout.SOUTH);
                                                                    
                                                                        // Show the dialog
                                                                        addDialog.setVisible(true);
                                                                    }
                                                                    
                                                                    
                                                            
                                                            
                                                            
                                                                    private static void createTaskTile(Task task, JPanel panel, String userRole) {
                                                                        JPanel taskTile = new JPanel();
                                                                        taskTile.setLayout(new BoxLayout(taskTile, BoxLayout.Y_AXIS));
                                                                        taskTile.setBackground(new Color(45, 45, 45));
                                                                        taskTile.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
                                                                        taskTile.setMaximumSize(new Dimension(600, 200));
                                                                    
                                                                        JLabel titleLabel = new JLabel(task.getTitle());
                                                                        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                                                                        titleLabel.setForeground(Color.WHITE);
                                                                    
                                                                        JLabel descriptionLabel = new JLabel("<html><div style='width: 500px;'>" + task.getDescription() + "</div></html>");
                                                                        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                                                                        descriptionLabel.setForeground(Color.LIGHT_GRAY);
                                                                    
                                                                        JLabel dueDateLabel = new JLabel("Due: " + (task.getDueDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(task.getDueDate()) : "N/A"));
                                                                        dueDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                                                                        dueDateLabel.setForeground(Color.GRAY);
                                                                    
                                                                        JLabel priorityLabel = new JLabel("Priority: " + task.getPriority());
                                                                        priorityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                                                                        priorityLabel.setForeground(getPriorityColor(task.getPriority()));
                                                                    
                                                                        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                                                                        tagsPanel.setBackground(new Color(45, 45, 45));
                                                                    
                                                                        if (task.getTagsname() != null && !task.getTagsname().isEmpty()) {
                                                                            for (String tag : task.getTagsname()) {
                                                                                JLabel tagLabel = new JLabel(tag);
                                                                                tagLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                                                                                tagLabel.setForeground(Color.WHITE);
                                                                                tagLabel.setOpaque(true);
                                                                                tagLabel.setBackground(new Color(0, 0, 128));
                                                                                tagLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                                                                                tagsPanel.add(tagLabel);
                                                                            }
                                                                        }
                                                                    
                                                                        taskTile.add(titleLabel);
                                                                        taskTile.add(Box.createVerticalStrut(5));
                                                                        taskTile.add(descriptionLabel);
                                                                        taskTile.add(Box.createVerticalStrut(5));
                                                                        taskTile.add(dueDateLabel);
                                                                        taskTile.add(Box.createVerticalStrut(5));
                                                                        taskTile.add(priorityLabel);
                                                                        taskTile.add(Box.createVerticalStrut(5));
                                                                        taskTile.add(tagsPanel);
                                                                    
                                                                        // Add a MouseListener to make the tile clickable
                                                                        taskTile.addMouseListener(new MouseAdapter() {
                                                                            @Override
                                                                            public void mouseClicked(MouseEvent e) {
                                                                                openEditDialog(task, taskTile);
                                                                            }
                                                                    
                                                                            @Override
                                                                            public void mouseEntered(MouseEvent e) {
                                                                                taskTile.setBackground(new Color(60, 60, 60)); // Highlight effect on hover
                                                                            }
                                                                    
                                                                            @Override
                                                                            public void mouseExited(MouseEvent e) {
                                                                                taskTile.setBackground(new Color(45, 45, 45)); // Revert back to original color
                                                                            }
                                                                        });
                                                                    
                                                                        panel.add(taskTile);
                                                                        panel.add(Box.createVerticalStrut(10));
                                                                    }
                                                                    
                                                    
                                                        
                                                        
                                                    
                                                        /**
                                                     * Opens a dialog to edit the task details, including tags.
                                                     *
                                                     * @param task     The task to edit.
                                                     * @param taskTile The JPanel representing the task tile to update after editing.
                                                     */
                                                    static void openEditDialog(Task task, JPanel taskTile) {
                                                        // Create a modal dialog
                                                        JDialog editDialog = new JDialog((Frame) null, "Edit Task", true);
                                                        editDialog.setSize(500, 600);
                                                        editDialog.setLocationRelativeTo(null);
                                                        editDialog.setLayout(new BorderLayout());
                                                    
                                                        // Create a panel for form inputs
                                                        JPanel formPanel = new JPanel();
                                                        formPanel.setLayout(new GridBagLayout());
                                                        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                                                        GridBagConstraints gbc = new GridBagConstraints();
                                                        gbc.insets = new Insets(5,5,5,5);
                                                        gbc.fill = GridBagConstraints.HORIZONTAL;
                                                    
                                                        // Title
                                                        JLabel titleLabel = new JLabel("Title:");
                                                        JTextField titleField = new JTextField(task.getTitle(), 20);
                                                        gbc.gridx = 0;
                                                        gbc.gridy = 0;
                                                        formPanel.add(titleLabel, gbc);
                                                        gbc.gridx = 1;
                                                        formPanel.add(titleField, gbc);
                                                    
                                                        // Description
                                                        JLabel descriptionLabel = new JLabel("Description:");
                                                        JTextArea descriptionArea = new JTextArea(task.getDescription(), 5, 20);
                                                        descriptionArea.setLineWrap(true);
                                                        descriptionArea.setWrapStyleWord(true);
                                                        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
                                                        gbc.gridx = 0;
                                                        gbc.gridy = 1;
                                                        formPanel.add(descriptionLabel, gbc);
                                                        gbc.gridx = 1;
                                                        formPanel.add(descriptionScroll, gbc);
                                                    
                                                        // Due Date
                                                        JLabel dueDateLabel = new JLabel("Due Date (yyyy-MM-dd):");
                                                        JTextField dueDateField = new JTextField(task.getDueDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(task.getDueDate()) : "", 20);
                                                        gbc.gridx = 0;
                                                        gbc.gridy = 2;
                                                        formPanel.add(dueDateLabel, gbc);
                                                        gbc.gridx = 1;
                                                        formPanel.add(dueDateField, gbc);
                                                    
                                                        // Priority
                                                        JLabel priorityLabel = new JLabel("Priority:");
                                                        JComboBox<Priority> priorityCombo = new JComboBox<>(Priority.values());
                                                        priorityCombo.setSelectedItem(task.getPriority());
                                                        gbc.gridx = 0;
                                                        gbc.gridy = 3;
                                                        formPanel.add(priorityLabel, gbc);
                                                        gbc.gridx = 1;
                                                        formPanel.add(priorityCombo, gbc);
                                                    
                                                        // Status
                                                        JLabel statusLabel = new JLabel("Status:");
                                                        JComboBox<Status> statusCombo = new JComboBox<>(Status.values());
                                                        statusCombo.setSelectedItem(task.getStatus());
                                                        gbc.gridx = 0;
                                                        gbc.gridy = 4;
                                                        formPanel.add(statusLabel, gbc);
                                                        gbc.gridx = 1;
                                                        formPanel.add(statusCombo, gbc);
                                                    
                                                        // Tags
                                                        JLabel tagsLabel = new JLabel("Tags:");
                                                        // Fetch existing workspace tags
                                                        java.util.List<String> existingTags = fetchWorkspaceTags(task.getWorkspaceId());
                                                    
                                                        // Create a list model and populate with existing tags
                                                        DefaultListModel<String> tagListModel = new DefaultListModel<>();
                                                        for (String tag : existingTags) {
                                                            tagListModel.addElement(tag);
                                                        }
                                                    
                                                        // JList for existing tags with multiple selection
                                                        JList<String> existingTagsList = new JList<>(tagListModel);
                                                        existingTagsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                                                        existingTagsList.setVisibleRowCount(5);
                                                        JScrollPane tagsScrollPane = new JScrollPane(existingTagsList);
                                                    
                                                        // Text field to add a new tag
                                                        JTextField newTagField = new JTextField(15);
                                                        JButton addTagButton = new JButton("Add Tag");
                                                    
                                                        // Panel to hold existing tags list and new tag field
                                                        JPanel tagsPanel = new JPanel();
                                                        tagsPanel.setLayout(new BorderLayout());
                                                        tagsPanel.add(tagsScrollPane, BorderLayout.CENTER);
                                                    
                                                        JPanel addTagPanel = new JPanel();
                                                        addTagPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                                                        addTagPanel.add(new JLabel("New Tag:"));
                                                        addTagPanel.add(newTagField);
                                                        addTagPanel.add(addTagButton);
                                                        tagsPanel.add(addTagPanel, BorderLayout.SOUTH);
                                                    
                                                        // Pre-select the task's existing tags in the list
                                                        if (task.getTagsname() != null && !task.getTagsname().isEmpty()) {
                                                            int[] selectedIndices = task.getTagsname().stream()
                                                                    .mapToInt(tag -> existingTags.indexOf(tag))
                                                                    .filter(index -> index >= 0)
                                                                    .toArray();
                                                            existingTagsList.setSelectedIndices(selectedIndices);
                                                        }
                                                    
                                                        gbc.gridx = 0;
                                                        gbc.gridy = 5;
                                                        formPanel.add(tagsLabel, gbc);
                                                        gbc.gridx = 1;
                                                        formPanel.add(tagsPanel, gbc);
                                                    
                                                        // Add form panel to dialog
                                                        editDialog.add(formPanel, BorderLayout.CENTER);
                                                    
                                                        // Create Save button
                                                        JButton saveButton = new JButton("Save");
                                                        editDialog.getRootPane().setDefaultButton(saveButton); // Make Save button respond to Enter key
                                                    
                                                        // Action listener for adding a new tag
                                                        addTagButton.addActionListener(e -> {
                                                            String newTag = newTagField.getText().trim();
                                                            if (!newTag.isEmpty() && !tagListModel.contains(newTag)) {
                                                                tagListModel.addElement(newTag);
                                                                existingTagsList.setSelectedValue(newTag, true);
                                                                newTagField.setText("");
                                                            } else if (tagListModel.contains(newTag)) {
                                                                JOptionPane.showMessageDialog(editDialog, "Tag already exists in workspace.", "Duplicate Tag", JOptionPane.WARNING_MESSAGE);
                                                            }
                                                        });
                                                    
                                                        saveButton.addActionListener(e -> {
                                                            String newTitle = titleField.getText().trim();
                                                            String newDescription = descriptionArea.getText().trim();
                                                            String newDueDate = dueDateField.getText().trim();
                                                            String newPriority = ((Priority) priorityCombo.getSelectedItem()).name();
                                                            String newStatus = ((Status) statusCombo.getSelectedItem()).name();
                                                    
                                                            // Get selected existing tags
                                                            List<String> selectedExistingTags = existingTagsList.getSelectedValuesList();
                                                    
                                                            // Get any new tag added during this session
                                                            String newTag = newTagField.getText().trim();
                                                            if (!newTag.isEmpty() && !tagListModel.contains(newTag)) {
                                                                selectedExistingTags.add(newTag);
                                                            }
                                                    
                                                            // Remove duplicates if any
                                                            List<String> uniqueTags = selectedExistingTags.stream().distinct().collect(Collectors.toList());
                                                    
                                                            // Validate input (optional)
                                                            if (newTitle.isEmpty()) {
                                                                JOptionPane.showMessageDialog(editDialog, "Title cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                                                                return;
                                                            }
                                                    
                                                            // Save changes to Firestore
                                                            saveTaskChanges(task, newTitle, newDescription, newDueDate, newPriority, newStatus, uniqueTags);
                                                    
                                                            // Close the dialog
                                                            editDialog.dispose();
                                                        });
                                                    
                                                        // Add Save button to dialog
                                                        JPanel buttonPanel = new JPanel();
                                                        buttonPanel.add(saveButton);
                                                        editDialog.add(buttonPanel, BorderLayout.SOUTH);
                                                    
                                                        // Show the dialog
                                                        editDialog.setVisible(true);
                                                    }
                                                    
                                                    /**
                                                     * Saves the updated task information to Firestore and updates workspace tags if necessary.
                                                     *
                                                     * @param task          The task to update.
                                                     * @param newTitle      The new title of the task.
                                                     * @param newDescription The new description of the task.
                                                     * @param newDueDate    The new due date as a string.
                                                     * @param newPriority   The new priority as a string.
                                                     * @param newStatus     The new status as a string.
                                                     * @param newTags       The updated list of tags for the task.
                                                     */
                                                    private static void saveTaskChanges(Task task, String newTitle, String newDescription, String newDueDate, String newPriority, String newStatus, List<String> newTags) {
                                                        System.out.println("=== saveTaskChanges Started ===");
                                                        System.out.println("Task ID: " + task.getTaskID());
                                                        System.out.println("Workspace ID: " + task.getWorkspaceId());
                                                        System.out.println("New Title: " + newTitle);
                                                        System.out.println("New Description: " + newDescription);
                                                        System.out.println("New Due Date: " + newDueDate);
                                                        System.out.println("New Priority: " + newPriority);
                                                        System.out.println("New Status: " + newStatus);
                                                        System.out.println("New Tags: " + newTags);
                                                    
                                                        Firestore db = FirestoreClient.getFirestore();
                                                        WriteBatch batch = db.batch();
                                                    
                                                        try {
                                                            // Kiểm tra Task ID, nếu chưa có thì tạo mới
                                                            if (task.getTaskID() == null || task.getTaskID().isEmpty()) {
                                                                String autoGeneratedId = db.collection("Workspace")
                                                                                           .document(task.getWorkspaceId())
                                                                                           .collection("Task")
                                                                                           .document()
                                                                                           .getId();
                                                                task.setTaskID(autoGeneratedId);
                                                                System.out.println("Generated new Task ID: " + autoGeneratedId);
                                                            }
                                                    
                                                            // Update the task object with the new values
                                                            System.out.println("Updating task fields...");
                                                            task.setTitle(newTitle);
                                                            task.setDescription(newDescription);
                                                    
                                                            // Chuyển đổi chuỗi hiển thị priority sang enum
                                                            try {
                                                                task.setPriority(convertToPriorityEnum(newPriority));
                                                            } catch (IllegalArgumentException e) {
                                                                System.err.println("Invalid priority value: " + newPriority);
                                                                throw new IllegalArgumentException("Invalid priority value: " + newPriority + ". Valid values are: HIGH, MEDIUM, LOW.");
                                                            }
                                                    
                                                            // Chuyển đổi Status
                                                            try {
                                                                task.setStatus(Status.valueOf(newStatus));
                                                            } catch (IllegalArgumentException e) {
                                                                System.err.println("Invalid status value: " + newStatus);
                                                                throw new IllegalArgumentException("Invalid status value: " + newStatus + ".");
                                                            }
                                                    
                                                            System.out.println("Task fields updated.");
                                                    
                                                            // Parse the due date string and set it in the task
                                                            try {
                                                                System.out.println("Parsing due date...");
                                                                Date dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(newDueDate);
                                                                task.setDueDate(dueDate);
                                                                System.out.println("Due date set to: " + dueDate);
                                                            } catch (ParseException e) {
                                                                System.out.println("Invalid due date format: " + newDueDate + ". Setting due date to null.");
                                                                task.setDueDate(null); // If the date format is invalid, set it as null
                                                            }
                                                    
                                                            // Update the task's tags
                                                            System.out.println("Updating task's tags...");
                                                            task.setTagsname(newTags);
                                                            System.out.println("Task's tags updated to: " + newTags);
                                                    
                                                            // Reference to the task document
                                                            DocumentReference taskRef = db.collection("Workspace")
                                                                                          .document(task.getWorkspaceId())
                                                                                          .collection("Task")
                                                                                          .document(task.getTaskID());
                                                            batch.set(taskRef, task);
                                                            System.out.println("Task document reference obtained: " + taskRef.getPath());
                                                    
                                                            // Fetch existing workspace tags using the provided function
                                                            System.out.println("Fetching existing workspace tags...");
                                                            List<String> existingWorkspaceTags = fetchWorkspaceTags(task.getWorkspaceId());
                                                    
                                                            // Debug existing workspace tags
                                                            System.out.println("Existing Workspace Tags: " + existingWorkspaceTags);
                                                    
                                                            // Identify new tags to add to the workspace
                                                            System.out.println("Identifying new tags to add to workspace...");
                                                            List<String> tagsToAdd = newTags.stream()
                                                                                            .filter(tag -> !existingWorkspaceTags.contains(tag))
                                                                                            .collect(Collectors.toList());
                                                            System.out.println("Tags to add to workspace: " + tagsToAdd);
                                                    
                                                            if (!tagsToAdd.isEmpty()) {
                                                                System.out.println("Adding new tags to workspace...");
                                                                batch.update(db.collection("Workspace").document(task.getWorkspaceId()), "tags", FieldValue.arrayUnion(tagsToAdd.toArray()));
                                                                System.out.println("New tags added to workspace.");
                                                            } else {
                                                                System.out.println("No new tags to add to workspace.");
                                                            }
                                                    
                                                            // Commit the batch
                                                            System.out.println("Committing batch update to Firestore...");
                                                            ApiFuture<List<WriteResult>> writeResult = batch.commit();
                                                    
                                                            // Wait for the commit to complete
                                                            List<WriteResult> results = writeResult.get();
                                                            System.out.println("Batch commit completed. Write results:");
                                                            for (WriteResult result : results) {
                                                                System.out.println("Update Time: " + result.getUpdateTime());
                                                            }
                                                    
                                                            // Notify user of success
                                                            JOptionPane.showMessageDialog(null, "Task updated successfully.");
                                                            System.out.println("=== saveTaskChanges Completed Successfully ===");
                                                    
                                                        } catch (Exception e) {
                                                            System.err.println("=== Error in saveTaskChanges ===");
                                                            e.printStackTrace();
                                                            JOptionPane.showMessageDialog(null, "Error saving task: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                                                        }
                                                    }
                                                    
                                                    
                                                    // Hàm chuyển đổi priority từ chuỗi sang enum Priority
                                                    private static Priority convertToPriorityEnum(String priority) {
                                                        switch (priority.toUpperCase()) {
                                                            case "HIGH":
                                                            case "HIGH PRIORITY":
                                                                return Priority.HIGH;
                                                            case "MEDIUM":
                                                            case "MEDIUM PRIORITY":
                                                                return Priority.MEDIUM;
                                                            case "LOW":
                                                            case "LOW PRIORITY":
                                                                return Priority.LOW;
                                                            default:
                                                                throw new IllegalArgumentException("Invalid priority value: " + priority);
                                                        }
                                                    }
                                                    
                                                    
                                    
                                    
                                            /**
                                         * Fetches existing tags from the workspace in Firestore.
                                         *
                                         * @param workspaceId The ID of the workspace.
                                         * @return A list of existing tags.
                                         */
                                        private static List<String> fetchWorkspaceTags(String workspaceId) {
                                    Firestore db = FirestoreClient.getFirestore();
                                    DocumentReference workspaceRef = db.collection("Workspace").document(workspaceId);
                                    ApiFuture<DocumentSnapshot> future = workspaceRef.get();
                                    try {
                                        DocumentSnapshot document = future.get();
                                        if (document.exists()) {
                                            List<String> tags = (List<String>) document.get("tags");
                                            return tags != null ? tags : new ArrayList<>();
                                        } else {
                                            return new ArrayList<>();
                                        }
                                    } catch (InterruptedException | ExecutionException e) {
                                        e.printStackTrace();
                                        JOptionPane.showMessageDialog(null, "Error fetching workspace tags: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                        return new ArrayList<>();
                                    }
                                }
                                
                                // Helper function to return priority color
                                private static Color getPriorityColor(Priority priority) {
		switch (priority) {
			case HIGH:
				return Color.RED; // High priority is red
			case MEDIUM:
				return Color.YELLOW; // Medium priority is yellow
			case LOW:
				return Color.GREEN; // Low priority is green
			default:
				return Color.GRAY; // Default color
		}
	}
	
	// Helper function to return status color
	// Helper function to return status color
private Color getStatusColor(Status status) {
    switch (status) {
        case Completed:
            return Color.GREEN; // Completed tasks are green
        case Pending:
            return Color.ORANGE; // Pending tasks are orange
        case Todo:
            return Color.BLUE; // Todo tasks are blue
        case New:
            return Color.CYAN; // New tasks are cyan (or any color you'd like)
        case Overdue:
            return Color.RED; // Overdue tasks are red
        case Archived:
            return Color.GRAY; // Archived tasks are gray
        default:
            return Color.GRAY; // Default color
    }
}

	

    // Method to switch between views in the content area (using CardLayout)
    // Method to switch between views in the content area (using CardLayout)
// Method to switch between views in the content area (using CardLayout)
public void switchContentView(String viewName, String workspaceId) {
    // Get the CardLayout panel from the content pane
    JPanel contentPanel = (JPanel) getContentPane().getComponent(1); // Ensure the contentPanel is at index 1
    CardLayout cardLayout = (CardLayout) contentPanel.getLayout();

	// Clear the content panel before adding the new view
    contentPanel.removeAll();
    contentPanel.revalidate();  // Revalidate to apply changes
    contentPanel.repaint();     // Repaint to ensure it's updated

    // Log the current view and the requested view for debugging
    System.out.println("switchContentView called. Requested view: " + viewName + " with workspaceId: " + workspaceId);
    
    // Show the desired view (either Home or Workspace)
    cardLayout.show(contentPanel, viewName);
    Sidebar sidebar = new Sidebar(currentUser, new Sidebar.OnWorkspaceSwitchListener() {
        @Override
        public void onWorkspaceSwitch(String viewName, String workspaceId) {
            // Call the switchContentView method in the Home class
            switchContentView(viewName, workspaceId); // 'Home.this' refers to the outer Home instance
        }
    });
    // If switching to Workspace view, update the workspace panel with the correct ID
    if (viewName.equals("Workspace")) {
        System.out.println("Switching to Workspace view. Workspace ID: " + workspaceId);

        // Fetch the workspace details and create a workspace panel with the provided ID
        JPanel workspacePanel = createWorkspacePanel(workspaceId, sidebar);

        // Add the workspace panel to the content panel
        contentPanel.add(workspacePanel, "Workspace");
        
        // Revalidate and repaint to ensure the changes are rendered
        contentPanel.revalidate();
        contentPanel.repaint();
        
        System.out.println("Workspace panel added with ID: " + workspaceId);
    } else if (viewName.equals("Home")) {
        // If switching to Home, we simply show the home view without adding or removing anything
        System.out.println("Switching to Home view.");
		JPanel homePanel = createHomepagePanel();
		contentPanel.add(homePanel, "Home");
        // No need to re-add the home panel, just show it
        cardLayout.show(contentPanel, "Home");

        System.out.println("Home view displayed.");
    }
}

public void DeleteWorkspace(String workspaceId) {
    // Initialize Firebase Firestore instance
    Firestore db = FirestoreClient.getFirestore();
    // Delete the workspace from the Workspace collection
    try {
        // Delete the workspace from the Workspace collection
        ApiFuture<WriteResult> deleteFuture = db.collection("Workspace").document(workspaceId).delete();
        deleteFuture.get(); // Wait for the delete operation to complete
        System.out.println("Workspace deleted successfully!");

        // Update the user's WorkspacesId field
        String currentUserId = currentUser.getUserId(); // Replace with your method to get the current user's ID
        DocumentReference userDocRef = db.collection("User").document(currentUserId);

        ApiFuture<WriteResult> updateFuture = userDocRef.update("workspacesId", FieldValue.arrayRemove(workspaceId));
        updateFuture.get(); // Wait for the update operation to complete
        System.out.println("Workspace ID removed from user's document successfully!");

    } catch (ExecutionException | InterruptedException e) {
        System.err.println("Error occurred during Firestore operation: " + e.getMessage());
        Thread.currentThread().interrupt(); // Restore interrupted state
    }
}

private void showAddCollaboratorDialog(String workspaceId) {
    // Create a panel for the dialog
    JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
    panel.setPreferredSize(new Dimension(400, 150));

    // Input fields
    JTextField emailField = new JTextField();
    JComboBox<WorkspaceRole> roleComboBox = new JComboBox<>(WorkspaceRole.values());

    // Add components to the panel
    panel.add(new JLabel("Gmail:"));
    panel.add(emailField);
    panel.add(new JLabel("Role:"));
    panel.add(roleComboBox);

    int result = JOptionPane.showConfirmDialog(null, panel, "Add Collaborator",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
        String email = emailField.getText().trim();
        WorkspaceRole selectedRole = (WorkspaceRole) roleComboBox.getSelectedItem();

        if (isValidEmail(email)) {
            addCollaboratorToWorkspace(workspaceId, email, selectedRole);
        } else {
            JOptionPane.showMessageDialog(null, "Invalid Gmail address.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

private boolean isValidEmail(String email) {
    // Basic email validation (replace with a regex or more robust validation if needed)
    return email.contains("@") && email.endsWith(".com");
}

private void addCollaboratorToWorkspace(String workspaceId, String email, WorkspaceRole role) {
    Firestore db = FirestoreClient.getFirestore();

    // Step 1: Retrieve the user ID from the Users collection based on the email
    ApiFuture<QuerySnapshot> queryFuture = db.collection("User").whereEqualTo("email", email).get();

    try {
        QuerySnapshot querySnapshot = queryFuture.get();

        if (querySnapshot.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No user found with the provided email.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Assuming each email is unique and maps to one user
        String userId = querySnapshot.getDocuments().get(0).getId();

        // Step 2: Update the userRoles map in the Workspace document
        DocumentReference workspaceDoc = db.collection("Workspace").document(workspaceId);
        ApiFuture<WriteResult> updateUserRolesFuture = workspaceDoc.update("userRoles." + userId, role.name());

        updateUserRolesFuture.get(); // Wait for completion

        // Step 3: Add the workspaceId to the collaborator's document under workspacesId
        DocumentReference userDoc = db.collection("User").document(userId);
        ApiFuture<WriteResult> updateWorkspaceIdsFuture = userDoc.update("workspacesId", FieldValue.arrayUnion(workspaceId));

        updateWorkspaceIdsFuture.get(); // Wait for completion

        // Notify the user of success
        JOptionPane.showMessageDialog(null, "Collaborator added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

    } catch (InterruptedException | ExecutionException e) {
        System.err.println("Error adding collaborator: " + e.getMessage());
        JOptionPane.showMessageDialog(null, "Error adding collaborator: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        Thread.currentThread().interrupt(); // Restore interrupted state
    }
}

private void saveUpdatedWorkspaceName(String updatedName, String workspaceId) {
    Firestore db = FirestoreClient.getFirestore();

    if (workspaceId == null || workspaceId.isEmpty()) {
        System.err.println("Workspace ID is null or empty. Cannot update workspace name.");
        return;
    }

    // Reference to the current workspace document
    DocumentReference workspaceDoc = db.collection("Workspace").document(workspaceId);

    // Update the name field
    ApiFuture<WriteResult> updateFuture = workspaceDoc.update("name", updatedName);

    try {
        WriteResult result = updateFuture.get();
        System.out.println("Workspace name updated successfully at: " + result.getUpdateTime());
    } catch (InterruptedException | ExecutionException e) {
        System.err.println("Error updating workspace name: " + e.getMessage());
        Thread.currentThread().interrupt(); // Restore interrupted status
    }
}

private void saveUpdatedWorkspaceDescription(String updatedDescription, String workspaceId) {
    Firestore db = FirestoreClient.getFirestore();

    if (workspaceId == null || workspaceId.isEmpty()) {
        System.err.println("Workspace ID is null or empty. Cannot update workspace description.");
        return;
    }

    // Reference to the current workspace document
    DocumentReference workspaceDoc = db.collection("Workspace").document(workspaceId);

    // Update the description field
    ApiFuture<WriteResult> updateFuture = workspaceDoc.update("description", updatedDescription);

    try {
        WriteResult result = updateFuture.get();
        System.out.println("Workspace description updated successfully at: " + result.getUpdateTime());
    } catch (InterruptedException | ExecutionException e) {
        System.err.println("Error updating workspace description: " + e.getMessage());
        Thread.currentThread().interrupt(); // Restore interrupted status
    }
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Dummy User object for testing purposes
            User testUser = new User("userId123", "lamthanhz", "test@example.com", "password", "1990-01-01", 1, "1234567890", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

            // Create and show the homepage with the dummy user
            Home ui = new Home(testUser);
            ui.setVisible(true);
        });
    }
}
