/**
 * Created by udit on 16/02/16.
 */

package in.incognitech.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import in.incognitech.reminder.provider.ReminderAdapter;
import in.incognitech.reminder.util.Utils;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.listener.dismiss.DefaultDismissableManager;

public class IncomingRemindersActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.customSetup(R.layout.activity_drawer, R.id.reminder_toolbar, R.id.reminder_nav_view);

        CardListView cardListView = (CardListView) findViewById(R.id.reminder_list);
        ReminderAdapter reminderAdapter = new ReminderAdapter(this, Utils.getCurrentUserID(this), ReminderAdapter.INCOMING);
        reminderAdapter.setDismissable(new DefaultDismissableManager());
        reminderAdapter.setEnableUndo(true);
        cardListView.setAdapter(reminderAdapter);

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

}