package at.ameise.moodtracker.backend.models;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.List;

@Entity
public class UserAccount {

    @Id
    private Long key;

    private String firstName;
    private String lastName;

    @Index
    private String email;

    private List<Ref<Mood>> moods = new ArrayList<Ref<Mood>>();


    public final String getFirstName() {
        return firstName;
    }

    public final void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public final String getLastName() {
        return lastName;
    }

    public final void setName(final String lastName) {
        this.lastName = lastName;
    }

    public final String getEmail() {
        return email;
    }

    public final void setEmail(final String email) {
        this.email = email;
    }

    public List<Ref<Mood>> getMoods() {
        return moods;
    }

    public void setMoods(final List<Ref<Mood>> moods) {
        this.moods = moods;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "key=" + key +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", moods=" + moods +
                '}';
    }
}
