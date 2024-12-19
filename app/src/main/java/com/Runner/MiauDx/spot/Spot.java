package com.Runner.MiauDx.spot;


import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Spot implements Parcelable {
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

    public static final Creator<Spot> CREATOR = new Creator<Spot>() {
        @Override
        public Spot createFromParcel(Parcel in) {
            return new Spot(in);
        }

        @Override
        public Spot[] newArray(int size) {
            return new Spot[size];
        }
    };

    protected Spot(Parcel in) {
        frequency = in.readString();
        flag = in.readString();
        callSign = in.readString();
        location = in.readString();
        currentTime = in.readLong();
        comment = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(frequency);
        dest.writeString(flag);
        dest.writeString(callSign);
        dest.writeString(location);
        dest.writeLong(currentTime);
        dest.writeString(comment);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
