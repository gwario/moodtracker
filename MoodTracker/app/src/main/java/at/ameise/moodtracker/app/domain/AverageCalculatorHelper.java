package at.ameise.moodtracker.app.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import at.ameise.moodtracker.app.Setting;
import at.ameise.moodtracker.app.TagConstant;
import at.ameise.moodtracker.app.util.DateTimeUtil;
import at.ameise.moodtracker.app.util.Logger;

/**
 * Contains the methods to calculate average values. Operates on the data provider level.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 05.04.15.
 */
public class AverageCalculatorHelper {

    /**
     * Calculates the latest monthly average values on the database.
     * Uses {@link at.ameise.moodtracker.app.domain.MoodTableHelper.EMoodScope#QUARTER_DAY} as source values.
     * @param ctx the context
     */
    public static void calculateMonthlyAverage(Context ctx) {

        final List<ContentValues> averageMoods = new ArrayList<>();

        final Cursor dailyTimestampCursor = MoodCursorHelper.getAllMonthlyValueTimestamps(ctx);
        final Cursor rawTimestampCursor = MoodCursorHelper.getAllRawValueTimestamps(ctx);

        final DateTime maxDate = DateTime.now();

        DateTime startDate = getLastDateTime(dailyTimestampCursor);
        if(startDate != null) {

            Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "Most recent average mood was from: " + Setting.DEBUG_DATE_FORMATTER.print(startDate));
            startDate = DateTimeUtil.withStartOfNextMonth(startDate);

        } else {

            Logger.warn(TagConstant.AVERAGE_CALCULATOR, "No average moods available in this scope!");
            Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "Trying to get start date from raw moods...");

