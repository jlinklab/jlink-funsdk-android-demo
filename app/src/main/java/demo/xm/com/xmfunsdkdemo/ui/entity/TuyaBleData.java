package demo.xm.com.xmfunsdkdemo.ui.entity;

import com.blankj.utilcode.util.ConvertUtils;

import demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth.DevBluetoothCtrlActivity;

public class TuyaBleData {
    private int dpId;//功能Id
    private int dpType;//功能对应的数据类型
    private int dpDataLen;//功能对应的数据长度
    private String dpDataContent;//功能数据

    public int getDpId() {
        return dpId;
    }

    public void setDpId(int dpId) {
        this.dpId = dpId;
    }

    public int getDpType() {
        return dpType;
    }

    public void setDpType(int dpType) {
        this.dpType = dpType;
    }

    public int getDpDataLen() {
        return dpDataLen;
    }

    public void setDpDataLen(int dpDataLen) {
        this.dpDataLen = dpDataLen;
    }

    public TuyaBleData parseData(boolean isSendData, String tuyaData) {
        int len = 0;
        dpId = ConvertUtils.hexString2Int(tuyaData.substring(len, 2));
        len = 2;
        dpType = ConvertUtils.hexString2Int(tuyaData.substring(len, 4));
        len = 4;
        dpDataLen = ConvertUtils.hexString2Int(tuyaData.substring(len, 6));
        if (dpDataLen * 2 != tuyaData.length() - 6) {
            dpDataLen = ConvertUtils.hexString2Int(tuyaData.substring(len, 8));
            len = 8;
        } else {
            len = 6;
        }

        dpDataContent = tuyaData.substring(len, len + dpDataLen * 2);
        return this;
    }

    public String getSendData() {
        String sendData = "";
        sendData += String.format("%02X", dpId);
        sendData += String.format("%02X", dpType);
        sendData += String.format("%04X", dpDataLen);
        sendData += dpDataContent;
        return sendData;
    }

    public String getDpDataContent() {
        return dpDataContent;
    }

    public void setDpDataContent(String dpDataContent) {
        this.dpDataContent = dpDataContent;
    }
}
