package com.Runner.MiauDx.radios;

import com.Runner.MiauDx.comPort.ComPortManager;

public abstract class RadioBase {

    // Method to set DX (frequency and mode)
    public void setDx(String frequency, RadioMode mode) {
        setFrequency(frequency);
        setMode(mode);
    }

    // Abstract method for setting frequency (to be implemented by child classes)
    public abstract void setFrequency(String frequencyMHz);

    // Abstract method for setting mode (to be implemented by child classes)
    public abstract void setMode(RadioMode mode);

    // Method to send and receive commands (shared by all radios)
    protected void sendAndReceiveCommand(String command) {
        ComPortManager.getInstance().queueCommand(command);
    }
}
