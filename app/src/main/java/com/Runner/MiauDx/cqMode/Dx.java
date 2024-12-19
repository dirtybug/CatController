package com.Runner.MiauDx.cqMode;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Dx implements Parcelable {
    private final String callSign;


    public Dx(String callSign, String comment, String location) {
        this.callSign = callSign;

    }

    public String getCallSign() {
        return callSign;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }
}


