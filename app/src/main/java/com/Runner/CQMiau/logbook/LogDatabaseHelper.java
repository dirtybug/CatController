package com.Runner.CQMiau.logbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LogDatabaseHelper extends SQLiteOpenHelper {

    static final String TABLE_NAME = "logs";
    static final String COLUMN_ID = "id";
    static final String COLUMN_FREQUENCY = "frequency";
    static final String COLUMN_CALL_SIGN = "call_sign";
    static final String COLUMN_LOCATION = "location";
    static final String COLUMN_TIME = "time";
    static final String COLUMN_RECEIVE_S_VALUE = "receive_s_value";
    static final String COLUMN_SEND_S_VALUE = "send_s_value";
    private static final String DATABASE_NAME = "logbook.db";
    private static final int DATABASE_VERSION = 1;
    private static LogDatabaseHelper instance;

    public LogDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized LogDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new LogDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FREQUENCY + " TEXT, " +
                COLUMN_CALL_SIGN + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_TIME + " INTEGER, " +
                COLUMN_RECEIVE_S_VALUE + " INTEGER, " +
                COLUMN_SEND_S_VALUE + " INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertLog(String frequency, String callSign, String location, Long time, int receiveSValue, int sendSValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_FREQUENCY, frequency);
        values.put(COLUMN_CALL_SIGN, callSign.trim().toUpperCase());
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_RECEIVE_S_VALUE, receiveSValue);
        values.put(COLUMN_SEND_S_VALUE, sendSValue);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public Cursor getAllLogs() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public int updateLog(int id, String frequency, String callSign, String location, long time, int receiveSValue, int sendSValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_FREQUENCY, frequency);
        values.put(COLUMN_CALL_SIGN, callSign);
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_RECEIVE_S_VALUE, receiveSValue);
        values.put(COLUMN_SEND_S_VALUE, sendSValue);

        return db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteLog(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public LogBook getLogById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME, // Table name
                null, // All columns
                COLUMN_ID + " = ?", // WHERE clause
                new String[]{String.valueOf(id)}, // WHERE arguments
                null, // GROUP BY
                null, // HAVING
                null // ORDER BY
        );

        LogBook log = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String frequency = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FREQUENCY));
                String callSign = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CALL_SIGN));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
                long time = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                int receiveSValue = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECEIVE_S_VALUE));
                int sendSValue = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SEND_S_VALUE));

                log = new LogBook(id, frequency, callSign, location, time, receiveSValue, sendSValue);
            }

            cursor.close();
        }
        return log;
    }
}
