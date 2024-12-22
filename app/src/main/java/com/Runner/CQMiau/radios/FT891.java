package com.Runner.CQMiau.radios;

public class FT891 extends RadioBase {

    @Override
    public void setFrequency(String frequencyMHz) {
        // Convert MHz to Hz (e.g., 144360.0 KHz -> 144360000 Hz)
        double frequency = Double.parseDouble(frequencyMHz);
        long frequencyHz = Math.round(frequency * 1_000);

        // Format as 10-digit string with leading zeros
        String formattedFrequency = String.format("%09d", frequencyHz);

        // Prepend the FA command
        sendAndReceiveCommand("FA" + formattedFrequency + ";");
    }

    @Override
    public void setMode(RadioMode mode) {

        String command;
        switch (mode) {
            case FMN:
                command = "MD04;"; // Example command for FM mode
                break;
            case SSB:
                command = "MD01;"; // Example command for SSB mode
                break;


            default:
                return; // Unsupported mode
        }

        sendAndReceiveCommand(command);
    }
}
