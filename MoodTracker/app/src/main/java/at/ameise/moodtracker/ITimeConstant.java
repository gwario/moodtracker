package at.ameise.moodtracker;

/**
 * Contains some time constants.
 *
 * Created by Mario Gastegger <mgastegger AT buzzmark DOT com> on 04.04.15.
 */
public interface ITimeConstant {

    static final long SECOND_MILLIS = 1000;
    static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    static final long DAY_MILLIS = 24 * HOUR_MILLIS;

    static final long QUARTER_DAY_MILLIS = DAY_MILLIS / 4;
}
