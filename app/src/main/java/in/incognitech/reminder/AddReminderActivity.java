package in.incognitech.reminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity {

    TextView date1;
    ImageView map;
    int year_x, month_x, day_x, hour_x, minute_x, ampm_x;
    static final int date_id =0, time_id=1;
    final Calendar myCalender =Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy    hh:mm aa");
    SimpleDateFormat stf = new SimpleDateFormat("hh:mm aa");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        date1 = (TextView) findViewById(R.id.add_textView4);

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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


}
