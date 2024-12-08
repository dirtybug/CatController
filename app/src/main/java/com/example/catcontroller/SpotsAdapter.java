package com.example.catcontroller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SpotsAdapter extends RecyclerView.Adapter<SpotsAdapter.SpotViewHolder> {

    private static final long SPOT_EXPIRATION_THRESHOLD = 7 * 60 * 1000; // 7-minute expiration threshold
    private final Map<String, Spot> spotMap = new HashMap<>();
    private final List<Spot> visibleSpots = new ArrayList<>(); // Spots to display in the RecyclerView
    private Integer[] selectedBand = {1, 3, 7, 14, 21, 28}; // Current selected band in meters (0 = show all)

    public SpotsAdapter() {
    }

    @NonNull
    @Override
    public SpotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spot_item, parent, false);
        return new SpotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpotViewHolder holder, int position) {
        Spot spot = visibleSpots.get(position);
        if (spot == null) return;
        holder.frequencyView.setText(spot.getFrequency());
        holder.callSignView.setText(spot.getCallSign());
        holder.locationView.setText(spot.getMessage());
        holder.time.setText(spot.getTime());
    }

    @Override
    public int getItemCount() {
        return visibleSpots.size();
    }

    /**
     * Selects a band to display (in meters).
     * If no band is selected (bandMeters = 0), shows all spots.
     */
    public void selectBand(Integer[] bandMeters) {
        this.selectedBand = bandMeters;
        filterSpots();
    }

    /**
     * Adds or updates a spot in the map and refreshes the visible list.
     */
    public boolean setSpot(String frequency, String callSign, String location) {
        String key = callSign; // Unique key based on the call sign
        long currentTime = System.currentTimeMillis(); // Current timestamp
        boolean hasAdded = false;
        if (spotMap.containsKey(key)) {
            Spot existingSpot = spotMap.get(key);
            if (existingSpot != null) {
                existingSpot.setTimestamp(currentTime); // Update the timestamp
            }
        } else {
            // Add a new spot
            Spot newSpot = new Spot(frequency, callSign, location, currentTime);
            spotMap.put(key, newSpot);

            hasAdded = true;

        }


        filterSpots(); // Refresh the visible spots

        return hasAdded;
    }

    private void filterSpots() {
        visibleSpots.clear();

        for (Spot spot : spotMap.values()) {
            int frequency = (int) (Double.parseDouble(spot.getFrequency()) / 1000); // Convert frequency to band in meters

            // Check if frequency matches any of the selected bands
            boolean isMatch = containsBand(frequency);

            if (isMatch) {
                visibleSpots.add(spot);
            }
        }

        startSpotCleanupTask();
        notifyDataSetChanged();
    }

    /**
     * Helper method to check if a frequency matches any selected band.
     */
    private boolean containsBand(int frequency) {
        if (selectedBand == null) return true;
        for (Integer band : selectedBand) {
            if (band == 0 || band == frequency) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes spots older than the expiration threshold.
     */
    private void startSpotCleanupTask() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, Spot>> iterator = spotMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Spot> entry = iterator.next();
            Spot spot = entry.getValue();

            if (currentTime - spot.getTimestamp() > SPOT_EXPIRATION_THRESHOLD) {
                iterator.remove(); // Remove expired spot
            }
        }
    }

    public static class SpotViewHolder extends RecyclerView.ViewHolder {
        TextView frequencyView, callSignView, locationView, time;

        public SpotViewHolder(@NonNull View itemView) {
            super(itemView);
            frequencyView = itemView.findViewById(R.id.frequencyView);
            callSignView = itemView.findViewById(R.id.callSignView);
            locationView = itemView.findViewById(R.id.locationView);
            time = itemView.findViewById(R.id.time);
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
}
