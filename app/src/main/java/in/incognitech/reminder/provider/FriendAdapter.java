package in.incognitech.reminder.provider;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import in.incognitech.reminder.FriendsActivity;
import in.incognitech.reminder.R;
import in.incognitech.reminder.db.FriendDbHelper;
import in.incognitech.reminder.model.User;
import in.incognitech.reminder.query.ContactsQuery;
import in.incognitech.reminder.util.Utils;

/**
 * Created by udit on 17/02/16.
 */
public class FriendAdapter extends CursorAdapter implements SectionIndexer {

    private Context context;
    private LayoutInflater mInflater; // Stores the layout inflater
    private AlphabetIndexer mAlphabetIndexer; // Stores the AlphabetIndexer instance
    private TextAppearanceSpan highlightTextSpan; // Stores the highlight text appearance style

    public final static int ACTION_TYPE = R.string.ACTION_TYPE;
    public final static String ACTION_TYPE_INVITE = "invite";
    public final static String ACTION_TYPE_REMINDER = "reminder";
    public final static int ACTION_CONTEXT = R.string.ACTION_CONTEXT;
    public final static String ACTION_CONTEXT_PHONE = "phone";
    public final static String ACTION_CONTEXT_EMAIL = "email";
    public final static int ACTION_DATA = R.string.ACTION_DATA;

    static class ViewHolder {
        ImageView image;
        TextView name;
        TextView detail;
    }

    public FriendAdapter(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Contacts Adapter.
     * @param context A context that has access to the app's layout.
     */
    public FriendAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);

        this.context = context;

        // Stores inflater for use later
        mInflater = LayoutInflater.from(context);

        // Loads a string containing the English alphabet. To fully localize the app, provide a
        // strings.xml file in res/values-<x> directories, where <x> is a locale. In the file,
        // define a string with android:name="alphabet" and contents set to all of the
        // alphabetic characters in the language in their proper sort order, in upper case if
        // applicable.
        final String alphabet = context.getString(R.string.alphabet);

        // Instantiates a new AlphabetIndexer bound to the column used to sort contact names.
        // The cursor is left null, because it has not yet been retrieved.
        mAlphabetIndexer = new AlphabetIndexer(null, ContactsQuery.SORT_KEY, alphabet);

