package com.smarttodo.ui;

import com.smarttodo.workspace.model.Workspace;
import com.smarttodo.task.model.Task;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WorkspaceUI extends JFrame {

    private Workspace workspace;

    public WorkspaceUI(Workspace workspace) {
        this.workspace = workspace;
        initializeUI();
    }

    private void initializeUI() {
        // Set up the JFrame (Workspace UI)
        setTitle("Workspace: " + workspace.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create main panel and set layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Sidebar (left side)
        JPanel sidebarPanel = createSidebarPanel();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Task tiles (right side)
        JPanel tasksPanel = createTasksPanel(workspace.getTasks());
        mainPanel.add(tasksPanel, BorderLayout.CENTER);

        // Set the main panel as the content of the frame
        setContentPane(mainPanel);

        // Make the UI visible
        setVisible(true);
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(45, 45, 45));  // Dark background for sidebar
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));

        // Create workspace-related buttons (e.g., Tasks, Settings, etc.)
        JButton tasksButton = createSidebarButton("Tasks");
        sidebarPanel.add(tasksButton);

        // Add more buttons if necessary (e.g., settings, user profile, etc.)
        // For now, we only have one button for tasks

        return sidebarPanel;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Hover effect for sidebar button
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(80, 80, 80));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 60, 60));
            }
        });

        return button;
    }

    private JPanel createTasksPanel(List<Task> tasks) {
        JPanel tasksPanel = new JPanel();
        tasksPanel.setLayout(new GridLayout(0, 1, 10, 10));  // Layout for task tiles
        tasksPanel.setBackground(Color.WHITE);

        // Create task tiles (panels) for each task in the workspace
        for (Task task : tasks) {
            JPanel taskTile = createTaskTile(task);
            tasksPanel.add(taskTile);
        }

        return tasksPanel;
    }

    private JPanel createTaskTile(Task task) {
        JPanel taskTile = new JPanel();
        taskTile.setLayout(new BoxLayout(taskTile, BoxLayout.Y_AXIS));
        taskTile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        taskTile.setBackground(new Color(240, 240, 240));
        taskTile.setPreferredSize(new Dimension(200, 100));
        
        // Set up task tile with task details
        JLabel taskTitle = new JLabel(task.getTitle());
        taskTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JLabel taskDescription = new JLabel("<html>" + task.getDescription() + "</html>");
        taskDescription.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JLabel taskPriority = new JLabel("Priority: " + task.getPriority().toString());
        taskPriority.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Add components to the task tile
        taskTile.add(taskTitle);
        taskTile.add(taskDescription);
        taskTile.add(taskPriority);

        // Add click listener (action to be taken on click)
        taskTile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                System.out.println("Task " + task.getTitle() + " clicked!");
                // Here you can add code to redirect to the task details page or perform another action
            }
        });

        return taskTile;
    }
}
