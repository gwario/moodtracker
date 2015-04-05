package at.ameise.moodtracker.domain;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Contains helper methods to operate on the mood database.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 14.02.15.
 */
public final class MoodCursorHelper {

    /**
     * @param context   the context
     * @return a {@link android.database.Cursor} on all {@link at.ameise.moodtracker.domain.Mood}s with {@link Mood#getScope()} = {@link at.ameise.moodtracker.domain.MoodTableHelper.EMoodScope#RAW}. Uses {@link at.ameise.moodtracker.domain.MoodTableHelper#SORT_ORDER_TIMESTAMP_ASC}.
     */
    public static Cursor getAllRawMoodsCursor(Context context) {

        return context.getContentResolver().query(MoodContentProvider.CONTENT_URI_MOOD,
                MoodTableHelper.ALL_COLUMNS,
                MoodTableHelper.COL_SCOPE+" = *",
                new String[] { MoodTableHelper.EMoodScope.RAW.getColumnValue(), },
                MoodTableHelper.SORT_ORDER_TIMESTAMP_ASC);
    }

    /**
     * @param context   the context
     * @return a {@link android.content.CursorLoader} on the quarter-day-average of all {@link at.ameise.moodtracker.domain.Mood}s. Uses {@link at.ameise.moodtracker.domain.MoodTableHelper#SORT_ORDER_TIMESTAMP_ASC}.
     */
    public static Loader<Cursor> getAllMoodsAvgQuarterDayCursorLoader(Context context) {

        return new CursorLoader(context, MoodContentProvider.CONTENT_URI_MOOD_AVG_QUARTER_DAY, MoodTableHelper.ALL_COLUMNS, null, null, MoodTableHelper.SORT_ORDER_TIMESTAMP_ASC);
    }

    /**
     * @param context   the context
     * @return a {@link android.content.CursorLoader} on the day-average of all {@link at.ameise.moodtracker.domain.Mood}s. Uses {@link at.ameise.moodtracker.domain.MoodTableHelper#SORT_ORDER_TIMESTAMP_ASC}.
     */
    public static Loader<Cursor> getAllMoodsAvgDayCursorLoader(Context context) {

        return new CursorLoader(context, MoodContentProvider.CONTENT_URI_MOOD_AVG_DAY, MoodTableHelper.ALL_COLUMNS, null, null, MoodTableHelper.SORT_ORDER_TIMESTAMP_ASC);
    }

    /**
     * @param context   the context
     * @return a {@link android.content.CursorLoader} on the week-average of all {@link at.ameise.moodtracker.domain.Mood}s. Uses {@link at.ameise.moodtracker.domain.MoodTableHelper#SORT_ORDER_TIMESTAMP_ASC}.
     */
    public static Loader<Cursor> getAllMoodsAvgWeekCursorLoader(Context context) {

        return new CursorLoader(context, MoodContentProvider.CONTENT_URI_MOOD_AVG_WEEK, MoodTableHelper.ALL_COLUMNS, null, null, MoodTableHelper.SORT_ORDER_TIMESTAMP_ASC);
    }

