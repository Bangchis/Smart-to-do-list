package com.smarttodo.ui;

import javax.swing.*;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.smarttodo.firebase.FirebaseConfig;
import com.smarttodo.task.model.Priority;
import com.smarttodo.task.model.Status;
import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;
import com.smarttodo.user.service.UserService;
import com.smarttodo.workspace.model.Workspace;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;
import java.util.concurrent.Executors;

public class Sidebar extends JPanel {
    private User currentUser;
    private Homepage homepage;

    public Sidebar(User user) {
        FirebaseConfig.initializeFirebase();
        this.currentUser = user;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));  // Align buttons vertically
        setBackground(new Color(40, 40, 40));

        // Add profile section - Center username label horizontally
        JLabel profileLabel = new JLabel(currentUser.getUsername(), JLabel.CENTER);
        profileLabel.setForeground(Color.WHITE);
        profileLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        profileLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        profileLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center username label
        add(profileLabel);

        // Add Sidebar buttons
        String[] sidebarOptions = {"Search", "Smart Assistant", "Home", "Create Workspace", "Add Reminder"};
        for (String option : sidebarOptions) {
            JButton button = createSidebarButton(option);
            if (option.equals("Create Workspace")) {
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Call the method to create a new workspace
                        createWorkspace();
                    }
                });
            } else if (option.equals("Add Reminder")) {
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Open the AddReminderPage dialog and pass the currentUser
                        new AddReminderPage((Frame) SwingUtilities.getWindowAncestor(Sidebar.this), currentUser);
                    }
                });
            }
            add(button);
        }

        add(Box.createVerticalGlue()); // Add vertical space between sections

        // Add "My Workspaces" label
        JLabel workspacesLabel = new JLabel("My Workspaces");
        workspacesLabel.setForeground(Color.WHITE);
        workspacesLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        workspacesLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center workspaces label
        add(workspacesLabel);

        // Assuming currentUser has a method to get the list of workspace IDs
        List<String> workspaceIDs = currentUser.getWorkspacesId();  // Get workspace IDs from currentUser

        // Loop through each workspaceId and create a button for each workspace
        for (String workspaceId : workspaceIDs) {
            String workspaceName = getWorkspaceName(workspaceId);  // Assuming you have a way to get the workspace name
            JButton workspaceButton = createWorkspaceButton(workspaceName, workspaceId);  // Create a button with name and workspace ID
            workspaceButton.addActionListener(e -> {
                // Redirect to the workspace page when button is clicked
                System.out.println("Redirecting to workspace: " + workspaceId);
                homepage.redirectToWorkspacePage(workspaceId);
            });
            add(workspaceButton);  // Add the button to the sidebar
        }


        add(Box.createVerticalGlue());  // Push remaining buttons to the bottom

        // Add more options like Calendar, Settings, etc.
        String[] moreOptions = {"Calendar", "Settings", "Logout"};
        for (String option : moreOptions) {
            JButton button = createSidebarButton(option);
            add(button);
        }

        // Set fixed width for the sidebar in pixels (e.g., 250px)
        setFixedSidebarWidth();
    }

    // Method to create a workspace with default values and add to Firebase
    private void createWorkspace() {
        try {
            String workspaceId = UUID.randomUUID().toString();
                    // Call WorkspaceService to create a new Workspace instance and save to Firestore   
            Workspace workspace = UserService.createWorkspaceInstance(workspaceId,"New Page", "Fresh new page");
            currentUser.createnewWorkspace(workspaceId,"New Page", "Fresh new page");
            currentUser.addWorkspacesId(workspaceId);
            JOptionPane.showMessageDialog(null, "Workspace added successfully.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Failed to add workspace: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private String getWorkspaceName(String workspaceId) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference workspaceDocRef = db.collection("Workspace").document(workspaceId);
            DocumentSnapshot snapshot = workspaceDocRef.get().get();  // Retrieve the document

            if (snapshot.exists()) {
                // Return the workspace name
                return snapshot.getString("name");
            } else {
                System.out.println("Workspace not found for ID: " + workspaceId);
                return "Workspace Name Not Found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching workspace name";
        }
    }
    

    private JButton createWorkspaceButton(String workspaceName, String workspaceId) {
        JButton button = new JButton(workspaceName);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center the button horizontally
        button.setMaximumSize(new Dimension(200, 40));     // Set maximum size for button
        button.setBackground(new Color(0, 0, 0));          // Default background color
        button.setForeground(Color.WHITE);                  // Text color
        button.setFocusPainted(false);                      // Remove focus ring
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Font settings
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding for button text
    
        // Hover effect (changing background color)
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(80, 80, 80)); // Lighter grey shade when hovered
                button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand pointer
            }
    
            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(60, 60, 60)); // Revert to default color when mouse leaves
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // Reset cursor to default
            }
    
            @Override
            public void mousePressed(MouseEvent evt) {
                button.setBackground(new Color(100, 100, 100)); // Change color when button is pressed
            }
    
            @Override
            public void mouseReleased(MouseEvent evt) {
                button.setBackground(new Color(80, 80, 80)); // Revert to hover color when mouse released
            }
        });
    
        // Add ActionListener to handle click event and redirect to workspace page
        button.addActionListener(e -> homepage.redirectToWorkspacePage(workspaceId));
    
        return button;
    }
    

    // Method to create a styled button for the sidebar
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center the button horizontally
        button.setMaximumSize(new Dimension(200, 40));     // Set maximum size for button
        button.setBackground(new Color(0, 0, 0));       // Default background color
        button.setForeground(Color.WHITE);                  // Text color
        button.setFocusPainted(false);                      // Remove focus ring
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Font settings
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding for button text

        // Hover effect (changing background color)
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(80, 80, 80)); // Lighter grey shade when hovered
                button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand pointer
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(60, 60, 60)); // Revert to default color when mouse leaves
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // Reset cursor to default
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                button.setBackground(new Color(100, 100, 100)); // Change color when button is pressed
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                button.setBackground(new Color(80, 80, 80)); // Revert to hover color when mouse released
            }
        });

        return button;
    }

    // Method to set a fixed width for the sidebar (in pixels)
    private void setFixedSidebarWidth() {
        int fixedSidebarWidth = 220;  // Set a fixed width for the sidebar in pixels (e.g., 250px)
        setPreferredSize(new Dimension(fixedSidebarWidth, getHeight()));  // Set fixed width and current height
    }
}
