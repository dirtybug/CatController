package com.example.catcontroller.spot;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catcontroller.R;

public class SpotViewHolder extends RecyclerView.ViewHolder {
    TextView frequencyView, callSignView, locationView, time;

    public SpotViewHolder(@NonNull View itemView) {
        super(itemView);
        frequencyView = itemView.findViewById(R.id.frequencyView);
        callSignView = itemView.findViewById(R.id.callSignView);
        locationView = itemView.findViewById(R.id.locationView);
        time = itemView.findViewById(R.id.timeView);
    }

    public void bind(Spot spot, Context context) {
        frequencyView.setText(spot.getFrequency());
        callSignView.setText(spot.getCallSign());
        locationView.setText(spot.getMessage());
        time.setText(spot.getTime());

        // Set click listener to open SpotDetailActivity
        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SpotDetailActivity.class);

            // Pass data to the new activity
            intent.putExtra("frequency", spot.getFrequency());
            intent.putExtra("callSign", spot.getCallSign());
            intent.putExtra("location", spot.getMessage());
            intent.putExtra("time", spot.getTime());

            context.startActivity(intent); // Start the new activity
        });
    }
}