    /**
     * @param context   the context
     * @return a {@link android.content.CursorLoader} on the month-average of all {@link at.ameise.moodtracker.domain.Mood}s. Uses {@link at.ameise.moodtracker.domain.MoodTableHelper#SORT_ORDER_TIMESTAMP_ASC}.
     */
    public static Loader<Cursor> getAllMoodsAvgMonthCursorLoader(Context context) {

        return new CursorLoader(context, MoodContentProvider.CONTENT_URI_MOOD_AVG_MONTH, MoodTableHelper.ALL_COLUMNS, null, null, MoodTableHelper.SORT_ORDER_TIMESTAMP_ASC);
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
     * Removes all {@link at.ameise.moodtracker.domain.Mood}s from the database.
     *
     * @param context   the context
     */
    public static void removeAllMoods(Context context) {

        context.getContentResolver().delete(MoodContentProvider.CONTENT_URI_MOOD, null, null);
    }

    /**
     * @param database  the database
     * @return a {@link android.database.Cursor} containing all the timestamps of raw mood values in ascending order. Uses {@link at.ameise.moodtracker.domain.MoodTableHelper#SORT_ORDER_TIMESTAMP_ASC}.
     */
    static Cursor getAllRawValueTimestamps(SQLiteDatabase database) {

        return database.query(MoodTableHelper.TABLE_NAME,
                new String[]{ MoodTableHelper.COL_TIMESTAMP, MoodTableHelper.COL_SCOPE, },
                MoodTableHelper.COL_SCOPE+" = ?",
                new String[] { MoodTableHelper.EMoodScope.RAW.getColumnValue(), },
                null, null, MoodTableHelper.SORT_ORDER_TIMESTAMP_ASC);
    }

    /**
     * @param context  the context
     * @return a {@link android.database.Cursor} containing all the timestamps of raw mood values in ascending order. Uses {@link at.ameise.moodtracker.domain.MoodTableHelper#SORT_ORDER_TIMESTAMP_ASC}.
     */
    static Cursor getAllRawValueTimestamps(Context context) {

        //we use moods, since we want to project only timestamps and scope
        return context.getContentResolver().query(MoodContentProvider.CONTENT_URI_MOOD,
                new String[]{MoodTableHelper.COL_TIMESTAMP, MoodTableHelper.COL_SCOPE,},
                MoodTableHelper.COL_SCOPE + " = ?",
                new String[]{MoodTableHelper.EMoodScope.RAW.getColumnValue(),},
                MoodTableHelper.SORT_ORDER_TIMESTAMP_ASC);
    }

    /**
     * @param context  the context
     * @return a {@link android.database.Cursor} containing all the timestamps of quarter daily average values in ascending order. Uses {@link at.ameise.moodtracker.domain.MoodTableHelper#SORT_ORDER_TIMESTAMP_ASC}.
     */
    static Cursor getAllQuarterDailyValueTimestamps(Context context) {

        //we use moods, since we want to project only timestamps and scope
        return context.getContentResolver().query(MoodContentProvider.CONTENT_URI_MOOD,
                new String[]{ MoodTableHelper.COL_TIMESTAMP, MoodTableHelper.COL_SCOPE, },
                MoodTableHelper.COL_SCOPE+" = ?",
                new String[] { MoodTableHelper.EMoodScope.QUARTER_DAY.getColumnValue(), },
                MoodTableHelper.SORT_ORDER_TIMESTAMP_ASC);
    }

    /**
     * @param context  the context
     * @return a {@link android.database.Cursor} containing all the timestamps of daily average values in ascending order. Uses {@link at.ameise.moodtracker.domain.MoodTableHelper#SORT_ORDER_TIMESTAMP_ASC}.
     */
    static Cursor getAllDailyValueTimestamps(Context context) {

        //we use moods, since we want to project only timestamps and scope
        return context.getContentResolver().query(MoodContentProvider.CONTENT_URI_MOOD,
                new String[]{ MoodTableHelper.COL_TIMESTAMP, MoodTableHelper.COL_SCOPE, },
                MoodTableHelper.COL_SCOPE+" = ?",
                new String[] { MoodTableHelper.EMoodScope.DAY.getColumnValue(), },
                MoodTableHelper.SORT_ORDER_TIMESTAMP_ASC);
    }

    /**
     * @param context  the context
     * @return a {@link android.database.Cursor} containing all the timestamps of weekly average values in ascending order. Uses {@link at.ameise.moodtracker.domain.MoodTableHelper#SORT_ORDER_TIMESTAMP_ASC}.
     */
    static Cursor getAllWeeklyValueTimestamps(Context context) {

        //we use moods, since we want to project only timestamps and scope
        return context.getContentResolver().query(MoodContentProvider.CONTENT_URI_MOOD,
                new String[]{ MoodTableHelper.COL_TIMESTAMP, MoodTableHelper.COL_SCOPE, },
                MoodTableHelper.COL_SCOPE+" = ?",
                new String[] { MoodTableHelper.EMoodScope.WEEK.getColumnValue(), },
                MoodTableHelper.SORT_ORDER_TIMESTAMP_ASC);
    }

    /**
     * @param context  the context
     * @return a {@link android.database.Cursor} containing all the timestamps of monthly average values in ascending order. Uses {@link at.ameise.moodtracker.domain.MoodTableHelper#SORT_ORDER_TIMESTAMP_ASC}.
     */
    static Cursor getAllMonthlyValueTimestamps(Context context) {

        //we use moods, since we want to project only timestamps and scope
        return context.getContentResolver().query(MoodContentProvider.CONTENT_URI_MOOD,
                new String[]{ MoodTableHelper.COL_TIMESTAMP, MoodTableHelper.COL_SCOPE, },
                MoodTableHelper.COL_SCOPE+" = ?",
                new String[] { MoodTableHelper.EMoodScope.MONTH.getColumnValue(), },
                MoodTableHelper.SORT_ORDER_TIMESTAMP_ASC);
    }

    /**
     * @param context   the context
     * @return a {@link android.database.Cursor} containing all quarter daily average moods sorted by timestamp in ascending order. Uses {@link at.ameise.moodtracker.domain.MoodTableHelper#SORT_ORDER_TIMESTAMP_ASC}.
     */
    public static Cursor getQuarterDailyMoodsCursor(Context context, String[] projection) {

        if(projection == null)
            projection = MoodTableHelper.ALL_COLUMNS;

        return context.getContentResolver().query(MoodContentProvider.CONTENT_URI_MOOD,
                projection,
                MoodTableHelper.COL_SCOPE + " = ?",
                new String[] { MoodTableHelper.EMoodScope.QUARTER_DAY.getColumnValue(), },
                MoodTableHelper.SORT_ORDER_TIMESTAMP_ASC);
    }
}
