package com.smarttodo.ui;

import javax.swing.*;

import com.smarttodo.firebase.FirebaseConfig;
import com.smarttodo.user.model.User;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class Homepage extends JFrame {

    private JPanel sidebarPanel;  // Make sidebarPanel a member variable to access later
    private User currentUser; // Store the user object

    public Homepage(User user) {

        FirebaseConfig.initializeFirebase();
        this.currentUser = user;
        // Frame settings
        setTitle("Client Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Correct usage
        setLocationRelativeTo(null); // Center the frame

        // Get the screen size (width and height)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        // Set the frame to full screen (maximized)
        setSize(screenWidth, screenHeight);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Main layout for the frame
        setLayout(new BorderLayout());

        // Left sidebar panel
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(40, 40, 40));

        // Add profile section
        // Profile label showing the current user's username
        JLabel profileLabel = new JLabel(currentUser.getUsername(), JLabel.CENTER);  // Use the username from the currentUser object
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
        workspaceButtonsPanel.setLayout(new GridLayout(2, 3, 20, 20)); // 2 rows, 3 columns
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

    // Update the sidebar width based on the current window width
    private void updateSidebarWidth() {
        int newWidth = getWidth() / 6; // Sidebar width is 1/6th of the window width
        sidebarPanel.setPreferredSize(new Dimension(newWidth, getHeight())); // Adjust height to window height
        revalidate(); // Revalidate the layout to update the sidebar size
    }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        // Dummy User object for testing purposes
        User testUser = new User("userId123", "Nguyen Thanh Lam", "test@example.com", "password", "1990-01-01", 1, "1234567890", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        
        // Create and show the homepage with the dummy user
        Homepage ui = new Homepage(testUser);
        ui.setVisible(true);
    });
}

}
