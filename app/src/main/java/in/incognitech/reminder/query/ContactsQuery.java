package in.incognitech.reminder.query;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by udit on 04/03/16.
 *
 * This interface defines constants for the Cursor and CursorLoader, based on constants defined
 * in the {@link android.provider.ContactsContract.Contacts} class.
 */
public class ContactsQuery {

    // An identifier for the loader
    final static public int QUERY_ID = 1;

    // A content URI for the Contacts table
    final static public Uri CONTENT_URI = ContactsContract.Data.CONTENT_URI;

    // The selection clause for the CursorLoader query. The search criteria defined here
    // restrict results to contacts that have a display name and are linked to visible groups.
    // Notice that the search on the string provided by the user is implemented by appending
    // the search string to CONTENT_FILTER_URI.
    @SuppressLint("InlinedApi")
    final static public String SELECTION = ContactsContract.Data.DISPLAY_NAME_PRIMARY + "<>''" +
            " AND " + ContactsContract.Data.IN_VISIBLE_GROUP + " = 1" +
            " AND ( " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" +
            " OR " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "' )";

    final static public String FILTER_SELECTION = ContactsContract.Data.DISPLAY_NAME_PRIMARY + "<>''" +
            " AND " + ContactsContract.Data.IN_VISIBLE_GROUP + " = 1" +
            " AND " + ContactsContract.Data.DISPLAY_NAME_PRIMARY + " LIKE ?" +
            " AND ( " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" +
            " OR " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "' )";

    // The desired sort order for the returned Cursor. In Android 3.0 and later, the primary
    // sort key allows for localization. In earlier versions. use the display name as the sort
    // key.
    @SuppressLint("InlinedApi")
    final static public String SORT_ORDER = ContactsContract.Data.SORT_KEY_PRIMARY;

    // The projection for the CursorLoader query. This is a list of columns that the Contacts
    // Provider should return in the Cursor.
    @SuppressLint("InlinedApi")
    final static public String[] PROJECTION = {

            ContactsContract.Data._ID,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.Data.PHOTO_URI,
            ContactsContract.Data.MIMETYPE,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            SORT_ORDER,
    };

    // The query column numbers which map to each value in the projection
    final static public int ID = 0;
    final static public int CONTACT_ID = 1;
    final static public int DISPLAY_NAME = 2;
    final static public int PHOTO_URI = 3;
    final static public int MIMETYPE = 4;
    final static public int EMAIL = 5;
    final static public int PHONE_NUMBER = 6;
    final static public int SORT_KEY = 7;
}