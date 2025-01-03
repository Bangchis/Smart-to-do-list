package com.smarttodo.ui;

import javax.swing.*;
import com.smarttodo.user.model.User;
import com.smarttodo.reminder.model.Reminder;
import com.smarttodo.reminder.model.RecurrencePattern;
import com.smarttodo.user.service.UserService;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class AddReminderPage extends JDialog {

    private JTextField titleField, dueDateField;
    private JComboBox<RecurrencePattern> recurrencePatternComboBox;
    private JButton addReminderButton, backButton;
    private User currentUser;

    // Modify constructor to accept a User object
    public AddReminderPage(Frame owner, User user) {
        super(owner, "Add Reminder", true);  // true makes this a modal dialog
        this.currentUser = user;  // Set the currentUser to the passed user
        setSize(400, 300);
        setLocationRelativeTo(owner);  // Center the dialog relative to the owner
        setLayout(new GridLayout(4, 2));

        // Title input field
        JLabel titleLabel = new JLabel("Title:");
        titleField = new JTextField();

        // Due Date input field (with Date and Time)
        JLabel dueDateLabel = new JLabel("Due Date (YYYY-MM-DD HH:MM):");
        dueDateField = new JTextField();

        // Recurrence Pattern ComboBox
        JLabel recurrencePatternLabel = new JLabel("Recurrence Pattern:");
        recurrencePatternComboBox = new JComboBox<>(RecurrencePattern.values());

        // Add Reminder Button
        addReminderButton = new JButton("Add Reminder");
        addReminderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String title = titleField.getText();
                    String dueDateStr = dueDateField.getText();
                    String recurrencePattern = recurrencePatternComboBox.getSelectedItem().toString();

                    // Parse the date and time
                    Date dueDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dueDateStr);

                    // Create the reminder instance

                    String taskId = null;
                    Reminder reminder = UserService.createReminderInstance(taskId, recurrencePattern, dueDate, currentUser, title);

                    // Upload the reminder to Firestore under the user's sub-collection of reminders
                    currentUser.addReminder(reminder);

                    JOptionPane.showMessageDialog(null, "Reminder added successfully.");
                    dispose();  // Close the dialog after adding the reminder
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Failed to add reminder: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        // Back Button
        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();  // Close the dialog and return to the previous screen
            }
        });

        // Add components to the dialog
        add(titleLabel);
        add(titleField);
        add(dueDateLabel);
        add(dueDateField);
        add(recurrencePatternLabel);
        add(recurrencePatternComboBox);
        add(addReminderButton);
        add(backButton);

        setVisible(true);
    }
}
