package com.Runner.CQMiau.logbook;


import java.text.SimpleDateFormat;
import java.util.Date;

public class LogBook{
    private final String frequency;
    private final String callSign;
    private final String location;
    private final long time;
    private final int receiveSValue;
    private final int sendSValue;
    private final int Id;

    public LogBook(int Id, String frequency, String callSign, String location, long time, int receiveSValue, int sendSValue) {
        this.Id = Id;
        this.frequency = frequency;
        this.callSign = callSign;
        this.location = location;
        this.time = time;
        this.receiveSValue = receiveSValue;
        this.sendSValue = sendSValue;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getCallSign() {
        return callSign;
    }

    public String getLocation() {
        return location;
    }

    public long getTime() {
        return time;
    }
    public String getTimeStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Convert the timestamp to a Date object
        Date date = new Date(time);

        // Format the Date object as a human-readable string
        return sdf.format(date);
    }
    public int getReceiveSValue() {
        return receiveSValue;
    }

    public int getSendSValue() {
        return sendSValue;
    }

    public int getId() {
        return this.Id;
    }
}
