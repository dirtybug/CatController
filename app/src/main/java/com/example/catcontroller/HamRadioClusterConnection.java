package com.example.catcontroller;

import org.apache.commons.net.telnet.TelnetClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class HamRadioClusterConnection extends Thread {
    private final JSONArray clusters;
    private final String callsign;
    private final FreqCallback freqCallback;
    private final LogCallback logCallback;
    private final TelnetClient telnetClient;
    private PrintWriter writer;
    private BufferedReader reader;
    private int currentIndex = 0;

    public HamRadioClusterConnection(JSONArray clusters, String callsign, LogCallback logCallback, FreqCallback freqCallback) {
        this.clusters = clusters;
        this.callsign = callsign;
        this.logCallback = logCallback;
        this.freqCallback = freqCallback;
        this.telnetClient = new TelnetClient();
    }

    @Override
    public void run() {
        while (true) {

            while (attemptConnection() == false) {

            }
            listenForSpots();
        }


    }

    private boolean attemptConnection() {
        try {
            JSONObject cluster = getNextCluster();
            if (cluster == null) {
                logCallback.onLog("No more clusters to connect to.");
                return false;
            }

            String host = cluster.getString("host");
            int port = cluster.getInt("port");

            logCallback.onLog("Attempting to connect to cluster at " + host + ":" + port);
            telnetClient.connect(host, port);

            writer = new PrintWriter(telnetClient.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(telnetClient.getInputStream()));

            logCallback.onLog("Connected to cluster at " + host + ":" + port);
            writer.println(callsign);
            logCallback.onLog("Sent callsign: " + callsign);


            return true;
        } catch (Exception e) {
            logCallback.onLog("Failed to connect to cluster: " + e.getMessage());
            disconnect();
            return false;
        }
    }

    private JSONObject getNextCluster() {
        if (clusters == null || clusters.length() == 0) {
            logCallback.onLog("Cluster list is empty.");
            return null;
        }

        JSONObject cluster = clusters.optJSONObject(currentIndex);
        currentIndex = (currentIndex + 1) % clusters.length(); // Circular iteration
        return cluster;
    }

    private void listenForSpots() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                logCallback.onLog("Received line: " + line);
                extractSpotInfo(line);
            }
        } catch (Exception e) {
            logCallback.onLog("Error while listening for spots: " + e.getMessage());
        }
    }

    private void extractSpotInfo(String line) {
        try {
            // Example line: "DX de SV8SYK:    18100.0  N4ZR                                        2014Z"
            String[] parts = line.split("\\s+");
            if (parts.length >= 5 && parts[0].equals("DX") && parts[1].equals("de")) {
                String frequency = parts[3];
                String callSign = parts[4];
                String location = "";
                if (parts.length > 6) {
                    location = parts[5];
                }

                freqCallback.onAdd(frequency, callSign, location);
            }
        } catch (Exception e) {
            logCallback.onLog("Failed to parse spot info: " + e.getMessage());
            disconnect();
        }
    }

    public void sendCommad(String commad) {

        synchronized (this) {
            try {
                if (writer != null) {
                    writer.println(commad);
                    writer.flush();
                    logCallback.onLog("Command sent: " + commad);
                } else {
                    logCallback.onLog("Writer is not initialized. Command not sent.");
                }
            } catch (Exception e) {
                logCallback.onLog("Error while sending command: " + e.getMessage());
            }
        }

    }

    public void disconnect() {
        try {
            if (telnetClient != null && telnetClient.isConnected()) {
                telnetClient.disconnect();
                logCallback.onLog("Disconnected from cluster.");
            }
        } catch (Exception e) {
            logCallback.onLog("Error during disconnect: " + e.getMessage());
        }
    }

    public interface LogCallback {
        void onLog(String message);
    }

    public interface FreqCallback {
        void onAdd(String frequency, String callSign, String location);
    }
}
