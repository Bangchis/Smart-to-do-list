package com.smarttodo.ui;

import com.smarttodo.firebase.FirebaseConfig;
import com.smarttodo.task.model.Priority;
import com.smarttodo.task.model.Status;
import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;
import com.smarttodo.workspace.model.Workspace;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Home extends JFrame {

    private User currentUser;

    public Home(User user) {
        FirebaseConfig.initializeFirebase();
        this.currentUser = user;

        // Frame settings
        setTitle("Client Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Get the screen size (width and height)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        // Set the frame to full screen (maximized)
        setSize(screenWidth, screenHeight);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Main layout for the frame
        setLayout(new BorderLayout());

        // Left sidebar panel (using Sidebar class)
		Sidebar sidebar = new Sidebar(currentUser, new Sidebar.OnWorkspaceSwitchListener() {
			@Override
			public void onWorkspaceSwitch(String viewName, String workspaceId) {
				// Call the switchContentView method in the Home class
				switchContentView(viewName, workspaceId); // 'Home.this' refers to the outer Home instance
			}
		});
		add(sidebar, BorderLayout.WEST);
		


        // Right main panel with CardLayout for switching views
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(new Color(30, 30, 30));

        // Homepage panel
        JPanel homepagePanel = createHomepagePanel();
        contentPanel.add(homepagePanel, "Home");

        // Workspace panel (initially empty or placeholder)
        JPanel workspacePanel = createWorkspacePanel("a7d69ce4-4648-49f4-9208-cef26b9ee440"
		);
        contentPanel.add(workspacePanel, "Workspace");

        // Add the content panel to the main frame
        add(contentPanel, BorderLayout.CENTER);

        // Add a component listener to handle window resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateSidebarWidth();
            }
        });

        // Initial sidebar width update
        updateSidebarWidth();
    }

    // Update the sidebar width based on the current window width
    private void updateSidebarWidth() {
        int newWidth = getWidth() / 6; // Sidebar width is 1/6th of the window width
        revalidate(); // Revalidate the layout to update the sidebar size
    }

    // Create the Homepage view panel
    private JPanel createHomepagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername(), JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        panel.setBackground(new Color(30, 30, 30));
        panel.add(welcomeLabel, BorderLayout.CENTER);
        return panel;
    }

    // Create the Workspace view panel (fetch workspace data using workspace ID)
    private JPanel createWorkspacePanel(String workspaceId) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical stacking
		
		// Set background color and padding
		panel.setBackground(new Color(30, 30, 30));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Optional padding
	
		// Fetch workspace data from Firestore using the workspaceId
		Firestore db = FirestoreClient.getFirestore();
		DocumentReference workspaceDocRef = db.collection("Workspace").document(workspaceId);
		ApiFuture<DocumentSnapshot> future = workspaceDocRef.get();
	
		try {
			DocumentSnapshot workspaceDoc = future.get();
			if (workspaceDoc.exists()) {
				String workspaceName = workspaceDoc.getString("name");
				String workspaceDescription = workspaceDoc.getString("description");
	
				// Add workspace name and description to the panel
				JLabel workspaceLabel = new JLabel("<html><center><b>" + workspaceName + "</b><br>" + workspaceDescription + "</center></html>", JLabel.CENTER);
				workspaceLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
				workspaceLabel.setForeground(Color.WHITE);
				panel.add(workspaceLabel);  // Add workspace name/description at the top
			}
	
			// Fetch the tasks for the workspace from the 'Tasks' subcollection
			CollectionReference tasksRef = db.collection("Workspace").document(workspaceId).collection("Task");
			ApiFuture<QuerySnapshot> tasksFuture = tasksRef.get();
			
			// Wait for the tasks to be fetched
			QuerySnapshot querySnapshot = tasksFuture.get();
			if (!querySnapshot.isEmpty()) {
				for (QueryDocumentSnapshot document : querySnapshot) {
					Task task = document.toObject(Task.class);
					createTaskTile(task, panel);
				}
			} else {
				// No tasks in the workspace, show a message
				JLabel noTasksLabel = new JLabel("No tasks available for this workspace.", JLabel.CENTER);
				noTasksLabel.setFont(new Font("Segoe UI", Font.ITALIC, 18));
				noTasksLabel.setForeground(Color.GRAY);
				panel.add(noTasksLabel);
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	
		return panel;
	}
	
	private void createTaskTile(Task task, JPanel panel) {
		// Create a task tile (a panel for each task)
		JPanel taskTile = new JPanel();
		taskTile.setLayout(new BoxLayout(taskTile, BoxLayout.Y_AXIS)); // Stack vertically
		taskTile.setBackground(new Color(45, 45, 45)); // Tile background color
		taskTile.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1)); // Border for the tile
		taskTile.setMaximumSize(new Dimension(600, 150)); // Max size for the tile
	
		// Title
		JLabel titleLabel = new JLabel(task.getTitle());
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
		titleLabel.setForeground(Color.WHITE);
		
		// Description
		JLabel descriptionLabel = new JLabel("<html><div style='width: 500px;'>" + task.getDescription() + "</div></html>");
		descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		descriptionLabel.setForeground(Color.LIGHT_GRAY);
	
		// Due Date
		JLabel dueDateLabel = new JLabel("Due: " + (task.getDueDate() != null ? task.getDueDate().toString() : "N/A"));
		dueDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		dueDateLabel.setForeground(Color.GRAY);
	
		// Priority
		JLabel priorityLabel = new JLabel("Priority: " + task.getPriority());
		priorityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		priorityLabel.setForeground(getPriorityColor(task.getPriority())); // Get the color based on priority
		
		// Status
		JLabel statusLabel = new JLabel("Status: " + task.getStatus());
		statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		statusLabel.setForeground(getStatusColor(task.getStatus())); // Get the color based on status
	
		// Add the components to the task tile
		taskTile.add(titleLabel);
		taskTile.add(descriptionLabel);
		taskTile.add(dueDateLabel);
		taskTile.add(priorityLabel);
		taskTile.add(statusLabel);
	
		// Add the task tile to the panel
		panel.add(taskTile);
	}
	
	// Helper function to return priority color
	private Color getPriorityColor(Priority priority) {
		switch (priority) {
			case HIGH:
				return Color.RED; // High priority is red
			case MEDIUM:
				return Color.YELLOW; // Medium priority is yellow
			case LOW:
				return Color.GREEN; // Low priority is green
			default:
				return Color.GRAY; // Default color
		}
	}
	
	// Helper function to return status color
	// Helper function to return status color
