package com.smarttodo.ui;

import javax.swing.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.smarttodo.task.model.Priority;
import com.smarttodo.task.model.Status;
import com.smarttodo.task.model.Task;
import com.smarttodo.ui.Sidebar.OnWorkspaceSwitchListener;
import com.smarttodo.user.model.User;
import com.smarttodo.user.service.UserService;
import com.smarttodo.workspace.model.Workspace;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Sidebar extends JPanel {

    private User currentUser;
    private OnWorkspaceSwitchListener switchViewListener;

    public interface OnWorkspaceSwitchListener {
        void onWorkspaceSwitch(String viewName, String workspaceId);
    }

    public Sidebar(User user, OnWorkspaceSwitchListener listener) {
        this.currentUser = user;
        this.switchViewListener = listener;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Optional: Set the sidebar background color and size
        setBackground(new Color(50, 50, 50));
        setPreferredSize(new Dimension(200, 0)); // Adjust width as necessary
        
        // Create and add the "Add Reminder" button
        JButton addReminderButton = new JButton("Add Reminder");
        addReminderButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center button
        addReminderButton.setBackground(new Color(70, 130, 180)); // Optional: Add color to the button
        addReminderButton.setForeground(Color.BLACK);
        addReminderButton.setFocusPainted(false);
        
        // Add ActionListener to the button
        addReminderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the AddReminderPage dialog and pass the currentUser
                new AddReminderPage((Frame) SwingUtilities.getWindowAncestor(Sidebar.this), currentUser);
            }
        });
        
        add(addReminderButton); // Add the button to the sidebar

        // Create and add the "Create Workspace" button
        JButton createWorkspaceButton = new JButton("Create Workspace");
        createWorkspaceButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center button
        createWorkspaceButton.setBackground(new Color(70, 130, 180)); // Optional: Add color to the button
        createWorkspaceButton.setForeground(Color.BLACK);
        createWorkspaceButton.setFocusPainted(false);

        // Add ActionListener to the "Create Workspace" button
        createWorkspaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call the createWorkspace function when the button is clicked
                createWorkspace();
            }
        });
        
        add(createWorkspaceButton); // Add the button to the sidebar
        
        // Create and add the "Home" button to switch back to the home view
        JButton homeButton = new JButton("Home");
        homeButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center button
        homeButton.setBackground(new Color(50, 205, 50)); // Optional: Green button for Home
        homeButton.setForeground(Color.BLACK);
        homeButton.setFocusPainted(false);

        // Add ActionListener to switch back to the Home view
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (switchViewListener != null) {
                    switchViewListener.onWorkspaceSwitch("Home", null); // Passing null since we are going to the home view
                }
            }
        });

        add(homeButton); // Add the button to the sidebar
        
        // Fetch workspaces for the user
        fetchWorkspaces();
    }

    private void createWorkspace() {
        try {
            // Generate a new workspace ID
            String workspaceId = UUID.randomUUID().toString();
            
            // Call WorkspaceService to create a new Workspace instance and save to Firestore
            currentUser.createnewWorkspace(workspaceId, "New Workspace", "Fresh new workspace");
            currentUser.addWorkspacesId(workspaceId);

            // Create a Firestore reference for the workspace
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference workspaceRef = db.collection("Workspace").document(workspaceId);

            workspaceRef.update("tags", FieldValue.arrayUnion("Personal"));
            
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

            // Show success message
            JOptionPane.showMessageDialog(null, "Workspace and initial task added successfully.");
        } catch (Exception ex) {
            // Show error message if something goes wrong
            JOptionPane.showMessageDialog(null, "Failed to add workspace: " + ex.getMessage());
            ex.printStackTrace();
        }

        // After creating the workspace and task, reload the workspaces in the sidebar
        fetchWorkspaces();
    }

    // Fetch all workspaces for the current user from Firestore
    private void fetchWorkspaces() {
        // Firestore instance
        Firestore db = FirestoreClient.getFirestore();

        // Assuming that the "userId" field exists in the user document
        DocumentReference userDocRef = db.collection("User").document(currentUser.getUserId());
        ApiFuture<DocumentSnapshot> future = userDocRef.get();

        try {
            DocumentSnapshot userDoc = future.get();
            if (userDoc.exists()) {
                List<String> workspaceIds = (List<String>) userDoc.get("workspacesId");
                
                if (workspaceIds != null) {
                    // Remove all existing workspace buttons first
                    removeAllWorkspaceButtons();
                    
                    // Create a button for each workspace
                    for (String workspaceId : workspaceIds) {
                        createWorkspaceButton(workspaceId);
                    }
                }
            } else {
                System.out.println("User document does not exist");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // Create a button for a workspace
    private void createWorkspaceButton(String workspaceId) {
        JButton workspaceButton = new JButton("Workspace " + workspaceId);
        
        // Add ActionListener to switch to that workspace view
        workspaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (switchViewListener != null) {
                    switchViewListener.onWorkspaceSwitch("Workspace", workspaceId); // Switch to the specific workspace
                }
            }
        });

        // Add button to sidebar
        add(workspaceButton);
    }

    // Remove all existing workspace buttons
    private void removeAllWorkspaceButtons() {
        // Assuming all workspace buttons are added under this panel
        for (Component component : getComponents()) {
            if (component instanceof JButton && ((JButton) component).getText().startsWith("Workspace")) {
                remove(component);
            }
        }

        // Revalidate and repaint to ensure UI updates
        revalidate();
        repaint();
    }
}
