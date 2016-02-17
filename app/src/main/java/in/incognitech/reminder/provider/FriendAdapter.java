package in.incognitech.reminder.provider;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import in.incognitech.reminder.model.Friend;

/**
 * Created by udit on 17/02/16.
 */
public class FriendAdapter extends ArrayAdapter<Friend> {

    public FriendAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
