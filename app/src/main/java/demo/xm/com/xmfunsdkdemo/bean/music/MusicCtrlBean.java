package demo.xm.com.xmfunsdkdemo.bean.music;

public class MusicCtrlBean {
    private MusicCtrlContent MusicCtrl;
    private String Name;
    private int Ret;
    private String SessionID;

    // Getters and Setters  
    public MusicCtrlContent getMusicCtrl() {
        return MusicCtrl;
    }

    public void setMusicCtrl(MusicCtrlContent musicCtrl) {
        MusicCtrl = musicCtrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getRet() {
        return Ret;
    }

    public void setRet(int ret) {
        Ret = ret;
    }

    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(String sessionID) {
        SessionID = sessionID;
    }
}