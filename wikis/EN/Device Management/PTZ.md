# PTZ

> Recommendation: The pan-tilt (PTZ) rotation speed can be set to 3 levels, and the horizontal movement step is 2 times the vertical step.
For example:
Slow: Horizontal step is 2, vertical step is 1.
Normal: Horizontal step is 4, vertical step is 2.
Fast: Horizontal step is 8, vertical step is 4.

## Interface Description

```java
/**
 * PTZ Control
 *
 * @param ptzCtrlInfoBean PTZ control parameters
 * @param listener        Callback handler
 * @return
 */
boolean devPTZControl(PtzCtrlInfoBean ptzCtrlInfoBean, DeviceManager.OnDevManagerListener listener);
```

## Sample Code

```java
/**
 * Device PTZ Control
 *
 * @param chnId       Channel number
 * @param nPTZCommand PTZ command
 * @param speed       Movement step *Default value is 4, range is 1 ~ 8. The device program will convert this step into the corresponding speed value, where 1 is the slowest speed and 8 is the fastest.
                      Recommended three levels for horizontal: 2, 4, 8, and three levels for vertical: 1, 2, 4.
 * @param bStop       Whether to stop
 */
@Override
public void devicePTZControl(int chnId, int nPTZCommand, int speed, boolean bStop) {//Cloud console, operation button callback
    if (!monitorManagers.containsKey(chnId)) {
        return;
    }

    MonitorManager mediaManager = monitorManagers.get(chnId);
    if (mediaManager != null) {
        PtzCtrlInfoBean ptzCtrlInfoBean = new PtzCtrlInfoBean();
        ptzCtrlInfoBean.setDevId(mediaManager.getDevId());
        ptzCtrlInfoBean.setPtzCommandId(nPTZCommand);
        ptzCtrlInfoBean.setStop(bStop);//Whether to stop the PTZ rotation, if false, the PTZ will keep rotating
        ptzCtrlInfoBean.setChnId(chnId);//Channel number
        ptzCtrlInfoBean.setSpeed(speed);//PTZ operation speed (step)
        manager.devPTZControl(ptzCtrlInfoBean, null);
    }
}
```