# Device List
### 1. Get device status(You must get the device status before logging in to the device)
```
XMAccountManager.getInstance().updateAllDevStateFromServer( AccountManager.getInstance().getDevList(), new BaseAccountManager.OnDevStateListener() {
    @Override
    public void onUpdateDevState(String devId) {
        //Single device status callback
         XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(devId);
        //Enumeration corresponding to device state
        //public static final int OFF_LINE = 0;//offline
        //public static final int ON_LINE = 1;//online
        //public static final int SLEEP = 2;//dormancy
        //public static final int WAKE_UP = 3;//On the rise
        //public static final int WAKE = 4;//Have been awakened
        //public static final int SLEEP_UNWAKE = 5;// Sleep cannot be awakened
        //public static final int PREPARE_SLEEP = 6;//Ready for sleep
        xmDevInfo.getDevState()
    }

    @Override
    public void onUpdateCompleted() {
        //All device status acquisition end callback
    }
});
```

### 2.Obtaining the Device List(You must log in to the device before obtaining the device list)
```
XMAccountManager.getInstance().getDevList(); // Obtain the device list
XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo("devId"); 
// Obtain the device information according to the device serial number
```
#### XMDevInfo

| Field name | Instructions|
| --- | --- |
| devId | device serial number |
| devName  |  Device Name|
| devUserName | Device login Name|
| devPassword | Device Login Password|
| devIp | Device IP address|
| pid | Product type, similar to devType, will be replaced in the new product|
| mac | MAC Address|
| devPort | Device port |
| devType | Type of device|
| devState | Device Online Status|
| cloudState | Cloud Service Status|
| cloudExpired | The cloud service expired status|
| systemInfoBean | Cached device information|
| systemInfoExBean | Cache device extension information|
| systemFunctionBean | Cached device capability set|
| sdbDevInfo | Device Online Status|
| cloudState | Cloud Service Status|
| cloudExpired | The cloud service expired status|
| systemInfoBean | Compatible with old SDK device information|
| chipOEMId | Chip OEMId|

#### Enumerates the device status

| Field name | Instructions|
| --- | --- |
| OFF_LINE：0 | offline|
| ON_LINE：1  | online|
| SLEEP：2 | Sleeping (low-power devices) |
| WAKE_UP：3 | Waking up (low power device) |
| WAKE：4 | Woke up (low power device)|
| SLEEP_UNWAKE：5 | Unwakeable during sleep (low power device)|
| PREPARE_SLEEP：6 | Preparing for Sleep (low power device)|

#### CloudState Enumerates the cloud service status

| Field name | Instructions|
| --- | --- |
| CLOUD_GET_STATE：0 | getting the status |
| CLOUD_NOT_SUPPORT：1 |Not supported|
| CLOUD_NOT_OPEND：2 | Not opened|
| CLOUD_NORMAL：3 | Has been opened, in normal use |
| CLOUD_EXPIRED：4 | Has expired|