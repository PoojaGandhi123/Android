package in.incognitech.reminder.card;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import in.incognitech.reminder.R;
import in.incognitech.reminder.db.FriendDbHelper;
import in.incognitech.reminder.model.Reminder;
import in.incognitech.reminder.model.User;
import it.gmariotti.cardslib.library.internal.CardExpand;

/**
 * Created by udit on 18/03/16.
 */
public class ReminderCardExpand extends CardExpand {

    private Reminder reminder;

    public ReminderCardExpand(Context context) {
        super(context);
    }

    public ReminderCardExpand(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        if(view != null) {
            TextView reminderDescView = (TextView) parent.findViewById(R.id.reminder_desc);
            if (reminderDescView != null) {
                reminderDescView.setText(reminder.getDescription());
            }

            TextView reminderTimestampView = (TextView) parent.findViewById(R.id.reminder_desc);
            if (reminderTimestampView != null) {
                reminderTimestampView.setText(reminder.getReminderDate());
            }

            ImageView authorGravatarView = (ImageView) parent.findViewById(R.id.reminder_author_gravatar);
            if ( authorGravatarView != null ) {
                User author = FriendDbHelper.getFriend(mContext, reminder.getAuthor());
                ImageLoader imageLoader = ImageLoader.getInstance();
                float f = view.getResources().getDisplayMetrics().density;
                ImageSize targetSize = new ImageSize((int)(45*f), (int)(45*f));
                imageLoader.displayImage(author.getPhotoUrl(), authorGravatarView, targetSize);
            }
        }
    }

    public Reminder getReminder() {
        return reminder;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }
}
