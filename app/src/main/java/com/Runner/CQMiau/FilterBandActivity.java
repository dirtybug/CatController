package com.Runner.CQMiau;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FilterBandActivity extends AppCompatActivity {

    // Constants for SharedPreferences
    private static final String PREFS_NAME = "BandPreferences";
    private static final String KEY_160M = "checkbox_160m";

    private static final String KEY_80M = "checkbox_80m";
    private static final String KEY_40M = "checkbox_40m";
    private static final String KEY_30M = "checkbox_30m";
    private static final String KEY_20M = "checkbox_20m";
    private static final String KEY_18M = "checkbox_18m";

    private static final String KEY_15M = "checkbox_15m";
    private static final String KEY_12M = "checkbox_12m";
    private static final String KEY_10M = "checkbox_10m";
    private static final String KEY_6M = "checkbox_6m";

    private static BandFilterCallback bandFilterCallback;
    Integer[] bandsArray;

    public static void setBandFilterCallback(BandFilterCallback callback) {
        bandFilterCallback = callback;
    }

    public static Integer[] getBandFilterArray(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        List<Integer> selectedBands = new ArrayList<>();
        if (preferences.getBoolean(KEY_160M, true)) selectedBands.add(1);  // 160m
        if (preferences.getBoolean(KEY_80M, true)) selectedBands.add(3);  // 80m
        if (preferences.getBoolean(KEY_40M, true)) selectedBands.add(7);  // 40m

        if (preferences.getBoolean(KEY_30M, true)) selectedBands.add(10);

        if (preferences.getBoolean(KEY_20M, true)) selectedBands.add(14); // 20m
        if (preferences.getBoolean(KEY_18M, true)) selectedBands.add(18); // 18m
        if (preferences.getBoolean(KEY_15M, true)) selectedBands.add(21); // 15m
        if (preferences.getBoolean(KEY_12M, true)) selectedBands.add(24); // 12m
        if (preferences.getBoolean(KEY_10M, true)) selectedBands.add(28); // 10m

        if (preferences.getBoolean(KEY_6M, true)) selectedBands.add(50); // 10m

        return selectedBands.toArray(new Integer[0]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);


        // Initialize checkboxes
        CheckBox checkbox160m = findViewById(R.id.checkbox_160m);
        CheckBox checkbox80m = findViewById(R.id.checkbox_80m);
        CheckBox checkbox40m = findViewById(R.id.checkbox_40m);
        CheckBox checkbox30m = findViewById(R.id.checkbox_30m);
        CheckBox checkbox20m = findViewById(R.id.checkbox_20m);
        CheckBox checkbox18m = findViewById(R.id.checkbox_18m);
        CheckBox checkbox15m = findViewById(R.id.checkbox_15m);
        CheckBox checkbox12m = findViewById(R.id.checkbox_12m);
        CheckBox checkbox10m = findViewById(R.id.checkbox_10m);
        CheckBox checkbox6m = findViewById(R.id.checkbox_6m);
        // Initialize the Exit button
        Button exitButton = findViewById(R.id.exitButton);

        // Load saved preferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        checkbox160m.setChecked(preferences.getBoolean(KEY_160M, true));
        checkbox80m.setChecked(preferences.getBoolean(KEY_80M, true));
        checkbox40m.setChecked(preferences.getBoolean(KEY_40M, true));
        checkbox30m.setChecked(preferences.getBoolean(KEY_30M, true));
        checkbox20m.setChecked(preferences.getBoolean(KEY_20M, true));
        checkbox18m.setChecked(preferences.getBoolean(KEY_18M, true));
        checkbox15m.setChecked(preferences.getBoolean(KEY_15M, true));
        checkbox12m.setChecked(preferences.getBoolean(KEY_12M, true));

        checkbox10m.setChecked(preferences.getBoolean(KEY_10M, true));
        checkbox6m.setChecked(preferences.getBoolean(KEY_6M, true));

        // Set Exit button action
        exitButton.setOnClickListener(v -> {


            // Save preferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_160M, checkbox160m.isChecked());
            editor.putBoolean(KEY_80M, checkbox80m.isChecked());
            editor.putBoolean(KEY_40M, checkbox40m.isChecked());
            editor.putBoolean(KEY_30M, checkbox30m.isChecked());
            editor.putBoolean(KEY_20M, checkbox20m.isChecked());
            editor.putBoolean(KEY_18M, checkbox18m.isChecked());
            editor.putBoolean(KEY_15M, checkbox15m.isChecked());
            editor.putBoolean(KEY_12M, checkbox12m.isChecked());
            editor.putBoolean(KEY_10M, checkbox10m.isChecked());
            editor.putBoolean(KEY_6M, checkbox6m.isChecked());
            editor.apply();
            bandsArray = getBandFilterArray(this);
            // Convert to array and send to callback
            if (bandFilterCallback != null) {
                bandFilterCallback.filterBand(bandsArray);
            }

            // Close the activity
            finish();
        });
    }

    public interface BandFilterCallback {
        void filterBand(Integer[] bandMeters);
    }
}
