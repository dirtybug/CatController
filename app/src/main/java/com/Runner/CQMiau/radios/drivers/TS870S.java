
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.NewYeasu;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class TS870S extends NewYeasu {

    public TS870S() {
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
        super.Len=12;
    }


}
