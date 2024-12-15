package com.Runner.MiauDx;

import android.content.Context;
import android.util.Log;

import org.apache.commons.net.telnet.TelnetClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HamRadioClusterConnection extends Thread {

    private static final String TAG = "HamRadioCluster";

    private final TelnetClient telnetClient;
    private final String callsign;
    private static HamRadioClusterConnection obj = null;
    private JSONArray clusters;
    private DXCCLoader dxccload;
    private PrintWriter writer;
    private BufferedReader reader;
    private int currentIndex = 0;
    private BlockingQueue<String> commandQueue;
    private volatile boolean isConnected = false;

    public HamRadioClusterConnection(Context context) {
        try {
            InputStream is = context.getAssets().open("ham_radio_clusters.json");
            byte[] buffer = new byte[1024];
            int size = is.available();
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            clusters = new JSONArray(jsonString);
            obj = this;

            this.commandQueue = new LinkedBlockingQueue<>();
            this.dxccload = new DXCCLoader(context);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        callsign = UserSettingsActivity.getCallsign(context);


        this.telnetClient = new TelnetClient();
    }

    public static HamRadioClusterConnection getHandlerObj() {
        return obj;
    }

    @Override
    public void run() {
        while (true) {
            if (!isConnected) {
                if (!attemptConnection()) {
                    onLog("Retrying connection...");
                    continue;
                }
            }

            try {
                String line;
                while (isConnected && (line = reader.readLine()) != null) {
                    processCommandQueue();
                    onLog("Received line: " + line);
                    extractSpotInfo(line); // Keeping your original method untouched
                }
            } catch (IOException e) {
                onLog("Connection lost: " + e.getMessage());
                disconnect();
            }
        }
    }

    private boolean attemptConnection() {
        try {
            JSONObject cluster = getNextCluster();
            if (cluster == null) {
                onLog("No more clusters to connect to.");
                return false;
            }

            String host = cluster.getString("host");
            int port = cluster.getInt("port");

            onLog("Attempting to connect to cluster at " + host + ":" + port);
            telnetClient.connect(host, port);

            writer = new PrintWriter(telnetClient.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(telnetClient.getInputStream()));

            onLog("Connected to cluster at " + host + ":" + port);
            writer.println(callsign);
            onLog("Sent callsign: " + callsign);

            writer.println("SET/DXITU");

            isConnected = true;
            return true;
        } catch (Exception e) {
            onLog("Failed to connect to cluster: " + e.getMessage());
            disconnect();
            return false;
        }
    }

    private JSONObject getNextCluster() {
        if (clusters == null || clusters.length() == 0) {
            onLog("Cluster list is empty.");
            return null;
        }

        JSONObject cluster = clusters.optJSONObject(currentIndex);
        currentIndex = (currentIndex + 1) % clusters.length(); // Circular iteration
        return cluster;
    }

    private void processCommandQueue() {
        try {
            while (!commandQueue.isEmpty()) {
                String command = commandQueue.take();
                sendCommandDirect(command);
            }
        } catch (InterruptedException e) {
            onLog("Command processing interrupted.");
        }
    }

    private void sendCommandDirect(String command) {
        try {
            if (writer != null) {
                writer.println(command);
                writer.flush();
                onLog("Command sent: " + command);
            } else {
                onLog("Writer is not initialized. Command not sent.");
            }
        } catch (Exception e) {
            onLog("Error while sending command: " + e.getMessage());
        }
    }

    private void onLog(String message) {
        Log.d(TAG, message);
    }

    public void sendCommand(String command) {
        try {
            commandQueue.put(command); // Queue the command
        } catch (InterruptedException e) {
            onLog("Failed to queue command: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (telnetClient != null && telnetClient.isConnected()) {
                telnetClient.disconnect();
                onLog("Disconnected from cluster.");
            }
        } catch (Exception e) {
            onLog("Error during disconnect: " + e.getMessage());
        } finally {
            isConnected = false;
        }
    }

    private void extractSpotInfo(String line) {
        try {
            // Example line: "DX de SV8SYK:    18100.0  N4ZR                                        2014Z"
            String[] parts = line.split("\\s+");
            if (parts.length >= 5 && parts[0].equals("DX") && parts[1].equals("de")) {
                String spotter = line.substring(6, 16).trim();        // Characters 7-16 (spotter callsign)
                String frequency = line.substring(17, 26).trim();    // Characters 19-27 (frequency)
                String dxCallSign = line.substring(26, 39).trim();      // Characters 29-40 (spotted callsign)
                String time = line.substring(70, 75).trim();         // Characters 42-46 (time)
                String coment = line.substring(39, 67);
                String location = parts[parts.length - 3];
                location += ", " + parts[parts.length - 1];

                if (dxCallSign.equals(this.callsign)) {
                    String spoter = spotter.replace(":", "");
                    onLog("You were spoted By:" + spoter);
                } else {
                    String flag = this.dxccload.getFlagFromCallSign(dxCallSign);
                    MainActivity.getHandlerObj().addSpot(frequency, flag, dxCallSign, location, coment);
                }
            }

            Log.v(TAG, line);
        } catch (Exception e) {
            onLog("Failed to parse spot info: " + e.getMessage());
            disconnect();
        }
    }


}
