package in.incognitech.reminder;

import android.os.Bundle;
import android.widget.ListView;

import in.incognitech.reminder.provider.FriendAdapter;

/**
 * Created by udit on 17/02/16.
 */
public class FriendsActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        ListView listView = (ListView) findViewById(R.id.list_view_friends);
        listView.setAdapter(new FriendAdapter(this, R.layout.friend_row));
    }

}
