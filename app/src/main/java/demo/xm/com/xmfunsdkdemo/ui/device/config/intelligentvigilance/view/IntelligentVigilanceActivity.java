package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lib.FunSDK;
import com.lib.SDKCONST;
import com.lib.sdk.bean.ChannelHumanRuleLimitBean;
import com.lib.sdk.bean.HumanDetectionBean;
import com.xm.base.code.ErrorCodeManager;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view.AlertSetActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view.AlgoRuleSettingActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.listener.IntelligentVigilanceContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.presenter.IntelligentVigilancePresenter;
import io.reactivex.annotations.Nullable;

import static com.lib.sdk.bean.HumanDetectionBean.IA_PERIMETER;
import static com.lib.sdk.bean.HumanDetectionBean.IA_TRIPWIRE;
import static com.manager.db.Define.ALERT_AREA_TYPE;
import static com.manager.db.Define.ALERT_lINE_TYPE;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

/**
 * 人形检测、智能警戒等
 * @author hws
 * @class describe
 * @time 2020/12/15 18:42
 */
public class IntelligentVigilanceActivity extends BaseConfigActivity<IntelligentVigilancePresenter> implements IntelligentVigilanceContract.IIntelligentVigilanceView {
    @BindView(R.id.lsi_human_detection_switch)
    ListSelectItem lsiSwitch;
    @BindView(R.id.lsi_human_detection_track)
    ListSelectItem lsiTrack;
    @BindView(R.id.lsi_human_detection_line)
    ListSelectItem lsiLine;
    @BindView(R.id.lsi_human_detection_area)
    ListSelectItem lsiArea;
    @BindView(R.id.lsi_human_detection_perimeter)
    ListSelectItem lsiPerimeter;
    @BindView(R.id.ll_human_detection_perimeter)
    LinearLayout llPerimeter;

    private ConstraintLayout mClRuleSetNew;
    private ListSelectItem mLsiRuleSetting;
    private ListSelectItem mTvRuleSetTitle;

    private ListSelectItem mLsiCamera1;
    private ListSelectItem mLsiCamera2;
    private ListSelectItem mLsiCamera3;
    private ArrayList<ListSelectItem> views = new ArrayList<>();

