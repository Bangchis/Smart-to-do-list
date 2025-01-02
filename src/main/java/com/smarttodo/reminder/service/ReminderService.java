package com.smarttodo.reminder.service;


import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.smarttodo.firebase.service.FirebaseFirestore;
import com.smarttodo.reminder.model.Reminder;
import com.smarttodo.ui.CalendarPanel;
import com.smarttodo.user.model.User;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReminderService {

    private FirebaseFirestore firestore;
    private User currentUser;

    public ReminderService(User currentUser) {
        // Get Firestore instance from FirestoreClient
        this.currentUser = currentUser;
    }

    public List<Reminder> fetchReminders() throws InterruptedException, ExecutionException {
        // Reference to the user's document and the reminder sub-collection
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference userDocRef = db.collection("User").document(currentUser.getUserId());
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
}
