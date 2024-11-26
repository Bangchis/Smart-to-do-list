package com.smarttodo.ui;

import com.smarttodo.firebase.service.FirebaseAuthentication;
import com.google.firebase.messaging.AndroidConfig.Priority;
import com.smarttodo.firebase.FirebaseConfig;
import com.smarttodo.user.model.User;
import com.smarttodo.user.service.UserService;
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
public class AddReminderDialog extends JDialog {
    private JTextField recurrencePatternField, dueDateField;
    private JButton saveButton, cancelButton;
    private String workspaceId;
    private String taskId;
    private ManageRemindersPage parentPage;

    public AddReminderDialog(String workspaceId, String taskId, ManageRemindersPage parentPage) {
        super(parentPage, "Add Reminder", true);
        this.workspaceId = workspaceId;
        this.taskId = taskId;
        this.parentPage = parentPage;

        setLayout(new GridLayout(3, 2));
        setSize(400, 200);
        setLocationRelativeTo(parentPage);

        JLabel recurrencePatternLabel = new JLabel("Recurrence Pattern:");
        recurrencePatternField = new JTextField();

        JLabel dueDateLabel = new JLabel("Due Date (YYYY-MM-DD):");
        dueDateField = new JTextField();

        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                String recurrencePattern = recurrencePatternField.getText();
                String dueDateStr = dueDateField.getText();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dueDate = dateFormat.parse(dueDateStr);

                Reminder reminder = new Reminder(taskId, recurrencePattern, dueDate);
                WorkspaceService.addReminderToTask(workspaceId, taskId, reminder);
                parentPage.refreshReminderList();
                JOptionPane.showMessageDialog(this, "Reminder added successfully.");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding reminder: " + ex.getMessage());
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        add(recurrencePatternLabel);
        add(recurrencePatternField);
        add(dueDateLabel);
        add(dueDateField);
        add(saveButton);
        add(cancelButton);

        setVisible(true);
    }
}
