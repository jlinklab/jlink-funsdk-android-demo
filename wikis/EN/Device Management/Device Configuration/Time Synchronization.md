# Time Synchronization
>Step One: Synchronize Time Zone;
Step Two: Synchronize Time.

## Time Zone Synchronization
- Interface Description

```java
/**
 * Device time and time zone synchronization
 *
 * @param devId    Device serial number
 * @param timeZone Time zone
 * @param listener Callback
 */
void syncDevTimeZone(String devId, int timeZone, DeviceManager.OnDevManagerListener listener);

```

- Parameter Description

| Parameter | Description            |
| :-------- | :--------------------- |
| devId     | Device serial number   |
| timeZone  | Time zone              |
| listener  | Callback               |

- Sample Code

```java
// Set time zone
Calendar cal = Calendar.getInstance(Locale.getDefault());
float zoneOffset = (float) cal.get(java.util.Calendar.ZONE_OFFSET);
float zone = (float) (zoneOffset / 60.0 / 60.0 / 1000.0);// Time zone, positive for the eastern time zone, negative for the western time zone
DeviceManager.getInstance().syncDevTimeZone(devSN, (int) (-zone * 60), new DeviceManager.OnDevManagerListener() {
    @Override
    public void onSuccess(String devId, int operationType, Object result) {

    }

    @Override
    public void onFailed(String devId, int msgId, String jsonName, int errorId) {

    }
});

```
## Time Synchronization
- Interface Description

```java
/**
 * Synchronize device time
 *
 * @param devId    Device serial number
 * @param time     Time format: yyyy-MM-dd HH:mm:ss
 * @param listener Callback
 */
void syncDevTime(String devId, String time, DeviceManager.OnDevManagerListener listener);

```
- Parameter Description

| Parameter | Description                 |
| :-------- | :-------------------------- |
| devId     | Device serial number        |
| time     | Time format: yyyy-MM-dd HH:mm:ss|
| listener  | Callback                     |

- Sample Code

```java

Calendar calendar = Calendar.getInstance(Locale.getDefault());
String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(c.getTime());
DeviceManager.getInstance().syncDevTime(devSN, time, new DeviceManager.OnDevManagerListener() {
    @Override
    public void onSuccess(String devId, int operationType, Object result) {

    }

    @Override
    public void onFailed(String devId, int msgId, String jsonName, int errorId) {

    }
});
```