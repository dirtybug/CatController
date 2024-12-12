package com.example.catcontroller.logbook;


import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catcontroller.R;

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
        logsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Log> logs = getLogsFromDatabase();
        LogAdapter adapter = new LogAdapter(logs);
        logsRecyclerView.setAdapter(adapter);
    }

    private List<Log> getLogsFromDatabase() {
        List<Log> logs = new ArrayList<>();
        Cursor cursor = dbHelper.getAllLogs();
        if (cursor.moveToFirst()) {
            do {
                String frequency = cursor.getString(cursor.getColumnIndexOrThrow("frequency"));
                String callSign = cursor.getString(cursor.getColumnIndexOrThrow("call_sign"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                int receiveSValue = cursor.getInt(cursor.getColumnIndexOrThrow("receive_s_value"));
                int sendSValue = cursor.getInt(cursor.getColumnIndexOrThrow("send_s_value"));

                logs.add(new Log(frequency, callSign, location, time, receiveSValue, sendSValue));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return logs;
    }
}
