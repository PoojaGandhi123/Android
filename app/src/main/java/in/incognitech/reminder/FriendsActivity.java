package in.incognitech.reminder;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import in.incognitech.reminder.db.FriendDbHelper;
import in.incognitech.reminder.model.User;
import in.incognitech.reminder.provider.FriendAdapter;
import in.incognitech.reminder.query.ContactsQuery;
import in.incognitech.reminder.util.Utils;

/**
 * Created by udit on 17/02/16.
 */
public class FriendsActivity extends DrawerActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    private FriendAdapter friendAdapter;
    private ListView friendListView;

    private String searchTerm; // Stores the current search query term

    private Cursor defaultCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if ( ! Utils.isUserLoggedIn(this) ) {
            redirectToLogin();
        }

        super.onCreate(savedInstanceState);

        this.customSetup(R.layout.activity_friends, R.id.friend_toolbar, R.id.friend_nav_view);

        SQLiteDatabase db = new FriendDbHelper(this).getWritableDatabase();
        String where = FriendDbHelper.authorID_COLUMN + " = ? AND " + FriendDbHelper.isActive_COLUMN + " = 'true' AND " + FriendDbHelper.userID_COLUMN + "!= ?";
        String whereArgs[] = {Utils.getCurrentUserID(this), Utils.getCurrentUserID(this)};
        String groupBy = null;
        String having = null;
        String order = null;
        defaultCursor = db.query(FriendDbHelper.DATABASE_TABLE, null, where, whereArgs, groupBy, having, order);

        friendAdapter = new FriendAdapter(this, defaultCursor);

        friendListView = (ListView) findViewById(R.id.list_view_friends);
        friendListView.setAdapter(friendAdapter);
        friendListView.setOnItemClickListener(this);
        friendListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                ImageLoader imageLoader = ImageLoader.getInstance();
                // Pause image loader to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    imageLoader.pause();
                } else {
                    imageLoader.resume();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_friends_search, menu);

        MenuItem searchItem = menu.findItem( R.id.search_friend);

        // Retrieves the system search manager service
        final SearchManager searchManager =
                (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);

        // Retrieves the SearchView from the search menu item
        final SearchView searchView = (SearchView) searchItem.getActionView();

        // Assign searchable info to SearchView
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(this.getComponentName()));

        ((EditText) searchView.findViewById(R.id.search_src_text)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    onQueryTextChange(s.toString());
                }
            }
        });

        // Set listeners for SearchView
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                // Nothing to do when the action item is expanded
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                // When the user collapses the SearchView the current search string is
                // cleared and the loader restarted.
                if (!TextUtils.isEmpty(searchTerm)) {
                    onSelectionCleared();
                }
                searchTerm = null;
                friendAdapter.swapCursor(defaultCursor);
                return true;
            }
        });

        if (searchTerm != null) {
            // If search term is already set here then this fragment is
            // being restored from a saved state and the search menu item
            // needs to be expanded and populated again.

            // Stores the search term (as it will be wiped out by
            // onQueryTextChange() when the menu item is expanded).
            final String savedSearchTerm = searchTerm;

            searchItem.expandActionView();

            // Sets the SearchView to the previous search string
            searchView.setQuery(savedSearchTerm, false);
        }

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String queryText) {
        // Nothing needs to happen when the user submits the search string
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed.  Updates
        // the search filter, and restarts the loader to do a new query
        // using the new search string.

        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

        if ( newFilter == null ) {
            searchTerm = newFilter;
            friendAdapter.swapCursor(defaultCursor);
            return true;
        }

        // Don't do anything if the filter is empty
        if (searchTerm == null && newFilter == null) {
            friendAdapter.swapCursor(defaultCursor);
            return true;
        }

        // Don't do anything if the new filter is the same as the current filter
        if (searchTerm != null && searchTerm.equals(newFilter)) {
            return true;
        }

        // Updates current filter to new filter
        searchTerm = newFilter;

        // Restarts the loader. This triggers onCreateLoader(), which builds the
        // necessary content Uri from mSearchTerm.
        getSupportLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, this);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // If this is the loader for finding contacts in the Contacts Provider
        // (the only one supported)
        if (id == ContactsQuery.QUERY_ID) {
            Uri contentUri;

            contentUri = ContactsQuery.CONTENT_URI;

            if (searchTerm == null) {
                return new CursorLoader(
                    this,
                    contentUri,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.SELECTION,
                    null,
                    ContactsQuery.SORT_ORDER
                );
            } else {
                return new CursorLoader(
                    this,
                    contentUri,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.FILTER_SELECTION,
                    new String[] {"%" + searchTerm + "%"},
                    ContactsQuery.SORT_ORDER
                );
            }
        }

        System.out.println("onCreateLoader - incorrect ID provided (" + id + ")");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // This swaps the new cursor into the adapter.
        if (loader.getId() == ContactsQuery.QUERY_ID && searchTerm != null) {
            friendAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == ContactsQuery.QUERY_ID) {
            // When the loader is being reset, clear the cursor from the adapter. This allows the
            // cursor resources to be freed.
            friendAdapter.swapCursor(null);
        }
    }

    /**
     * Called when ListView selection is cleared, for example
     * when search mode is finished and the currently selected
     * contact should no longer be selected.
     */
    private void onSelectionCleared() {
        // Clears currently checked item
        friendListView.clearChoices();
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Gets the Cursor object currently bound to the ListView
        final Cursor cursor = friendAdapter.getCursor();

        // Moves to the Cursor row corresponding to the ListView item that was clicked
        cursor.moveToPosition(position);

        TextView friendDetail = (TextView) view.findViewById(R.id.friend_detail);
        String actionType = (String) friendDetail.getTag(FriendAdapter.ACTION_TYPE);

        switch (actionType) {
            case FriendAdapter.ACTION_TYPE_INVITE:
                String actionContext = (String) friendDetail.getTag(FriendAdapter.ACTION_CONTEXT);
                switch (actionContext) {
                    case FriendAdapter.ACTION_CONTEXT_PHONE:
                        String number = (String) friendDetail.getTag(FriendAdapter.ACTION_DATA);

                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.putExtra("sms_body", "Hey! Install this App! It's awesome!!");
                        sendIntent.putExtra("address", number);
                        sendIntent.setData(Uri.parse("smsto:"+number));
                        sendIntent.setType("vnd.android-dir/mms-sms");

                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 101);
                            return;
                        }

                        startActivity(sendIntent);

                        break;
                    case FriendAdapter.ACTION_CONTEXT_EMAIL:
                        String email = (String) friendDetail.getTag(FriendAdapter.ACTION_DATA);

                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",email, null));
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Reminder Invite !");
                        intent.putExtra(Intent.EXTRA_TEXT, "Hey! Install this App! It's awesome!!");

                        try {
                            startActivity(intent);
                        } catch(ActivityNotFoundException e) {
                            Toast.makeText(this, "Error: No supported app found.", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
                break;
            case FriendAdapter.ACTION_TYPE_REMINDER:

                String userID = (String) friendDetail.getTag(FriendAdapter.ACTION_DATA);
                User user = FriendDbHelper.getFriend(this, userID);

                if ( getCallingActivity() != null ) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("userID", userID);
                    resultIntent.putExtra("userDisplayName", user.getName());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Intent intent = new Intent(this, AddReminderActivity.class);
                    intent.putExtra("userID", userID);
                    intent.putExtra("userDisplayName", user.getName());
                    startActivity(intent);
                    finish();
                }
                finish();
                break;
        }

    }

    private void redirectToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
