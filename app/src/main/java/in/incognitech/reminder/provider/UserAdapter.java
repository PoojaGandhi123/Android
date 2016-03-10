package in.incognitech.reminder.provider;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import in.incognitech.reminder.api.FirebaseAPI;
import in.incognitech.reminder.model.User;
import in.incognitech.reminder.util.Constants;

/**
 * Created by udit on 09/03/16.
 */
public class UserAdapter {

    public static void setIsActive(String uid, boolean flag) {
        Firebase usersRef = FirebaseAPI.getInstance().child(Constants.FIREBASE_USERS_PATH);
        usersRef.child(uid).child("isActive").setValue(flag);
    }

    public static void addUser(User user) {
        Map<String, Object> map = user.convertToMap();

        Firebase fbUserRef = FirebaseAPI.getInstance().child(Constants.FIREBASE_USERS_PATH).child(user.getId());
        fbUserRef.setValue(map, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    System.out.println("Data could not be saved. " + firebaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                }
            }
        });
    }

}
