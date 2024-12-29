
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class FT747 extends RadioBase {

    public FT747() {
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
    }

    @Override
    public void setFrequency(String freq) {
        // Function body extracted from FT747.cxx
        {
	A.freq = freq;
	freq /=10; // 747 does not support 1 Hz resolution
	cmd = to_bcd_be(freq, 8);
	cmd += 0x0A; // SET FREQUENCY
	SLOG_INFO("cmd: %s", str2hex(cmd.c_str(), cmd.length()));
	sendCommand(cmd);
}
    }

    @Override
    public void setMode(RadioMode mode) {
        // Placeholder for setMode implementation
        System.out.println("Mode set: " + mode);
    }
}
