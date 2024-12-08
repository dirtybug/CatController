package com.example.catcontroller;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FT891CommManager extends Thread {

    private static final String TAG = "HamRadioCluster";
    private static final String ACTION_USB_PERMISSION = "com.example.catcontroller.USB_PERMISSION";

    private final UsbManager usbManager;
    private final BlockingQueue<String> commandQueue;
    private final Context context;
    private UsbDevice usbDevice;
    private UsbSerialDevice serialDevice;
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        onLog("Permission granted for device: " + device.getDeviceName());
                        connect(device);
                    } else {
                        onLog("Permission denied for device: " + device.getDeviceName());
                    }
                }
            }
        }
    };
    private List<String> deviceList;

    public FT891CommManager(Context context) {
        this.context = context;

        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        this.commandQueue = new LinkedBlockingQueue<>();

        // Register receiver for USB permission
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(usbReceiver, filter);
    }

    public void getAvailableDevices() {
        deviceList = new ArrayList<>();
        HashMap<String, UsbDevice> deviceMap = usbManager.getDeviceList();
        for (UsbDevice device : deviceMap.values()) {
            deviceList.add(device.getDeviceName());
            onLog(device.getDeviceName());
            requestPermission(device);
        }
    }

    private void requestPermission(UsbDevice device) {
        if (!usbManager.hasPermission(device)) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    new Intent(ACTION_USB_PERMISSION),
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            usbManager.requestPermission(device, permissionIntent);
        } else {
            connect(device);
        }
    }

    public boolean connect(UsbDevice device) {
        try {
            this.usbDevice = device;
            onLog("Device Name: " + device.getDeviceName());
            onLog("Vendor ID: " + device.getVendorId());
            onLog("Product ID: " + device.getProductId());

            if (device == null) {
                onLog("Error: Device not found.");
                return false;
            }

            UsbDeviceConnection connection = usbManager.openDevice(device);
            if (connection == null) {
                onLog("Error: Unable to open device. Ensure permissions are granted.");
                return false;
            }

            UsbInterface usbInterface = device.getInterface(0); // Replace index if needed
            if (usbInterface == null) {
                onLog("Error: UsbInterface is null.");
                return false;
            }

            boolean claimed = connection.claimInterface(usbInterface, true);
            if (!claimed) {
                onLog("Error: Could not claim interface.");
                connection.close();
                return false;
            }

            UsbSerialDevice serialDevice = UsbSerialDevice.createUsbSerialDevice(usbDevice, connection);
            if (serialDevice != null) {
                if (serialDevice.open()) {
                    serialDevice.setBaudRate(38400);
                    serialDevice.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    serialDevice.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    serialDevice.setParity(UsbSerialInterface.PARITY_NONE);
                    serialDevice.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);

                    this.serialDevice = serialDevice;
                    serialDevice.read(this::onReceivedData);
                    onLog("Connected to " + usbDevice.getDeviceName());

                    // Send initial commands
                    sendAndReceiveCommand("ID;");
                    sendAndReceiveCommand("FA;");

                    // Start the thread
                    this.start();
                    return true;
                } else {
                    onLog("Failed to open serial device.");
                }
            } else {
                onLog("Device not supported or driver unavailable.");
            }
        } catch (Exception e) {
            onLog("Error connecting to device: " + e.getMessage());
        }
        return false;
    }

    private void onLog(String message) {
        Log.d(TAG, message);
    }

    private void onReceivedData(byte[] data) {
        String response = new String(data);
        onLog("Response: " + response);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                String command = commandQueue.take();
                sendCommand(command);
            }
        } catch (InterruptedException e) {
            onLog("Command sender thread interrupted.");
        }
    }

    public void disconnect() {
        this.interrupt();

        if (serialDevice != null) {
            serialDevice.close();
            onLog("Disconnected.");
        }

        // Unregister the USB receiver
        context.unregisterReceiver(usbReceiver);
    }

    public void queueCommand(String command) {
        if (!command.endsWith(";")) {
            command += ";";
        }
        try {
            commandQueue.put(command);
        } catch (InterruptedException e) {
            onLog("Failed to queue command: " + e.getMessage());
        }
    }

    private void sendCommand(String command) {
        if (serialDevice == null) {
            onLog("Not connected to any device.");
            return;
        }

        try {
            serialDevice.write(command.getBytes());
            onLog("Command sent: " + command);
        } catch (Exception e) {
            onLog("Failed to send command: " + e.getMessage());
        }
    }

    private void sendAndReceiveCommand(String command) {
        queueCommand(command);
    }
}