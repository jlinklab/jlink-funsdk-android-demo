# User Information
- API Description

```java
/**
 * Get user information
 *
 * @param ls Callback
 * @return
 */
boolean getUserInfo(BaseAccountManager.OnAccountManagerListener ls);

```

- Sample Code

```java
XMAccountManager.getInstance().getUserInfo(new BaseAccountManager.OnAccountManagerListener() {
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
