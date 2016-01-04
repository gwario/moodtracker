package at.ameise.moodtracker.app.domain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import at.ameise.moodtracker.app.TagConstant;
import at.ameise.moodtracker.app.util.Logger;

/**
 * Contains methods to create, delete or upgrade the database.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 14.02.15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "mood.db";

    static final int DATABASE_VERSION = 9;

    private static DatabaseHelper INSTANCE = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * @param context
     * @return a singleton instance of {@link DatabaseHelper}.
     */
    public static DatabaseHelper getInstance(Context context) {

        if (INSTANCE == null)
            INSTANCE = new DatabaseHelper(context);

        return INSTANCE;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(MoodTableHelper.STMT_CREATE);
    }

    private void onDrop(SQLiteDatabase db) {

        db.execSQL(createStmtDrop(MoodTableHelper.TABLE_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Logger.info(TagConstant.DATABASE, "Upgrading mood table from version " + oldVersion + " to version " + newVersion);

        switch (oldVersion) {
            case 0:
            case 1:
                changeMoodTypeToReal(1, db);
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                addScopeColumn(6, db);
            case 7:
                removeMyScopeColumn(7, db);
            case 8:
                changeTimestampTypeToLong(8, db);
            case 9:
                addSyncTimestampColumn(9, db);
                break;

            default:
                throw new AssertionError("Unhandled database version migration from "+oldVersion+" to "+newVersion+"!");
        }

        //onDrop(db);
        //onCreate(db);
    }

    /**
     * Adds a new column to the mood table to support synchronization with the backend.
     * @param targetVersion the version we are upgrading to.
     * @param db the database
     */
    private void addSyncTimestampColumn(int targetVersion, SQLiteDatabase db) {

        Logger.info(TagConstant.DATABASE, "Upgrading table '" + MoodTableHelper.TABLE_NAME + "': Adding column '" + MoodTableHelper.COL_SYNC_TIMESTAMP + "'...");

        db.execSQL("ALTER TABLE "+MoodTableHelper.TABLE_NAME+" ADD COLUMN "+MoodTableHelper.COL_SYNC_TIMESTAMP+" INTEGER DEFAULT NULL");
    }

    /**
     * Creates new table with the correct column types, migrates the values, deletes the original table and renames the new table.
     * @param targetVersion the version we are upgrading to.
     * @param db the database
     */
    private void changeTimestampTypeToLong(int targetVersion, SQLiteDatabase db) {

        Logger.info(TagConstant.DATABASE, "Upgrading table '"+MoodTableHelper.TABLE_NAME+"': Changing type of column '"+MoodTableHelper.COL_TIMESTAMP+"' from string to number...");

        final String MIGRATION_TABLE = "migrate_mood";

        //Create new schema, this ensures, that in when upgrading from ie v8 to v10, the right create for the upgrade from v8 to v9 is preserved.
        db.execSQL(MoodTableHelper.createStmtCreate(targetVersion, MIGRATION_TABLE));

        //move to new table(with conversion)
        db.execSQL("INSERT INTO "+MIGRATION_TABLE+" SELECT "+MoodTableHelper.COL_ID+", CAST("+MoodTableHelper.COL_TIMESTAMP+" AS INTEGER)"+", "+MoodTableHelper.COL_MOOD+", "+MoodTableHelper.COL_SCOPE+" FROM "+MoodTableHelper.TABLE_NAME);
        //drop old table
        db.execSQL(createStmtDrop(MoodTableHelper.TABLE_NAME));

        //rename new table
        db.execSQL(createStmtRename(MIGRATION_TABLE, MoodTableHelper.TABLE_NAME));
    }

    /**
     * Removes the strange myscope column.
     * @param targetVersion the version we are upgrading to.
     * @param db the database
     */
    private void removeMyScopeColumn(int targetVersion, SQLiteDatabase db) {

        Logger.info(TagConstant.DATABASE, "Upgrading table '"+MoodTableHelper.TABLE_NAME+"': Removing column 'myscope'...");

        final String MIGRATION_TABLE = "migrate_mood";
        //create new table
        db.execSQL(MoodTableHelper.createStmtCreate(targetVersion, MIGRATION_TABLE));

        //move to new table(with conversion)
        db.execSQL("INSERT INTO "+MIGRATION_TABLE+" SELECT "+MoodTableHelper.COL_ID+","+MoodTableHelper.COL_TIMESTAMP+","+MoodTableHelper.COL_MOOD+","+MoodTableHelper.COL_SCOPE+" FROM "+MoodTableHelper.TABLE_NAME);
        //drop old table
        db.execSQL(createStmtDrop(MoodTableHelper.TABLE_NAME));

        //rename new table
        db.execSQL(createStmtRename(MIGRATION_TABLE, MoodTableHelper.TABLE_NAME));
    }

    /**
     * Adds a new column to the mood table to allow avg values.
     * @param targetVersion the version we are upgrading to.
     * @param db the database
     */
    private void addScopeColumn(int targetVersion, SQLiteDatabase db) {

        Logger.info(TagConstant.DATABASE, "Upgrading table '"+MoodTableHelper.TABLE_NAME+"': Adding column '"+MoodTableHelper.COL_SCOPE+"'...");

        db.execSQL("ALTER TABLE "+MoodTableHelper.TABLE_NAME+" ADD COLUMN "+MoodTableHelper.COL_SCOPE+" TEXT NOT NULL DEFAULT '"+ MoodTableHelper.EMoodScope.RAW.getColumnValue()+"'");
    }

    /**
     * Creates new table with the correct column types, migrates the values, deletes the original table and renames the new table.
     * @param targetVersion the version we are upgrading to.
     * @param db the database
     */
    private void changeMoodTypeToReal(int targetVersion, SQLiteDatabase db) {

        Logger.info(TagConstant.DATABASE, "Upgrading table '"+MoodTableHelper.TABLE_NAME+"': Changing type of column '"+MoodTableHelper.COL_MOOD+"' from integer to real...");

        final String MIGRATION_TABLE = "migrate_mood";
        //create new table
        db.execSQL(MoodTableHelper.createStmtCreate(targetVersion, MIGRATION_TABLE));

        //move to new table(with conversion)
        db.execSQL("INSERT INTO "+MIGRATION_TABLE+" SELECT * FROM "+MoodTableHelper.TABLE_NAME);
        //drop old table
        db.execSQL(createStmtDrop(MoodTableHelper.TABLE_NAME));

        //rename new table
        db.execSQL(createStmtRename(MIGRATION_TABLE, MoodTableHelper.TABLE_NAME));
    }

    private static String createStmtRename(String fromTableName, String toTableName) {

        return "ALTER TABLE "+fromTableName+" RENAME TO "+toTableName;
    }

    /**
     * @param tableName the name of the table.
     * @return a drop statement for the specified table name.
     */
    private static String createStmtDrop(String tableName) {

        return "DROP TABLE "+tableName;
    }
}
