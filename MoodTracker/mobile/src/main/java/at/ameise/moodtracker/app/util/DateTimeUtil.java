package at.ameise.moodtracker.app.util;

import org.joda.time.DateTime;

/**
 * Contains helper to modify {@link java.util.Calendar} and get {@link DateTime} objects.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 14.03.15.
 */
public class DateTimeUtil {

    private DateTimeUtil() {}

    /**
     * @param dateTime the datetime
     * @return true if the datetime is the first day of the current month.
     */
    public static boolean isFirstDayOfMonth(DateTime dateTime) {

        return dateTime.getDayOfMonth() == dateTime.dayOfMonth().withMinimumValue().getDayOfMonth();
    }

    /**
     * @param dateTime the datetime
     * @return true if the datetime is the first day of the current week.
     */
    public static boolean isFirstDayOfWeek(DateTime dateTime) {

        return dateTime.getDayOfWeek() == dateTime.dayOfWeek().getMinimumValue();
    }

    /**
     * @param dateTime the datetime
     * @return true if the datetime is within the first quarter of the current day.
     */
    public static boolean isFirstQuarterOfDay(DateTime dateTime) {

        return 0 <= dateTime.getHourOfDay() && dateTime.getHourOfDay() < getDurationOfQuarterOfDayHours();
    }

    /**
     * Returns the datetime with the start of the current day.
     * @param dateTime the datetime
     * @return the datetime with the start of the current day.
     */
    public static DateTime withStartOfDay(DateTime dateTime) {

        return dateTime.withTimeAtStartOfDay();
    }

    /**
     * Returns the datetime with the end of the current day.
     * @param dateTime the datetime
     * @return the datetime with the end of the current day.
     */
    public static DateTime withEndOfDay(DateTime dateTime) {

        return dateTime
            .hourOfDay().withMaximumValue()
            .minuteOfHour().withMaximumValue()
            .secondOfMinute().withMaximumValue()
            .millisOfSecond().withMaximumValue();
    }

    /**
     * Returns the datetime with the start of the next day.
     * @param dateTime the datetime
     * @return the datetime with the start of the next day.
     */
    public static DateTime withStartOfNextDay(DateTime dateTime) {

        return withStartOfDay(dateTime.plusDays(1));
    }

    /**
     * Returns the datetime with the end of the next day.
     * @param dateTime the datetime
     * @return the datetime with the end of the next day.
     */
    public static DateTime withEndOfNextDay(DateTime dateTime) {

        return withEndOfDay(dateTime.plusDays(1));
    }

    /**
     * Returns the datetime with the start of the current week.
     * @param dateTime the datetime
     * @return the datetime with the start of the current week.
     */
    public static DateTime withStartOfWeek(DateTime dateTime) {

        return withStartOfDay(dateTime.dayOfWeek().withMinimumValue());
    }

    /**
     * Returns the datetime with the end of the current week.
     * @param dateTime the datetime
     * @return the datetime with the end of the current week.
     */
    public static DateTime withEndOfWeek(DateTime dateTime) {

        return withEndOfDay(dateTime.dayOfWeek().withMaximumValue());
    }

    /**
     * Returns the datetime with the start of the next week.
     * @param dateTime the datetime
     * @return the datetime with the start of the next week.
     */
    public static DateTime withStartOfNextWeek(DateTime dateTime) {

        return withStartOfWeek(dateTime.plusWeeks(1));
    }

    /**
     * Returns the datetime with the end of the next week.
     * @param dateTime the datetime
     * @return the datetime with the end of the next week.
     */
    public static DateTime withEndOfNextWeek(DateTime dateTime) {

        return withEndOfWeek(dateTime.plusWeeks(1));
    }

    /**
     * Returns the datetime with the beginning of the current month.
     * @param dateTime the datetime
     * @return the datetime with the beginning of the current month.
     */
    public static DateTime withStartOfMonth(DateTime dateTime) {

        return dateTime
            .dayOfMonth().withMinimumValue()
            .withTimeAtStartOfDay();
    }

    /**
     * Returns the datetime with the end of the current month.
     * @param dateTime the datetime
     * @return the datetime with the end of the current month.
     */
    public static DateTime withEndOfMonth(DateTime dateTime) {

        return withEndOfDay(dateTime.dayOfMonth().withMaximumValue());
    }

    /**
     * Returns the datetime with the beginning of the next month.
     * @param dateTime the datetime
     * @return the datetime with the beginning of the next month.
     */
    public static DateTime withStartOfNextMonth(DateTime dateTime) {

        return withStartOfMonth(dateTime.plusMonths(1));
    }

    /**
     * Returns the datetime with the end of the next month.
     * @param dateTime the datetime
     * @return the datetime with the end of the next month.
     */
    public static DateTime withEndOfNextMonth(DateTime dateTime) {

        return withEndOfMonth(dateTime.plusMonths(1));
    }

