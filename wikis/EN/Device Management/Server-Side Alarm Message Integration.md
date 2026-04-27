# Server-Side Alarm Message Integration
Sent to a third-party server via HTTP POST:
Body format: "param=" + json

## Example
```
POST / HTTP/1.1
Host: xxxx
Content-Type: application/x-www-form-urlencoded
Content-Length: xxx

```
```
param={
    "AlarmEvent": "VideoMotion:1",            // Alarm type
    "AlarmID": "18101893921",                // Message ID
    "AlarmMsg": "",                            // Additional information
    "AlarmTime": "2018-10-18 09:39:21",        // Alarm time
    "AuthCode": "3ead6a672e1c3724",            // Ignore
    "Channel": 0,                            // Alarm channel number
    "SerialNumber": "3ead6a672e1c3724",        // Device serial number
    "Status": "appEventStart"                // Alarm status (Start or End)
}

```

## Explanation of Alarm Types (AlarmEvent)
| 报警类型	 | 解释说明|
| - | - |
| MotionDetect | Motion detection|
| Motion_detection| Motion detection|
| VideoMotion|Motion alarm|
| BlindDetect| Video blockage|
| VideoBlind| Video blockage|
| VideoMotion| Motion alarm|
| BlindDetect| Video blockage|
| StorageNotExist| Hard disk not exist|
| StorageFailure| Storage failure|
| StorageLowSpace| Insufficient storage space|
| VideoAnalyze| Video analysis|
| IPCAlarm| IPC alarm|
| LocalAlarm LocalIO| Local alarm|
| appEventHumanDetectAlarm| Human detection|
| FaceDetection|Face detection|
| VideoLoss|Video loss