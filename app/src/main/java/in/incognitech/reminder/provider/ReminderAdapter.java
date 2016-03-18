package in.incognitech.reminder.provider;

import android.content.Context;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.Map;

import in.incognitech.reminder.R;
import in.incognitech.reminder.api.FirebaseAPI;
import in.incognitech.reminder.card.ReminderCard;
import in.incognitech.reminder.db.FriendDbHelper;
import in.incognitech.reminder.model.Reminder;
import in.incognitech.reminder.model.User;
import in.incognitech.reminder.util.Constants;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;

/**
 * Created by udit on 14/02/16.
 */
public class ReminderAdapter extends CardArrayRecyclerViewAdapter implements ChildEventListener {

    public static int INCOMING = 0;
    public static int OUTGOING = 1;

    private int reminderType;

    private ArrayList<Reminder> reminders;

    public ReminderAdapter(Context context, String currentUserID) {
        this(context, currentUserID, INCOMING);
    }

    public ReminderAdapter(Context context, String currentUserID, int reminderType) {

        super(context, null);

        this.reminderType = reminderType;

        this.reminders = new ArrayList<Reminder>();

        String queryBy = (this.reminderType==INCOMING) ? "friend" : "author";

        Firebase ref = FirebaseAPI.getInstance().child(Constants.FIREBASE_REMINDERS_PATH);
        Query queryRef = ref.orderByChild(queryBy).equalTo(currentUserID);
        queryRef.addChildEventListener(this);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Reminder reminder = dataSnapshot.getValue(Reminder.class);
        reminder.setKey(dataSnapshot.getKey());
        this.reminders.add(reminder);
        this.add(this.createCard(reminder));
        this.notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Reminder reminder = dataSnapshot.getValue(Reminder.class);
        reminder.setKey(dataSnapshot.getKey());
        int position = this.searchReminderByKey(dataSnapshot.getKey());
        if(position != -1) {
            this.reminders.remove(position);
            this.remove(position);

            this.reminders.add(reminder);
            this.add(this.createCard(reminder));

            this.notifyDataSetChanged();
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Reminder reminder = dataSnapshot.getValue(Reminder.class);
        reminder.setKey(dataSnapshot.getKey());
        int position = this.searchReminderByKey(dataSnapshot.getKey());
        if(position != -1) {
            this.reminders.remove(position);
            this.remove(position);

            this.notifyDataSetChanged();
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        Reminder reminder = dataSnapshot.getValue(Reminder.class);
        reminder.setKey(dataSnapshot.getKey());
        int position = this.searchReminderByKey(dataSnapshot.getKey());
        if(position != -1) {
            this.reminders.remove(position);
            this.remove(position);

            this.notifyDataSetChanged();
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    private Card createCard(Reminder reminder) {

        User friend = FriendDbHelper.getFriend(mContext, reminder.getFriend());

        ReminderCard card = new ReminderCard(mContext, R.layout.card);
        card.setReminder(reminder);
        card.setFriend(friend);

//        ReminderCardExpand expand = new ReminderCardExpand(mContext, R.layout.card_expand);
//        expand.setReminder(reminder);

        CardExpand expand = new CardExpand(mContext);
        expand.setTitle("YOYO");
        card.addCardExpand(expand);

        card.setSwipeable(true);

        return card;
    }

    private int searchReminderByKey(String key) {
        int position = -1;
        for (int i = 0; i<this.reminders.size();i++) {
            Reminder temp = this.reminders.get(i);
            if(temp.getKey().equals(key)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public static void addReminder(Reminder reminder) {
        Map<String, Object> map = reminder.convertToMap();

        Firebase fbReminderRef = FirebaseAPI.getInstance().child(Constants.FIREBASE_REMINDERS_PATH);
        fbReminderRef.push().setValue(map, new Firebase.CompletionListener() {
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
