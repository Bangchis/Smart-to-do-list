package com.smarttodo.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WorkspaceUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}

// Main Frame
class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Workspace");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLayout(new BorderLayout());

        // Add Main Sidebar (Left Sidebar)
        MainSidebar mainSidebar = new MainSidebar();
        add(mainSidebar, BorderLayout.WEST);

        // Add Sub Sidebar and Content in Center
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Add Sub Sidebar (Secondary Sidebar)
        SubSidebar subSidebar = new SubSidebar();
        centerPanel.add(subSidebar, BorderLayout.WEST);

        // Add Content Area
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(Color.WHITE);

        // Add Top Bar Inside Content Area
        TopBar topBar = new TopBar();
        contentArea.add(topBar, BorderLayout.NORTH);

        // Add Main Content
        JTextArea contentDetails = new JTextArea();
        contentDetails.setText("This is where the main content will be displayed.");
        contentDetails.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentDetails.setEditable(false);
        contentArea.add(new JScrollPane(contentDetails), BorderLayout.CENTER);

        centerPanel.add(contentArea, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}

// Main Sidebar (Left Sidebar)
class MainSidebar extends JPanel {

    public MainSidebar() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(40, 40, 40));

        // Add Profile Section
        JLabel profileLabel = new JLabel("User1", JLabel.CENTER);
        profileLabel.setForeground(Color.WHITE);
        profileLabel.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Larger font for profile
        profileLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        profileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(profileLabel);

        // Add Main Options
        String[] options = {"Search", "Smart Assistant", "Home", "Inbox"};
        for (String option : options) {
            JButton button = createSidebarButton(option);
            add(button);
        }

        // Add Workspaces Section
        JLabel workspaceLabel = new JLabel("My Workspaces", JLabel.CENTER);
        workspaceLabel.setForeground(Color.WHITE);
        workspaceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Slightly smaller font for headers
        workspaceLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        workspaceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(workspaceLabel);

        String[] workspaces = {"Work1", "Work2", "Work3", "Work4", "Work5", "Work6"};
        for (String workspace : workspaces) {
            JButton workspaceButton = createSidebarButton(workspace);
            add(workspaceButton);
        }

        // Add Footer Options
        add(Box.createVerticalGlue()); // Push footer to the bottom
        String[] footerOptions = {"Calendar", "Settings", "Create Workspace", "Logout"};
        for (String footer : footerOptions) {
            JButton footerButton = createSidebarButton(footer);
            add(footerButton);
        }

        setPreferredSize(new Dimension(220, getHeight())); // Adjust sidebar width
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button horizontally
        button.setMaximumSize(new Dimension(180, 40));     // Larger buttons for better readability
        button.setBackground(new Color(50, 50, 50));       // Default background color
        button.setForeground(Color.WHITE);                 // White text color
        button.setFocusPainted(false);                     // Remove focus ring
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Slightly larger font for buttons
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding for better spacing

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(70, 70, 70)); // Lighter grey on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(50, 50, 50)); // Default color on exit
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(90, 90, 90)); // Darker color on click
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(70, 70, 70)); // Revert to hover color
            }
        });

        return button;
    }
}

// Sub Sidebar (Secondary Sidebar)
class SubSidebar extends JPanel {

    public SubSidebar() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 182, 193)); // Pastel pink color

        // Add Back Button in a Small Panel
        JPanel backButtonPanel = new JPanel();
        backButtonPanel.setPreferredSize(new Dimension(50, 50));
        backButtonPanel.setBackground(new Color(255, 182, 193)); // Same color as sidebar
        backButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5)); // Align to top-left

        JButton backButton = createIconButton("<");
        backButton.setPreferredSize(new Dimension(40, 40)); // Small square size
        backButtonPanel.add(backButton);
        add(backButtonPanel, BorderLayout.NORTH);

        // Main Content of Sidebar
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(255, 182, 193)); // Same background as parent

        // Add Tasks Section
        JLabel tasksLabel = new JLabel("Tasks");
        tasksLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tasksLabel.setForeground(Color.DARK_GRAY);
        tasksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tasksLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        contentPanel.add(tasksLabel);

        String[] tasks = {"All", "Scheduled", "Priority", "Completed"};
        for (String task : tasks) {
            JButton taskButton = createSidebarButton(task);
            contentPanel.add(taskButton);
        }

        // Add Tags Section
        JLabel tagsLabel = new JLabel("Tags");
        tagsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tagsLabel.setForeground(Color.DARK_GRAY);
        tagsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tagsLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        contentPanel.add(tagsLabel);

        String[] tags = {"#binh", "#loan", "#tien", "#nhuan"};
        for (String tag : tags) {
            JButton tagButton = createSidebarButton(tag);
            contentPanel.add(tagButton);
        }

        // Add Users Section
        JLabel usersLabel = new JLabel("Users");
        usersLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        usersLabel.setForeground(Color.DARK_GRAY);
        usersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usersLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        contentPanel.add(usersLabel);

        String[] users = {"User1 (Host)", "User2"};
        for (String user : users) {
            JButton userButton = createSidebarButton(user);
            contentPanel.add(userButton);
        }

        add(contentPanel, BorderLayout.CENTER);
        setPreferredSize(new Dimension(220, getHeight())); // Match width with MainSidebar
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40)); // Similar size as MainSidebar buttons
        button.setBackground(new Color(255, 228, 232)); // Lighter pink
        button.setForeground(Color.DARK_GRAY);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(255, 204, 213)); // Hover color
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(255, 228, 232)); // Default color
            }
        });

        return button;
    }

    private JButton createIconButton(String iconText) {
        JButton button = new JButton(iconText);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.DARK_GRAY);
        button.setBackground(new Color(255, 204, 213));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}

// Top Bar (Inside Main Content Area)
class TopBar extends JPanel {

    public TopBar() {
        setLayout(new BorderLayout());
        setBackground(new Color(60, 60, 60)); // Gray color

        // Add Title on the Left
        JLabel titleLabel = new JLabel("All", JLabel.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
        add(titleLabel, BorderLayout.WEST);

        // Add "+" Button on the Right
        JButton addButton = new JButton("+");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addButton.setForeground(Color.WHITE);
        addButton.setBackground(new Color(80, 80, 80)); // Slightly darker gray for the button
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        addButton.addActionListener(e -> new AddTaskDialog()); // Open Add Task Dialog

        add(addButton, BorderLayout.EAST);

        setPreferredSize(new Dimension(0, 40)); // Height of the top bar
    }
}

// Add Task Dialog (No changes from original implementation)
class AddTaskDialog extends JDialog {
    // Implementation remains unchanged.
}
