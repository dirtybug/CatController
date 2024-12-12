package com.example.catcontroller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.felhr.usbserial.UsbSerialInterface;

public class ComPortConfigActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ComPortConfigPrefs";
    private static final String KEY_BAUD_RATE = "baudRate";
    private static final String KEY_DATA_BITS = "dataBits";
    private static final String KEY_STOP_BITS = "stopBits";
    private static final String KEY_PARITY = "parity";
    private static final String KEY_FLOW_CONTROL = "flowControl";

    private static ComPortConfigActivity instance;
    private int baudRate;
    private int dataBits;
    private int stopBits;
    private int parity;
    private int flowControl;

    public static ComPortConfigActivity getInstance() {
        return instance;
    }

    public int getBaudRate() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.baudRate = preferences.getInt(KEY_BAUD_RATE, 38400);
        return baudRate;
    }


    public int getDataBits() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.dataBits = preferences.getInt(KEY_DATA_BITS, UsbSerialInterface.DATA_BITS_8);
        return dataBits;
    }


    public int getStopBits() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.stopBits = preferences.getInt(KEY_STOP_BITS, UsbSerialInterface.STOP_BITS_1);
        return stopBits;
    }


    public int getParity() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.parity = preferences.getInt(KEY_PARITY, UsbSerialInterface.PARITY_NONE);
        return parity;
    }


    public int getFlowControl() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.flowControl = preferences.getInt(KEY_FLOW_CONTROL, UsbSerialInterface.FLOW_CONTROL_OFF);
        return flowControl;
    }

    public void setFlowControl(int flowControl) {
        this.flowControl = flowControl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com_port_config);

        instance = this;

        // Initialize spinners
        Spinner baudRateSpinner = findViewById(R.id.baudRateSpinner);
        Spinner dataBitsSpinner = findViewById(R.id.dataBitsSpinner);
        Spinner stopBitsSpinner = findViewById(R.id.stopBitsSpinner);
        Spinner paritySpinner = findViewById(R.id.paritySpinner);
        Spinner flowControlSpinner = findViewById(R.id.flowControlSpinner);

        // Set up baud rate options
        Integer[] baudRates = {300, 1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200};
        ArrayAdapter<Integer> baudRateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, baudRates);
        baudRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        baudRateSpinner.setAdapter(baudRateAdapter);

        // Set up data bits options
        Integer[] dataBits = {5, 6, 7, 8};
        ArrayAdapter<Integer> dataBitsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataBits);
        dataBitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataBitsSpinner.setAdapter(dataBitsAdapter);

        // Set up stop bits options
        String[] stopBits = {"1", "1.5", "2"};
        ArrayAdapter<String> stopBitsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stopBits);
        stopBitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stopBitsSpinner.setAdapter(stopBitsAdapter);

        // Set up parity options
        String[] parity = {"None", "Odd", "Even", "Mark", "Space"};
        ArrayAdapter<String> parityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, parity);
        parityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paritySpinner.setAdapter(parityAdapter);

        // Set up flow control options
        String[] flowControl = {"Off", "RTS/CTS", "DSR/DTR", "XON/XOFF"};
        ArrayAdapter<String> flowControlAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, flowControl);
        flowControlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        flowControlSpinner.setAdapter(flowControlAdapter);

        // Load saved preferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.baudRate = preferences.getInt(KEY_BAUD_RATE, 38400);
        this.dataBits = preferences.getInt(KEY_DATA_BITS, UsbSerialInterface.DATA_BITS_8);


        this.stopBits = preferences.getInt(KEY_STOP_BITS, UsbSerialInterface.STOP_BITS_1);
        this.parity = preferences.getInt(KEY_PARITY, UsbSerialInterface.PARITY_NONE);
        this.flowControl = preferences.getInt(KEY_FLOW_CONTROL, UsbSerialInterface.FLOW_CONTROL_OFF);

        // Set spinner selections based on saved preferences
        baudRateSpinner.setSelection(getIndex(baudRates, baudRate));
        dataBitsSpinner.setSelection(getIndex(dataBits, dataBits));
        stopBitsSpinner.setSelection(this.stopBits - 1);
        paritySpinner.setSelection(this.parity);
        flowControlSpinner.setSelection(this.flowControl);

        // Set up Apply button
        Button applyButton = findViewById(R.id.applyButton);
        applyButton.setOnClickListener(v -> {
            // Retrieve selected values
            this.baudRate = (Integer) baudRateSpinner.getSelectedItem();
            this.dataBits = (Integer) dataBitsSpinner.getSelectedItem();
            this.stopBits = stopBitsSpinner.getSelectedItemPosition() + 1;
            this.parity = paritySpinner.getSelectedItemPosition();
            this.flowControl = flowControlSpinner.getSelectedItemPosition();

            // Save preferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(KEY_BAUD_RATE, this.baudRate);
            editor.putInt(KEY_DATA_BITS, this.dataBits);
            editor.putInt(KEY_STOP_BITS, this.stopBits);
            editor.putInt(KEY_PARITY, this.parity);
            editor.putInt(KEY_FLOW_CONTROL, this.flowControl);
            editor.apply();

            ComPortManager radio = new ComPortManager(this);
            radio.getAvailableDevices();

            // Close the configuration window
            finish();
        });
    }

    private <T> int getIndex(T[] array, T value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return 0; // Default to the first item if not found
    }
}
