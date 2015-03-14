package at.ameise.moodtracker;

/**
 * Contains all loader ids.
 *
 * Created by Mario Gastegger <mgastegger AT buzzmark DOT com> on 03.03.15.
 */
public interface ILoader {

    static final int MOOD_HISTORY_ALL_VALUES_LOADER = 0;
    static final int MOOD_HISTORY_PER_DAY_LOADER = 1;
    static final int MOOD_HISTORY_PER_WEEK_LOADER = 2;
    static final int MOOD_HISTORY_PER_MONTH_LOADER = 3;
}
