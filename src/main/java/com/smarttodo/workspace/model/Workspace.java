package com.smarttodo.workspace.model;

import java.util.List;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import com.google.api.core.ApiFuture;
import com.smarttodo.task.model.Task;
import com.smarttodo.user.model.User;
import com.smarttodo.user.service.UserService;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;




public class Workspace {
    // Attributes
    private String workspaceID;
    private String name;
    private String description;
    private String ownerId;
    private List<String> collaboratorIds;
    private List<String> taskIds;

    // Constructor
    public Workspace(String workspaceID, String name, String description, String ownerId) {
        this.workspaceID = workspaceID;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
    }

    // Getters and Setters
    public String getWorkspaceID() {
        return workspaceID;
    }

    public void setWorkspaceID(String workspaceID) {
        this.workspaceID = workspaceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return ownerId;
    }

    public void setOwner(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<String> getCollaborators() {
        return collaboratorIds;
    }

    public void setCollaborators(List<String> collaboratorIds) {
        this.collaboratorIds = collaboratorIds;
    }

    public List<String> getTasks() {
        return taskIds;
    }

    public void setTasks(List<String> taskIds) {
        this.taskIds = taskIds;
    }

    // Methods
    public void createTask(Task task) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            String taskId = task.getTaskID();
            task.setWorkspaceId(this.workspaceID);

            // Create task details map
            DocumentReference workspaceDocRef = db.collection("Workspace").document(this.workspaceID);
            ApiFuture<WriteResult> future = workspaceDocRef.collection("Task").document(taskId).set(task);
            future.get(); // Wait for the operation to complete

            // Update taskIds list
            this.taskIds.add(taskId);
            workspaceDocRef.update("taskIds", this.taskIds).get();

            System.out.println("Task created successfully with ID: " + taskId);
        } catch (Exception e) {
            System.err.println("Error while creating task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeTask(String taskId) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference workspaceDocRef = db.collection("Workspace").document(this.workspaceID);
            ApiFuture<WriteResult> future = workspaceDocRef.collection("Task").document(taskId).delete();
            future.get(); // Wait for the operation to complete

            // Remove from taskIds list and update Firestore
            this.taskIds.remove(taskId);
            workspaceDocRef.update("taskIds", this.taskIds).get();

            System.out.println("Task removed successfully with ID: " + taskId);
        } catch (Exception e) {
            System.err.println("Error while removing task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void editTask(Task task) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            String taskId = task.getTaskID();
            DocumentReference taskDocRef = db.collection("Workspace").document(this.workspaceID).collection("Task").document(taskId);
            ApiFuture<WriteResult> future = taskDocRef.set(task);
            future.get(); // Wait for the operation to complete

            System.out.println("Task edited successfully with ID: " + taskId);
        } catch (Exception e) {
            System.err.println("Error while editing task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Task> getAllTasks() {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference workspaceDocRef = db.collection("Workspace").document(this.workspaceID);
            List<Task> tasks = workspaceDocRef.collection("Task").get().get().toObjects(Task.class);
            System.out.println("Fetched all tasks successfully for workspace ID: " + this.workspaceID);
            return tasks;
        } catch (Exception e) {
            System.err.println("Error while fetching tasks: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void addCollaborator(User user) {
        if (!collaboratorIds.contains(user.getUserId())) {
            collaboratorIds.add(user.getUserId());
        }
        System.out.println("Collaborator added with UserID: " + user.getUserId());
        // Logic to add a collaborator to Firestore can be added here.
    }

    public void removeCollaborator(String userId) {
        collaboratorIds.remove(userId);
        System.out.println("Collaborator removed with UserID: " + userId);
        // Logic to remove a collaborator from Firestore can be added here.
    }

    public static Workspace createWorkspaceInstance(String workspaceId, String name, String description) {
        Workspace workspace = new Workspace(workspaceId, name, description, UserService.getCurrentUser().getUserId());
        System.out.println("Workspace instance created with ID: " + workspaceId);
        return workspace;
    }
}
