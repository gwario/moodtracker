package at.ameise.moodtracker.app;

/**
 * Contains some time constants.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 04.04.15.
 */
public final class TimeConstant {

    private TimeConstant() {}

    @Deprecated
    public static final long SECOND_MILLIS = 1000;
    @Deprecated
    public static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    @Deprecated
    public static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    @Deprecated
    public static final long DAY_MILLIS = 24 * HOUR_MILLIS;

    @Deprecated
    public static final long QUARTER_DAY_MILLIS = DAY_MILLIS / 4;

    public static final int QUARTER_DAY_H = 6;
}
