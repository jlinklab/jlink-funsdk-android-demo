# Device 4G Signal
## Interface Description

```java
 /**
  * Retrieve 4G signal
  */
private void getDev4GSignalLevel() {
    DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
        /**
         * Success callback
         *
         * @param devId         Device type
         * @param operationType Operation type
         * @param result        Result
         */
        @Override
        public void onSuccess(String devId, int msgId, String jsonData) {
            if (jsonData != null) {
                HandleConfigData<Dev4GInfoBean> handleConfigData = new HandleConfigData<>();
                if (handleConfigData.getDataObj(jsonData, Dev4GInfoBean.class)) {
                    Dev4GInfoBean dev4gInfoBean = handleConfigData.getObj();
                    if (iDevMonitorView != null) {
                        iDevMonitorView.on4GSignalLevelResult(dev4gInfoBean );
                    }
                }
            }
        }

        /**
         * Failure callback
         *
         * @param devId    Device serial number
         * @param msgId    Message ID【EUIMSG】
         * @param jsonName Reference【JsonConfig】
         * @param errorId  Error code
         */
        @Override
        public void onFailed(String devId, int msgId, String jsonName, int errorId) {
			// Retrieval failed, analyze the specific reason through errorId
        }
    });


    devConfigInfo.setCmdId(1020);
    devConfigInfo.setJsonName(JsonConfig.DEV_4G_INFO);
    devConfigInfo.setChnId(-1);
    devConfigManager.setDevCmd(devConfigInfo);
}


```

- Request：

```json
{
    "Name": "4GInfo",
    "SessionID": "0x0000000198"
}


```

- Response:

```json
{
    "4GInfo": {
        "SignalLevel": 1
    },
    "Name": "4GInfo",
    "Ret": 100,
    "SessionID": "0x00000198"
}


```
- Field Description

| Field        | Type | Description                                      |
| ------------ | ---- | ------------------------------------------------ |
| SignalLevel  | Int  | Value range (0~100) indicating 4G signal strength |