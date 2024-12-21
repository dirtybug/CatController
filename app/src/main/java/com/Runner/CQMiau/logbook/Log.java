package com.Runner.CQMiau.logbook;


public class Log {
    private final String frequency;
    private final String callSign;
    private final String location;
    private final String time;
    private final int receiveSValue;
    private final int sendSValue;
    private final int Id;

    public Log(int Id, String frequency, String callSign, String location, String time, int receiveSValue, int sendSValue) {
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

    public String getTime() {
        return time;
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
