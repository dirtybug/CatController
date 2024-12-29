
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class FT920 extends RadioBase {

    public FT920() {
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
    }

    @Override
    public void setFrequency(String freq) {
        // Function body extracted from FT920.cxx
        {
	A.freq = freq;
	freq /=10; // 920 does not support 1 Hz resolution
	cmd = to_bcd_be(freq, 8);
	cmd += 0x0A;
	sendCommand(cmd);
	showresp(WARN, HEX, "set vfo A", cmd, replystr);
}
    }

    @Override
    public void setMode(RadioMode mode) {
        // Placeholder for setMode implementation
        System.out.println("Mode set: " + mode);
    }
}
