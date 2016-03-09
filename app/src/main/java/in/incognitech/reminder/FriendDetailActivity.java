package in.incognitech.reminder;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
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

public class FriendDetailActivity extends DrawerActivity implements View.OnClickListener {

    private Uri friendUri;
    private String friendID;     // friends unique ID

    private final static int ACTION_TYPE = R.string.ACTION_TYPE;
    private final static String ACTION_TYPE_INVITE = "invite";
    private final static String ACTION_TYPE_REMINDER = "reminder";
    private final static int ACTION_CONTEXT = R.string.ACTION_CONTEXT;
    private final static String ACTION_CONTEXT_PHONE = "phone";
    private final static String ACTION_CONTEXT_EMAIL = "email";
    private final static int ACTION_DATA = R.string.ACTION_DATA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.customSetup(R.layout.activity_friend_detail, R.id.friend_toolbar, R.id.friend_nav_view);

        final Uri uri = getIntent().getData();
        setFriend(uri);

        setupImageCache();
    }

    @Override
    public void onClick(View v) {
        String actionType = (String) v.getTag(ACTION_TYPE);
        switch (actionType) {
            case ACTION_TYPE_INVITE:
                String actionContext = (String) v.getTag(ACTION_CONTEXT);
                switch (actionContext) {
                    case ACTION_CONTEXT_PHONE:
                        String number = (String) v.getTag(ACTION_DATA);

                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.putExtra("sms_body", "Hey! Install this App! It's awesome!!");
                        sendIntent.putExtra("address", number);
                        sendIntent.setData(Uri.parse("smsto:"+number));
                        sendIntent.setType("vnd.android-dir/mms-sms");

                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},101);
                            return;
                        }

                        startActivity(sendIntent);

                        break;
                    case ACTION_CONTEXT_EMAIL:
                        String email = (String) v.getTag(ACTION_DATA);

//                        Uri uri = Uri.parse("mailto:" + email)
//                                .buildUpon()
//                                .appendQueryParameter("subject", "Reminder Invite !")
//                                .appendQueryParameter("body", "Hey! Install this App! It's awesome!!")
//                                .build();
//
//                        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);

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
            case ACTION_TYPE_REMINDER:
                String userID = (String) v.getTag(ACTION_DATA);
                System.out.println(userID);
                break;
        }
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
            boolean foundVerifiedEmail = false;
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
                    verifiedIcon.setTag(ACTION_TYPE, ACTION_TYPE_REMINDER);
                    verifiedIcon.setTag(ACTION_DATA, emailData[0]);
                    verifiedIcon.setOnClickListener(this);
                    relativeLayout.addView(verifiedIcon);
                    foundVerifiedEmail = true;
                } else {
                    TextView inviteIcon = new TextView(this);
                    inviteIcon.setLayoutParams(iconAlignment);
                    inviteIcon.setTypeface(FontAwesomeManager.getTypeface(this, FontAwesomeManager.FONTAWESOME));
                    inviteIcon.setText(getResources().getString(R.string.fa_share_square_o));
                    inviteIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    inviteIcon.setTag(ACTION_TYPE, ACTION_TYPE_INVITE);
                    inviteIcon.setTag(ACTION_CONTEXT, ACTION_CONTEXT_EMAIL);
                    inviteIcon.setTag(ACTION_DATA, emailData[0]);
                    inviteIcon.setOnClickListener(this);
                    relativeLayout.addView(inviteIcon);
                }

                linearLayout.addView(relativeLayout);
                emailContainer.addView(linearLayout);
            }

            if ( ! foundVerifiedEmail ) {
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

                    TextView inviteIcon = new TextView(this);
                    inviteIcon.setLayoutParams(iconAlignment);
                    inviteIcon.setTypeface(FontAwesomeManager.getTypeface(this, FontAwesomeManager.FONTAWESOME));
                    inviteIcon.setText(getResources().getString(R.string.fa_share_square_o));
                    inviteIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    inviteIcon.setTag(ACTION_TYPE, ACTION_TYPE_INVITE);
                    inviteIcon.setTag(ACTION_CONTEXT, ACTION_CONTEXT_PHONE);
                    inviteIcon.setTag(ACTION_DATA, numberData[0]);
                    inviteIcon.setOnClickListener(this);
                    relativeLayout.addView(inviteIcon);

                    linearLayout.addView(relativeLayout);
                    phoneContainer.addView(linearLayout);
                }
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