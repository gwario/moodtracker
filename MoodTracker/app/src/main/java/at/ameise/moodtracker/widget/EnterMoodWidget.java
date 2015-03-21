package at.ameise.moodtracker.widget;

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

import java.util.Calendar;

import at.ameise.moodtracker.ITag;
import at.ameise.moodtracker.R;
import at.ameise.moodtracker.domain.Mood;
import at.ameise.moodtracker.domain.MoodCursorHelper;
import at.ameise.moodtracker.domain.MoodTableHelper;
import at.ameise.moodtracker.util.Logger;
import at.ameise.moodtracker.util.ShareUtil;

/**
 * Implementation of App Widget functionality.
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


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
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
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     */
    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.enter_mood_widget);
        final ComponentName watchWidget = new ComponentName(context, EnterMoodWidget.class);

        remoteViews.setOnClickPendingIntent(R.id.ibUpdateCurrentMoodWidget, getPendingSelfIntent(context, ACTION_UPDATE_CLICKED, null));
        remoteViews.setOnClickPendingIntent(R.id.ibShareCurrentMoodWidget, getPendingSelfIntent(context, ACTION_SHARE_CLICKED, null));

        for(int i = 0; i < MOOD_BUTTONS.length; i++)
            remoteViews.setOnClickPendingIntent(MOOD_BUTTONS[i], getPendingSelfIntent(context, getMoodButtonAction(i), null));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    /**
     * @param buttonIndex
     * @return the action of the the button.
     */
    private static String getMoodButtonAction(int buttonIndex) {

        return ACTION_MOOD_CLICKED_PREFIX + buttonIndex;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.enter_mood_widget);
        final ComponentName thisWidget = new ComponentName(context, EnterMoodWidget.class);

        Logger.debug(ITag.ENTER_MOOD, "Intent: " + intent);
        Logger.debug(ITag.ENTER_MOOD, "Extras: " + intent.getExtras());

        if (ACTION_UPDATE_CLICKED.equals(intent.getAction())) {

            final Mood currentMood = new Mood();
            currentMood.setDate(Calendar.getInstance());
            currentMood.setMood(getCurrentMood(context));
            MoodCursorHelper.createMood(context, currentMood);

            Logger.verbose(ITag.ENTER_MOOD, "Created: " + currentMood.toString());
            Toast.makeText(context, "Updated mood", Toast.LENGTH_LONG).show();

            setDefaultMoodOnButtons(context, remoteViews);

        } else if (intent.getAction() != null && intent.getAction().startsWith(ACTION_MOOD_CLICKED_PREFIX)) {

            int buttonIndex = getButtonIndexFromAction(context, intent.getAction());
            Logger.verbose(ITag.ENTER_MOOD, "ButtonIndex: " + buttonIndex);

            currentMoodInt = getMoodFromButtonIndex(buttonIndex);
            Logger.verbose(ITag.ENTER_MOOD, "Set current mood to " + currentMoodInt);

            setCurrentMoodOnButtons(remoteViews, buttonIndex);

        } else if (ACTION_SHARE_CLICKED.equals(intent.getAction())) {

            final Cursor moodCursor = MoodCursorHelper.getAllMoodsCursor(context);

            if(moodCursor.moveToLast()) {

                final Mood mostRecentMood = MoodTableHelper.fromCursor(moodCursor);

                ShareUtil.shareMood(context, mostRecentMood);

                Logger.verbose(ITag.ENTER_MOOD, "Sharing: " + mostRecentMood.toString());

            } else {

                Logger.info(ITag.ENTER_MOOD, "No mood yet!");
                Toast.makeText(context, R.string.message_no_moods_yet, Toast.LENGTH_SHORT).show();
            }
        }

        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    /**
     * @param context
     * @return the current mood or the default mood if not set
     */
    private static int getCurrentMood(Context context) {

        if(currentMoodInt < 0)
            return context.getResources().getInteger(R.integer.default_mood);
        else
            return currentMoodInt;
    }

    private static final int getMoodFromButtonIndex(int buttonIndex) {

        return buttonIndex + 1;
    }
    /**
     * @param action
     * @return the button index according to the action.
     */
    private static int getButtonIndexFromAction(Context context, String action) {

        try {

            return Integer.valueOf(action.substring(ACTION_MOOD_CLICKED_PREFIX.lastIndexOf(".")+1));

        } catch (NumberFormatException e) {

            Logger.error(ITag.ENTER_MOOD, "Failed to get button index, using default!", e);

            return context.getResources().getInteger(R.integer.default_mood);
        }
    }

    /**
     * @param context
     * @param action
     * @param extras
     * @return an intent which calls {@link at.ameise.moodtracker.widget.EnterMoodWidget#onReceive(android.content.Context, android.content.Intent)}.
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
     * @param remoteViews
     */
    private static final void setDefaultMoodOnButtons(Context context, RemoteViews remoteViews) {

        final int buttonIndex = context.getResources().getInteger(R.integer.default_mood) - 1;

        setCurrentMoodOnButtons(remoteViews, buttonIndex);
    }

    /**
     * Updates the state of the buttons according to the current mood.
     * @param remoteViews
     * @param buttonIndex
     */
    private static final void setCurrentMoodOnButtons(RemoteViews remoteViews, int buttonIndex) {

        for(int i = 0; i < MOOD_BUTTONS.length; i++) {

            final int buttonResId = MOOD_BUTTONS[i];

            if(i <= buttonIndex)
                remoteViews.setImageViewResource(buttonResId, R.drawable.glyphicons_13_heart);
            else
                remoteViews.setImageViewResource(buttonResId, R.drawable.glyphicons_20_heart_empty);
        }
    }

}


