package at.ameise.moodtracker.domain;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Contains all information necessary to understand the data contained in the mood table.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 14.02.15.
 */
public class MoodTableHelper {

    static final String TABLE_NAME = "mood";

    static final String COL_ID = "_id";
    static final String COL_MOOD = "mood";
    static final String COL_TIMESTAMP = "timestamp";
    static final String COL_SCOPE = "scope";

    public static final String[] ALL_COLUMNS = { COL_ID, COL_MOOD, COL_TIMESTAMP, COL_SCOPE };

    public static final String SORT_ORDER_TIMESTAMP_DESC = COL_TIMESTAMP + " desc";
    public static final String SORT_ORDER_TIMESTAMP_ASC = COL_TIMESTAMP + " asc";

    static final String STMT_CREATE = createStmtCreate(TABLE_NAME);

    /**
     * @param tableName the name of the new table
     * @return a create statement with for this schema with the specified table name.
     */
    static String createStmtCreate(String tableName) {

        return "CREATE TABLE IF NOT EXISTS " + tableName + "("
                + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TIMESTAMP +" TEXT NOT NULL UNIQUE ON CONFLICT REPLACE,"
                + COL_MOOD +" REAL NOT NULL,"
                + COL_SCOPE + " TEXT NOT NULL DEFAULT '"+EMoodScope.RAW.getColumnValue()+"'"
                +")";
    }

    /**
     * @param mood the mood to populate the ContentValues with.
     * @return returns the {@link android.content.ContentValues} containing the mood.
     */
    public static ContentValues fromMood(Mood mood) {

        final ContentValues values = new ContentValues();

        values.put(COL_MOOD, mood.getMood());
        values.put(COL_TIMESTAMP, mood.getDateInSeconds());
        values.put(COL_SCOPE, mood.getScope().getColumnValue());

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
        mood.setScope(EMoodScope.fromColumnValue(cursor.getString(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_SCOPE))));
        mood.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_TIMESTAMP)));

        return mood;
    }

    /**
     * The scope of a mood represents the time frame in which it is valid.
     * <p/>
     * Values for the SCOPE column:
     * <ul>
     * <li>RAW            a single mood value as set by the user<li/>
     * <li>QUARTER_DAY    the average of a quarter of a day<li/>
     * <li>DAY            the average of a day<li/>
     * <li>WEEK           the average of a week<li/>
     * <li>MONTH          the average of a month<li/>
     * </ul>
     */
    public static enum EMoodScope {

        RAW("RAW"),
        QUARTER_DAY("QUARTER_DAY"),
        DAY("DAY"),
        WEEK("WEEK"),
        MONTH("MONTH");

        private String columnValue;

        /**
         * @param columnValue the string stored in {@link at.ameise.moodtracker.domain.MoodTableHelper#COL_SCOPE}
         */
        private EMoodScope(String columnValue) {

            this.columnValue = columnValue;
        }

        String getColumnValue() {
            return columnValue;
        }

        /**
         * @param columnValue the column value for the scope.
         * @return the enum with the specified column value.
         */
        static EMoodScope fromColumnValue(String columnValue) {

            for(EMoodScope scope : values()) {
                if(scope.getColumnValue().equals(columnValue))
                    return scope;
            }

            throw new IllegalArgumentException("There is no scope for the column value '"+columnValue+"'!");
        }
    }
}
