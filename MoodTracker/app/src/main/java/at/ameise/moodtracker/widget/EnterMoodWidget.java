package at.ameise.moodtracker.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Calendar;

import at.ameise.moodtracker.ISetting;
import at.ameise.moodtracker.ITag;
import at.ameise.moodtracker.R;
import at.ameise.moodtracker.domain.Mood;
import at.ameise.moodtracker.domain.MoodCursorHelper;

/**
 * Implementation of App Widget functionality.
 */
public class EnterMoodWidget extends AppWidgetProvider {

    private static final String ACTION_UPDATE_CLICKED = "at.ameise.moodtracker.action.updateButtonClick";
    private static final String ACTION_MOOD_CLICKED_PREFIX = "at.ameise.moodtracker.action.moodButtonClick.";

    private static int currentMoodInt = ISetting.DEFAULT_MOOD;

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
        remoteViews.setOnClickPendingIntent(R.id.ibCurrentMoodWidget0, getPendingSelfIntent(context, getMoodButtonAction(0), null));
        remoteViews.setOnClickPendingIntent(R.id.ibCurrentMoodWidget1, getPendingSelfIntent(context, getMoodButtonAction(1), null));
        remoteViews.setOnClickPendingIntent(R.id.ibCurrentMoodWidget2, getPendingSelfIntent(context, getMoodButtonAction(2), null));
        remoteViews.setOnClickPendingIntent(R.id.ibCurrentMoodWidget3, getPendingSelfIntent(context, getMoodButtonAction(3), null));
        remoteViews.setOnClickPendingIntent(R.id.ibCurrentMoodWidget4, getPendingSelfIntent(context, getMoodButtonAction(4), null));
        remoteViews.setOnClickPendingIntent(R.id.ibCurrentMoodWidget5, getPendingSelfIntent(context, getMoodButtonAction(5), null));
        remoteViews.setOnClickPendingIntent(R.id.ibCurrentMoodWidget6, getPendingSelfIntent(context, getMoodButtonAction(6), null));
        remoteViews.setOnClickPendingIntent(R.id.ibCurrentMoodWidget7, getPendingSelfIntent(context, getMoodButtonAction(7), null));
        remoteViews.setOnClickPendingIntent(R.id.ibCurrentMoodWidget8, getPendingSelfIntent(context, getMoodButtonAction(8), null));
        remoteViews.setOnClickPendingIntent(R.id.ibCurrentMoodWidget9, getPendingSelfIntent(context, getMoodButtonAction(9), null));

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

        Log.d(ITag.TAG_ENTER_MOOD, "Intent: "+intent);
        Log.d(ITag.TAG_ENTER_MOOD, "Extras: "+intent.getExtras());

        if (ACTION_UPDATE_CLICKED.equals(intent.getAction())) {

            final Mood currentMood = new Mood();
            currentMood.setDate(Calendar.getInstance());
            currentMood.setMood(currentMoodInt);
            MoodCursorHelper.createMood(context, currentMood);

            Log.v(ITag.TAG_ENTER_MOOD, "Created mood: " + currentMood.toString());
            Toast.makeText(context, "Updated mood", Toast.LENGTH_LONG).show();

            setDefaultMoodOnButtons(remoteViews);

        } else if (intent.getAction() != null && intent.getAction().startsWith(ACTION_MOOD_CLICKED_PREFIX)) {

            int buttonIndex = getButtonIndexFromAction(intent.getAction());
            Log.v(ITag.TAG_ENTER_MOOD, "ButtonIndex: " + buttonIndex);

            currentMoodInt = getMoodFromButtonIndex(buttonIndex);
            Log.v(ITag.TAG_ENTER_MOOD, "Set current mood to " + currentMoodInt);

            setCurrentMoodOnButtons(remoteViews, buttonIndex);
        }

        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    private static final int getMoodFromButtonIndex(int buttonIndex) {

        return buttonIndex + 1;
    }
    /**
     * @param action
     * @return the button index according to the action.
     */
    private static int getButtonIndexFromAction(String action) {

        try {

            return Integer.valueOf(action.substring(ACTION_MOOD_CLICKED_PREFIX.lastIndexOf(".")+1));

        } catch (NumberFormatException e) {

            Log.e(ITag.TAG_ENTER_MOOD, "Failed to get button index, using default!", e);

            return ISetting.DEFAULT_MOOD;
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
    private static final void setDefaultMoodOnButtons(RemoteViews remoteViews) {

        remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget0, R.drawable.glyphicons_13_heart);
        remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget1, R.drawable.glyphicons_13_heart);
        remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget2, R.drawable.glyphicons_13_heart);
        remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget3, R.drawable.glyphicons_13_heart);
        remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget4, R.drawable.glyphicons_13_heart);
        remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget5, R.drawable.glyphicons_13_heart);
        remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget6, R.drawable.glyphicons_20_heart_empty);
        remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget7, R.drawable.glyphicons_20_heart_empty);
        remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget8, R.drawable.glyphicons_20_heart_empty);
        remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget9, R.drawable.glyphicons_20_heart_empty);
    }

    /**
     * Updates the state of the buttons according to the current mood.
     * @param remoteViews
     * @param buttonIndex
     */
    private static final void setCurrentMoodOnButtons(RemoteViews remoteViews, int buttonIndex) {

        switch (buttonIndex) {
            case 0:
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget0, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget1, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget2, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget3, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget4, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget5, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget6, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget7, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget8, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget9, R.drawable.glyphicons_20_heart_empty);
                break;
            case 1:
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget0, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget1, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget2, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget3, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget4, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget5, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget6, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget7, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget8, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget9, R.drawable.glyphicons_20_heart_empty);
                break;
            case 2:
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget0, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget1, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget2, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget3, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget4, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget5, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget6, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget7, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget8, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget9, R.drawable.glyphicons_20_heart_empty);
                break;
            case 3:
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget0, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget1, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget2, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget3, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget4, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget5, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget6, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget7, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget8, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget9, R.drawable.glyphicons_20_heart_empty);
                break;
            case 4:
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget0, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget1, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget2, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget3, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget4, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget5, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget6, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget7, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget8, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget9, R.drawable.glyphicons_20_heart_empty);
                break;
            case 5:
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget0, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget1, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget2, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget3, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget4, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget5, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget6, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget7, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget8, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget9, R.drawable.glyphicons_20_heart_empty);
                break;
            case 6:
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget0, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget1, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget2, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget3, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget4, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget5, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget6, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget7, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget8, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget9, R.drawable.glyphicons_20_heart_empty);
                break;
            case 7:
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget0, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget1, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget2, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget3, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget4, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget5, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget6, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget7, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget8, R.drawable.glyphicons_20_heart_empty);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget9, R.drawable.glyphicons_20_heart_empty);
                break;
            case 8:
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget0, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget1, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget2, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget3, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget4, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget5, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget6, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget7, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget8, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget9, R.drawable.glyphicons_20_heart_empty);
                break;
            case 9:
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget0, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget1, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget2, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget3, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget4, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget5, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget6, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget7, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget8, R.drawable.glyphicons_13_heart);
                remoteViews.setImageViewResource(R.id.ibCurrentMoodWidget9, R.drawable.glyphicons_13_heart);
                break;
        }
    }
}


