package at.ameise.moodtracker.domain;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Calendar;

/**
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 14.02.15.
 */
public class MoodTableHelper {

    static final String TABLE_NAME = "mood";

    static final String COL_ID = "_id";
    static final String COL_MOOD = "mood";
    static final String COL_TIMESTAMP = "timestamp";

    public static final String[] ALL_COLUMNS = { COL_ID, COL_MOOD, COL_TIMESTAMP, };

    public static final String SORT_ORDER_MOOD_DESC = COL_MOOD + " desc";
    public static final String SORT_ORDER_MOOD_ASC = COL_MOOD + " asc";

    public static final String SORT_ORDER_TIMESTAMP_ASC = COL_TIMESTAMP + " asc";
    public static final String SORT_ORDER_TIMESTAMP_DESC = COL_TIMESTAMP + " desc";


    static final String STMT_CREATE = createStmtCreate(TABLE_NAME);

    /**
     * @param tableName
     * @return a create statement with for this schema with the specified table name.
     */
    static final String createStmtCreate(String tableName) {

        return "CREATE TABLE IF NOT EXISTS " + tableName + "("
                + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TIMESTAMP +" TEXT NOT NULL UNIQUE ON CONFLICT REPLACE,"
                + COL_MOOD +" REAL NOT NULL"
                +")";
    }

    /**
     * @param mood
     * @return returns the @link{ #ContentValues} containing the mood.
     */
    public static ContentValues fromMood(Mood mood) {

        final ContentValues values = new ContentValues();

        values.put(COL_MOOD, mood.getMood());
        values.put(COL_TIMESTAMP, mood.getDate().getTimeInMillis() / 1000);

        return values;
    }

    /**
     * @param cursor    cursor pointing to a row
     * @return the {@link at.ameise.moodtracker.domain.Mood} at the current cursor position.
     */
    public static Mood fromCursor(Cursor cursor) {

        final Mood mood = new Mood();

        mood.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_ID)));
        mood.setMood(cursor.getFloat(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_MOOD)));
        Calendar timestamp = Calendar.getInstance();
        timestamp.setTimeInMillis(1000 * cursor.getLong(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_TIMESTAMP)));
        mood.setDate(timestamp);

        return mood;
    }
}
