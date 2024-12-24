package com.smarttodo.ui;

import javax.swing.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.smarttodo.task.model.Task;
import com.smarttodo.ui.Sidebar.OnWorkspaceSwitchListener;
import com.smarttodo.user.model.User;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Sidebar extends JPanel {

    private User currentUser;
    private OnWorkspaceSwitchListener switchViewListener;
    private JPanel workspacePanel;

    public interface OnWorkspaceSwitchListener {
        void onWorkspaceSwitch(String viewName, String workspaceId);
    }

    public Sidebar(User user, OnWorkspaceSwitchListener listener) {
        this.currentUser = user;
        this.switchViewListener = listener;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(40, 40, 40));
        setPreferredSize(new Dimension(220, 0));

        // Add Profile Section
        JLabel profileLabel = new JLabel(user.getUsername(), JLabel.CENTER);
        profileLabel.setForeground(Color.WHITE);
        profileLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        profileLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        profileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(profileLabel);

        // Add Main Options
        String[] options = {"Add Reminder", "Create Workspace", "Home"};
        for (String option : options) {
            JButton button = createSidebarButton(option);
            button.addActionListener(e -> {
                switch (option) {
                    case "Add Reminder":
                        new AddReminderPage((Frame) SwingUtilities.getWindowAncestor(Sidebar.this), currentUser);
                        break;
                    case "Create Workspace":
                        createWorkspace();
                        break;
                    case "Home":
                        if (switchViewListener != null) {
                            switchViewListener.onWorkspaceSwitch("Home", null);
                        }
                        break;
                }
            });
            add(button);
        }

        // Add Workspace Section Header
        JLabel workspaceLabel = new JLabel("My Workspaces", JLabel.CENTER);
        workspaceLabel.setForeground(Color.WHITE);
        workspaceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        workspaceLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        workspaceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(workspaceLabel);

        // Add workspace panel directly
        workspacePanel = new JPanel();
        workspacePanel.setLayout(new BoxLayout(workspacePanel, BoxLayout.Y_AXIS));
        workspacePanel.setBackground(new Color(40, 40, 40));
        add(workspacePanel);

        // Fetch workspaces for the user
        fetchWorkspaces();

        add(Box.createVerticalGlue()); // Push footer options to the bottom
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setBackground(new Color(50, 50, 50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(70, 70, 70));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(50, 50, 50));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(90, 90, 90));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(70, 70, 70));
            }
        });

        return button;
    }

    private void createWorkspace() {
        try {
            String workspaceId = UUID.randomUUID().toString();
            currentUser.createnewWorkspace(workspaceId, "New Workspace", "Fresh new workspace");
            currentUser.addWorkspacesId(workspaceId);
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference workspaceRef = db.collection("Workspace").document(workspaceId);
            workspaceRef.update("userRoles", currentUser.getUserId() + "=OWNER");
            workspaceRef.update("tags", "Personal");
            JOptionPane.showMessageDialog(null, "Workspace created successfully.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Failed to add workspace: " + ex.getMessage());
            ex.printStackTrace();
        }
        fetchWorkspaces();
    }

    private void fetchWorkspaces() {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference userDocRef = db.collection("User").document(currentUser.getUserId());
        ApiFuture<DocumentSnapshot> future = userDocRef.get();

        try {
            DocumentSnapshot userDoc = future.get();
            if (userDoc.exists()) {
                List<String> workspaceIds = (List<String>) userDoc.get("workspacesId");
                if (workspaceIds != null) {
                    workspacePanel.removeAll();
                    for (String workspaceId : workspaceIds) {
                        addWorkspaceButton(workspaceId);
                    }
                    workspacePanel.revalidate();
                    workspacePanel.repaint();
                }
            } else {
                System.out.println("User document does not exist");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void addWorkspaceButton(String workspaceId) {
        JButton workspaceButton = createSidebarButton("Workspace: " + workspaceId.substring(0, 8));
        workspaceButton.addActionListener(e -> {
            if (switchViewListener != null) {
                switchViewListener.onWorkspaceSwitch("Workspace", workspaceId);
            }
        });
        workspacePanel.add(workspaceButton);
    }
}
