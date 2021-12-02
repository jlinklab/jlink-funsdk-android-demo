package demo.xm.com.xmfunsdkdemo.ui.device.preview.presenter;

import static demo.xm.com.xmfunsdkdemo.base.DemoConstant.SUPPORT_SCALE_THREE_LENS;
import static demo.xm.com.xmfunsdkdemo.base.DemoConstant.SUPPORT_SCALE_TWO_LENS;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.lib.sdk.bean.SensorInfoBean;
import com.manager.db.Define;
import com.manager.device.config.ISensorManager;
import com.manager.device.config.SensorManager;
import com.manager.device.media.monitor.MonitorManager;

import java.util.ArrayList;

import demo.xm.com.xmfunsdkdemo.ui.device.preview.listener.SensorChangeContract;
import demo.xm.com.xmfunsdkdemo.utils.SPUtil;

public class SensorChangePresenter implements SensorManager.OnSensorChangeListener, SensorChangeContract.ISensorChangePresenter {
    private static final int ANIMATION_STEP_TWO = 250;
    private static final int ANIMATION_STEP_THREE = 400;

    private ISensorManager sensorManager;
    private SensorChangeContract.ISensorChangeView iSensorChangeView;
    /**
     * 当前缩放比例
     */
    private float curScale;
    /**
     * 要去改变的SensorId
     */
    private int changeSensorId = -1;
    /**
     * 当前的SensorId 从0开始
     */
    private int curSensorId;
    /**
     * Sensor个数
     */
    private int senSorCount = 0;
    /**
     * 需要切换镜头的缩放倍数 从0开始
     */
    private float needChangeScale = 3f;
    /**
     * 每个镜头间的变倍个数
     */
    private float sensorItemCount = 18f;
    /**
     * 正在缩放
     */
    private boolean isScaling;
    private byte[] scaleLock = new byte[1];
    /**
     * 正在等待的缩放进度
     */
    private int waitProgress = -1;
    private MonitorManager monitorManager;

    private float[] scaleList;
    private float targetScale;
    private int animationStep = ANIMATION_STEP_TWO;
    private int mMaxTimes;
    private int mSmallSubCount = 5;
    private boolean needSendData = true;//APP变倍是否要发送变倍数据
    private float scaleRate = 1f;//APP变倍实际与显示的倍数的比值

    public SensorChangePresenter(SensorChangeContract.ISensorChangeView iSensorChangeView) {
        this.iSensorChangeView = iSensorChangeView;
        sensorManager = SensorManager.getInstance(this);
    }

    public void release() {
        iSensorChangeView = null;
    }

    @Override
    public void onGetSensorResult(String devId, int chnId, SensorInfoBean sensorInfoBean) {
        curScale = sensorInfoBean.getScale();
        if (iSensorChangeView != null) {
            iSensorChangeView.onGetSensorResult(devId, chnId, sensorInfoBean);
        }
        mMaxTimes = sensorInfoBean.getMaxTimes();
        sensorManager.getSensorFL(devId, chnId);
    }

    @Override
    public void onSetSensorResult(String devId, boolean isSuccess) {
        if (iSensorChangeView != null) {
            iSensorChangeView.onSetSensorResult(devId, isSuccess);
        }
    }

    @Override
    public void onInitSensor(String devId, ArrayList<Float> arrayList) {
        if (arrayList != null && arrayList.size() >= 2) {
            float sensorFl1 = arrayList.get(0);
            float sensorFl2 = arrayList.get(1);
            if (mMaxTimes > 0) {
                //获取到最大变倍数，更新本地焦距列表。测试发现此处不更新没问题，有待确认
                if (arrayList.size() == 2) {
                    arrayList.add(mMaxTimes * sensorFl1);
                } else {
                    arrayList.set(2, mMaxTimes * sensorFl1);
                }
            }
            scaleList = new float[arrayList.size() - 1];
            for (int i = 0; i < scaleList.length; i++) {
                scaleList[i] = arrayList.get(i + 1) / arrayList.get(i);
            }
            if (senSorCount == 2) {
                animationStep = ANIMATION_STEP_TWO;
            } else if (senSorCount == 3) {
                animationStep = ANIMATION_STEP_THREE;
            }
            if (sensorFl1 > 0 && sensorFl2 > 0) {
                //计算出 需要切换镜头的倍数
                needChangeScale = sensorFl2 / sensorFl1 - 1;
                //计算出 当前的缩放倍数
//                curScale = Math.max(curScale,curSensorId * needChangeScale);
                if (iSensorChangeView != null) {
                    SPUtil.getInstance(iSensorChangeView.getContext())
                            .setSettingParam("CHANGE_SECOND_LENS_TIMES" + devId, (float) (Math.floor(sensorFl2 / sensorFl1 * 10) / 10f));
                    iSensorChangeView.onInitSensor(devId, arrayList);
                }
            }
        }
    }

