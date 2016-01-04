package at.ameise.moodtracker.app.domain;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Content provider for mood data.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com>.
 */
public class MoodContentProvider extends ContentProvider {

    // database
    private DatabaseHelper database;

    private static final String AUTHORITY = "at.ameise.moodtracker.contentprovider";

    // used for the UriMacher ids
    private static final int MOODS = 0x000;

    private static final int MOOD_AVG_DATE_RANGE_SCOPE = 0x010;
    private static final int MOOD_AVG_QUARTER_DAY = 0x011;
    private static final int MOOD_AVG_DAY = 0x012;
    private static final int MOOD_AVG_WEEK = 0x013;
    private static final int MOOD_AVG_MONTH = 0x014;

    private static final String BASE_PATH_MOOD = "mood";

    /**
     * NOTE: Selection, SelectionArgs and Order are ignored!
     * @param fromTimestamp inclusive start date
     * @param toTimestamp inclusive end date
     * @param sourceScope the scope from which the values for the average should be taken.
     * @return content uri for this query; the resulting cursor contains a mood with id = 0, timestamp = fromTimestamp, the average mood and scope = RAW
     */
    public static Uri getCONTENT_URI_AVG_MOOD_DATE_RANGE_SCOPE(DateTime fromTimestamp, DateTime toTimestamp, MoodTableHelper.EMoodScope sourceScope) {
        return Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_MOOD + "/avg/" + "date?from=" + fromTimestamp.getMillis() +"&to="+toTimestamp.getMillis()+"&source_scope="+sourceScope.getColumnValue());
    }

    public static final Uri CONTENT_URI_MOOD = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_MOOD);
    public static final Uri CONTENT_URI_MOOD_AVG_QUARTER_DAY = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_MOOD+ "/avg/quarterDay");
    public static final Uri CONTENT_URI_MOOD_AVG_DAY = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_MOOD+ "/avg/day");
    public static final Uri CONTENT_URI_MOOD_AVG_WEEK = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_MOOD+ "/avg/week");
    public static final Uri CONTENT_URI_MOOD_AVG_MONTH = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_MOOD+ "/avg/month");



    public static final String CONTENT_TYPE_MOODS = ContentResolver.CURSOR_DIR_BASE_TYPE + "/moods";

    public static final String CONTENT_ITEM_TYPE_MOOD = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mood";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
		/*
		 * Uri to work on all moods
		 */
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MOOD, MOODS);

		/*
		 * Uri to work on on specific moods
		 */
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MOOD + "/avg/quarterDay", MOOD_AVG_QUARTER_DAY);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MOOD + "/avg/day", MOOD_AVG_DAY);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MOOD + "/avg/week", MOOD_AVG_WEEK);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MOOD + "/avg/month", MOOD_AVG_MONTH);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MOOD + "/avg/date", MOOD_AVG_DATE_RANGE_SCOPE);

    }

    @Override
    public boolean onCreate() {
        database = DatabaseHelper.getInstance(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase sqlDb = database.getWritableDatabase();
        final int uriType = sURIMatcher.match(uri);

        int rowsDeleted;

        switch (uriType) {

            case MOODS:
                rowsDeleted = sqlDb.delete(MoodTableHelper.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase sqlDB = database.getWritableDatabase();
        final int uriType = sURIMatcher.match(uri);
        Uri returnUri;

        switch (uriType) {

            case MOODS:
                long id = sqlDB.insert(MoodTableHelper.TABLE_NAME, null, values);
                returnUri = Uri.parse(BASE_PATH_MOOD + "/" + id);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = database.getWritableDatabase();
        final int uriType = sURIMatcher.match(uri);
        int updateCount = -1;

        switch (uriType) {

            case MOODS:
                updateCount = db.update(MoodTableHelper.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = database.getWritableDatabase();
        final int uriType = sURIMatcher.match(uri);
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String rawQueryString = null;

        checkCourseColumns(projection);

        switch (uriType) {

            case MOODS:
                queryBuilder.setTables(MoodTableHelper.TABLE_NAME);
                break;

            case MOOD_AVG_QUARTER_DAY:
                queryBuilder.setTables(MoodTableHelper.TABLE_NAME);
                queryBuilder.appendWhere(MoodTableHelper.COL_SCOPE+" = ");
                queryBuilder.appendWhereEscapeString(MoodTableHelper.EMoodScope.QUARTER_DAY.getColumnValue());
                break;

            case MOOD_AVG_DAY:
                queryBuilder.setTables(MoodTableHelper.TABLE_NAME);
                queryBuilder.appendWhere(MoodTableHelper.COL_SCOPE+" = ");
                queryBuilder.appendWhereEscapeString(MoodTableHelper.EMoodScope.DAY.getColumnValue());
                break;

            case MOOD_AVG_WEEK:
                queryBuilder.setTables(MoodTableHelper.TABLE_NAME);
                queryBuilder.appendWhere(MoodTableHelper.COL_SCOPE+" = ");
                queryBuilder.appendWhereEscapeString(MoodTableHelper.EMoodScope.WEEK.getColumnValue());
                break;

            case MOOD_AVG_MONTH:
                queryBuilder.setTables(MoodTableHelper.TABLE_NAME);
                queryBuilder.appendWhere(MoodTableHelper.COL_SCOPE+" = ");
                queryBuilder.appendWhereEscapeString(MoodTableHelper.EMoodScope.MONTH.getColumnValue());
                break;

            case MOOD_AVG_DATE_RANGE_SCOPE:
                final String fromTimestamp = uri.getQueryParameter("from");
                final String toTimestamp = uri.getQueryParameter("to");
                final String sourceScope = uri.getQueryParameter("source_scope");

                rawQueryString = "SELECT 0 as "+MoodTableHelper.COL_ID+", "
                        +"avg("+MoodTableHelper.COL_MOOD+") as "+MoodTableHelper.COL_MOOD+", "
                        +"'"+fromTimestamp+"' as "+MoodTableHelper.COL_TIMESTAMP+", "
                        +"'"+ MoodTableHelper.EMoodScope.RAW.getColumnValue()+"' as "+MoodTableHelper.COL_SCOPE+" "
                    +"FROM "+MoodTableHelper.TABLE_NAME+" "
                    +"WHERE "+fromTimestamp+" <= "+MoodTableHelper.COL_TIMESTAMP
                        +" AND "+MoodTableHelper.COL_TIMESTAMP+" <= "+toTimestamp
                        +" AND "+MoodTableHelper.COL_SCOPE+" = '"+sourceScope+"'";
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor c;

        if(rawQueryString == null)
            c = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        else
            c = db.rawQuery(rawQueryString, new String[0]);

        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    /**
     * Checks if the projection only uses the available columns.
     *
     * @param projection the projection
     */
    private static void checkCourseColumns(String[] projection) {

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(MoodTableHelper.ALL_COLUMNS));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

}
