package demo.xm.com.xmfunsdkdemo.ui.device.config.filetransfer;

import com.alibaba.fastjson.annotation.JSONField;

public class OpSpecifyAudioBean {
    public static final String JSON_NAME = "OpSpecifyAudio";
    public static final int CMD_ID = 3020;
    @JSONField(name = "Cmd")
    private String cmd;
    @JSONField(name = "Arg1")
    private int number;//对应的音频序号

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
