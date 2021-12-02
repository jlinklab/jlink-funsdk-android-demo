package demo.xm.com.xmfunsdkdemo.ui.device.config.advance.presenter;

import android.os.Message;
import android.widget.TextView;

import com.basic.G;
import com.lib.EDEV_JSON_ID;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.struct.SDK_TitleDot;
import com.manager.device.DeviceManager;

import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.utils.XUtils;
import com.xm.activity.base.XMBasePresenter;

import org.w3c.dom.Text;

import java.util.ArrayList;

import demo.xm.com.xmfunsdkdemo.ui.device.config.advance.data.GPIOInfo;
import demo.xm.com.xmfunsdkdemo.ui.device.config.advance.listener.DevAdvanceContract;

/**
 * 高级配置界面,该功能主要是一些更加专业化的设置项,包括曝光时间,情景模式,电子慢快门,色彩模式等
 * Created by jiangping on 2018-10-23.
 */
public class DevAdvancePresenter extends XMBasePresenter<DeviceManager> implements
        DevAdvanceContract.IDevAdvancePresenter, IFunSDKResult {

    private DevAdvanceContract.IDevAdvanceView iDevAdvanceView;
    private DevConfigManager devConfigManager;
    private ArrayList<GPIOInfo> gpioInfoArrayList;
    private int userId;

    public DevAdvancePresenter(DevAdvanceContract.IDevAdvanceView iDevAdvanceView) {
        this.iDevAdvanceView = iDevAdvanceView;
        gpioInfoArrayList = new ArrayList<>();
        userId = FunSDK.GetId(userId, this);
    }

    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        devConfigManager = manager.getDevConfigManager(devId);
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void getGPIO() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                HandleConfigData<ArrayList<GPIOInfo>> handleConfigData = new HandleConfigData<ArrayList<GPIOInfo>>();
                boolean bRet = handleConfigData.getDataObj((String) result, GPIOInfo.class);
                if (bRet) {
                    gpioInfoArrayList = handleConfigData.getObj();
                    if (gpioInfoArrayList.size() < 2) {
                        gpioInfoArrayList.clear();
                        GPIOInfo gpioInfo = new GPIOInfo();
                        gpioInfo.setType(1);
                        gpioInfo.setStatus(0);
                        gpioInfoArrayList.add(gpioInfo);
                        gpioInfo = new GPIOInfo();
                        gpioInfo.setType(2);
                        gpioInfo.setStatus(1);
                        gpioInfoArrayList.add(gpioInfo);
                    }
                } else {
                    GPIOInfo gpioInfo = new GPIOInfo();
                    gpioInfo.setType(1);
                    gpioInfo.setStatus(0);
                    gpioInfoArrayList.add(gpioInfo);
                    gpioInfo = new GPIOInfo();
                    gpioInfo.setType(2);
                    gpioInfo.setStatus(1);
                    gpioInfoArrayList.add(gpioInfo);
                }

                if (iDevAdvanceView != null) {
                    iDevAdvanceView.onGetGPIOResult(gpioInfoArrayList);
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                GPIOInfo gpioInfo = new GPIOInfo();
                gpioInfo.setType(1);
                gpioInfo.setStatus(0);
                gpioInfoArrayList.add(gpioInfo);
                gpioInfo.setType(2);
                gpioInfo.setStatus(1);
                gpioInfoArrayList.add(gpioInfo);
                if (iDevAdvanceView != null) {
                    iDevAdvanceView.onGetGPIOResult(gpioInfoArrayList);
                }
            }
        });

        devConfigInfo.setJsonName("System.ReadyGPIOControl");
        devConfigInfo.setChnId(-1);
        devConfigManager.getDevConfig(devConfigInfo);
    }

    @Override
    public void saveGPIO() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                if (iDevAdvanceView != null) {
                    iDevAdvanceView.onSaveGPIOResult(true);
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                if (iDevAdvanceView != null) {
                    iDevAdvanceView.onSaveGPIOResult(false);
                }
            }
        });

        devConfigInfo.setJsonName("System.ReadyGPIOControl");
        String jsonData = HandleConfigData.getSendData("System.ReadyGPIOControl", "0x08", gpioInfoArrayList);
        devConfigInfo.setJsonData(jsonData);
        devConfigManager.setDevConfig(devConfigInfo);
    }

    @Override
    public void setGPIO(int position, int status) {
        if (gpioInfoArrayList == null) {
            return;
        }

        if (position >= gpioInfoArrayList.size()) {
            return;
        }

        GPIOInfo gpioInfo = gpioInfoArrayList.get(position);
        if (gpioInfo != null) {
            gpioInfo.setStatus(status);
        }
    }

    /**
     * 修改设备预览的OSD信息/Modify OSD (On-Screen Display) information for device preview
     *
     * @param devName
     * @param tvOSD   存放水印信息的TextView，该TextView需要设置成invisible/The TextView for storing watermark information, which needs to be set to invisible
     */
    @Override
    public void modifyDevNameForOSD(String devName, TextView tvOSD) {
        DeviceManager.getInstance().modifyDevName(getDevId(), devName, new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                modifyOsd(tvOSD);
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                if (iDevAdvanceView != null) {
                    iDevAdvanceView.onModifyDevNameOsd(false, errorId);
                }
            }
        });
    }

    /**
     * Modify watermark information; this method is designed to address issues with internationalized bitmap libraries
     *
     * @param tvOSD
     */
    private void modifyOsd(TextView tvOSD) {
        SDK_TitleDot titleDot = makeTitleDot(tvOSD);
        FunSDK.DevCmdGeneral(userId,
                getDevId(),
                EDEV_JSON_ID.CONFIG_CHANNELTILE_DOT_SET,
                "TitleDot",
                1024,
                8000,
                G.ObjToBytes(titleDot),
                -1,
                0);
    }

    /**
     * 创建水印配置/Create watermark configuration
     */
    private SDK_TitleDot makeTitleDot(TextView tvOSD) {
        byte[] pixels = XUtils.getPixelsToDevice(tvOSD);
        if (pixels != null) {
            SDK_TitleDot mTitleDot = new SDK_TitleDot(tvOSD.getWidth(), tvOSD.getHeight());
            G.SetValue(mTitleDot.st_3_pDotBuf, pixels);
            mTitleDot.st_0_width = (short) tvOSD.getWidth();
            mTitleDot.st_1_height = (short) tvOSD.getHeight();
            return mTitleDot;
        }

        return null;
    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        switch (message.what) {
            case EUIMSG.DEV_CMD_EN:
                if ("TitleDot".equals(msgContent.str)) {//设置水印
                    if (message.arg1 >= 0) {
                        if (iDevAdvanceView != null) {
                            iDevAdvanceView.onModifyDevNameOsd(true, 0);
                        }
                    } else {
                        if (iDevAdvanceView != null) {
                            iDevAdvanceView.onModifyDevNameOsd(false, message.arg1);
                        }
                    }
                }
                break;
        }
        return 0;
    }
}

