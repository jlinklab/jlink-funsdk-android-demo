package demo.xm.com.xmfunsdkdemo.ui.entity;

public class ResolutionInfo {

    public static int GetIndex(String resolution) {
        final String D1 = "D1";
        final String HD1 = "HD1";
        final String BCIF = "BCIF";
        final String CIF = "CIF";
        final String QCIF = "QCIF";
        final String VGA = "VGA";
        final String QVGA = "QVGA";
        final String SVCD = "SVCD";
        final String QQVGA = "QQVGA";
        final String ND1 = "ND1";
        final String _650TVL = "650TVL";
        final String _720P = "720P";
        final String _1_3M = "1_3M";
        final String UXGA = "UXGA";
        final String _1080P = "1080P";
        final String WUXGA = "WUXGA";
        final String _2_5M = "2_5M";
        final String _3M = "3M";
        final String _4M = "4M";
        final String _5M = "5M";
        final String _1080N = "1080N";
        final String _6M = "6M";
        final String _8M = "8M";
        final String _12M = "12M";
        final String _4K = "4K";
        final String _720N = "720N";
        final String WSVGA = "WSVGA";
        final String NHD = "NHD";
        final String _3M_N = "3M_N";
        final String _4M_N = "4M_N";
        final String _5M_N = "5M_N";
        final String _4K_N = "4K_N";

        if (resolution.equals(D1)) {
            return 0;
        } else if (resolution.equals(HD1)) {
            return 1;
        } else if (resolution.equals(BCIF)) {
            return 2;
        } else if (resolution.equals(CIF)) {
            return 3;
        } else if (resolution.equals(QCIF)) {
            return 4;
        } else if (resolution.equals(VGA)) {
            return 5;
        } else if (resolution.equals(QVGA)) {
            return 6;
        } else if (resolution.equals(SVCD)) {
            return 7;
        } else if (resolution.equals(QQVGA)) {
            return 8;
        } else if (resolution.equals(ND1)) {
            return 9;
        } else if (resolution.equals(_650TVL)) {
            return 10;
        } else if (resolution.equals(_720P)) {
            return 11;
        } else if (resolution.equals(_1_3M)) {
            return 12;
        } else if (resolution.equals(UXGA)) {
            return 13;
        } else if (resolution.equals(_1080P)) {
            return 14;
        } else if (resolution.equals(WUXGA)) {
            return 15;
        } else if (resolution.equals(_2_5M)) {
            return 16;
        } else if (resolution.equals(_3M)) {
            return 17;
        } else if (resolution.equals(_5M)) {
            return 18;
        } else if (resolution.equals(_1080N)) {
            return 19;
        } else if (resolution.equals(_4M)) {
            return 20;
        } else if (resolution.equals(_6M)) {
            return 21;
        } else if (resolution.equals(_8M)) {
            return 22;
        } else if (resolution.equals(_12M)) {
            return 23;
        } else if (resolution.equals(_4K)) {
            return 24;
        } else if (resolution.equals(_720N)) {
            return 25;
        } else if (resolution.equals(WSVGA)) {
            return 26;
        } else if (resolution.equals(NHD)) {
            return 27;
        } else if (resolution.equals(_3M_N)) {
            return 28;
        } else if (resolution.equals(_4M_N)) {
            return 29;
        } else if (resolution.equals(_5M_N)) {
            return 30;
        } else if (resolution.equals(_4K_N)) {
            return 31;
        } else {
            return 0;
        }
    }

    public static String GetString(int i) {
        final String D1 = "D1";
        final String HD1 = "HD1";
        final String BCIF = "BCIF";
        final String CIF = "CIF";
        final String QCIF = "QCIF";
        final String VGA = "VGA";
        final String QVGA = "QVGA";
        final String SVCD = "SVCD";
        final String QQVGA = "QQVGA";
        final String ND1 = "ND1";
        final String _650TVL = "650TVL";
        final String _720P = "720P";
        final String _1_3M = "1_3M";
        final String UXGA = "UXGA";
        final String _1080P = "1080P";
        final String WUXGA = "WUXGA";
        final String _2_5M = "2_5M";
        final String _3M = "3M";
        final String _4M = "4M";
        final String _5M = "5M";
        final String _1080N = "1080N";
        final String _6M = "6M";
        final String _8M = "8M";
        final String _12M = "12M";
        final String _4K = "4K";
        final String _720N = "720N";
        final String WSVGA = "WSVGA";
        final String NHD = "NHD";
        final String _3M_N = "3M_N";
        final String _4M_N = "4M_N";
        final String _5M_N = "5M_N";
        final String _4K_N = "4K_N";

        switch (i) {
            case 0:
                return D1;
            case 1:
                return HD1;
            case 2:
                return BCIF;
            case 3:
                return CIF;
            case 4:
                return QCIF;
            case 5:
                return VGA;
            case 6:
                return QVGA;
            case 7:
                return SVCD;
            case 8:
                return QQVGA;
            case 9:
                return ND1;
            case 10:
                return _650TVL;
            case 11:
                return _720P;
            case 12:
                return _1_3M;
            case 13:
                return UXGA;
            case 14:
                return _1080P;
            case 15:
                return WUXGA;
            case 16:
                return _2_5M;
            case 17:
                return _3M;
            case 18:
                return _5M;
            case 19:
                return _1080N;
            case 20:
                return _4M;
            case 21:
                return _6M;
            case 22:
                return _8M;
            case 23:
                return _12M;
            case 24:
                return _4K;
            case 25:
                return _720N;
            case 26:
                return WSVGA;
            case 27:
                return NHD;
            case 28:
                return _3M_N;
            case 29:
                return _4M_N;
            case 30:
                return _5M_N;
            case 31:
                return _4K_N;
            default:
                return D1;
        }
    }

}
