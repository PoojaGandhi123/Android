package in.incognitech.reminder;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import in.incognitech.reminder.db.FriendDbHelper;
import in.incognitech.reminder.util.Constants;
import in.incognitech.reminder.util.FontAwesomeManager;
import in.incognitech.reminder.util.image.ImageCache;
import in.incognitech.reminder.util.image.ImageFetcher;

public class FriendDetailActivity extends DrawerActivity {

    private Uri friendUri;
    private String friendID;     // friends unique ID
    private ImageView friendAvatar;
    private TextView friendDisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.customSetup(R.layout.activity_friend_detail, R.id.friend_toolbar, R.id.friend_nav_view);

        final Uri uri = getIntent().getData();
        setFriend(uri);

        setupImageCache();

        friendAvatar = (ImageView) findViewById(R.id.friend_avatar);
        friendDisplayName = (TextView) findViewById(R.id.friend_display_name);
    }

    private void setupImageCache() {
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, Constants.IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        mImageFetcher = new ImageFetcher(this, (int) this.getResources().getDimension(R.dimen.user_avatar_width), (int) this.getResources().getDimension(R.dimen.user_avatar_height));
        mImageFetcher.addImageCache(this.getSupportFragmentManager(), cacheParams);
    }

    /**
     * Sets the contact that this Fragment displays, or clears the display if the contact argument
     * is null. This will re-initialize all the views and start the queries to the system contacts
     * provider to populate the contact information.
     *
     * @param friendLookupUri The contact lookup Uri to load and display in this fragment. Passing
     *                         null is valid and the fragment will display a message that no
     *                         contact is currently selected instead.
     */
    public void setFriend(Uri friendLookupUri) {

        friendUri = friendLookupUri;

        // If the Uri contains data, load the contact's image and load contact details.
        if (friendLookupUri != null) {
            // Starts two queries to to retrieve contact information from the Contacts Provider.
            // restartLoader() is used instead of initLoader() as this method may be called
            // multiple times.
            Bitmap photo = retrieveFriendPhoto();
            String name = retrieveFriendName();
            ArrayList<String[]> emails = retrieveFriendEmail();
            ArrayList<String[]> numbers = retrieveFriendNumber();

            ArrayList<String> activeEmails = FriendDbHelper.getActiveEmails(this, friendLookupUri.toString());

            ((ImageView) findViewById(R.id.friend_avatar)).setImageBitmap(photo);
            ((TextView) findViewById(R.id.friend_display_name)).setText(name);

            float d = getResources().getDisplayMetrics().density;

            LinearLayout.LayoutParams typeMargins = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            typeMargins.setMargins(0, (int)(10*d), 0, 0);

            RelativeLayout.LayoutParams relativeLayoutMargins = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            relativeLayoutMargins.setMargins(0, (int)(5*d), 0, (int)(10*d));

            RelativeLayout.LayoutParams iconAlignment = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            iconAlignment.addRule(RelativeLayout.ALIGN_PARENT_END);

            LinearLayout emailContainer = (LinearLayout) findViewById(R.id.friend_email_container);
            for(int i=0;i<emails.size();i++) {
                String[] emailData = emails.get(i);

                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setLayoutParams(relativeLayoutMargins);

                TextView type = new TextView(this);
                type.setLayoutParams(typeMargins);
                type.setText(emailData[1]);
                type.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                linearLayout.addView(type);

                TextView email = new TextView(this);
                email.setText(emailData[0]);
                email.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                relativeLayout.addView(email);

                if(activeEmails.contains(emails.get(i))) {
                    TextView verifiedIcon = new TextView(this);
                    verifiedIcon.setLayoutParams(iconAlignment);
                    verifiedIcon.setTypeface(FontAwesomeManager.getTypeface(this, FontAwesomeManager.FONTAWESOME));
                    verifiedIcon.setText(getResources().getString(R.string.fa_check_square_o));
                    verifiedIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    verifiedIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(FriendDetailActivity.this, "YO Goto Reminder Activity", Toast.LENGTH_LONG).show();
                        }
                    });
                    relativeLayout.addView(verifiedIcon);
                } else {
                    TextView verifiedIcon = new TextView(this);
                    verifiedIcon.setLayoutParams(iconAlignment);
                    verifiedIcon.setTypeface(FontAwesomeManager.getTypeface(this, FontAwesomeManager.FONTAWESOME));
                    verifiedIcon.setText(getResources().getString(R.string.fa_share_square_o));
                    verifiedIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    verifiedIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(FriendDetailActivity.this, "YO Goto Invite", Toast.LENGTH_LONG).show();
                        }
                    });
                    relativeLayout.addView(verifiedIcon);
                }

                linearLayout.addView(relativeLayout);
                emailContainer.addView(linearLayout);
            }

            LinearLayout phoneContainer = (LinearLayout) findViewById(R.id.friend_phone_container);
            for(int i=0;i<numbers.size();i++) {

                String[] numberData = numbers.get(i);

                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setLayoutParams(relativeLayoutMargins);

                TextView type = new TextView(this);
                type.setLayoutParams(typeMargins);
                type.setText(numberData[1]);
                type.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                linearLayout.addView(type);

                TextView number = new TextView(this);
                number.setText(numberData[0]);
                number.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                relativeLayout.addView(number);

                // ToDo: Check if email invite button is there. then accordingly show invite numbers

                linearLayout.addView(relativeLayout);
                phoneContainer.addView(linearLayout);
            }
        }
    }

    private String retrieveFriendID() {

        if(friendID==null) {
            // getting friends ID
            Cursor cursorID = getContentResolver().query(friendUri,
                    new String[]{ContactsContract.Contacts._ID},
                    null, null, null);

            if (cursorID.moveToFirst()) {

                friendID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            }

            cursorID.close();
        }
        return friendID;
    }

    private String retrieveFriendName() {

        String friendName = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(friendUri, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            friendName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();
        return friendName;
    }

    private ArrayList<String[]> retrieveFriendEmail() {

        ArrayList<String[]> friendEmails = new ArrayList<String[]>();
        String friendID = retrieveFriendID();

        // Using the contact ID now we will get contact email
        Cursor cursorEmail = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Email.DATA, ContactsContract.CommonDataKinds.Email.TYPE},

                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",

                new String[]{friendID},
                null);

        if (cursorEmail.moveToFirst()) {
            do {
                int type = cursorEmail.getInt(cursorEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                String[] emailData = {
                        cursorEmail.getString(cursorEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)),
                        getResources().getText(ContactsContract.CommonDataKinds.Email.getTypeLabelResource(type)).toString()
                };
                friendEmails.add(emailData);
            } while(cursorEmail.moveToNext());
        }

        cursorEmail.close();
        return friendEmails;
    }

    private ArrayList<String[]> retrieveFriendNumber() {

        ArrayList<String[]> friendNumbers = new ArrayList<String[]>();
        String friendID = retrieveFriendID();

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",

                new String[]{friendID},
                null);

        if (cursorPhone.moveToFirst()) {
            do {
                int type = cursorPhone.getInt(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                String[] numberData = {
                        cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
                        getResources().getText(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(type)).toString()
                };
                friendNumbers.add(numberData);
            } while (cursorPhone.moveToNext());
        }

        cursorPhone.close();
        return friendNumbers;
    }

    private Bitmap retrieveFriendPhoto() {

        Bitmap photo = null;
        String friendID = retrieveFriendID();

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(friendID)), true);

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } else {
                photo = BitmapFactory.decodeResource(getResources(), android.R.drawable.sym_def_app_icon);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return photo;
    }
}
