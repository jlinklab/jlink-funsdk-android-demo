# Add device

## Before adding a device, you need to log in to your account

### 1. Quickly configure and add devices

#### 1.1. Start rapid network distribution:

- Interface Description


```

void startQuickSetWiFi(String ssid, String pwd, String capabilities, DhcpInfo dhcpInfo, DeviceManager.OnQuickSetWiFiListener listener);

```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|Ssid | WiFi hotspot|
|Pwd | WiFi password|
|Capabilities | WiFi encryption type, obtained from ScanResult|
|DhcpInfo | WiFi dhcp Information|
|Listener | callback|

- Sample code

```

DeviceManager.getInstance().startQuickSetWiFi(wifiInfo, scanResult, dhcpInfo, pwd, new DeviceManager.OnQuickSetWiFiListener() {

@Override

public void onQuickSetResult(XMDevInfo xmDevInfo, int i) {

AccountManager.getInstance().addDev(xmDevInfo, isUnbindDevUnderOther, new BaseAccountManager.OnAccountManagerListener() {

@Override

public void onSuccess(int msgId) {

Toast.makeText(iDevQuickConnectView.getContext(),

Successfully added, Toast.LENGTH_ LONG).show();

iDevQuickConnectView.onAddDevResult();

}



@Override

public void onFailed(int msgId, int errorId) {

Toast.makeText(iDevQuickConnectView.getContext(),

Add failed:+errorId, Toast.LENGTH_ LONG).show();

iDevQuickConnectView.onAddDevResult();

}



@Override

public void onFunSDKResult(Message message, MsgContent msgContent) {



}

});

}

});

```

#### 1.2. Stop rapid distribution network:

- Interface Description



```

void stopQuickSetWiFi();

```

<hr style="height:1px;border:none;border-top:1px solid #eeeeee;"/>



### 2. Manually adding devices

- Interface Description

```

void addDev(XMDevInfo xmDevInfo, boolean isUnbindDevUnderOther, BaseAccountManager.OnAccountManagerListener ls);

```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|XmDevInfo | Device information|
|IsUnbindDevUnderOther | Do you want to unbind this device from another account|
|Ls | Callback|

- Sample code



```

SDBDeviceInfo deviceInfo = new SDBDeviceInfo();

G.SetValue(deviceInfo.st_0_Devmac,devId);

G.SetValue(deviceInfo.st_5_loginPsw,pwd);

G.SetValue(deviceInfo.st_4_loginName,userName);

XMDevInfo xmDevInfo = new XMDevInfo();

xmDevInfo.sdbDevInfoToXMDevInfo(deviceInfo);

AccountManager.getInstance().addDev(xmDevInfo, new BaseAccountManager.OnAccountManagerListener() {

@Override

public void onSuccess(int i) {

if (iDevSnConnectView != null) {

iDevSnConnectView.onAddDevResult(true);

}

}



@Override

public void onFailed(int i, int i1) {

if (iDevSnConnectView != null) {

iDevSnConnectView.onAddDevResult(false);

}

}



@Override

public void onFunSDKResult(Message message, MsgContent msgContent) {



}

});

```



<hr style="height:1px;border:none;border-top:1px solid #eeeeee;" />



### 3. QR code distribution network addition

#### 3.1. Obtain WiFi QR code information for QR code distribution network:


- Interface Description

```

Bitmap initDevToRouterByQrCode(String ssid, String wifiPwd, int pwdType, String macAddress, String ipAddress, DeviceManager.OnDevWiFiSetListener listener);

```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|Ssid | WiFi hotspot name|
|WifiPwd | WiFi password|
|PwdType | Encryption type|
|MacAddress | The MAC address of the phone|
|IpAddress | The IP address of the phone connection|
|Listener | callback|

- Sample code



