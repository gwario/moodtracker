package at.ameise.moodtracker.domain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import at.ameise.moodtracker.ITag;
import at.ameise.moodtracker.util.Logger;

/**
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 14.02.15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "mood.db";

    private static final int DATABASE_VERSION = 2;


    private static DatabaseHelper INSTANCE = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * @param context
     * @return a singleton instance of {@link DatabaseHelper}.
     */
    public static final DatabaseHelper getInstance(Context context) {

        if (INSTANCE == null)
            INSTANCE = new DatabaseHelper(context);

        return INSTANCE;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(MoodTableHelper.STMT_CREATE);
    }

    private void onDrop(SQLiteDatabase db) {

        db.execSQL(createDropStatement(MoodTableHelper.TABLE_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(oldVersion == 1) {

            Logger.info(ITag.DATABASE, "Upgrading mood table from version 1");
            changeMoodTypeToReal(db);
        }
        //onDrop(db);
        //onCreate(db);
    }

    /**
     * Creates new table with the correct column types, migrates the values, deletes the original table and renames the new table.
     * @param db
     */
    private void changeMoodTypeToReal(SQLiteDatabase db) {

        final String MIGRATION_TABLE = "migrate_mood";
        //create new table
        db.execSQL(MoodTableHelper.createStmtCreate(MIGRATION_TABLE));

        //move to new table(with conversion)
        db.execSQL("INSERT INTO "+MIGRATION_TABLE+" SELECT * FROM "+MoodTableHelper.TABLE_NAME);
        //drop old table
        db.execSQL(createDropStatement(MoodTableHelper.TABLE_NAME));

        //rename new table
        db.execSQL(createRenameStatement(MIGRATION_TABLE, MoodTableHelper.TABLE_NAME));
    }

    private static final String createRenameStatement(String fromTableName, String toTableName) {

        return "ALTER TABLE "+fromTableName+" RENAME TO "+toTableName;
    }

    /**
     * @param tableName
     * @return a drop statement for the specified table name.
     */
    private static final String createDropStatement(String tableName) {

        return "DROP TABLE "+tableName;
    }
}
