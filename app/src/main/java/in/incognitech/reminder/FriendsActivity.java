package in.incognitech.reminder;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import in.incognitech.reminder.provider.FriendAdapter;
import in.incognitech.reminder.query.ContactsQuery;
import in.incognitech.reminder.util.Utils;

/**
 * Created y udit on 17/02/16.
 */
public class FriendsActivity extends DrawerActivity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private FriendAdapter friendAdapter;
    private ListView friendListView;

    private String searchTerm; // Stores the current search query term

    // Whether or not the search query has changed since the last time the loader was refreshed
    private boolean searchQueryChanged;

    // Stores the previously selected search item so that on a configuration change the same item
    // can be reselected again
    private int mPreviouslySelectedSearchItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.customSetup(R.layout.activity_friends, R.id.friend_toolbar, R.id.friend_nav_view);

        friendAdapter = new FriendAdapter(this);

        friendListView = (ListView) findViewById(R.id.list_view_friends);
        friendListView.setAdapter(friendAdapter);
        friendListView.setOnItemClickListener(this);

        // If there's a previously selected search item from a saved state then don't bother
        // initializing the loader as it will be restarted later when the query is populated into
        // the action bar search view (see onQueryTextChange() in onCreateOptionsMenu()).
        if (mPreviouslySelectedSearchItem == 0) {
            // Initialize the loader, and create a loader identified by ContactsQuery.QUERY_ID
            getSupportLoaderManager().initLoader(ContactsQuery.QUERY_ID, null, this);
        }

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

        // Set listeners for SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

                // Don't do anything if the filter is empty
                if (searchTerm == null && newFilter == null) {
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
                searchQueryChanged = true;
                getSupportLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, FriendsActivity.this);
                return true;
            }
        });

        if (Utils.hasICS()) {
            // This listener added in ICS
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
                    getSupportLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, FriendsActivity.this);
                    return true;
                }
            });
        }

        if (searchTerm != null) {
            // If search term is already set here then this fragment is
            // being restored from a saved state and the search menu item
            // needs to be expanded and populated again.

            // Stores the search term (as it will be wiped out by
            // onQueryTextChange() when the menu item is expanded).
            final String savedSearchTerm = searchTerm;

            // Expands the search menu item
            if (Utils.hasICS()) {
                searchItem.expandActionView();
            }

            // Sets the SearchView to the previous search string
            searchView.setQuery(savedSearchTerm, false);
        }



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

            // There are two types of searches, one which displays all contacts and
            // one which filters contacts by a search query. If searchTerm is set
            // then a search query has been entered and the latter should be used.

            if (searchTerm == null) {
                // Since there's no search string, use the content URI that searches the entire
                // Contacts table
                contentUri = ContactsQuery.CONTENT_URI;
            } else {
                // Since there's a search string, use the special content Uri that searches the
                // Contacts table. The URI consists of a base Uri and the search string.
                contentUri =
                        Uri.withAppendedPath(ContactsQuery.FILTER_URI, Uri.encode(searchTerm));
            }

            // Returns a new CursorLoader for querying the Contacts table. No arguments are used
            // for the selection clause. The search string is either encoded onto the content URI,
            // or no contacts search string is used. The other search criteria are constants. See
            // the ContactsQuery interface.
            return new CursorLoader(this,
                    contentUri,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.SELECTION,
                    null,
                    ContactsQuery.SORT_ORDER);
        }

        System.out.println("onCreateLoader - incorrect ID provided (" + id + ")");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // This swaps the new cursor into the adapter.
        if (loader.getId() == ContactsQuery.QUERY_ID) {
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



    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a contact has been selected.
     *
     * @param contactUri The contact Uri to the selected contact.
     */
    public void onContactSelected(Uri contactUri) {
            // Otherwise single pane layout, start a new ContactDetailActivity with
            // the contact Uri
            Intent intent = new Intent(this, FriendDetailActivity.class);
            intent.setData(contactUri);
            startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Gets the Cursor object currently bound to the ListView
        final Cursor cursor = friendAdapter.getCursor();

        // Moves to the Cursor row corresponding to the ListView item that was clicked
        cursor.moveToPosition(position);

        // Creates a contact lookup Uri from contact ID and lookup_key
        final Uri uri = ContactsContract.Contacts.getLookupUri(
                cursor.getLong(ContactsQuery.ID),
                cursor.getString(ContactsQuery.LOOKUP_KEY));

        // Notifies the parent activity that the user selected a contact. In a two-pane layout, the
        // parent activity loads a ContactDetailFragment that displays the details for the selected
        // contact. In a single-pane layout, the parent activity starts a new activity that
        // displays contact details in its own Fragment.
        this.onContactSelected(uri);
    }
}
