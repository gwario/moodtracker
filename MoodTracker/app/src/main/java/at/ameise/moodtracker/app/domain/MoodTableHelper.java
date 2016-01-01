package at.ameise.moodtracker.app.domain;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Arrays;

import at.ameise.moodtracker.app.util.Logger;

/**
 * Contains all information necessary to understand the data contained in the mood table.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 14.02.15.
 */
public class MoodTableHelper {

    private static final String TAG = "MoodTableHelper";

    static final String TABLE_NAME = "mood";

    static final String COL_ID = "_id";
    static final String COL_MOOD = "mood";
    static final String COL_TIMESTAMP = "timestamp";
    static final String COL_SCOPE = "scope";
    static final String COL_SYNC_TIMESTAMP = "syncTimestampNs";

    public static final String[] ALL_COLUMNS = { COL_ID, COL_MOOD, COL_TIMESTAMP, COL_SCOPE, COL_SYNC_TIMESTAMP };

    public static final String SORT_ORDER_TIMESTAMP_DESC = COL_TIMESTAMP + " desc";
    public static final String SORT_ORDER_TIMESTAMP_ASC = COL_TIMESTAMP + " asc";
    public static final String SORT_ORDER_SYNC_TIMESTAMP_DESC = COL_SYNC_TIMESTAMP + " desc";
    public static final String SORT_ORDER_SYNC_TIMESTAMP_ASC = COL_SYNC_TIMESTAMP+ " asc";

    /**
     * The current schema.
     */
    static final String STMT_CREATE = createStmtCreate(DatabaseHelper.DATABASE_VERSION, TABLE_NAME);

    /**
     * TODO this is still not the best way.... rethink it.
     * @param thisVersion the version of which the create statement should be returned.
     * @param tableName the name of the new table
     * @return a create statement with for this schema with the specified table name.
     */
    static String createStmtCreate(int thisVersion, String tableName) {

        switch (thisVersion) {

            case 1:
                return "CREATE TABLE IF NOT EXISTS " + tableName + "("
                    + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_TIMESTAMP +" TEXT NOT NULL UNIQUE ON CONFLICT REPLACE,"
                    + COL_MOOD +" INTEGER NOT NULL"
                    +")";

            case 7:
                return "CREATE TABLE IF NOT EXISTS " + tableName + "("
                    + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_TIMESTAMP +" TEXT NOT NULL UNIQUE ON CONFLICT REPLACE,"
                    + COL_MOOD +" REAL NOT NULL,"
                    + COL_SCOPE + " TEXT NOT NULL DEFAULT '"+EMoodScope.RAW.getColumnValue()+"'"
                    +")";

            case 8:
                return "CREATE TABLE IF NOT EXISTS " + tableName + "("
                    + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_TIMESTAMP +" INTEGER NOT NULL UNIQUE ON CONFLICT REPLACE,"
                    + COL_MOOD +" REAL NOT NULL,"
                    + COL_SCOPE + " TEXT NOT NULL DEFAULT '"+EMoodScope.RAW.getColumnValue()+"'"
                    +")";

            case 9://the current version needs always to be in here as well as all the schema changing versions
                return "CREATE TABLE IF NOT EXISTS " + tableName + "("
                    + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_TIMESTAMP +" INTEGER NOT NULL UNIQUE ON CONFLICT REPLACE,"
                    + COL_MOOD +" REAL NOT NULL,"
                    + COL_SCOPE + " TEXT NOT NULL DEFAULT '"+EMoodScope.RAW.getColumnValue()+"',"
                    + COL_SYNC_TIMESTAMP +" INTEGER DEFAULT NULL"
                    +")";

            default:
                throw new AssertionError("Unhandled schema version!");
        }
    }

    /**
     * @param mood the mood to populate the ContentValues with.
     * @return returns the {@link android.content.ContentValues} containing the mood.
     */
    public static ContentValues fromMood(Mood mood) {

        final ContentValues values = new ContentValues();

        if(mood.getId() != null)
            values.put(COL_ID, mood.getId());
        values.put(COL_MOOD, mood.getMood());
        values.put(COL_TIMESTAMP, mood.getTimestamp().getMillis());
        values.put(COL_SCOPE, mood.getScope().getColumnValue());
        values.put(COL_SYNC_TIMESTAMP, mood.getSyncTimestampNs());

        return values;
    }

    /**
     * @param cursor    cursor pointing to a row
     * @return the {@link at.ameise.moodtracker.app.domain.Mood} at the current cursor position.
     */
    public static Mood fromCursor(Cursor cursor) {

        final Mood mood = new Mood();

        mood.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_ID)));
        mood.setMood(cursor.getFloat(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_MOOD)));
        mood.setScope(EMoodScope.fromColumnValue(cursor.getString(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_SCOPE))));
        mood.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_TIMESTAMP)));
        mood.setSyncTimestampNs(cursor.getLong(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_SYNC_TIMESTAMP)));

        return mood;
    }

    /**
     * @param cursor    cursor pointing to a row
     * @return the cells(number id,number mood,string scope,string timestamp) of this row as csv. Separator is , and string delimiter is '.
     */
    public static String csvFromCursor(Cursor cursor) {

        String csvRow = "";

        csvRow += cursor.getLong(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_ID))+",";
        csvRow += cursor.getFloat(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_MOOD))+",";
        csvRow += "'"+EMoodScope.fromColumnValue(cursor.getString(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_SCOPE)))+"',";
        csvRow += cursor.getLong(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_TIMESTAMP))+",";
        csvRow += cursor.getLong(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_SYNC_TIMESTAMP))+'\n';

        return csvRow;
    }

    public static Mood fromCsv(String csvRow) {

        Mood mood;

        String[] cells = csvRow.split(",(?=([^']*'[^']*')*[^']*$)");

        cells[2] = cells[2].substring(1, cells[2].length()-1);
        if(cells[3].contains("'"))
            cells[3] = cells[3].substring(1, cells[3].length()-1);
        if(cells.length >= 5)
            cells[4] = cells[4].substring(1, cells[4].length() - 1);

        Logger.verbose(TAG, "Cells: "+ Arrays.toString(cells));

        mood = new Mood();
        mood.setId(Long.valueOf(cells[0]));
        mood.setMood(Float.valueOf(cells[1]));
        mood.setScope(EMoodScope.fromColumnValue(cells[2]));
        mood.setTimestamp(Long.valueOf(cells[3]));
        if(cells.length >= 5)
            mood.setSyncTimestampNs(Long.valueOf(cells[4]));

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
    public enum EMoodScope {

        RAW("RAW"),
        QUARTER_DAY("QUARTER_DAY"),
        DAY("DAY"),
        WEEK("WEEK"),
        MONTH("MONTH");

        private String columnValue;

        /**
         * @param columnValue the string stored in {@link at.ameise.moodtracker.app.domain.MoodTableHelper#COL_SCOPE}
         */
        EMoodScope(String columnValue) {

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