            startDate = getFirstDateTime(rawTimestampCursor);
            if(startDate != null) {

                Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "First raw mood was from: " + Setting.DEBUG_DATE_FORMATTER.print(startDate));
                startDate = DateTimeUtil.withStartOfMonth(startDate);
            }
        }

        closeCursor(dailyTimestampCursor);
        closeCursor(rawTimestampCursor);

        if(maxDate != null && startDate != null) {

            Logger.debug(TagConstant.AVERAGE_CALCULATOR, "Start date for calculations: " + Setting.DEBUG_DATE_FORMATTER.print(startDate));
            Logger.debug(TagConstant.AVERAGE_CALCULATOR, "End date for calculations: " + Setting.DEBUG_DATE_FORMATTER.print(maxDate));

            //get the first end date from the start date
            DateTime endDate = DateTimeUtil.withEndOfMonth(startDate);

            Mood mood;
            Float averageMood;

            while(endDate.isBefore(maxDate)) {

                Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "Next avg between: "+ Setting.DEBUG_DATE_FORMATTER.print(startDate)+" and "+ Setting.DEBUG_DATE_FORMATTER.print(endDate));

                //get the avg value
                averageMood = calculateAverageBetween(ctx, startDate, endDate, MoodTableHelper.EMoodScope.QUARTER_DAY);
                if(averageMood != null) {

                    mood = new Mood();
                    mood.setScope(MoodTableHelper.EMoodScope.MONTH);
                    mood.setTimestamp(startDate);
                    mood.setMood(averageMood);

                    //add mood to bulk
                    averageMoods.add(MoodTableHelper.fromMood(mood));

                } else {

                    Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "No raw moods in the current time window.");
                }


                //update start and end timestamp
                startDate = DateTimeUtil.withStartOfNextMonth(startDate);
                endDate = DateTimeUtil.withEndOfNextMonth(endDate);
            }

            ctx.getContentResolver().bulkInsert(MoodContentProvider.CONTENT_URI_MOOD, averageMoods.toArray(new ContentValues[averageMoods.size()]));
        }
    }

    /**
     * Calculates the latest weekly average values on the database.
     * Uses {@link at.ameise.moodtracker.app.domain.MoodTableHelper.EMoodScope#QUARTER_DAY} as source values.
     * @param ctx the context
     */
    public static void calculateWeeklyAverage(Context ctx) {

        final List<ContentValues> averageMoods = new ArrayList<>();

        final Cursor dailyTimestampCursor = MoodCursorHelper.getAllWeeklyValueTimestamps(ctx);
        final Cursor rawTimestampCursor = MoodCursorHelper.getAllRawValueTimestamps(ctx);

        final DateTime maxDate = DateTime.now();

        DateTime startDate = getLastDateTime(dailyTimestampCursor);
        if(startDate != null) {

            Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "Most recent average mood was from: " + Setting.DEBUG_DATE_FORMATTER.print(startDate));
            startDate = DateTimeUtil.withStartOfNextWeek(startDate);

        } else {

            Logger.warn(TagConstant.AVERAGE_CALCULATOR, "No average moods available in this scope!");
            Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "Trying to get start date from raw moods...");

            startDate = getFirstDateTime(rawTimestampCursor);
            if(startDate != null) {

                Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "First raw mood was from: " + Setting.DEBUG_DATE_FORMATTER.print(startDate));
                startDate = DateTimeUtil.withStartOfWeek(startDate);

            } else {

                Logger.info(TagConstant.AVERAGE_CALCULATOR, "No raw moods available. Nothing to calculate!");
            }
        }

        closeCursor(dailyTimestampCursor);
        closeCursor(rawTimestampCursor);

        if(maxDate != null && startDate != null) {

            Logger.debug(TagConstant.AVERAGE_CALCULATOR, "Start date for calculations: " + Setting.DEBUG_DATE_FORMATTER.print(startDate));
            Logger.debug(TagConstant.AVERAGE_CALCULATOR, "End date for calculations: " + Setting.DEBUG_DATE_FORMATTER.print(maxDate));

            //get the first end date from the start date
            DateTime endDate = DateTimeUtil.withEndOfWeek(startDate);

            Mood mood;
            Float averageMood;

            while(endDate.isBefore(maxDate)) {

                Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "Next avg between: "+ Setting.DEBUG_DATE_FORMATTER.print(startDate)+" and "+ Setting.DEBUG_DATE_FORMATTER.print(endDate));

                //get the avg value
                averageMood = calculateAverageBetween(ctx, startDate, endDate, MoodTableHelper.EMoodScope.QUARTER_DAY);
                if(averageMood != null) {

                    mood = new Mood();
                    mood.setScope(MoodTableHelper.EMoodScope.WEEK);
                    mood.setTimestamp(startDate);
                    mood.setMood(averageMood);

                    //add mood to bulk
                    averageMoods.add(MoodTableHelper.fromMood(mood));

                } else {

                    Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "No raw moods in the current time window.");
                }


                //update start and end timestamp
                startDate = DateTimeUtil.withStartOfNextWeek(startDate);
                endDate = DateTimeUtil.withEndOfNextWeek(endDate);
            }

            ctx.getContentResolver().bulkInsert(MoodContentProvider.CONTENT_URI_MOOD, averageMoods.toArray(new ContentValues[averageMoods.size()]));
        }
    }

    /**
     * Calculates the latest daily average values on the database.
     * Uses {@link at.ameise.moodtracker.app.domain.MoodTableHelper.EMoodScope#QUARTER_DAY} as source values.
     * @param ctx the context
     */
    public static void calculateDailyAverage(Context ctx) {

        final List<ContentValues> averageMoods = new ArrayList<>();

        final Cursor dailyTimestampCursor = MoodCursorHelper.getAllDailyValueTimestamps(ctx);
        final Cursor rawTimestampCursor = MoodCursorHelper.getAllRawValueTimestamps(ctx);

        final DateTime maxDate = DateTime.now();

        DateTime startDate = getLastDateTime(dailyTimestampCursor);
        if(startDate != null) {

            Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "Most recent average mood was from: " + Setting.DEBUG_DATE_FORMATTER.print(startDate));
            startDate = DateTimeUtil.withStartOfNextDay(startDate);

        } else {

            Logger.warn(TagConstant.AVERAGE_CALCULATOR, "No average moods available in this scope!");
            Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "Trying to get start date from raw moods...");

            startDate = getFirstDateTime(rawTimestampCursor);
            if(startDate != null) {

                Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "First raw mood was from: " + Setting.DEBUG_DATE_FORMATTER.print(startDate));
                startDate = DateTimeUtil.withStartOfDay(startDate);

            } else {

                Logger.info(TagConstant.AVERAGE_CALCULATOR, "No raw moods available. Nothing to calculate!");
            }
        }

        closeCursor(dailyTimestampCursor);
        closeCursor(rawTimestampCursor);

        if(maxDate != null && startDate != null) {

            Logger.debug(TagConstant.AVERAGE_CALCULATOR, "Start date for calculations: " + Setting.DEBUG_DATE_FORMATTER.print(startDate));
            Logger.debug(TagConstant.AVERAGE_CALCULATOR, "End date for calculations: " + Setting.DEBUG_DATE_FORMATTER.print(maxDate));

            //get the first end date from the start date
            DateTime endDate = DateTimeUtil.withEndOfDay(startDate);

            Mood mood;
            Float averageMood;

            while(endDate.isBefore(maxDate)) {

                Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "Next avg between: "+ Setting.DEBUG_DATE_FORMATTER.print(startDate)+" and "+ Setting.DEBUG_DATE_FORMATTER.print(endDate));

                //get the avg value
                averageMood = calculateAverageBetween(ctx, startDate, endDate, MoodTableHelper.EMoodScope.QUARTER_DAY);
                if(averageMood != null) {

                    mood = new Mood();
                    mood.setScope(MoodTableHelper.EMoodScope.DAY);
                    mood.setTimestamp(startDate);
                    mood.setMood(averageMood);

                    //add mood to bulk
                    averageMoods.add(MoodTableHelper.fromMood(mood));

                } else {

                    Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "No raw moods in the current time window.");
                }


                //update start and end timestamp
                startDate = DateTimeUtil.withStartOfNextDay(startDate);
                endDate = DateTimeUtil.withEndOfNextDay(endDate);
            }

            ctx.getContentResolver().bulkInsert(MoodContentProvider.CONTENT_URI_MOOD, averageMoods.toArray(new ContentValues[averageMoods.size()]));
        }
    }

    /**
     * Calculates the latest quarter daily average values on the database.
     * Uses {@link at.ameise.moodtracker.app.domain.MoodTableHelper.EMoodScope#QUARTER_DAY} as source values.
     * @param ctx the context
     */
    public static void calculateQuarterDailyAverage(Context ctx) {

        final List<ContentValues> averageMoods = new ArrayList<>();

        final Cursor quarterDailyTimestampCursor = MoodCursorHelper.getAllQuarterDailyValueTimestamps(ctx);
        final Cursor rawTimestampCursor = MoodCursorHelper.getAllRawValueTimestamps(ctx);

        final DateTime maxDate = DateTime.now();

        DateTime startDate = getLastDateTime(quarterDailyTimestampCursor);
        if(startDate != null) {

            Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "Most recent average mood was from: " + Setting.DEBUG_DATE_FORMATTER.print(startDate));
            startDate = DateTimeUtil.withStartOfNextQuarterOfDay(startDate);

        } else {

            Logger.warn(TagConstant.AVERAGE_CALCULATOR, "No average moods available in this scope!");
            Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "Trying to get start date from raw moods...");

            startDate = getFirstDateTime(rawTimestampCursor);
            if(startDate != null) {

                Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "First raw mood was from: " + Setting.DEBUG_DATE_FORMATTER.print(startDate));
                startDate = DateTimeUtil.withStartOfQuarterOfDay(startDate);

            } else {

                Logger.info(TagConstant.AVERAGE_CALCULATOR, "No raw moods available. Nothing to calculate!");
            }
        }

        closeCursor(quarterDailyTimestampCursor);
        closeCursor(rawTimestampCursor);

        if(maxDate != null && startDate != null) {

            Logger.debug(TagConstant.AVERAGE_CALCULATOR, "Start date for calculations: " + Setting.DEBUG_DATE_FORMATTER.print(startDate));
            Logger.debug(TagConstant.AVERAGE_CALCULATOR, "End date for calculations: " + Setting.DEBUG_DATE_FORMATTER.print(maxDate));

            //get the first end date from the start date
            DateTime endDate = DateTimeUtil.withEndOfQuarterOfDay(startDate);

            Mood mood;
            Float averageMood;

            while(endDate.isBefore(maxDate)) {

                Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "Next avg between: "+ Setting.DEBUG_DATE_FORMATTER.print(startDate)+" and "+ Setting.DEBUG_DATE_FORMATTER.print(endDate));

                //get the avg value
                averageMood = calculateAverageBetween(ctx, startDate, endDate, MoodTableHelper.EMoodScope.RAW);
                if(averageMood != null) {

                    mood = new Mood();
                    mood.setScope(MoodTableHelper.EMoodScope.QUARTER_DAY);
                    mood.setTimestamp(startDate);
                    mood.setMood(averageMood);

                    //add mood to bulk
                    averageMoods.add(MoodTableHelper.fromMood(mood));

                } else {

                    Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "No raw moods in the current time window.");
                }

                //update start and end timestamp
                startDate = DateTimeUtil.withStartOfNextQuarterOfDay(startDate);
                endDate = DateTimeUtil.withEndOfNextQuarterOfDay(endDate);
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
    private static Float calculateAverageBetween(Context ctx, DateTime startDate, DateTime endDate, MoodTableHelper.EMoodScope scope) {

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

            Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "The average mood: " + avgValue);

        } else {

            Logger.warn(TagConstant.AVERAGE_CALCULATOR, "No value, failed to calculate average mood between " + Setting.DEBUG_DATE_FORMATTER.print(startDate) + " and " + Setting.DEBUG_DATE_FORMATTER.print(endDate) + "!");
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
     * NOTE: The cursor must contain the column {@link at.ameise.moodtracker.app.domain.MoodTableHelper#COL_TIMESTAMP}!
     * @param cursor the cursor
     * @return the first date of the {@link android.database.Cursor}. If the cursor is empty, null is returned.
     */
    private static DateTime getFirstDateTime(Cursor cursor) {

        DateTime firstDate = null;

        if(cursor != null && cursor.moveToFirst()) {

            long millis = cursor.getLong(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_TIMESTAMP));
            Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "Millis: "+millis);
            firstDate = new DateTime().withMillis(millis);
        }

        return firstDate;
    }

    /**
     * NOTE: The cursor must contain the column {@link at.ameise.moodtracker.app.domain.MoodTableHelper#COL_TIMESTAMP}!
     * @param cursor the cursor
     * @return the last date of the {@link android.database.Cursor}. If the cursor is empty, null is returned.
     */
    private static DateTime getLastDateTime(Cursor cursor) {

        DateTime lastDate = null;

        if(cursor != null && cursor.moveToLast()) {

            long millis = cursor.getLong(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_TIMESTAMP));
            Logger.verbose(TagConstant.AVERAGE_CALCULATOR, "Millis: "+millis);
            lastDate = new DateTime(millis);
        }

        return lastDate;
    }

}
