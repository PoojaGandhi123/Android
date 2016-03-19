package in.incognitech.reminder.card;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import in.incognitech.reminder.R;
import in.incognitech.reminder.model.Reminder;
import in.incognitech.reminder.model.User;
import in.incognitech.reminder.util.Constants;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.component.CardHeaderView;

/**
 * Created by udit on 18/03/16.
 */
public class ReminderCard extends Card {

    private Reminder reminder;
    private User friend;

    public ReminderCard(Context context) {
        super(context);
    }

    public ReminderCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        if(view != null) {
            ImageView friendGravatarView = (ImageView) parent.findViewById(R.id.reminder_friend_gravatar);
            if ( friendGravatarView != null ) {
                ImageLoader imageLoader = ImageLoader.getInstance();
                float f = view.getResources().getDisplayMetrics().density;
                ImageSize targetSize = new ImageSize((int)(45*f), (int)(45*f));
                imageLoader.displayImage(friend.getPhotoUrl(), friendGravatarView, targetSize);
            }

            CardHeaderView headerView = (CardHeaderView) parent.findViewById(R.id.card_header_layout);
            if ( headerView != null ) {
                CardHeader header = new CardHeader(mContext);
                header.setTitle(reminder.getDescription());
                header.setButtonExpandVisible(true);

                header.setPopupMenu(R.menu.card_reminder_menu, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                    @Override
                    public void onMenuItemClick(BaseCard card, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.reminder_nav_edit:
                                Toast.makeText(mContext, "Click on " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });

                headerView.addCardHeader(header);
            }

            TextView dateView = (TextView) parent.findViewById(R.id.reminder_date);
            if (dateView != null) {
                DateFormat utcFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Calendar calendar = Calendar.getInstance();
                try {
                    Date utcDate = utcFormat.parse(reminder.getReminderDateGMT());
                    DateFormat tzFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
                    tzFormat.setTimeZone(TimeZone.getDefault());
                    String tzDateStr = tzFormat.format(utcDate);
                    Date tzDate = tzFormat.parse(tzDateStr);
                    calendar.setTime(tzDate);
                    String dateStr = "" + calendar.get(Calendar.DATE) + " "
                            + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + ", "
                            + calendar.get(Calendar.YEAR);
                    dateView.setText(dateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            TextView friendView = (TextView) parent.findViewById(R.id.reminder_friend_name);
            if (friendView != null) {
                friendView.setText(friend.getName());
            }
        }
    }

    public Reminder getReminder() {
        return reminder;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }
}
