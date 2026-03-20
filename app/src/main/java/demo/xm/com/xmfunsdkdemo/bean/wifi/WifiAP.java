package demo.xm.com.xmfunsdkdemo.bean.wifi;

import com.alibaba.fastjson.annotation.JSONField;

public class WifiAP {
    private String Auth;
    private int Channel;
    private String EncrypType;
    private String NetType;
    private String RSSI;
    private String SSID;
    private int nRSSI;

    @JSONField(
            name = "Auth"
    )
    public String getAuth() {
        return this.Auth;
    }

    @JSONField(
            name = "Auth"
    )
    public void setAuth(String auth) {
        this.Auth = auth;
    }

    @JSONField(
            name = "Channel"
    )
    public int getChannel() {
        return this.Channel;
    }

    @JSONField(
            name = "Channel"
    )
    public void setChannel(int channel) {
        this.Channel = channel;
    }

    @JSONField(
            name = "EncrypType"
    )
    public String getEncrypType() {
        return this.EncrypType;
    }

    @JSONField(
            name = "EncrypType"
    )
    public void setEncrypType(String encrypType) {
        this.EncrypType = encrypType;
    }

    @JSONField(
            name = "NetType"
    )
    public String getNetType() {
        return this.NetType;
    }

    @JSONField(
            name = "NetType"
    )
    public void setNetType(String netType) {
        this.NetType = netType;
    }

    @JSONField(
            name = "RSSI"
    )
    public String getRSSI() {
        return this.RSSI;
    }

    @JSONField(
            name = "RSSI"
    )
    public void setRSSI(String RSSI) {
        this.RSSI = RSSI;
    }

    @JSONField(
            name = "SSID"
    )
    public String getSSID() {
        return this.SSID;
    }

    @JSONField(
            name = "SSID"
    )
    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    @JSONField(
            name = "nRSSI"
    )
    public int getnRSSI() {
        return this.nRSSI;
    }

    @JSONField(
            name = "nRSSI"
    )
    public void setnRSSI(int nRSSI) {
        this.nRSSI = nRSSI;
    }
}

