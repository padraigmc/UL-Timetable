package com.example.ultimetable;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

public class Database {

    boolean DEBUG = true;
    String TAG = "Database";


    //The index (key) column name for use in where clauses.
    public static final String KEY_ID = "eventID";

    //The name and column index of each column in your database.
    //These should be descriptive.
    public static final String KEY_TITLE_COLUMN = "title";
    public static final String KEY_START_TIME_COLUMN = "startTime";
    public static final String KEY_END_TIME_COLUMN = "endTime";
    public static final String KEY_LOCATION_COLUMN = "location";
    public static final String KEY_DESCRIPTION_COLUMN = "description";;

    //public static DateFormat defaultFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    // Database open/upgrade helper
    private static EventDBOpenHelper eventDBOpenHelper;

    public Database(Context context) {
        Log.i(MainActivity.TAG, "Database constructor called");
        eventDBOpenHelper = new EventDBOpenHelper(context, EventDBOpenHelper.DATABASE_NAME, null, EventDBOpenHelper.DATABASE_VERSION);
    }

    // Called when you no longer need access to the database.
    public void closeDatabase() {
        eventDBOpenHelper.close();
    }

    /*
     * Return all the module codes that contain one or more lab sessions in a list of HashMaps
     */
    public static Cursor getAllEventsCursor(int year, int month) {

        Calendar cal = Calendar.getInstance();

        // Set month and year timestamp of month start
        cal.set(year, month-1, 1, 0, 0, 0);
        String monthStart = Long.toString(cal.getTimeInMillis());

        // add 1 month and get timestamp
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.SECOND, -1);
        String monthEnd = Long.toString(cal.getTimeInMillis());

        String[] result_columns = new String[] {
                KEY_ID,
                KEY_TITLE_COLUMN,
                KEY_START_TIME_COLUMN,
                KEY_END_TIME_COLUMN,
                KEY_LOCATION_COLUMN,
                KEY_DESCRIPTION_COLUMN
        };

        String where = KEY_END_TIME_COLUMN + " < ? AND ? <= " + KEY_START_TIME_COLUMN;
        String[] whereArgs = { monthEnd, monthStart };
        String groupBy = null;
        String having = null;
        String order = null;

