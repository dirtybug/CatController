package com.example.catcontroller.logbook;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catcontroller.R;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private final List<Log> logs;

    public LogAdapter(List<Log> logs) {
        this.logs = logs;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        Log log = logs.get(position);
        holder.frequencyView.setText("Frequency: " + log.getFrequency());
        holder.callSignView.setText("Call Sign: " + log.getCallSign());
        holder.locationView.setText("Location: " + log.getLocation());
        holder.timeView.setText("Time: " + log.getTime());
        holder.receiveSValueView.setText("Receive S: " + log.getReceiveSValue());
        holder.sendSValueView.setText("Send S: " + log.getSendSValue());
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView frequencyView, callSignView, locationView, timeView, receiveSValueView, sendSValueView;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            frequencyView = itemView.findViewById(R.id.frequencyView);
            callSignView = itemView.findViewById(R.id.callSignView);
            locationView = itemView.findViewById(R.id.locationView);
            timeView = itemView.findViewById(R.id.timeView);
            receiveSValueView = itemView.findViewById(R.id.receiveSValueView);
            sendSValueView = itemView.findViewById(R.id.sendSValueView);
        }
    }
}
