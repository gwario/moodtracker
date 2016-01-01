package at.ameise.moodtracker.backend.apis.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import at.ameise.moodtracker.backend.models.Mood;

/**
 * Container for a list of moods.
 * Since cloud endpoints don't support lists as parameters, we have to use this container.
 *
 * Created by mariogastegger on 09.12.15.
 */
public class MoodList implements Iterable<Mood> {

    List<Mood> moods = new ArrayList<>();

    public List<Mood> getMoods() {
        return moods;
    }

    public void setMoods(List<Mood> moods) {
        this.moods = moods;
    }

    public int size() {
        return moods.size();
    }

    public void add(Mood mood) {
        moods.add(mood);
    }

    @Override
    public String toString() {
        return "MoodList{" +
            "moods=" + moods +
            '}';
    }

    @Override
    public Iterator iterator() {
        return moods.iterator();
    }
}
