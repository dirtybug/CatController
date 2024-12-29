package com.Runner.CQMiau.spot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import java.util.Date;
import java.util.Locale;
public class SpotDetailActivity extends AppCompatActivity {

    private LogDatabaseHelper dbHelper;
    DatePicker datePicker;
    TimePicker timePicker;

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
        String time = getIntent().getStringExtra("time");

        // Bind the data to views
        EditText frequencyView = findViewById(R.id.frequencyItemView);
        EditText callSignView = findViewById(R.id.callSignItemView);
        TextView locationView = findViewById(R.id.locationItemView);
         datePicker = findViewById(R.id.SpotdatePicker);
        timePicker = findViewById(R.id.SpottimePicker);

        TextView Country = findViewById(R.id.Country);

        frequencyView.setText(frequency);
        Country.setText(flag);
        callSignView.setText(callSign);
        locationView.setText(location);

        // Initialize dropdowns
        Spinner receiveSValueSpinner = findViewById(R.id.receiveSValueSpinner);
        Spinner sendSValueSpinner = findViewById(R.id.sendSValueSpinner);

        ArrayAdapter<Integer> sValueAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getSValueRange());
        sValueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        receiveSValueSpinner.setAdapter(sValueAdapter);
        sendSValueSpinner.setAdapter(sValueAdapter);

        // Initialize editable time and date field
        String currentDateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

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
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth(); // January = 0
            int year = datePicker.getYear();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String formattedDateTime = String.format(Locale.getDefault(),
                    "%04d-%02d-%02d %02d:%02d:00", year, month + 1, day, hour, minute);
            dbHelper.insertLog(updatedFrequency, updatedCallSign, updatedLocation, formattedDateTime, selectedReceiveSValue, selectedSendSValue);
            Toast.makeText(this, "Log saved to database!", Toast.LENGTH_SHORT).show();
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
