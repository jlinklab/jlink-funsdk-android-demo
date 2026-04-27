# Login Account
### 1.Account login
```
XMAccountManager.getInstance().login("Username", "Password", LOGIN_BY_INTERNET, new BaseAccountManager.OnAccountManagerListener() {
            @Override
            public void onSuccess(int msgId) {
                
            }

            @Override
            public void onFailed(int msgId, int errorId) {

            }

            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {

            }
        })
```

### 2. Local login
```
String localPath = Environment.getExternalStorageDirectory();//Phone local path
LocalAccountManager.getInstance().login(localPath, new BaseAccountManager.OnAccountManagerListener() {
            @Override
            public void onSuccess(int msgId) {
                
            }

            @Override
            public void onFailed(int msgId, int errorId) {

            }

            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {

            }
        })
```