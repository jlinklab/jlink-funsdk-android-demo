# Login device

### 1. Obtain device status (obtain device status before logging in)

#### 1.1. Obtain individual device status:

- Interface Description

```

void updateDevStateFromServer(BaseAccountManager.OnDevStateListener listener, String devId);

```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|Listener | callback|
|DevId | Device serial number|

- Sample code

```

XMAccountManager.getInstance().updateDevStateFromServer(new BaseAccountManager.OnDevStateListener() {

@Override

public void onUpdateDevState(String s) {

}

@Override

public void onUpdateCompleted() {

}

}, devId);

```


#### 1.2. Obtain all device statuses:

- Interface Description

```

void updateAllDevStateFromServer(List<String> devIdList, BaseAccountManager.OnDevStateListener listener);

```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|DevIdList | Device list|
|Ls | Callback|

- Sample code



```

XMAccountManager.getInstance().updateAllDevStateFromServer( XMAccountManager.getInstance().getDevList(), new BaseAccountManager.OnDevStateListener() {

@Override

public void onUpdateDevState(String devId) {

//Single device status callback

}



@Override

public void onUpdateCompleted() {

//All device status acquisition end callback

}

});

```



<hr style="height:1px;border:none;border-top:1px solid #eeeeee;" />



### 2. Login device

#### 2.1. Normal device login:

- Interface Description



```

boolean loginDev(String devId, DeviceManager.OnDevManagerListener listener);

```

- Parameter Description



|Parameter | Comment|
| :--------- | -------- |
|DevId | Device serial number|
|Listener | callback|



- Sample code

```

DeviceManager.getInstance().loginDev(devId, new DeviceManager.OnDevManagerListener() {

/**

*Successful callback

*

*@ param devId Device Type

*@ param operationType operation type

*@ param result result

*/

@Override

public void onSuccess(String devId, int operationType, Object o) {



}



/**

*Failed callback

*

*@ param devId device serial number

*@ param msgId Message ID

* @param jsonName

*@ param errorId error code

*/

@Override

public void onFailed(String devId, int msgId, String jsonName, int i1) {



}

});



```



#### 2.2. Low power device login:

- Interface Description

```

/**

*Logging into low-power devices

*Low power devices need to be awakened, and EventBus needs to be integrated in the project

*/

boolean loginDevByLowPower(String devId, DeviceManager.OnDevManagerListener listener);

```

- Parameter Description



|Parameter | Comment|
| :--------- | -------- |
|DevId | Device serial number|
|Listener | callback|

- Sample code

```

DeviceManager.getInstance().loginDevByLowPower(devId, new DeviceManager.OnDevManagerListener() {

/**

*Successful callback

*

*@ param devId Device Type

*@ param operationType operation type

*@ param result result

*/

@Override

public void onSuccess(String devId, int operationType, Object o) {



}



/**

*Failed callback

*

*@ param devId device serial number

*@ param msgId Message ID

* @param jsonName

*@ param errorId error code

*/

@Override

public void onFailed(String devId, int msgId, String jsonName, int i1) {



}

});

```



<hr style="height:1px;border:none;border-top:1px solid #eeeeee;" />