package com.Runner.CQMiau.radios;

public class Icom extends RadioBase {
    protected int Len;

    protected Icom()
    {
        super();

        super.Max= 54000000L;
        super.Max= 3000L;
    }

    @Override
    public void setFrequency(String frequencyKHz) {
        /*	cmd = pre_to;
	cmd += '\x05';
	cmd.append( to_bcd_be( freq, 10 ) );
	cmd.append(post);
	waitFB("set vfo A");*/

    }

    @Override
    public void setMode(RadioMode mode) {

    }
}
