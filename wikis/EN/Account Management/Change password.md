# Change password

- Interface Description



```
boolean modifyPwd(String userName, String oldPassw, String newPassw, BaseAccountManager.OnAccountManagerListener ls)

```

- Parameter Description



|Parameter | Comment|
| :--------- | -------- |
|UserName | User name|
|OldPassword | Old password|
|NewPassword | New password|
|Ls | Callback|



- Sample code



```

XMAccountManager.getInstance().modifyPwd(userName, oldPwd, newPwd, new BaseAccountManager.OnAccountManagerListener() {
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