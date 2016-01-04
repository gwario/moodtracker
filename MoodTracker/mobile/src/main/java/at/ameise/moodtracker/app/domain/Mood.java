package at.ameise.moodtracker.app.domain;

import com.google.android.gms.wearable.DataMap;

import org.joda.time.DateTime;

import at.ameise.moodtracker.app.Setting;

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
    private Long syncTimestampNs;


    /**
     * Sets the default scope {@link at.ameise.moodtracker.app.domain.MoodTableHelper.EMoodScope#RAW} and the current time.
     */
    public Mood(float mood) {

        this.mood = mood;
        this.timestampMs = DateTime.now().getMillis();
        this.scope = MoodTableHelper.EMoodScope.RAW.name();
    }

    /**
     * For creation from wearable devices.
     * TODO maybe use a static method for this
     */
    public Mood(DataMap dataMap) {

        final String MOOD_KEY = "com.example.key.mood";
        final String TIMESTAMP_KEY = "com.example.key.ts";
        this.mood = dataMap.getInt(MOOD_KEY);
        this.timestampMs = dataMap.getLong(TIMESTAMP_KEY);;
        this.scope = MoodTableHelper.EMoodScope.RAW.name();
    }

    /**
     * For creation from database values.
     * This constructor does not set any fields.
     * TODO maybe use a static method for this
     */
    Mood() {}

    @Override
    public String toString() {
        return "Mood{" +
                "id=" + id +
                ", mood=" + mood +
                ", timestampMs=" + timestampMs +
                ", scope='" + scope + '\'' +
                ", syncTimestampNs=" + syncTimestampNs +
                '}';
    }

    public DateTime getTimestamp() {
        return new DateTime().withMillis(timestampMs);
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

        at.ameise.moodtracker.moodTrackerBackend.model.Mood moodModel = new at.ameise.moodtracker.moodTrackerBackend.model.Mood();

        moodModel.setMood(this.mood);
        moodModel.setScope(this.scope);
        moodModel.setTimestamp(this.timestampMs);

        return moodModel;
    }

    //TODO maybe use a constructor for this??
    public static Mood getMoodFromModel(at.ameise.moodtracker.moodTrackerBackend.model.Mood moodModel) {

        Mood mood = new Mood();

        mood.setMood(moodModel.getMood());
        mood.setScope(MoodTableHelper.EMoodScope.valueOf(moodModel.getScope()));
        mood.setTimestamp(moodModel.getTimestamp());
        mood.setSyncTimestampNs(moodModel.getSyncTimestampNs());

        return mood;
    }

    public Long getSyncTimestampNs() {
        return syncTimestampNs;
    }

    void setSyncTimestampNs(long syncTimestampNs) {
        this.syncTimestampNs = syncTimestampNs;
    }
}
