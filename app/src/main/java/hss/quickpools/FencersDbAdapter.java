package hss.quickpools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by Kat on 4/25/2015.
 */
public class FencersDbAdapter {

    //Declare column names/standards
    public static final String KEY_ROWID = "_id";
    public static final String KEY_FIRSTNAME = "firstName";
    public static final String KEY_LASTNAME = "lastName";
    public static final String KEY_CLUB = "club";
    public static final String KEY_BIRTHYEAR = "birthYear";
    public static final String KEY_WEAPON = "weapon";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_HAND = "hand";
    public static final String KEY_RATING = "rating";
    public static final String KEY_COUNTRY = "country";

    // Pool Stats
    public static final String KEY_TOTALVICT = "totalVictories";
    public static final String KEY_NUMRCARDS = "numRCards";
    public static final String KEY_NUMYCARDS = "numYCards";
    public static final String KEY_AVGBOUTSCORE = "avgBoutScore";
    public static final String KEY_PERVICT = "percentageVictory";
    public static final String KEY_TOTALBOUTS = "totalBouts";

    //DE database columns
    public static final String KEY_TOTALVICT_DE = "totalVictoriesDE";
    public static final String KEY_NUMRCARDS_DE = "numRCardsDE";
    public static final String KEY_NUMYCARDS_DE = "numYCardsDE";
    public static final String KEY_AVGBOUTSCORE_DE = "avgBoutScoreDE";
    public static final String KEY_PERVICT_DE = "percentageVictoryDE";
    public static final String KEY_TOTALBOUTS_DE = "totalBoutsDE";


    //Setup Table Data
    private static final String TAG = "FencersDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DATABASE_NAME = "fencerData";
    private static final String DATABASE_TABLE = "fencers";

    //Creation statement
    private static final String DATABASE_CREATE =
            "create table fencers (_id integer primary key autoincrement, "
                    + "firstName text not null, lastName text not null, club text not null," +
                    "birthYear integer, weapon integer, gender integer, hand integer," +
                    "totalVictories integer, numRCards integer, numYCards integer," +
                    "avgBoutScore text default '0.0', percentageVictory text default '0.0', totalBouts integer, " +
                    "totalVictoriesDE integer, numRCardsDE integer, numYCardsDE integer, " +
                    "avgBoutScoreDE text default '0.0', percentageVictoryDE text default '0.0'," +
                    "totalBoutsDE integer, rating text default 'U', country text);";

    //version
    private static final int DATABASE_VERSION = 9;

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    private final Context mCtx;

