package com.Runner.CQMiau.spot;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Runner.CQMiau.R;


public class SpotViewHolder extends RecyclerView.ViewHolder {
    final TextView frequencyView;
    final TextView callSignView;
    final TextView locationView;
    final TextView time;

    public SpotViewHolder(@NonNull View itemView) {
        super(itemView);
        frequencyView = itemView.findViewById(R.id.frequencyView);
        callSignView = itemView.findViewById(R.id.callSignView);
        locationView = itemView.findViewById(R.id.locationView);
        time = itemView.findViewById(R.id.timeView);
    }

    public void bind(Spot spot, Context context) {
        frequencyView.setText(spot.getFrequency());
        callSignView.setText(spot.getFlag() + spot.getCallSign());
        locationView.setText(spot.getComment());
        time.setText(spot.getTime());

        // Set click listener to open SpotDetailActivity
        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SpotDetailActivity.class);

            // Pass data to the new activity
            intent.putExtra("frequency", spot.getFrequency());
            intent.putExtra("callSign", spot.getCallSign());
            intent.putExtra("location", spot.getLocation());
            intent.putExtra("comment", spot.getComment());

            intent.putExtra("time", spot.getTime());
            intent.putExtra("flag", spot.getFlag());


            context.startActivity(intent); // Start the new activity
        });
    }
}