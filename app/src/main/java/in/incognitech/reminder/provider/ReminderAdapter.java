package in.incognitech.reminder.provider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.Map;

import in.incognitech.reminder.R;
import in.incognitech.reminder.api.FirebaseAPI;
import in.incognitech.reminder.model.Reminder;
import in.incognitech.reminder.util.Constants;

/**
 * Created by udit on 14/02/16.
 */
public class ReminderAdapter extends ArrayAdapter<Reminder> implements ChildEventListener/*, ValueEventListener*/ {

    public static int INCOMING = 0;
    public static int OUTGOING = 1;

    private int reminderType;

    static class ViewHolder {
        TextView textView;
    }

    public ReminderAdapter(Context context, int resource, String currentUserID) {
        super(context, resource);

        this.reminderType = INCOMING;

        Firebase ref = FirebaseAPI.getInstance().child(Constants.FIREBASE_REMINDERS_PATH);
        Query queryRef = ref.orderByChild("friend").equalTo(currentUserID);
        queryRef.addChildEventListener(this);
    }

    public ReminderAdapter(Context context, int resource, String currentUserID, int reminderType) {
        super(context, resource);

        this.reminderType = reminderType;

        String queryBy = (this.reminderType==INCOMING) ? "friend" : "author";

        Firebase ref = FirebaseAPI.getInstance().child(Constants.FIREBASE_REMINDERS_PATH);
        Query queryRef = ref.orderByChild(queryBy).equalTo(currentUserID);
        queryRef.addChildEventListener(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Reminder reminder = this.getItem(position);

        View row;
        ViewHolder holder;

        if ( convertView == null ) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.outgoing_reminder_row, null);
            holder = new ViewHolder();
            holder.textView = (TextView) row.findViewById(R.id.outgoing_reminder_desc);
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ViewHolder) row.getTag();
        }

        // Set the text
        holder.textView.setText(reminder.getDescription());

        return row;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Reminder reminder = dataSnapshot.getValue(Reminder.class);
        this.add(reminder);
        this.notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Reminder reminder = dataSnapshot.getValue(Reminder.class);
        this.remove(reminder);
        this.add(reminder);
        this.notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Reminder reminder = dataSnapshot.getValue(Reminder.class);
        this.remove(reminder);
        this.notifyDataSetChanged();
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        Reminder reminder = dataSnapshot.getValue(Reminder.class);
        this.remove(reminder);
        this.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

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
