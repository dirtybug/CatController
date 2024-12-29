
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class AOR5K extends RadioBase {

    public AOR5K() {
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
    }

    @Override
    public void setFrequency(String freq) {
        // Ensure the input is numeric
        if (!freq.matches("\\d+")) {
            throw new IllegalArgumentException("Frequency must be a numeric string.");
        }

        // Parse frequency into a long
        long numericFreq = Long.parseLong(freq);

        // Start with the base command
        char[] cmd = "VE0000000000\r".toCharArray();

        // Populate the frequency portion of the command
        for (int i = 11; i > 1; i--) {
            cmd[i] += numericFreq % 10;
            numericFreq /= 10;
        }

        // Convert the command array back to a string
        String finalCommand = new String(cmd);

        // Send the command (placeholder for actual logic)
        sendAndReceiveCommand(finalCommand);
    }

    @Override
    public void setMode(RadioMode mode) {
    }
}
