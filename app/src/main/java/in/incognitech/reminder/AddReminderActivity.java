package in.incognitech.reminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Layout;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import in.incognitech.reminder.db.FriendDbHelper;
import in.incognitech.reminder.model.Reminder;
import in.incognitech.reminder.model.User;
import in.incognitech.reminder.provider.ReminderAdapter;
import in.incognitech.reminder.util.Constants;
import in.incognitech.reminder.util.DateUtils;
import in.incognitech.reminder.util.FontAwesomeManager;
import in.incognitech.reminder.util.TextDrawable;
import in.incognitech.reminder.util.Utils;

public class AddReminderActivity extends DrawerActivity implements ValueEventListener {

    TextView date1;
    EditText description;
    ImageView map;
    int year_x, month_x, day_x, hour_x, minute_x, ampm_x;
    static final int date_id =0, time_id=1;
    final Calendar myCalender =Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy    hh:mm aa");

    public final static int FRIEND_ID = R.string.FRIEND_ID;
    public final static int REMINDER_CONTEXT = R.string.REMINDER_CONTEXT;
    public final static String REMINDER_CONTEXT_ADD = "add";
    public final static String REMINDER_CONTEXT_EDIT = "edit";
    private final static int REQUEST_CODE = 101;
    private FloatingActionButton fab;
    private String userID;
    private String userDisplayName;
    private String reminderID;
    TextView displayNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if ( ! Utils.isUserLoggedIn(this) ) {
            redirectToLogin();
        }

        sdf.setTimeZone(TimeZone.getDefault());

        super.onCreate(savedInstanceState);

        this.customSetup(R.layout.activity_add_reminder, R.id.add_reminder_toolbar, R.id.add_reminder_nav_view);

        date1 = (TextView) findViewById(R.id.add_textView4);
        description =(EditText) findViewById(R.id.editText);

        year_x=myCalender.get(Calendar.YEAR);
        month_x=myCalender.get(Calendar.MONTH);
        day_x=myCalender.get(Calendar.DAY_OF_MONTH);
        hour_x=myCalender.get(Calendar.HOUR_OF_DAY);
        minute_x=myCalender.get(Calendar.MINUTE);
        ampm_x=myCalender.get(Calendar.AM_PM);

        final TextView timeRange = (TextView) findViewById(R.id.add_textView7);
        final LinearLayout time_range = (LinearLayout) findViewById(R.id.time_range);



        String strDate = sdf.format(myCalender.getTime());
        date1.setText(strDate);

