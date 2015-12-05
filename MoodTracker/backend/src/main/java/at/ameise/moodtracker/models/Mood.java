package at.ameise.moodtracker.models;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Ref;
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

    @Parent @Index
    private Ref<UserAccount> userAccount;

    private float mood;

    @Index
    private long timestampMs;

    @Index
    private String scope;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public float getMood() {
        return mood;
    }

    public void setMood(float mood) {
        this.mood = mood;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Ref<UserAccount> getUserAccount() {
        return userAccount;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setUserAccount(Ref<UserAccount> userAccount) {
        this.userAccount = userAccount;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public final long getTimestamp() {
        return this.timestampMs;
    }

    public final void setTimestamp(final long timestampMs) {
        this.timestampMs = timestampMs;
    }

    @Override
    public String toString() {
        return "Mood{" +
                "key=" + key +
                ", userAccount=" + userAccount +
                ", mood=" + mood +
                ", timestampMs=" + timestampMs +
                ", scope='" + scope + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mood mood1 = (Mood) o;

        if (Float.compare(mood1.mood, mood) != 0) return false;
        if (timestampMs != mood1.timestampMs) return false;
        if (key != null ? !key.equals(mood1.key) : mood1.key != null) return false;
        if (userAccount != null ? !userAccount.equals(mood1.userAccount) : mood1.userAccount != null)
            return false;
        return !(scope != null ? !scope.equals(mood1.scope) : mood1.scope != null);

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (userAccount != null ? userAccount.hashCode() : 0);
        result = 31 * result + (mood != +0.0f ? Float.floatToIntBits(mood) : 0);
        result = 31 * result + (int) (timestampMs ^ (timestampMs >>> 32));
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        return result;
    }
}
