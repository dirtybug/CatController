package com.example.catcontroller.logbook;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LogDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "logbook.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_NAME = "logs";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FREQUENCY = "frequency";
    private static final String COLUMN_CALL_SIGN = "call_sign";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_RECEIVE_S_VALUE = "receive_s_value";
    private static final String COLUMN_SEND_S_VALUE = "send_s_value";

    public LogDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FREQUENCY + " TEXT, " +
                COLUMN_CALL_SIGN + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_RECEIVE_S_VALUE + " INTEGER, " +
                COLUMN_SEND_S_VALUE + " INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a log entry
    public void insertLog(String frequency, String callSign, String location, String time, int receiveSValue, int sendSValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FREQUENCY, frequency);
        values.put(COLUMN_CALL_SIGN, callSign);
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_RECEIVE_S_VALUE, receiveSValue);
        values.put(COLUMN_SEND_S_VALUE, sendSValue);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Retrieve all logs
    public Cursor getAllLogs() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
