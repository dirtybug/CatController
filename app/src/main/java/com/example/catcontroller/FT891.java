package com.example.catcontroller;

public class FT891 {
    public FT891() {

    }

    public void setDx(String frequency, String mode) {
        setFrequency(frequency);
        setMode(mode);

    }

    public void setFrequency(String frequencyMHz) {
        // Convert MHz to Hz (e.g., 144360.0 MHz -> 144360000 Hz)
        double frequency = Double.parseDouble(frequencyMHz);
        long frequencyHz = Math.round(frequency * 1_000);

        // Format as 10-digit string with leading zeros
        String formattedFrequency = String.format("%010d", frequencyHz);

        // Prepend the FA command
        sendAndReceiveCommand("FA" + formattedFrequency);
    }

    private void setMode(String mode) {
        String command = "";

        switch (mode.toUpperCase()) {
            case "FM ANALOG":
                command = "MD04;"; // Example command for FM mode
                break;
            case "SSB":
                command = "MD01;"; // Example command for AM mode
                break;
            case "DMR":
                command = "MD05;"; // Example command for DMR mode (assuming it exists)
                break;
            // Add other modes as needed
            default:

                return;
        }

        sendAndReceiveCommand(command);

    }

    private void sendAndReceiveCommand(String command) {
        ComPortManager.getInstance().queueCommand(command);
    }
}