private Color getStatusColor(Status status) {
    switch (status) {
        case Completed:
            return Color.GREEN; // Completed tasks are green
        case Pending:
            return Color.ORANGE; // Pending tasks are orange
        case Todo:
            return Color.BLUE; // Todo tasks are blue
        case New:
            return Color.CYAN; // New tasks are cyan (or any color you'd like)
        case Overdue:
            return Color.RED; // Overdue tasks are red
        case Archived:
            return Color.GRAY; // Archived tasks are gray
        default:
            return Color.GRAY; // Default color
    }
}

	

    // Method to switch between views in the content area (using CardLayout)
    // Method to switch between views in the content area (using CardLayout)
// Method to switch between views in the content area (using CardLayout)
public void switchContentView(String viewName, String workspaceId) {
    // Get the CardLayout panel from the content pane
    JPanel contentPanel = (JPanel) getContentPane().getComponent(1); // Ensure the contentPanel is at index 1
    CardLayout cardLayout = (CardLayout) contentPanel.getLayout();

	// Clear the content panel before adding the new view
    contentPanel.removeAll();
    contentPanel.revalidate();  // Revalidate to apply changes
    contentPanel.repaint();     // Repaint to ensure it's updated

    // Log the current view and the requested view for debugging
    System.out.println("switchContentView called. Requested view: " + viewName + " with workspaceId: " + workspaceId);
    
    // Show the desired view (either Home or Workspace)
    cardLayout.show(contentPanel, viewName);

    // If switching to Workspace view, update the workspace panel with the correct ID
    if (viewName.equals("Workspace")) {
        System.out.println("Switching to Workspace view. Workspace ID: " + workspaceId);

        // Fetch the workspace details and create a workspace panel with the provided ID
        JPanel workspacePanel = createWorkspacePanel(workspaceId);

        // Add the workspace panel to the content panel
        contentPanel.add(workspacePanel, "Workspace");
        
        // Revalidate and repaint to ensure the changes are rendered
        contentPanel.revalidate();
        contentPanel.repaint();
        
        System.out.println("Workspace panel added with ID: " + workspaceId);
    } else if (viewName.equals("Home")) {
        // If switching to Home, we simply show the home view without adding or removing anything
        System.out.println("Switching to Home view.");
		JPanel homePanel = createHomepagePanel();
		contentPanel.add(homePanel, "Home");
        // No need to re-add the home panel, just show it
        cardLayout.show(contentPanel, "Home");

        System.out.println("Home view displayed.");
    }
}



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Dummy User object for testing purposes
            User testUser = new User("userId123", "lamthanhz", "test@example.com", "password", "1990-01-01", 1, "1234567890", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

            // Create and show the homepage with the dummy user
            Home ui = new Home(testUser);
            ui.setVisible(true);
        });
    }
}
