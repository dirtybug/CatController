package com.example.catcontroller;

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

public class HamRadioClusterConnection extends Thread {

    private static final String TAG = "HamRadioCluster";
    private final FreqCallback freqCallback;
    private final TelnetClient telnetClient;
    private final String callsign;
    private JSONArray clusters;
    private DXCCLoader dxccload;
    private PrintWriter writer;
    private BufferedReader reader;
    private int currentIndex = 0;

    public HamRadioClusterConnection(Context context, String callsign, FreqCallback freqCallback) {
        try {
            InputStream is = context.getAssets().open("ham_radio_clusters.json");
            byte[] buffer = new byte[1024];
            int size = is.available();
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            clusters = new JSONArray(jsonString);


            this.dxccload = new DXCCLoader(context);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.callsign = callsign;
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

    private void listenForSpots() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                onLog("Received line: " + line);
                extractSpotInfo(line);
            }
        } catch (Exception e) {
            onLog("Error while listening for spots: " + e.getMessage());
        }
    }

    private void onLog(String message) {
        Log.d(TAG, message);
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
                String flag = this.dxccload.getFlagFromCallSign(callSign);
                freqCallback.onAdd(frequency, flag + " " + callSign, location);
            }
        } catch (Exception e) {
            onLog("Failed to parse spot info: " + e.getMessage());
            disconnect();
        }
    }

    public void sendCommad(String commad) {

        synchronized (this) {
            try {
                if (writer != null) {
                    writer.println(commad);
                    writer.flush();
                    onLog("Command sent: " + commad);
                } else {
                    onLog("Writer is not initialized. Command not sent.");
                }
            } catch (Exception e) {
                onLog("Error while sending command: " + e.getMessage());
            }
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
        }
    }


    public interface FreqCallback {

        void onAdd(String frequency, String callSign, String location);
    }
}
