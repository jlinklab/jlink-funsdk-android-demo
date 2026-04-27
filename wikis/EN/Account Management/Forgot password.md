# Forgot Password

## Interface Overview
| Interface Class | Interface Name | Interface Description | Remarks |
| --------------- | -------------- | ---------------------- | ------- |
| XMAccountManager | sendEmailCodeForResetPwd | Send email for password reset verification | |
| XMAccountManager | sendPhoneCodeForResetPwd | Send phone for password reset verification | |
| XMAccountManager | verifyEmailCode | Verify email verification code | |
| XMAccountManager | verifyPhoneCode | Verify phone verification code | |
| XMAccountManager | resetPwdByEmail | Reset password via email | |
| XMAccountManager | resetPwdByPhone | Reset password via phone | |

### 1. Get Verification Code
#### 1.1. Get Verification Code via Email

- Interface Description

```java
/**
 * Send email for password reset verification
 *
 * @param email Email
 * @param ls    Callback
 * @return
 */
boolean sendEmailCodeForResetPwd(String email, BaseAccountManager.OnAccountManagerListener ls);

```
- Parameter Description

| Parameter | Type | Description | Remarks |
| --------- | ---- | ----------- | ------- |
| email     | String | Email | |
| ls        | OnAccountManagerListener | Callback interface | Returns success and failure, including error information for failures |

- Example Code

```java
XMAccountManager.getInstance().sendEmailCodeForResetPwd(email, new BaseAccountManager.OnAccountManagerListener() {
            @Override
            public void onSuccess(int msgId) {
                // Success callback
            }
        
            @Override
            public void onFailed(int msgId, int errorId) {
              // Failure callback, errorId error code
            }
        
            @Override
            public void onFunSDKResult(Message message, MsgContent msgContent) {
        
            }
});

```
#### 1.2.Get Verification Code via Phone Number
- API Description

```
/**
 * Send verification code for resetting password via phone
 *
 * @param phone Phone number
 * @param ls    Callback
 * @return
 */
boolean sendPhoneCodeForResetPwd(String phone, BaseAccountManager.OnAccountManagerListener ls);

```
- Parameter Description

User
| Parameter | Type                        | Description                                     | Remarks                                       |
| --------- | --------------------------- | ----------------------------------------------- | --------------------------------------------- |
| phone     | String                      | Phone number                                   |                                               |
| ls        | OnAccountManagerListener    | Callback interface                             | Returns success and failure, including error information |


- Sample Code


```
XMAccountManager.getInstance().sendPhoneCodeForResetPwd(phone, new BaseAccountManager.OnAccountManagerListener() {
    @Override
    public void onSuccess(int msgId) {
        // Successful callback
    }

    @Override
    public void onFailed(int msgId, int errorId) {
        // Failure callback, errorId error code
    }

    @Override
    public void onFunSDKResult(Message message, MsgContent msgContent) {

    }
});

```
### 2. Submit Verification Code (Verify Code)
#### 2.1.Verify Email Verification Code

- API Description

```
/**
 * Verify email verification code
 *
 * @param email      Email
 * @param verifyCode Verification code
 * @param ls         Callback
 * @return
 */
boolean verifyEmailCode(String email, String verifyCode, BaseAccountManager.OnAccountManagerListener ls);

```
- Parameter Description

User
| Parameter   | Type                      | Description                               | Remarks                                         |
| ----------- | ------------------------- | ----------------------------------------- | ----------------------------------------------- |
| email       | String                    | Email                                     |                                                 |
| verifyCode  | String                    | Verification code                         |                                                 |
| ls          | OnAccountManagerListener  | Callback interface                        | Returns success and failure, including error information |


- Sample Code

