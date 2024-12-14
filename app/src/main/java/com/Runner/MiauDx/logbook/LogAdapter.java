package com.Runner.MiauDx.logbook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Runner.MiauDx.R;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {


    private final List<Log> logs;
    private final Context context;

    // Constructor with context and logs
    public LogAdapter(Context context, List<Log> logs) {
        this.context = context;
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

        // Handle Edit Button
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditLogActivity.class);
            intent.putExtra("LOG_ID", log.getId());
            EditLogActivity.setLogUpdateCallback(this::updateLogs);
            context.startActivity(intent);
        });

        // Handle Delete Button
        holder.deleteButton.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle("Delete Log")
                .setMessage("Are you sure you want to delete this log?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    LogDatabaseHelper.getInstance(context).deleteLog(log.getId());
                    logs.remove(position);
                    notifyItemRemoved(position);
                })
                .setNegativeButton("No", null)
                .show());
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public void updateLogs(int id) {
        // Find the log index by ID
        for (int i = 0; i < logs.size(); i++) {
            if (logs.get(i).getId() == id) {
                // Reload the updated log from the database
                Log updatedLog = LogDatabaseHelper.getInstance(context).getLogById(id);
                if (updatedLog != null) {
                    logs.set(i, updatedLog); // Update the specific log in the list
                    notifyItemChanged(i); // Notify RecyclerView to refresh only the updated item
                }
                return;
            }
        }
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView frequencyView, callSignView, locationView, timeView, receiveSValueView, sendSValueView;
        Button editButton, deleteButton;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            frequencyView = itemView.findViewById(R.id.frequencyLogView);
            callSignView = itemView.findViewById(R.id.callSignLogView);
            locationView = itemView.findViewById(R.id.locationLogView);
            timeView = itemView.findViewById(R.id.timeLogView);
            receiveSValueView = itemView.findViewById(R.id.receiveLogSValueView);
            sendSValueView = itemView.findViewById(R.id.sendLogSValueView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
