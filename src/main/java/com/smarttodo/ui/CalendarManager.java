package com.smarttodo.ui;

import com.smarttodo.reminder.model.Reminder;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CalendarManager extends JPanel {

    private JPanel calendarPanelContainer;  // Container for calendar and view selection panel
    private JPanel viewSelectionPanel;     // Panel for selecting calendar view (e.g., buttons for Day, Week, Month, Year)
    private List<Reminder> reminders;      // List of reminders to be displayed on the calendar

    public CalendarManager(List<Reminder> reminders) {
        this.reminders = reminders;

        // Initialize components
        setLayout(new BorderLayout());

        // Initialize the calendar panel container
        calendarPanelContainer = new JPanel();
        calendarPanelContainer.setLayout(new BorderLayout());
        add(calendarPanelContainer, BorderLayout.CENTER);

        // Initialize the view selection panel
        viewSelectionPanel = new JPanel();
        viewSelectionPanel.setLayout(new FlowLayout());

        // Add buttons for selecting different views (Day, Week, Month, Year)
        JButton dayButton = new JButton("Day");
        JButton weekButton = new JButton("Week");
        JButton monthButton = new JButton("Month");
        JButton yearButton = new JButton("Year");

        // Add action listeners to buttons to switch views
        dayButton.addActionListener(e -> switchCalendarView("DAY"));
        weekButton.addActionListener(e -> switchCalendarView("WEEK"));
        monthButton.addActionListener(e -> switchCalendarView("MONTH"));
        yearButton.addActionListener(e -> switchCalendarView("YEAR"));

        // Add buttons to the view selection panel
        viewSelectionPanel.add(dayButton);
        viewSelectionPanel.add(weekButton);
        viewSelectionPanel.add(monthButton);
        viewSelectionPanel.add(yearButton);

        // Initially load the calendar with the month view
        switchCalendarView("MONTH");
    }

    private void switchCalendarView(String view) {
        // Clear existing calendar and re-create with the new view, passing the reminders
        CalendarPanel newCalendarPanel = new CalendarPanel(view, reminders);
        newCalendarPanel.setPreferredSize(new Dimension(800, 550));

        // Clear the container and add the new calendar
        calendarPanelContainer.removeAll();
        calendarPanelContainer.add(viewSelectionPanel, BorderLayout.NORTH);
        calendarPanelContainer.add(newCalendarPanel, BorderLayout.CENTER);

        // Revalidate and repaint to refresh the layout
        calendarPanelContainer.revalidate();
        calendarPanelContainer.repaint();
    }
}
