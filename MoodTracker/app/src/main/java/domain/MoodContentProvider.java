package domain;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class MoodContentProvider extends ContentProvider {

    // database
    private DatabaseHelper database;

    private static final String AUTHORITY = "at.ameise.moodtracker.contentprovider";

    // used for the UriMacher ids
    private static final int MOODS = 0x001;
    private static final int MOOD_ID = 0x002;
    private static final int MOOD_DATE = 0x003;
    private static final int MOOD_DATE_RANGE = 0x004;

    private static final String BASE_PATH_MOOD = "mood";
    public static final Uri CONTENT_URI_COURSE = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_MOOD);

    public static final Uri getCONTENT_URI_COURSE_STUDENT(long courseId, long studentId) {
        return Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_MOOD + "/" + courseId + "/student/" + studentId);
    }

    public static Uri getCONTENT_URI_COURSE_STUDENTS(long courseId) {
        return Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_MOOD + "/" + courseId + "/students");
    }

    private static final String BASE_PATH_STUDENT = "student";
    public static final Uri CONTENT_URI_STUDENT = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_STUDENT);

    public static final String CONTENT_TYPE_COURSES = ContentResolver.CURSOR_DIR_BASE_TYPE + "/courses";
    public static final String CONTENT_ITEM_TYPE_COURSE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/course";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
		/*
		 * Uri to work on all courses
		 */
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MOOD, MOODS);

		/*
		 * Uri to work on on specific course
		 */
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MOOD + "/#", MOOD_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MOOD + "/#/student/#", MOOD_DATE);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MOOD + "/#/students", MOOD_DATE_RANGE);

		/*
		 * Uri to work on all course student mappings
		 */
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_STUDENT, STUDENTS);

		/*
		 * Uri to work on a specific student
		 */
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_STUDENT + "/#", STUDENT_ID);
    }

    @Override
    public boolean onCreate() {
        database = CoasyDatabaseHelper.getInstance(getContext());
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
                rowsDeleted = sqlDb.delete(CourseTable.TABLE_NAME, selection, selectionArgs);
                break;

            case MOOD_ID:
                String courseId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDb.delete(CourseTable.TABLE_NAME, //
                            CourseTable.COL_ID + " = " + courseId, null);
                } else {
                    rowsDeleted = sqlDb.delete(CourseTable.TABLE_NAME, //
                            CourseTable.COL_ID + " = " + courseId + " AND " + selection, selectionArgs);
                }
                break;

            case MOOD_DATE:
                List<String> segments = uri.getPathSegments();
                courseId = segments.get(1);
                String studentId = segments.get(3);
                rowsDeleted = sqlDb.delete(CourseStudentTable.TABLE_NAME, //
                        CourseStudentTable.COL_COURSE_ID + " = " + courseId + " AND " + CourseStudentTable.COL_STUDENT_ID + " = " + studentId, null);
                break;

            case MOOD_DATE_RANGE:
                courseId = uri.getPathSegments().get(1);
                rowsDeleted = sqlDb.delete(CourseStudentTable.TABLE_NAME, //
                        CourseStudentTable.COL_COURSE_ID + " = " + courseId, null);
                break;

            case STUDENTS:
                rowsDeleted = sqlDb.delete(CourseStudentTable.TABLE_NAME, selection, selectionArgs);
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
                id = sqlDB.insert(CourseTable.TABLE_NAME, null, values);
                returnUri = Uri.parse(BASE_PATH_MOOD + "/" + id);
                break;

            case MOOD_DATE:
                List<String> segments = uri.getPathSegments();
                if (values != null) {
                    values = new ContentValues();
                    String courseId = segments.get(1);
                    String studentId = segments.get(3);
                    values.put(CourseStudentTable.COL_COURSE_ID, courseId);
                    values.put(CourseStudentTable.COL_STUDENT_ID, studentId);
                }
                id = sqlDB.insert(CourseStudentTable.TABLE_NAME, null, values);
                returnUri = Uri.parse(BASE_PATH_MOOD + "/" + id);
                break;

            case STUDENT_ID:
                id = sqlDB.insert(StudentTable.TABLE_NAME, null, values);
                returnUri = Uri.parse(BASE_PATH_STUDENT + "/" + id);
                break;

            case MOOD_ID:
            case MOOD_DATE_RANGE:
            case STUDENTS:
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
                rowsUpdated = sqlDB.update(CourseTable.TABLE_NAME, values, selection, selectionArgs);
                break;

            case MOOD_ID:
                final String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(CourseTable.TABLE_NAME, values, CourseTable.COL_ID + " = " + id, null);
                } else {
                    rowsUpdated = sqlDB.update(CourseTable.TABLE_NAME, values, CourseTable.COL_ID + " = " + id + " AND " + selection, selectionArgs);
                }
                break;

            case MOOD_DATE:
            case MOOD_DATE_RANGE:
            case STUDENTS:
                throw new IllegalArgumentException("URI (" + uri + ") not implemented, because it makes no sense!");
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = database.getWritableDatabase();
        final int uriType = sURIMatcher.match(uri);
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (uriType) {

            case MOODS:
                checkCourseColumns(projection);
                queryBuilder.setTables(CourseTable.TABLE_NAME);
                break;

            case MOOD_ID:
                checkCourseColumns(projection);
                queryBuilder.setTables(CourseTable.TABLE_NAME);
                queryBuilder.appendWhere(CourseTable.COL_ID + " = " + uri.getLastPathSegment());
                break;

            case MOOD_DATE:
                checkCourseStudentColumns(projection);
                queryBuilder.setTables(CourseTable.TABLE_NAME);
                List<String> segments = uri.getPathSegments();
                String courseId = segments.get(1);
                String studentId = segments.get(3);
                queryBuilder.appendWhere(CourseStudentTable.COL_COURSE_ID + " = " + courseId + " AND " + CourseStudentTable.COL_STUDENT_ID + " = " + studentId);
                break;

            case MOOD_DATE_RANGE:
                checkCourseStudentColumns(projection);
                queryBuilder.setTables(CourseStudentTable.TABLE_NAME + " INNER JOIN " + StudentTable.TABLE_NAME + " ON ("//
                        + CourseStudentTable.TABLE_NAME + "." + CourseStudentTable.COL_STUDENT_ID//
                        + " = "//
                        + StudentTable.TABLE_NAME + "." + StudentTable.COL_ID + ")");
                courseId = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(CourseStudentTable.COL_COURSE_ID + " = " + courseId);
                break;

            case STUDENTS:
                checkCourseStudentColumns(projection);
                queryBuilder.setTables(StudentTable.TABLE_NAME);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    /**
     * Checks if the projection only uses the available columns.
     *
     * @param projection
     */
    private void checkCourseColumns(String[] projection) {

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(CourseTable.ALL_COLUMNS));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

    /**
     * Checks if the projection only uses the available columns.
     *
     * @param projection
     */
    private void checkCourseStudentColumns(String[] projection) {

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(CourseStudentTable.ALL_COLUMNS));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }


    public MoodContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