```
XMAccountManager.getInstance().verifyEmailCode(email, verifyCode, new BaseAccountManager.OnAccountManagerListener() {
    @Override
    public void onSuccess(int msgId) {
        // Successful callback
    }

    @Override
    public void onFailed(int msgId, int errorId) {
        // Failure callback, errorId error code
    }

    @Override
    public void onFunSDKResult(Message message, MsgContent msgContent) {

    }
});

```
#### 2.2. Verify Phone Verification Code

- API Description

```
/**
 * Verify phone number verification code
 *
 * @param phone      Phone number
 * @param verifyCode Verification code
 * @param ls         Callback
 * @return
 */
boolean verifyPhoneCode(String phone, String verifyCode, BaseAccountManager.OnAccountManagerListener ls);

```
- Parameter Description

User
| Parameter   | Type                      | Description                               | Remarks                                         |
| ----------- | ------------------------- | ----------------------------------------- | ----------------------------------------------- |
| phone       | String                    | Phone number                              |                                                 |
| verifyCode  | String                    | Verification code                         |                                                 |
| ls          | OnAccountManagerListener  | Callback interface                        | Returns success and failure, including error information |


- Sample Code

```
XMAccountManager.getInstance().verifyPhoneCode(phone, verifyCode, new BaseAccountManager.OnAccountManagerListener() {
    @Override
    public void onSuccess(int msgId) {
        // Successful callback
    }

    @Override
    public void onFailed(int msgId, int errorId) {
        // Failure callback, errorId error code
    }

    @Override
    public void onFunSDKResult(Message message, MsgContent msgContent) {

    }
});

```

### 3. Change Password
#### 3.1.Reset Password via Email
- API Description


```
/**
 * Reset password via email
 *
 * @param email    Email
 * @param newPassw New password
 * @param ls       Callback
 * @return
 */
boolean resetPwdByEmail(String email, String newPassw, BaseAccountManager.OnAccountManagerListener ls);

```
- Parameter Description

User
| Parameter | Type                        | Description     | Remarks                                       |
| --------- | --------------------------- | --------------- | --------------------------------------------- |
| email     | String                      | Email           |                                               |
| newPassw   | String                      | New password    |                                               |
| ls        | OnAccountManagerListener    | Callback        | Returns success and failure, including error information |


- Sample Code

```
XMAccountManager.getInstance().resetPwdByEmail(email, newPwd, new BaseAccountManager.OnAccountManagerListener() {
    @Override
    public void onSuccess(int msgId) {
        // Successful callback
    }

    @Override
    public void onFailed(int msgId, int errorId) {
        // Failure callback, errorId error code
    }

    @Override
    public void onFunSDKResult(Message message, MsgContent msgContent) {

    }
});

```

#### 3.2.Reset Password via Phone Number
- API Description

```
/**
 * Reset password via phone number
 *
 * @param phone    Phone number
 * @param newPassw New password
 * @param ls       Callback
 * @return
 */
boolean resetPwdByPhone(String phone, String newPassw, BaseAccountManager.OnAccountManagerListener ls);

```
- Parameter Description

User
| Parameter | Type                        | Description     | Remarks                                       |
| --------- | --------------------------- | --------------- | --------------------------------------------- |
| phone     | String                      | Phone number    |                                               |
| newPassw   | String                      | New password    |                                               |
| ls        | OnAccountManagerListener    | Callback        | Returns success and failure, including error information |


- Sample Code

```
XMAccountManager.getInstance().resetPwdByPhone(phone, newPwd, new BaseAccountManager.OnAccountManagerListener() {
    @Override
    public void onSuccess(int msgId) {
        // Successful callback
    }

    @Override
    public void onFailed(int msgId, int errorId) {
        // Failure callback, errorId error code
    }

    @Override
    public void onFunSDKResult(Message message, MsgContent msgContent) {

    }
});

```
## Error Codes
[View Error Codes](https://docs.jftech.com/#/docs?menusId=ab0ed73834f54368be3e375075e27fb2&siderid=a5e54a6f9ab34703b0b53131f7a2b458&lang=zh&directory=false)