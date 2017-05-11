package hss.quickpools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by Kat on 4/27/2015.
 */
public class BoutDbAdapter {

    //Declare column names/standards
    public static final String KEY_ROWID = "_id";
    public static final String KEY_FENCERA = "fencerA";
    public static final String KEY_FENCERB = "fencerB";
    public static final String KEY_WINNER = "winner";
    public static final String KEY_CARDA = "cardA";
    public static final String KEY_CARDB = "cardB";
    public static final String KEY_ASCORE = "ascore";
    public static final String KEY_BSCORE = "bscore";
    public static final String KEY_TIMELEFT = "timeleft";
    public static final String KEY_DATE = "date";
    public static final String KEY_LEFTY = "lefty";
    public static final String KEY_isDE = "de";



    //Setup Table Data
    private static final String TAG = "BoutDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DATABASE_NAME = "boutData";
    private static final String DATABASE_TABLE = "bouts";

    //Creation statement
    private static final String DATABASE_CREATE =
            "create table bouts (_id integer primary key autoincrement, "
                    + "fencerA text not null, fencerB text not null, winner text not null," +
                    "cardA text, cardB text, ascore text, bscore text," +
                    "timeleft text, date text, lefty integer, de integer);";

    //version
    private static final int DATABASE_VERSION = 3;

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    private final Context mCtx;

    public BoutDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            //create table
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            String alter = "ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " +
                    KEY_isDE + " integer DEFAULT 0";

            //add new column
            db.execSQL(alter);

        }
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws java.sql.SQLException if the database could be neither opened or created
     */
    public BoutDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    //Create a Bout
    public long addBout(String fencerA, String fencerB, String winner,
                        String cardA, String cardB, String a_score, String b_score,
                        String time_left, String date, int hasLefty, Boolean isDE) {

        //initalize
        ContentValues initialValues = new ContentValues();

        initialValues.put(KEY_FENCERA, fencerA);
        initialValues.put(KEY_FENCERB, fencerB);
        initialValues.put(KEY_WINNER, winner);
        initialValues.put(KEY_CARDA, cardA);
        initialValues.put(KEY_CARDB, cardB);
        initialValues.put(KEY_ASCORE, a_score);
        initialValues.put(KEY_BSCORE, b_score);
        initialValues.put(KEY_TIMELEFT, time_left);
        initialValues.put(KEY_LEFTY, hasLefty);
        initialValues.put(KEY_DATE, date);

        if (isDE == true) initialValues.put(KEY_isDE, 1);
        else initialValues.put(KEY_isDE, 0);

        //insert into database
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }


    public boolean deleteBout(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    public Cursor fetchAllBouts() {

        return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FENCERA, KEY_FENCERB,
                KEY_WINNER, KEY_CARDA, KEY_CARDB, KEY_ASCORE, KEY_BSCORE,
                KEY_TIMELEFT, KEY_DATE, KEY_LEFTY}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @return Cursor positioned to matching note, if found
     * @throws java.sql.SQLException if note could not be found/retrieved
     */
    public Cursor fetchAllPastPoolBoutsPair(String FencerA, String FencerB) throws SQLException {

        String orderBy = KEY_ROWID + " DESC LIMIT 60";

        Cursor mCursor = mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FENCERA,
                        KEY_FENCERB, KEY_WINNER, KEY_CARDA, KEY_CARDB, KEY_ASCORE, KEY_BSCORE,
                        KEY_TIMELEFT, KEY_DATE, KEY_LEFTY, KEY_isDE},
                "(" + KEY_FENCERA + "=? AND " + KEY_FENCERB + "=? ) OR ( " +
                        KEY_FENCERA + "=? AND " + KEY_FENCERB + "=? ) AND (" + KEY_isDE + "=?)",
                new String[]{FencerA, FencerB, FencerB, FencerA, "0"}, null, null, orderBy);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetchAllPastDEBoutsPair(String FencerA, String FencerB) throws SQLException {

        String orderBy = KEY_ROWID + " DESC LIMIT 60";

        Cursor mCursor = mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FENCERA,
                        KEY_FENCERB, KEY_WINNER, KEY_CARDA, KEY_CARDB, KEY_ASCORE, KEY_BSCORE,
                        KEY_TIMELEFT, KEY_DATE, KEY_LEFTY, KEY_isDE},
                "(" + KEY_FENCERA + "=? AND " + KEY_FENCERB + "=? ) OR ( " +
                        KEY_FENCERA + "=? AND " + KEY_FENCERB + "=? ) AND (" + KEY_isDE + "=?)",
                new String[]{FencerA, FencerB, FencerB, FencerA, "1"}, null, null, orderBy);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }


    public Cursor fetchAllPastPoolBouts(String Fencer) throws SQLException {

        String orderBy = KEY_ROWID + " DESC LIMIT 60";

        Cursor mCursor = mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FENCERA,
                        KEY_FENCERB, KEY_WINNER, KEY_CARDA, KEY_CARDB, KEY_ASCORE, KEY_BSCORE,
                        KEY_TIMELEFT, KEY_DATE, KEY_LEFTY, KEY_isDE},
                "(" + KEY_FENCERA + "=? OR " + KEY_FENCERB + "=? ) AND (" + KEY_isDE + "=?)",
                new String[]{Fencer, Fencer, "0"}, null, null, orderBy);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }


    public Cursor fetchAllPastDEBouts(String Fencer) throws SQLException {

        String orderBy = KEY_ROWID + " DESC LIMIT 60";

        Cursor mCursor = mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FENCERA,
                        KEY_FENCERB, KEY_WINNER, KEY_CARDA, KEY_CARDB, KEY_ASCORE, KEY_BSCORE,
                        KEY_TIMELEFT, KEY_DATE, KEY_LEFTY, KEY_isDE},
                "(" + KEY_FENCERA + "=? OR " + KEY_FENCERB + "=? ) AND " + KEY_isDE + "=?",
                new String[]{Fencer, Fencer, "1"}, null, null, orderBy);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public void deleteFencerHistory(String FencerName) {
        // Delete Rows with fencer name in them
        mDb.delete(DATABASE_TABLE, KEY_FENCERA + "=?", new String[]{FencerName});
        mDb.delete(DATABASE_TABLE, KEY_FENCERB + "=?", new String[]{FencerName});
    }


    public void resetTable() {
        mDb.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        mDbHelper.onCreate(mDb);
    }


}