        // Defines a span for highlighting the part of a display name that matches the search
        // string
        highlightTextSpan = new TextAppearanceSpan(context, R.style.searchTextHiglight);
    }

    /**
     * Identifies the start of the search string in the display name column of a Cursor row.
     * E.g. If displayName was "Adam" and search query (mSearchTerm) was "da" this would
     * return 1.
     *
     * @param displayName The contact display name.
     * @return The starting position of the search string in the display name, 0-based. The
     * method returns -1 if the string is not found in the display name, or if the search
     * string is empty or null.
     */
    private int indexOfSearchQuery(String displayName) {
        String searchTerm = ((FriendsActivity)context).getSearchTerm();
        if (!TextUtils.isEmpty(searchTerm)) {
            return displayName.toLowerCase(Locale.getDefault()).indexOf(
                    searchTerm.toLowerCase(Locale.getDefault()));
        }
        return -1;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflates the list item layout.
        final View itemLayout =
                mInflater.inflate(R.layout.friend_row, parent, false);

        // Creates a new ViewHolder in which to store handles to each view resource. This
        // allows bindView() to retrieve stored references instead of calling findViewById for
        // each instance of the layout.
        final ViewHolder holder = new ViewHolder();
        holder.image = (ImageView) itemLayout.findViewById(R.id.friend_avatar);
        holder.name = (TextView) itemLayout.findViewById(R.id.friend_display_name);
        holder.detail = (TextView) itemLayout.findViewById(R.id.friend_detail);

        // Stores the resourceHolder instance in itemLayout. This makes resourceHolder
        // available to bindView and other methods that receive a handle to the item view.
        itemLayout.setTag(holder);

        // Returns the item layout view
        return itemLayout;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String searchTerm = ((FriendsActivity) context).getSearchTerm();

        // Gets handles to individual view resources
        final ViewHolder holder = (ViewHolder) view.getTag();

        if ( searchTerm != null ) {

            // For Android 3.0 and later, gets the thumbnail image Uri from the current Cursor row.
            // For platforms earlier than 3.0, this isn't necessary, because the thumbnail is
            // generated from the other fields in the row.
            String photoUri = cursor.getString(ContactsQuery.PHOTO_URI);
            String contactID = cursor.getString(ContactsQuery.CONTACT_ID);
            String contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactID).toString();

            String displayName = cursor.getString(ContactsQuery.DISPLAY_NAME);

            String mimeType = cursor.getString(ContactsQuery.MIMETYPE);
            holder.detail.setTag(ACTION_TYPE, ACTION_TYPE_INVITE);
            if (mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                String detail = cursor.getString(ContactsQuery.EMAIL);

                User user = FriendDbHelper.getFriendByEmail(context, detail);

                if ( user != null ) {
                    holder.name.setText(user.getName());
                    holder.detail.setText(user.getEmail());
                    holder.detail.setTag(ACTION_TYPE, ACTION_TYPE_REMINDER);
                    holder.detail.setTag(ACTION_DATA, user.getId());

                    ImageLoader imageLoader = ImageLoader.getInstance();
                    float f = context.getResources().getDisplayMetrics().density;
                    ImageSize targetSize = new ImageSize((int)(45*f), (int)(45*f));
                    imageLoader.displayImage(user.getPhotoUrl(), holder.image, targetSize);
                } else {
                    holder.detail.setText(detail);
                    holder.detail.setTag(ACTION_CONTEXT, ACTION_CONTEXT_EMAIL);
                    holder.detail.setTag(ACTION_DATA, detail);
                }
            } else if (mimeType.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                String detail = cursor.getString(ContactsQuery.EMAIL);
                holder.detail.setText(detail);
                holder.detail.setTag(ACTION_CONTEXT, ACTION_CONTEXT_PHONE);
                holder.detail.setTag(ACTION_DATA, detail);
            }

            final int startIndex = indexOfSearchQuery(displayName);

            if (startIndex == -1) {
                // If the user didn't do a search, or the search string didn't match a display
                // name, show the display name without highlighting
                holder.name.setText(displayName);

            } else {
                // If the search string matched the display name, applies a SpannableString to
                // highlight the search string with the displayed display name

                // Wraps the display name in the SpannableString
                final SpannableString highlightedName = new SpannableString(displayName);

                // Sets the span to start at the starting point of the match and end at "length"
                // characters beyond the starting point
                highlightedName.setSpan(highlightTextSpan, startIndex,
                        startIndex + searchTerm.length(), 0);

                // Binds the SpannableString to the display name View object
                holder.name.setText(highlightedName);
            }

            if (contactUri != null) {
                // load Image
                ImageLoader imageLoader = ImageLoader.getInstance();
                float f = context.getResources().getDisplayMetrics().density;
                ImageSize targetSize = new ImageSize((int)(45*f), (int)(45*f));
                imageLoader.displayImage(contactUri, holder.image, targetSize);
            }
        } else {
            String userID = cursor.getString(cursor.getColumnIndex(FriendDbHelper.userID_COLUMN));
            String displayName = cursor.getString(cursor.getColumnIndex(FriendDbHelper.name_COLUMN));
            String email = cursor.getString(cursor.getColumnIndex(FriendDbHelper.email_COLUMN));
            String photoUrl = cursor.getString(cursor.getColumnIndex(FriendDbHelper.photoURL_COLUMN));

            holder.name.setText(displayName);
            holder.detail.setText(email);
            holder.detail.setTag(ACTION_TYPE, ACTION_TYPE_REMINDER);
            holder.detail.setTag(ACTION_DATA, userID);

            ImageLoader imageLoader = ImageLoader.getInstance();
            float f = context.getResources().getDisplayMetrics().density;
            ImageSize targetSize = new ImageSize((int)(45*f), (int)(45*f));
            imageLoader.displayImage(photoUrl, holder.image, targetSize);
        }
    }

    /**
     * Overrides swapCursor to move the new Cursor into the AlphabetIndex as well as the
     * CursorAdapter.
     */
    @Override
    public Cursor swapCursor(Cursor newCursor) {
        // Update the AlphabetIndexer with new cursor as well
        mAlphabetIndexer.setCursor(newCursor);
        return super.swapCursor(newCursor);
    }

    /**
     * An override of getCount that simplifies accessing the Cursor. If the Cursor is null,
     * getCount returns zero. As a result, no test for Cursor == null is needed.
     */
    @Override
    public int getCount() {
        if (getCursor() == null) {
            return 0;
        }
        return super.getCount();
    }

    @Override
    public Object[] getSections() {
        return mAlphabetIndexer.getSections();
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        if (getCursor() == null) {
            return 0;
        }
        return mAlphabetIndexer.getPositionForSection(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        if (getCursor() == null) {
            return 0;
        }
        return mAlphabetIndexer.getSectionForPosition(position);
    }

    /**
     * Load a contact photo thumbnail and return it as a Bitmap,
     * resizing the image to the provided image dimensions as needed.
     * @param photoData photo ID Prior to Honeycomb, the contact's _ID value.
     * For Honeycomb and later, the value of PHOTO_THUMBNAIL_URI.
     * @return A thumbnail Bitmap, sized to the provided width and height.
     * Returns null if the thumbnail is not found.
     */
    private Bitmap loadContactPhotoThumbnail(String photoData) {
        // Creates an asset file descriptor for the thumbnail file.
        AssetFileDescriptor afd = null;
        // try-catch block for file not found
        try {
            // Creates a holder for the URI.
            Uri thumbUri;
            // If Android 3.0 or later
            if (Utils.hasHoneycomb()) {
                // Sets the URI from the incoming PHOTO_THUMBNAIL_URI
                thumbUri = Uri.parse(photoData);
            } else {
                // Prior to Android 3.0, constructs a photo Uri using _ID
                /*
                 * Creates a contact URI from the Contacts content URI
                 * incoming photoData (_ID)
                 */
                final Uri contactUri = Uri.withAppendedPath(ContactsQuery.CONTENT_URI, photoData);
                /*
                 * Creates a photo URI by appending the content URI of
                 * Contacts.Photo.
                 */
                thumbUri =
                        Uri.withAppendedPath(
                                contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            }

        /*
         * Retrieves an AssetFileDescriptor object for the thumbnail
         * URI
         * using ContentResolver.openAssetFileDescriptor
         */
            afd = context.getContentResolver().
                    openAssetFileDescriptor(thumbUri, "r");
        /*
         * Gets a file descriptor from the asset file descriptor.
         * This object can be used across processes.
         */
            FileDescriptor fileDescriptor = afd.getFileDescriptor();
            // Decode the photo file and return the result as a Bitmap
            // If the file descriptor is valid
            if (fileDescriptor != null) {
                // Decodes the bitmap
                return BitmapFactory.decodeFileDescriptor(
                        fileDescriptor, null, null);
            }
            // If the file isn't found
        } catch (FileNotFoundException e) {
            /*
             * Handle file not found errors
             */
            // In all cases, close the asset file descriptor
        } finally {
            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {}
            }
        }
        return null;
    }

}
