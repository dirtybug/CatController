package com.example.catcontroller;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PopupActivity extends AppCompatActivity {

    private static BandFilterCallback bandFilterCallback;

    public static void setBandFilterCallback(BandFilterCallback callback) {
        bandFilterCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        // Initialize checkboxes
        CheckBox checkbox160m = findViewById(R.id.checkbox_160m);
        CheckBox checkbox80m = findViewById(R.id.checkbox_80m);
        CheckBox checkbox40m = findViewById(R.id.checkbox_40m);
        CheckBox checkbox20m = findViewById(R.id.checkbox_20m);
        CheckBox checkbox15m = findViewById(R.id.checkbox_15m);
        CheckBox checkbox10m = findViewById(R.id.checkbox_10m);

        // Initialize the Exit button
        Button exitButton = findViewById(R.id.exitButton);

        // Set Exit button action
        exitButton.setOnClickListener(v -> {
            // Collect selected bands
            List<Integer> selectedBands = new ArrayList<>();
            if (checkbox160m.isChecked()) selectedBands.add(1);  // 160m
            if (checkbox80m.isChecked()) selectedBands.add(3);  // 80m
            if (checkbox40m.isChecked()) selectedBands.add(7);  // 40m
            if (checkbox20m.isChecked()) selectedBands.add(14); // 20m
            if (checkbox15m.isChecked()) selectedBands.add(21); // 15m
            if (checkbox10m.isChecked()) selectedBands.add(28); // 10m

            // Convert to array and send to callback
            if (bandFilterCallback != null) {
                Integer[] bandsArray = new Integer[selectedBands.size()];
                for (int i = 0; i < selectedBands.size(); i++) {
                    bandsArray[i] = selectedBands.get(i);
                }
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
