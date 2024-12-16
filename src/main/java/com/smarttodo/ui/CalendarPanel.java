package com.smarttodo.ui;

import com.smarttodo.reminder.model.Reminder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class CalendarPanel extends JPanel {

    private LocalDate currentDate;  // Holds the current date being displayed
    private JPanel calendarGrid;    // Holds the grid of days
    private JLabel monthLabel;      // Displays the month and year
    private JButton prevButton;     // Button to go to previous month
    private JButton nextButton;     // Button to go to next month
    private String viewType;        // Holds the current view type (Day, Week, Month, Year)
    private List<Reminder> reminders; // List of reminders to be displayed on the calendar

    // Constructor now accepts a list of reminders
    public CalendarPanel(String viewType, List<Reminder> reminders) {
        this.viewType = viewType;
        this.reminders = reminders;
        currentDate = LocalDate.now(); // Initialize current date as today
        setLayout(new BorderLayout());

        // Create the top panel with navigation buttons
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());

        prevButton = new JButton("<");
        prevButton.addActionListener(e -> navigateDate(-1));  // Go to previous (month/week/day/year)
        headerPanel.add(prevButton, BorderLayout.WEST);

        monthLabel = new JLabel("", JLabel.CENTER);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(monthLabel, BorderLayout.CENTER);

        nextButton = new JButton(">");
        nextButton.addActionListener(e -> navigateDate(1));  // Go to next (month/week/day/year)
        headerPanel.add(nextButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Initialize the grid for the calendar days
        calendarGrid = new JPanel();
        calendarGrid.setLayout(new GridLayout(0, 7));  // 7 days per week

        // Add weekday headers (Mon, Tue, Wed, etc.)
        String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String weekday : weekdays) {
            JLabel label = new JLabel(weekday, JLabel.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            calendarGrid.add(label);
        }

        // Add the grid to the main panel (below the header)
        add(calendarGrid, BorderLayout.CENTER);

        // Update the calendar view based on the selected view (Day, Week, Month, Year)
        updateCalendar();
    }

    // Navigate between months, weeks, days, or years
    private void navigateDate(int direction) {
        switch (viewType) {
            case "DAY":
                currentDate = currentDate.plusDays(direction);
                break;
            case "WEEK":
                currentDate = currentDate.plusWeeks(direction);
                break;
            case "MONTH":
                currentDate = currentDate.plusMonths(direction);
                break;
            case "YEAR":
                currentDate = currentDate.plusYears(direction);
                break;
        }
        updateCalendar();
    }

    // Updates the calendar based on the current view type (Day, Week, Month, Year)
    private void updateCalendar() {
        calendarGrid.removeAll();  // Clear the grid before updating

        switch (viewType) {
            case "DAY":
                showDayView();
                break;
            case "WEEK":
                showWeekView();
                break;
            case "MONTH":
                showMonthView();
                break;
            case "YEAR":
                showYearView();
                break;
        }

        // Refresh the calendar grid
        calendarGrid.revalidate();
        calendarGrid.repaint();
    }

    // Display the day view (show details for the selected day)
    private void showDayView() {
        monthLabel.setText(currentDate.getDayOfWeek() + ", " + currentDate.getDayOfMonth() + " " 
                           + currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) 
                           + " " + currentDate.getYear());

        // Iterate over the reminders and check if any match the current date
        for (Reminder reminder : reminders) {
            // Convert Date to LocalDate for comparison
            LocalDate reminderDate = reminder.getDueDate().toInstant()
                                              .atZone(ZoneId.systemDefault())
                                              .toLocalDate();

            // Compare the reminder's due date with the current date
            if (reminderDate.equals(currentDate)) {
                JButton reminderButton = new JButton("Reminder: " + reminder.getTaskID());
                reminderButton.setBackground(Color.GREEN); // Highlight reminder
                reminderButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Reminder for: " + reminder.getTaskID()));
                calendarGrid.add(reminderButton);
            }
        }
    }

    // Display the week view (show the 7 days of the current week)
    private void showWeekView() {
        monthLabel.setText("Week of " + currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + currentDate.getYear());

        // Get the start of the week (Monday) for the current date
        LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);

        for (int i = 0; i < 7; i++) {
            LocalDate dayOfWeek = startOfWeek.plusDays(i);
            JButton dayButton = new JButton(String.valueOf(dayOfWeek.getDayOfMonth()));

            // Highlight the current day if it matches today's date
            if (dayOfWeek.equals(LocalDate.now())) {
                dayButton.setBackground(Color.YELLOW);
            }

            // Iterate over reminders to see if any match the current week
            for (Reminder reminder : reminders) {
                LocalDate reminderDate = reminder.getDueDate().toInstant()
                                                  .atZone(ZoneId.systemDefault())
                                                  .toLocalDate();

                if (reminderDate.equals(dayOfWeek)) {
                    JButton reminderButton = new JButton("Reminder: " + reminder.getTaskID());
                    reminderButton.setBackground(Color.GREEN); // Highlight reminder
                    reminderButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Reminder for: " + reminder.getTaskID()));
                    calendarGrid.add(reminderButton);
                }
            }

            dayButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Selected Date: " + dayOfWeek));
            calendarGrid.add(dayButton);
        }
    }

    private void showMonthView() {
        String monthName = currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
        int year = currentDate.getYear();
        monthLabel.setText(monthName + " " + year);
    
        // Get the first day of the month and the number of days in the month
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        int lengthOfMonth = currentDate.lengthOfMonth();
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
    
        // Fill the calendar grid with empty labels for the initial days of the month
        for (int i = 1; i < firstDayOfWeek; i++) {
            calendarGrid.add(new JLabel("")); // Empty cell for days before the start of the month
        }
    
        // Create panels for each day in the month
        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate dayDate = currentDate.withDayOfMonth(day);
            JPanel dayPanel = new JPanel();
            dayPanel.setLayout(new BorderLayout());  // Use BorderLayout to easily position components
            dayPanel.setPreferredSize(new Dimension(100, 100)); // Set preferred size for the panel
    
            // Set border for each day cell
            dayPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Add a gray border around each cell
    
            // Create the day label for the top-right of the cell
            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.RIGHT);
            dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            dayLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5)); // Padding for the day number
            dayPanel.add(dayLabel, BorderLayout.NORTH);  // Place the day number at the top of the panel
    
            // Highlight the current day number in red
            if (dayDate.equals(LocalDate.now())) {
                dayLabel.setForeground(Color.RED); // Red color for the current day's number
            }
    
            // Start building the reminder content if any
            String reminderText = "";  // Initialize reminder text string
            for (Reminder reminder : reminders) {
                LocalDate reminderDate = reminder.getDueDate().toInstant()
                                                  .atZone(ZoneId.systemDefault())
                                                  .toLocalDate();
    
                // If the reminder's due date matches this day, add the reminder title
                if (reminderDate.equals(dayDate)) {
                    if (!reminderText.isEmpty()) {
                        reminderText += "\n"; // Add a new line for the next reminder
                    }
                    reminderText += reminder.getTitle(); // Append the reminder title
                }
            }
    
            // If there are reminders, add them to the day panel below the day number
            if (!reminderText.isEmpty()) {
                JTextArea reminderArea = new JTextArea(reminderText);
                reminderArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                reminderArea.setBackground(Color.YELLOW); // Background yellow for reminder text
                reminderArea.setEditable(false);  // Make the text area non-editable
                reminderArea.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5)); // Padding for the reminder area
                dayPanel.add(reminderArea, BorderLayout.CENTER);  // Add the reminder text below the day number
            }
    
            // Action listener for the day panel (to show selected date)
            dayPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JOptionPane.showMessageDialog(dayPanel, "Selected Date: " + dayDate);
                }
            });
    
            // Add the day panel to the calendar grid
            calendarGrid.add(dayPanel);
        }
    
        // Refresh the calendar grid after all days are added
        calendarGrid.revalidate();
        calendarGrid.repaint();
    }
    
    
    
    
    

    private void showYearView() {
        monthLabel.setText("Year " + currentDate.getYear());

        // Loop through all months of the current year and display them
        for (Month month : Month.values()) {
            JButton monthButton = new JButton(month.getDisplayName(TextStyle.FULL, Locale.getDefault()));

            // Iterate over reminders and check if they match the current month
            for (Reminder reminder : reminders) {
                LocalDate reminderDate = reminder.getDueDate().toInstant()
                                                  .atZone(ZoneId.systemDefault())
                                                  .toLocalDate();

                if (reminderDate.getYear() == currentDate.getYear() && reminderDate.getMonth() == month) {
                    JButton reminderButton = new JButton("Reminder: " + reminder.getTaskID());
                    reminderButton.setBackground(Color.GREEN); // Highlight reminder
                    reminderButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Reminder for: " + reminder.getTaskID()));
                    calendarGrid.add(reminderButton);
                }
            }

            monthButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Selected Month: " + month));
            calendarGrid.add(monthButton);
        }
    }
}
