package com.smarttodo.ui;



import com.smarttodo.firebase.service.FirebaseAuthentication;
import com.smarttodo.task.model.Priority; // Import đúng Priority từ package của bạn

import com.smarttodo.firebase.FirebaseConfig;
import com.smarttodo.user.model.User;
import com.smarttodo.user.service.UserService;
import com.smarttodo.task.model.Task;
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



public class EditTaskDialog extends JDialog {
    private JTextField titleField, descriptionField, dueDateField;
    private JComboBox<Priority> priorityComboBox; // Đây là Priority từ com.smarttodo.task.model
    private JButton saveButton, cancelButton;
    private String workspaceId;
    private Task task;
    private ManageTasksPage parentFrame;

    public EditTaskDialog(String workspaceId, Task task, ManageTasksPage parentFrame) {
        super(parentFrame, "Edit Task", true);

        if (task == null) {
            JOptionPane.showMessageDialog(parentFrame, "No task selected for editing.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        this.workspaceId = workspaceId;
        this.task = task;
        this.parentFrame = parentFrame;

        setLayout(new GridLayout(5, 2));
        setSize(400, 300);
        setLocationRelativeTo(parentFrame);

        JLabel titleLabel = new JLabel("Task Title:");
        titleField = new JTextField(task.getTitle() != null ? task.getTitle() : "");

        JLabel descriptionLabel = new JLabel("Task Description:");
        descriptionField = new JTextField(task.getDescription() != null ? task.getDescription() : "");

        JLabel dueDateLabel = new JLabel("Due Date (YYYY-MM-DD):");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dueDateField = new JTextField(task.getDueDate() != null ? dateFormat.format(task.getDueDate()) : "");

        JLabel priorityLabel = new JLabel("Priority:");
        priorityComboBox = new JComboBox<>(Priority.values()); // Sử dụng com.smarttodo.task.model.Priority
        priorityComboBox.setSelectedItem(task.getPriority() != null ? task.getPriority() : Priority.MEDIUM);

        saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String description = descriptionField.getText().trim();
                String dueDateStr = dueDateField.getText().trim();
                com.smarttodo.task.model.Priority priority = (com.smarttodo.task.model.Priority) priorityComboBox.getSelectedItem(); // Sử dụng Priority từ com.smarttodo.task.model

                if (title.isEmpty() || description.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Title and description cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Date dueDate = dateFormat.parse(dueDateStr);

                task.setTitle(title);
                task.setDescription(description);
                task.setDueDate(dueDate);
                task.setPriority(priority);

                WorkspaceService.editTask(workspaceId, task.getTaskID(), task);

                JOptionPane.showMessageDialog(this, "Task updated successfully.");
                parentFrame.refreshTaskList(workspaceId);
                dispose();
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating task: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        add(titleLabel);
        add(titleField);
        add(descriptionLabel);
        add(descriptionField);
        add(dueDateLabel);
        add(dueDateField);
        add(priorityLabel);
        add(priorityComboBox);
        add(saveButton);
        add(cancelButton);

        setVisible(true);
    }
}
