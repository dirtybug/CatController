package com.example.catcontroller;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DXCCLoader {
    private final List<DXCCEntry> dxccEntries;

    public DXCCLoader(Context context) throws IOException, JSONException {
        dxccEntries = new ArrayList<>();

        // Load the JSON file from assets
        InputStream inputStream = context.getAssets().open("dxcc.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder jsonBuilder = new StringBuilder();
        String line;

        // Read the file into a String
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        reader.close();

        // Parse the JSON string
        String jsonString = jsonBuilder.toString();
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray dxccArray = jsonObject.getJSONArray("dxcc");

        // Populate the DXCC entries list
        for (int i = 0; i < dxccArray.length(); i++) {
            JSONObject entry = dxccArray.getJSONObject(i);
            String flag = entry.optString("flag", "🏳️");
            String prefixRegex = entry.optString("prefixRegex", "");
            if (!prefixRegex.isEmpty()) {
                dxccEntries.add(new DXCCEntry(flag, prefixRegex));
            }
        }
    }

    // Method to extract the flag based on the call sign
    public String getFlagFromCallSign(String callSign) {
        for (DXCCEntry entry : dxccEntries) {
            if (entry.matchesCallSign(callSign)) {
                return entry.getFlag();
            }
        }
        return "🏳️"; // Default flag if no match is found
    }

    // Inner class to hold DXCC entries
    private static class DXCCEntry {
        private final String flag;
        private final Pattern regex;

        public DXCCEntry(String flag, String regex) {
            this.flag = flag;
            this.regex = Pattern.compile(regex);
        }

        public boolean matchesCallSign(String callSign) {
            Matcher matcher = regex.matcher(callSign);
            return matcher.find();
        }

        public String getFlag() {
            return flag;
        }
    }
}