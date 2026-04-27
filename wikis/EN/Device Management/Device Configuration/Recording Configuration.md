# Recording Configuration
## When the Recording is Full
- Interface Description

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

devConfigInfo.setJsonName(JsonConfig.GENERAL_GENERAL);
devConfigInfo.setChnId(-1);
devConfigManager.getDevConfig(devConfigInfo);

```

- Explanation of the returned OverWrite field

| Value      | Description|
| ---------- | ----------------- |
| OverWrite  | Recording Full - Overwrite |
| StopRecord | Recording Full - Stop Recording |