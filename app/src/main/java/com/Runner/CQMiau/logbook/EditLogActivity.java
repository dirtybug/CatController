package com.Runner.CQMiau.logbook;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Runner.CQMiau.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditLogActivity extends AppCompatActivity {
    private static ChangeCallback changeCallbackObj;
    private LogDatabaseHelper dbHelper;
    private int logId;
    private Spinner receiveSValueSpinner;
    private Spinner sendSValueSpinner;

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
        TimePicker timePicker = findViewById(R.id.LogtimePicker);
        EditText dateEditText = findViewById(R.id.LogeditDate);
        Button setNowButton = findViewById(R.id.LogsetNowButton);


        setNowButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDate = dateFormat.format(calendar.getTime());
            dateEditText.setText(currentDate);

            timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(calendar.get(Calendar.MINUTE));
        });
        // Set up the date picker dialog on EditText click
        dateEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                dateEditText.setText(selectedDate);
            }, year, month, day);

            datePickerDialog.show();
        });

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
        LogBook cursor = dbHelper.getLogById(logId);
        if (cursor != null) {
            editFrequency.setText(cursor.getFrequency());
            editCallSign.setText(cursor.getCallSign());
            editLocation.setText(cursor.getLocation());

            long time = cursor.getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);

            // Format and set the date to the EditText
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(calendar.getTime());
            dateEditText.setText(formattedDate);

            // Set time in TimePicker
            timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(calendar.get(Calendar.MINUTE));

            receiveSValueSpinner.setSelection(cursor.getReceiveSValue() - 1);
            sendSValueSpinner.setSelection(cursor.getSendSValue() - 1);
        }

        // Save changes
        saveButton.setOnClickListener(v -> {
            String frequency = editFrequency.getText().toString();
            String callSign = editCallSign.getText().toString();
            String location = editLocation.getText().toString();
            String dateString = dateEditText.getText().toString();

            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            int receiveSValue = (int) receiveSValueSpinner.getSelectedItem();
            int sendSValue = (int) sendSValueSpinner.getSelectedItem();

            Calendar calendar = Calendar.getInstance();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                calendar.setTime(dateFormat.parse(dateString));
            } catch (Exception e) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
                return;
            }
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

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
