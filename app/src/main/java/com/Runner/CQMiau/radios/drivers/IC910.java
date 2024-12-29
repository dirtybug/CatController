
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

    }

    @Override
    public void setMode(RadioMode mode) {
        // Placeholder for setMode implementation
        System.out.println("Mode set: " + mode);
    }
}
