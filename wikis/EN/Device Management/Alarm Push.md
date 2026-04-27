# Alarm Push
## Initialize
- Interface Description

```
boolean initFunSDKPush(Context context,SMCInitInfo info, int pushType);

```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|Context | Context|
|Info | Push initialization information|
|PushType | Push type, such as PUSH_ TYPE_ XM |

##### PushType

|Field Name | Description|
| ------ | ------ |
| PUSH_ TYPE_ XM | Xiongmai Push|
| PUSH_ TYPE_ Google | Google Push|
| PUSH_ TYPE_ HUAWEI | Huawei Push|
| PUSH_ TYPE_ XG | Carrier pigeon push|
| PUSH_ TYPE_ WECHAT | WeChat Push|
| PUSH_ TYPE_ LINE | Line Push|
| PUSH_ TYPE_ XIAOMI | Xiaomi Push|
| PUSH_ TYPE_ VIVO | VIVO Push|
| PUSH_ TYPE_ OPPO | OPPO push|
| PUSH_ TYPE_ Honor Push|
- Sample code

```

XMPushManager xmPushManager = new XMPushManager(xmPushLinkResult);

String pushToken = XUtils.getPushToken(this);

if (!StringUtils.isStringNULL(pushToken)) {

SMCInitInfo info = new SMCInitInfo();

G.SetValue(info.st_0_user, XMAccountManager.getInstance().getAccountName());

G.SetValue(info.st_1_password, XMAccountManager.getInstance().getPassword());

G.SetValue(info.st_2_token, pushToken);

xmPushManager.initFunSDKPush(info,PUSH_TYPE_XM);

}

```
- Example Code for URL Initialization Alarm Message

```
XMPushManager  xmPushManager = new XMPushManager(xmPushLinkResult);
String pushToken = XUtils.getPushToken(this);
if (!StringUtils.isStringNULL(pushToken)) {
    SMCInitInfo info = new SMCInitInfo();
    G.SetValue(info.st_0_user, XMAccountManager.getInstance().getAccountName());
    G.SetValue(info.st_1_password, XMAccountManager.getInstance().getPassword());
    G.SetValue(info.st_2_token, pushToken);
    G.SetValue(info.st_5_appType,"Third:http://xxxxx xxxx");
    // (Example: Third:http(s)://xmey.net:80/xxx/xxx or Third:ip:80 separated by ":" in between)
    xmPushManager.initFunSDKPush(this,info,PUSH_TYPE_XM);
}

```
> [Server-Side Alarm Message Integration Guide](https://developer.jftech.com/docs/?menusId=ab9a6dddd50c46a6af8a913b472ed015&siderid=205e3f2a9a47424ea739f5f5b679eee6&lang=zh)

## Subscription

- Interface Description

```
void linkAlarm(String devId, int seq);

```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|DevId | Device serial number|
|Seq | Return Id|

- Sample code

```
xmPushManager.linkAlarm(devId,0);
```

## Unsubscribe

- Interface Description

```

void unLinkAlarm(String devId, int seq);

```

-Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|DevId | Device serial number|
|Seq | Return Id|

- Sample code

```
xmPushManager.unLinkAlarm(devId,0);

```
## Receive push information



```

public interface OnXMPushLinkListener {

/**

*Subscription initialization

*

* @param pushType

* @param errorId

*/

void onPushInit(int pushType, int errorId);



/**

*Subscription callback

*

* @param pushType

* @param devId

* @param seq

* @param errorId

*/

void onPushLink(int pushType, String devId, int seq, int errorId);


/**

*Unsubscribe callback

*

* @param pushType

* @param devId

* @param seq

* @param errorId

*/

void onPushUnLink(int pushType, String devId, int seq, int errorId);



/**

*Obtain subscription status from the server

*

*@ param pushType Push Type

*@ param devId device serial number

*Do you want to subscribe to @ param isLinked

*/

void onIsPushLinkedFromServer(int pushType, String devId, boolean isLinked);



/**

*Alarm push callback

*

* @param pushType

* @param devId

* @param msg

* @param ex

*/

void onAlarmInfo(int pushType, String devId, Message msg, MsgContent ex);



/**

*Subscription disconnect callback

*

* @param pushType

* @param devId

*/

void onLinkDisconnect(int pushType, String devId);



/**

*WeChat push status

*

* @param devId

* @param state

* @param errorId

*/

void onWeChatState(String devId, int state, int errorId);



/**

*Third party push status

*

* @param info

* @param pushType

* @param state

* @param errorId

*/

void onThirdPushState(String info, int pushType, int state, int errorId);



/**

*All subscription cancellation callbacks

*/

void onAllUnLinkResult();



/**

*All data returned by FunSDK

*

* @param msg

* @param ex

* @return

*/

void onFunSDKResult(Message msg, MsgContent ex);



}

```