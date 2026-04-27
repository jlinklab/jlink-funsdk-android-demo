
- 接口说明

```java
 /**
  * 获取WiFi信号
  */
private void getDevWiFiSignalLevel() {
    DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
        /**
         * 成功回调
         *
         * @param devId         设备类型
         * @param operationType 操作类型
         * @param result        结果
         */
        @Override
        public void onSuccess(String devId, int msgId, String jsonData) {
            if (jsonData != null) {
                HandleConfigData<WifiRouteInfo> handleConfigData = new HandleConfigData<>();
                if (handleConfigData.getDataObj(jsonData, WifiRouteInfo.class)) {
                    WifiRouteInfo wifiRouteInfo = handleConfigData.getObj();
                    if (iDevMonitorView != null) {
                        iDevMonitorView.onWiFiSignalLevelResult(wifiRouteInfo);
                    }
                }
            }
        }

        /**
         * 失败回调
         *
         * @param devId    设备序列号
         * @param msgId    消息ID【EUIMSG】
         * @param jsonName 参考【JsonConfig】
         * @param errorId  错误码
         */
        @Override
        public void onFailed(String devId, int msgId, String jsonName, int errorId) {
			//获取失败，通过errorId分析具体原因
        }
    });


    devConfigInfo.setCmdId(1020);
    devConfigInfo.setJsonName(JsonConfig.WIFI_ROUTE_INFO);
    devConfigInfo.setChnId(-1);
    devConfigManager.setDevCmd(devConfigInfo);
}
```
- 请求：

```json
{
    "Name": "WifiRouteInfo",
    "SessionID": "0x0000000001"
}

```

- 响应：

```json
{
    "Ret": 100,
    "SessionID": "0x00000003",
    "WifiRouteInfo": {
        "WlanStatus": true,
        "Eth0Status": true,
        "WlanMac": "00:00:00:00:00:00",
        "SignalLevel": 94
    },
    "Name": "WifiRouteInfo"
}
```


- 字段说明

| 字段名      | 类型    | 描述                                                      |
| ----------- | ------- | --------------------------------------------------------- |
| WlanStatus  | Boolean | 无线网络是否连接上，true表示连上了                        |
| Eth0Status  | Boolean | 有线网络是否连接上，true表示连上了                        |
| WlanMac     | String  | 无线网卡的mac地址                                         |
| SignalLevel | Int     | 取值范围（0~100）连接的无线热点的信号强度(db值+100的结果) |

- Demo示例图

![](https://obs-xm-pub.obs.cn-south-1.myhuaweicloud.com/docs/20231215/1702624089995.jpg)