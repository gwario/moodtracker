package domain;

import java.util.Calendar;

/**
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 14.02.15.
 */
public class Mood {

    private int id;
    private int mood;
    private Calendar date;

    @Override
    public String toString() {
        return "Mood{" +
                "mood=" + mood +
                ", date=" + date +
                '}';
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getMood() {

        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }
}