    public FencersDbAdapter(Context ctx) {
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

            if (oldVersion < 5) {
                //add new columns
                String alter = "ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " +
                        KEY_TOTALVICT_DE + " integer DEFAULT 0";
                db.execSQL(alter);

                alter = "ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " +
                        KEY_NUMRCARDS_DE + " integer DEFAULT 0";
                db.execSQL(alter);

                alter = "ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " +
                        KEY_NUMYCARDS_DE + " integer DEFAULT 0";
                db.execSQL(alter);

                alter = "ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " +
                        KEY_AVGBOUTSCORE_DE + " text default '0.0'";
                db.execSQL(alter);

                alter = "ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " +
                        KEY_PERVICT_DE + " text default '0.0'";
                db.execSQL(alter);

                alter = "ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " +
                        KEY_TOTALBOUTS_DE + " integer DEFAULT 0";
                db.execSQL(alter);

                // add rating and country
                try {
                    alter = "ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " +
                            KEY_RATING + " text DEFAULT 'U'";
                    db.execSQL(alter);
                    alter = "ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " +
                            KEY_COUNTRY + " text";
                    db.execSQL(alter);
                } catch (Exception e) {

                }

            } else {
                //add new fencer data columns
                try {
                    String alter = "ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " +
                            KEY_RATING + " text DEFAULT 'U'";
                    db.execSQL(alter);
                    alter = "ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " +
                            KEY_COUNTRY + " text";
                    db.execSQL(alter);
                } catch (Exception e) {

                }

            }



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
    public FencersDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    //Create a Fencer
    public long createFencer(String firstName, String lastName,
                             String club, int birthYear, Fencer.Weapon weapon,
                             Fencer.Gender gender,
                             Fencer.Hand hand, String Rating,
                             String Country) {

        //initalize
        ContentValues initialValues = new ContentValues();

        initialValues.put(KEY_FIRSTNAME, firstName);
        initialValues.put(KEY_LASTNAME, lastName);
        initialValues.put(KEY_CLUB, club);
        initialValues.put(KEY_BIRTHYEAR, birthYear);
        initialValues.put(KEY_WEAPON, weapon.ordinal());
        initialValues.put(KEY_GENDER, gender.ordinal());
        initialValues.put(KEY_HAND, hand.ordinal());
        initialValues.put(KEY_TOTALBOUTS, 0);
        initialValues.put(KEY_NUMRCARDS, 0);
        initialValues.put(KEY_NUMYCARDS, 0);
        initialValues.put(KEY_TOTALVICT, 0);
        initialValues.put(KEY_AVGBOUTSCORE, "0.0");
        initialValues.put(KEY_PERVICT, "0.0");
        initialValues.put(KEY_TOTALBOUTS_DE, 0);
        initialValues.put(KEY_NUMRCARDS_DE, 0);
        initialValues.put(KEY_NUMYCARDS_DE, 0);
        initialValues.put(KEY_TOTALVICT_DE, 0);
        initialValues.put(KEY_AVGBOUTSCORE_DE, "0.0");
        initialValues.put(KEY_PERVICT_DE, "0.0");
        initialValues.put(KEY_COUNTRY, Country);
        initialValues.put(KEY_RATING, Rating);

        //insert into database
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }


    public boolean deleteFencer(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllFencers() {

        String orderBy = KEY_FIRSTNAME + " ASC";

        return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FIRSTNAME,
                KEY_LASTNAME, KEY_CLUB, KEY_BIRTHYEAR, KEY_WEAPON, KEY_GENDER,
                KEY_HAND, KEY_TOTALVICT, KEY_NUMRCARDS, KEY_NUMYCARDS, KEY_AVGBOUTSCORE,
                KEY_PERVICT, KEY_TOTALBOUTS}, null, null, null, null, orderBy);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws java.sql.SQLException if note could not be found/retrieved
     */
    public Cursor fetchFencer(long rowId) throws SQLException {

        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FIRSTNAME,
                        KEY_LASTNAME, KEY_CLUB, KEY_BIRTHYEAR, KEY_WEAPON, KEY_GENDER,
                        KEY_HAND, KEY_TOTALVICT, KEY_NUMRCARDS, KEY_NUMYCARDS, KEY_AVGBOUTSCORE,
                        KEY_PERVICT, KEY_TOTALBOUTS, KEY_TOTALVICT_DE,
                        KEY_NUMRCARDS_DE, KEY_NUMYCARDS_DE, KEY_AVGBOUTSCORE_DE, KEY_PERVICT_DE,
                        KEY_TOTALBOUTS_DE, KEY_RATING, KEY_COUNTRY}, KEY_ROWID + "=" + rowId, null,
                null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetchFencer(String firstName, String lastName) throws SQLException {

        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FIRSTNAME,
                        KEY_LASTNAME, KEY_CLUB, KEY_BIRTHYEAR, KEY_WEAPON, KEY_GENDER,
                        KEY_HAND, KEY_TOTALVICT, KEY_NUMRCARDS, KEY_NUMYCARDS, KEY_AVGBOUTSCORE,
                        KEY_PERVICT, KEY_TOTALBOUTS, KEY_TOTALVICT_DE,
                        KEY_NUMRCARDS_DE, KEY_NUMYCARDS_DE, KEY_AVGBOUTSCORE_DE, KEY_PERVICT_DE,
                        KEY_TOTALBOUTS_DE, KEY_RATING, KEY_COUNTRY}, KEY_FIRSTNAME + "=? AND " + KEY_LASTNAME +
                        "=?", new String[]{firstName, lastName},
                null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateFencer(long rowId, String firstName, String lastName,
                                String club, int birthYear, Fencer.Weapon weapon,
                                Fencer.Gender gender,
                                Fencer.Hand hand, String Rating, String Country) {

        //setup content values
        ContentValues args = new ContentValues();

        //put arguments
        args.put(KEY_FIRSTNAME, firstName);
        args.put(KEY_LASTNAME, lastName);
        args.put(KEY_CLUB, club);
        args.put(KEY_BIRTHYEAR, birthYear);
        args.put(KEY_WEAPON, weapon.ordinal());
        args.put(KEY_GENDER, gender.ordinal());
        args.put(KEY_HAND, hand.ordinal());
        args.put(KEY_COUNTRY, Country);
        args.put(KEY_RATING, Rating);

        //update database
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }


    public boolean updateFencerPoolData(long rowId, int totalBouts,
                                        int numRCards, int numYCards, int totalV, double avgBoutScore,
                                        double perVictory) {

        //setup content values
        ContentValues args = new ContentValues();

        //put arguments
        args.put(KEY_TOTALBOUTS, totalBouts);
        args.put(KEY_NUMRCARDS, numRCards);
        args.put(KEY_NUMYCARDS, numYCards);
        args.put(KEY_TOTALVICT, totalV);
        args.put(KEY_AVGBOUTSCORE, String.valueOf(avgBoutScore));
        args.put(KEY_PERVICT, String.valueOf(perVictory));


        //update database
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }


    public boolean updateFencerDEData(long rowId, int totalBouts,
                                      int numRCards, int numYCards, int totalV, double avgBoutScore,
                                      double perVictory) {

        //setup content values
        ContentValues args = new ContentValues();

        //put arguments
        args.put(KEY_TOTALVICT_DE, totalV);
        args.put(KEY_NUMRCARDS_DE, numRCards);
        args.put(KEY_NUMYCARDS_DE, numYCards);
        args.put(KEY_TOTALBOUTS_DE, totalBouts);
        args.put(KEY_AVGBOUTSCORE_DE, String.valueOf(avgBoutScore));
        args.put(KEY_PERVICT_DE, String.valueOf(perVictory));

        //update database
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public void resetTable() {
        mDb.execSQL("DROP TABLE IF EXISTS fencers");
        mDbHelper.onCreate(mDb);
    }


}
