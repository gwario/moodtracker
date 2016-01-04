package at.ameise.moodtracker.app.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import at.ameise.moodtracker.R;
import at.ameise.moodtracker.app.TagConstant;
import at.ameise.moodtracker.app.domain.Mood;
import at.ameise.moodtracker.app.domain.MoodCursorHelper;
import at.ameise.moodtracker.app.domain.MoodTableHelper;
import at.ameise.moodtracker.app.util.Logger;
import at.ameise.moodtracker.app.util.NotificationUtil;
import at.ameise.moodtracker.app.util.ShareUtil;
import at.ameise.moodtracker.app.util.ToastUtil;

/**
 * Implementation of App Widget functionality.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com>.
 */
public class EnterMoodWidget extends AppWidgetProvider {

    private static final String ACTION_UPDATE_CLICKED = "at.ameise.moodtracker.action.updateButtonClick";
    private static final String ACTION_SHARE_CLICKED = "at.ameise.moodtracker.action.shareButtonClick";
    private static final String ACTION_MOOD_CLICKED_PREFIX = "at.ameise.moodtracker.action.moodButtonClick.";

    private static int currentMoodInt = -1;

    private static final int[] MOOD_BUTTONS = {
        R.id.ibCurrentMoodWidget0,
        R.id.ibCurrentMoodWidget1,
        R.id.ibCurrentMoodWidget2,
        R.id.ibCurrentMoodWidget3,
        R.id.ibCurrentMoodWidget4,
        R.id.ibCurrentMoodWidget5,
        R.id.ibCurrentMoodWidget6,
    };

    private RemoteViews remoteViews;
    private ComponentName thisWidget;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        if(remoteViews == null)
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.enter_mood_widget);
        if(thisWidget == null)
            thisWidget = new ComponentName(context, EnterMoodWidget.class);

        // There may be multiple widgets active, so update all of them
        for (int i = 0; i < appWidgetIds.length; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /**
     * Does the actual update of a widget
     * @param context the context
     * @param appWidgetManager the widget manager
     * @param appWidgetId the widget id
     */
    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        remoteViews.setOnClickPendingIntent(R.id.ibUpdateCurrentMoodWidget, getPendingSelfIntent(context, ACTION_UPDATE_CLICKED, null));
        remoteViews.setOnClickPendingIntent(R.id.ibShareCurrentMoodWidget, getPendingSelfIntent(context, ACTION_SHARE_CLICKED, null));

        for(int i = 0; i < MOOD_BUTTONS.length; i++) {
            remoteViews.setOnClickPendingIntent(MOOD_BUTTONS[i], getPendingSelfIntent(context, getMoodButtonAction(i), null));
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    /**
     * @param buttonIndex the button index
     * @return the action of the the button.
     */
    private static String getMoodButtonAction(int buttonIndex) {

        return ACTION_MOOD_CLICKED_PREFIX + buttonIndex;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);

        if(remoteViews == null)
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.enter_mood_widget);
        if(thisWidget == null)
            thisWidget = new ComponentName(context, EnterMoodWidget.class);

        Logger.debug(TagConstant.ENTER_MOOD, "Intent: " + intent);
        Logger.debug(TagConstant.ENTER_MOOD, "Extras: " + intent.getExtras());

        if (ACTION_UPDATE_CLICKED.equals(intent.getAction())) {

            NotificationUtil.consumeReminderNotification(context);

            Mood currentMood = new Mood(getCurrentMood(context));

            Logger.verbose(TagConstant.ENTER_MOOD, "Creating mood in the local database...");
            MoodCursorHelper.createMood(context, currentMood);
            Logger.verbose(TagConstant.ENTER_MOOD, "Mood has been created in the local database.");

            ToastUtil.showMoodUpdatedText(context);

            setDefaultMoodOnButtons(context, remoteViews);

        } else if (intent.getAction() != null && intent.getAction().startsWith(ACTION_MOOD_CLICKED_PREFIX)) {

            int buttonIndex = getButtonIndexFromAction(context, intent.getAction());
            Logger.verbose(TagConstant.ENTER_MOOD, "ButtonIndex: " + buttonIndex);

            currentMoodInt = getMoodFromButtonIndex(buttonIndex);
            Logger.verbose(TagConstant.ENTER_MOOD, "Set current mood to " + currentMoodInt);

            setCurrentMoodOnButtons(remoteViews, buttonIndex);

        } else if (ACTION_SHARE_CLICKED.equals(intent.getAction())) {

            final Cursor moodCursor = MoodCursorHelper.getAllRawMoodsCursor(context);

            if(moodCursor.moveToLast()) {

                final Mood mostRecentMood = MoodTableHelper.fromCursor(moodCursor);

                ShareUtil.shareMood(context, mostRecentMood);

                Logger.verbose(TagConstant.ENTER_MOOD, "Sharing: " + mostRecentMood.toString());

            } else {

                Logger.info(TagConstant.ENTER_MOOD, "No mood yet!");
                ToastUtil.showNoMoodsYetText(context);
            }
        }

        AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, remoteViews);
    }

    /**
     * @param context the context
     * @return the current mood or the default mood if not set
     */
    private static int getCurrentMood(Context context) {

        if(currentMoodInt < 0)
            return context.getResources().getInteger(R.integer.default_mood);
        else
            return currentMoodInt;
    }

    private static int getMoodFromButtonIndex(int buttonIndex) {

        return buttonIndex + 1;
    }
    /**
     * @param action the action
     * @return the button index according to the action.
     */
    private static int getButtonIndexFromAction(Context context, String action) {

        try {

            return Integer.valueOf(action.substring(ACTION_MOOD_CLICKED_PREFIX.lastIndexOf(".")+1));

        } catch (NumberFormatException e) {

            Logger.error(TagConstant.ENTER_MOOD, "Failed to get button index, using default!", e);

            return context.getResources().getInteger(R.integer.default_mood);
        }
    }

    /**
     * @param context the context
     * @param action the action
     * @param extras the extras
     * @return an intent which calls {@link at.ameise.moodtracker.app.widget.EnterMoodWidget#onReceive(android.content.Context, android.content.Intent)}.
     */
    private static PendingIntent getPendingSelfIntent(Context context, String action, Bundle extras) {

        final Intent intent = new Intent(context, EnterMoodWidget.class);
        intent.setAction(action);
        if(extras != null)
            intent.putExtras(extras);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /**
     * Updates the state of the buttons to the default mood.
     * @param remoteViews the views
     */
    private static void setDefaultMoodOnButtons(Context context, RemoteViews remoteViews) {

        final int buttonIndex = context.getResources().getInteger(R.integer.default_mood) - 1;

        setCurrentMoodOnButtons(remoteViews, buttonIndex);
    }

    /**
     * Updates the state of the buttons according to the current mood.
     * @param remoteViews the views
     * @param buttonIndex the button index
     */
    private static void setCurrentMoodOnButtons(RemoteViews remoteViews, int buttonIndex) {

        for(int i = 0; i < MOOD_BUTTONS.length; i++) {

            final int buttonResId = MOOD_BUTTONS[i];

            if(i <= buttonIndex)
                remoteViews.setImageViewResource(buttonResId, R.drawable.glyphicons_13_heart);
            else
                remoteViews.setImageViewResource(buttonResId, R.drawable.glyphicons_20_heart_empty);
        }
    }

}


