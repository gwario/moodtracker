package at.ameise.moodtracker.app.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import at.ameise.moodtracker.R;
import at.ameise.moodtracker.app.NotificationId;
import at.ameise.moodtracker.app.activity.SignInActivity;

/**
 * Contains methods to ease handling of notifications.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 10.12.15.
 */
public class NotificationUtil {

    private NotificationUtil() {}

    public static void issueReminderNotification(Context context) {

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, SignInActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
            .setSmallIcon(R.mipmap.ic_launcher)
            //.setCategory(Notification.CATEGORY_SOCIAL)
            .setPriority(Notification.PRIORITY_LOW)
            .setContentTitle("Have you tracked your mood yet?")
            .setContentText("The more often you track the more accurate statistics are and the more you benefit from them!")
            .setContentIntent(resultPendingIntent);

        mNotifyMgr.notify(NotificationId.TRACK_MOOD_REMINDER, mBuilder.build());
    }

    public static void consumeReminderNotification(Context context) {

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyMgr.cancel(NotificationId.TRACK_MOOD_REMINDER);
    }
}
