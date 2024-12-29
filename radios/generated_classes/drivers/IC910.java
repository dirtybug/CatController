
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class IC910 extends RadioBase {

    public IC910() {
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
    }

    @Override
    public void setFrequency(String freq) {
        // Function body extracted from IC910.cxx
        {
	long nufreq;
	if (freq > 1300000000L) nufreq = 1300000000L;
	else if (freq > 450000000L && freq < 1240000000L && freqA <= 450000000L)
		nufreq = 1240000000L;
	else if (freq > 450000000L && freq < 1240000000L && freqA >= 1240000000L)
		nufreq = 450000000L;
	else if (freq > 148000000L && freq < 430000000L && freqA <= 148000000L)
		nufreq = 430000000L;
	else if (freq > 148000000L && freq < 430000000L && freqA >= 430000000L)
		nufreq = 148000000L;
	else if (freq < 144000000L) nufreq = 144000000L;
	else nufreq = freq;
	freqA = nufreq;

	if (freqA != freq) {
		vfoA.freq = freqA;
		setFreqDispA((void*)0);
	}
	A.freq = freqA;
	cmd = pre_to;
	cmd += '\x05';
	cmd.append( to_bcd_be( freqA, 10 ) );
	cmd.append( post );
	waitFB("set vfo A");}
    }

    @Override
    public void setMode(RadioMode mode) {
        // Placeholder for setMode implementation
        System.out.println("Mode set: " + mode);
    }
}
