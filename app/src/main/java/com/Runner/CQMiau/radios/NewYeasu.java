package com.Runner.CQMiau.radios;

public class NewYeasu extends RadioBase {
    protected int Len;

    protected NewYeasu()
    {
        super();
        super.Max= 54000000L;
        super.Max= 3000L;
    }

    @Override
    public void setFrequency(String frequencyKHz) {
        // Ensure the input is numeric
        if (!frequencyKHz.matches("\\d+")) {
            throw new IllegalArgumentException("Frequency must be a numeric string.");
        }

        // Parse the frequency as a long
        long numericFreq = Long.parseLong(frequencyKHz);

        // Construct the base command with variable length
        StringBuilder cmdBuilder = new StringBuilder("FA");
        for (int i = 0; i < Len; i++) {
            cmdBuilder.append("0"); // Append zeros dynamically based on varLen
        }
        cmdBuilder.append(";");

        // Convert the command string to a char array for manipulation
        char[] cmd = cmdBuilder.toString().toCharArray();

        // Populate the frequency portion from the end
        for (int i = Len; i > 1; i--) {
            cmd[i] += numericFreq % 10;
            numericFreq /= 10;
        }

        // Convert the char array back to a string
        String finalCommand = new String(cmd);

        // Send the command (placeholder for actual logic)
        sendAndReceiveCommand(finalCommand);
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
