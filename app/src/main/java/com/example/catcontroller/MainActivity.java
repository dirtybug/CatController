package com.example.catcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private static final int MAX_LOG_LINES = 10; // Maximum number of lines to keep
    private final List<String> logLines = new LinkedList<>();
    private final Handler cleanupHandler = new Handler(Looper.getMainLooper());
    private HamRadioClusterConnection clusterConnection;
    private TextView logsView;
    private SpotsAdapter spotsAdapter;
    private Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logsView = findViewById(R.id.logsView);
        RecyclerView spotsRecyclerView = findViewById(R.id.spotsRecyclerView);
        Button openPopupButton = findViewById(R.id.openPopupButton);
        Button openRadio = findViewById(R.id.openRadio);
        spotsAdapter = new SpotsAdapter();
        spotsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        spotsRecyclerView.setAdapter(spotsAdapter);

        uiHandler = new Handler(Looper.getMainLooper());


        try {
            InputStream is = getAssets().open("ham_radio_clusters.json");
            byte[] buffer = new byte[1024];
            int size = is.available();
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONArray clusters = new JSONArray(jsonString);
            clusterConnection = new HamRadioClusterConnection(
                    clusters,
                    "HB9IMH",
                    this::logMessage,
                    this::addSpot
            );


            clusterConnection.start();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        openPopupButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PopupActivity.class);
            PopupActivity.setBandFilterCallback(this::filterBand);
            startActivity(intent);
        });
        openRadio.setOnClickListener(v -> {
                    FT891CommManager radio = new FT891CommManager(this);
                    radio.getAvailableDevices();

                }
        );


    }

    public void filterBand(Integer[] bandMeters) {
        spotsAdapter.selectBand(bandMeters);
    }


    private void logMessage(String message) {
        if (logLines.size() >= MAX_LOG_LINES) {
            logLines.remove(0); // Remove the oldest log
        }
        logLines.add(message);

        // Update the TextView
        runOnUiThread(() -> logsView.setText(TextUtils.join("\n", logLines)));
    }

    private void addSpot(String frequency, String callSign, String location) {


        uiHandler.post(() -> {

            if (spotsAdapter.setSpot(frequency, callSign, location)) {
                logMessage("ADDED " + callSign);
            } else {
                logMessage("REFRESH " + callSign);
            }
        });
    }


}
