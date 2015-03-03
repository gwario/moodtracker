package at.ameise.moodtracker.domain;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import java.util.Calendar;

import at.ameise.moodtracker.domain.Mood;
import at.ameise.moodtracker.domain.MoodContentProvider;
import at.ameise.moodtracker.domain.MoodTableHelper;

/**
 * Contains helper methods to operate on the mood database.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 14.02.15.
 */
public final class MoodCursorHelper {

    /**
     * @param context   the context
     * @return a {@link CursorLoader} on all {@link at.ameise.moodtracker.domain.Mood}s.
     */
    public static Loader<Cursor> getAllMoodsCursorLoader(Context context) {

        return new CursorLoader(context, MoodContentProvider.CONTENT_URI_MOOD, null, null, null, MoodTableHelper.SORT_ORDER_TIMESTAMP_ASC);
    }

    /**
     * Creates the given {@link at.ameise.moodtracker.domain.Mood} in the database.<br>
     * <br>
     * Note: This method does not check if the course does already exist!
     *
     * @param context   the context
     * @param mood      the mood to be saved
     */
    public static void createMood(Context context, Mood mood) {

        final ContentValues values = MoodTableHelper.fromMood(mood);

        final Uri returnUri = context.getContentResolver().insert(MoodContentProvider.CONTENT_URI_MOOD, values);
        final long id = Long.parseLong(returnUri.getLastPathSegment());
        mood.setId(id);
    }

    /**
     * @param context   the context
     * @param moodId    id of the mood to be loaded
     * @return a {@link CursorLoader} on the {@link at.ameise.moodtracker.domain.Mood} with the specified id.
     */
    static Loader<Cursor> getMoodCursorLoader(Context context, long moodId) {

        return new CursorLoader(context, MoodContentProvider.getCONTENT_URI_MOOD_ID(moodId), null, null, null, null);
    }

    /**
     * @param context   the context
     * @param timestamp timestamp of the mood to be loaded
     * @return a {@link CursorLoader} on the {@link at.ameise.moodtracker.domain.Mood} with the specified timestamp.
     */
    public static Loader<Cursor> getMoodCursorLoader(Context context, Calendar timestamp) {

        return new CursorLoader(context, MoodContentProvider.getCONTENT_URI_MOOD_DATE(timestamp), null, null, null, null);
    }

    /**
     * @param context   the context
     * @param fromTimestamp inclusive start of the time range
     * @param toTimestamp   inclusive end of the time range
     * @return a {@link CursorLoader} on the {@link at.ameise.moodtracker.domain.Mood}s with in the range.
     */
    public static Loader<Cursor> getMoodCursorLoader(Context context, Calendar fromTimestamp, Calendar toTimestamp) {

        return new CursorLoader(context, MoodContentProvider.getCONTENT_URI_MOOD_DATE_RANGE(fromTimestamp, toTimestamp), null, null, null, MoodTableHelper.SORT_ORDER_TIMESTAMP_ASC);
    }

    /**
     * Removes all {@link at.ameise.moodtracker.domain.Mood}s from the database.
     *
     * @param context   the context
     */
    public static void removeAllMoods(Context context) {

        context.getContentResolver().delete(MoodContentProvider.CONTENT_URI_MOOD, null, null);
    }

    /**
     * Removes the {@link at.ameise.moodtracker.domain.Mood} with the specified id.
     *
     * @param context   the context
     */
    public static void removeMood(Context context, long id) {

        context.getContentResolver().delete(MoodContentProvider.getCONTENT_URI_MOOD_ID(id), null, null);
    }

    /**
     * Removes the {@link at.ameise.moodtracker.domain.Mood} with the specified timestamp.
     *
     * @param context   the context
     */
    public static void removeMood(Context context, Calendar timestamp) {

        context.getContentResolver().delete(MoodContentProvider.getCONTENT_URI_MOOD_DATE(timestamp), null, null);
    }

    /**
     * Removes the {@link at.ameise.moodtracker.domain.Mood} within the specified range.
     *
     * @param context   the context
     */
    public static void removeMood(Context context, Calendar fromTimestamp, Calendar toTimestamp) {

        context.getContentResolver().delete(MoodContentProvider.getCONTENT_URI_MOOD_DATE_RANGE(fromTimestamp, toTimestamp), null, null);
    }

    /**
     * @param cursor    cursor pointing to a row
     * @return the {@link at.ameise.moodtracker.domain.Mood} at the current cursor position.
     */
    public static Mood fromCursor(Cursor cursor) {

        final Mood mood = new Mood();

        mood.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_ID)));
        mood.setMood(cursor.getInt(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_MOOD)));
        Calendar timestamp = Calendar.getInstance();
        timestamp.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_TIMESTAMP)));
        mood.setDate(timestamp);

        return mood;
    }
}