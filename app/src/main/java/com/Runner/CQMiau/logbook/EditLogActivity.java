package com.Runner.CQMiau.logbook;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Runner.CQMiau.R;

import java.util.Calendar;
import java.util.Locale;

public class EditLogActivity extends AppCompatActivity {
    private static ChangeCallback changeCallbackObj;
    private LogDatabaseHelper dbHelper;
    private int logId;
    Spinner receiveSValueSpinner ;
    Spinner sendSValueSpinner ;

    public static void setLogUpdateCallback(ChangeCallback callback) {
        changeCallbackObj = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_log);

        dbHelper = new LogDatabaseHelper(this);

        EditText editFrequency = findViewById(R.id.LogeditFrequency);
        EditText editCallSign = findViewById(R.id.LogeditCallSign);
        EditText editLocation = findViewById(R.id.LogeditLocation);
        DatePicker datePicker = findViewById(R.id.LogdatePicker);
        TimePicker timePicker = findViewById(R.id.LogtimePicker);

        Button saveButton = findViewById(R.id.LogsaveButton);

        logId = getIntent().getIntExtra("LOG_ID", -1);
        // Initialize dropdowns
         receiveSValueSpinner = findViewById(R.id.LogreceiveSValueSpinner);
         sendSValueSpinner = findViewById(R.id.LogsendSValueSpinner);

        ArrayAdapter<Integer> sValueAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getSValueRange());
        sValueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        receiveSValueSpinner.setAdapter(sValueAdapter);
        sendSValueSpinner.setAdapter(sValueAdapter);
        // Fetch log details from the database
        Cursor cursor = dbHelper.getAllLogs();
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_ID)) == logId) {
                    editFrequency.setText(cursor.getString(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_FREQUENCY)));
                    editCallSign.setText(cursor.getString(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_CALL_SIGN)));
                    editLocation.setText(cursor.getString(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_LOCATION)));
                   long time= cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_TIME);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time);

                    // Extract date and time components
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH); // January = 0
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    // Set DatePicker to Unix time
                    datePicker.updateDate(year, month, day);

                    // Set TimePicker to Unix time
                    timePicker.setHour(hour);
                    timePicker.setMinute(minute);
                    receiveSValueSpinner.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_RECEIVE_S_VALUE)) - 1);
                    sendSValueSpinner.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_SEND_S_VALUE)) - 1);

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

            int receiveSValue = (int) receiveSValueSpinner.getSelectedItem();
            int sendSValue = (int) sendSValueSpinner.getSelectedItem();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month); // Note: Month is 0-based
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0); // Optional: Set seconds to 0
            calendar.set(Calendar.MILLISECOND, 0); // Optional: Set milliseconds to 0

            long timeInMillis = calendar.getTimeInMillis();

            int rowsAffected = dbHelper.updateLog(logId, frequency, callSign, location, timeInMillis, receiveSValue, sendSValue);

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
    private Integer[] getSValueRange() {
        Integer[] sValues = new Integer[9];
        for (int i = 0; i < sValues.length; i++) {
            sValues[i] = i + 1;
        }
        return sValues;
    }
}
