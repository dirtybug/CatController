
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class TT538 extends RadioBase {

    public TT538() {
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
    }

    @Override
    public void setFrequency(String freq) {
        // Function body extracted from TT538.cxx
        {
	freqA = freq;
	long xfreq = freqA * (1 + VfoAdj/1e6) + 0.5;
	cmd = TT538setFREQA;
	cmd[5] = xfreq & 0xff; xfreq = xfreq >> 8;
	cmd[4] = xfreq & 0xff; xfreq = xfreq >> 8;
	cmd[3] = xfreq & 0xff; xfreq = xfreq >> 8;
	cmd[2] = xfreq & 0xff;
	sendCommand(cmd);
	set_if_shift(pbt);
	return ;
}
    }

    @Override
    public void setMode(RadioMode mode) {
        // Placeholder for setMode implementation
        System.out.println("Mode set: " + mode);
    }
}