        date1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(date_id);
            }
        });

        map = (ImageView) findViewById(R.id.imageButton);

        map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                timeRange.setVisibility(View.VISIBLE);
                time_range.setVisibility(View.VISIBLE);


            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( userID == null ) {
                    redirectToHome("No friend or self context passed. Please choose friend.");
                } else {

                    String reminderContext = (String) fab.getTag(REMINDER_CONTEXT);

                    Reminder newReminder = new Reminder();
                    newReminder.setAuthor(Utils.getCurrentUserID(AddReminderActivity.this));
                    newReminder.setDescription(description.getText().toString());
                    newReminder.setReminderDate(DateUtils.toString(myCalender.getTime()));
                    newReminder.setReminderDateGMT(DateUtils.toGMT(myCalender.getTime()));
                    newReminder.setFriend(userID);
                    newReminder.setKey(reminderID);

                    if(reminderContext == REMINDER_CONTEXT_ADD) {
                        ReminderAdapter.addReminder(newReminder);
                    } else if ( reminderContext == REMINDER_CONTEXT_EDIT ) {
                        ReminderAdapter.updateReminder(newReminder);
                    }

                    Intent i = new Intent(AddReminderActivity.this, OutgoingRemindersActivity.class);
                    startActivity(i);

                    finish();
                }
            }
        });

        fab.setTag(REMINDER_CONTEXT, REMINDER_CONTEXT_ADD);

        this.setupIcons();

        displayNameTextView = (TextView) findViewById(R.id.add_textView2);
        displayNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddReminderActivity.this, FriendsActivity.class);
                startActivityForResult(i, REQUEST_CODE);
            }
        });

        Bundle extras = getIntent().getExtras();
        if ( extras != null ) {
            userID = extras.getString("userID");
            userDisplayName = extras.getString("userDisplayName");
            reminderID = extras.getString("reminderID");

            if ( ! TextUtils.isEmpty(userID) && ! TextUtils.isEmpty(userDisplayName) ) {
                setFriendDetails();
            } else if ( ! TextUtils.isEmpty(reminderID) ) {
                setReminderDetails();
            } else {
                redirectToHome("There's no activity to edit. Please choose an activity.");
            }

        } else {
            redirectToHome("No friend or self context passed. Please choose friend.");
        }
    }

    private void setupIcons() {
        TextDrawable faIcon = new TextDrawable(this);
        faIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
        faIcon.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        faIcon.setTypeface(FontAwesomeManager.getTypeface(this, FontAwesomeManager.FONTAWESOME));
        faIcon.setText(getResources().getText(R.string.fa_map_marker));
        map.setImageDrawable(faIcon);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        faIcon = new TextDrawable(this);
        faIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        faIcon.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        faIcon.setTypeface(FontAwesomeManager.getTypeface(this, FontAwesomeManager.FONTAWESOME));
        faIcon.setText(getResources().getText(R.string.fa_check));
        fab.setImageDrawable(faIcon);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == REQUEST_CODE ) {
            if ( data != null ) {
                userID = data.getStringExtra("userID");
                userDisplayName = data.getStringExtra("userDisplayName");
                setFriendDetails();
            } else {
                redirectToHome("No friend or self context passed. Please choose friend.");
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id){

        if (id== date_id) {
            return new DatePickerDialog(this, dpickerListener, year_x, month_x, day_x);
        }if (id==time_id){
            return new TimePickerDialog(this, tpickerListener, hour_x, minute_x, false);
        }
        return null;
    }


    private DatePickerDialog.OnDateSetListener dpickerListener =new DatePickerDialog.OnDateSetListener(){


        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalender.set(Calendar.YEAR, year);
            myCalender.set(Calendar.MONTH, monthOfYear);
            myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            showDialog(time_id);

        }
    };

    private TimePickerDialog.OnTimeSetListener tpickerListener =new TimePickerDialog.OnTimeSetListener(){


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalender.set(Calendar.MINUTE, minute);

            String date = sdf.format(myCalender.getTime());

            date1.setText(date);

        }
    };

    private void redirectToHome(String message) {
        ComponentName name = getCallingActivity();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, OutgoingRemindersActivity.class);
        startActivity(i);
        finish();
    }

    private void setFriendDetails() {
        displayNameTextView.setText(userDisplayName);
        displayNameTextView.setTag(FRIEND_ID, userID);
    }

    private void setReminderDetails() {
        Firebase reminderRef = firebaseRef.child(Constants.FIREBASE_REMINDERS_PATH).child(reminderID);
        reminderRef.addValueEventListener(this);
    }

    private void redirectToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Reminder reminder = dataSnapshot.getValue(Reminder.class);
        if ( reminder != null ) {
            User friend = FriendDbHelper.getFriend(this, reminder.getFriend());

            if ( friend != null ) {
                userDisplayName = friend.getName() + (friend.getId().equals(Utils.getCurrentUserID(this)) ? " (Self)" : "");
                userID = friend.getId();
                setFriendDetails();
            } else {
                redirectToHome("Reminder data is corrupted. Please choose a valid reminder.");
            }

            DateFormat utcFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date utcDate = utcFormat.parse(reminder.getReminderDateGMT());
                String tzDateStr = sdf.format(utcDate);
                Date tzDate = sdf.parse(tzDateStr);
                myCalender.setTime(tzDate);
                date1.setText(tzDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            description.setText(reminder.getDescription());

            getSupportActionBar().setTitle(getString(R.string.title_activity_edit_reminder));

            fab.setTag(REMINDER_CONTEXT, REMINDER_CONTEXT_EDIT);

        } else {
            redirectToHome("There's no activity to edit. Please choose an activity.");
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
