package com.Runner.CQMiau.spot;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Runner.CQMiau.HamRadioClusterConnection;
import com.Runner.CQMiau.R;
import com.Runner.CQMiau.comPort.ComPortManager;
import com.Runner.CQMiau.logbook.LogDatabaseHelper;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SpotDetailActivity extends AppCompatActivity {

    public static final String TIME = "time";
    private LogDatabaseHelper dbHelper;
    private EditText dateEditText;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot);

        // Initialize database helper
        dbHelper = new LogDatabaseHelper(this);

        // Get data from the intent
        String frequency = getIntent().getStringExtra("frequency");
        String callSign = getIntent().getStringExtra("callSign");
        String location = getIntent().getStringExtra("location");
        String flag = getIntent().getStringExtra("flag");
        long time = getIntent().getLongExtra(TIME, System.currentTimeMillis());

        // Bind the data to views
        EditText frequencyView = findViewById(R.id.frequencyItemView);
        EditText callSignView = findViewById(R.id.callSignItemView);
        TextView locationView = findViewById(R.id.locationItemView);
        dateEditText = findViewById(R.id.editDate);
        timePicker = findViewById(R.id.SpottimePicker);
        timePicker.setIs24HourView(true); // Set to 24-hour format
        Button setNowButton = findViewById(R.id.setNowButton);

        // Set up initial time from intent
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateEditText.setText(dateFormat.format(calendar.getTime()));

        timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(calendar.get(Calendar.MINUTE));

        TextView countryView = findViewById(R.id.Country);
        frequencyView.setText(frequency);
        countryView.setText(flag);
        callSignView.setText(callSign);
        locationView.setText(location);

        // Initialize dropdowns
        Spinner receiveSValueSpinner = findViewById(R.id.receiveSValueSpinner);
        Spinner sendSValueSpinner = findViewById(R.id.sendSValueSpinner);

        ArrayAdapter<Integer> sValueAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getSValueRange());
        sValueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        receiveSValueSpinner.setAdapter(sValueAdapter);
        sendSValueSpinner.setAdapter(sValueAdapter);

        // Date picker dialog for dateEditText
        dateEditText.setOnClickListener(v -> {
            final Calendar newCalendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                newCalendar.set(year, month, dayOfMonth);
                dateEditText.setText(dateFormat.format(newCalendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        setNowButton.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            dateEditText.setText(dateFormat.format(now.getTime()));
            timePicker.setHour(now.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(now.get(Calendar.MINUTE));
        });

        // Open QRZ link
        Button openLinkButton = findViewById(R.id.openLinkButton);
        openLinkButton.setOnClickListener(v -> {
            String updatedCallSign = callSignView.getText().toString();
            String url = "https://www.qrz.com/db/" + updatedCallSign;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        // Send spot action
        Button sendSpot = findViewById(R.id.SendSpot);
        sendSpot.setOnClickListener(v -> {
            String updatedFrequency = validateFrequency(frequencyView.getText().toString());
            frequencyView.setText(updatedFrequency);

            String updatedCallSign = callSignView.getText().toString();

            if (updatedFrequency.isEmpty()) {
                frequencyView.setError("Frequency is required.");
                return;
            }

            try {
                double dfrequency = Double.parseDouble(updatedFrequency);
                if (dfrequency <= 0) {
                    frequencyView.setError("Frequency must be a positive number.");
                    return;
                }

                String sentSpot = "DX " + updatedCallSign + " " + updatedFrequency;
                HamRadioClusterConnection.getHandlerObj().sendCommand(sentSpot);
                Toast.makeText(this, "Spot sent: " + sentSpot, Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                frequencyView.setError("Invalid frequency. Please enter a valid number.");
            }
        });

        // Set frequency button action
        Button setFrequencyButton = findViewById(R.id.setFrequencyButton);
        setFrequencyButton.setOnClickListener(v -> {
            String updatedFrequency = validateFrequency(frequencyView.getText().toString());
            frequencyView.setText(updatedFrequency);

            ComPortManager.getInstance().Connect();
            RadioBase radioObj = RadioFactory.getRadio();
            radioObj.setFrequency(updatedFrequency);

            Toast.makeText(this, "Frequency set: " + updatedFrequency, Toast.LENGTH_SHORT).show();
        });

        // Save to log book button action
        Button saveLogButton = findViewById(R.id.saveLogButton);
        saveLogButton.setOnClickListener(v -> {
            String updatedFrequency = frequencyView.getText().toString();
            String updatedCallSign = callSignView.getText().toString();
            String updatedLocation = locationView.getText().toString();
            int selectedReceiveSValue = Integer.parseInt(receiveSValueSpinner.getSelectedItem().toString());
            int selectedSendSValue = Integer.parseInt(sendSValueSpinner.getSelectedItem().toString());

            try {
                Calendar logCalendar = Calendar.getInstance();
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                logCalendar.setTime(inputDateFormat.parse(dateEditText.getText().toString()));
                logCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                logCalendar.set(Calendar.MINUTE, timePicker.getMinute());
                logCalendar.set(Calendar.SECOND, 0);
                logCalendar.set(Calendar.MILLISECOND, 0);

                long timeInMillis = logCalendar.getTimeInMillis();

                dbHelper.insertLog(updatedFrequency, updatedCallSign, updatedLocation, timeInMillis, selectedReceiveSValue, selectedSendSValue);
                Toast.makeText(this, "Log saved to database!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error saving log: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String validateFrequency(String frequency) {
        if (!frequency.contains(".")) {
            return frequency + ".0";
        }
        int decimalIndex = frequency.indexOf(".");
        if (frequency.length() > decimalIndex + 2) {
            return frequency.substring(0, decimalIndex + 2);
        }
        return frequency;
    }

    private Integer[] getSValueRange() {
        Integer[] sValues = new Integer[9];
        for (int i = 0; i < sValues.length; i++) {
            sValues[i] = i + 1;
        }
        return sValues;
    }
}