```

WifiInfo wifiInfo = xmWifiManager.getWifiInfo();

DhcpInfo dhcpInfo = xmWifiManager.getDhcpInfo();// Obtain Dhcp information

String ssid = getConnectWiFiSsid();// Get the currently connected WiFi hotspot

scanResult = xmWifiManager.getCurScanResult(ssid);// The ScanResult object obtained through ssid contains more network information objects

int pwdType = XUtils.getEncrypPasswordType(scanResult.capabilities);// Obtain WiFi password encryption type

if (pwdType == 3 && (wifiPwd.length() == 10 || wifiPwd.length() == 26)) {

wifiPwd = XUtils.asciiToString(wifiPwd);

}



String ipAddress = Formatter.formatIpAddress(dhcpInfo.ipAddress);// Obtain IP address

String macAddress = XMWifiManager.getWiFiMacAddress().replace(":", "");// Obtain MAC address



//Generate a QR code based on the above information

Bitmap bitmap = manager.initDevToRouterByQrCode(ssid, wifiPwd, pwdType, macAddress, ipAddress, new DeviceManager.OnDevWiFiSetListener() {

@Override

public void onDevWiFiSetState(int errorId) {

if (errorId < 0) {

//If TODO fails, you can try multiple times

}

}



@Override

public void onDevWiFiSetResult(XMDevInfo xmDevInfo) {

//After the distribution network is successful, call to add the device interface

XMAccountManager.getInstance().addDev(xmDevInfo, new BaseAccountManager.OnAccountManagerListener() {

@Override

public void onSuccess(int msgId) {



}



@Override

public void onFailed(int msgId, int errorId) {



}



@Override

public void onFunSDKResult(Message msg, MsgContent ex) {



}

});

}

});



```

<hr style="height:1px;border:none;border-top:1px solid #eeeeee;"/>



#### 3.2. Start QR code distribution:

- Interface Description



```

void startDevToRouterByQrCode();



```



- Sample code



```

//Align the QR code with the camera of the device and start network distribution

DeviceManager.getInstance().startDevToRouterByQrCode();

```

#### 3.3. Stop QR code distribution:

- Interface Description

```

void stopSetDevToRouterByQrCode();

```

- Sample code

```

DeviceManager.getInstance().stopSetDevToRouterByQrCode();

```

#### 3.4. Release resources:

- Interface Description

```

void unInitDevToRouterByQrCode();

```



- Sample code



```

DeviceManager.getInstance().unInitDevToRouterByQrCode();

```



### 4. Adding devices to a local area network

#### 4.1. Obtain a list of LAN devices

- Interface Description



```

void searchLanDevice(OnSearchLocalDevListener listener);

```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|Listener | callback|

- Sample code

```

DeviceManager.getInstance().searchLanDevice(new DeviceManager.OnSearchLocalDevListener() {

/**

*

*List of LAN devices found in @ param list

*/

@Override

public void onSearchLocalDevResult(List<XMDevInfo> list) {



}

});

```



#### 4.2. Adding Devices

- Interface Description

```

void addDev(XMDevInfo xmDevInfo, BaseAccountManager.OnAccountManagerListener ls)

```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|XmDevInfo | Device information|
|Ls | Callback|

- Sample code

```

XMAccountManager.getInstance().addDev(xmDevInfo, new BaseAccountManager.OnAccountManagerListener() {

@Override

public void onSuccess(int msgId) {

if (msgId == EUIMSG.SYS_ADD_DEVICE) {

if (iDevLanConnectView != null) {

iDevLanConnectView.onAddDevResult(true,0);

}

}



@Override

public void onFailed(int msgId, int errorId) {

if (msgId == EUIMSG.SYS_ADD_DEVICE) {

if (iDevLanConnectView != null) {

iDevLanConnectView.onAddDevResult(false,errorId);

}

}

}



@Override

public void onFunSDKResult(Message msg, MsgContent ex) {



}

});

```



### 5. Bluetooth distribution network

#### 5.1. Search for Bluetooth devices

- Interface Description

```

void startScan(Context context, final IXMBleManagerListener iBleManagerListener)

```

- Parameter Description
|Parameter | Comment|
| :--------- | -------- |
|Listener | callback|

- Sample code


```

XMBleManager.getInstance().setTimeOut((int) (SEARCH_TIME * 1000)).startScan(AddDeviceWithWifiBaseStationActivity.this, new IXMBleManagerListener()

{

@Override

public void onBleScanResult(XMBleInfo xmBleInfo) {

super.onBleScanResult(xmBleInfo);



}

});

```





##### XMDevInfo

|Field Name | Description|
| ------ | ------ |
|DevId | Device serial number|
|DevName | Device name|
|DevUserName | Device login name|
|DevPassword | Device login password|
|DevIp | Device IP address|
|Pid | Product type, similar to devType, will replace devType in new products|
|MAC | MAC address|
|DevPort | Device port number|
|DevType | Device type|
|DevState | Device online state|
|CloudState | Cloud service state|
|CloudExpired | Cloud service expiration status|
|SystemInfoBean | Cached device information|
|SystemInfoExBean | Cached device extension information|
|SystemFunctionBean | Cached device capability set|
|SdbDevInfo | Device online status|
|CloudState | Cloud service state|
|CloudExpired | Cloud service expiration status|
|SystemInfoBean | Compatible with device information from older SDKs|
|ChipOEMId | Chip OEMId|