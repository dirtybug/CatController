package com.example.catcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catcontroller.logbook.ViewLogsActivity;
import com.example.catcontroller.spot.SpotsAdapter;

import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "HamRadioCluster";
    private static final int MAX_LOG_LINES = 10; // Maximum number of lines to keep
    private static MainActivity obj;
    private final List<String> logLines = new LinkedList<>();
    private final Handler cleanupHandler = new Handler(Looper.getMainLooper());
    private HamRadioClusterConnection clusterConnection;
    private TextView logsView;
    private SpotsAdapter spotsAdapter;
    private Handler uiHandler;

    public static MainActivity getHandlerObj() {
        return obj;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logsView = findViewById(R.id.logsView);
        RecyclerView spotsRecyclerView = findViewById(R.id.spotsRecyclerView);
        Button openPopupButton = findViewById(R.id.openPopupButton);
        Button openRadio = findViewById(R.id.openRadio);
        Button ViewLog = findViewById(R.id.ViewLog);

        spotsAdapter = new SpotsAdapter(this);

        spotsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        spotsRecyclerView.setAdapter(spotsAdapter);

        uiHandler = new Handler(Looper.getMainLooper());


        clusterConnection = new HamRadioClusterConnection(
                this,

                "HB9IMH",

                this::addSpot
        );


        clusterConnection.start();
        MainActivity.obj = this;
        ViewLog.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewLogsActivity.class);
            startActivity(intent);
        });

        openPopupButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FilterBandActivity.class);
            FilterBandActivity.setBandFilterCallback(this::filterBand);
            startActivity(intent);
        });
        openRadio.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, ComPortConfigActivity.class);
                    startActivity(intent);


                }
        );


    }

    public void filterBand(Integer[] bandMeters) {
        spotsAdapter.selectBand(bandMeters);
    }

    private void onLog(String message) {
        Log.d(TAG, message);
    }

    public void logMessage(String message) {
        logsView.append(message + "\n");

        // Scroll to the bottom of the ScrollView
        ScrollView logScrollView = findViewById(R.id.logScrollView);
        logScrollView.post(() -> logScrollView.fullScroll(View.FOCUS_DOWN));

    }

    private void addSpot(String frequency, String callSign, String location) {


        uiHandler.post(() -> {

            if (spotsAdapter.setSpot(frequency, callSign, location)) {
                logMessage("ADDED " + callSign + "@" + frequency);
            } else {
                logMessage("REFRESH " + callSign + "@" + frequency);
            }
        });
    }


}
