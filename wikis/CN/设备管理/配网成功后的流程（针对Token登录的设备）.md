> 支持Token登录的设备，配网成功后，需要获取到设备登录的Token和设备的特征码，否则无法将设备添加到云账号下
## 1.获取设备的随机用户名和密码
示例代码
```
private void getDevRandomUserPwd(XMDevInfo xmDevInfo) {
        DeviceManager.getInstance().getDevRandomUserPwd(xmDevInfo, new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                //获取设备登录Token信息：先要登录设备，然后通过DevGetLocalEncToken来获取
                DeviceManager.getInstance().loginDev(devId, new DeviceManager.OnDevManagerListener() {
                    @Override
                    public void onSuccess(String devId, int operationType, Object result) {
                        //获取设备登录Token信息
                        String devToken = FunSDK.DevGetLocalEncToken(devId);
                        xmDevInfo.setDevToken(devToken);
                        //获取设备特征码
                        getCloudCryNum(xmDevInfo);
                    }

                    @Override
                    public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                         //失败回调 errorId是错误码
                    }
                });
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                //获取随机用户名和密码因为超时失败的话，可尝试重试 
                if (isNeedGetDevRandomUserPwdAgain && (errorId == -10005 || errorId == -100000)) {
                    //如果获取随机用户名密码超时的话，可以延时1s重试一次
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
                    //如果不支持随机用户名密码的话，就以 用户名：admin 密码为空登录设备
                    //是否要将该设备从其他账号下移除
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
                    //"配网失败：" + errorId
                }
            }
        });
    }

```

## 2.获取设备特征码

示例代码
```
/**
 * 获取设备配网成功后的特征码，这个特征码用来给服务器校验能否能进行弱绑定，将原来已经添加到其他账号的设备重新添加当前账号
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
            // 获取设备特征码因为超时失败的话，可尝试重试 
            if (isNeedGetCloudTryNum && (errorId == -10005 || errorId == -100000)) {
                //如果获取特征码超时的话，可以延时1s重试一次
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