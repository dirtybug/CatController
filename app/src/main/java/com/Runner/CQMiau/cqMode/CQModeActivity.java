package com.Runner.CQMiau.cqMode;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

public class CQModeActivity extends AppCompatActivity {

    private static CQModeActivity instance; // Static reference to the activity instance
    private static final List<Dx> dxList = new CopyOnWriteArrayList<>(); // Thread-safe shared DX list

    private Spinner spinnerReceiveS;
    DatePicker datePicker;
    private EditText frequecyS;
    TimePicker timePicker;
    private Spinner spinnerSendS;
    private EditText inputCallSign;
    private Button buttonDXMyself;
    private Button buttonLoadFreqFromRadio;
    private Button buttonSaveLog;
    private Button buttonSetTime;
    private RecyclerView recyclerView;
    private DXAdapter dxAdapter;
    private LogDatabaseHelper dbHelper;

    /**
     * Adds a new DX entry to the RecyclerView.
     */
    public static void addNewDX(String callSign, String flag, String comment, String location) {
        synchronized (dxList) {
            Dx newDx = new Dx(callSign, flag, comment, location);
            dxList.add(newDx);

            // Update the UI if the activity is running
            if (instance != null) {
                instance.runOnUiThread(() -> {
                    instance.dxAdapter.notifyItemInserted(dxList.size() - 1);
                    Toast.makeText(instance, "DX added: " + callSign, Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the state of RecyclerView
        if (recyclerView != null) {
            outState.putParcelable("recyclerViewState", recyclerView.getLayoutManager().onSaveInstanceState());
        }

        // Save other UI states
        outState.putString("frequency", frequecyS.getText().toString());
        outState.putString("callSign", inputCallSign.getText().toString());
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth(); // January = 0
        int year = datePicker.getYear();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        String time = String.format(Locale.getDefault(),
                "%04d-%02d-%02d %02d:%02d:00", year, month + 1, day, hour, minute);
        outState.putString("time", time);
        outState.putInt("receiveS", spinnerReceiveS.getSelectedItemPosition());
        outState.putInt("sendS", spinnerSendS.getSelectedItemPosition());
    }

    public static CQModeActivity getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CQModeActivity instance is not initialized or destroyed.");
        }
        return instance;
    }

    /**
     * Populates the call sign field with the provided value.
     */
    public static void populateCallSign(String callSign) {
        if (instance != null) {
            instance.inputCallSign.setText(callSign);
            Toast.makeText(instance, "Call Sign populated: " + callSign, Toast.LENGTH_SHORT).show();
        } else {
            throw new IllegalStateException("CQModeActivity instance is not initialized.");
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore other UI states
        frequecyS.setText(savedInstanceState.getString("frequency", ""));
        inputCallSign.setText(savedInstanceState.getString("callSign", ""));
        spinnerReceiveS.setSelection(savedInstanceState.getInt("receiveS", 0));
        spinnerSendS.setSelection(savedInstanceState.getInt("sendS", 0));

        // Restore RecyclerView state
        if (savedInstanceState.containsKey("recyclerViewState")) {
            recyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable("recyclerViewState"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cq_mode);
        this.dbHelper = new LogDatabaseHelper(this);

        // Assign static reference
        instance = this;

        // Initialize Views
        spinnerReceiveS = findViewById(R.id.spinner_receive_s);
        frequecyS = findViewById(R.id.input_frequecy_s);
        spinnerSendS = findViewById(R.id.spinner_sender_s);
        inputCallSign = findViewById(R.id.input_callsign);
         datePicker = findViewById(R.id.cqdatePicker);
         timePicker = findViewById(R.id.cqtimePicker);
         buttonDXMyself = findViewById(R.id.button_spot_myself);
        buttonLoadFreqFromRadio = findViewById(R.id.button_loadFreqFromRadio);
        buttonSaveLog = findViewById(R.id.button_SaveLog);
        buttonSetTime = findViewById(R.id.set_currentTime_button);
        recyclerView = findViewById(R.id.recycler_view);

        // Setup Spinners
        Integer[] sValues = getSValueRange();
        ArrayAdapter<Integer> sValueAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sValues);
        sValueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerReceiveS.setAdapter(sValueAdapter);
        spinnerSendS.setAdapter(sValueAdapter);

        // Initialize RecyclerView
        dxAdapter = new DXAdapter(dxList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dxAdapter);

        // Assign Functions to Buttons
        buttonDXMyself.setOnClickListener(v -> {
            String frequency = validateFrequency(frequecyS.getText().toString());
            frequecyS.setText(frequency);
            String callsign = UserSettingsActivity.getCallsign(this);

            String sentSpot = "DX " + callsign + " " + frequency;
            HamRadioClusterConnection.getHandlerObj().sendCommand(sentSpot);
        });

        buttonLoadFreqFromRadio.setOnClickListener(v -> {
            // TODO: Implement "Load Frequency from Radio" logic
        });

        buttonSaveLog.setOnClickListener(v -> {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth(); // January = 0
            int year = datePicker.getYear();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String formattedDateTime = String.format(Locale.getDefault(),
                    "%04d-%02d-%02d %02d:%02d:00", year, month + 1, day, hour, minute);

            String callSign = inputCallSign.getText().toString();
            String frequency = validateFrequency(frequecyS.getText().toString());
            frequecyS.setText(frequency);

            int selectedReceiveSValue = getSpinnerValue(spinnerReceiveS);
            int selectedSendSValue = getSpinnerValue(spinnerSendS);

            dbHelper.insertLog(frequency, callSign, "", formattedDateTime, selectedReceiveSValue, selectedSendSValue);
            String sentSpot = "DX " + callSign + " " + frequency;
            HamRadioClusterConnection.getHandlerObj().sendCommand(sentSpot);
        });

        buttonSetTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            timePicker.setIs24HourView(true);

            // Get the current date and time

            int currentYear = calendar.get(Calendar.YEAR);
            int currentMonth = calendar.get(Calendar.MONTH); // Note: January = 0
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = calendar.get(Calendar.MINUTE);

            // Set the current date on DatePicker
            datePicker.updateDate(currentYear, currentMonth, currentDay);

            // Set the current time on TimePicker
            timePicker.setHour(currentHour);
            timePicker.setMinute(currentMinute);
            String formattedDateTime = String.format(Locale.getDefault(),
                    "%04d-%02d-%02d %02d:%02d:00", currentYear, currentMonth + 1, currentDay, currentHour, currentMinute);

            Toast.makeText(this, "Current time set: " + formattedDateTime, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null; // Clear the static reference
    }

    /**
     * Validates the frequency, ensuring proper format.
     */
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

    /**
     * Safely gets the value from a Spinner as an integer.
     */
    private int getSpinnerValue(Spinner spinner) {
        try {
            return Integer.parseInt(spinner.getSelectedItem().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid S-value selection", Toast.LENGTH_SHORT).show();
            return 0; // Default value in case of error
        }
    }

    /**
     * Returns an array of S-numbers (1 to 9).
     */
    private Integer[] getSValueRange() {
        Integer[] sValues = new Integer[9]; // Correct size for 1 to 9
        for (int i = 0; i < sValues.length; i++) {
            sValues[i] = i + 1; // Populate 1 to 9
        }
        return sValues;
    }
}


