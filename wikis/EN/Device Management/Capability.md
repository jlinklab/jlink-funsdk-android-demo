# Capability

## Get Device Capabilities

- API Description

```
/**
 * Get device capabilities
 *
 * @param devId    Device serial number
 * @param listener Callback
 * @param field    Capability attributes
 *
 * @return
 */
boolean getDevAbility(String devId, DeviceManager.OnDevManagerListener listener, String... field);

```

- Parameter Description 

| Parameter | Description |
| :-------- | ----------- |
| devId      | Device serial number  |
| listener  | Callback |
| field     | Refer to: [Capability Attributes](https://docs.jftech.com/#docs-hash-2) |


- Sample Code

```
DeviceManager.getInstance().getDevAbility(devId, new DeviceManager.OnDevManagerListener() {
    /**
     * Success callback
     * @param devId         Device serial number
     * @param operationType Operation type
     */
    @Override
    public void onSuccess(String devId, int operationType, Object isSupport) {
        // isSupport -> true means support, false means not support
    }

    /**
     * Failure callback
     *
     * @param devId    Device serial number
     * @param msgId    Message ID
     * @param jsonName 
     * @param errorId  Error code
     */
    @Override
    public void onFailed(String devId, int msgId, String jsonName, int errorId) {
         // Failed to get, analyze the specific reason through errorId
    }
}, "OtherFunction", "SupportAlarmVoiceTipsType");// Whether to support custom alarm voice prompts

```
### AlarmFunction
| Attribute | Description |
| ------------------------------ | -------------------------------------------- |
| AlarmConfig                    | Alarm configuration                                      |
| BlindDetect                    | Occlusion detection                                         |
| LossDetect                     | Loss detection                                      |
| MotionDetect                   | Motion detection                                      |
| NetAbort                       | Network termination                                      |
| NetAlarm                       | Network alarm                                      |
| NetIpConflict                  | IP conflict                                       |
| StorageFailure                 | Storage failure                                      |
| StorageLowSpace                 | Insufficient storage space                                    |
| StorageNotExist                 | Hard disk does not exist                                    |
| IPCAlarm                       | IPC alarm                                       |
| NetAbortExtend                  | Extended network abnormality                                    |
| VideoAnalyze                   | Video analysis                                      |
| NewVideoAnalyze                 | New intelligent analysis                                     |
| PEAInHumanPed                  | Human detection with perimeter blending line function, only for IPC devices, NVR devices use channel capability set |
| ManuIntellAlertAlarm            | Manual alert                                       |
| IntellAlertAlarm                 | Intelligent alert, single-product device                                 |
| MotionHumanDection               | Whether to support opening motion tracking and human alarm function simultaneously                   |

### EncodeFunction
| Attribute | Description |
| ------------------------------ | -------------------------------------------- |
| DoubleStream                   | Dual stream                                      |
| SnapStream                     | Snapshot                                      |
| WaterMark                      | Watermark                                      |
| SmartH264                      | Image enhancement                                       |
| SmartH264V2                    | Image enhancement                                       |
| CustomChnDAMode                | Custom analog switch function                                   |
| MultiChannel                   | Multi-channel preview encoding                                   |

### NetServerFunction
| Attribute | Description |
| ------------------------------ | -------------------------------------------- |
| Net4GSignalLevel                | 4G dial-up internet function                                             |
| WifiRouteSignalLevel            | Support device WiFi signal strength acquisition                              |

### OtherFunction
| Attribute | Description |
| ------------------------------ | -------------------------------------------- |
| DownLoadPause                  | Recording download pause                                      |
| USBsupportRecord               | USB support recording                                      |
| SDsupportRecord                | SD support recording                                       |
| SupportOnvifClient              | Whether to support ONVIF client                               |
| SupportNetLocalSearch           | Whether to support remote search                                |
| SupportMaxPlayback             | Whether to support displaying the maximum number of playback channels                             |
| SupportNVR                      | Whether it is a professional NVR                                  |
| HideDigital                     | Channel mode shielding                                       |
| NotSupportAH                    | Horizontal sharpness                                      |
| NotSupportAV                    | Vertical sharpness                                      |
| SupportBT                       | Wide dynamic                                      |
| NotSupportTalk                   | Intercom                                       |
| AlterDigitalName                | Digital channel name modification                                  |
| SupportShowConnectStatus        | Whether to support displaying connection status such as WiFi, 3G, and active registration                             |
| SupportPlayBackExactSeek        | Whether to support precise playback positioning                               |
| TitleAndStateUpload              | Channel title and digital channel status upload capability set                              |
| SupportSetDigIP                 | Set front-end IP                                       |
| SupportShowProductType           | Display customer-defined product type in IE version information page                                  |
| SupportCamareStyle               | Support camera image style                                  |
| SupportStatusLed                | Whether to support status light control                              |
| SupportImpRecord                | Identity enablement                                      |
| XMModeSwitch                    | Mode switching enablement                                    |
| SupportSetPTZPresetAttribute     | Support setting preset points                                |
| SupportConsSensorAlarmLink       | Support intelligent link alarm                                  |
| SupportPTZTour                  | Support cruise                                      |
| SupportSetSnapFormat             | Support setting photo quality                                |
| SupportCapturePriority           | Support setting photo priority                                |
| SupportWifiSmartWakeup           | Support setting WiFi wake-up                                  |
| SupportPushLowBatteryMsg         | Support low battery reminder (doorbell)                             |
| SupportDoorLock                  | Support door lock                                      |
| SupportReserveWakeUp             | Support doorbell reservation for incoming calls                              |
| SupportNoDisturbing              | Support do not disturb function                                   |
| SupportElectronicPTZ             | Support electronic PTZ capability set                                |
| SupportAlarmVoiceTips            | Prompt sound                                       |
| SupportAlarmVoiceTipsType        | Customized voice prompt                                  |
| SupportNetWorkMode               | Support network mode switching                                |
| SupportCameraWhiteLight          | White light control                                       |