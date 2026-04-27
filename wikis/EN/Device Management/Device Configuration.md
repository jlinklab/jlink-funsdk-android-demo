# Device Configuration

### 1. Obtain device configuration

- Interface Description

```
DevConfigInfo create(DeviceManager.OnDevManagerListener listener, String... field);

```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|Field | Configuration name|
|Listener | callback|

- Sample code

```
DevConfigInfo devConfigInfo = DevConfigInfo.create(new DevConfigManager.OnDevConfigResultListener() {

@Override

public void onSuccess(String devId, int msgId, Object result) {



}



@Override

public void onFailed(String devId, int msgId, String s1, int errorId) {



}



@Override

public void onFunSDKResult(Message message, MsgContent msgContent) {



}

});

------------------------------Most configurations----------------------------------------

devConfigInfo.setJsonName(jsonName);// Set Configuration JsonName: Find the JsonName information you want to obtain through the JsonConfig class

devConfigInfo.setChnId(chnId);// Channel ID: If the configuration of the entire device is configured to transmit -1, the corresponding channel number will be transmitted for the channel, such as 0 for the first channel, 1 for the second channel, and so on

devConfigManager.getDevConfig(devConfigInfo);// Get Configuration

devConfigManager.setDevConfig(devConfigInfo);// Save Configuration



------------------------------Other special commands--------------------------------------



devConfigInfo.setJsonName(jsonName);// Set Configuration JsonName: Find the JsonName information you want to obtain through the JsonConfig class

devConfigInfo.setJsonData(jsonData);// JSON data sent

For example, restarting the device

JSONObject obj = new JSONObject();

obj.put("Action", "Reboot");

String jsonData = HandleConfigData.getSendData(JsonConfig.OPERATION_OPMACHINE, "0x01", obj)

devConfigInfo.setJsonData(jsonData);// JSON data sent



devConfigInfo.setCmdId(cmdId);// Command ID: Through EDEV_ JSON_ ID class found the command ID you want to set

devConfigInfo.setChnId(chnId);// Channel ID: If the configuration of the entire device is configured to transmit -1, the corresponding channel number will be transmitted for the channel, such as 0 for the first channel, 1 for the second channel, and so on

devConfigManager.setDevCmd(devConfigInfo);

```



### 2. Save Configuration



- Interface Description



```
void saveConfig(String jsonName, int chnId, String jsonData)

```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|JsonName | Configuration name|
|ChnId | Channel number|
|JsonData | Configuration content|

- Sample code

```
/**

*

*@ param jsonName Configuration name such as' Camera. WhiteLight '

*The @ param chnId channel number is as shown in -1

*The configuration content of @ param jsonData is as follows: {"MoveTrigLight": {"Duration": 60, "Level": 3}, "WorkMode": "Auto", "WorkPeriod": {"EHour": 6, "EMinute": 0, "SHOur": 18, "SMinute": 0}

*/

presenter.saveConfig(jsonName, spConfigChn.getSelectedValue(), jsonData);

```