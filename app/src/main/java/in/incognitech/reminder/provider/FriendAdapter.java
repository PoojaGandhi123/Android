package in.incognitech.reminder.provider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import in.incognitech.reminder.R;
import in.incognitech.reminder.model.Friend;

/**
 * Created by udit on 17/02/16.
 */
public class FriendAdapter extends ArrayAdapter<Friend> {

    private List<Friend> friendList;
    private String phoneNumber;
    private String message;
    static class ViewHolder {
        TextView name;
        TextView email;
        Button sendInvites;
    }

    public FriendAdapter(Context context, int resource, List<Friend> friendList) {
        super(context, resource, friendList);
        this.friendList = friendList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Friend friend = this.getItem(position);

        View row;
        ViewHolder holder;

        if ( convertView == null ) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.friend_row, null);
            holder = new ViewHolder();
            holder.name = (TextView) row.findViewById(R.id.friend_display_name);
            holder.email = (TextView) row.findViewById(R.id.friend_email);
            holder.sendInvites=(Button)row.findViewById(R.id.inviteFriends);
            holder.sendInvites.setVisibility(View.VISIBLE);
            holder.sendInvites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(getContext(),"Button Clicked",Toast.LENGTH_SHORT).show();

                    phoneNumber="191";
                    message="Testing Out Loud";
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);

                    smsIntent.setData(Uri.parse("smsto:"));
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address"  , phoneNumber);
                    smsIntent.putExtra("sms_body", message);



                }
            });
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ViewHolder) row.getTag();
        }

        holder.name.setText(friend.getName());
        holder.email.setText(friend.getEmail());

        return row;
    }

    public void filter(String keyword) {
        keyword = keyword.toLowerCase(Locale.getDefault());
        this.clear();
        if (keyword.length() == 0) {
            this.addAll(friendList);
        } else {
            for (Friend f : friendList) {
                if (f.getName().toLowerCase(Locale.getDefault())
                        .contains(keyword)) {
                    this.add(f);
                }
            }
        }
        notifyDataSetChanged();
    }
}
