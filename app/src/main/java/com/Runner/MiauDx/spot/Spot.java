package com.Runner.MiauDx.spot;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Spot {
    private final String location;
    private final String flag;
    private final String comment;
    private String frequency;
    private final String callSign;
    private long currentTime;


    public Spot(String frequency, String flag, String callSign, String location, long currentTime, String comment) {
        this.frequency = frequency;
        this.callSign = callSign;
        this.location = location;
        this.currentTime = currentTime;
        this.flag = flag;
        this.comment = comment;
    }

    public void setFrquecy(String frequency) {
        this.frequency = frequency;

    }

    public String getFrequency() {
        return frequency;
    }

    public String getCallSign() {
        return callSign;
    }

    public String getFlag() {
        return flag;
    }

    public String getLocation() {
        return location;
    }

    public long getTimestamp() {
        return currentTime;

    }

    public String getComment() {
        return comment;

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
