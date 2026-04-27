# Live Video
#### 1. Create a player:

- Interface Description

```
MonitorManager createMonitorPlayer(ViewGroup playView, String devId);

```

- Parameter Description



|Parameter | Comment|
| :--------- | -------- |
|PlayView | Play the canvas|
|DevId | Device serial number|

- Sample code

```

DeviceManager.getInstance().createMonitorPlayer(surfaceView,devId);

```



#### 2. Start Live Preview:

- Interface Description

```

void startMonitor();

```

- Sample code

```
MonitorManager mediaManager = createMonitorPlayer(viewGroup, devId);

mediaManager.startMonitor();// Open Video
```

#### 3. Pause real-time preview:

- Interface Description



```
void pausePlay();
```



- Sample code



```
mediaManager.pausePlay();// Pause playback

```

#### 4. Restore real-time preview:

- Interface Description



```
void rePlay();
```



- Sample code



```
mediaManager.rePlay();

```



#### 5. Stop real-time preview:

- Interface Description



```
void stopPlay();

```



- Sample code



```
mediaManager.stopPlay();

```



#### 6. Destroy player:

- Interface Description



```
void destroyPlay();

```



- Sample code



```

mediaManager.destroyPlay();

```



#### 7. Switching stream:

- Interface Description



```
T setStreamType(int streamType);

```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|StreamType | Stream type|

- Sample code



```
mediaManager.setStreamType(SDKCONST.StreamType.Extra);

```

#### 8. Set the shape of the video image (ball shaped, bowl shaped):

- Interface Description

```
void setShape(int shape);

```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|Shape | Screen shape: spherical or bowl shaped|

- Sample code

```
mediaManager.setShape(XMVRShape.Shape_Ball);

```

#### 9. Set screen placement (wall, desktop):

- Interface Description

```
void setMount(int mount);

```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|Mount | Screen placement|

- Sample code

```
mediaManager.setMount(XMVRMount.Ceiling);

```



#### 10. Set to doorbell wall mode:

- Interface Description

```

void setDoorBellWallMode(boolean isDoorBellWallMode);

```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
| IsDoorBellWallMode | Whether it is in doorbell wall mode|

- Sample code

```
mediaManager.setDoorBellWallMode(true);

```




#### 11. Real time capture:

- Interface Description



```
String capture(String dir);
```

- Parameter Description



|Parameter | Comment|
| :--------- | -------- |
|Dir | Path to the folder where the pictures are saved|

- Sample code

```
String imgPath = mRecordPlayer.capture(MyApplication.PATH_PHOTO);

```

#### 12. Start real-time cutting:

- Interface Description

```
boolean startRecord(String dir);
```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|Dir | Path to the folder where the video is saved|

- Sample code

```
mediaManager.startRecord(SDKDemoApplication.PATH_VIDEO);

```

#### 13. Stop real-time cutting:

- Interface Description

```
String stopRecord();
```
- Sample code

```
mediaManager.stopRecord();
```

#### 14. Enable audio:

- Interface Description


```
void openVoiceBySound();

```

- Sample code


```
mediaManager.openVoiceBySound();

```

#### 15. Stop accompanying sound:

- Interface Description


```
void closeVoiceBySound();
```

- Sample code

```
mediaManager.closeVoiceBySound();
```

#### 16. Enable half duplex intercom:

- Interface Description

```

void startTalkByHalfDuplex(Context context);

```

- Parameter Description
|Parameter | Comment|
| :--------- | -------- |
|Context | Context|

- Sample code

```

mediaManager.startTalkByHalfDuplex(context);

```

#### 17. Turn off half duplex intercom:

- Interface Description

```
void stopTalkByHalfDuplex();
```

- Sample code

```
mediaManager.stopTalkByHalfDuplex();

```

#### 18. Enable full duplex intercom:

- Interface Description

```
void startTalkByDoubleDirection(Context context, boolean uploadTalk);

```

- Parameter Description

|Parameter | Comment|
| :--------- | -------- |
|Context | Context|
|UploadTalk | Do you want to upload intercom voice|

- Sample code

```
mediaManager.startTalkByDoubleDirection(context,true);// Enable two-way intercom

```

#### 19. Turn off full duplex intercom:

- Interface Description

```
void stopTalkByDoubleDirection();

```

- Sample code

```
mediaManager.stopTalkByDoubleDirection();

```


##### XMVRShape Video Shape

|Field Name | Description|
| ------ | ------ |
| Shape_ Ball: 0 | Ball/Hemisphere (default for 360VR)|
| Shape_ Ball_ Hat: 1 | Ball/hemisphere, hat shaped, hemisphere inverted|
| Shape_ Ball_ Bowl: 2 | Ball/Hemisphere, Bowl and Ball_ Hat opposite|
| Shape_ Cylinder: 3 | Cylinder|
| Shape_ CylinderS: 4 | Cylinder (close range, only partially visible)|
| Shape_ Rectangle: 5 | Rectangle, stretched and unfolded|
| Shape_ Rectangle_ 2R: 6 | Rectangle, two row mode|
| Shape_ Grid_ 4R: 7 | Ball/Hemisphere correction, 4 grid/screen modes, initially at 4 different angles|
| Shape_ Grid_ 1O_ 5R: 8 | The circle image in the upper left corner is displayed, and the five small windows on the right and bottom are displayed|
| Shape_ Grid_ 1L_ 2R: 9 | The correction effect of magnifying the two circles above, and the effect of one row below|
| Shape_ Grid_ 3R: 10 | 3 grid/screen|



##### XMVRMount screen placement

|Field Name | Description|
| ------ | ------ |
|Ceiling: 0 | Ceiling mode|
|Wall: 1 | Wall mode|
|WallInverted: 2 | Wall Inverted|
|Table 3 | Wall Reversal|