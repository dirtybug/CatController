
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class FT857D extends RadioBase {

    public FT857D() {
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
    }

    @Override
    public void setFrequency(String freq) {
        // Function body extracted from FT857D.cxx
        {
	freqA = freq;
	freq /=10; // 857D does not support 1 Hz resolution
	cmd = to_bcd(freq, 8);
	cmd += 0x01;
	replystr.clear();
	set_getACK();
	showresp(WARN, HEX, "set vfo A", cmd, replystr);
}
    }

    @Override
    public void setMode(RadioMode mode) {
        // Placeholder for setMode implementation
        System.out.println("Mode set: " + mode);
    }
}