    /**
     * Returns the datetime with the start of the current quarter day.
     * @param dateTime the datetime
     * @return the datetime with the start of the current quarter day.
     */
    public static DateTime withStartOfQuarterOfDay(DateTime dateTime) {

        DateTime ret = null;
        final int hourOfDay = dateTime.getHourOfDay();
        final int durationOfQuarterOfDay = getDurationOfQuarterOfDayHours();

        if(0 <= hourOfDay && hourOfDay < durationOfQuarterOfDay) {

            ret = dateTime.withHourOfDay(0);

        } else if(durationOfQuarterOfDay <= hourOfDay && hourOfDay < 2 * durationOfQuarterOfDay) {

            ret = dateTime.withHourOfDay(durationOfQuarterOfDay);

        } else if(2 * durationOfQuarterOfDay <= hourOfDay && hourOfDay < 3 * durationOfQuarterOfDay) {

            ret = dateTime.withHourOfDay(2 * durationOfQuarterOfDay);

        } else if(3 * durationOfQuarterOfDay <= hourOfDay && hourOfDay < 4 * durationOfQuarterOfDay) {

            ret = dateTime.withHourOfDay(3 * durationOfQuarterOfDay);
        }

        return ret
            .minuteOfHour().withMinimumValue()
            .secondOfMinute().withMinimumValue()
            .millisOfSecond().withMinimumValue();
    }

    /**
     * Returns the datetime with the start of the next quarter day.
     * @param dateTime the datetime
     * @return the datetime with the start of the next quarter day.
     */
    public static DateTime withStartOfNextQuarterOfDay(DateTime dateTime) {

        return withStartOfQuarterOfDay(dateTime.plusHours(getDurationOfQuarterOfDayHours()));

    }

    /**
     * Returns the begin of the next quarter day.
     * @return The date of the next quarter day. The hours set to 0, 6, 12 or 18; the minutes, seconds and milliseconds set to 0.
     */
    public static DateTime getStartOfNextQuarterOfDay() {

        DateTime ret = DateTime.now();
        final int hourOfDay = ret.getHourOfDay();
        final int durationOfQuarterOfDay = getDurationOfQuarterOfDayHours();

        if(durationOfQuarterOfDay <= hourOfDay && hourOfDay < durationOfQuarterOfDay) {

            ret = ret.withHourOfDay(6);

        } else if(durationOfQuarterOfDay <= hourOfDay && hourOfDay < 2 * durationOfQuarterOfDay) {

            ret = ret.withHourOfDay(12);

        } else if(2 * durationOfQuarterOfDay <= hourOfDay && hourOfDay < 3 * durationOfQuarterOfDay) {

            ret = ret.withHourOfDay(18);

        } else if(3 * durationOfQuarterOfDay <= hourOfDay && hourOfDay < 4 * durationOfQuarterOfDay) {

            ret = ret.plusDays(1).withHourOfDay(0);
        }

        return ret
            .minuteOfHour().withMinimumValue()
            .secondOfMinute().withMinimumValue()
            .millisOfSecond().withMinimumValue();
    }

    /**
     * Returns the datetime with the end of the current quarter day.
     * @param dateTime the datetime
     * @return the datetime with the end of the current quarter day.
     */
    public static DateTime withEndOfQuarterOfDay(DateTime dateTime) {

        DateTime ret = withStartOfQuarterOfDay(dateTime);

        return ret
            .plusHours(getDurationOfQuarterOfDayHours()-1)
            .minuteOfHour().withMaximumValue()
            .secondOfMinute().withMaximumValue()
            .millisOfSecond().withMaximumValue();
    }

    /**
     * Returns the datetime with the end of the next quarter day.
     * @param dateTime the datetime
     * @return the datetime with the end of the next quarter day.
     */
    public static DateTime withEndOfNextQuarterOfDay(DateTime dateTime) {

        return withEndOfQuarterOfDay(dateTime.plusHours(getDurationOfQuarterOfDayHours()));
    }

    /**
     * @return 6 hours!
     */
    private static int getDurationOfQuarterOfDayHours() {

        return 6;
    }

    /**
     * Returns the date of the next noon.
     * @return the date.
     */
    public static DateTime getNextNoon() {

        DateTime ret = DateTime.now();

        if(ret.getHourOfDay() > 12) {
            return ret.plusDays(1)
                .withHourOfDay(12)
                .minuteOfHour().withMinimumValue()
                .secondOfMinute().withMinimumValue()
                .millisOfSecond().withMinimumValue();

        } else {
            return ret
                .withHourOfDay(12)
                .minuteOfHour().withMinimumValue()
                .secondOfMinute().withMinimumValue()
                .millisOfSecond().withMinimumValue();
        }
    }
}
