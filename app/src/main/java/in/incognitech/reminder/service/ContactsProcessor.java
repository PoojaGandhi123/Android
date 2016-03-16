package in.incognitech.reminder.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import in.incognitech.reminder.api.FirebaseAPI;
import in.incognitech.reminder.db.FriendDbHelper;
import in.incognitech.reminder.model.User;
import in.incognitech.reminder.query.ContactsQuery;
import in.incognitech.reminder.util.Constants;

/**
 * Created by udit on 15/03/16.
 */
public class ContactsProcessor extends IntentService implements ValueEventListener {

    private Firebase usersRef;

    public ContactsProcessor() {
        super("ContactsProcessor");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ContactsProcessor(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAPI.setAndroidContext(this);
        usersRef = FirebaseAPI.getInstance().child(Constants.FIREBASE_USERS_PATH);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Cursor contacts = getContentResolver().query(
                    ContactsQuery.CONTENT_URI,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.SELECTION,
                    null,
                    ContactsQuery.SORT_ORDER
            );

            if ( contacts.moveToFirst() ) {
                do {
                    String mimeType = contacts.getString(ContactsQuery.MIMETYPE);
                    if(mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                        String email = contacts.getString(ContactsQuery.EMAIL);
                        usersRef.orderByChild("email").equalTo(email).addValueEventListener(this);
                    }
                } while (contacts.moveToNext());
            }
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            User user = postSnapshot.getValue(User.class);
            if(user!=null) {
                FriendDbHelper.addFriend(this, user);
            }
            break;
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
