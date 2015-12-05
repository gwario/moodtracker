package at.ameise.moodtracker.domain;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;

import at.ameise.moodtracker.ISetting;

/**
 * Representation of a single mood entry.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 14.02.15.
 */
public class Mood {

    private Long id;
    private float mood;
    private long timestampMs;
    private String scope;
    private boolean onBackend;

    /**
     * Sets the default scope {@link at.ameise.moodtracker.domain.MoodTableHelper.EMoodScope#RAW} and the current time.
     */
    public Mood(float mood) {

        this.mood = mood;
        this.timestampMs = Calendar.getInstance().getTimeInMillis();
        this.scope = MoodTableHelper.EMoodScope.RAW.name();
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
                "id=" + id + ", " +
                "mood=" + mood + ", " +
                "timestampMs=" + ISetting.DEBUG_DATE_FORMAT.format(new Date(timestampMs)) + ", " +
                "scope=" + scope +
                '}';
    }

    public DateTime getTimestamp() {
        return new DateTime(timestampMs);
    }

    void setTimestamp(long milliSeconds) {
        this.timestampMs = milliSeconds;
    }

    void setTimestamp(DateTime timestamp) {
        this.timestampMs = timestamp.getMillis();
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
        return MoodTableHelper.EMoodScope.valueOf(scope);
    }

    void setScope(MoodTableHelper.EMoodScope scope) {
        this.scope = scope.name();
    }

    public at.ameise.moodtracker.moodTrackerBackend.model.Mood getMoodModel() {

        at.ameise.moodtracker.moodTrackerBackend.model.Mood mood = new at.ameise.moodtracker.moodTrackerBackend.model.Mood();

        mood.setMood(this.mood);
        mood.setScope(this.scope);
        mood.setTimestamp(this.timestampMs);

        return mood;
    }

    public boolean isOnBackend() {
        return onBackend;
    }

    public void setOnBackend(boolean onBackend) {
        this.onBackend = onBackend;
    }
}