        // query database and return cursor
        SQLiteDatabase db = eventDBOpenHelper.getReadableDatabase();
        return db.query(EventDBOpenHelper.DATABASE_TABLE, result_columns, where, whereArgs, groupBy, having, order);
    }


    /*
     * Return all the module codes that contain one or more lab sessions in a list of HashMaps
     */
    public static ArrayList<HashMap<String, String>> getAllEvents(int year, int month) {


        // declare arrays lists
        List<HashMap<String, String>> eventArray = new ArrayList<HashMap<String, String>>();

        Calendar cal = Calendar.getInstance();

        // Set month and year timestamp of month start
        cal.set(year, month-1, 1, 0, 0, 0);
        String monthStart = Long.toString(cal.getTimeInMillis());

        // add 1 month and get timestamp
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.SECOND, -1);
        String monthEnd = Long.toString(cal.getTimeInMillis());

        String[] result_columns = new String[] {
                KEY_ID,
                KEY_TITLE_COLUMN,
                KEY_START_TIME_COLUMN,
                KEY_END_TIME_COLUMN,
                KEY_LOCATION_COLUMN,
                KEY_DESCRIPTION_COLUMN
        };

        String where = KEY_END_TIME_COLUMN + " < ? AND ? <= " + KEY_START_TIME_COLUMN;
        String[] whereArgs = { monthEnd, monthStart };
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = eventDBOpenHelper.getReadableDatabase();
        Cursor eventCursor = db.query(EventDBOpenHelper.DATABASE_TABLE, result_columns, where, whereArgs, groupBy, having, order);

        // run through resulting cursor and store all unique module codes
        boolean result = eventCursor.moveToFirst();
        while (result) {
            HashMap<String, String> row = new HashMap<String, String>();

            // add column values to row (HashMap<String, String>)
            row.put("KEY_ID", eventCursor.getString(eventCursor.getColumnIndex(Database.KEY_ID)));
            row.put("KEY_TITLE_COLUMN", eventCursor.getString(eventCursor.getColumnIndex(Database.KEY_TITLE_COLUMN)));
            row.put("KEY_START_TIME_COLUMN", eventCursor.getString(eventCursor.getColumnIndex(Database.KEY_START_TIME_COLUMN)));
            row.put("KEY_END_TIME_COLUMN", eventCursor.getString(eventCursor.getColumnIndex(Database.KEY_END_TIME_COLUMN)));
            row.put("KEY_LOCATION_COLUMN", eventCursor.getString(eventCursor.getColumnIndex(Database.KEY_LOCATION_COLUMN)));
            row.put("KEY_DESCRIPTION_COLUMN", eventCursor.getString(eventCursor.getColumnIndex(Database.KEY_DESCRIPTION_COLUMN)));

            // after all columns are saved, iterate cursor, add HashMap to eventArray and clear it
            eventArray.add(row);
            result = eventCursor.moveToNext();
        }

        eventCursor.close();
        return (ArrayList<HashMap<String, String>>) eventArray;
    }


    public long addNewEvent(String eventTitle, long startTime, long endTime, String location, String description) {
        // Create a new row of values to insert.
        ContentValues newValues = new ContentValues();

        // Assign values for each row.
        newValues.put(KEY_TITLE_COLUMN, eventTitle);
        newValues.put(KEY_START_TIME_COLUMN, Long.toString(startTime));
        newValues.put(KEY_END_TIME_COLUMN, Long.toString(endTime));
        newValues.put(KEY_LOCATION_COLUMN, location);
        newValues.put(KEY_DESCRIPTION_COLUMN, description);

        // Insert the row into your table
        SQLiteDatabase db = eventDBOpenHelper.getWritableDatabase();
        return db.insert(EventDBOpenHelper.DATABASE_TABLE, null, newValues);
    }

    /*
     * updateTable looks after updating the KEY_SLOT_NEXT_OCCURENCE time for row rowId.
     * This method should be run any time a slot time was retrieved from the database for scheduling
     * slot occurences are periodic with a week period so this method simply adds a full week to the scheduled time
     * if the time stored in the database for this particular slot is in the past. If forceUpdate = true, the time will be updated
     * to the next occurence regardless.
     */
    /*public boolean updateTable(Integer rowId, boolean forceUpdate) {

        if (DEBUG) {
            Cursor slotInfo = getSlotInfo(rowId);
            if (slotInfo.moveToFirst()) {
                Log.e(TAG, "Slot content: " + slotInfo.getString(slotInfo.getColumnIndex(KEY_MODULE_CODE_COLUMN)) +", " +
                        slotInfo.getString(slotInfo.getColumnIndex(KEY_SLOT_DAY_COLUMN)) +", " +
                        slotInfo.getString(slotInfo.getColumnIndex(KEY_SLOT_NEXT_OCCURENCE_COLUMN))) ;
            }
        }

        Calendar nextStart = getStartTime(rowId);

        if (nextStart.getTimeInMillis()==0) {
            Cursor slotInfo = getSlotInfo(rowId);
            if (slotInfo.moveToFirst()) {
                String weekDay = slotInfo.getString(slotInfo.getColumnIndex(KEY_SLOT_DAY_COLUMN));
                String startHour = slotInfo.getString(slotInfo.getColumnIndex(KEY_SLOT_START_COLUMN));
                nextStart = Calendar.getInstance();
                nextStart.set(Calendar.DAY_OF_WEEK, (Slot.DAY.valueOf(weekDay).getValue()));
                nextStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startHour));
            }
            else
                return false;
        }


        while (nextStart.before(Calendar.getInstance()) || forceUpdate) {
            nextStart.add(Calendar.DAY_OF_MONTH, 7);
            forceUpdate = false;
        }


        ContentValues values = new ContentValues();
        String where = KEY_ID + " = ?";
        String whereArgs[] = {rowId.toString()};
        values.put(KEY_SLOT_NEXT_OCCURENCE_COLUMN, defaultFormatter.format(nextStart.getTime())+":00:00");
        SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
        if (db.update(EventDBOpenHelper.DATABASE_TABLE, values, where, whereArgs) == 1) {
            if (DEBUG) {
                Cursor slotInfo = getSlotInfo(rowId);
                if (slotInfo.moveToFirst()) {
                    Log.e(TAG, "Slot updated: " + slotInfo.getString(slotInfo.getColumnIndex(KEY_MODULE_CODE_COLUMN)) +", " +
                            slotInfo.getString(slotInfo.getColumnIndex(KEY_SLOT_DAY_COLUMN)) +", " +
                            slotInfo.getString(slotInfo.getColumnIndex(KEY_SLOT_NEXT_OCCURENCE_COLUMN))) ;
                }
            }
            return true;
        }
        if (DEBUG) Log.e(TAG, "Database update failed");
        return false;

    }*/

    /*
     * updateTable looks after updating the KEY_SLOT_NEXT_OCCURENCE times in the whole table.
     * This method should be run any time the full database needs scheduling
     */
    /*public void updateTable() {
        Cursor idCursor = getIds();
        boolean result = idCursor.moveToFirst();
        while (result) {
            updateTable(idCursor.getInt(idCursor.getColumnIndex(KEY_ID)), false);
            result = idCursor.moveToNext();
        }
    }*/

    public void deleteRow(int idNr) {
        // Specify a where clause that determines which row(s) to delete.
        // Specify where arguments as necessary.
        String where = KEY_ID + "=" + idNr;
        String whereArgs[] = null;

        // Delete the rows that match the where clause.
        SQLiteDatabase db = eventDBOpenHelper.getWritableDatabase();
        db.delete(EventDBOpenHelper.DATABASE_TABLE, where, whereArgs);
    }

    private static class EventDBOpenHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "ulTimetable.db";
        private static final String DATABASE_TABLE = "Event";
        private static final int DATABASE_VERSION = 1;

        // SQL Statement to create a new database.
        private static final String DATABASE_CREATE =
                "create table " + DATABASE_TABLE + " (" +
                KEY_ID + " integer primary key autoincrement, " +
                KEY_TITLE_COLUMN + " text not null, " +
                KEY_START_TIME_COLUMN + " text not null, " +
                KEY_END_TIME_COLUMN + " text not null, " +
                KEY_LOCATION_COLUMN + " text not null, " +
                KEY_DESCRIPTION_COLUMN + " text not null);";

        public EventDBOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // Called when no database exists in disk and the helper class needs to create a new one.
        @Override
        public void onCreate(SQLiteDatabase db) {
            // create database
            db.execSQL(DATABASE_CREATE);

            Log.i(MainActivity.TAG, "Database onCreate() called");
        }

        // Called when there is a database version mismatch meaning that
        // the version of the database on disk needs to be upgraded to
        // the current version.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            // Log the version upgrade.
            Log.w("TaskDBAdapter", "Upgrading from version " +
                    oldVersion + " to " +
                    newVersion + ", which will destroy all old data");

            // Upgrade the existing database to conform to the new
            // version. Multiple previous versions can be handled by
            // comparing oldVersion and newVersion values.

            // The simplest case is to drop the old table and create a new one.
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            // Create a new one.
            onCreate(db);
        }
    }
}
