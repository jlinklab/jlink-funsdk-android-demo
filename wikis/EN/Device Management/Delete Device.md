# Delete Device
```
AccountManager.getInstance().deleteDev(devId, new BaseAccountManager.OnAccountManagerListener() {
    @Override
    public void onSuccess(int msgId) {
        
    }

    @Override
    public void onFailed(int msgId, int errorId) {

    }

    @Override
    public void onFunSDKResult(Message message, MsgContent msgContent) {

    }
});
```