    @Override
    public IntelligentVigilancePresenter getPresenter() {
        return new IntelligentVigilancePresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human_detection);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.hunman_detect));
        titleBar.setRightBtnResource(R.mipmap.icon_save_normal,R.mipmap.icon_save_pressed);
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });

        titleBar.setRightTvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                showWaitDialog();
                presenter.saveHumanDetect();
            }
        });

        lsiLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lsiLine.setRightImage(SDKCONST.Switch.Open);
                lsiArea.setRightImage(SDKCONST.Switch.Close);
                presenter.setRuleType(IA_TRIPWIRE);

                Intent intent = new Intent(IntelligentVigilanceActivity.this, AlertSetActivity.class);
                intent.putExtra("devId",presenter.getDevId());
                intent.putExtra("HumanDetection",presenter.getHumanDetection());
                intent.putExtra("RuleType", ALERT_lINE_TYPE);
                intent.putExtra("ChannelHumanRuleLimit",presenter.getChannelHumanRuleLimitBean());
                startActivityForResult(intent, ALERT_lINE_TYPE);
            }
        });

        lsiArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lsiArea.setRightImage(SDKCONST.Switch.Open);
                lsiLine.setRightImage(SDKCONST.Switch.Close);
                presenter.setRuleType(IA_PERIMETER);
                Intent intent = new Intent(IntelligentVigilanceActivity.this, AlertSetActivity.class);
                intent.putExtra("devId",presenter.getDevId());
                intent.putExtra("HumanDetection",presenter.getHumanDetection());
                intent.putExtra("RuleType", ALERT_AREA_TYPE);
                intent.putExtra("ChannelHumanRuleLimit",presenter.getChannelHumanRuleLimitBean());
                startActivityForResult(intent, ALERT_AREA_TYPE);
            }
        });

        lsiSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lsiSwitch.setRightImage(lsiSwitch.getRightValue()
                        == SDKCONST.Switch.Open ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
                presenter.setHumanDetectEnable(lsiSwitch.getRightValue() == SDKCONST.Switch.Open);
            }
        });

        lsiTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lsiTrack.setRightImage(lsiTrack.getRightValue()
                        == SDKCONST.Switch.Open ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
                presenter.setShowTrack(lsiTrack.getRightValue() == SDKCONST.Switch.Open);
            }
        });

        lsiPerimeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lsiPerimeter.setRightImage(lsiPerimeter.getRightValue()
                        == SDKCONST.Switch.Open ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
                presenter.setRuleEnable(lsiPerimeter.getRightValue() == SDKCONST.Switch.Open);
            }
        });

        mClRuleSetNew = findViewById(R.id.cl_rule_set_new);
        mLsiCamera1 = findViewById(R.id.lsi_camera_1);
        mLsiCamera2 = findViewById(R.id.lsi_camera_2);
        mLsiCamera3 = findViewById(R.id.lsi_camera_3);
        if(isMultiChnDev(presenter.getDevId())){ // 多目码流设备
            presenter.getAllChnName();
        }else {
            // 暂时这样修改 默认第一个是枪机镜头之后的是球机镜头
            mLsiCamera1.setTitle(String.format(FunSDK.TS("TR_Live_Gun_Camera"), "01"));
            mLsiCamera2.setTitle(String.format(FunSDK.TS("TR_Live_Ball_Camera"), "01"));
            mLsiCamera3.setTitle(String.format(FunSDK.TS("TR_Live_Ball_Camera"), "02"));
        }

        views.add(mLsiCamera1);
        views.add(mLsiCamera2);
        views.add(mLsiCamera3);


        mLsiRuleSetting = findViewById(R.id.lsi_rule_setting);
        mLsiRuleSetting.setTitle(FunSDK.TS("TR_Detect_Rule_Setting"));
        mLsiRuleSetting.setOnClickListener(v -> navigateToAlgoRuleSetting());

        mTvRuleSetTitle = findViewById(R.id.tv_rule_set_title);
        mTvRuleSetTitle.setTitle(FunSDK.TS("alert_area"));

        mLsiCamera1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntelligentVigilanceActivity.this, AlertSetActivity.class);
                intent.putExtra("devId",presenter.getDevId());
                intent.putExtra("HumanDetection", presenter.getHumanDetection());
                intent.putExtra("RuleType", ALERT_AREA_TYPE);
                intent.putExtra("ChannelHumanRuleLimit", presenter.getChannelHumanRuleLimitBean());
                intent.putExtra("mWndNum", 0);
                intent.putExtra("title",mLsiCamera1.getTitle());
                startActivityForResult(intent, ALERT_AREA_TYPE);
            }
        });
        mLsiCamera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntelligentVigilanceActivity.this, AlertSetActivity.class);
                intent.putExtra("devId",presenter.getDevId());
                intent.putExtra("HumanDetection", presenter.getHumanDetection());
                intent.putExtra("RuleType", ALERT_AREA_TYPE);
                intent.putExtra("ChannelHumanRuleLimit", presenter.getChannelHumanRuleLimitBean());
                intent.putExtra("mWndNum", 1);
                intent.putExtra("title",mLsiCamera1.getTitle());
                startActivityForResult(intent, ALERT_AREA_TYPE);
            }
        });
        mLsiCamera3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntelligentVigilanceActivity.this, AlertSetActivity.class);
                intent.putExtra("devId",presenter.getDevId());
                intent.putExtra("HumanDetection", presenter.getHumanDetection());
                intent.putExtra("RuleType", ALERT_AREA_TYPE);
                intent.putExtra("ChannelHumanRuleLimit", presenter.getChannelHumanRuleLimitBean());
                intent.putExtra("mWndNum", 2);
                intent.putExtra("title",mLsiCamera2.getTitle());
                startActivityForResult(intent, ALERT_AREA_TYPE);
            }
        });
        mLsiCamera1.getImageLeft().setOnClickListener((v) -> changePedRules(mLsiCamera1, 0));
        mLsiCamera2.getImageLeft().setOnClickListener((v) -> changePedRules(mLsiCamera2, 1));
        mLsiCamera3.getImageLeft().setOnClickListener((v) -> changePedRules(mLsiCamera3, 2));
    }

    private void initData() {
        showWaitDialog();
        presenter.updateHumanDetectAbility();
    }

    @Override
    public void updateHumanRuleSupportResult(boolean isSupport) {
        if (isSupport) {
            lsiTrack.setVisibility(presenter.isTrackSupport() ? View.VISIBLE : View.GONE);
            lsiLine.setVisibility(presenter.isLineSupport() ? View.VISIBLE : View.GONE);
            lsiArea.setVisibility(presenter.isAreaSupport() ? View.VISIBLE : View.GONE);

            lsiTrack.setRightImage(presenter.isShowTrack() ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
            if (presenter.getRuleType() == IA_TRIPWIRE) {
                lsiLine.setRightImage(SDKCONST.Switch.Open);
                lsiArea.setRightImage(SDKCONST.Switch.Close);
            }else if (presenter.getRuleType() == IA_PERIMETER){
                lsiLine.setRightImage(SDKCONST.Switch.Close);
                lsiArea.setRightImage(SDKCONST.Switch.Open);
            }

            lsiPerimeter.setRightImage(presenter.isRuleEnable() ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
        }else {
            lsiTrack.setVisibility(View.GONE);
            lsiLine.setVisibility(View.GONE);
            lsiArea.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateHumanDetectResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        if (isSuccess) {
            lsiSwitch.setRightImage(presenter.isHumanDetectEnable() ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
        }else {
            showToast(getString(R.string.dev_get_human_dissupport) + ":" + ErrorCodeManager.getSDKStrErrorByNO(errorId), Toast.LENGTH_LONG);
            finish();
        }
        ChannelHumanRuleLimitBean channelHumanRuleLimitBean = presenter.getChannelHumanRuleLimitBean();
        if (channelHumanRuleLimitBean!=null && channelHumanRuleLimitBean.getMultiSensor() != null) {
            if (channelHumanRuleLimitBean.isSupportLine()) {
                //多镜头支持警戒线和警戒区域
                mLsiRuleSetting.setVisibility(View.VISIBLE);
                llPerimeter.setVisibility(View.GONE);
                mClRuleSetNew.setVisibility(View.GONE);
            } else {
                //多镜头仅支持警戒区域，不支持警戒线
                mLsiRuleSetting.setVisibility(View.GONE);
                llPerimeter.setVisibility(View.GONE);
                mClRuleSetNew.setVisibility(View.VISIBLE);
            }
            int[] areaNum = channelHumanRuleLimitBean.getMultiSensor().getAreaNum();
            int[] sensorOrder = channelHumanRuleLimitBean.getMultiSensor().getSensorOrder();
            HumanDetectionBean humanDetectionBean = presenter.getHumanDetection();
            ArrayList<HumanDetectionBean.PedRule> rules = humanDetectionBean.getPedRules();
            for (int i = 0; i < views.size() && i < areaNum.length; i++) {
                if (areaNum[i] > 0) {
                    views.get(i).setVisibility(View.VISIBLE);
                    boolean isOpen = false;
                    for (int j = 0; j < sensorOrder.length; j++) {
                        if (sensorOrder[j] == i) {
                            if (rules.size() > j && rules.get(j).isEnable()) {
                                isOpen = true;
                            }
                        }
                    }
                    views.get(i).setLeftImage(isOpen ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
                } else {
                    views.get(i).setVisibility(View.GONE);
                }
            }
        } else {
            //单镜头
            mLsiRuleSetting.setVisibility(View.GONE);
            llPerimeter.setVisibility(View.VISIBLE);
            mClRuleSetNew.setVisibility(View.GONE);
        }
    }

    @Override
    public void saveHumanDetectResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        if (isSuccess) {
            showToast(getString(R.string.set_dev_config_success), Toast.LENGTH_LONG);
        }else {
            showToast(getString(R.string.set_dev_config_failed) + ":" + ErrorCodeManager.getSDKStrErrorByNO(errorId), Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ALERT_lINE_TYPE
                    || requestCode == ALERT_AREA_TYPE ) {
                HumanDetectionBean humanDetectionBean = (HumanDetectionBean) data.getSerializableExtra("HumanDetection");
                if (humanDetectionBean != null) {
                    presenter.setHumanDetection(humanDetectionBean);
                }
                if (requestCode == ALERT_lINE_TYPE) {
                    presenter.setRuleType(IA_TRIPWIRE);
                    lsiLine.setRightImage(SDKCONST.Switch.Open);
                    lsiArea.setRightImage(SDKCONST.Switch.Close);
                }else {
                    presenter.setRuleType(IA_PERIMETER);
                    lsiLine.setRightImage(SDKCONST.Switch.Close);
                    lsiArea.setRightImage(SDKCONST.Switch.Open);
                }
                try {
                    int wndNum = data.getIntExtra("mWndNum", -1);
                    if (wndNum >= 0) {
                        int[] sensorOrder = presenter.getChannelHumanRuleLimitBean().getMultiSensor().getSensorOrder();
                        for (int i = 0; i < sensorOrder.length; i++) {
                            if (sensorOrder[i] == wndNum) {
                                humanDetectionBean.getPedRules().get(i).setEnable(true);
                                if (wndNum < views.size()) {
                                    views.get(wndNum).setLeftImage(SDKCONST.Switch.Open);
                                }
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 处理规则设置页面返回结果
            if (requestCode == 9001) { // REQ_RULE_SETTING
                String pedRuleJson = data.getStringExtra(AlgoRuleSettingActivity.EXTRA_RESULT_PED_RULE);
                if (pedRuleJson != null && !pedRuleJson.isEmpty()) {
                    try {
                        java.util.ArrayList<HumanDetectionBean.PedRule> rules =
                                (java.util.ArrayList<HumanDetectionBean.PedRule>) com.alibaba.fastjson.JSON.parseArray(pedRuleJson, HumanDetectionBean.PedRule.class);
                        if (rules != null && !rules.isEmpty()) {
                            presenter.getHumanDetection().setPedRules(rules);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 跳转规则设置页面
     */
    private void navigateToAlgoRuleSetting() {
        Intent intent = new Intent(IntelligentVigilanceActivity.this, AlgoRuleSettingActivity.class);
        intent.putExtra(AlgoRuleSettingActivity.EXTRA_DEV_ID, presenter.getDevId());
        intent.putExtra(AlgoRuleSettingActivity.EXTRA_CHANNEL, -1);

        // 确定算法类型
        int algoTypeOrdinal = 0; // HumanDetect ordinal
        intent.putExtra(AlgoRuleSettingActivity.EXTRA_ALGO_TYPE, algoTypeOrdinal);

        // 构建能力集 JSON
        ChannelHumanRuleLimitBean ruleLimitBean = presenter.getChannelHumanRuleLimitBean();
        if (ruleLimitBean != null) {
            String abilityData = "{\"SupportArea\":" + ruleLimitBean.isSupportArea()
                    + ",\"SupportLine\":" + ruleLimitBean.isSupportLine();
            if (ruleLimitBean.getMultiSensor() != null) {
                abilityData += ",\"MultiSensor\":{";
                if (ruleLimitBean.getMultiSensor().getAreaNum() != null) {
                    abilityData += "\"AreaNum\":" + com.alibaba.fastjson.JSON.toJSONString(ruleLimitBean.getMultiSensor().getAreaNum());
                }
                if (ruleLimitBean.getMultiSensor().getSensorOrder() != null) {
                    if (ruleLimitBean.getMultiSensor().getAreaNum() != null) abilityData += ",";
                    abilityData += "\"SensorOrder\":" + com.alibaba.fastjson.JSON.toJSONString(ruleLimitBean.getMultiSensor().getSensorOrder());
                }
                abilityData += "}";
            }
            abilityData += "}";
            intent.putExtra(AlgoRuleSettingActivity.EXTRA_ABILITY_DATA, abilityData);
        }

        // 传入当前 PedRule 配置
        HumanDetectionBean hdBean = presenter.getHumanDetection();
        if (hdBean != null && hdBean.getPedRules() != null) {
            String configData = com.alibaba.fastjson.JSON.toJSONString(hdBean.getPedRules());
            intent.putExtra(AlgoRuleSettingActivity.EXTRA_CONFIG_DATA, configData);
        }

        try {
            startActivityForResult(intent, 9001); // REQ_RULE_SETTING
        } catch (Exception e) {
            Toast.makeText(this, FunSDK.TS("Function_Under_Development"), Toast.LENGTH_SHORT).show();
        }
    }


    public boolean isMultiChnDev(String devId){
        return FunSDK.GetDevAbility(devId, "OtherFunction/MultiChnSplitWindows") > 0;
    }


    private void changePedRules(ListSelectItem listSelectItem, int chn) {
        if (presenter != null && presenter.getHumanDetection() != null
                && presenter.getChannelHumanRuleLimitBean() != null) {
            int[] sensorOrder = presenter.getChannelHumanRuleLimitBean().getMultiSensor().getSensorOrder();
            ArrayList<HumanDetectionBean.PedRule> rules = presenter.getHumanDetection().getPedRules();
            boolean isOpen = listSelectItem.getRightValue() == SDKCONST.Switch.Open;
            listSelectItem.setLeftImage(isOpen ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
            for (int i = 0; i < sensorOrder.length; i++) {
                if (sensorOrder[i] == chn) {
                    rules.get(i).setEnable(!isOpen);
                }
            }
        }
    }


    @Override
    public void updateCameraTitle(int chnId, String title) {
        switch (chnId) {
            case 0:
                mLsiCamera1.setTitle(title);
                break;
            case 1:
                mLsiCamera2.setTitle(title);
                break;
            case 2:
                mLsiCamera3.setTitle(title);
                break;
        }
    }


}
