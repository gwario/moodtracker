package at.ameise.moodtracker.domain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 14.02.15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "mood.db";

    private static final int DATABASE_VERSION = 1;


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

        db.execSQL(MoodTableHelper.STMT_DROP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onDrop(db);
        onCreate(db);
    }
}
