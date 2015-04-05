package at.ameise.moodtracker.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Contains helper to modify {@link java.util.Calendar} objects.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 14.03.15.
 */
public class CalendarUtil {

    private CalendarUtil() {}

    /**
     * Sets {@link java.util.Calendar#HOUR_OF_DAY} = 0,
     * {@link java.util.Calendar#MINUTE} = 0, {@link java.util.Calendar#SECOND} = 0 and
     * {@link java.util.Calendar#MILLISECOND} = 0
     * @param date the date
     */
    public static void setStartOfDay(Calendar date) {

        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Sets {@link java.util.Calendar#HOUR_OF_DAY} = 23,
     * {@link java.util.Calendar#MINUTE} = 59, {@link java.util.Calendar#SECOND} = 59 and
     * {@link java.util.Calendar#MILLISECOND} = 999
     * @param date the date
     */
    public static void setEndOfDay(Calendar date) {

        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 999);
    }

    /**
     * Adds {@link java.util.Calendar#DATE} 1,
     * Sets {@link java.util.Calendar#HOUR_OF_DAY} = 0,
     * {@link java.util.Calendar#MINUTE} = 0, {@link java.util.Calendar#SECOND} = 0 and
     * {@link java.util.Calendar#MILLISECOND} = 0
     * @param date the date
     */
    public static void setStartOfNextDay(Calendar date) {

        date.add(Calendar.DATE, 1);
        setStartOfDay(date);
    }

    /**
     * Subtracts {@link java.util.Calendar#DATE} 1,
     * Sets {@link java.util.Calendar#HOUR_OF_DAY} = 23,
     * {@link java.util.Calendar#MINUTE} = 59, {@link java.util.Calendar#SECOND} = 59 and
     * {@link java.util.Calendar#MILLISECOND} = 999
     * @param date the date
     */
    public static void setEndOfPreviousDay(Calendar date) {

        date.add(Calendar.DATE, -1);
        setEndOfDay(date);
    }

    /**
     * Adds {@link java.util.Calendar#DATE} 1,
     * Sets {@link java.util.Calendar#HOUR_OF_DAY} = 23,
     * {@link java.util.Calendar#MINUTE} = 59, {@link java.util.Calendar#SECOND} = 59 and
     * {@link java.util.Calendar#MILLISECOND} = 999
     * @param date the date
     */
    public static void setEndOfNextDay(Calendar date) {

        date.add(Calendar.DATE, 1);
        setEndOfDay(date);
    }

    /**
     * Sets {@link java.util.Calendar#DAY_OF_WEEK} = {@link java.util.Calendar#MONDAY},
     * {@link java.util.Calendar#HOUR_OF_DAY} = 0,
     * {@link java.util.Calendar#MINUTE} = 0, {@link java.util.Calendar#SECOND} = 0 and
     * {@link java.util.Calendar#MILLISECOND} = 0
     * @param date the date
     */
    public static void setStartOfWeek(Calendar date) {

        date.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        setStartOfDay(date);
    }

    /**
     * Sets {@link java.util.Calendar#DAY_OF_WEEK} = {@link java.util.Calendar#SUNDAY},
     * {@link java.util.Calendar#HOUR_OF_DAY} = 23,
     * {@link java.util.Calendar#MINUTE} = 59, {@link java.util.Calendar#SECOND} = 59 and
     * {@link java.util.Calendar#MILLISECOND} = 999
     * @param date the date
     */
    public static void setEndOfWeek(Calendar date) {

        date.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        setEndOfDay(date);
    }

    /**
     * Subtracts {@link java.util.Calendar#WEEK_OF_MONTH} 1
     * Sets {@link java.util.Calendar#DAY_OF_WEEK} = {@link java.util.Calendar#SUNDAY},
     * {@link java.util.Calendar#HOUR_OF_DAY} = 23,
     * {@link java.util.Calendar#MINUTE} = 59, {@link java.util.Calendar#SECOND} = 59 and
     * {@link java.util.Calendar#MILLISECOND} = 999
     * @param date the date
     */
    public static void setEndOfPreviousWeek(Calendar date) {

        date.add(Calendar.WEEK_OF_MONTH, -1);
        setEndOfWeek(date);
    }

    /**
     * Adds {@link java.util.Calendar#WEEK_OF_MONTH} 1
     * Sets {@link java.util.Calendar#DAY_OF_WEEK} = {@link java.util.Calendar#MONDAY},
     * {@link java.util.Calendar#HOUR_OF_DAY} = 0,
     * {@link java.util.Calendar#MINUTE} = 0, {@link java.util.Calendar#SECOND} = 0 and
     * {@link java.util.Calendar#MILLISECOND} = 0
     * @param date the date
     */
    public static void setStartOfNextWeek(Calendar date) {

        date.add(Calendar.WEEK_OF_MONTH, 1);
        setStartOfWeek(date);
    }

    /**
     * Adds {@link java.util.Calendar#WEEK_OF_MONTH} 1
     * Sets {@link java.util.Calendar#DAY_OF_WEEK} = {@link java.util.Calendar#SUNDAY},
     * {@link java.util.Calendar#HOUR_OF_DAY} = 23,
     * {@link java.util.Calendar#MINUTE} = 59, {@link java.util.Calendar#SECOND} = 59 and
     * {@link java.util.Calendar#MILLISECOND} = 999
     * @param date the date
     */
    public static void setEndOfNextWeek(Calendar date) {

        date.add(Calendar.WEEK_OF_MONTH, 1);
        setEndOfWeek(date);
    }

    /**
     * Sets {@link java.util.Calendar#DAY_OF_MONTH} = {@link java.util.Calendar#getActualMinimum(int)},
     * {@link java.util.Calendar#HOUR_OF_DAY} = 0,
     * {@link java.util.Calendar#MINUTE} = 0, {@link java.util.Calendar#SECOND} = 0 and
     * {@link java.util.Calendar#MILLISECOND} = 0
     * @param date the date
     */
    public static void setStartOfMonth(Calendar date) {

        date.set(Calendar.DAY_OF_MONTH, date.getActualMinimum(Calendar.DAY_OF_MONTH));
        setStartOfDay(date);
    }

    /**
     * Sets {@link java.util.Calendar#DAY_OF_MONTH} = {@link java.util.Calendar#getActualMaximum(int)},
     * {@link java.util.Calendar#HOUR_OF_DAY} = 23,
     * {@link java.util.Calendar#MINUTE} = 59, {@link java.util.Calendar#SECOND} = 59 and
     * {@link java.util.Calendar#MILLISECOND} = 999
     * @param date the date
     */
    public static void setEndOfMonth(Calendar date) {

        date.set(Calendar.DAY_OF_MONTH, date.getActualMaximum(Calendar.DAY_OF_MONTH));
        setEndOfDay(date);
    }

    /**
     * Sets {@link java.util.Calendar#DAY_OF_MONTH} = {@link java.util.Calendar#getActualMaximum(int)},
     * {@link java.util.Calendar#HOUR_OF_DAY} = 0,
     * {@link java.util.Calendar#MINUTE} = 0, {@link java.util.Calendar#SECOND} = 0 and
     * {@link java.util.Calendar#MILLISECOND} = 0
     * and adds one month.
     * @param date the date
     */
    public static void setStartOfNextMonth(Calendar date) {

        date.add(Calendar.MONTH, 1);
        setStartOfMonth(date);
    }

    /**
     * Adds {@link java.util.Calendar#MONTH} 1
     * Sets {@link java.util.Calendar#DAY_OF_MONTH} = {@link java.util.Calendar#getActualMaximum(int)},
     * {@link java.util.Calendar#HOUR_OF_DAY} = 23,
     * {@link java.util.Calendar#MINUTE} = 59, {@link java.util.Calendar#SECOND} = 59 and
     * {@link java.util.Calendar#MILLISECOND} = 999
     * @param date the date
     */
    public static void setEndOfNextMonth(Calendar date) {

        date.add(Calendar.MONTH, 1);
        setEndOfMonth(date);
    }

    /**
     * Subtracts {@link java.util.Calendar#MONTH} 1
     * Sets {@link java.util.Calendar#DAY_OF_MONTH} = {@link java.util.Calendar#getActualMaximum(int)},
     * {@link java.util.Calendar#HOUR_OF_DAY} = 23,
     * {@link java.util.Calendar#MINUTE} = 59, {@link java.util.Calendar#SECOND} = 59 and
     * {@link java.util.Calendar#MILLISECOND} = 999
     * @param date the date
     */
    public static void setEndOfPreviousMonth(Calendar date) {

        date.add(Calendar.MONTH, -1);
        setEndOfMonth(date);
    }

    /**
     * @param date the date
     * @return true if the specified date is within the first quarter of the day.
     */
    public static boolean isFirstQuarterOfDay(Calendar date) {

        final long hourOfDay = date.get(Calendar.HOUR_OF_DAY);

        return 0 <= hourOfDay && hourOfDay <= date.getActualMaximum(Calendar.HOUR_OF_DAY) / 4;
    }

    /**
     * @param date the date
     * @return true if the specified date is on the first day of the week.
     */
    public static boolean isFirstDayOfWeek(Calendar date) {

        return date.get(Calendar.DAY_OF_WEEK) == date.getActualMinimum(Calendar.DAY_OF_WEEK);
    }

    /**
     * @param date the date
     * @return true if the specified date is on the first day of the month.
     */
    public static boolean isFirstDayOfMonth(Calendar date) {

        return date.get(Calendar.DAY_OF_MONTH) == date.getActualMinimum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Sets {@link java.util.Calendar#HOUR_OF_DAY} = to the begin of the quarter,
     * {@link java.util.Calendar#MINUTE} = 0, {@link java.util.Calendar#SECOND} = 0 and
     * {@link java.util.Calendar#MILLISECOND} = 0
     * @param date the date
     */
    public static void setStartOfQuarterOfDay(Calendar date) {

        final int hourOfDay = date.get(Calendar.HOUR_OF_DAY);
        final int durationOfQuarterOfDay = getDurationOfQuarterOfDay(date);

        if(0 * durationOfQuarterOfDay <= hourOfDay && hourOfDay < 1 * durationOfQuarterOfDay) {

            date.set(Calendar.HOUR_OF_DAY, 0);

        } else if(1 * durationOfQuarterOfDay <= hourOfDay && hourOfDay < 2 * durationOfQuarterOfDay) {

            date.set(Calendar.HOUR_OF_DAY, durationOfQuarterOfDay);

        } else if(2 * durationOfQuarterOfDay <= hourOfDay && hourOfDay < 3 * durationOfQuarterOfDay) {

            date.set(Calendar.HOUR_OF_DAY, 2 * durationOfQuarterOfDay);

        } else if(3 * durationOfQuarterOfDay <= hourOfDay && hourOfDay < 4 * durationOfQuarterOfDay) {

            date.set(Calendar.HOUR_OF_DAY, 3 * durationOfQuarterOfDay);
        }

        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Sets {@link java.util.Calendar#HOUR_OF_DAY} = the begin of the next of the quarter(adds a quarter of a day an calls setStartOfQuarterOfDay),
     * {@link java.util.Calendar#MINUTE} = 59, {@link java.util.Calendar#SECOND} = 59 and
     * {@link java.util.Calendar#MILLISECOND} = 999
     * @param date the date
     */
    public static void setStartOfNextQuarterOfDay(Calendar date) {

        date.add(Calendar.HOUR, getDurationOfQuarterOfDay(date));
        setStartOfQuarterOfDay(date);
    }

    /**
     * Sets {@link java.util.Calendar#HOUR_OF_DAY} = the end of the quarter,
     * {@link java.util.Calendar#MINUTE} = 59, {@link java.util.Calendar#SECOND} = 59 and
     * {@link java.util.Calendar#MILLISECOND} = 999
     * @param date the date
     */
    public static void setEndOfQuarterOfDay(Calendar date) {

        final int durationOfQuarterOfDay = getDurationOfQuarterOfDay(date);

        setStartOfQuarterOfDay(date);

        date.add(Calendar.HOUR, durationOfQuarterOfDay - 1);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 999);
    }

    /**
     * Sets {@link java.util.Calendar#HOUR_OF_DAY} = the end of the next quarter,
     * {@link java.util.Calendar#MINUTE} = 59, {@link java.util.Calendar#SECOND} = 59 and
     * {@link java.util.Calendar#MILLISECOND} = 999
     * @param date the date
     */
    public static void setEndOfNextQuarterOfDay(Calendar date) {

        date.add(Calendar.HOUR, getDurationOfQuarterOfDay(date));
        setEndOfQuarterOfDay(date);
    }

    /**
     * Sets {@link java.util.Calendar#HOUR_OF_DAY} = the end of the previous quarter,
     * {@link java.util.Calendar#MINUTE} = 59, {@link java.util.Calendar#SECOND} = 59 and
     * {@link java.util.Calendar#MILLISECOND} = 999
     * @param date the date
     */
    public static void setEndOfPreviousQuarterOfDay(Calendar date) {

        date.add(Calendar.HOUR, -getDurationOfQuarterOfDay(date));
        setEndOfQuarterOfDay(date);
    }

    /**
     * @param date the date
     * @return getActualMaximum(Calendar.HOUR_OF_DAY) + 1) / 4 which is 6 hours!
     */
    private static int getDurationOfQuarterOfDay(Calendar date) {

        return (date.getActualMaximum(Calendar.HOUR_OF_DAY) + 1) / 4;
    }

}
