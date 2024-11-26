package com.smarttodo.workspace.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.smarttodo.reminder.model.Reminder;
import com.smarttodo.task.model.Task;
import com.smarttodo.user.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
public class WorkspaceService {

    // Method to add a task to a Workspace
    public static void addTask(String workspaceId, Task task) {
        if (workspaceId == null || workspaceId.isEmpty()) {
            throw new IllegalArgumentException("Workspace ID is null or empty.");
        }
    
        if (task == null) {
            throw new IllegalArgumentException("Task object is null.");
        }
    
        try {
            Firestore db = FirestoreClient.getFirestore();
            String taskId = task.getTaskID() != null ? task.getTaskID() : UUID.randomUUID().toString();
            task.setTaskID(taskId);
            task.setWorkspaceId(workspaceId);
    
            // Chuyển đổi Task thành một map để lưu vào Firestore
            Map<String, Object> taskDetails = new HashMap<>();
            taskDetails.put("taskID", task.getTaskID());
            taskDetails.put("title", task.getTitle());
            taskDetails.put("description", task.getDescription());
            taskDetails.put("dueDate", task.getDueDate()); // Lưu Date thay vì String
            taskDetails.put("priority", task.getPriority() != null ? task.getPriority().toString() : "MEDIUM");
            taskDetails.put("status", task.getStatus() != null ? task.getStatus().toString() : "New");
            taskDetails.put("tagsname", task.getTagsname() != null ? task.getTagsname() : new ArrayList<>());
            taskDetails.put("assigneesIds", task.getAssigneesIds() != null ? task.getAssigneesIds() : new ArrayList<>());
            taskDetails.put("reminderIds", task.getReminderIds() != null ? task.getReminderIds() : new ArrayList<>());
            taskDetails.put("workspaceId", workspaceId);
    
            // Thêm Task vào sub-collection của Workspace
            DocumentReference workspaceDocRef = db.collection("Workspace").document(workspaceId);
            workspaceDocRef.collection("Task").document(taskId).set(taskDetails).get();
    
            System.out.println("Task added successfully to Workspace ID: " + workspaceId);
        } catch (Exception e) {
            System.err.println("Error adding Task to Workspace: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    


    public void removeTask(String workspaceId, String taskId) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference workspaceDocRef = db.collection("Workspace").document(workspaceId);
            DocumentReference taskDocRef = workspaceDocRef.collection("Task").document(taskId);
            taskDocRef.delete().get(); // Xóa task từ Firestore
    
            // Cập nhật danh sách taskIds trong Workspace
            DocumentSnapshot workspaceSnapshot = workspaceDocRef.get().get();
            if (workspaceSnapshot.exists()) {
                List<String> taskIds = (List<String>) workspaceSnapshot.get("taskIds");
                if (taskIds != null && taskIds.contains(taskId)) {
                    taskIds.remove(taskId);
                    workspaceDocRef.update("taskIds", taskIds).get();
                }
            }
    
            // Xóa các Reminder liên quan đến task đó
            DocumentReference userDocRef = db.collection("User").document(UserService.currentUser.getUserId());
            ApiFuture<DocumentSnapshot> userSnapshotFuture = userDocRef.get();
            DocumentSnapshot userSnapshot = userSnapshotFuture.get();
            if (userSnapshot.exists()) {
                List<String> reminderIds = (List<String>) userSnapshot.get("reminderIds");
                if (reminderIds != null) {
                    List<String> remindersToRemove = new ArrayList<>();
                    for (String reminderId : reminderIds) {
                        DocumentReference reminderDocRef = userDocRef.collection("reminders").document(reminderId);
                        DocumentSnapshot reminderSnapshot = reminderDocRef.get().get();
                        if (reminderSnapshot.exists() && taskId.equals(reminderSnapshot.getString("taskID"))) {
                            remindersToRemove.add(reminderId);
                            reminderDocRef.delete(); // Xóa reminder khỏi Firestore
                        }
                    }
                    reminderIds.removeAll(remindersToRemove);
                    userDocRef.update("reminderIds", reminderIds).get(); // Cập nhật danh sách reminderIds của user
                }
            }
    
            System.out.println("Task and related reminders removed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    



    public static void editTask(String workspaceId, String taskId, Task updatedTask) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            
            
            // Chuyển đổi Task thành một map để lưu vào Firestore
            Map<String, Object> taskDetails = new HashMap<>();
            taskDetails.put("title", updatedTask.getTitle());
            taskDetails.put("description", updatedTask.getDescription());
            taskDetails.put("dueDate", updatedTask.getDueDate().toString());
            taskDetails.put("priority", updatedTask.getPriority().toString());
            taskDetails.put("status", updatedTask.getStatus().toString());
            taskDetails.put("assigneesIds", updatedTask.getAssigneesIds());
    
            // Cập nhật document của Task trong sub-collection "Task"
            DocumentReference workspaceDocRef = db.collection("Workspace").document(workspaceId);
            workspaceDocRef.collection("Task").document(taskId).update(taskDetails).get();
    
            System.out.println("Successfully updated Task with ID: " + taskId + " in Workspace: " + workspaceId);
        } catch (Exception e) {
            System.err.println("Error updating Task in Workspace: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static List<Task> viewAllTasks(String workspaceId) {
        List<Task> taskList = new ArrayList<>();
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference workspaceDocRef = db.collection("Workspace").document(workspaceId);
            ApiFuture<QuerySnapshot> future = workspaceDocRef.collection("Task").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
    
            for (QueryDocumentSnapshot document : documents) {
                try {
                    Task task = document.toObject(Task.class);
                    task.setTaskID(document.getId()); // Gán ID cho Task
                    taskList.add(task);
                } catch (Exception ex) {
                    System.err.println("Error deserializing Task: " + ex.getMessage());
                }
            }
    
            System.out.println("Successfully fetched all tasks from Workspace: " + workspaceId);
        } catch (Exception e) {
            System.err.println("Error fetching tasks from Workspace: " + e.getMessage());
            e.printStackTrace();
        }
        return taskList;
    }
    

public static void addReminderToTask(String workspaceId, String taskId, Reminder reminder) {
    try {
        Firestore db = FirestoreClient.getFirestore();
        String reminderId = UUID.randomUUID().toString();
        reminder.setReminderID(reminderId);

        // Set reminder details
        Map<String, Object> reminderDetails = new HashMap<>();
        reminderDetails.put("reminderID", reminder.getReminderID());
        reminderDetails.put("taskID", taskId);
        reminderDetails.put("recurrencePattern", reminder.getRecurrencePattern());
        reminderDetails.put("dueDate", reminder.getDueDate().toString());

        // Add reminder to Firestore under the appropriate task
        DocumentReference taskDocRef = db.collection("Workspace")
                .document(workspaceId)
                .collection("Task")
                .document(taskId);
        taskDocRef.collection("Reminders").document(reminderId).set(reminderDetails).get();

        System.out.println("Reminder added to Task ID: " + taskId + " in Workspace ID: " + workspaceId);
    } catch (Exception e) {
        System.err.println("Error adding reminder to task: " + e.getMessage());
        e.printStackTrace();
    }
}

public static List<Task> getTasksForWorkspace(String workspaceId) {
    List<Task> taskList = new ArrayList<>();
    try {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection("Workspace")
                                            .document(workspaceId)
                                            .collection("Task")
                                            .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            Task task = document.toObject(Task.class);
            task.setTaskID(document.getId()); // Gán Task ID từ Firestore
            taskList.add(task);
        }
        System.out.println("Fetched " + taskList.size() + " tasks for Workspace ID: " + workspaceId);
    } catch (Exception e) {
        System.err.println("Error fetching tasks for Workspace ID: " + workspaceId + " - " + e.getMessage());
        e.printStackTrace();
    }
    return taskList;
}


public static void deleteTaskFromWorkspace(String workspaceId, String taskId) {
    try {
        Firestore db = FirestoreClient.getFirestore();

        // Xóa Task từ Firestore
        DocumentReference taskDocRef = db.collection("Workspace")
                                         .document(workspaceId)
                                         .collection("Task")
                                         .document(taskId);
        taskDocRef.delete().get();

        // Xóa tất cả các Reminder liên quan đến Task
        ApiFuture<QuerySnapshot> remindersFuture = taskDocRef.collection("Reminders").get();
        List<QueryDocumentSnapshot> reminders = remindersFuture.get().getDocuments();
        for (QueryDocumentSnapshot reminderDoc : reminders) {
            reminderDoc.getReference().delete(); // Xóa từng Reminder
        }

        System.out.println("Task with ID: " + taskId + " and related reminders deleted from Workspace ID: " + workspaceId);
    } catch (Exception e) {
        System.err.println("Error deleting Task with ID: " + taskId + " from Workspace ID: " + workspaceId + " - " + e.getMessage());
        e.printStackTrace();
    }
}

public static void updateReminder(String workspaceId, String taskId, Reminder reminder) {
    try {
        Firestore db = FirestoreClient.getFirestore();

        // Tạo map từ đối tượng Reminder để lưu vào Firestore
        Map<String, Object> reminderDetails = new HashMap<>();
        reminderDetails.put("reminderID", reminder.getReminderID());
        reminderDetails.put("taskID", taskId);
        reminderDetails.put("workspaceID", workspaceId);
        reminderDetails.put("recurrencePattern", reminder.getRecurrencePattern());
        reminderDetails.put("dueDate", reminder.getDueDate().toString());

        // Cập nhật reminder vào Firestore
        DocumentReference reminderDocRef = db.collection("Workspace")
                .document(workspaceId)
                .collection("Task")
                .document(taskId)
                .collection("Reminders")
                .document(reminder.getReminderID());
        reminderDocRef.set(reminderDetails).get();

        System.out.println("Reminder updated successfully: " + reminder.getReminderID());
    } catch (Exception e) {
        System.err.println("Error updating reminder: " + e.getMessage());
        e.printStackTrace();
    }
}


public static List<Reminder> getRemindersForTask(String workspaceId, String taskId) {
    List<Reminder> reminders = new ArrayList<>();
    try {
        Firestore db = FirestoreClient.getFirestore();

        // Lấy danh sách Reminder từ Firestore
        ApiFuture<QuerySnapshot> future = db.collection("Workspace")
                                            .document(workspaceId)
                                            .collection("Task")
                                            .document(taskId)
                                            .collection("Reminders")
                                            .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            Reminder reminder = document.toObject(Reminder.class);
            reminder.setReminderID(document.getId());
            reminders.add(reminder);
        }

        System.out.println("Fetched " + reminders.size() + " reminders for Task ID: " + taskId);
    } catch (Exception e) {
        System.err.println("Error fetching reminders for Task ID: " + taskId + " - " + e.getMessage());
        e.printStackTrace();
    }
    return reminders;
}


public static void removeReminder(String workspaceId, String taskId, String reminderId) {
    try {
        Firestore db = FirestoreClient.getFirestore();

        // Xóa Reminder từ Firestore
        DocumentReference reminderDocRef = db.collection("Workspace")
                                             .document(workspaceId)
                                             .collection("Task")
                                             .document(taskId)
                                             .collection("Reminders")
                                             .document(reminderId);
        reminderDocRef.delete().get();

        System.out.println("Reminder with ID: " + reminderId + " removed successfully.");
    } catch (Exception e) {
        System.err.println("Error removing reminder with ID: " + reminderId + " - " + e.getMessage());
        e.printStackTrace();
    }
}

}

    // Các phương thức khác: removeTask(), editTask(), fetchAllTasks()...

