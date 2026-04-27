# Get Device Disk Information

## Interface Description

```java
DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
    @Override
    public void onSuccess(String devId, int msgId, Object result) {
        // result is JSON data
    }

    @Override
    public void onFailed(String devId, int msgId, String s1, int errorId) {
        // Failed to retrieve, analyze the specific reason through errorId
    }
});

devConfigInfo.setJsonName(JsonConfig.STORAGE_INFO);
devConfigInfo.setChnId(-1);
devConfigInfo.setCmdId(1020);
devConfigManager.setDevCmd(devConfigInfo);
```
## Disk Information Field Description
| Name            | Type      | Required | Reference Value      | Remarks                       |
|-----------------|-----------|----------|:---------------------|------------------------------|
| Name            | string    | Required |                      | Name                         |
| Ret             | int       | Required | 100                  |                              |
| SessionID       | string    | Required | 0x00000196           |                              |
| StorageInfo     | object[]  |          |                      |                              |
| └─ PlysicalNo   | int       |          |                      | Physical disk number         |
| └─ PartNumber    | int       |          |                      | Number of disk partitions     |
| └─ Partition     | object    |          |                      |                              |
|    ──└─ DirverType    | int       |      | 0                    | Disk type, read/write: 0, read-only: 1, redundant: 3, snapshot: 4 |
|    ──└─ TotalSpace    | int       |      | 2048000              | Total size, in MB           |
|   ──└─ RemainSpace   | int       |      | 2048000              | Remaining size, in MB               |
|     ──└─ IsCurrent     | boolean   |      | false                | Is it the working disk               |
| ──└─ LogicSerialNo | int       |      | 0                    | Logical serial number: disk number plus partition number               |
|     ──└─ NewEndTime     | string    |      | 0000-00-00 00:00:00 | New recording file end time           |
|   ──└─ NewStartTime   | string    |      | 0000-00-00 00:00:00 | New recording file start time           |
|     ──└─ OldEndTime     | string    |      | 0000-00-00 00:00:00 | Previous recording file end time       |
|   ──└─ OldStartTime   | string    |      | 0000-00-00 00:00:00 | Previous recording file start time       |
|       ──└─ Status       | int       |      | 0                    | Disk error flag               |