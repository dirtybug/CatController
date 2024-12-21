package com.Runner.CQMiau.cqMode;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Dx implements Parcelable {
    public static final Creator<Dx> CREATOR = new Creator<Dx>() {
        @Override
        public Dx createFromParcel(Parcel in) {
            return new Dx(in);
        }

        @Override
        public Dx[] newArray(int size) {
            return new Dx[size];
        }
    };
    private final String callSign;
    private String location;
    private String flag;
    private String comment;

    public Dx(String callSign, String flag, String comment, String location) {
        this.callSign = callSign;
        this.flag = flag;
        this.comment = comment;
        this.location = location;
    }

    protected Dx(Parcel in) {
        callSign = in.readString();
    }

    public String getCallSign() {
        return callSign;
    }

    public String getFlag() {
        return flag;
    }

    public String getComment() {
        return comment;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(callSign);
    }
}


