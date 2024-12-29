
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class FT767 extends RadioBase {

    public FT767() {
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
    }

    @Override
    public void setFrequency(String freq) {
        // Function body extracted from FT767.cxx
        {
	freqA = freq;
	freq /=10; // 767 does not support 1 Hz resolution
	cmd = to_bcd(freq, 8);
	cmd += 0x08; // SET FREQUENCY
	sendCommand(cmd);
}
    }

    @Override
    public void setMode(RadioMode mode) {
        // Placeholder for setMode implementation
        System.out.println("Mode set: " + mode);
    }
}
