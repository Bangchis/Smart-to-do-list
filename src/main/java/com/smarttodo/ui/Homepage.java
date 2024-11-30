package com.smarttodo.ui;

import com.smarttodo.firebase.FirebaseConfig;
import com.smarttodo.user.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

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
