package com.smarttodo.ui;

import javax.swing.*;
import java.awt.*;

public class Homepage extends JFrame {

    public Homepage() {
        // Frame settings
        setTitle("Client Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame

        // Main layout for the frame
        setLayout(new BorderLayout());

        // Left sidebar panel
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
        sidebarPanel.setBackground(new Color(40, 40, 40));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        // Profile section
        JLabel profileLabel = new JLabel("Nguyen Thanh Binh", JLabel.CENTER);
        profileLabel.setForeground(Color.WHITE);
        profileLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        profileLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        sidebarPanel.add(profileLabel);

        // Sidebar buttons
        String[] sidebarOptions = {"Search", "Smart Assistant", "Home", "Inbox"};
        for (String option : sidebarOptions) {
            JButton button = createSidebarButton(option);
            sidebarPanel.add(button);
        }

        // Workspaces label
        JLabel workspacesLabel = new JLabel("My Workspaces");
        workspacesLabel.setForeground(Color.WHITE);
        workspacesLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        sidebarPanel.add(workspacesLabel);

        // Workspace options
        String[] workspaces = {"Work1", "Work2", "Work3", "Work4", "Work5", "Work6"};
        for (String workspace : workspaces) {
            JButton workspaceButton = createSidebarButton(workspace);
            sidebarPanel.add(workspaceButton);
        }

        // More options at the bottom
        sidebarPanel.add(Box.createVerticalGlue()); // Push remaining buttons to the bottom
        String[] moreOptions = {"Calendar", "Settings", "Create a workspace", "Logout"};
        for (String option : moreOptions) {
            JButton button = createSidebarButton(option);
            sidebarPanel.add(button);
        }

        // Right main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, Nguyen Thanh Binh", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Workspace buttons panel
        JPanel workspaceButtonsPanel = new JPanel();
        workspaceButtonsPanel.setLayout(new GridLayout(2, 3, 20, 20)); // 2 hàng, 3 cột
        workspaceButtonsPanel.setBackground(new Color(30, 30, 30));
        
        // Add colored workspace buttons
        Color[] colors = {
            new Color(70, 130, 180), // Steel Blue
            new Color(220, 20, 60),  // Crimson
            new Color(128, 0, 128),  // Purple
            new Color(255, 215, 0),  // Gold
            new Color(0, 128, 0),    // Green
            new Color(255, 69, 0)    // Orange Red
        };
        for (int i = 0; i < 6; i++) {
            JButton workspaceButton = new JButton("Work" + (i + 1));
            workspaceButton.setPreferredSize(new Dimension(100, 100));
            workspaceButton.setBackground(colors[i]);
            workspaceButton.setForeground(Color.WHITE);
            workspaceButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            workspaceButton.setFocusPainted(false);
            workspaceButtonsPanel.add(workspaceButton);
        }
        mainPanel.add(workspaceButtonsPanel, BorderLayout.CENTER);

        // Calendar panel
        JPanel calendarPanel = new JPanel();
        calendarPanel.setPreferredSize(new Dimension(500, 300));
        calendarPanel.setBackground(Color.WHITE);
        calendarPanel.setBorder(BorderFactory.createTitledBorder("Calendar"));
        mainPanel.add(calendarPanel, BorderLayout.SOUTH);

        // Add panels to the main frame
        add(sidebarPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }

    // Method to create a styled button for the sidebar
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Homepage ui = new Homepage();
            ui.setVisible(true);
        });
    }
}
