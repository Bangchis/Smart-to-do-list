package com.smarttodo.ui;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.smarttodo.firebase.FirebaseConfig;
import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;
import com.smarttodo.workspace.model.Workspace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Homepage extends JFrame {

    private User currentUser;

    public Homepage(User user) {
        FirebaseConfig.initializeFirebase();
        this.currentUser = user;

        // Frame settings
        setTitle("Client Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Get the screen size (width and height)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        // Set the frame to full screen (maximized)
        setSize(screenWidth, screenHeight);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Main layout for the frame
        setLayout(new BorderLayout());

        // Left sidebar panel (using Sidebar class)
        Sidebar sidebar = new Sidebar(currentUser);
        add(sidebar, BorderLayout.WEST);

        // Right main panel
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

        // Remove any border from the reminders panel (if set)
        remindersPanel.setBorder(null);

        // Add reminder buttons (total 5 reminders)
        Color[] colors = {
            new Color(70, 130, 180), // Steel Blue
            new Color(220, 20, 60),  // Crimson
            new Color(128, 0, 128),  // Purple
            new Color(255, 215, 0),  // Gold
            new Color(0, 128, 0)     // Green
        };

        // Create 5 reminder buttons
        for (int i = 0; i < 5; i++) {
            JButton reminderButton = new JButton("Reminder " + (i + 1));
            reminderButton.setPreferredSize(new Dimension(120, 100)); // Size for each reminder button
            reminderButton.setBackground(colors[i % colors.length]); // Cycle through colors
            reminderButton.setForeground(Color.WHITE);
            reminderButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            reminderButton.setFocusPainted(false);
            scrollableContainer.add(reminderButton);
        }

        // Set the preferred size of the scrollable container
        // This will center the buttons by adjusting the container size based on button width
        scrollableContainer.setPreferredSize(new Dimension(600, 120));  // 600px width to fit 5 buttons with 20px gaps

        // Scroll panel for reminders (only horizontal scroll)
        JScrollPane scrollPane = new JScrollPane(scrollableContainer,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,  // No vertical scrollbar
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  // Horizontal scrollbar always

        // Apply the custom scroll bar UI
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        remindersPanel.add(scrollPane);

        // Set a fixed height for the reminders panel (height can be adjusted)
        remindersPanel.setPreferredSize(new Dimension(800, 300));

        // Add the reminders panel to the main panel
        mainPanel.add(remindersPanel, BorderLayout.CENTER);

        // Calendar panel: Replace with CalendarPanel from external class
        CalendarPanel calendarPanel = new CalendarPanel();  // Using the CalendarPanel you created
        calendarPanel.setPreferredSize(new Dimension(800, 550)); // Adjust the size of the calendar
        mainPanel.add(calendarPanel, BorderLayout.SOUTH);

        // Add panels to the main frame
        add(mainPanel, BorderLayout.CENTER);

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

    public void redirectToWorkspacePage(String workspaceId) {
    if (workspaceId == null || workspaceId.isEmpty()) {
        System.out.println("Error: Workspace ID is null or empty");
        return;
    }

    try {
        // Retrieve workspace data from Firestore
        System.out.println("Fetching workspace for ID: " + workspaceId);
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference workspaceDocRef = db.collection("Workspace").document(workspaceId);
        DocumentSnapshot snapshot = workspaceDocRef.get().get();  // Retrieve the workspace document

        if (snapshot == null) {
            System.out.println("Error: Snapshot is null for Workspace ID: " + workspaceId);
            return;
        }

        if (!snapshot.exists()) {
            System.out.println("Workspace not found for ID: " + workspaceId);
            return;
        }

        // Retrieve workspace data
        String name = snapshot.getString("name");
        String description = snapshot.getString("description");
        String ownerId = snapshot.getString("ownerId");
        List<String> collaboratorIds = (List<String>) snapshot.get("collaboratorIds");

        // Check for null values
        if (name == null || description == null || ownerId == null || collaboratorIds == null) {
            System.out.println("Error: Some fields in the workspace snapshot are null");
            return;
        }

        // Create Workspace instance
        Workspace workspace = new Workspace(workspaceId, name, description, ownerId);
        workspace.setCollaborators(collaboratorIds);

        // Retrieve the tasks from the Task subcollection of this workspace
        List<Task> tasks = new ArrayList<>();
        CollectionReference taskCollection = workspaceDocRef.collection("Task");
        ApiFuture<QuerySnapshot> query = taskCollection.get();

        // Process the task documents
        List<QueryDocumentSnapshot> taskDocs = query.get().getDocuments();
        System.out.println("Number of tasks fetched: " + taskDocs.size());

        for (QueryDocumentSnapshot taskDoc : taskDocs) {
            Task task = taskDoc.toObject(Task.class);  // Convert each document to a Task object
            if (task == null) {
                System.out.println("Warning: Task is null for document ID: " + taskDoc.getId());
            } else {
                tasks.add(task);
            }
        }

        // Set the tasks for the workspace
        workspace.setTasks(tasks);

        // Create and display the WorkspaceUI
        WorkspaceUI workspaceUI = new WorkspaceUI(workspace);
        workspaceUI.setVisible(true);  // Show the workspace page (WorkspaceUI)

        // Dispose of the Homepage window (if no longer needed)
        this.dispose();  // Assuming `this` refers to the Homepage frame

    } catch (InterruptedException e) {
        System.out.println("Error: Firestore query was interrupted: " + e.getMessage());
        e.printStackTrace();
    } catch (ExecutionException e) {
        System.out.println("Error: Execution error while querying Firestore: " + e.getMessage());
        e.printStackTrace();
    } catch (Exception e) {
        System.out.println("General error while fetching workspace data: " + e.getMessage());
        e.printStackTrace();
    }
}

    
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Dummy User object for testing purposes
            User testUser = new User("userId123", "lamthanhz", "test@example.com", "password", "1990-01-01", 1, "1234567890", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

            // Create and show the homepage with the dummy user
            Homepage ui = new Homepage(testUser);
            ui.setVisible(true);
        });
    }
}
