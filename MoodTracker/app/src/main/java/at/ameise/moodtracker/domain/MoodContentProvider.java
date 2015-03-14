package at.ameise.moodtracker.domain;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import at.ameise.moodtracker.ITag;
import at.ameise.moodtracker.util.CalendarUtil;

public class MoodContentProvider extends ContentProvider {

    // database
    private DatabaseHelper database;

    private static final String AUTHORITY = "at.ameise.moodtracker.contentprovider";

    // used for the UriMacher ids
    private static final int MOODS = 0x001;
    private static final int MOOD_ID = 0x002;
    private static final int MOOD_DATE = 0x003;
    private static final int MOOD_DATE_RANGE = 0x004;
    private static final int MOOD_DATE_GB_DAY = 0x005;
    private static final int MOOD_DATE_GB_WEEK = 0x006;
    private static final int MOOD_DATE_GB_MONTH = 0x007;

    private static final String BASE_PATH_MOOD = "mood";

    public static final Uri CONTENT_URI_MOOD = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_MOOD);

    public static final Uri getCONTENT_URI_MOOD_ID(long id) {
        return Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_MOOD + "/" + id);
    }

    public static final Uri getCONTENT_URI_MOOD_DATE(Calendar timestamp) {
        return Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_MOOD + "/" + "date/" + timestamp.getTimeInMillis() / 1000);
    }

    public static Uri getCONTENT_URI_MOOD_DATE_RANGE(Calendar fromTimestamp, Calendar toTimestamp) {
        return Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_MOOD + "/" + "date/from/" + fromTimestamp.getTimeInMillis()/1000 +"/to/"+toTimestamp.getTimeInMillis()/1000);
    }

    public static final Uri CONTENT_URI_MOOD_GB_DAY = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_MOOD+ "/gbday");
    public static final Uri CONTENT_URI_MOOD_GB_WEEK = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_MOOD+ "/gbweek");
    public static final Uri CONTENT_URI_MOOD_GB_MONTH = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_MOOD+ "/gbmonth");



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
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MOOD + "/#", MOOD_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MOOD + "/date/#", MOOD_DATE);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MOOD + "/date/from/#/to/#", MOOD_DATE_RANGE);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MOOD + "/gbday", MOOD_DATE_GB_DAY);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MOOD + "/gbweek", MOOD_DATE_GB_WEEK);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MOOD + "/gbmonth", MOOD_DATE_GB_MONTH);

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

        int rowsDeleted = 0;

        switch (uriType) {

            case MOODS:
                rowsDeleted = sqlDb.delete(MoodTableHelper.TABLE_NAME, selection, selectionArgs);
                break;

            case MOOD_ID:
                final String moodId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDb.delete(MoodTableHelper.TABLE_NAME, //
                            MoodTableHelper.COL_ID + " = " + moodId, null);
                } else {
                    rowsDeleted = sqlDb.delete(MoodTableHelper.TABLE_NAME, //
                            MoodTableHelper.COL_ID + " = " + moodId + " AND " + selection, selectionArgs);
                }
                break;

            case MOOD_DATE:
                final String timestamp = uri.getLastPathSegment();
                rowsDeleted = sqlDb.delete(MoodTableHelper.TABLE_NAME, //
                        MoodTableHelper.COL_TIMESTAMP + " = " + timestamp, null);
                break;

            case MOOD_DATE_RANGE:
                final List<String> segments = uri.getPathSegments();
                final String fromTimestamp = segments.get(1);
                final String toTimestamp = segments.get(2);
                rowsDeleted = sqlDb.delete(MoodTableHelper.TABLE_NAME, //
                        fromTimestamp +" <= "+ MoodTableHelper.COL_TIMESTAMP + " AND "+ MoodTableHelper.COL_TIMESTAMP + " <= " + toTimestamp, null);
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
        Uri returnUri = null;

        long id = 0;

        switch (uriType) {

            case MOODS:
                id = sqlDB.insert(MoodTableHelper.TABLE_NAME, null, values);
                returnUri = Uri.parse(BASE_PATH_MOOD + "/" + id);
                break;

            case MOOD_ID:
            case MOOD_DATE:
            case MOOD_DATE_RANGE:
                throw new IllegalArgumentException("URI (" + uri + ") not implemented, because it makes no sense!");

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase sqlDB = database.getWritableDatabase();
        final int uriType = sURIMatcher.match(uri);

        int rowsUpdated = 0;

        switch (uriType) {

            case MOODS:
            case MOOD_ID:
            case MOOD_DATE:
            case MOOD_DATE_RANGE:
                throw new IllegalArgumentException("URI (" + uri + ") not implemented, because it makes no sense!");

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        //getContext().getContentResolver().notifyChange(Uri.EMPTY, null);

        //return rowsUpdated;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = database.getWritableDatabase();
        final int uriType = sURIMatcher.match(uri);
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String unionQueryString = null;

        switch (uriType) {

            case MOODS:
                checkCourseColumns(projection);
                queryBuilder.setTables(MoodTableHelper.TABLE_NAME);
                break;

            case MOOD_ID:
                checkCourseColumns(projection);
                queryBuilder.setTables(MoodTableHelper.TABLE_NAME);
                queryBuilder.appendWhere(MoodTableHelper.COL_ID + " = " + uri.getLastPathSegment());
                break;

            case MOOD_DATE:
                queryBuilder.setTables(MoodTableHelper.TABLE_NAME);
                final String timestamp = uri.getLastPathSegment();
                queryBuilder.appendWhere(MoodTableHelper.COL_TIMESTAMP + " = " + timestamp);
                break;

            case MOOD_DATE_RANGE:
                final List<String> segments = uri.getPathSegments();
                final String fromTimestamp = segments.get(1);
                final String toTimestamp = segments.get(2);
                queryBuilder.setTables(MoodTableHelper.TABLE_NAME);
                queryBuilder.appendWhere(fromTimestamp + " <= " + MoodTableHelper.COL_TIMESTAMP + " AND " +MoodTableHelper.COL_TIMESTAMP + " <= " + toTimestamp);
                break;

            case MOOD_DATE_GB_DAY:

                queryBuilder.setTables(MoodTableHelper.TABLE_NAME);

                String[] unionQueries = getGroupByDayUnionQuery(db);
                Log.v(ITag.TAG_MOOD_PROVIDER, "# of days: "+unionQueries.length);

                unionQueryString = queryBuilder.buildUnionQuery(unionQueries, null, null);
                break;

            case MOOD_DATE_GB_WEEK:

                queryBuilder.setTables(MoodTableHelper.TABLE_NAME);

                unionQueries = getGroupByWeekUnionQuery(db);
                Log.v(ITag.TAG_MOOD_PROVIDER, "# of weeks: " + unionQueries.length);

                unionQueryString = queryBuilder.buildUnionQuery(unionQueries, null, null);
                break;

            case MOOD_DATE_GB_MONTH:

                queryBuilder.setTables(MoodTableHelper.TABLE_NAME);

                unionQueries = getGroupByMonthUnionQuery(db);
                Log.v(ITag.TAG_MOOD_PROVIDER, "# of months: " + unionQueries.length);

                unionQueryString = queryBuilder.buildUnionQuery(unionQueries, null, null);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor c = null;

        if(unionQueryString == null)
            c = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        else
            c = db.rawQuery(unionQueryString, new String[0]);

        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    /**
     * @param database
     * @return returns a {@link java.lang.String[]} containing a select of the avg mood of each month.
     */
    private String[] getGroupByMonthUnionQuery(SQLiteDatabase database) {

        final List<String> unionQueries = new ArrayList<>();
        final Cursor c = MoodCursorHelper.getAllTimestamps(database);
        final Calendar fromDate = getFirstDate(c);
        final Calendar maxDate = getLastDate(c);

        Log.v(ITag.TAG_MOOD_PROVIDER, "#dates: " + c.getCount());

        CalendarUtil.setStartOfMonth(fromDate);
        CalendarUtil.setEndOfMonth(maxDate);
        Log.v(ITag.TAG_MOOD_PROVIDER, "initial fromDate: " + fromDate.toString());
        Log.v(ITag.TAG_MOOD_PROVIDER, "maxDate: " + maxDate.toString());


        while(fromDate.before(maxDate)) {

            final long startOfDaySec = fromDate.getTimeInMillis() / 1000;
            CalendarUtil.setEndOfMonth(fromDate);
            final long endOfDaySec = fromDate.getTimeInMillis() / 1000;

            unionQueries.add(avgMoodSelectQueryString(startOfDaySec, endOfDaySec));

            fromDate.add(Calendar.MILLISECOND, 1);
            Log.v(ITag.TAG_MOOD_PROVIDER, "next fromDate: " + fromDate.toString());
        }

        return unionQueries.toArray(new String[unionQueries.size()]);
    }


    /**
     * @param database
     * @return returns a {@link java.lang.String[]} containing a select of the avg mood of each week.
     */
    private static String[] getGroupByWeekUnionQuery(SQLiteDatabase database) {

        final List<String> unionQueries = new ArrayList<>();
        final Cursor c = MoodCursorHelper.getAllTimestamps(database);
        final Calendar fromDate = getFirstDate(c);
        final Calendar maxDate = getLastDate(c);

        Log.v(ITag.TAG_MOOD_PROVIDER, "#dates: " + c.getCount());

        CalendarUtil.setStartOfWeek(fromDate);
        CalendarUtil.setEndOfWeek(maxDate);
        Log.v(ITag.TAG_MOOD_PROVIDER, "initial fromDate: " + fromDate.toString());
        Log.v(ITag.TAG_MOOD_PROVIDER, "maxDate: " + maxDate.toString());


        while(fromDate.before(maxDate)) {

            final long startOfDaySec = fromDate.getTimeInMillis() / 1000;
            CalendarUtil.setEndOfWeek(fromDate);
            final long endOfDaySec = fromDate.getTimeInMillis() / 1000;

            unionQueries.add(avgMoodSelectQueryString(startOfDaySec, endOfDaySec));

            fromDate.add(Calendar.MILLISECOND, 1);
            Log.v(ITag.TAG_MOOD_PROVIDER, "next fromDate: " + fromDate.toString());
        }

        return unionQueries.toArray(new String[unionQueries.size()]);
    }

    /**
     * @param database
     * @return returns a {@link java.lang.String[]} containing a select of the avg mood of each day.
     */
    private static String[] getGroupByDayUnionQuery(SQLiteDatabase database) {

        final List<String> unionQueries = new ArrayList<>();
        final Cursor c = MoodCursorHelper.getAllTimestamps(database);
        final Calendar fromDate = getFirstDate(c);
        final Calendar maxDate = getLastDate(c);

        Log.v(ITag.TAG_MOOD_PROVIDER, "#dates: " + c.getCount());

        CalendarUtil.setStartOfDay(fromDate);
        CalendarUtil.setEndOfDay(maxDate);
        Log.v(ITag.TAG_MOOD_PROVIDER, "initial fromDate: " + fromDate.toString());
        Log.v(ITag.TAG_MOOD_PROVIDER, "maxDate: " + maxDate.toString());


        while(fromDate.before(maxDate)) {

            final long startOfDaySec = fromDate.getTimeInMillis() / 1000;
            CalendarUtil.setEndOfDay(fromDate);
            final long endOfDaySec = fromDate.getTimeInMillis() / 1000;

            unionQueries.add(avgMoodSelectQueryString(startOfDaySec, endOfDaySec));

            fromDate.add(Calendar.MILLISECOND, 1);
            Log.v(ITag.TAG_MOOD_PROVIDER, "next fromDate: " + fromDate.toString());
        }

        return unionQueries.toArray(new String[unionQueries.size()]);
    }

    /**
     * @param startOfDateSec the unix timestamp of the date in seconds.
     * @param endOfDateSec the unix timestamp of the date in seconds.
     * @return the select to get the average mood between two dates.
     */
    private static String avgMoodSelectQueryString(long startOfDateSec, long endOfDateSec) {

        return "SELECT 0 as "+MoodTableHelper.COL_ID+", avg("+MoodTableHelper.COL_MOOD+") as "+MoodTableHelper.COL_MOOD+", '"+startOfDateSec+"' as "+MoodTableHelper.COL_TIMESTAMP+" "
                +"FROM "+MoodTableHelper.TABLE_NAME+" "
                +"WHERE "+startOfDateSec+" <= "+MoodTableHelper.COL_TIMESTAMP+" AND "+MoodTableHelper.COL_TIMESTAMP+" <= "+endOfDateSec;
    }

    /**
     * NOTE: The cursor must contain the column {@link at.ameise.moodtracker.domain.MoodTableHelper#COL_TIMESTAMP}!
     * @param cursor
     * @return the first date of the {@link android.database.Cursor}. If the cursor is empty, {@link java.util.Calendar#getInstance()} is returned.
     */
    private static Calendar getFirstDate(Cursor cursor) {

        final Calendar firstDate = Calendar.getInstance();

        if(cursor.moveToFirst()) {
            firstDate.setTimeInMillis(1000 * Long.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_TIMESTAMP))));
        }

        return firstDate;
    }

    /**
     * NOTE: The cursor must contain the column {@link at.ameise.moodtracker.domain.MoodTableHelper#COL_TIMESTAMP}!
     * @param cursor
     * @return the last date of the {@link android.database.Cursor}. If the cursor is empty, {@link java.util.Calendar#getInstance()} is returned.
     */
    private static Calendar getLastDate(Cursor cursor) {

        final Calendar lastDate = Calendar.getInstance();

        if(cursor.moveToLast()) {
            lastDate.setTimeInMillis(1000 * Long.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MoodTableHelper.COL_TIMESTAMP))));
        }

        return lastDate;
    }


    /**
     * Checks if the projection only uses the available columns.
     *
     * @param projection
     */
    private static void checkCourseColumns(String[] projection) {

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(MoodTableHelper.ALL_COLUMNS));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

}
