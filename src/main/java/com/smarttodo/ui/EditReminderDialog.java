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


public class EditReminderDialog extends JDialog {
    private JTextField recurrencePatternField, dueDateField;
    private JButton saveButton, cancelButton;
    private Reminder reminder;
    private String workspaceId;
    private String taskId;
    private ManageRemindersPage parentPage;

    public EditReminderDialog(String workspaceId, String taskId, Reminder reminder, ManageRemindersPage parentPage) {
        super(parentPage, "Edit Reminder", true);
        this.workspaceId = workspaceId;
        this.taskId = taskId;
        this.reminder = reminder;
        this.parentPage = parentPage;

        setLayout(new GridLayout(3, 2));
        setSize(400, 200);
        setLocationRelativeTo(parentPage);

        JLabel recurrencePatternLabel = new JLabel("Recurrence Pattern:");
        recurrencePatternField = new JTextField(reminder.getRecurrencePattern());

        JLabel dueDateLabel = new JLabel("Due Date (YYYY-MM-DD):");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dueDateField = new JTextField(dateFormat.format(reminder.getDueDate()));

        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                String recurrencePattern = recurrencePatternField.getText();
                String dueDateStr = dueDateField.getText();
                Date dueDate = dateFormat.parse(dueDateStr);

                reminder.setRecurrencePattern(recurrencePattern);
                reminder.setDueDate(dueDate);

                WorkspaceService.updateReminder(workspaceId, taskId, reminder);
                parentPage.refreshReminderList();
                JOptionPane.showMessageDialog(this, "Reminder updated successfully.");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating reminder: " + ex.getMessage());
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
