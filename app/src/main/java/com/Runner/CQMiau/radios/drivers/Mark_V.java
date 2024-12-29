
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.OldYeasu;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class Mark_V extends OldYeasu {

    public Mark_V() {
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
        super.CAT_FREQ_SET=0x0A;
    }




}
