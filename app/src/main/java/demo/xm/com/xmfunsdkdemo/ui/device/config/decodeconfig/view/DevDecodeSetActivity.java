package demo.xm.com.xmfunsdkdemo.ui.device.config.decodeconfig.view;

import static demo.xm.com.xmfunsdkdemo.ui.device.config.decodeconfig.presenter.DevDecodeSetPresenter.s_fps;
import static demo.xm.com.xmfunsdkdemo.ui.device.config.decodeconfig.presenter.DevDecodeSetPresenter.s_res_str_0;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.basic.G;
import com.lib.DevSDK;
import com.lib.FunSDK;
import com.lib.SDKCONST;
import com.lib.sdk.bean.EncodeCapabilityBean;
import com.lib.sdk.bean.SimplifyEncodeBean;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.decodeconfig.listener.DevDecodeSetContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.decodeconfig.presenter.DevDecodeSetPresenter;
import demo.xm.com.xmfunsdkdemo.ui.entity.ResolutionInfo;
import io.reactivex.annotations.Nullable;

/**
 * 编码配置界面,包括主码流及辅码流(分辨率,帧数,清晰度,音频,视频)
 * Created by jiangping on 2018-10-23.
 */
public class DevDecodeSetActivity extends BaseConfigActivity<DevDecodeSetPresenter>
        implements DevDecodeSetContract.IDevDecodeSetView, OnClickListener {

    /**
     * 分辨率:"D1", "HD1", "BCIF", "CIF", "QCIF", "VGA", "QVGA",  "SVCD", "QQVGA", "ND1", "650TVL",
     * "960P", "2_5M", "3M", "5M", "6M", "8M", "12M", "4K", "720N", “SXVGA”, "WSVGA", “UXGA”,
     * "WUXGA", “720P”, “1_3M”,“1080P”，“4M”
     */
    private Spinner spMainSolution, spSubSolution;
    /**
     * 视频帧率:负数表示多秒一帧，比如-3表示3秒一帧，取值范围-5~30.(但目前通用版本范围都是1~25，所以这里也用1~25)
     */
    private Spinner spMainFPS, spSubFPS;
    /**
     * 图像质量,只有在可变码流下才起作用:取值1-6，值越大，图像质量越好。
     */
    private Spinner spMainQuality, spSubQuality;
    /**
     * 音频使能
     */
    private ImageButton spMainVoice, spSubVoice;
    /**
     * 视频使能
     */
    private ImageButton spMainVideo, spSubVideo;

    /**
     * 图像质量,只有在可变码流下才起作用:取值1-6，值越大，图像质量越好。
     */
    private static final int[] qualValus = {1, 2, 3, 4, 5, 6};
    private static final String[] qualsName = {FunSDK.TS("Bad"), FunSDK.TS("Poor"), FunSDK.TS("General"),
            FunSDK.TS("Good"), FunSDK.TS("Better"), FunSDK.TS("Best")};
    private int minSubRes = 0;//最小辅码流分辨率

    @Override
    public DevDecodeSetPresenter getPresenter() {
        return new DevDecodeSetPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_setup_encode);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_setup_encode));
        titleBar.setLeftClick(this);
        titleBar.setRightBtnResource(R.mipmap.icon_save_normal, R.mipmap.icon_save_pressed);
        titleBar.setRightIvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                tryTosaveConfig();
            }
        });

        spMainSolution = findViewById(R.id.sp_dev_set_encode_main_resolution);
        spMainSolution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initMainFps(getIntValue(spMainSolution.getId()));
                initSubFps();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spMainFPS = findViewById(R.id.sp_dev_set_encode_main_FPS);
        spMainQuality = findViewById(R.id.sp_dev_set_encode_main_quality);
        spMainVoice = findViewById(R.id.btn_dev_set_encode_main_voice);
        spMainVoice.setOnClickListener(this);
        spMainVideo = findViewById(R.id.btn_dev_set_encode_main_video);
        spMainVideo.setOnClickListener(this);

        spSubSolution = findViewById(R.id.sp_dev_set_encode_sub_resolution);
        spSubSolution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initMainFps(getIntValue(spMainSolution.getId()));
                initSubFps();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spSubFPS = findViewById(R.id.sp_dev_set_encode_sub_FPS);
        spSubQuality = findViewById(R.id.sp_dev_set_encode_sub_definition);
        spSubVoice = findViewById(R.id.btn_dev_set_encode_sub_voice);
        spSubVoice.setOnClickListener(this);
        spSubVideo = findViewById(R.id.btn_dev_set_encode_sub_video);
        spSubVideo.setOnClickListener(this);

        ArrayAdapter<String> adapterQual = new ArrayAdapter<>(this, R.layout.right_spinner_item, qualsName);
        adapterQual.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMainQuality.setAdapter(adapterQual);
        spMainQuality.setTag(qualValus);
        spSubQuality.setAdapter(adapterQual);
        spSubQuality.setTag(qualValus);

    }

    private void initData() {
        showWaitDialog();
        initMinSubRes();
        presenter.getDevEncodeInfo();
        presenter.getDevEncodeCapability();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_dev_set_encode_main_voice:
                spMainVoice.setSelected(!spMainVoice.isSelected());
                break;
            case R.id.btn_dev_set_encode_main_video:
                spMainVideo.setSelected(!spMainVideo.isSelected());
                break;
            case R.id.btn_dev_set_encode_sub_video:
                spSubVideo.setSelected(!spSubVideo.isSelected());
                break;
            case R.id.btn_dev_set_encode_sub_voice:
                spSubVoice.setSelected(!spSubVoice.isSelected());
                break;
            default:
                break;
        }
    }

    @Override
    public void onGetEncodeConfigResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        if (!isSuccess) {
            showToast(getString(R.string.get_dev_config_failed) + ":" + errorId, Toast.LENGTH_SHORT);
            finish();
        }
    }

    @Override
    public void onGetEncodeCapability(boolean isSuccess, int errorId) {
        if (isSuccess) {
            EncodeCapabilityBean encodeCapabilityBean = presenter.getEncodeCapabilityBean();
            SimplifyEncodeBean simplifyEncodeBean = presenter.getSimplifyEncodeBean(0);
            if (encodeCapabilityBean != null && simplifyEncodeBean != null) {

                //主码流支持的分辨率和帧率由最大编码能力-辅码流最小支持的分辨率*帧率来计算出来的
                initMainResolution();
                initSubResolution();
                initMainFps(ResolutionInfo.GetIndex(simplifyEncodeBean.MainFormat.Video.Resolution));
                initSubFps();


                setValue(spMainSolution.getId(), ResolutionInfo.GetIndex(simplifyEncodeBean.MainFormat.Video.Resolution));
                setValue(spMainQuality.getId(), simplifyEncodeBean.MainFormat.Video.Quality);

                spMainVideo.setSelected(simplifyEncodeBean.MainFormat.VideoEnable);
                spMainVoice.setSelected(simplifyEncodeBean.MainFormat.AudioEnable);


                setValue(spSubSolution.getId(), ResolutionInfo.GetIndex(simplifyEncodeBean.ExtraFormat.Video.Resolution));
                setValue(spSubQuality.getId(), simplifyEncodeBean.ExtraFormat.Video.Quality);
                setValue(spSubFPS.getId(), simplifyEncodeBean.ExtraFormat.Video.FPS);
                spSubVideo.setSelected(simplifyEncodeBean.ExtraFormat.VideoEnable);
                spSubVoice.setSelected(simplifyEncodeBean.ExtraFormat.AudioEnable);
            }
        } else {
            showToast(getString(R.string.get_dev_config_failed) + ":" + errorId, Toast.LENGTH_SHORT);
        }
    }

    /**
     * 获取支持辅码流最小分辨率
     *
     * @return
     */
    private int initMinSubRes() {
        int nMinResSize = 0;

        long nMainMark = getMainResMark();//主码流支持的分辨率掩码值
        for (int j = 0; j < SDKCONST.SDK_CAPTURE_SIZE_t.SDK_CAPTURE_SIZE_EXT_V3_NR; ++j) {
            if (0 != (nMainMark & (0x1 << j))) {
                long nSubResMark = getSubResMark(j);
                for (int i = 0; i < SDKCONST.SDK_CAPTURE_SIZE_t.SDK_CAPTURE_SIZE_EXT_V3_NR; ++i) {
                    if (0 != (nSubResMark & (0x1 << i))) {
                        if (nMinResSize == 0 || nMinResSize > getResolutionSize(i)) {
                            nMinResSize = getResolutionSize(i);
                            minSubRes = i;
                        }
                    }
                }
            }
        }
        return minSubRes;
    }

    /**
     * 获取主码流支持的分辨率掩码值
     *
     * @return
     */
    private long getMainResMark() {
        try {
            EncodeCapabilityBean encodeCapabilityBean = presenter.getEncodeCapabilityBean();
            if (encodeCapabilityBean != null) {
                long nRetMark = 0;
                nRetMark = G.getLongFromHex(encodeCapabilityBean.ImageSizePerChannel[0]);
                if (nRetMark == 0) {
                    nRetMark = G.getLongFromHex(encodeCapabilityBean.EncodeInfo.get(0).ResolutionMask);
                }
                return nRetMark;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取辅码流支持的分辨率掩码值
     *
     * @param nMainRes
     * @return
     */
    private long getSubResMark(int nMainRes) {
        EncodeCapabilityBean encodeCapabilityBean = presenter.getEncodeCapabilityBean();
        if (encodeCapabilityBean != null) {
            long nRetMark = 0;
            nRetMark = G.getLongFromHex(encodeCapabilityBean.ExImageSizePerChannelEx[0][nMainRes]);
            if (nRetMark == 0) {
                nRetMark = G.getLongFromHex(encodeCapabilityBean.CombEncodeInfo.get(0).ResolutionMask);
            }
            return (int) nRetMark;
        } else {
            return 0;
        }
    }

    /**
     * 初始化主码流分辨率选择器
     */
    private void initMainResolution() {
        //主码流支持的分辨率
        long nRetMark = getMainResMark();
        int nCount = 0;
        for (int i = 0; i <= SDKCONST.SDK_CAPTURE_SIZE_t.SDK_CAPTURE_SIZE_4M; ++i) {
            if (0 != (nRetMark & (1 << i))) {
                nCount++;
            }
        }
        String mainKeys[] = new String[nCount];
        int mainSolutionValue[] = new int[nCount];
        int j = 0;
        for (int i = 0; i <= SDKCONST.SDK_CAPTURE_SIZE_t.SDK_CAPTURE_SIZE_4M; ++i) {
            if (0 != (nRetMark & (1 << i))) {
                mainKeys[j] = getResFromIndex(i);
                mainSolutionValue[j] = i;
                j++;
            }
        }

        ArrayAdapter<String> mainAdapterSolu = new ArrayAdapter<>(this, R.layout.right_spinner_item, mainKeys);
        mainAdapterSolu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spMainSolution.setAdapter(mainAdapterSolu);
        spMainSolution.setTag(mainSolutionValue);
    }

    /**
     * 初始化辅码流分辨率选择器
     */
    private void initSubResolution() {
        SimplifyEncodeBean simplifyEncodeBean = presenter.getSimplifyEncodeBean(0);
        int mainRes = ResolutionInfo.GetIndex(simplifyEncodeBean.MainFormat.Video.Resolution);
        long nRetSubMark = getSubResMark(mainRes);
        int nCount = 0;
        for (int i = 0; i <= SDKCONST.SDK_CAPTURE_SIZE_t.SDK_CAPTURE_SIZE_4M; ++i) {
            if (0 != (nRetSubMark & (1 << i))) {
                nCount++;
            }
        }
        String subKeys[] = new String[nCount];
        int subSolutionValue[] = new int[nCount];
        int j = 0;
        for (int i = 0; i <= SDKCONST.SDK_CAPTURE_SIZE_t.SDK_CAPTURE_SIZE_4M; ++i) {
            if (0 != (nRetSubMark & (1 << i))) {
                subKeys[j] = getResFromIndex(i);
                subSolutionValue[j] = i;
                j++;
            }
        }

        ArrayAdapter<String> subAdapterSolu = new ArrayAdapter<>(this, R.layout.right_spinner_item, subKeys);
        subAdapterSolu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spSubSolution.setAdapter(subAdapterSolu);
        spSubSolution.setTag(subSolutionValue);
    }

    /**
     * 初始化主码流帧率选择器
     */
    private void initMainFps(int mainRes) {
        SimplifyEncodeBean simplifyEncodeBean = presenter.getSimplifyEncodeBean(0);
        int maxFps = getMainMaxRate(mainRes);
        String[] fpsName = new String[maxFps];  //FPS属性，1~支持的最大帧率
        int[] fpsValue = new int[maxFps];
        for (int i = 1; i <= maxFps; i++) {
            fpsName[i - 1] = i + "";
            fpsValue[i - 1] = i;
        }
        ArrayAdapter<String> adapterFps = new ArrayAdapter<>(this, R.layout.right_spinner_item, fpsName);
        adapterFps.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMainFPS.setAdapter(adapterFps);
        spMainFPS.setTag(fpsValue);
        int fps = Math.min(simplifyEncodeBean.MainFormat.Video.FPS, maxFps);
        setValue(spMainFPS.getId(), fps);
    }

    /**
     * 初始化辅码流帧率选择器
     */
    private void initSubFps() {
        SimplifyEncodeBean simplifyEncodeBean = presenter.getSimplifyEncodeBean(0);
        int maxFps = 25;//辅码流最大帧率默认25帧
        String[] fpsName = new String[maxFps];  //FPS属性，1~25
        int[] fpsValue = new int[maxFps];
        for (int i = 1; i <= maxFps; i++) {
            fpsName[i - 1] = i + "";
            fpsValue[i - 1] = i;
        }
        ArrayAdapter<String> adapterFps = new ArrayAdapter<>(this, R.layout.right_spinner_item, fpsName);
        adapterFps.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubFPS.setAdapter(adapterFps);
        spSubFPS.setTag(fpsValue);
        setValue(spSubFPS.getId(), simplifyEncodeBean.ExtraFormat.Video.FPS);
    }

    /**
     * 获取主码流支持的最大帧率
     *
     * @param nMainRes
     * @return
     */
    private int getMainMaxRate(int nMainRes) {
        EncodeCapabilityBean encodeCapabilityBean = presenter.getEncodeCapabilityBean();
        SimplifyEncodeBean simplifyEncodeBean = presenter.getSimplifyEncodeBean(0);
        if (nMainRes < 0) {
            nMainRes = 0;
        }
        int nMaxEncPower = G.getIntFromHex(encodeCapabilityBean.MaxEncodePowerPerChannel[0]);
        int nSupportSubResMark = G.getIntFromHex(encodeCapabilityBean.ExImageSizePerChannelEx[0][nMainRes]);//辅码流支持的分辨率掩码
        int nSubMaxRate = 25;//最大的辅码流帧率默认25帧
        return DevSDK.GetMainMaxRateEx(nMaxEncPower, nSupportSubResMark, nMainRes, 0,nSubMaxRate);
    }

    /**
     * 获取分辨率
     *
     * @param nResIndex
     * @return
     */
    public int getResolutionSize(int nResIndex) {
        if (nResIndex >= 0 && nResIndex <= SDKCONST.SDK_CAPTURE_SIZE_t.SDK_CAPTURE_SIZE_4M) {
            return s_fps[nResIndex];
        }
        return s_fps[0];
    }

    private String getResFromIndex(int index) {
        if (index >= 0 && index <= SDKCONST.SDK_CAPTURE_SIZE_t.SDK_CAPTURE_SIZE_4M) {
            return s_res_str_0[index];
        }
        return "";
    }

    @Override
    public void onSaveResult(boolean isSuccess) {
        hideWaitDialog();
        if (isSuccess) {
            showToast(getString(R.string.set_dev_config_success), Toast.LENGTH_SHORT);
            finish();
        } else {
            showToast(getString(R.string.set_dev_config_failed), Toast.LENGTH_SHORT);
        }
    }

    /**
     * 去保存配置数据
     */
    private void tryTosaveConfig() {
        SimplifyEncodeBean simplifyEncodeBean = presenter.getSimplifyEncodeBean(0);
        if (simplifyEncodeBean != null) {
            simplifyEncodeBean.MainFormat.Video.Resolution = ResolutionInfo.GetString(getIntValue(spMainSolution.getId()));
            simplifyEncodeBean.MainFormat.Video.FPS = getIntValue(spMainFPS.getId());
            simplifyEncodeBean.MainFormat.Video.Quality = getIntValue(spMainQuality.getId());
            simplifyEncodeBean.MainFormat.VideoEnable = spMainVideo.isSelected();
            simplifyEncodeBean.MainFormat.AudioEnable = spMainVoice.isSelected();

            simplifyEncodeBean.ExtraFormat.Video.Resolution = ResolutionInfo.GetString(getIntValue(spSubSolution.getId()));
            simplifyEncodeBean.ExtraFormat.Video.FPS = getIntValue(spSubFPS.getId());
            simplifyEncodeBean.ExtraFormat.Video.Quality = getIntValue(spSubQuality.getId());
            simplifyEncodeBean.ExtraFormat.VideoEnable = spSubVideo.isSelected();
            simplifyEncodeBean.ExtraFormat.AudioEnable = spSubVoice.isSelected();

            showWaitDialog();
            presenter.setDevEncodeInfo();
        }
    }

    /**
     * 获取spinner状态
     */
    private int getIntValue(int id) {
        View v = this.findViewById(id);
        if (v == null) {
            return 0;
        }

        if (v instanceof Spinner) {
            Spinner sp = (Spinner) v;
            Object iv = v.getTag();
            if (iv instanceof int[]) {
                int[] values = (int[]) iv;
                int i = sp.getSelectedItemPosition();
                if (i >= 0 && i < values.length) {
                    return values[i];
                }
                return 0;
            }
        } else {
            System.err.println("GetIntValue:" + id);
        }
        return 0;
    }

    /**
     * 设置spinner状态
     */
    private void setValue(int id, int value) {
        View v = this.findViewById(id);
        if (v == null) {
            return;
        }
        if (v instanceof Spinner) {
            Spinner sp = (Spinner) v;
            Object iv = v.getTag();
            if (iv instanceof int[]) {
                int[] values = (int[]) iv;
                for (int i = 0; i < values.length; ++i) {
                    if (value == values[i]) {
                        sp.setSelection(i);
                        break;
                    }
                }
            }
        } else {
            System.err.println("SetIntValue:" + id);
        }
    }
}







