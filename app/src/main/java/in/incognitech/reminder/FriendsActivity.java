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
import android.widget.Button;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import in.incognitech.reminder.model.Friend;
import in.incognitech.reminder.provider.FriendAdapter;

/**
 * Created by udit on 17/02/16.
 */
public class FriendsActivity extends DrawerActivity {

    private ArrayList<Friend> friendsList;
    private Cursor friendCursor,PhoneCursor;
    private FriendAdapter friendAdapter;
    private ListView friendListView;
    private Button inviteFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.customSetup(R.layout.activity_friends, R.id.friend_toolbar, R.id.friend_nav_view);

        friendListView = (ListView) findViewById(R.id.list_view_friends);
        inviteFriends=(Button)findViewById(R.id.button);

        friendsList = new ArrayList<Friend>();

        friendCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") ASC");
        //PhoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");




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
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                friendAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }


    @Override
    protected void onStop() {
        super.onStop();
       // pCur.close();
        //emailCur.close();
        friendCursor.close();


    }

    class LoadContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {


            if(friendCursor.getCount()>0) {

                while(friendCursor.moveToNext()) {


                    String id = friendCursor.getString(friendCursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = friendCursor.getString(friendCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Friend friend = new Friend();
                    if (Integer.parseInt(friendCursor.getString(friendCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        System.out.println("name : " + name + ", ID : " + id);
                        friend.setName(name);
                        // get the phone number
                         Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id},null);
                        while (pCur.moveToNext()) {
                            String phone = pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            friend.setPhone(phone);
                            System.out.println("phone" + phone);
                        }
                    pCur.close();


                        //get Email

                       Cursor  emailCur = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (emailCur.moveToNext()) {
                            // This would allow you get several email addresses
                            // if the email addresses were stored in an array
                            String email = emailCur.getString(
                                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            String emailType = emailCur.getString(
                                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                            friend.setEmail(email);

                            System.out.println("Email " + email + " Email Type : " + emailType);
                        }
                     emailCur.close();
                        friendsList.add(friend);
                    }

                }

            }





//            String name = friendCursor.getString(friendCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//
//                    String EmailAddr = friendCursor.getString(friendCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                    String phoneNumber = friendCursor.getString(friendCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
//
//                    String image_thumb = friendCursor.getString(friendCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
//
//            Friend friend = new Friend();
//                    friend.setPhotoUrl(image_thumb);
//                    friend.setName(name);
//
//
//
//                    friend.setEmail(EmailAddr);
//                    friend.setPhone(phoneNumber);
//                    friendsList.add(friend);
//                }
//            }

//            if (PhoneCursor != null) {
//                Log.e("count", "" + PhoneCursor.getCount());
//                if (PhoneCursor.getCount() == 0) {
//                    Toast.makeText(FriendsActivity.this, "No contacts in your contact list.", Toast.LENGTH_LONG).show();
//                }
//
//                while (PhoneCursor.moveToNext()) {
//                    String name = PhoneCursor.getString(PhoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                    String phoneNumber = PhoneCursor.getString(PhoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
//                    String image_thumb = PhoneCursor.getString(PhoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
//
//                    Friend friend = new Friend();
//                    friend.setPhotoUrl(image_thumb);
//                    friend.setName(name);
//                    friend.setPhone(phoneNumber);
//                    friendsList.add(friend);
//                }
//            }




            else {
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
            friendListView.setTextFilterEnabled(true);
        }
    }
}
