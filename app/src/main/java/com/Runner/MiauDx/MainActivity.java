package com.Runner.MiauDx;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Runner.MiauDx.comPort.ComPortConfigActivity;
import com.Runner.MiauDx.comPort.ComPortManager;
import com.Runner.MiauDx.logbook.ViewLogsActivity;
import com.Runner.MiauDx.spot.SpotsAdapter;


public class MainActivity extends AppCompatActivity {

    private static final String DONATE = "https://www.paypal.com/donate/?hosted_button_id=BJ44UH97D4MMS";
    private static final String TAG = "HamRadioCluster";

    private static MainActivity obj;

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
        Button openLinkButton = findViewById(R.id.donate);

        // Set up the button click listener
        openLinkButton.setOnClickListener(v -> {
            // The URL to be opened


            // Create an intent to open the URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DONATE));

            // Start the activity
            startActivity(intent);
        });
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
                    new ComPortManager(this);

                    Intent intent = new Intent(MainActivity.this, ComPortConfigActivity.class);
                    startActivity(intent);


                }
        );


    }

    public void filterBand(Integer[] bandMeters) {
        spotsAdapter.selectBand(bandMeters);
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
