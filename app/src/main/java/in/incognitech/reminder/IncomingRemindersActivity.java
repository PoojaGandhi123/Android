/**
 * Created by udit on 16/02/16.
 */

package in.incognitech.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.Date;

import in.incognitech.reminder.model.Reminder;
import in.incognitech.reminder.provider.ReminderAdapter;
import in.incognitech.reminder.util.DateUtils;
import in.incognitech.reminder.util.Utils;

public class IncomingRemindersActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.customSetup(R.layout.activity_drawer, R.id.reminder_toolbar, R.id.reminder_nav_view);

        ListView listView = new ListView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        listView.setLayoutParams(params);

        listView.setAdapter(new ReminderAdapter(this, R.layout.reminder_row, Utils.getCurrentUserID(this), ReminderAdapter.INCOMING));

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content_drawer_container);
        layout.addView(listView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userID = Utils.getCurrentUserID(IncomingRemindersActivity.this);
                String displayName = Utils.getCurrentUserDisplayName(IncomingRemindersActivity.this);

                Intent addReminderIntent = new Intent(IncomingRemindersActivity.this, AddReminderActivity.class);
                addReminderIntent.putExtra("userID", userID);
                addReminderIntent.putExtra("userDisplayName", displayName + " (Self)" );
                startActivity(addReminderIntent);
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