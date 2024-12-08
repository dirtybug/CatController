package com.example.catcontroller;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SpotDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_detail);

        // Get data from the intent
        String frequency = getIntent().getStringExtra("frequency");
        String callSign = getIntent().getStringExtra("callSign");
        String location = getIntent().getStringExtra("location");
        String time = getIntent().getStringExtra("time");

        // Bind the data to views
        TextView frequencyView = findViewById(R.id.frequencyView);
        TextView callSignView = findViewById(R.id.callSignView);
        TextView locationView = findViewById(R.id.locationView);
        TextView timeView = findViewById(R.id.timeView);

        frequencyView.setText(frequency);
        callSignView.setText(callSign);
        locationView.setText(location);
        timeView.setText(time);
    }
}