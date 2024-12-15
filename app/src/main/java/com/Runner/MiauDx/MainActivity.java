package com.Runner.MiauDx;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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


    static MainActivity obj;

    private final Handler cleanupHandler = new Handler(Looper.getMainLooper());
    private HamRadioClusterConnection clusterConnection;
    private TextView logsView;
    private SpotsAdapter spotsAdapter;
    private Handler uiHandler;

    public static MainActivity getHandlerObj() {
        return obj;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        logsView = findViewById(R.id.logsView);
        RecyclerView spotsRecyclerView = findViewById(R.id.spotsRecyclerView);
        Button openPopupButton = findViewById(R.id.openFilterButton);

        Button ViewLog = findViewById(R.id.ViewLog);

        spotsAdapter = new SpotsAdapter(this);

        spotsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        spotsRecyclerView.setAdapter(spotsAdapter);

        uiHandler = new Handler(Looper.getMainLooper());

        String callsign = UserSettingsActivity.getCallsign(this);
        if (callsign == null || callsign.isEmpty()) {
            startActivity(new Intent(this, UserSettingsActivity.class));
        } else {

            clusterConnection = new HamRadioClusterConnection(this);

            clusterConnection.start();
        }
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


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.open_radio: {
                new ComPortManager(this);

                Intent intent = new Intent(MainActivity.this, ComPortConfigActivity.class);
                startActivity(intent);

            }
            return true;
            case R.id.donate: {
                // Create an intent to open the URL
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DONATE));

                // Start the activity
                startActivity(intent);
            }
            return true;
            case R.id.UserSettings: {
                startActivity(new Intent(this, UserSettingsActivity.class));

            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    public void addSpot(String frequency, String flag, String callSign, String location, String comment) {


        uiHandler.post(() -> {

            if (spotsAdapter.setSpot(frequency, flag, callSign, location, comment)) {
                logMessage("ADDED " + flag + callSign + "@" + frequency);
            } else {
                logMessage("REFRESH " + flag + callSign + "@" + frequency);
            }
        });
    }


}