    /**
     * 设置设备缩放比例
     *
     * @param devId     设备序列号
     * @param scale     缩放比例（浮点型）
     * @param isSuccess 是否成功
     */
    @Override
    public void onSetScaleResult(String devId, float scale, boolean isSuccess) {
        if (iSensorChangeView != null) {
            iSensorChangeView.onSetScaleResult(devId, scale, isSuccess);
        }
    }

    @Override
    public void initSensor(String devId, int streamType, int chnId) {
        sensorManager.getSensor(devId, streamType, chnId);
    }

    /**
     * @param scale    目标倍数
     * @param sensorId
     */
    private int getTargetSensor(float scale, int sensorId) {
        if (senSorCount < 2 || scaleList == null || scaleList.length < 2) {
            return 0;
        }
        float minScale = getMinScale(sensorId);
        float maxScale = getMaxScale(sensorId);

        if (scale >= minScale && scale < maxScale) {
            //不需要切换
            return sensorId;
        }
        if (scale < maxScale) {
            if (sensorId < 1) {
                sensorId = 0;
            } else {
                sensorId = getTargetSensor(scale, sensorId - 1);
            }
        } else {
            sensorId = getTargetSensor(scale, sensorId + 1);
        }
        return Math.min(sensorId, senSorCount - 1);
    }

    public float getMinScale(int sensorId) {
        float minScale = 0;
        if (sensorId == 0) {
            return 0;
        }
        for (int i = 1; i <= sensorId; i++) {
            if (i == 1) {
                minScale = scaleList[i - 1];
            } else {
                if (sensorId > scaleList.length - 1) {
                    return minScale;
                } else {
                    minScale = minScale * scaleList[i - 1];
                }
            }
        }
        return minScale - 1;
    }

    public float getMaxScale(int sensorId) {
        float maxScale = 0;
        for (int i = 0; i <= sensorId; i++) {
            if (i == 0) {
                maxScale = scaleList[i];
            } else {
                if (sensorId > scaleList.length - 1) {
                    //数据异常时，按照默认两倍处理
                    maxScale = maxScale * 2;
                } else {
                    maxScale = maxScale * scaleList[i];
                }
            }
        }
        return maxScale - 1;
    }

