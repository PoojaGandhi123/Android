/**
 * Created by udit on 16/02/16.
 */

package in.incognitech.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import in.incognitech.reminder.provider.ReminderAdapter;
import in.incognitech.reminder.util.Utils;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

public class OutgoingRemindersActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.customSetup(R.layout.activity_drawer, R.id.reminder_toolbar, R.id.reminder_nav_view);

        CardRecyclerView recyclerView = (CardRecyclerView) findViewById(R.id.reminder_list);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ReminderAdapter(this, Utils.getCurrentUserID(this), ReminderAdapter.OUTGOING));

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
