/**
 * Created by udit on 16/02/16.
 */

package in.incognitech.reminder;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import in.incognitech.reminder.model.Reminder;
import in.incognitech.reminder.provider.ReminderAdapter;
import in.incognitech.reminder.util.Constants;
import in.incognitech.reminder.util.DateUtils;
import in.incognitech.reminder.util.FontAwesomeManager;
import in.incognitech.reminder.util.Utils;
import in.incognitech.reminder.util.image.ImageCache;
import in.incognitech.reminder.util.image.ImageFetcher;

public class OutgoingRemindersActivity extends DrawerActivity implements View.OnClickListener {

    private Uri friendUri;
    private String friendID;
    private Bitmap friendPhoto;
    Cursor cursor;
    ArrayList<Reminder> reminds;
    ContentResolver resolver;
    private ImageFetcher mImageFetcher;
    private String reminderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


        this.customSetup(R.layout.activity_drawer, R.id.reminder_toolbar, R.id.reminder_nav_view);
        resolver = this.getContentResolver();
        cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        String photoUrl = Utils.getCurrentUserPhotoUrl(this);

        if ( ! photoUrl.equals("") ) {
            ImageView imageView = (ImageView)findViewById(R.id.friend_avatar);
            mImageFetcher.loadImage(photoUrl, imageView);
        }



        ListView listView = new ListView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        listView.setLayoutParams(params);

        listView.setAdapter(new ReminderAdapter(this, R.layout.item, Utils.getCurrentUserID(this), ReminderAdapter.OUTGOING));

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content_drawer_container);
        layout.addView(listView);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addReminderIntent = new Intent(OutgoingRemindersActivity.this, AddReminderActivity.class);
                startActivity(addReminderIntent);
            }
        });
    }

    private void setupImageCache() {
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, Constants.IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        mImageFetcher = new ImageFetcher(this, (int) getResources().getDimension(R.dimen.login_background_width), (int) getResources().getDimension(R.dimen.login_background_height));
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
    }













        private void testingOutLoud() {

        Date date = new Date();
        String curDate = DateUtils.toString(date);
        String gmtDate = DateUtils.toGMT(date);

        Reminder test = new Reminder();
        test.setAuthor(Utils.getCurrentUserID(this));
        test.setDescription("testing out loud");
        test.setFriend(Utils.getCurrentUserID(this));
        test.setReminderDate(curDate);
        test.setReminderDateGMT(gmtDate);

        ReminderAdapter.addReminder(test);
    }


    @Override
    public void onClick(View v) {

        Reminder newReminder = new Reminder();
       newReminder.setAuthor(Utils.getCurrentUserID(OutgoingRemindersActivity.this));
        ReminderAdapter.deleteReminder(newReminder);



    }
}
