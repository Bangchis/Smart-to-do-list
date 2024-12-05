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

        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Set up a simple label for testing
        JLabel label = new JLabel("Workspace: " + workspace.getName());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        mainPanel.add(label, BorderLayout.CENTER);

        // Set the main panel as the content of the frame
        setContentPane(mainPanel);

        // Make the UI visible
        setVisible(true);
    }
}
