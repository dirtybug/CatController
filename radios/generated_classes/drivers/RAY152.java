
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class RAY152 extends RadioBase {

    public RAY152() {
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
    }

    @Override
    public void setFrequency(String freq) {
        // Function body extracted from RAY152.cxx
        {
	A.freq = freq;
	cmd = "FT000000\r";
	freq /= 100;
	cmd[7] += freq % 10; freq /= 10;
	cmd[6] += freq % 10; freq /= 10;
	cmd[5] += freq % 10; freq /= 10;
	cmd[4] += freq % 10; freq /= 10;
	cmd[3] += freq % 10; freq /=10;
	cmd[2] += freq;
	sendCommand(cmd, 0);
LOG_INFO("%s", cmd.c_str());
	cmd[1] = 'R';
	sendCommand(cmd, 0);
LOG_INFO("%s", cmd.c_str());
}
    }

    @Override
    public void setMode(RadioMode mode) {
        // Placeholder for setMode implementation
        System.out.println("Mode set: " + mode);
    }
}
