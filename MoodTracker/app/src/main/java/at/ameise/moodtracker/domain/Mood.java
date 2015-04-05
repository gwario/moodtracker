package at.ameise.moodtracker.domain;

import java.util.Calendar;

import at.ameise.moodtracker.ISetting;

/**
 * Representation of a single mood entry.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 14.02.15.
 */
public class Mood {

    private Long id;
    private float mood;
    private Calendar date;
    private MoodTableHelper.EMoodScope scope;

    /**
     * Sets the default scope {@link at.ameise.moodtracker.domain.MoodTableHelper.EMoodScope#RAW} and the current time.
     */
    public Mood(float mood) {

        this.mood = mood;
        this.date = Calendar.getInstance();
        this.scope = MoodTableHelper.EMoodScope.RAW;
    }

    /**
     * For creation from database values.
     * This constructor does not set any fields.
     */
    Mood() {
    }

    @Override
    public String toString() {
        return "Mood {" +
                "mood=" + mood + ", " +
                "date=" + ISetting.DEBUG_DATE_FORMAT.format(date.getTime()) + ", " +
                "scope=" + scope +
                '}';
    }

    public Calendar getDate() {
        return date;
    }

    public long getDateInSeconds() {
        return date.getTimeInMillis() / 1000;
    }

    void setDate(long seconds) {

        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(seconds * 1000);

        this.date = c;
    }

    void setDate(Calendar date) {
        this.date = date;
    }

    public float getMood() {

        return mood;
    }

    void setMood(float mood) {
        this.mood = mood;
    }

    public Long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    public MoodTableHelper.EMoodScope getScope() {
        return scope;
    }

    void setScope(MoodTableHelper.EMoodScope scope) {
        this.scope = scope;
    }
}
