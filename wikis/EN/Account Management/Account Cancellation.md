# Account Cancellation
### 1. Call the delete interface for the first time (if the mobile phone number or email address has not been bound, it will be deleted directly; if it is bound, the verification code will be sent to the mobile phone or email address, and then call it again)
```
   XMAccountManager.getInstance().deleteXMAccount("", new BaseAccountManager.OnAccountManagerListener() {
            @Override
            public void onSuccess(int msgId) {

            }

            @Override
            public void onFailed(int msgId, int errorId) {

            }

            @Override
            public void onFunSDKResult(Message message, MsgContent msgContent) {

            }
        })

```

### 2. Call the delete interface for the second time (when the phone number or email is bound)
```
   XMAccountManager.getInstance().deleteXMAccount("Verification Code", new BaseAccountManager.OnAccountManagerListener() {
            @Override
            public void onSuccess(int msgId) {

            }

            @Override
            public void onFailed(int msgId, int errorId) {

            }

            @Override
            public void onFunSDKResult(Message message, MsgContent msgContent) {

            }
        })

```