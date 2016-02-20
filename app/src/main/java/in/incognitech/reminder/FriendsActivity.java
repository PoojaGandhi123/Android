package in.incognitech.reminder;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import in.incognitech.reminder.model.Friend;
import in.incognitech.reminder.provider.FriendAdapter;

/**
 * Created by udit on 17/02/16.
 */
public class FriendsActivity extends DrawerActivity {

    private ArrayList<Friend> friendsList;
    private Cursor friendCursor;
    private FriendAdapter friendAdapter;
    private ListView friendListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.customSetup(R.layout.activity_friends, R.id.friend_toolbar, R.id.friend_nav_view);

        friendListView = (ListView) findViewById(R.id.list_view_friends);

        friendsList = new ArrayList<Friend>();
        friendCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        LoadContacts loadContacts = new LoadContacts();
        loadContacts.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_friends_search, menu);

        MenuItem menuItemSearch = menu.findItem( R.id.search_friend);
        SearchView searchView = (SearchView) menuItemSearch.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
        // TODO Auto-generated method stub

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                 TODO Auto-generated method stub
                friendAdapter.filter(newText);
                return false;
            }
        });

        return true;
    }


    @Override
    protected void onStop() {
        super.onStop();
        friendCursor.close();
    }

    class LoadContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (friendCursor != null) {
                Log.e("count", "" + friendCursor.getCount());
                if (friendCursor.getCount() == 0) {
                    Toast.makeText(FriendsActivity.this, "No contacts in your contact list.", Toast.LENGTH_LONG).show();
                }

                while (friendCursor.moveToNext()) {
                    String name = friendCursor.getString(friendCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String EmailAddr = friendCursor.getString(friendCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    String image_thumb = friendCursor.getString(friendCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));

                    Friend friend = new Friend();
                    friend.setPhotoUrl(image_thumb);
                    friend.setName(name);
                    friend.setEmail(EmailAddr);
                    friendsList.add(friend);
                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            friendAdapter = new FriendAdapter(FriendsActivity.this, R.id.list_view_friends, friendsList);
            friendListView.setAdapter(friendAdapter);

            // Select item on listclick
            friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Log.e("search", "here---------------- listener");

                    Friend friend = friendsList.get(i);
                }
            });

            friendListView.setFastScrollEnabled(true);
        }
    }
}
