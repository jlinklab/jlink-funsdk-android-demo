package demo.xm.com.xmfunsdkdemo.ui.device.cloud.presenter;

import android.content.Context;

import com.lib.sdk.bean.SysDevAbilityInfoBean;
import com.manager.base.BaseManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.sysability.OnSysAbilityResultListener;
import com.manager.sysability.SysAbilityManager;
import com.utils.TimeUtils;

import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.device.cloud.listener.CloudStateContract;

import static com.lib.sdk.bean.SysDevAbilityInfoBean.CLOUD_STATE_EXPIRE;
import static com.lib.sdk.bean.SysDevAbilityInfoBean.CLOUD_STATE_NORMAL;
import static com.lib.sdk.bean.SysDevAbilityInfoBean.CLOUD_STATE_NOT_OPENED;
import static com.lib.sdk.bean.SysDevAbilityInfoBean.CLOUD_STATE_NOT_SUPPORT;
import static com.lib.sdk.bean.SysDevAbilityInfoBean.XMC_CSS_VID_ENABLE;
import static com.lib.sdk.bean.SysDevAbilityInfoBean.XMC_CSS_VID_EXPIRATIONTIME;
import static com.lib.sdk.bean.SysDevAbilityInfoBean.XMC_CSS_VID_NORMAL;
import static com.lib.sdk.bean.SysDevAbilityInfoBean.XMC_CSS_VID_SUPPORT;

import java.util.List;

/**
 * @author cjm
 * @class describe
 * @time 2020/7/28 11:15
 */
public class CloudStatePresenter extends XMBasePresenter implements CloudStateContract.ICloudStatePresenter {
    private SysAbilityManager sysAbilityManager;
    private CloudStateContract.ICloudStateView iCloudStateView;
    private int storageState;

    public CloudStatePresenter(CloudStateContract.ICloudStateView iCloudStateView) {
        this.iCloudStateView = iCloudStateView;
    }

    @Override
    protected BaseManager getManager() {
        return null;
    }

    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        sysAbilityManager = SysAbilityManager.getInstance();
    }

    @Override
    public void updateCloudState(Context context) {
        sysAbilityManager.getSysDevAbilityInfos(context,getDevId(), true, new OnSysAbilityResultListener<SysDevAbilityInfoBean>() {
            @Override
            public void onSupportResult(SysDevAbilityInfoBean sysDevAbilityInfoBean, boolean b) {
                dealWithStorageInfo(sysDevAbilityInfoBean);
            }
        });
    }

    @Override
    public boolean isCanTurnToCloudWeb() {
        return storageState != CLOUD_STATE_NOT_SUPPORT;
    }

    /**
     * 处理云存储信息
     *
     * @param sysDevAbilityInfoBean
     */
    private void dealWithStorageInfo(SysDevAbilityInfoBean sysDevAbilityInfoBean) {
        if (sysDevAbilityInfoBean == null) {
            return;
        }

        //处理云存储状态
        if (sysDevAbilityInfoBean.isConfigSupport(XMC_CSS_VID_SUPPORT)) {  //Handles whether it is expired and some state acquisition
            boolean isEnable = sysDevAbilityInfoBean.isConfigSupport(XMC_CSS_VID_ENABLE);
            boolean isVideoNormal = sysDevAbilityInfoBean.isConfigSupport(XMC_CSS_VID_NORMAL);
            boolean isExpire = true;
            String expirtionTime = "";
            try {
                Object result = sysDevAbilityInfoBean.getConfig(XMC_CSS_VID_EXPIRATIONTIME, false);
                long time = Long.parseLong(result.toString());
                if (time > 0) {
                    expirtionTime = TimeUtils.showNormalFormat(time * 1000);
                    if (time > System.currentTimeMillis() / 1000) {
                        isExpire = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            dealWithStorageState(isEnable, isVideoNormal, isExpire, expirtionTime);
        } else {
            storageState = CLOUD_STATE_NOT_SUPPORT;
            iCloudStateView.onUpdateCloudStateResult(storageState, "不支持");
        }
    }

    /**
     * 处理云存储状态
     *
     * @param isEnable
     * @param isVideoNormal
     * @param isExpire
     */
    private void dealWithStorageState(boolean isEnable, boolean isVideoNormal, boolean isExpire, String expirtionTime) {
        String info;
        if (isEnable) {
            if (isVideoNormal && !isExpire) {
                storageState = CLOUD_STATE_NORMAL;
                info = "正常使用中,到期时间:" + expirtionTime;
            } else {
                storageState = CLOUD_STATE_EXPIRE;
                info = "已经过期了,到期时间:" + expirtionTime;
            }
        } else {
            storageState = CLOUD_STATE_NOT_OPENED;
            info = "未开通";
        }

        iCloudStateView.onUpdateCloudStateResult(storageState, info);
    }
}
