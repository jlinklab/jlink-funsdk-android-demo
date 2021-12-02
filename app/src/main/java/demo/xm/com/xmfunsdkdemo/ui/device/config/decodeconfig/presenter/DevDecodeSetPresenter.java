package demo.xm.com.xmfunsdkdemo.ui.device.config.decodeconfig.presenter;

import com.alibaba.fastjson.JSON;
import com.lib.sdk.bean.EncodeCapabilityBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.SimplifyEncodeBean;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;

import com.xm.activity.base.XMBasePresenter;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.ui.device.config.decodeconfig.listener.DevDecodeSetContract;

/**
 * 编码配置界面,包括主码流及辅码流(分辨率,帧数,清晰度,音频,视频)
 * Created by jiangping on 2018-10-23.
 */
public class DevDecodeSetPresenter extends XMBasePresenter<DeviceManager> implements DevDecodeSetContract.IDevDecodeSetPresenter {
    /**
     * 分辨率命名
     */
    public static final String s_res_str_0[] = {
            "D1",
            "HD1", // /< 352*576(PAL)
            // 352*480(NTSC)
            "BCIF", // /< 720*288(PAL) 720*240(NTSC)
            "CIF", // /< 352*288(PAL) 352*240(NTSC)
            "QCIF", // /< 176*144(PAL) 176*120(NTSC)
            "VGA", // /< 640*480(PAL) 640*480(NTSC)
            "QVGA", // /< 320*240(PAL) 320*240(NTSC)
            "SVCD", // /< 480*480(PAL) 480*480(NTSC)
            "QQVGA", // /< 160*128(PAL) 160*128(NTSC)
            "ND1", // /< 240*192
            "960H", // /< 926*576
            "720P", // /< 1280*720
            "1_3M", // /< 1280*960
            "UXGA ", // /< 1600*1200
            "1080P", // /< 1920*1080
            "WUXGA", // /< 1920*1200
            "2_5M", // /< 1872*1408
            "3M", // /< 2048*1536
            "5M", // 3744 * 1408
            "1080N", // 960 * 1080
            "4M",        ///< 2592*1520
            "6M",        ///< 3072×2048
            "8M",        ///< 3264×2448
            "12M",        ///< 4000*3000
            "4K",        ///< 4096 * 2160通用/3840*2160海思
            "720N",    //  640*720
            "WSVGA",    ///< 1024*576
            "NHD",      // wifi IPC 640*360
            "3M_N",     // <1024*1536
            "4M_N",    //1280*1440
            "5M_N",    //1872*1408
            "4K_N"    //2048*2160
    };
    /**
     * 分辨率大小
     */
    public final static int s_fps[] = {
            704 * 576, // D1
            704 * 288, // HD1
            352 * 576, // BCIF
            352 * 288, // CIF
            176 * 144, // QCIF
            640 * 480, // VGA
            320 * 240, // QVGA
            480 * 480, // SVCD
            160 * 128, // QQVGA
            240 * 192, // ND1
            928 * 576, // 650TVL
            1280 * 720, // 720P
            1280 * 960, // 1_3M
            1600 * 1200, // UXGA
            1920 * 1080, // 1080P
            1920 * 1200, // WUXGA
            1872 * 1408, // 2_5M
            2048 * 1536, // 3M
            2560 * 1920,// 5M //2019-06-14 应用部要求分辨率改为2560*1920
            944 * 1080, //1080N  //2019-07-18 从IE端合并
            2560 * 1440,   // 4M  //2019-07-18 从IE端合并
            3072 * 2048,   // 6M
            3264 * 2448,   // 8M
            4000 * 3000,   // 12M
            3840 * 2160,   // 4K //2019-06-14 应用部要求分辨率改为3840*2160
            640 * 720,    //  720N
            1024 * 576,    // WSVGA
            640 * 360,      // NHD
            1024 * 1536,    // 3M_N
            1280 * 1440,    //4M_N
            1280 * 1920,    //5M_N  //2019-07-18 从IE端合并
            1920 * 2160     //4K_N  //2019-07-18 从IE端合并
    };
    private DevDecodeSetContract.IDevDecodeSetView iDevDecodeSetView;
    private DevConfigManager devConfigManager;
    private List<SimplifyEncodeBean> simplifyEncodeBeans;//编码配置
    private EncodeCapabilityBean encodeCapabilityBean;//编码能力，用于判断主辅码流支持哪些分辨率
    public DevDecodeSetPresenter(DevDecodeSetContract.IDevDecodeSetView iDevDecodeSetView) {
        this.iDevDecodeSetView = iDevDecodeSetView;
    }

    @Override
    public void setDevId(String devId) {
        devConfigManager = manager.getDevConfigManager(devId);
        super.setDevId(devId);
    }


    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void getDevEncodeInfo() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<List<SimplifyEncodeBean>>() {
            @Override
            public void onSuccess(String devId, int msgId, List<SimplifyEncodeBean> result) {
                simplifyEncodeBeans = result;
                iDevDecodeSetView.onGetEncodeConfigResult(true,0);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevDecodeSetView.onGetEncodeConfigResult(false, errorId);
            }
        });

        devConfigInfo.setJsonName(JsonConfig.SIMPLIFY_ENCODE);
        devConfigInfo.setChnId(-1);
        devConfigManager.getDevConfig(devConfigInfo);
    }

    @Override
    public void setDevEncodeInfo() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                if (result instanceof String) {
                    iDevDecodeSetView.onSaveResult(true);
                } else {
                    iDevDecodeSetView.onSaveResult(true);
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevDecodeSetView.onSaveResult(false);
            }
        });
        devConfigInfo.setJsonName(JsonConfig.SIMPLIFY_ENCODE);
        devConfigInfo.setChnId(-1);

        devConfigInfo.setJsonData(JSON.toJSONString(simplifyEncodeBeans));
        devConfigManager.setDevConfig(devConfigInfo);
    }

    @Override
    public void getDevEncodeCapability() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                HandleConfigData handleConfigData = new HandleConfigData();
                if (handleConfigData.getDataObj(result, EncodeCapabilityBean.class)) {
                    encodeCapabilityBean = (EncodeCapabilityBean) handleConfigData.getObj();
                }

                iDevDecodeSetView.onGetEncodeCapability(true,0);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevDecodeSetView.onGetEncodeCapability(false, errorId);
            }
        });

        devConfigInfo.setJsonName(JsonConfig.ENCODE_CAPABILITY);
        devConfigInfo.setCmdId(1360);
        devConfigInfo.setChnId(-1);
        devConfigManager.setDevCmd(devConfigInfo);
    }

    public EncodeCapabilityBean getEncodeCapabilityBean() {
        return encodeCapabilityBean;
    }

    public SimplifyEncodeBean getSimplifyEncodeBean(int chnId) {
        if (simplifyEncodeBeans != null) {
            return simplifyEncodeBeans.get(chnId);
        }

        return null;
    }
}

