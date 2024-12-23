package com.Runner.CQMiau.spot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
        String comment = getIntent().getStringExtra("comment");


        String time = getIntent().getStringExtra("time");

        // Bind the data to views
        TextView frequencyView = findViewById(R.id.frequencyItemView);
        TextView callSignView = findViewById(R.id.callSignItemView);
        TextView locationView = findViewById(R.id.locationItemView);
        TextView timeView = findViewById(R.id.timeItemView);

        frequencyView.setText(frequency);
        callSignView.setText(flag + callSign);
        locationView.setText(location);
        timeView.setText(time);

        // Initialize dropdowns
        Spinner receiveSValueSpinner = findViewById(R.id.receiveSValueSpinner);
        Spinner sendSValueSpinner = findViewById(R.id.sendSValueSpinner);

        ArrayAdapter<Integer> sValueAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getSValueRange());
        sValueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        receiveSValueSpinner.setAdapter(sValueAdapter);
        sendSValueSpinner.setAdapter(sValueAdapter);

        // Initialize editable time and date field
        EditText timeDateEditText = findViewById(R.id.timeDateEditText);
        String currentDateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        timeDateEditText.setText(currentDateAndTime);
        Button openLinkButton = findViewById(R.id.openLinkButton);
        Button sendSpot = findViewById(R.id.SendSpot);
        sendSpot.setOnClickListener(v -> {
                    String sentSpot = "DX " + callSign + " " + frequency;
                    HamRadioClusterConnection.getHandlerObj().sendCommand(sentSpot);


                }
        );
        // Set up the button click listener
        openLinkButton.setOnClickListener(v -> {
            // The URL to be opened
            String url = "https://www.qrz.com/db/" + callSign;

            // Create an intent to open the URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            // Start the activity
            startActivity(intent);
        });
        // Set frequency button action
        Button setFrequencyButton = findViewById(R.id.setFrequencyButton);
        setFrequencyButton.setOnClickListener(v -> {
            Toast.makeText(this, "Frequency set: " + frequency, Toast.LENGTH_SHORT).show();

            ComPortManager.getInstance().Connect();


            RadioBase radioObj = RadioFactory.getRadio();

            radioObj.setFrequency(frequency);

        });

        // Save to log book button action
        Button saveLogButton = findViewById(R.id.saveLogButton);
        saveLogButton.setOnClickListener(v -> {
            String editedTimeDate = timeDateEditText.getText().toString();
            int selectedReceiveSValue = Integer.parseInt(receiveSValueSpinner.getSelectedItem().toString());
            int selectedSendSValue = Integer.parseInt(sendSValueSpinner.getSelectedItem().toString());

            dbHelper.insertLog(frequency, callSign, location, editedTimeDate, selectedReceiveSValue, selectedSendSValue);

            Toast.makeText(this, "Log saved to database!", Toast.LENGTH_SHORT).show();
        });
    }

    private Integer[] getSValueRange() {
        Integer[] sValues = new Integer[9]; // Correct size for 1 to 9
        for (int i = 0; i < sValues.length; i++) {
            sValues[i] = i + 1; // Populate 1 to 9
        }
        return sValues;
    }
}