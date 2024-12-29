
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class FT847 extends RadioBase {

    public FT847() {
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
    }

    @Override
    public void setFrequency(String freq) {
        // Function body extracted from FT847.cxx
        {
	A.freq = freq;
	freq /=10; // 847 does not support 1 Hz resolution
	cmd = to_bcd(freq, 8);
	cmd += 0x01;
	replystr.clear();
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
