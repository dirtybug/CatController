
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class FT1000 extends RadioBase {

    public FT1000() {
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
    }

    @Override
    public void setFrequency(String freq) {
        // Function body extracted from FT1000.cxx
        {
	freqA = freq;
	freq /=10; // 1000 does not support 1 Hz resolution
	cmd = to_bcd(freq, 8);
	cmd += 0x0A;
	sendCommand(cmd, 0);
}
    }

    @Override
    public void setMode(RadioMode mode) {
        // Placeholder for setMode implementation
        System.out.println("Mode set: " + mode);
    }
}
