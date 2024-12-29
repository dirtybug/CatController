
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class FT990 extends RadioBase {

    public FT990() {
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
    }

    @Override
    public void setFrequency(String freq) {
        // Function body extracted from FT990.cxx
        {
	A.freq = freq;
	freq /=10; // 100D does not support 1 Hz resolution
	cmd = to_bcd_be(freq, 8);
	cmd += 0x0A;
	sendCommand(cmd);
	showresp(WARN, HEX, "set freq A", cmd, replystr);
}
    }

    @Override
    public void setMode(RadioMode mode) {
        // Placeholder for setMode implementation
        System.out.println("Mode set: " + mode);
    }
}
