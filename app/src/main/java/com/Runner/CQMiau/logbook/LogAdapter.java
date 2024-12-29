package com.Runner.CQMiau.logbook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Runner.CQMiau.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {


    private final List<LogBook> logs;
    private final Context context;
    private LogBook log;

    // Constructor with context and logs
    public LogAdapter(Context context, List<LogBook> logs) {
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
        this.log = logs.get(position);

        holder.frequencyView.setText("Frequency: " + log.getFrequency());
        holder.callSignView.setText("Call Sign: " + log.getCallSign());
        holder.locationView.setText("Location: " + log.getLocation());
        holder.timeView.setText("Time: " + log.getTimeStr());
        holder.receiveSValueView.setText("Receive S: " + log.getReceiveSValue());
        holder.sendSValueView.setText("Send S: " + log.getSendSValue());


        holder.generateAdifButton.setOnClickListener(v -> generateAdifFile());
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

    private void generateAdifFile() {
        // Fetch all logs from the database


        // Define the ADIF file header and footer
        StringBuilder adifContent = new StringBuilder();
        adifContent.append("ADIF Export\n");
        adifContent.append("<EOH>\n"); // End of header

        // Loop through logs and add ADIF entries

        adifContent.append("<CALL:").append(log.getCallSign().length()).append(">")
                .append(log.getCallSign()).append(" ");
        adifContent.append("<FREQ:").append(log.getFrequency().length()).append(">")
                .append(log.getFrequency()).append(" ");
        adifContent.append("<TIME_ON:").append(formatTimestampToADIF(log.getTime())).append(">")
                .append(log.getTime()).append(" ");
        adifContent.append("<RST_SENT:").append(String.valueOf(log.getSendSValue()).length()).append(">")
                .append(log.getSendSValue()).append(" ");
        adifContent.append("<RST_RCVD:").append(String.valueOf(log.getReceiveSValue()).length()).append(">")
                .append(log.getReceiveSValue()).append(" ");
        adifContent.append("<QTH:").append(log.getLocation().length()).append(">")
                .append(log.getLocation()).append("\n");


        adifContent.append("<EOF>\n"); // End of file

        // Save ADIF file to external storage
        try {
            File adifFile = new File(context.getExternalFilesDir(null), log.getCallSign() + ".adi");
            FileWriter writer = new FileWriter(adifFile);
            writer.write(adifContent.toString());
            writer.close();
            offerToShareFile(adifFile);

            Toast.makeText(context, "ADIF file generated: " + adifFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, "Error generating ADIF file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private String formatTimestampToADIF(long timestamp) {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyyMMdd HHmmss", java.util.Locale.getDefault());
        java.util.Date date = new java.util.Date(timestamp); // Convert milliseconds to Date
        return dateFormat.format(date); // Format to ADIF-compatible string
    }
    private void offerToShareFile(File adifFile) {
        // Get the URI using FileProvider
        android.net.Uri fileUri = androidx.core.content.FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                adifFile
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "ADIF File");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Here is the ADIF file for the log: " + log.getCallSign());

        // Grant temporary read permission for the file
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent chooser = Intent.createChooser(shareIntent, "Share ADIF File");
        if (shareIntent.resolveActivity(context.getPackageManager()) != null) {

            try {
                context.startActivity(chooser);
            } catch (Exception e) {
                Log.v("LogAdapter", "Error starting share intent: ", e);
            }
        } else {
            Toast.makeText(context, "No app available to share the file", Toast.LENGTH_SHORT).show();
        }
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
                LogBook updatedLog = LogDatabaseHelper.getInstance(context).getLogById(id);
                if (updatedLog != null) {
                    logs.set(i, updatedLog); // Update the specific log in the list
                    notifyItemChanged(i); // Notify RecyclerView to refresh only the updated item
                }
                return;
            }
        }
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView frequencyView, generateAdifButton, callSignView, locationView, timeView, receiveSValueView, sendSValueView;
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
            generateAdifButton = itemView.findViewById(R.id.generateAdifButton);

        }
    }
}
