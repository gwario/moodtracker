package at.ameise.moodtracker.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import at.ameise.moodtracker.app.util.NotificationUtil;

/**
 * Receiver which runs daily at 12:00 am.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 10.12.15.
 */
public class DailyReceiver extends BroadcastReceiver {

    public DailyReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationUtil.issueReminderNotification(context);
    }
}
