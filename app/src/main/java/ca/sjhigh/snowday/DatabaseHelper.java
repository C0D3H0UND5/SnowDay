package ca.sjhigh.snowday;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jason on 2016-09-19.
 * This class was initially created for another app of mine so I am copying it and modifying it for this project
 *
 * This class is used to interface with the database this app will be using to store bus information.
 * The database will store: The bus number, delay time, and date of delay
 *
 * If you don't know anything about databases then don't modify this file
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    /* Database version */
    private static final int DATABASE_VERSION = 1;
    /* Database name */
    private static final String DATABASE_NAME = "LateBuses.db";
    /* Table name */
    private static final String TABLE_NAME = "bus_table";

    /* Table column names */
    private static final String KEY_ID = "ID";
    private static final String KEY_NUMBER = "Bus_Number";
    private static final String KEY_DELAY = "Delay";
    private static final String KEY_DATE = "Date";

    /**
     * Instantiates a new database helper object
     * @param context The activity accessing this database
     */
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + "INTEGER PRIMARY KEY," +
                            KEY_NUMBER + " INTEGER," + KEY_DELAY + " INTEGER," + KEY_DATE + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int one, int two){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Breaks down a person object and inserts it into the database
     * @param tweet Tweet object containing bus information
     * @return Whether or not the insertion succeeded
     */
    public boolean insertRecord(Tweet tweet){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NUMBER, tweet.getBusNumber());
        values.put(KEY_DELAY, tweet.getDelay());
        values.put(KEY_DATE, tweet.getDate());
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1;
    }

    /**
     * Updates the entry of the specified person
     * @param tweet The tweet object
     * @return whether or not the update operation succeeded
     */
    public boolean updateRecord(Tweet tweet){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NUMBER, tweet.getBusNumber());
        values.put(KEY_DELAY, tweet.getDelay());
        values.put(KEY_DATE, tweet.getDate());
        long result = db.update(TABLE_NAME, values, KEY_NUMBER + "= ?", new String[] {String.valueOf(tweet.getBusNumber())});
        db.close();
        return result != -1;
    }

    /**
     * Returns the information regarding a certain bus
     * @param busNumber The bus number that the user takes
     * @return The person object
     */
    public Tweet retrieveRecord(int busNumber){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_NUMBER + " = ?";
        String[] selectionArguments = {String.valueOf(busNumber)};
        Cursor cursor = db.rawQuery(query, selectionArguments);
        Tweet person = new Tweet();
        // If the query returned a result
        if(cursor.moveToFirst()){
            person.setBusNumber(cursor.getInt(1));
            person.setDelay(cursor.getInt(2));
            person.setDate(cursor.getString(3));
        }
        db.close();
        cursor.close();
        return person;
    }

    /**
     * Returns all of the persons in the database in an array
     * @return All the persons in the database
     */
    public Tweet[] retrieveAllRecords(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        int count = cursor.getCount();
        Tweet[] personList = new Tweet[count];
        if(cursor.moveToFirst()) {
            do {
                Tweet person = new Tweet();
                person.setBusNumber(cursor.getInt(1));
                person.setDelay(cursor.getInt(2));
                person.setDate(cursor.getString(3));
                personList[personList.length - count] = person;
                count--;
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return personList;
    }

    /**
     * Deletes the record of the specified person
     * @param busNumber The number of the bus in the record to delete
     */
    public boolean deleteRecord(int busNumber){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = KEY_NUMBER + "= ?";
        String[] whereArgs = {String.valueOf(busNumber)};
        long result = db.delete(TABLE_NAME, whereClause, whereArgs);
        db.close();
        return result != -1;
    }

    /**
     * Deletes all the records in the database (I want to modify it to drop any that aren't from the current day
     */
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    /**
     * Checks if the bus number inputted matches a record stored in the database
     * @param busNumber The bus number to check
     * @return If the value is already stored
     */
    public boolean isStored(int busNumber){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_NUMBER + " = " + busNumber;
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
}

