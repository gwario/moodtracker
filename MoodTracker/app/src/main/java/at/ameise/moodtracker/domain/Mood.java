package at.ameise.moodtracker.domain;

import java.util.Calendar;

/**
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 14.02.15.
 */
public class Mood {

    private Long id;
    private float mood;
    private Calendar date;

    @Override
    public String toString() {
        return "Mood {" +
                "mood=" + mood + ", " +
                "date=" + date +
                '}';
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public float getMood() {

        return mood;
    }

    public void setMood(float mood) {
        this.mood = mood;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
