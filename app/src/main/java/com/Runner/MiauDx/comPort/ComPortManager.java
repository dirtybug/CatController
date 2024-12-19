package com.Runner.MiauDx.comPort;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import com.Runner.MiauDx.MainActivity;
import com.felhr.usbserial.UsbSerialDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ComPortManager extends Thread {
    private static ComPortManager instance;

    private static final String ACTION_USB_PERMISSION = "com.Runner.MiauDx.comPort.USB_PERMISSION";

    private final UsbManager usbManager;
    private final BlockingQueue<String> commandQueue;
    private final Context context;
    private final List<UsbDevice> deviceList;

    private UsbDevice usbDevice;
    private UsbSerialDevice serialDevice;

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        onLog("Permission granted for device: " + device.getDeviceName());
                        connectToDevice(device);
                    } else {
                        onLog("Permission denied for device: " + device.getDeviceName());
                    }
                }
            }
        }
    };

    private ComPortManager(Context context) {
        this.context = context;
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        this.commandQueue = new LinkedBlockingQueue<>();
        this.deviceList = new ArrayList<>();

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
    }

    public static synchronized ComPortManager getInstance(Context context) {
        if (instance == null) {
            instance = new ComPortManager(context.getApplicationContext());
        }
        return instance;
    }

    public static synchronized ComPortManager getInstance() {

        return instance;
    }

    public void Connect() {

        String selectedDeviceId = ComPortConfigActivity.getInstance().getUsbDeviceId();
        // Request permission for the selected device
        if (!ComPortManager.getInstance().requestPermission(selectedDeviceId)) {
            onLog("No devices Selected.");
        }
    }

    public String[] getDeviceIds() {
        List<String> deviceNames = new ArrayList<>();
        deviceList.clear();

        HashMap<String, UsbDevice> deviceMap = usbManager.getDeviceList();
        for (UsbDevice device : deviceMap.values()) {
            deviceList.add(device);
            deviceNames.add(device.getDeviceName());
            onLog("Device found: " + device.getDeviceName());
        }

        return deviceNames.toArray(new String[0]);
    }

    public boolean requestPermission(String deviceName) {
        UsbDevice device = findDeviceByName(deviceName);
        if (device == null) {
            onLog("Device not found: " + deviceName);
            return false;
        }

        if (!usbManager.hasPermission(device)) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    new Intent(ACTION_USB_PERMISSION),
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            usbManager.requestPermission(device, permissionIntent);
            return false;
        } else {
            return connectToDevice(device);
        }
    }

    private UsbDevice findDeviceByName(String deviceName) {
        for (UsbDevice device : deviceList) {
            if (device.getDeviceName().equals(deviceName)) {
                return device;
            }
        }
        return null;
    }

    public boolean connectToDevice(UsbDevice device) {
        if (serialDevice != null && serialDevice.isOpen() && device.equals(usbDevice)) {
            onLog("Already connected to this device: " + device.getDeviceName());
            return true;
        }

        try {
            UsbDeviceConnection connection = usbManager.openDevice(device);
            if (connection == null) {
                onLog("Error: Unable to open device. Ensure permissions are granted.");
                return false;
            }

            UsbInterface usbInterface = device.getInterface(0);
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

            UsbSerialDevice newSerialDevice = UsbSerialDevice.createUsbSerialDevice(device, connection);
            if (newSerialDevice != null) {
                if (newSerialDevice.open()) {
                    configureSerialDevice(newSerialDevice);
                    serialDevice = newSerialDevice;
                    usbDevice = device;

                    onLog("Connected to " + device.getDeviceName());
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

    private void configureSerialDevice(UsbSerialDevice device) {
        device.setBaudRate(ComPortConfigActivity.getInstance().getBaudRate());
        device.setDataBits(ComPortConfigActivity.getInstance().getDataBits());
        device.setStopBits(ComPortConfigActivity.getInstance().getStopBits());
        device.setParity(ComPortConfigActivity.getInstance().getParity());
        device.setFlowControl(ComPortConfigActivity.getInstance().getFlowControl());

        device.read(this::onReceivedData);
    }

    private void onReceivedData(byte[] data) {
        String response = new String(data);
        MainActivity.getHandlerObj().logMessage("Response: " + response);
    }

    public void queueCommand(String command) {
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
            onLog("Serial device disconnected.");
        }

        context.unregisterReceiver(usbReceiver);
        onLog("USB receiver unregistered.");
    }

    private void onLog(String message) {
        MainActivity.getHandlerObj().logMessage(message);
    }
}