    @Override
    public int ctrlVideoScaleWhenUp(MonitorManager monitorManager, int progress, int chnId, int streamSync) {
        //根据拖动条的进度值算出缩放倍数
        float scale = (float) (progress * ((float) 1 / mSmallSubCount));
        targetScale = scale;

        //获取是否支持多目变倍
        boolean supportScaleMoreLens;
        if (chnId == -1) {
            supportScaleMoreLens = SPUtil.getInstance(iSensorChangeView.getContext())
                    .getSettingParam(SUPPORT_SCALE_TWO_LENS + monitorManager.getDevId(), false)
                    || SPUtil.getInstance(iSensorChangeView.getContext())
                    .getSettingParam(SUPPORT_SCALE_THREE_LENS + monitorManager.getDevId(), false);
        } else {
            supportScaleMoreLens = SPUtil.getInstance(iSensorChangeView.getContext())
                    .getSettingParam(SUPPORT_SCALE_TWO_LENS + monitorManager.getDevId() + chnId, false)
                    || SPUtil.getInstance(iSensorChangeView.getContext())
                    .getSettingParam(SUPPORT_SCALE_THREE_LENS + monitorManager.getDevId() + chnId, false);
        }
        if (supportScaleMoreLens) {
            ctrlVideoScale(monitorManager, true, scale, chnId, streamSync);
        } else {
            //锁住当前缩放，其他进来的值赋值给waitScale
            synchronized (scaleLock) {
                if (isScaling) {
                    waitProgress = progress;
                    return 0;
                }
            }
            if (scale == curScale) {
                return 0;
            }


            //是否需要切换Sensor
            int targetSensor = getTargetSensor(scale, curSensorId);
            if (targetSensor != curSensorId) {
                if (curSensorId < targetSensor) {
                    if (curScale >= getMaxScale(curSensorId)) {
                        changeSensorId = curSensorId + 1;
                        switchSensor(monitorManager.getDevId(), changeSensorId, monitorManager.getStreamType(), chnId);
                    } else {
                        changeSensorId = curSensorId;
                    }
                } else {
                    changeSensorId = curSensorId - 1;
                    switchSensor(monitorManager.getDevId(), changeSensorId, monitorManager.getStreamType(), chnId);
                }
            }
            sensorManager.setScale(monitorManager.getDevId(), targetScale, chnId);
            ValueAnimator valueAnimator;
            int duration = (int) (Math.abs(curScale - scale) * animationStep);
            valueAnimator = ValueAnimator.ofFloat(curScale, scale);
            Log.e("lmy", "ctrlVideoScaleWhenUp----1 progress:" + progress + "  duration:" + duration
                    + "-----curScale:" + curScale + "  scale:" + scale + "  changeSensorId:" + changeSensorId
                    + "  targetSensor:" + targetSensor);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    System.out.println("animation.getAnimatedValue():" + animation.getAnimatedValue());
                    ctrlVideoScale(monitorManager, false, (Float) animation.getAnimatedValue(), chnId, streamSync);
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    valueAnimator.cancel();
                    Log.e("lmy", "onAnimationEnd ");

                    synchronized (scaleLock) {
                        isScaling = false;
                    }
                    targetScale = -1;
//                        //等待的缩放比例>=0的，就需要继续处理
                    if (waitProgress >= 0) {
                        ctrlVideoScaleWhenUp(monitorManager, waitProgress, chnId, streamSync);
                        waitProgress = -1;
                    }
                }
            });
            valueAnimator.setDuration(Math.max(duration, 1000));
            valueAnimator.setStartDelay(100);
            valueAnimator.start();
            isScaling = true;
        }
        return 0;
    }

    @Override
    public int ctrlVideoScale(MonitorManager monitorManager, boolean isDevSupportMoreLens, float scale, int chnId, int streamSync) {
        if (monitorManager == null) {
            return Math.round(curScale * mSmallSubCount);
        }

        int streamType = monitorManager.getStreamType();
        String devId = monitorManager.getDevId();

        if (isDevSupportMoreLens) {
            //设备端变倍只需要传放大倍数，切换sensor设备端自己处理
            sensorManager.setScaleTimesSwitch(devId, streamType, curScale, scale, chnId, streamSync);
            curScale = scale;
        } else {
            //APP端处理缩放
            if (scale != curScale) {
                if (scale > curScale) {
                    curScale = scale;
                    //当前改变的SensorId和当前的缩放倍数所能达到的SensorId不一致，就需要切换镜头
                    if (changeSensorId != getTargetSensor(targetScale, changeSensorId) && needSendData) {
                        if (scale >= getMaxScale(changeSensorId)) {
                            changeSensorId = changeSensorId + 1;
                            switchSensor(monitorManager.getDevId(), changeSensorId, monitorManager.getStreamType(), chnId);
                        }
                    }
                } else {
                    curScale = scale;
                    //当前改变的SensorId和当前的缩放倍数所能达到的SensorId不一致，就需要切换镜头
                    if (changeSensorId != getTargetSensor(targetScale, changeSensorId) && needSendData) {
                        if (scale <= getMaxScale(changeSensorId)) {
                            changeSensorId = changeSensorId - 1;
                            switchSensor(monitorManager.getDevId(), changeSensorId, monitorManager.getStreamType(), chnId);
                        }
                    }
                }
            }
            monitorManager.setScale(curScale * scaleRate + 1f);
        }

        System.out.println("curScale:" + curScale);

        return Math.round(curScale * mSmallSubCount);
    }

    @Override
    public float ctrlVideoScaleByProgress(MonitorManager monitorManager, boolean isDevSupportMoreLens, int progress, int chnId, int streamSync) {
        float scale = (float) (progress * ((float) 1 / mSmallSubCount));
        ctrlVideoScale(monitorManager, isDevSupportMoreLens, scale, chnId, streamSync);
        return scale;
    }

    @Override
    public void switchSensor(String devId, int sensorId, int streamType, int chnId) {
        if (sensorManager != null) {
            if (sensorId < 0) {
                sensorId = curSensorId;
            }
            Log.e("lmy", "SwitchSensor sensorId:" + sensorId + "   curScale:" + curScale);
            sensorManager.switchSensor(devId, sensorId, streamType, chnId);
        }
    }

    /**
     * 设备端变倍切换主辅码流
     */
    public void ctrlVideoScale(String devId, int streamType, float progress, int chnId, int streamSync) {
        if (iSensorChangeView == null || iSensorChangeView.getContext() == null) {
            return;
        }
        float scale = (float) (progress * ((float) 1 / mSmallSubCount));
        boolean supportScaleTwoLens;
        if (chnId == -1) {
            supportScaleTwoLens = SPUtil.getInstance(iSensorChangeView.getContext())
                    .getSettingParam(SUPPORT_SCALE_TWO_LENS + devId, false)
                    || SPUtil.getInstance(iSensorChangeView.getContext())
                    .getSettingParam(SUPPORT_SCALE_THREE_LENS + devId, false);
        } else {
            supportScaleTwoLens = SPUtil.getInstance(iSensorChangeView.getContext())
                    .getSettingParam(SUPPORT_SCALE_TWO_LENS + devId + chnId, false)
                    || SPUtil.getInstance(iSensorChangeView.getContext())
                    .getSettingParam(SUPPORT_SCALE_THREE_LENS + devId + chnId, false);
        }
        if (supportScaleTwoLens) {
            Log.e("lmy", "ctrlVideoScale    setScaleTimesSwitch scale:" + scale);
            sensorManager.setScaleTimesSwitch(devId, streamType, scale, scale, chnId, streamSync);
        }
    }

    @Override
    public void setCurScale(float scale) {
        this.curScale = scale;
    }

    @Override
    public float getCurScale() {
        return curScale;
    }

    @Override
    public void setChangeSensorId(int sensorId) {
        this.changeSensorId = sensorId;
    }

    @Override
    public float getChangeSensorId() {
        return changeSensorId;
    }

    @Override
    public int getProgress() {
        return Math.round(curScale * mSmallSubCount);
    }

    public void setSmallSubCount(int count) {
        mSmallSubCount = count;
    }

    @Override
    public void setCurSensorId(int sensorId) {
        if (this.curSensorId != sensorId) {
            this.curSensorId = sensorId;

            //镜头切换后，需要对剩下的倍数进行动画处理
//            synchronized (scaleLock) {
//                isScaling = false;
//            }
//
//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                @Override
//                public void run() {
//                    if (waitProgress >= 0) {
//                        ctrlVideoScaleWhenUp(waitMonitorPlayer,waitProgress);
//                        waitProgress = -1;
//                    }
//                }
//            });
        } else {
            this.curSensorId = sensorId;
        }
    }

    @Override
    public int getCurSensorId() {
        return curSensorId;
    }

    @Override
    public void setSensorCount(int count) {
        this.senSorCount = count;
    }

    @Override
    public int getSensorCount() {
        return senSorCount;
    }

    @Override
    public float getNeedChangeSensorScale() {
        return needChangeScale;
    }

    @Override
    public void setSensorItemCount(int count) {
        this.sensorItemCount = count;
    }

    public float[] getScaleList() {
        return scaleList;
    }

    public boolean isScaling() {
        return isScaling;
    }

    public float getOffsetX(String devId, int sensorId) {
        if (sensorManager != null) {
            ArrayList<Float> offsetX = sensorManager.getOffsetX(devId);
            if (offsetX != null && offsetX.size() > sensorId) {
                return offsetX.get(sensorId);
            }
        }
        return 0;
    }

    public float getOffsetY(String devId, int sensorId) {
        if (sensorManager != null) {
            ArrayList<Float> offsetY = sensorManager.getOffsetY(devId);
            if (offsetY != null && offsetY.size() > sensorId) {
                return offsetY.get(sensorId);
            }
        }
        return 0;
    }

    public void setNeedSendData(boolean needSendData) {
        this.needSendData = needSendData;
    }

    public void setScaleRate(float scaleRate) {
        this.scaleRate = scaleRate;
    }
}
