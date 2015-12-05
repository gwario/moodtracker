package at.ameise.moodtracker.domain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import at.ameise.moodtracker.ITag;
import at.ameise.moodtracker.util.Logger;

/**
 * Contains methods to create, delete or upgrade the database.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 14.02.15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "mood.db";

    private static final int DATABASE_VERSION = 8;


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

        Logger.info(ITag.DATABASE, "Upgrading mood table from version " + oldVersion + " to version " + newVersion);

        if(oldVersion <= 1) {

            changeMoodTypeToReal(db);
        }

        if(oldVersion <= 6) {

            addScopeColumn(db);
        }

        if(oldVersion <= 7) {

            removeMyScopeColumn(db);
        }

        if(oldVersion <= 8) {

            changeTimestampTypeToLong(db);
        }

        //onDrop(db);
        //onCreate(db);
    }

    /**
     * Creates new table with the correct column types, migrates the values, deletes the original table and renames the new table.
     * @param db
     */
    private void changeTimestampTypeToLong(SQLiteDatabase db) {

        Logger.info(ITag.DATABASE, "Upgrading table '"+MoodTableHelper.TABLE_NAME+"': Changing type of column '"+MoodTableHelper.COL_TIMESTAMP+"' from string to number...");

        final String MIGRATION_TABLE = "migrate_mood";
        //create new table
        db.execSQL(MoodTableHelper.createStmtCreate(MIGRATION_TABLE));

        //move to new table(with conversion)
        db.execSQL("INSERT INTO "+MIGRATION_TABLE+" SELECT "+MoodTableHelper.COL_ID+", CAST("+MoodTableHelper.COL_TIMESTAMP+" AS INTEGER)"+", "+MoodTableHelper.COL_MOOD+", "+MoodTableHelper.COL_SCOPE+" FROM "+MoodTableHelper.TABLE_NAME);
        //drop old table
        db.execSQL(createStmtDrop(MoodTableHelper.TABLE_NAME));

        //rename new table
        db.execSQL(createStmtRename(MIGRATION_TABLE, MoodTableHelper.TABLE_NAME));
    }

    /**
     * Removes the strange myscope column.
     * @param db
     */
    private void removeMyScopeColumn(SQLiteDatabase db) {

        Logger.info(ITag.DATABASE, "Upgrading table '"+MoodTableHelper.TABLE_NAME+"': Removing column 'myscope'...");

        final String MIGRATION_TABLE = "migrate_mood";
        //create new table
        db.execSQL(MoodTableHelper.createStmtCreate(MIGRATION_TABLE));

        //move to new table(with conversion)
        db.execSQL("INSERT INTO "+MIGRATION_TABLE+" SELECT "+MoodTableHelper.COL_ID+","+MoodTableHelper.COL_TIMESTAMP+","+MoodTableHelper.COL_MOOD+","+MoodTableHelper.COL_SCOPE+" FROM "+MoodTableHelper.TABLE_NAME);
        //drop old table
        db.execSQL(createStmtDrop(MoodTableHelper.TABLE_NAME));

        //rename new table
        db.execSQL(createStmtRename(MIGRATION_TABLE, MoodTableHelper.TABLE_NAME));
    }

    /**
     * Adds a new column to the mood table to allow avg values.
     * @param db
     */
    private void addScopeColumn(SQLiteDatabase db) {

        Logger.info(ITag.DATABASE, "Upgrading table '"+MoodTableHelper.TABLE_NAME+"': Adding column '"+MoodTableHelper.COL_SCOPE+"'...");

        db.execSQL("ALTER TABLE "+MoodTableHelper.TABLE_NAME+" ADD COLUMN "+MoodTableHelper.COL_SCOPE+" NOT NULL DEFAULT '"+ MoodTableHelper.EMoodScope.RAW.getColumnValue()+"'");
    }

    /**
     * Creates new table with the correct column types, migrates the values, deletes the original table and renames the new table.
     * @param db
     */
    private void changeMoodTypeToReal(SQLiteDatabase db) {

        Logger.info(ITag.DATABASE, "Upgrading table '"+MoodTableHelper.TABLE_NAME+"': Changing type of column '"+MoodTableHelper.COL_MOOD+"' from integer to real...");

        final String MIGRATION_TABLE = "migrate_mood";
        //create new table
        db.execSQL(MoodTableHelper.createStmtCreate(MIGRATION_TABLE));

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
     * @param tableName
     * @return a drop statement for the specified table name.
     */
    private static String createStmtDrop(String tableName) {

        return "DROP TABLE "+tableName;
    }
}
