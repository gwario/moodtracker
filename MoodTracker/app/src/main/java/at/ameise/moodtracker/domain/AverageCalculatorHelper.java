package at.ameise.moodtracker.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import at.ameise.moodtracker.ISetting;
import at.ameise.moodtracker.ITag;
import at.ameise.moodtracker.util.CalendarUtil;
import at.ameise.moodtracker.util.Logger;

/**
 * Contains the methods to calculate average values. Operates on the data provider level.
 *
 * Created by Mario Gastegger <mgastegger AT buzzmark DOT com> on 05.04.15.
 */
public class AverageCalculatorHelper {

    /**
     * Calculates the latest monthly average values on the database.
     * Uses {@link at.ameise.moodtracker.domain.MoodTableHelper.EMoodScope#QUARTER_DAY} as source values.
     * @param ctx the context
     */
    public static void calculateMonthlyAverage(Context ctx) {

        final List<ContentValues> averageMoods = new ArrayList<>();

        final Cursor dailyTimestampCursor = MoodCursorHelper.getAllMonthlyValueTimestamps(ctx);
        final Cursor rawTimestampCursor = MoodCursorHelper.getAllRawValueTimestamps(ctx);

        final Calendar maxDate = Calendar.getInstance();

        Calendar startDate = getLastDate(dailyTimestampCursor);
        if(startDate != null) {

            Logger.verbose(ITag.AVERAGE_CALCULATOR, "Most recent average mood was from: " + ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime()));
            CalendarUtil.setStartOfNextMonth(startDate);

        } else {

            Logger.warn(ITag.AVERAGE_CALCULATOR, "No average moods available in this scope!");
            Logger.verbose(ITag.AVERAGE_CALCULATOR, "Trying to get start date from raw moods...");

            startDate = getFirstDate(rawTimestampCursor);
            if(startDate != null) {

                Logger.verbose(ITag.AVERAGE_CALCULATOR, "First raw mood was from: " + ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime()));
                CalendarUtil.setStartOfMonth(startDate);
            }
        }

        closeCursor(dailyTimestampCursor);
        closeCursor(rawTimestampCursor);

        if(maxDate != null && startDate != null) {

            Logger.debug(ITag.AVERAGE_CALCULATOR, "Start date for calculations: " + ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime()));
            Logger.debug(ITag.AVERAGE_CALCULATOR, "End date for calculations: " + ISetting.DEBUG_DATE_FORMAT.format(maxDate.getTime()));

            //get the first end date from the start date
            Calendar endDate = Calendar.getInstance();
            endDate.setTimeInMillis(startDate.getTimeInMillis());
            CalendarUtil.setEndOfMonth(endDate);

            Mood mood;
            Float averageMood;

            while(endDate.before(maxDate)) {

                Logger.verbose(ITag.AVERAGE_CALCULATOR, "Next avg between: "+ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime())+" and "+ISetting.DEBUG_DATE_FORMAT.format(endDate.getTime()));

                //get the avg value
                averageMood = calculateAverageBetween(ctx, startDate, endDate, MoodTableHelper.EMoodScope.QUARTER_DAY);
                if(averageMood != null) {

                    mood = new Mood();
                    mood.setScope(MoodTableHelper.EMoodScope.MONTH);
                    mood.setDate(startDate);
                    mood.setMood(averageMood);

                    //add mood to bulk
                    averageMoods.add(MoodTableHelper.fromMood(mood));

                } else {

                    Logger.verbose(ITag.AVERAGE_CALCULATOR, "No raw moods in the current time window.");
                }


                //update start and end timestamp
                CalendarUtil.setStartOfNextMonth(startDate);
                CalendarUtil.setEndOfNextMonth(endDate);
            }

            ctx.getContentResolver().bulkInsert(MoodContentProvider.CONTENT_URI_MOOD, averageMoods.toArray(new ContentValues[averageMoods.size()]));
        }
    }

    /**
     * Calculates the latest weekly average values on the database.
     * Uses {@link at.ameise.moodtracker.domain.MoodTableHelper.EMoodScope#QUARTER_DAY} as source values.
     * @param ctx the context
     */
    public static void calculateWeeklyAverage(Context ctx) {

        final List<ContentValues> averageMoods = new ArrayList<>();

        final Cursor dailyTimestampCursor = MoodCursorHelper.getAllWeeklyValueTimestamps(ctx);
        final Cursor rawTimestampCursor = MoodCursorHelper.getAllRawValueTimestamps(ctx);

        final Calendar maxDate = Calendar.getInstance();

        Calendar startDate = getLastDate(dailyTimestampCursor);
        if(startDate != null) {

            Logger.verbose(ITag.AVERAGE_CALCULATOR, "Most recent average mood was from: " + ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime()));
            CalendarUtil.setStartOfNextWeek(startDate);

        } else {

            Logger.warn(ITag.AVERAGE_CALCULATOR, "No average moods available in this scope!");
            Logger.verbose(ITag.AVERAGE_CALCULATOR, "Trying to get start date from raw moods...");

            startDate = getFirstDate(rawTimestampCursor);
            if(startDate != null) {

                Logger.verbose(ITag.AVERAGE_CALCULATOR, "First raw mood was from: " + ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime()));
                CalendarUtil.setStartOfWeek(startDate);
            }
        }

        closeCursor(dailyTimestampCursor);
        closeCursor(rawTimestampCursor);

        if(maxDate != null && startDate != null) {

            Logger.debug(ITag.AVERAGE_CALCULATOR, "Start date for calculations: " + ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime()));
            Logger.debug(ITag.AVERAGE_CALCULATOR, "End date for calculations: " + ISetting.DEBUG_DATE_FORMAT.format(maxDate.getTime()));

            //get the first end date from the start date
            Calendar endDate = Calendar.getInstance();
            endDate.setTimeInMillis(startDate.getTimeInMillis());
            CalendarUtil.setEndOfWeek(endDate);

            Mood mood;
            Float averageMood;

            while(endDate.before(maxDate)) {

                Logger.verbose(ITag.AVERAGE_CALCULATOR, "Next avg between: "+ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime())+" and "+ISetting.DEBUG_DATE_FORMAT.format(endDate.getTime()));

                //get the avg value
                averageMood = calculateAverageBetween(ctx, startDate, endDate, MoodTableHelper.EMoodScope.QUARTER_DAY);
                if(averageMood != null) {

                    mood = new Mood();
                    mood.setScope(MoodTableHelper.EMoodScope.WEEK);
                    mood.setDate(startDate);
                    mood.setMood(averageMood);

                    //add mood to bulk
                    averageMoods.add(MoodTableHelper.fromMood(mood));

                } else {

                    Logger.verbose(ITag.AVERAGE_CALCULATOR, "No raw moods in the current time window.");
                }


                //update start and end timestamp
                CalendarUtil.setStartOfNextWeek(startDate);
                CalendarUtil.setEndOfNextWeek(endDate);
            }

            ctx.getContentResolver().bulkInsert(MoodContentProvider.CONTENT_URI_MOOD, averageMoods.toArray(new ContentValues[averageMoods.size()]));
        }
    }

    /**
     * Calculates the latest daily average values on the database.
     * Uses {@link at.ameise.moodtracker.domain.MoodTableHelper.EMoodScope#QUARTER_DAY} as source values.
     * @param ctx the context
     */
    public static void calculateDailyAverage(Context ctx) {

        final List<ContentValues> averageMoods = new ArrayList<>();

        final Cursor dailyTimestampCursor = MoodCursorHelper.getAllDailyValueTimestamps(ctx);
        final Cursor rawTimestampCursor = MoodCursorHelper.getAllRawValueTimestamps(ctx);

        final Calendar maxDate = Calendar.getInstance();

        Calendar startDate = getLastDate(dailyTimestampCursor);
        if(startDate != null) {

            Logger.verbose(ITag.AVERAGE_CALCULATOR, "Most recent average mood was from: " + ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime()));
            CalendarUtil.setStartOfNextDay(startDate);

        } else {

            Logger.warn(ITag.AVERAGE_CALCULATOR, "No average moods available in this scope!");
            Logger.verbose(ITag.AVERAGE_CALCULATOR, "Trying to get start date from raw moods...");

            startDate = getFirstDate(rawTimestampCursor);
            if(startDate != null) {

                Logger.verbose(ITag.AVERAGE_CALCULATOR, "First raw mood was from: " + ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime()));
                CalendarUtil.setStartOfDay(startDate);
            }
        }

        closeCursor(dailyTimestampCursor);
        closeCursor(rawTimestampCursor);

        if(maxDate != null && startDate != null) {

            Logger.debug(ITag.AVERAGE_CALCULATOR, "Start date for calculations: " + ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime()));
            Logger.debug(ITag.AVERAGE_CALCULATOR, "End date for calculations: " + ISetting.DEBUG_DATE_FORMAT.format(maxDate.getTime()));

            //get the first end date from the start date
            Calendar endDate = Calendar.getInstance();
            endDate.setTimeInMillis(startDate.getTimeInMillis());
            CalendarUtil.setEndOfDay(endDate);

            Mood mood;
            Float averageMood;

            while(endDate.before(maxDate)) {

                Logger.verbose(ITag.AVERAGE_CALCULATOR, "Next avg between: "+ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime())+" and "+ISetting.DEBUG_DATE_FORMAT.format(endDate.getTime()));

                //get the avg value
                averageMood = calculateAverageBetween(ctx, startDate, endDate, MoodTableHelper.EMoodScope.QUARTER_DAY);
                if(averageMood != null) {

                    mood = new Mood();
                    mood.setScope(MoodTableHelper.EMoodScope.DAY);
                    mood.setDate(startDate);
                    mood.setMood(averageMood);

                    //add mood to bulk
                    averageMoods.add(MoodTableHelper.fromMood(mood));

                } else {

                    Logger.verbose(ITag.AVERAGE_CALCULATOR, "No raw moods in the current time window.");
                }


                //update start and end timestamp
                CalendarUtil.setStartOfNextDay(startDate);
                CalendarUtil.setEndOfNextDay(endDate);
            }

            ctx.getContentResolver().bulkInsert(MoodContentProvider.CONTENT_URI_MOOD, averageMoods.toArray(new ContentValues[averageMoods.size()]));
        }
    }

    /**
     * Calculates the latest quarter daily average values on the database.
     * Uses {@link at.ameise.moodtracker.domain.MoodTableHelper.EMoodScope#QUARTER_DAY} as source values.
     * @param ctx the context
     */
    public static void calculateQuarterDailyAverage(Context ctx) {

        final List<ContentValues> averageMoods = new ArrayList<>();

        final Cursor quarterDailyTimestampCursor = MoodCursorHelper.getAllQuarterDailyValueTimestamps(ctx);
        final Cursor rawTimestampCursor = MoodCursorHelper.getAllRawValueTimestamps(ctx);

        final Calendar maxDate = Calendar.getInstance();

        Calendar startDate = getLastDate(quarterDailyTimestampCursor);
        if(startDate != null) {

            Logger.verbose(ITag.AVERAGE_CALCULATOR, "Most recent average mood was from: " + ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime()));
            CalendarUtil.setStartOfNextQuarterOfDay(startDate);

        } else {

            Logger.warn(ITag.AVERAGE_CALCULATOR, "No average moods available in this scope!");
            Logger.verbose(ITag.AVERAGE_CALCULATOR, "Trying to get start date from raw moods...");

            startDate = getFirstDate(rawTimestampCursor);
            if(startDate != null) {

                Logger.verbose(ITag.AVERAGE_CALCULATOR, "First raw mood was from: " + ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime()));
                CalendarUtil.setStartOfQuarterOfDay(startDate);
            }
        }

        closeCursor(quarterDailyTimestampCursor);
        closeCursor(rawTimestampCursor);

        if(maxDate != null && startDate != null) {

            Logger.debug(ITag.AVERAGE_CALCULATOR, "Start date for calculations: " + ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime()));
            Logger.debug(ITag.AVERAGE_CALCULATOR, "End date for calculations: " + ISetting.DEBUG_DATE_FORMAT.format(maxDate.getTime()));

            //get the first end date from the start date
            Calendar endDate = Calendar.getInstance();
            endDate.setTimeInMillis(startDate.getTimeInMillis());
            CalendarUtil.setEndOfQuarterOfDay(endDate);

            Mood mood;
            Float averageMood;

            while(endDate.before(maxDate)) {

                Logger.verbose(ITag.AVERAGE_CALCULATOR, "Next avg between: "+ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime())+" and "+ISetting.DEBUG_DATE_FORMAT.format(endDate.getTime()));

                //get the avg value
                averageMood = calculateAverageBetween(ctx, startDate, endDate, MoodTableHelper.EMoodScope.RAW);
                if(averageMood != null) {

                    mood = new Mood();
                    mood.setScope(MoodTableHelper.EMoodScope.QUARTER_DAY);
                    mood.setDate(startDate);
                    mood.setMood(averageMood);

                    //add mood to bulk
                    averageMoods.add(MoodTableHelper.fromMood(mood));

                } else {

                    Logger.verbose(ITag.AVERAGE_CALCULATOR, "No raw moods in the current time window.");
                }

                //update start and end timestamp
                CalendarUtil.setStartOfNextQuarterOfDay(startDate);
                CalendarUtil.setEndOfNextQuarterOfDay(endDate);
            }

            ctx.getContentResolver().bulkInsert(MoodContentProvider.CONTENT_URI_MOOD, averageMoods.toArray(new ContentValues[averageMoods.size()]));
        }
    }

    /**
     * @param ctx the context
     * @param startDate start date
     * @param endDate end date
     * @param scope scope
     * @return the avg of all values between startDate and endDate in the specified scope.
     */
    private static Float calculateAverageBetween(Context ctx, Calendar startDate, Calendar endDate, MoodTableHelper.EMoodScope scope) {

        Float avgValue = null;
        //get the average of the raw vales from the current interval
        final Cursor avgMoodCursor = ctx.getContentResolver().query(MoodContentProvider.getCONTENT_URI_AVG_MOOD_DATE_RANGE_SCOPE(startDate, endDate, scope),
                MoodTableHelper.ALL_COLUMNS,
                null,
                null,
                MoodTableHelper.SORT_ORDER_TIMESTAMP_ASC);

        if(avgMoodCursor != null && avgMoodCursor.moveToFirst()) {

            final int moodColIndex = avgMoodCursor.getColumnIndexOrThrow(MoodTableHelper.COL_MOOD);

            if(!avgMoodCursor.isNull(moodColIndex)) {
                avgValue = avgMoodCursor.getFloat(moodColIndex);
            }

            avgMoodCursor.close();
        }

        if(avgValue != null) {

            Logger.verbose(ITag.AVERAGE_CALCULATOR, "The average mood: " + avgValue);

        } else {

            Logger.warn(ITag.AVERAGE_CALCULATOR, "No value, Failed to calculate average mood between " + ISetting.DEBUG_DATE_FORMAT.format(startDate.getTime()) + " and " + ISetting.DEBUG_DATE_FORMAT.format(endDate.getTime()) + "!");
        }
        return avgValue;
    }

    /**
     * Closed the cursor if it is not null
     * @param cursor the cursor to be closed
     */
    private static void closeCursor(Cursor cursor) {

        if(cursor != null)
            cursor.close();
    }

    /**
     * NOTE: The cursor must contain the column {@link at.ameise.moodtracker.domain.MoodTableHelper#COL_TIMESTAMP}!
     * @param cursor the cursor
     * @return the first date of the {@link android.database.Cursor}. If the cursor is empty, null is returned.
     */
    private static Calendar getFirstDate(Cursor cursor) {

        Calendar firstDate = null;

        if(cursor != null && cursor.moveToFirst()) {

            firstDate = Calendar.getInstance();
            firstDate.setTimeInMillis(1000 * Long.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_TIMESTAMP))));
        }

        return firstDate;
    }

    /**
     * NOTE: The cursor must contain the column {@link at.ameise.moodtracker.domain.MoodTableHelper#COL_TIMESTAMP}!
     * @param cursor the cursor
     * @return the last date of the {@link android.database.Cursor}. If the cursor is empty, null is returned.
     */
    private static Calendar getLastDate(Cursor cursor) {

        Calendar lastDate = null;

        if(cursor != null && cursor.moveToLast()) {

            lastDate = Calendar.getInstance();
            lastDate.setTimeInMillis(1000 * Long.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_TIMESTAMP))));
        }

        return lastDate;
    }

}
