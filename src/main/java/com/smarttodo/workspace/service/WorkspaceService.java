package com.smarttodo.workspace.service;

import com.smarttodo.workspace.model.Workspace;
import com.smarttodo.workspace.model.WorkspaceRole;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.smarttodo.reminder.model.Reminder;
import com.smarttodo.user.model.User;


public class WorkspaceService {

    // Add a user to a workspace with a specific role
    public void addUserToWorkspace(Workspace workspace, User user, WorkspaceRole role) {
        // Only owners can add new users with roles
        if (workspace.isOwner(user)) {
            workspace.addUserWithRole(user, role);
        } else {
            System.out.println("Only the owner can add users with roles.");
        }
    }

    // Add a reminder to a sub-collection under Workspace
    public boolean addReminderToWorkspace(String workspaceId, String reminderId, Map<String, Object> reminderData) {
        Firestore db = FirestoreClient.getFirestore();

        try {
            // Reference to the "Workspace" collection
            CollectionReference workspaceCollection = db.collection("Workspace");

            // Reference to the specific workspace's "Reminder" sub-collection
            DocumentReference workspaceDoc = workspaceCollection.document(workspaceId);
            CollectionReference reminderCollection = workspaceDoc.collection("reminders");

            // Add the reminder data
            ApiFuture<WriteResult> writeResult = reminderCollection.document(reminderId).set(reminderData);

            // Ensure the write completes successfully
            writeResult.get();
            System.out.println("Reminder added successfully to Workspace " + workspaceId);
            return true;
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error adding reminder: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Reminder> fetchWorkspaceReminders(String workspaceID) throws InterruptedException, ExecutionException {
        // Reference to the user's document and the reminder sub-collection
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference userDocRef = db.collection("Workspace").document(workspaceID);
        CollectionReference remindersCollection = userDocRef.collection("reminders");

        // Fetch reminders asynchronously using FirestoreClient
        QuerySnapshot querySnapshot = remindersCollection.get().get(); // Blocking call for simplicity

        if (querySnapshot != null) {
            List<Reminder> reminders = querySnapshot.toObjects(Reminder.class);

            // Now pass the reminders to the CalendarPanel
            return reminders;
        }
        return null;
    }

    // Change a user's role in a workspace
    public void changeUserRoleInWorkspace(Workspace workspace, User user, WorkspaceRole newRole) {
        // Only owners can change roles
        if (workspace.isOwner(user)) {
            workspace.changeUserRole(user, newRole);
        } else {
            System.out.println("Only the owner can change roles.");
        }
    }

    // Get the role of a user in a workspace
    public WorkspaceRole getUserRoleInWorkspace(Workspace workspace, User user) {
        return workspace.getUserRole(user);
    }
}
