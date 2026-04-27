# Account Registration
### 1.1 Mobile phone Number Registration
#### 1.1.1 Send the verification code to mobile phone
```
XMAccountManager.getInstance().sendPhoneCodeForRegister("Username", "Password",new BaseAccountManager.OnAccountManagerListener() {
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


#### 1.1.2 Register by mobile phone number

 ```
XMAccountManager.getInstance().registerByPhoneNo("Username", "Password","Verification Code", "Phone Number" ,BaseAccountManager.OnAccountManagerListener() {
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

### 1.2 Email Registration
#### 1.2.1 Sending the Verification Code to  Email
```
XMAccountManager.getInstance().sendEmailCodeForRegister("Email Address", new BaseAccountManager.OnAccountManagerListener() {
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

#### 1.2.2 Register via email
```
XMAccountManager.getInstance().registerByPhoneNo("Username","Password","Email Address","Verification Code", new BaseAccountManager.OnAccountManagerListener() {
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