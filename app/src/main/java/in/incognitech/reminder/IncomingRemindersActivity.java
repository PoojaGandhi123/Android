/**
 * Created by udit on 16/02/16.
 */

package in.incognitech.reminder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ListView;
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
        setContentView(R.layout.activity_reminders);

        super.customSetup(R.id.toolbar, R.id.nav_view);

        ListView listView = (ListView) findViewById(R.id.list_view_reminders);
        listView.setAdapter(new ReminderAdapter(this, R.layout.reminder_row, Utils.getCurrentUserID(this), ReminderAdapter.INCOMING));

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