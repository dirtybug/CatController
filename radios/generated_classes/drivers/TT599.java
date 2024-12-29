
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class TT599 extends RadioBase {

    public TT599() {
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
    }

    @Override
    public void setFrequency(String freq) {
        // Function body extracted from TT599.cxx
        {
	freqA = freq;
	cmd = "*AF";
	cmd.append( to_decimal( freq, 8 ) );
	cmd += '\r';
	sendCommand(cmd);
	get_vfoA();
}
    }

    @Override
    public void setMode(RadioMode mode) {
        // Placeholder for setMode implementation
        System.out.println("Mode set: " + mode);
    }
}
