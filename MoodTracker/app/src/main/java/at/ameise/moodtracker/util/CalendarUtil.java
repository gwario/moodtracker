package at.ameise.moodtracker.util;

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
     * @param date
     */
    public static final void setStartOfDay(Calendar date) {

        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Sets {@link java.util.Calendar#HOUR_OF_DAY} = 23,
     * {@link java.util.Calendar#MINUTE} = 59, {@link java.util.Calendar#SECOND} = 59 and
     * {@link java.util.Calendar#MILLISECOND} = 999
     * @param date
     */
    public static final void setEndOfDay(Calendar date) {

        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 999);
    }

    /**
     * Sets {@link java.util.Calendar#DAY_OF_WEEK} = {@link java.util.Calendar#MONDAY},
     * {@link java.util.Calendar#HOUR_OF_DAY} = 0,
     * {@link java.util.Calendar#MINUTE} = 0, {@link java.util.Calendar#SECOND} = 0 and
     * {@link java.util.Calendar#MILLISECOND} = 0
     * @param date
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
     * @param date
     */
    public static void setEndOfWeek(Calendar date) {

        date.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        setEndOfDay(date);
    }

    /**
     * Sets {@link java.util.Calendar#DAY_OF_MONTH} = {@link java.util.Calendar#getActualMaximum(int)},
     * {@link java.util.Calendar#HOUR_OF_DAY} = 0,
     * {@link java.util.Calendar#MINUTE} = 0, {@link java.util.Calendar#SECOND} = 0 and
     * {@link java.util.Calendar#MILLISECOND} = 0
     * @param date
     */
    public static void setStartOfMonth(Calendar date) {

        date.set(Calendar.DAY_OF_MONTH, 1);
        setStartOfDay(date);
    }

    /**
     * Sets {@link java.util.Calendar#DAY_OF_MONTH} = ,
     * {@link java.util.Calendar#HOUR_OF_DAY} = 23,
     * {@link java.util.Calendar#MINUTE} = 59, {@link java.util.Calendar#SECOND} = 59 and
     * {@link java.util.Calendar#MILLISECOND} = 999
     * @param date
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
     * @param date
     */
    public static void setStartOfNextMonth(Calendar date) {

        setStartOfMonth(date);
        date.add(Calendar.MONTH, 1);
    }

    /**
     * @param date
     * @return true if the specified date is within the first quarter of the day.
     */
    public static boolean isFirstQuarterOfDay(Calendar date) {

        final long hourOfDay = date.get(Calendar.HOUR_OF_DAY);

        return 0 <= hourOfDay && hourOfDay <= date.getActualMaximum(Calendar.HOUR_OF_DAY) / 4;
    }

    /**
     * @param date
     * @return true if the specified date is on the first day of the week.
     */
    public static boolean isFirstDayOfWeek(Calendar date) {

        return date.get(Calendar.DAY_OF_WEEK) == date.getActualMinimum(Calendar.DAY_OF_WEEK);
    }

    /**
     * @param date
     * @return true if the specified date is on the first day of the month.
     */
    public static boolean isFirstDayOfMonth(Calendar date) {

        return date.get(Calendar.DAY_OF_MONTH) == 1;
    }
}
