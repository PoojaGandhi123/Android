package in.incognitech.reminder.provider;

import com.firebase.client.Firebase;

import java.util.List;
import java.util.Map;

import in.incognitech.reminder.model.Reminder;
import in.incognitech.reminder.util.Constants;

/**
 * Created by udit on 15/02/16.
 */
public class FirebaseReminder {

    public static List<Reminder> getReminders() {
        List<Reminder> reminders = null;
        return reminders;
    }

    public static void addReminder(Firebase firebaseRef, Reminder reminder) {
        Map<String, Object> map = reminder.convertToMap();

        Firebase fbReminderRef = firebaseRef.child(Constants.FIREBASE_REMINDERS_PATH);
        fbReminderRef.push().setValue(map);
    }

}
