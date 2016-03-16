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

import in.incognitech.reminder.provider.ReminderAdapter;
import in.incognitech.reminder.util.Utils;

public class OutgoingRemindersActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.customSetup(R.layout.activity_drawer, R.id.reminder_toolbar, R.id.reminder_nav_view);

        ListView listView = new ListView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        listView.setLayoutParams(params);

        listView.setAdapter(new ReminderAdapter(this, R.layout.reminder_row, Utils.getCurrentUserID(this), ReminderAdapter.OUTGOING));

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content_drawer_container);
        layout.addView(listView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent friendsIntent = new Intent(OutgoingRemindersActivity.this, FriendsActivity.class);
                startActivity(friendsIntent);
            }
        });
    }

}
