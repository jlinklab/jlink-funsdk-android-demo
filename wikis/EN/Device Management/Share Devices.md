# Share Devices
## Initialization
- API Description

```
/**
* Share initialization
*/
void init();

```

- Sample Code

```
 ShareManager.getInstance(context).init();
```
## Set Listener Events

- API Description

```
void addShareManagerListener(OnShareManagerListener onShareManagerListener);
public interface OnShareManagerListener {
    void onShareResult(ShareInfo shareInfo);
}
```

- Sample Code


```
ShareManager.getInstance(context).addShareManagerListener(new ShareManager.OnShareManagerListener() {
    @Override
    public void onShareResult(ShareInfo shareInfo) {
        
    }
});
```

## Get List of Shared Devices


- API Description

```
/**
* Get a list of users who have been shared devices by others
*/
void getOtherShareDevList();

```

- Sample Code

```
 ShareManager.getInstance(context).getOtherShareDevList();
```