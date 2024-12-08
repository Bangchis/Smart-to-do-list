package com.smarttodo.ui;

import com.smarttodo.firebase.FirebaseConfig;
import com.smarttodo.task.model.Priority;
import com.smarttodo.task.model.Status;
import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;
import com.smarttodo.workspace.model.Workspace;

import javafx.event.ActionEvent;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import javax.swing.*;
import java.awt.*;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Home extends JFrame {

    private User currentUser;

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
        JPanel workspacePanel = createWorkspacePanel("a7d69ce4-4648-49f4-9208-cef26b9ee440"
		);
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

    // Create the Homepage view panel
    private JPanel createHomepagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername(), JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        panel.setBackground(new Color(30, 30, 30));
        panel.add(welcomeLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createWorkspacePanel(String workspaceId) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical stacking
    
        // Set background color and padding for the main content
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Optional padding for content
    
        // Fetch workspace data from Firestore using the workspaceId
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference workspaceDocRef = db.collection("Workspace").document(workspaceId);
        ApiFuture<DocumentSnapshot> future = workspaceDocRef.get();
    
        try {
            DocumentSnapshot workspaceDoc = future.get();
            if (workspaceDoc.exists()) {
                String workspaceName = workspaceDoc.getString("name");
                String workspaceDescription = workspaceDoc.getString("description");
    
                // Create the App Bar with fixed height and no padding or margins
                JPanel appBar = new JPanel();
                appBar.setLayout(new BorderLayout()); // Use BorderLayout for the app bar
                appBar.setBackground(new Color(50, 50, 50)); // Dark background color for the app bar
                appBar.setPreferredSize(new Dimension(panel.getWidth(), 50)); // Fixed height of 50px
                appBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Ensure it doesn't grow vertically
    
                // Add Task Button with "+" icon on the Right
                JButton addTaskButton = new JButton("+");
                addTaskButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                addTaskButton.setForeground(Color.BLACK);
                addTaskButton.setBackground(new Color(60, 60, 60));
                addTaskButton.setFocusPainted(false); // Remove focus painting
                addTaskButton.setPreferredSize(new Dimension(50, 50)); // IconButton size
                addTaskButton.addActionListener(e -> openAddTaskDialog(workspaceId)); // Open dialog on button click
    
                // Add the button to the app bar, aligned to the right
                appBar.add(addTaskButton, BorderLayout.EAST);
    
                // Add the app bar to the main panel
                panel.add(appBar);
    
                // Add some space between the app bar and the workspace name
                panel.add(Box.createVerticalStrut(20)); // Add 20px of vertical space
    
                // Create the Workspace Name Label in the Main Content
                JLabel workspaceLabel = new JLabel("<html><b>" + workspaceName + "</b><br>" + workspaceDescription + "</html>", JLabel.LEFT);
                workspaceLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                workspaceLabel.setForeground(Color.WHITE);
    
                // Add workspace name/description to the panel
                panel.add(workspaceLabel);
    
                // Add some space between the workspace name and task tiles
                panel.add(Box.createVerticalStrut(20)); // Add 20px of vertical space
    
                // Create a panel for the tasks
                JPanel tasksPanel = new JPanel();
                tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS)); // Stack tasks vertically
                tasksPanel.setBackground(new Color(30, 30, 30));
    
                // Fetch the tasks for the workspace from the 'Tasks' subcollection
                CollectionReference tasksRef = db.collection("Workspace").document(workspaceId).collection("Task");
    
                // Use a real-time listener to detect changes in the tasks collection
                tasksRef.addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        System.err.println("Error fetching tasks: " + error.getMessage());
                        return;
                    }
    
                    // Clear the current tasks from the panel
                    tasksPanel.removeAll();
    
                    // If there are tasks, add them to the panel
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Task task = document.toObject(Task.class);
                            createTaskTile(task, tasksPanel); // Create a task tile for each task
                        }
                    } else {
                        // No tasks in the workspace, show a message
                        JLabel noTasksLabel = new JLabel("No tasks available for this workspace.", JLabel.CENTER);
                        noTasksLabel.setFont(new Font("Segoe UI", Font.ITALIC, 18));
                        noTasksLabel.setForeground(Color.GRAY);
                        tasksPanel.add(noTasksLabel);
                    }
    
                    // Revalidate and repaint the panel to apply changes
                    tasksPanel.revalidate();
                    tasksPanel.repaint();
    
                    // Add tasksPanel to the main panel
                    panel.add(tasksPanel);
    
                    // Revalidate and repaint the main panel to apply all changes
                    panel.revalidate();
                    panel.repaint();
                });
            }
    
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    
        return panel;
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

    // Create a panel for form inputs (similar to the edit dialog)
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

    // Tags (similar to existing code)
    JLabel tagsLabel = new JLabel("Tags:");
    // Fetch existing workspace tags
    List<String> existingTags = fetchWorkspaceTags(newTask.getWorkspaceId());
    DefaultListModel<String> tagListModel = new DefaultListModel<>();
    for (String tag : existingTags) {
        tagListModel.addElement(tag);
    }

    JList<String> existingTagsList = new JList<>(tagListModel);
    existingTagsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    existingTagsList.setVisibleRowCount(5);
    JScrollPane tagsScrollPane = new JScrollPane(existingTagsList);

    JTextField newTagField = new JTextField(15);
    JButton addTagButton = new JButton("Add Tag");

    JPanel tagsPanel = new JPanel();
    tagsPanel.setLayout(new BorderLayout());
    tagsPanel.add(tagsScrollPane, BorderLayout.CENTER);

    JPanel addTagPanel = new JPanel();
    addTagPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
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

    // Create Save button for adding a new task
    JButton saveButton = new JButton("Save");
    addDialog.getRootPane().setDefaultButton(saveButton);

    // Action listener for adding a new tag
    addTagButton.addActionListener(e -> {
        String newTag = newTagField.getText().trim();
        if (!newTag.isEmpty() && !tagListModel.contains(newTag)) {
            tagListModel.addElement(newTag);
            existingTagsList.setSelectedValue(newTag, true);
            newTagField.setText("");
        } else if (tagListModel.contains(newTag)) {
            JOptionPane.showMessageDialog(addDialog, "Tag already exists in workspace.", "Duplicate Tag", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(addDialog, "Title cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a Firestore reference for the workspace
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference workspaceRef = db.collection("Workspace").document(workspaceId);
        
        // Create an initial Task
        Task initialTask = new Task();
        initialTask.setTaskID(UUID.randomUUID().toString());
        initialTask.setTitle("First Task");
        initialTask.setDescription("This is the first task and I'm excited");
        initialTask.setPriority(Priority.HIGH);
        initialTask.setStatus(Status.New);
        initialTask.setTagsname(new ArrayList<>()); // Adding a tag
        initialTask.setAssigneesIds(new ArrayList<>()); // Empty assignees list initially
        initialTask.setReminderIds(new ArrayList<>()); // Empty reminder list initially
        initialTask.setWorkspaceId(workspaceId);
        initialTask.setDueDate(new Date()); // Set the due date to the current date or modify accordingly
        
        // Save the Task into the subcollection under the workspace document
        CollectionReference tasksRef = workspaceRef.collection("Task");
        tasksRef.document(initialTask.getTaskID()).set(initialTask);

        // Save the new task to Firestore
        saveTaskChanges(initialTask, newTitle, newDescription, newDueDate, newPriority, newStatus, uniqueTags);

        // Close the dialog
        addDialog.dispose();
    });

    // Add Save button to dialog
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(saveButton);
    addDialog.add(buttonPanel, BorderLayout.SOUTH);

    // Show the dialog
    addDialog.setVisible(true);
}

    private void createTaskTile(Task task, JPanel panel) {
        // Create a task tile (a panel for each task)
        JPanel taskTile = new JPanel();
        taskTile.setLayout(new BoxLayout(taskTile, BoxLayout.Y_AXIS)); // Stack vertically
        taskTile.setBackground(new Color(45, 45, 45)); // Tile background color
        taskTile.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1)); // Border for the tile
        taskTile.setMaximumSize(new Dimension(600, 200)); // Increased height to accommodate tags
        taskTile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor to hand on hover
    
        // Title
        JLabel titleLabel = new JLabel(task.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
    
        // Description
        JLabel descriptionLabel = new JLabel("<html><div style='width: 500px;'>" + task.getDescription() + "</div></html>");
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionLabel.setForeground(Color.LIGHT_GRAY);
    
        // Due Date
        JLabel dueDateLabel = new JLabel("Due: " + (task.getDueDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(task.getDueDate()) : "N/A"));
        dueDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dueDateLabel.setForeground(Color.GRAY);
    
        // Priority
        JLabel priorityLabel = new JLabel("Priority: " + task.getPriority());
        priorityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        priorityLabel.setForeground(getPriorityColor(task.getPriority())); // Get the color based on priority
    
        // Status
        JLabel statusLabel = new JLabel("Status: " + task.getStatus());
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(getStatusColor(task.getStatus())); // Get the color based on status
    
        // Tags Panel
        JPanel tagsPanel = new JPanel();
        tagsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        tagsPanel.setBackground(new Color(45, 45, 45)); // Same background as task tile
    
        if (task.getTagsname() != null && !task.getTagsname().isEmpty()) {
            for (String tag : task.getTagsname()) {
                JLabel tagLabel = new JLabel(tag);
                tagLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                tagLabel.setForeground(Color.WHITE);
                tagLabel.setOpaque(true);
                tagLabel.setBackground(new Color(0, 0, 128)); // Dark blue background for tags
                tagLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                tagsPanel.add(tagLabel);
            }
        } else {
            JLabel noTagLabel = new JLabel("No Tags");
            noTagLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            noTagLabel.setForeground(Color.GRAY);
            tagsPanel.add(noTagLabel);
        }
    
        // Add the components to the task tile
        taskTile.add(titleLabel);
        taskTile.add(Box.createVerticalStrut(5)); // Add spacing between components
        taskTile.add(descriptionLabel);
        taskTile.add(Box.createVerticalStrut(5));
        taskTile.add(dueDateLabel);
        taskTile.add(Box.createVerticalStrut(5));
        taskTile.add(priorityLabel);
        taskTile.add(Box.createVerticalStrut(5));
        taskTile.add(statusLabel);
        taskTile.add(Box.createVerticalStrut(5));
        taskTile.add(tagsPanel); // Add Tags Panel
    
        // Add MouseListener to handle clicks for editing
        taskTile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openEditDialog(task, taskTile);
            }
        });
    
        // Add the task tile to the panel
        panel.add(taskTile);
        panel.add(Box.createVerticalStrut(10)); // Add spacing between tiles
    }
    
    

    /**
 * Opens a dialog to edit the task details, including tags.
 *
 * @param task     The task to edit.
 * @param taskTile The JPanel representing the task tile to update after editing.
 */
private void openEditDialog(Task task, JPanel taskTile) {
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
private void saveTaskChanges(Task task, String newTitle, String newDescription, String newDueDate, String newPriority, String newStatus, List<String> newTags) {
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
        // Update the task object with the new values
        System.out.println("Updating task fields...");
        task.setTitle(newTitle);
        task.setDescription(newDescription);
        task.setPriority(Priority.valueOf(newPriority)); // Convert the string to Priority enum
        task.setStatus(Status.valueOf(newStatus)); // Convert the string to Status enum
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


        /**
     * Fetches existing tags from the workspace in Firestore.
     *
     * @param workspaceId The ID of the workspace.
     * @return A list of existing tags.
     */
    private List<String> fetchWorkspaceTags(String workspaceId) {
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
	private Color getPriorityColor(Priority priority) {
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

    // If switching to Workspace view, update the workspace panel with the correct ID
    if (viewName.equals("Workspace")) {
        System.out.println("Switching to Workspace view. Workspace ID: " + workspaceId);

        // Fetch the workspace details and create a workspace panel with the provided ID
        JPanel workspacePanel = createWorkspacePanel(workspaceId);

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
