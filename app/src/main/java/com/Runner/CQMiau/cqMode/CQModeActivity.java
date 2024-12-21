package com.Runner.CQMiau.cqMode;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Runner.CQMiau.HamRadioClusterConnection;
import com.Runner.CQMiau.R;
import com.Runner.CQMiau.UserSettingsActivity;
import com.Runner.CQMiau.logbook.LogDatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CQModeActivity extends AppCompatActivity {

    private static CQModeActivity instance; // Static reference to the activity instance

    private Spinner spinnerReceiveS;
    private EditText frequecyS;
    private Spinner spinnerSendS;
    private EditText inputCallSign;
    private EditText inputTime;
    private Button buttonDXMyself;
    private Button buttonLoadFreqFromRadio;
    private Button buttonSaveLog;
    private Button buttonSetTime;
    private RecyclerView recyclerView;
    private DXAdapter dxAdapter;
    private List<Dx> dxList;
    private LogDatabaseHelper dbHelper;

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
        inputTime = findViewById(R.id.input_time);
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
        dxList = new ArrayList<>();
        dxAdapter = new DXAdapter(dxList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dxAdapter);

        // Assign Empty Lambda Functions to Buttons
        buttonDXMyself.setOnClickListener(v -> {

            String frequency = frequecyS.getText().toString();
            String callsign = UserSettingsActivity.getCallsign(this);


            String sentSpot = "DX " + callsign + " " + frequency;
            HamRadioClusterConnection.getHandlerObj().sendCommand(sentSpot);
        });

        buttonLoadFreqFromRadio.setOnClickListener(v -> {
            // TODO: Implement "Load Frequency from Radio" logic
        });

        buttonSaveLog.setOnClickListener(v -> {
            String callSign = inputCallSign.getText().toString();
            String editedTimeDate = inputTime.getText().toString();
            String frequency = frequecyS.getText().toString();


            int selectedReceiveSValue = Integer.parseInt(spinnerReceiveS.getSelectedItem().toString());
            int selectedSendSValue = Integer.parseInt(spinnerSendS.getSelectedItem().toString());

            dbHelper.insertLog(frequency, callSign, "", editedTimeDate, selectedReceiveSValue, selectedSendSValue);
            String sentSpot = "DX " + callSign + " " + frequency;
            HamRadioClusterConnection.getHandlerObj().sendCommand(sentSpot);

        });

        buttonSetTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            String currentTime = DateFormat.format("HH:mm:ss", calendar).toString();
            inputTime.setText(currentTime);
            Toast.makeText(this, "Current time set: " + currentTime, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Adds a new DX entry to the RecyclerView.
     */
    public void addNewDX(String callSign, String flag, String comment, String location) {
        runOnUiThread(() -> {
            Dx newDx = new Dx(callSign, flag, comment, location);
            dxList.add(newDx);
            dxAdapter.notifyItemInserted(dxList.size() - 1);
            Toast.makeText(this, "DX added: " + callSign, Toast.LENGTH_SHORT).show();

            // Clear input fields
            inputCallSign.setText("");
            inputTime.setText("");
        });
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

