# Device Firmware Upgrade
### 1. Firmware Upgrade Check
- API Description

```
void checkDevUpgrade(String devId, DeviceManager.OnDevUpradeListener listener);
```
- Parameter Description


| Parameter        | Description|
| :--------- | -------- |
| devId |Device serial number   |
| listener|Callback|

- Sample Code

```
DeviceManager.getInstance().checkDevUpgrade(getDevId(), new DeviceManager.OnDevUpgradeListener() {
    /**
     * Callback for checking version upgrade
     *
     * @param upgradeType Upgrade type
     * @param verInfo     Upgrade version information
     * @param errorId     Error ID
     */
    @Override
    public void onCheckUpgradeResult(int upgradeType, String verInfo, int errorId) {

    }
    
    /**
     * Upgrade callback
     *
     * @param upgradeState Upgrade state
     *                    - ECONFIG.EUPGRADE_STEP_DOWN: Downloading firmware
     *                    - ECONFIG.EUPGRADE_STEP_UP: Uploading firmware
     *                    - ECONFIG.EUPGRADE_STEP_UPGRADE: Upgrading firmware
     *                    - ECONFIG.EUPGRADE_STEP_COMPLETE: Firmware upgrade completed
     * @param progress     Progress based on upgradeState value (Download progress, Upload progress, Upgrade progress)
     * @param errorId      Upgrade error
     * @param info         Information during the upgrade process
     */
    @Override
    public void onUpgradeProgress(int upgradeState, int progress, int errorId, String info) {

    }
});

```

### 2. Start Device Firmware Upgrade
- API Description

```
 void startDevUpgrade(String devId, int upgradeType, DeviceManager.OnDevUpgradeListener listener);
```
- Parameter Description


| Parameter | Description|
| :--------- | -------- |
| devId |Device serial number|
| upgradeType|Upgrade type|
| listener|Callback|

- upgradeType Upgrade Types

| Parameter       | Description               |
| :-------------- | ------------------------- |
| UPGRADE_TYPE_NONE | No upgrade available      |
| UPGRADE_TYPE_CLOUD | Cloud upgrade             |
| UPGRADE_TYPE_FILE_DOWNLOAD | Firmware download and upgrade |
| UPGRADE_TYPE_LOCAL_FILE | Local firmware upgrade    |


- Sample Code


```
DeviceManager.getInstance().startDevUpgrade(getDevId(), upgradeType, new DeviceManager.OnDevUpgradeListener() {
    /**
     * Callback for checking version upgrade
     *
     * @param upgradeType Upgrade type
     * @param verInfo     Upgrade version information
     * @param errorId     Error ID
     */
    @Override
    public void onCheckUpgradeResult(int upgradeType, String verInfo, int errorId) {

    }
    
    /**
     * Upgrade callback
     *
     * @param upgradeState Upgrade state
     *                    - ECONFIG.EUPGRADE_STEP_DOWN: Downloading firmware
     *                    - ECONFIG.EUPGRADE_STEP_UP: Uploading firmware
     *                    - ECONFIG.EUPGRADE_STEP_UPGRADE: Upgrading firmware
     *                    - ECONFIG.EUPGRADE_STEP_COMPLETE: Firmware upgrade completed
     * @param progress     Progress based on upgradeState value (Download progress, Upload progress, Upgrade progress)
     * @param errorId      Upgrade error
     * @param info         Information during the upgrade process
     */
    @Override
    public void onUpgradeProgress(int state, int progress, int errorId, String info) {

    }
});


```

### 3. Upgrade Device via Local Firmware File

- API Description

```
void startDevUpgradeByLocalFile(String devId, String filePath, DeviceManager.OnDevUpgradeListener listener);
```
- Parameter Description

| Parameter | Description      |
| :-------- | ---------------- |
| devId     | Device serial number |
| filePath  | Firmware file path   |
| listener  | Callback            |


- Sample Code

```
DeviceManager.getInstance().startDevUpgradeByLocalFile(getDevId(), filePath, new DeviceManager.OnDevUpgradeListener() {
    /**
     * Callback for checking version upgrade
     *
     * @param upgradeType Upgrade type
     * @param verInfo     Upgrade version information
     * @param errorId     Error ID
     */
    @Override
    public void onCheckUpgradeResult(int upgradeType, String verInfo, int errorId) {

    }
    
    /**
     * Upgrade callback
     *
     * @param upgradeState Upgrade state
     *                    - ECONFIG.EUPGRADE_STEP_DOWN: Downloading firmware
     *                    - ECONFIG.EUPGRADE_STEP_UP: Uploading firmware
     *                    - ECONFIG.EUPGRADE_STEP_UPGRADE: Upgrading firmware
     *                    - ECONFIG.EUPGRADE_STEP_COMPLETE: Firmware upgrade completed
     * @param progress     Progress based on upgradeState value (Download progress, Upload progress, Upgrade progress)
     * @param errorId      Upgrade error
     * @param info         Information during the upgrade process
     */
    @Override
    public void onUpgradeProgress(int state, int progress, int errorId, String info) {

    }
});

```

### 4. Upgrade Callback Functions
```
 public interface OnDevUpgradeListener {
    /**
     * Callback for checking version upgrade
     *
     * @param upgradeType Upgrade type
     * @param verInfo     Upgrade version information
     * @param errorId     Error ID
     */
    void onCheckUpgradeResult(int upgradeType, String verInfo, int errorId);

    /**
     * Upgrade callback
     *
     * @param upgradeState Upgrade state
     *                    - ECONFIG.EUPGRADE_STEP_DOWN: Downloading firmware
     *                    - ECONFIG.EUPGRADE_STEP_UP: Uploading firmware
     *                    - ECONFIG.EUPGRADE_STEP_UPGRADE: Upgrading firmware
     *                    - ECONFIG.EUPGRADE_STEP_COMPLETE: Firmware upgrade completed
     * @param progress     Progress based on upgradeState value (Download progress, Upload progress, Upgrade progress)
     * @param errorId      Upgrade error
     * @param info         Information during the upgrade process
     */
    void onUpgradeProgress(int upgradeState, int progress, int errorId, String info);
}

```