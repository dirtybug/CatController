package com.Runner.CQMiau.cqMode;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Runner.CQMiau.HamRadioClusterConnection;
import com.Runner.CQMiau.R;
import com.Runner.CQMiau.UserSettingsActivity;
import com.Runner.CQMiau.logbook.LogDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

public class CQModeActivity extends AppCompatActivity {

    private static CQModeActivity instance; // Static reference to the activity instance
    private static final CopyOnWriteArrayList<Dx> dxList = new CopyOnWriteArrayList<>(); // Thread-safe shared DX list

    private Spinner spinnerReceiveS;
    private Spinner spinnerSendS;
    private EditText frequencyS;
    private EditText inputCallSign;
    private EditText dateEditText;
    private TimePicker timePicker;
    private Button buttonDXMyself;
    private Button buttonSetTime;
    private Button buttonSaveLog;
    private RecyclerView recyclerView;
    private DXAdapter dxAdapter;
    private LogDatabaseHelper dbHelper;

    public static CQModeActivity getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CQModeActivity instance is not initialized or destroyed.");
        }
        return instance;
    }

    public static void addNewDX(String callSign, String flag, String comment, String location) {
        Dx newDx = new Dx(callSign, flag, comment, location);
        dxList.add(newDx);

        if (instance != null) {
            instance.runOnUiThread(() -> {
                instance.dxAdapter.notifyItemInserted(dxList.size() - 1);
                Toast.makeText(instance, "DX added: " + callSign, Toast.LENGTH_SHORT).show();
            });
        }
    }

    public static void populateCallSign(String callSign) {
        if (instance != null) {
            instance.inputCallSign.setText(callSign);
            Toast.makeText(instance, "Call Sign populated: " + callSign, Toast.LENGTH_SHORT).show();
        } else {
            throw new IllegalStateException("CQModeActivity instance is not initialized.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cq_mode);

        dbHelper = new LogDatabaseHelper(this);
        instance = this;

        // Initialize views
        spinnerReceiveS = findViewById(R.id.spinner_receive_s);
        frequencyS = findViewById(R.id.input_frequecy_s);
        spinnerSendS = findViewById(R.id.spinner_sender_s);
        inputCallSign = findViewById(R.id.input_callsign);
        dateEditText = findViewById(R.id.cqeditDate);
        timePicker = findViewById(R.id.cqtimePicker);
        buttonDXMyself = findViewById(R.id.button_spot_myself);
        buttonSetTime = findViewById(R.id.cqsetNowButton);
        buttonSaveLog = findViewById(R.id.button_SaveLog);
        recyclerView = findViewById(R.id.recycler_view);

        // Setup spinners
        ArrayAdapter<Integer> sValueAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getSValueRange());
        sValueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReceiveS.setAdapter(sValueAdapter);
        spinnerSendS.setAdapter(sValueAdapter);

        // Initialize RecyclerView
        dxAdapter = new DXAdapter(dxList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dxAdapter);

        // Handle "Spot Myself" button
        buttonDXMyself.setOnClickListener(v -> {
            String frequency = validateFrequency(frequencyS.getText().toString());
            frequencyS.setText(frequency);
            String callSign = UserSettingsActivity.getCallsign(this);

            String sentSpot = "DX " + callSign + " " + frequency;
            HamRadioClusterConnection.getHandlerObj().sendCommand(sentSpot);
            Toast.makeText(this, "Spot sent: " + sentSpot, Toast.LENGTH_SHORT).show();
        });

        // Handle "Set Time" button
        buttonSetTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance(); // Get the current date and time

            // Format the date and set it to the dateEditText
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDate = dateFormat.format(calendar.getTime());
            dateEditText.setText(currentDate);

            // Set the current time in the TimePicker
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                timePicker.setMinute(calendar.get(Calendar.MINUTE));
            } else {
                timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
            }

            // Show a confirmation toast
            Toast.makeText(this, "Date and time set to now", Toast.LENGTH_SHORT).show();
        });


        // Handle "Save Log" button
        buttonSaveLog.setOnClickListener(v -> {
            try {
                String frequency = validateFrequency(frequencyS.getText().toString());
                frequencyS.setText(frequency);

                String callSign = inputCallSign.getText().toString();
                int receiveS = getSpinnerValue(spinnerReceiveS);
                int sendS = getSpinnerValue(spinnerSendS);

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                calendar.setTime(inputDateFormat.parse(dateEditText.getText().toString()));

                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                long timeInMillis = calendar.getTimeInMillis();

                dbHelper.insertLog(frequency, callSign, "", timeInMillis, receiveS, sendS);
                Toast.makeText(this, "Log saved successfully!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error saving log: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Handle DateEditText click
        dateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String date = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                dateEditText.setText(date);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null; // Clear the static reference
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

    private int getSpinnerValue(Spinner spinner) {
        try {
            return Integer.parseInt(spinner.getSelectedItem().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid S-value selection", Toast.LENGTH_SHORT).show();
            return 0;
        }
    }

    private Integer[] getSValueRange() {
        Integer[] sValues = new Integer[9];
        for (int i = 0; i < sValues.length; i++) {
            sValues[i] = i + 1;
        }
        return sValues;
    }
}
