
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.NewYeasu;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class FT5000 extends NewYeasu {

    public FT5000() {
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
        super.Len=9;

    }



}
