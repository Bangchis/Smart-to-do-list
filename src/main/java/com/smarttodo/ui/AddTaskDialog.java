package com.smarttodo.ui;


import com.smarttodo.firebase.service.FirebaseAuthentication;

import com.smarttodo.firebase.FirebaseConfig;
import com.smarttodo.user.model.User;
import com.smarttodo.user.service.UserService;
import com.smarttodo.task.model.Priority;
import com.smarttodo.task.model.Status;
import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;
import com.smarttodo.reminder.model.Reminder;
import com.smarttodo.workspace.model.Workspace;
import com.smarttodo.workspace.service.WorkspaceService;

import java.util.Map;
import java.text.SimpleDateFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.UUID;

public class AddTaskDialog extends JDialog {
    private JTextField titleField, descriptionField, dueDateField;
    private JComboBox<Priority> priorityComboBox;
    private JButton addButton, cancelButton;
    private String workspaceId;

    public AddTaskDialog(String workspaceId, JFrame parentFrame) {
        super(parentFrame, "Add Task", true);
        this.workspaceId = workspaceId;

        setLayout(new GridLayout(5, 2));
        setSize(400, 300);
        setLocationRelativeTo(parentFrame);

        // Các trường nhập liệu
        JLabel titleLabel = new JLabel("Task Title:");
        titleField = new JTextField();

        JLabel descriptionLabel = new JLabel("Task Description:");
        descriptionField = new JTextField();

        JLabel dueDateLabel = new JLabel("Due Date (YYYY-MM-DD):");
        dueDateField = new JTextField();

        JLabel priorityLabel = new JLabel("Priority:");
        priorityComboBox = new JComboBox<>(Priority.values()); // Populate with enum values

        // Nút Add Task
        addButton = new JButton("Add Task");
        addButton.addActionListener(e -> {
            try {
                if (workspaceId == null || workspaceId.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Workspace ID is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
        
                String title = titleField.getText().trim();
                String description = descriptionField.getText().trim();
                String dueDateStr = dueDateField.getText().trim();
                Priority priority = (Priority) priorityComboBox.getSelectedItem();
        
                if (title.isEmpty() || description.isEmpty() || dueDateStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
        
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dueDate;
                try {
                    dueDate = dateFormat.parse(dueDateStr);
                } catch (Exception parseException) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
        
                // Tạo Task
                Task task = new Task();
                task.setTaskID(UUID.randomUUID().toString());
                task.setTitle(title);
                task.setDescription(description);
                task.setDueDate(dueDate);
                task.setPriority(priority);
                task.setStatus(Status.New);
        
                // Gọi WorkspaceService để thêm Task
                WorkspaceService.addTask(workspaceId, task);
                JOptionPane.showMessageDialog(this, "Task added successfully.");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding task: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        

        // Nút Cancel
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        // Thêm các thành phần vào giao diện
        add(titleLabel);
        add(titleField);
        add(descriptionLabel);
        add(descriptionField);
        add(dueDateLabel);
        add(dueDateField);
        add(priorityLabel);
        add(priorityComboBox);
        add(addButton);
        add(cancelButton);

        setVisible(true);
    }
}