# Device WiFi Signal
- Interface Description

```java
 /**
  * Get WiFi Signal
  */
private void getDevWiFiSignalLevel() {
    DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
        /**
         * Successful Callback
         *
         * @param devId         Device ID
         * @param operationType Operation Type
         * @param result        Result
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
         * Failure Callback
         *
         * @param devId    Device Serial Number
         * @param msgId    Message ID [EUIMSG]
         * @param jsonName Reference [JsonConfig]
         * @param errorId  Error Code
         */
        @Override
        public void onFailed(String devId, int msgId, String jsonName, int errorId) {
			// Failed to retrieve, analyze the specific reason through errorId
        }
    });


    devConfigInfo.setCmdId(1020);
    devConfigInfo.setJsonName(JsonConfig.WIFI_ROUTE_INFO);
    devConfigInfo.setChnId(-1);
    devConfigManager.setDevCmd(devConfigInfo);
}

```
- Response：

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
- Field Descriptions

| Field Name  | Type    | Description                                              |
| ----------- | ------- | -------------------------------------------------------- |
| WlanStatus  | Boolean | Indicates whether the wireless network is connected, true means connected |
| Eth0Status  | Boolean | Indicates whether the wired network is connected, true means connected |
| WlanMac     | String  | MAC address of the wireless network card                  |
| SignalLevel | Int     | Range (0~100) - Signal strength of the connected wireless hotspot (Result of db value + 100) |