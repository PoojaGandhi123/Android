/**
 * Created by udit on 16/02/16.
 */

package in.incognitech.reminder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import in.incognitech.reminder.model.Reminder;
import in.incognitech.reminder.provider.ReminderAdapter;
import in.incognitech.reminder.util.DateUtils;
import in.incognitech.reminder.util.Utils;

public class IncomingRemindersActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_reminders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.incoming_reminders_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.incoming_reminders_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final View headerView = navigationView.getHeaderView(0);

        this.setDrawerMenuIcons(navigationView.getMenu());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String displayName = extras.getString("displayName");
            String email = extras.getString("email");
            String photoUrl = extras.getString("photoUrl");

            if ( photoUrl != null ) {
                ImageView imageView = (ImageView) headerView.findViewById(R.id.userAvatar);
                mImageFetcher.loadImage(photoUrl, imageView);
            }

            ((TextView) headerView.findViewById(R.id.userDisplayName)).setText(displayName);
            ((TextView) headerView.findViewById(R.id.userEmail)).setText(email);
        }

        ListView listView = (ListView) findViewById(R.id.list_view_incoming_reminders);
        listView.setAdapter(new ReminderAdapter(this, R.layout.incoming_reminder_row, Utils.getCurrentUserID(this), ReminderAdapter.INCOMING));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IncomingRemindersActivity.this.testingOutLoud();
                Toast.makeText(IncomingRemindersActivity.this, "This will goto Create New Reminder Activity.", Toast.LENGTH_LONG).show();
            }
        });
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

}