package com.Runner.CQMiau.logbook;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Runner.CQMiau.R;
import com.Runner.CQMiau.spot.SpotDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class ViewLogsActivity extends AppCompatActivity {

    private LogDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_logs);

        dbHelper = new LogDatabaseHelper(this);

        RecyclerView logsRecyclerView = findViewById(R.id.logsRecyclerView);
        Button addToLogButton = findViewById(R.id.addToLogButton);

        addToLogButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SpotDetailActivity.class);
            this.startActivity(intent); // Start the new activity

        });
        logsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<LogBook> logs = getLogsFromDatabase();
        LogAdapter adapter = new LogAdapter(this, logs);
        logsRecyclerView.setAdapter(adapter);
    }

    private List<LogBook> getLogsFromDatabase() {
        List<LogBook> logs = new ArrayList<>();
        Cursor cursor = dbHelper.getAllLogs();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_ID));
                String frequency = cursor.getString(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_FREQUENCY));
                String callSign = cursor.getString(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_CALL_SIGN));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_LOCATION));
                long time = cursor.getLong(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_TIME));
                int receiveSValue = cursor.getInt(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_RECEIVE_S_VALUE));
                int sendSValue = cursor.getInt(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_SEND_S_VALUE));

                logs.add(new LogBook(id, frequency, callSign, location, time, receiveSValue, sendSValue));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return logs;
    }
}
