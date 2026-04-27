# Successful Networking Process (for Token-based Login Devices)
> For devices supporting Token-based login, after successful networking, you need to obtain the device's login Token and device characteristics, otherwise, the device cannot be added to the cloud account.
## 1. Obtain random username and password for the device

Example code


```
private void getDevRandomUserPwd(XMDevInfo xmDevInfo) {
    DeviceManager.getInstance().getDevRandomUserPwd(xmDevInfo, new DeviceManager.OnDevManagerListener() {
        @Override
        public void onSuccess(String devId, int operationType, Object result) {
            // Obtain device login Token information: Login to the device first, then obtain it through DevGetLocalEncToken
            DeviceManager.getInstance().loginDev(devId, new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String devId, int operationType, Object result) {
                    // Obtain device login Token information
                    String devToken = FunSDK.DevGetLocalEncToken(devId);
                    xmDevInfo.setDevToken(devToken);
                    // Obtain device characteristics
                    getCloudCryNum(xmDevInfo);
                }

                @Override
                public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                    // Failure callback, errorId is the error code
                }
            });
        }

        @Override
        public void onFailed(String devId, int msgId, String jsonName, int errorId) {
            // Obtain random username and password, if failed due to timeout, you can try to retry
            if (isNeedGetDevRandomUserPwdAgain && (errorId == -10005 || errorId == -100000)) {
                // If obtaining random username and password times out, you can retry after a delay of 1 second
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDevRandomUserPwd(xmDevInfo);
                    }
                }, 1000);

                isNeedGetDevRandomUserPwdAgain = false;
                return;
            }
            
            if (errorId == -400009) {
                // If random username and password are not supported, login to the device with the username: admin and an empty password
                // Do you want to remove the device from other accounts
                XMPromptDlg.onShow(iDevQuickConnectView.getContext(), iDevQuickConnectView.getContext().getString(R.string.is_need_delete_dev_from_other_account), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addDevice(xmDevInfo, true);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addDevice(xmDevInfo, false);
                    }
                });
            } else {
                // "Networking failed: " + errorId
            }
        }
    });
}

```

## 2.Obtain device characteristics

Example code

```
/**
 * Obtain the characteristics of the device after successful networking. This characteristic is used to verify whether weak binding can be performed by the server,
 * and to re-add the device to the current account that has already been added to another account.
 */
private void getCloudCryNum(XMDevInfo xmDevInfo) {
    DevConfigManager devConfigManager = DeviceManager.getInstance().getDevConfigManager(xmDevInfo.getDevId());
    DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
        @Override
        public void onSuccess(String devId, int operationType, Object result) {
            if (result instanceof String) {
                try {
                    org.json.JSONObject jsonObject = new JSONObject((String) result);
                    jsonObject = jsonObject.optJSONObject(GET_CLOUD_CRY_NUM);
                    if (jsonObject != null) {
                        String cloudCryNum = jsonObject.optString("CloudCryNum");
                        xmDevInfo.setCloudCryNum(cloudCryNum);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            addDevice(xmDevInfo, true);
        }

        @Override
        public void onFailed(String devId, int msgId, String jsonName, int errorId) {
            // Obtain device characteristics, if failed due to timeout, you can try to retry
            if (isNeedGetCloudTryNum && (errorId == -10005 || errorId == -100000)) {
                // If obtaining the characteristic times out, you can retry after a delay of 1 second
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getCloudCryNum(xmDevInfo);
                    }
                }, 1000);

                isNeedGetCloudTryNum = false;
                return;
            }
        }
    });

    devConfigInfo.setJsonName(GET_CLOUD_CRY_NUM);
    devConfigInfo.setCmdId(1020);
    devConfigManager.setDevCmd(devConfigInfo);
}

```