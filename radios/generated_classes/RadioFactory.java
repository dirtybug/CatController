
package com.Runner.CQMiau.radios;

import com.Runner.CQMiau.radios.drivers.RadioBase;
import com.Runner.CQMiau.radios.drivers.RadioType;
import com.Runner.CQMiau.comPort.ComPortConfigActivity;

public class RadioFactory {

    public static RadioBase getRadio() {
        RadioType type = ComPortConfigActivity.getInstance().getRadio();
        switch (type) {
            case TT588:
                return new TT588();
            case FT450D:
                return new FT450D();
            case FTdx3000:
                return new FTdx3000();
            case TS590SG:
                return new TS590SG();
            case FT767:
                return new FT767();
            case AOR5K:
                return new AOR5K();
            case FT1000MP:
                return new FT1000MP();
            case IC7600:
                return new IC7600();
            case TS870S:
                return new TS870S();
            case FT847:
                return new FT847();
            case IC718:
                return new IC718();
            case TS570:
                return new TS570();
            case FT990a:
                return new FT990a();
            case FT950:
                return new FT950();
            case TS850:
                return new TS850();
            case TS450S:
                return new TS450S();
            case IC910:
                return new IC910();
            case FT1000:
                return new FT1000();
            case IC7300:
                return new IC7300();
            case IC728:
                return new IC728();
            case FTdx9000:
                return new FTdx9000();
            case FT817:
                return new FT817();
            case TS990:
                return new TS990();
            case TS2000:
                return new TS2000();
            case FT100D:
                return new FT100D();
            case K2:
                return new K2();
            case FT900:
                return new FT900();
            case FT891:
                return new FT891();
            case TT599:
                return new TT599();
            case FTdx1200:
                return new FTdx1200();
            case IC735:
                return new IC735();
            case TS480SAT:
                return new TS480SAT();
            case IC756PRO3:
                return new IC756PRO3();
            case IC7000:
                return new IC7000();
            case TT538:
                return new TT538();
            case FT991:
                return new FT991();
            case TT566:
                return new TT566();
            case FT450:
                return new FT450();
            case TT563:
                return new TT563();
            case IC706MKIIG:
                return new IC706MKIIG();
            case FT2000:
                return new FT2000();
            case FT920:
                return new FT920();
            case FT5000:
                return new FT5000();
            case TS480HX:
                return new TS480HX();
            case FT990:
                return new FT990();
            case IC703:
                return new IC703();
            case DELTA_II:
                return new DELTA_II();
            case IC756:
                return new IC756();
            case FT890:
                return new FT890();
            case Mark_V:
                return new Mark_V();
            case TT550:
                return new TT550();
            case PCR1000:
                return new PCR1000();
            case IC7200:
                return new IC7200();
            case K3:
                return new K3();
            case TS140:
                return new TS140();
            case KX3:
                return new KX3();
            case IC746:
                return new IC746();
            case FT857D:
                return new FT857D();
            case RAY152:
                return new RAY152();
            case TT516:
                return new TT516();
            case FT747:
                return new FT747();
            case TS590S:
                return new TS590S();
            case IC756PRO2:
                return new IC756PRO2();
            default:
                throw new IllegalArgumentException("Unsupported radio type: " + type);
        }
    }
}
