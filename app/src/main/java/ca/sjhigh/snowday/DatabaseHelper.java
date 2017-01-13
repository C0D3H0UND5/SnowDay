package ca.sjhigh.snowday;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jason on 2016-09-19.
 *
 * This class is used to interface with the database this app will be using to store bus information.
 * The database will store: The bus number, delay time, and date of delay
 *
 * If you don't know anything about databases then don't modify this file
 */
class DatabaseHelper extends SQLiteOpenHelper{

    /* Database version */
    private static final int DATABASE_VERSION = 2;
    /* Database name */
    private static final String DATABASE_NAME = "SnowDay.db";
    /* Table name */
    private static final String TABLE_DELAY = "delay_table";
    private static final String TABLE_CLOSURE = "closure_table";

    /* Table column names */
    private static final String KEY_ID = "ID";
    private static final String KEY_DATE = "Date";
    /** Delay table **/
    private static final String KEY_NUMBER = "Bus_Number";
    private static final String KEY_DELAY = "Delay";
    /** Closure table **/
    private static final String KEY_TEXT = "Closure_Tweet";

    /** Logic Variables **/
    private int latestDelay = 0;
    private int latestClosure = 0;

    /**
     * Instantiates a new database helper object
     * @param context The activity accessing this database
     */
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_DELAY_TABLE = "CREATE TABLE " + TABLE_DELAY + "(" + KEY_ID + "INTEGER PRIMARY KEY," +
                            KEY_NUMBER + " INTEGER," + KEY_DELAY + " INTEGER," + KEY_DATE + " TEXT" + ")";
        String CREATE_CLOSURE_TABLE = "CREATE TABLE " + TABLE_CLOSURE + "(" + KEY_ID + "INTEGER PRIMARY KEY, "
                                    + KEY_TEXT + " TEXT," + KEY_DATE + " TEXT" + ")";
        db.execSQL(CREATE_DELAY_TABLE);
        db.execSQL(CREATE_CLOSURE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int one, int two){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELAY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLOSURE);
        onCreate(db);
    }

    /**
     * Stores a record of a delay to the database
     * @param delay Delay object containing delay information
     * @return Whether or not the insertion succeeded
     */
    public boolean insertDelay(Delay delay){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NUMBER, delay.getBusNumber());
        values.put(KEY_DELAY, delay.getDelay());
        values.put(KEY_DATE, delay.getDate());
        long result = db.insert(TABLE_DELAY, null, values);
        db.close();
        return result != -1;
    }

    /**
     * Stores a record of a closure to the database
     * @param closure Closure object containing closure information
     * @return Whether or not the insertion succeeded
     */
    public boolean insertClosure(Closure closure){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TEXT, closure.getText());
        values.put(KEY_DATE, closure.getDate());
        long result = db.insert(TABLE_CLOSURE, null, values);
        db.close();
        return result != -1;
    }

    /**
     * Updates the record of a delay based on the bus number
     * @param delay Delay object containing delay information
     * @return Whether or not the update operation succeeded
     */
    public boolean updateDelay(Delay delay){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NUMBER, delay.getBusNumber());
        values.put(KEY_DELAY, delay.getDelay());
        values.put(KEY_DATE, delay.getDate());
        long result = db.update(TABLE_DELAY, values, KEY_NUMBER + "= ?", new String[] {String.valueOf(delay.getBusNumber())});
        db.close();
        return result != -1;
    }

    // This will get used when the information for the user's buses is collected and processed
    /**
     * Returns the information regarding a certain bus delay
     * @param busNumber The bus number to get the delay of
     * @return Delay object containing delay information
     */
    public Delay retrieveDelay(int busNumber){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_DELAY + " WHERE " + KEY_NUMBER + " = ?";
        String[] selectionArguments = {String.valueOf(busNumber)};
        Cursor cursor = db.rawQuery(query, selectionArguments);
        Delay delay = new Delay();
        // If the query returned a result
        if(cursor.moveToFirst()){
            delay.setBusNumber(cursor.getInt(1));
            delay.setDelay(cursor.getInt(2));
            delay.setDate(cursor.getString(3));
        }
        db.close();
        cursor.close();
        return delay;
    }

    /**
     * Returns all of the delays stored in the database
     * @return All of the delays as Delay objects
     */
    public Delay[] retrieveDelays(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DELAY, null);
        int count = cursor.getCount();
        Delay[] delayList = new Delay[count];
        if(cursor.moveToFirst()) {
            do {
                Delay delay = new Delay();
                delay.setBusNumber(cursor.getInt(1));
                delay.setDelay(cursor.getInt(2));
                delay.setDate(cursor.getString(3));
                delayList[delayList.length - count] = delay;
                count--;
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return delayList;
    }

    /**
     * Returns all of the closures stored in the database
     * @return All of the closures as Closure objects
     */
    public Closure[] retrieveClosures(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLOSURE, null);
        int count = cursor.getCount();
        Closure[] closureList = new Closure[count];
        if(cursor.moveToFirst()) {
            do {
                Closure closure = new Closure();
                closure.setText(cursor.getString(1));
                closure.setDate(cursor.getString(2));
                closureList[closureList.length - count] = closure;
                count--;
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return closureList;
    }

    /**
     * Deletes the record of the specified delay
     * @param busNumber The number of the bus in the record to delete
     */
    public boolean deleteDelay(int busNumber){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = KEY_NUMBER + "= ?";
        String[] whereArgs = {String.valueOf(busNumber)};
        long result = db.delete(TABLE_DELAY, whereClause, whereArgs);
        db.close();
        return result != -1;
    }

    /**
     * Deletes all the delays in the database (I want to modify it to drop any that aren't from the current day
     */
    public void deleteAllDelays(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DELAY, null, null);
        db.close();
    }

    /**
     * Deletes all the closures in the database (I want to modify it to drop any that aren't from the current day
     */
    public void deleteAllClosures(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLOSURE, null, null);
        db.close();
    }

    /**
     * Checks if the bus number inputted matches a record stored in the database
     * @param busNumber The bus number to check
     * @return If the value is already stored
     */
    public boolean isDelayStored(int busNumber){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_DELAY + " WHERE " + KEY_NUMBER + " = ?";
        String[] selectionArguments = {String.valueOf(busNumber)};
        Cursor cursor = db.rawQuery(query, selectionArguments);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    /**
     * Checks if the text inputted matches a record stored in the database
     * @param text The text to check
     * @return If the value is already stored
     */
    public boolean isClosureStored(String text){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CLOSURE + " WHERE " + KEY_TEXT + " = ?";
        String[] selectionArguments = {text};
        Cursor cursor = db.rawQuery(query, selectionArguments);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    /**
     * Returns the number of delays that have been recognized by the user
     * @return the value of latestDelay
     */
    public int getLatestDelay() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_DELAY;
        int temp = latestDelay;
        latestDelay = db.rawQuery(query, null).getCount();
        return temp;
    }

    /**
     * Returns the number of closures that have been recognized by the user
     * @return the value of latestClosure
     */
    public int getLatestClosure() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CLOSURE;
        int temp = latestClosure;
        latestClosure = db.rawQuery(query, null).getCount();
        return temp;
    }
}

