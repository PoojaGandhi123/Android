package in.incognitech.reminder.api;

import android.content.Context;

import com.firebase.client.Firebase;

import in.incognitech.reminder.util.Constants;

/**
 * Created by udit on 16/02/16.
 */
public class FirebaseAPI {
    private static Firebase firebaseRef;

    public static Firebase getInstance() {
        if ( firebaseRef == null ) {
            firebaseRef = new Firebase(Constants.FIREBASE_APP_URL);
        }
        return firebaseRef;
    }

    private FirebaseAPI() {
    }

    public static void setAndroidContext(Context context) {
        Firebase.setAndroidContext(context);
    }
}
