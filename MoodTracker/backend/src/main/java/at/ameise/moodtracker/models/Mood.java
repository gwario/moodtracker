package at.ameise.moodtracker.models;

import com.google.api.server.spi.auth.common.User;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

/**
 * Created by mariogastegger on 28.11.15.
 */
@Entity
public class Mood {

    @Id
    private Long key;

    @Parent
    private Key<UserAccount> userAccountKey;

    private int mood;

    @Index
    private long timestamp;

    @Index
    private int scope;


    public final DateTime getTimestamp() {
        return new DateTime(this.timestamp);
    }

    public final void setTimestamp(final DateTime timestamp) {
        this.timestamp = timestamp.getMillis();
    }
}
