package com.Runner.CQMiau;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserSettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserSettings";
    private static final String KEY_CALLSIGN = "callsign";

    private EditText callsignEditText;
    private Button saveButton;

    /**
     * Static method to get the saved callsign.
     *
     * @param context Application context.
     * @return The saved callsign or an empty string if not set.
     */
    public static String getCallsign(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_CALLSIGN, "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        callsignEditText = findViewById(R.id.callsignEditText);
        saveButton = findViewById(R.id.saveButton);

        // Load saved callsign
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedCallsign = preferences.getString(KEY_CALLSIGN, "");
        callsignEditText.setText(savedCallsign);

        // Save callsign on button click
        saveButton.setOnClickListener(v -> {
            String newCallsign = callsignEditText.getText().toString().trim().toUpperCase();
            if (!newCallsign.isEmpty()) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(KEY_CALLSIGN, newCallsign);
                editor.apply();
                Toast.makeText(this, "Callsign saved! Closing APP please Reopen", Toast.LENGTH_SHORT).show();

                finishAffinity(); // Close all activities
                finish();
            } else {
                Toast.makeText(this, "Please enter a valid callsign.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}