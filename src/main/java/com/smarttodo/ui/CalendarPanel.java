package com.smarttodo.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class CalendarPanel extends JPanel {

    private LocalDate currentDate;  // Holds the current date being displayed
    private JPanel calendarGrid;    // Holds the grid of days
    private JLabel monthLabel;      // Displays the month and year
    private JButton prevButton;     // Button to go to previous month
    private JButton nextButton;     // Button to go to next month

    public CalendarPanel() {
        // Initialize current date as today
        currentDate = LocalDate.now();

        // Layout for the calendar panel
        setLayout(new BorderLayout());

        // Create the top panel with the navigation buttons
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());

        // Create and add the "Previous" and "Next" buttons
        prevButton = new JButton("<");
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentDate = currentDate.minusMonths(1);
                updateCalendar();
            }
        });
        headerPanel.add(prevButton, BorderLayout.WEST);

        // Create and add the month/year label
        monthLabel = new JLabel("", JLabel.CENTER);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(monthLabel, BorderLayout.CENTER);

        // Create and add the "Next" button
        nextButton = new JButton(">");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentDate = currentDate.plusMonths(1);
                updateCalendar();
            }
        });
        headerPanel.add(nextButton, BorderLayout.EAST);

        // Add header panel to the top of the calendar panel
        add(headerPanel, BorderLayout.NORTH);

        // Create the grid for the calendar days
        calendarGrid = new JPanel();
        calendarGrid.setLayout(new GridLayout(0, 7)); // 7 days per week

        // Add weekday headers (Mon, Tue, Wed, etc.)
        String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String weekday : weekdays) {
            JLabel label = new JLabel(weekday, JLabel.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            calendarGrid.add(label);
        }

        // Add the grid to the main panel (below the header)
        add(calendarGrid, BorderLayout.CENTER);

        // Update the calendar view to show the current month
        updateCalendar();
    }

    // Updates the calendar to reflect the selected month
    private void updateCalendar() {
        // Get the current month and year
        int month = currentDate.getMonthValue();
        int year = currentDate.getYear();

        // Update the month and year label
        String monthName = currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
        monthLabel.setText(monthName + " " + year);

        // Clear the previous day buttons from the grid
        calendarGrid.removeAll();

        // Add the weekday headers again
        String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String weekday : weekdays) {
            JLabel label = new JLabel(weekday, JLabel.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            calendarGrid.add(label);
        }

        // Get the first day of the month and the number of days in the month
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        int lengthOfMonth = currentDate.lengthOfMonth();
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday

        // Fill the calendar grid with empty labels for the initial days of the month
        for (int i = 1; i < firstDayOfWeek; i++) {
            calendarGrid.add(new JLabel("")); // Empty cell
        }

        // Create buttons for each day in the month
        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate dayDate = currentDate.withDayOfMonth(day);
            JButton dayButton = new JButton(String.valueOf(day));

            // Highlight the current day if it matches today's date
            if (dayDate.equals(LocalDate.now())) {
                dayButton.setBackground(Color.YELLOW);
            }

            // Set button action (you can customize this)
            dayButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(CalendarPanel.this, "Selected Date: " + dayDate);
                }
            });

            // Add the day button to the grid
            calendarGrid.add(dayButton);
        }

        // Refresh the calendar grid
        calendarGrid.revalidate();
        calendarGrid.repaint();
    }
}
