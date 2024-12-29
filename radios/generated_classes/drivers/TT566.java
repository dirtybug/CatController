
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class TT566 extends RadioBase {

    public TT566() {
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
    }

    @Override
    public void setFrequency(String freq) {
        // Function body extracted from TT566.cxx
        {
	A.freq = freq;
	cmd = TT566setFREQa;
	cmd.append(to_decimal(freq, 8));
	cmd += '\r';
	sendCommand(cmd);
	showresp(WARN, ASC, "set vfo A", cmd, replystr);
	return;
}
    }

    @Override
    public void setMode(RadioMode mode) {
        // Placeholder for setMode implementation
        System.out.println("Mode set: " + mode);
    }
}
