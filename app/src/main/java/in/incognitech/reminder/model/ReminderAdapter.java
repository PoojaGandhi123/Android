package in.incognitech.reminder.model;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by udit on 14/02/16.
 */
public class ReminderAdapter extends ArrayAdapter<Reminder> {

    public ReminderAdapter(Context context, int resource, List<Reminder> objects) {
        super(context, resource, objects);
    }

}
