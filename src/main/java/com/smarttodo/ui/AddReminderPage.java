package com.smarttodo.ui;

import javax.swing.*;
import com.smarttodo.user.model.User;
import com.smarttodo.reminder.model.Reminder;
import com.smarttodo.user.service.UserService;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddReminderPage extends JDialog {

    private JTextField taskIDField, recurrencePatternField, dueDateField;
    private JButton addReminderButton, backButton;
    private User currentUser;

    // Modify constructor to accept a User object
    public AddReminderPage(Frame owner, User user) {
        super(owner, "Add Reminder", true);  // true makes this a modal dialog
        this.currentUser = user;  // Set the currentUser to the passed user
        setSize(400, 300);
        setLocationRelativeTo(owner);  // Center the dialog relative to the owner
        setLayout(new GridLayout(4, 2));

        JLabel taskIDLabel = new JLabel("Task ID:");
        taskIDField = new JTextField();

        JLabel recurrencePatternLabel = new JLabel("Recurrence Pattern:");
        recurrencePatternField = new JTextField();

        JLabel dueDateLabel = new JLabel("Due Date (YYYY-MM-DD):");
        dueDateField = new JTextField();

        addReminderButton = new JButton("Add Reminder");
        addReminderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String taskID = taskIDField.getText();
                    String recurrencePattern = recurrencePatternField.getText();
                    String dueDateStr = dueDateField.getText();
                    Date dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(dueDateStr);

                    // Create the reminder instance
                    Reminder reminder = UserService.createReminderInstance(taskID, recurrencePattern, dueDate, currentUser);

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

        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();  // Close the dialog and return to the previous screen
            }
        });

        add(taskIDLabel);
        add(taskIDField);
        add(recurrencePatternLabel);
        add(recurrencePatternField);
        add(dueDateLabel);
        add(dueDateField);
        add(addReminderButton);
        add(backButton);

        setVisible(true);
    }
}
