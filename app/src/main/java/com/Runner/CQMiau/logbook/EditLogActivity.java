package com.Runner.CQMiau.logbook;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Runner.CQMiau.R;

import java.util.Locale;

public class EditLogActivity extends AppCompatActivity {
    private static ChangeCallback changeCallbackObj;
    private LogDatabaseHelper dbHelper;
    private int logId;

    public static void setLogUpdateCallback(ChangeCallback callback) {
        changeCallbackObj = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_log);

        dbHelper = new LogDatabaseHelper(this);

        EditText editFrequency = findViewById(R.id.editFrequency);
        EditText editCallSign = findViewById(R.id.editCallSign);
        EditText editLocation = findViewById(R.id.editLocation);
        DatePicker datePicker = findViewById(R.id.datePicker);
        TimePicker timePicker = findViewById(R.id.timePicker);
        EditText editReceiveSValue = findViewById(R.id.editReceiveSValue);
        EditText editSendSValue = findViewById(R.id.editSendSValue);
        Button saveButton = findViewById(R.id.saveButton);

        logId = getIntent().getIntExtra("LOG_ID", -1);

        // Fetch log details from the database
        Cursor cursor = dbHelper.getAllLogs();
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_ID)) == logId) {
                    editFrequency.setText(cursor.getString(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_FREQUENCY)));
                    editCallSign.setText(cursor.getString(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_CALL_SIGN)));
                    editLocation.setText(cursor.getString(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_LOCATION)));
                    //editTime.setText(cursor.getString(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_TIME)));
                    editReceiveSValue.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_RECEIVE_S_VALUE))));
                    editSendSValue.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_SEND_S_VALUE))));
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Save changes
        saveButton.setOnClickListener(v -> {
            String frequency = editFrequency.getText().toString();
            String callSign = editCallSign.getText().toString();
            String location = editLocation.getText().toString();

            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth(); // January = 0
            int year = datePicker.getYear();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String time = String.format(Locale.getDefault(),
                    "%04d-%02d-%02d %02d:%02d:00", year, month + 1, day, hour, minute);
            int receiveSValue = Integer.parseInt(editReceiveSValue.getText().toString());
            int sendSValue = Integer.parseInt(editSendSValue.getText().toString());

            int rowsAffected = dbHelper.updateLog(logId, frequency, callSign, location, time, receiveSValue, sendSValue);

            if (rowsAffected > 0) {
                Toast.makeText(this, "Log updated successfully", Toast.LENGTH_SHORT).show();
                if (changeCallbackObj != null) {
                    changeCallbackObj.update(logId);
                }
                finish();
            } else {
                Toast.makeText(this, "Failed to update log", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public interface ChangeCallback {
        void update(int id);
    }
}
