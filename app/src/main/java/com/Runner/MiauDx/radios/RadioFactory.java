package com.Runner.MiauDx.radios;


import com.Runner.MiauDx.comPort.ComPortConfigActivity;

public class RadioFactory {

    public static RadioBase getRadio() {
        RadioType type = ComPortConfigActivity.getInstance().getRadio();
        switch (type) {
            case FT891:
                return new FT891();
            case FT857:
                return new FT857();
            default:
                throw new IllegalArgumentException("Unsupported radio type: " + type);
        }
    }
}