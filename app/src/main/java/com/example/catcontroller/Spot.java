package com.example.catcontroller;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Spot {
    private final String message;
    private final String frequency;
    private final String callSign;
    private long currentTime;


    public Spot(String frequency, String callSign, String message, long currentTime) {
        this.frequency = frequency;
        this.callSign = callSign;
        this.message = message;
        this.currentTime = currentTime;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getCallSign() {
        return callSign;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return currentTime;

    }

    public void setTimestamp(long currentTime) {
        this.currentTime = currentTime;
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Convert the timestamp to a Date object
        Date date = new Date(currentTime);

        // Format the Date object as a human-readable string
        return sdf.format(date);
    }
}
