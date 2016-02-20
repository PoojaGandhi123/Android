package in.incognitech.reminder.provider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.incognitech.reminder.R;
import in.incognitech.reminder.model.Friend;

/**
 * Created by udit on 17/02/16.
 */
public class FriendAdapter extends ArrayAdapter<Friend> implements Filterable {

    private List<Friend> friendList;
    private List<Friend> filteredFriendList;
    private FriendFilter friendFilter = new FriendFilter();

    static class ViewHolder {
        TextView name;
        TextView email;
    }

    public FriendAdapter(Context context, int resource, List<Friend> friendList) {
        super(context, resource, friendList);
        this.friendList = new ArrayList<Friend>();
        this.friendList.addAll(friendList);
        this.filteredFriendList = new ArrayList<Friend>();
        this.filteredFriendList.addAll(friendList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Friend friend = filteredFriendList.get(position);

        View row;
        ViewHolder holder;

        if ( convertView == null ) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.friend_row, null);
            holder = new ViewHolder();
            holder.name = (TextView) row.findViewById(R.id.friend_display_name);
            holder.email = (TextView) row.findViewById(R.id.friend_email);
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ViewHolder) row.getTag();
        }

        holder.name.setText(friend.getName());
        holder.email.setText(friend.getEmail());

        return row;
    }

    @Override
    public Filter getFilter() {
        return friendFilter;
    }

    private class FriendFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String keyword = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();

            if(keyword != null && keyword.toString().length() > 0) {

                ArrayList<Friend> filteredItems = new ArrayList<Friend>();
                String email, name;

                for (int i = 0, l = friendList.size(); i < l; i++) {
                    name = friendList.get(i).getName();
                    email = friendList.get(i).getEmail();
                    if (name.toLowerCase().contains(keyword) || email.toLowerCase().contains(keyword)) {
                        filteredItems.add(friendList.get(i));
                    }
                }
                results.count = filteredItems.size();
                results.values = filteredItems;
            } else {
                synchronized(this)
                {
                    results.values = friendList;
                    results.count = friendList.size();
                }
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredFriendList = (ArrayList<Friend>)results.values;
//            notifyDataSetChanged();
            clear();
            for(int i = 0, l = filteredFriendList.size(); i < l; i++) {
                add(filteredFriendList.get(i));
            }
//            notifyDataSetInvalidated();
            notifyDataSetChanged();
        }
    }
}
