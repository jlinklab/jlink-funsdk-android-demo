package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lib.FunSDK;
import com.lib.SDKCONST;
import com.lib.sdk.bean.ChannelHumanRuleLimitBean;
import com.lib.sdk.bean.HumanDetectionBean;
import com.lib.sdk.bean.StringUtils;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.alarm.model.AlgoType;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.listener.AlgoRuleSettingContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.presenter.AlgoRuleSettingPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * 多算法智能报警 - 规则设置页面
 * <p>
 * 与旧版的主要区别：
 * 1. 移除"规则设置"总开关，规则通过点击直接选择（Enable=true + RuleType）
 * 2. 根据 SupportArea / SupportLine 能力集决定显示哪些选项
 * 3. 支持 MultiSensor 多目传感器：为每个镜头显示标题（枪机01/球机01）+ 对应选项
 * 4. 选择规则时设置 PedRule[pedRuleIndex].Enable=true + RuleType，无关闭入口
 * <p>
 * 单目传感器：使用布局中静态定义的 lsi_rule_area / lsi_rule_line
 * 多目传感器：动态填充 ll_multi_sensor_container，每个传感器一组卡片
 * <p>
 * 页面内每次修改（选择规则/绘图返回）都会通过页面自有的 AlgoDetectConfigManager
 * 立即保存到设备。返回时若本次会话有修改，通过
 * setResult(RESULT_OK) 返回最新 PedRule JSON，上级页面仅做本地同步
 * （syncPedRuleLocal），不再触发设备保存。
 */
public class AlgoRuleSettingActivity extends BaseConfigActivity<AlgoRuleSettingPresenter>
        implements AlgoRuleSettingContract.IAlgoRuleSettingView {

    private static final String TAG = "AlgoRuleSettingAct";

    /** Intent 参数 Key - 算法类型序号 (AlgoType.ordinal()) */
    public static final String EXTRA_ALGO_TYPE = "algoType";
    /** Intent 参数 Key - 设备序列号 */
    public static final String EXTRA_DEV_ID = "devId";
    /** Intent 参数 Key - 通道号 (IPC: -1, 多通道设备: >= 0) */
    public static final String EXTRA_CHANNEL = "channel";
    /** Intent 参数 Key - 能力集 JSON 字符串 (RuleLimitBean 对应的 JSON) */
    public static final String EXTRA_ABILITY_DATA = "abilityData";
    /** Intent 参数 Key - 配置数据 JSON 字符串 (PedRule 列表 JSON) */
    public static final String EXTRA_CONFIG_DATA = "configData";
    /** Intent 结果 Key - 返回修改后的 PedRule 列表 JSON */
    public static final String EXTRA_RESULT_PED_RULE = "resultPedRule";

    /** 规则类型: 警戒线 (对应 DrawType_PEA_Line = 0, HumanDetectionBean.IA_TRIPWIRE) */
    private static final int RULE_TYPE_LINE = 0;
    /** 规则类型: 警戒区域 (对应 DrawType_PEA_Area = 1, HumanDetectionBean.IA_PERIMETER) */
    private static final int RULE_TYPE_AREA = 1;

    
    
    // 单目传感器静态视图
    private View mLlSingleSensorCard;
    private ListSelectItem mLsiRuleArea;
    private ListSelectItem mLsiRuleLine;

    // 多目传感器动态容器
    private LinearLayout mLlMultiSensorContainer;

    private boolean mSupportArea;
    private boolean mSupportLine;
    private boolean mIsMultiSensor;

    /** 多目传感器分组列表（单目时仅一条，pedRuleIndex=0） */
    private List<SensorGroup> mSensorGroups = new ArrayList<>();

    /** 当前正在编辑的 PedRule 下标（onActivityResult 时使用，-1 表示未编辑） */
    private int mEditingPedRuleIndex = -1;

    // ==================== MVP ====================

    @Override
    public AlgoRuleSettingPresenter getPresenter() {
        return new AlgoRuleSettingPresenter(this);
    }

    // ==================== 生命周期 ====================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_algo_rule_setting);
        initView();
        initData();
        presenter.initConfig();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        mLlSingleSensorCard = findViewById(R.id.ll_single_sensor_card);
        mLsiRuleArea = findViewById(R.id.lsi_rule_area);
        mLsiRuleLine = findViewById(R.id.lsi_rule_line);
        mLlMultiSensorContainer = findViewById(R.id.ll_multi_sensor_container);

        titleBar.setLeftClick(() -> saveAndFinish());
        mLsiRuleArea.setTitle(FunSDK.TS("alert_area"));
        mLsiRuleArea.setOnClickListener(v -> selectRule(0, RULE_TYPE_AREA, -1));
        mLsiRuleLine.setTitle(FunSDK.TS("alert_line"));
        mLsiRuleLine.setOnClickListener(v -> selectRule(0, RULE_TYPE_LINE, -1));
    }

    private void initData() {
        // 解析 Intent 参数
        int algoTypeOrdinal = getIntent().getIntExtra(EXTRA_ALGO_TYPE, -1);
        String devId = getIntent().getStringExtra(EXTRA_DEV_ID);
        int channel = getIntent().getIntExtra(EXTRA_CHANNEL, -1);
        String abilityData = getIntent().getStringExtra(EXTRA_ABILITY_DATA);
        String configData = getIntent().getStringExtra(EXTRA_CONFIG_DATA);

        // 设置标题：统一显示"规则设置"，不显示具体算法名称
        AlgoType algoType = null;
        if (algoTypeOrdinal >= 0 && algoTypeOrdinal < AlgoType.values().length) {
            algoType = AlgoType.values()[algoTypeOrdinal];
        }
        presenter.setAlgoType(algoType);
        presenter.setDevId(devId);
        presenter.setChannel(channel);
        titleBar.setTitleText(FunSDK.TS("TR_Detect_Rule_Setting"));

        // 解析能力集
        mSupportArea = false;
        mSupportLine = false;

        if (!StringUtils.isStringNULL(abilityData)) {
            try {
                org.json.JSONObject abilityJson = new org.json.JSONObject(abilityData);
                mSupportArea = abilityJson.optBoolean("SupportArea", false);
                mSupportLine = abilityJson.optBoolean("SupportLine", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 小写 key 容错
        if (!mSupportArea && !mSupportLine && !StringUtils.isStringNULL(abilityData)) {
            try {
                org.json.JSONObject abilityJson = new org.json.JSONObject(abilityData);
                mSupportArea = abilityJson.optBoolean("supportArea", false);
                mSupportLine = abilityJson.optBoolean("supportLine", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 构建配置 Bean 和能力 Bean
        HumanDetectionBean hdBean = buildHumanDetectionBean(configData);
        ChannelHumanRuleLimitBean rlBean = buildRuleLimitBean(abilityData);
        presenter.setHumanDetectionBean(hdBean);
        presenter.setRuleLimitBean(rlBean);

        // 判断是否多目传感器
        mIsMultiSensor = false;
        if (rlBean != null && rlBean.getMultiSensor() != null
                && rlBean.getMultiSensor().getSensorOrder() != null
                && rlBean.getMultiSensor().getSensorOrder().length > 1) {
            mIsMultiSensor = true;
        }

        // 构建传感器分组
        buildSensorGroups();

        if (mIsMultiSensor) {
            // 多目：隐藏单目卡片，显示多目容器
            mLlSingleSensorCard.setVisibility(View.GONE);
            mLlMultiSensorContainer.setVisibility(View.VISIBLE);
            buildMultiSensorViews();
        } else {
            // 单目：显示单目卡片，根据能力集控制选项可见性
            mLlSingleSensorCard.setVisibility(View.VISIBLE);
            mLlMultiSensorContainer.setVisibility(View.GONE);
            mLsiRuleArea.setVisibility(mSupportArea ? View.VISIBLE : View.GONE);
            mLsiRuleLine.setVisibility(mSupportLine ? View.VISIBLE : View.GONE);
            mLsiRuleArea.setShowBottomLine(mSupportArea && mSupportLine);
        }

        updateSelection();
    }

    // ==================== 数据构建 ====================

    /**
     * 从配置 JSON 构建 HumanDetectionBean
     */
    // ==================== 数据构建 ====================

    private HumanDetectionBean buildHumanDetectionBean(String configData) {
        HumanDetectionBean bean = new HumanDetectionBean();
        if (configData != null && !configData.isEmpty()) {
            try {
                List<HumanDetectionBean.PedRule> rules =
                        com.alibaba.fastjson.JSON.parseArray(configData, HumanDetectionBean.PedRule.class);
                if (rules != null && !rules.isEmpty()) {
                    bean.setPedRules(new ArrayList<>(rules));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 确保至少有一条 PedRule
        if (bean.getPedRules() == null || bean.getPedRules().isEmpty()) {
            HumanDetectionBean.PedRule defaultRule = new HumanDetectionBean.PedRule();
            defaultRule.setEnable(false);
            defaultRule.setRuleType(HumanDetectionBean.IA_TRIPWIRE);
            bean.setPedRules(new ArrayList<>());
            bean.getPedRules().add(defaultRule);
        }
        return bean;
    }

    /**
     * 从能力 JSON 构建 ChannelHumanRuleLimitBean（含 MultiSensor 解析）
     */
    private ChannelHumanRuleLimitBean buildRuleLimitBean(String abilityData) {
        if (abilityData == null || abilityData.isEmpty()) return null;
        try {
            org.json.JSONObject json = new org.json.JSONObject(abilityData);
            ChannelHumanRuleLimitBean bean = new ChannelHumanRuleLimitBean();
            bean.setSupportLine(json.optBoolean("SupportLine", json.optBoolean("supportLine", false)));
            bean.setSupportArea(json.optBoolean("SupportArea", json.optBoolean("supportArea", false)));
            bean.setDwLineDirect(getCaseTolerant(json, "DwLineDirect", "dwLineDirect", "0x7"));
            bean.setDwAreaDirect(getCaseTolerant(json, "DwAreaDirect", "dwAreaDirect", "0x4"));
            bean.setDwAreaLine(getCaseTolerant(json, "DwAreaLine", "dwAreaLine", "0xFC"));
            bean.setLineNum(json.optInt("LineNum", json.optInt("lineNum", 0)));
            bean.setAreaNum(json.optInt("AreaNum", json.optInt("areaNum", 0)));

            // 解析 MultiSensor
            org.json.JSONObject multiSensorJson = json.optJSONObject("MultiSensor");
            if (multiSensorJson != null) {
                ChannelHumanRuleLimitBean.MultiSensor multiSensor = new ChannelHumanRuleLimitBean.MultiSensor();
                multiSensor.setAreaNum(jsonIntArrayToIntArray(multiSensorJson.optJSONArray("AreaNum")));
                multiSensor.setSensorOrder(jsonIntArrayToIntArray(multiSensorJson.optJSONArray("SensorOrder")));
                bean.setMultiSensor(multiSensor);
            }
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getCaseTolerant(org.json.JSONObject json, String key1, String key2, String def) {
        String v = json.optString(key1, null);
        if (v == null) v = json.optString(key2, null);
        return v != null ? v : def;
    }

    private int[] jsonIntArrayToIntArray(org.json.JSONArray array) {
        if (array == null) return null;
        int[] result = new int[array.length()];
        for (int i = 0; i < array.length(); i++) {
            result[i] = array.optInt(i, 0);
        }
        return result;
    }

    // ==================== 传感器分组构建 ====================

    /**
     * 构建传感器分组列表
     * <p>
     * 单目：一条分组，pedRuleIndex=0, sensorIndex=-1
     * 多目：根据 MultiSensor.AreaNum 和 SensorOrder 映射，跳过 areaNum<1 的传感器
     */
    private void buildSensorGroups() {
        mSensorGroups.clear();

        ChannelHumanRuleLimitBean ruleLimitBean = presenter.getRuleLimitBean();
        HumanDetectionBean hdBean = presenter.getHumanDetectionBean();

        if (!mIsMultiSensor || ruleLimitBean == null || ruleLimitBean.getMultiSensor() == null) {
            SensorGroup group = new SensorGroup();
            group.pedRuleIndex = 0;
            group.sensorIndex = -1;
            group.title = null;
            group.areaItem = mLsiRuleArea;
            group.lineItem = mLsiRuleLine;
            mSensorGroups.add(group);
            return;
        }

        ChannelHumanRuleLimitBean.MultiSensor multiSensor = ruleLimitBean.getMultiSensor();
        int[] areaNums = multiSensor.getAreaNum();
        int[] sensorOrder = multiSensor.getSensorOrder();
        // 长度不匹配容错：AreaNum 和 SensorOrder 长度应一致，不一致时取最小值防止越界
        int sensorCount;
        if (areaNums == null) {
            sensorCount = sensorOrder.length;
        } else {
            if (areaNums.length != sensorOrder.length) {
                Log.w(TAG, "buildSensorGroups: AreaNum.length(" + areaNums.length
                        + ") != SensorOrder.length(" + sensorOrder.length + "), using min length");
            }
            sensorCount = Math.min(areaNums.length, sensorOrder.length);
        }
        int pedRuleCount = hdBean != null ? hdBean.getPedRules().size() : 0;

        for (int sensorIndex = 0; sensorIndex < sensorCount; sensorIndex++) {
            if (areaNums != null && areaNums[sensorIndex] < 1) continue;

            int pedRuleIndex = -1;
            for (int i = 0; i < sensorOrder.length; i++) {
                if (sensorOrder[i] == sensorIndex) {
                    pedRuleIndex = i;
                    break;
                }
            }
            if (pedRuleIndex < 0 || pedRuleIndex >= pedRuleCount) continue;

            SensorGroup group = new SensorGroup();
            group.pedRuleIndex = pedRuleIndex;
            group.sensorIndex = sensorIndex;
            group.title = getCameraTitle(sensorIndex, sensorCount);
            mSensorGroups.add(group);
        }

        // 如果多目解析后没有有效分组，回退为单目
        if (mSensorGroups.isEmpty()) {
            mIsMultiSensor = false;
            buildSensorGroups();
        }
    }

    /**
     * 获取镜头标题（枪球回退命名）
     * <p>
     * 通用规则：最后一个传感器为球机01，其余为枪机01/02/...
     * 2 目：sensor 0 = 枪机01, sensor 1 = 球机01
     * 3 目：sensor 0 = 枪机01, sensor 1 = 枪机02, sensor 2 = 球机01
     * 4 目：sensor 0 = 枪机01, sensor 1 = 枪机02, sensor 2 = 枪机03, sensor 3 = 球机01
     */
    private String getCameraTitle(int sensorIndex, int sensorCount) {
        // 最后一个传感器为球机01，其余为枪机01/02/...
        if (sensorIndex == sensorCount - 1) {
            return String.format(FunSDK.TS("TR_Live_Ball_Camera"), "01");
        } else {
            return String.format(FunSDK.TS("TR_Live_Gun_Camera"), String.format("%02d", sensorIndex + 1));
        }
    }

    // ==================== 多目视图构建 ====================

    /**
     * 动态构建多目传感器视图
     * 为每个传感器创建一张卡片，包含标题行 + 警戒区域/警戒线选项
     */
    private void buildMultiSensorViews() {
        mLlMultiSensorContainer.removeAllViews();
        float density = getResources().getDisplayMetrics().density;

        for (SensorGroup group : mSensorGroups) {
            // 卡片容器
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.corner_white_bg_8);
            LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            int cardMargin = (int) (16 * density);
            cardLp.bottomMargin = cardMargin;
            card.setLayoutParams(cardLp);

            // 标题行
            if (group.title != null) {
                TextView titleView = new TextView(this);
                titleView.setText(group.title);
                titleView.setTextColor(getResources().getColor(R.color.color_text_normal));
                titleView.setTextSize(16);
                int padLeft = (int) (15 * density);
                int padVertical = (int) (16 * density);
                titleView.setPadding(padLeft, padVertical, 0, padVertical);
                titleView.setTypeface(null, Typeface.BOLD);
                card.addView(titleView);

                // 标题下方分割线
                View lineView = new View(this);
                LinearLayout.LayoutParams lineLp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                lineLp.leftMargin = padLeft;
                lineView.setLayoutParams(lineLp);
                lineView.setBackgroundColor(getResources().getColor(R.color.line_color));
                card.addView(lineView);
            }

            // 警戒区域选项
            if (mSupportArea) {
                ListSelectItem areaItem = (ListSelectItem) LayoutInflater.from(this)
                        .inflate(R.layout.item_algo_rule_option, card, false);
                areaItem.setTitle(FunSDK.TS("alert_area"));
                areaItem.setShowTopLine(false);
                areaItem.setShowBottomLine(mSupportLine);
                areaItem.setOnClickListener(v -> selectRule(group.pedRuleIndex, RULE_TYPE_AREA, group.sensorIndex));
                card.addView(areaItem);
                group.areaItem = areaItem;
            }

            // 警戒线选项
            if (mSupportLine) {
                ListSelectItem lineItem = (ListSelectItem) LayoutInflater.from(this)
                        .inflate(R.layout.item_algo_rule_option, card, false);
                lineItem.setTitle(FunSDK.TS("alert_line"));
                lineItem.setShowTopLine(false);
                lineItem.setShowBottomLine(false);
                lineItem.setOnClickListener(v -> selectRule(group.pedRuleIndex, RULE_TYPE_LINE, group.sensorIndex));
                card.addView(lineItem);
                group.lineItem = lineItem;
            }

            mLlMultiSensorContainer.addView(card);
        }
    }

    // ==================== 点击处理 ====================

    /**
     * 选择规则类型，设置 Enable=true + RuleType 并跳转绘图页面
     * <p>
     * - 如果已选中（Enable=true 且 RuleType 匹配），不重复设置值
     * - 设置 PedRule[pedRuleIndex].Enable=true, RuleType=ruleType
     * - 更新选中指示器
     * - 跳转 AlertSetActivity 绘图
     *
     * @param pedRuleIndex PedRule 数组下标
     * @param ruleType     RULE_TYPE_AREA 或 RULE_TYPE_LINE
     * @param sensorIndex  传感器序号（单目=-1，多目>=0，用于 mWndNum）
     * <p>
     * 绘图页标题按规则类型显示（警戒区域/警戒线），与单目行为保持一致；
     * 镜头名（枪机01/球机01）仅在本页卡片标题中展示，不再作为绘图页标题传递。
     */
    private void selectRule(int pedRuleIndex, int ruleType, int sensorIndex) {
        HumanDetectionBean humanDetectionBean = presenter.getHumanDetectionBean();
        ChannelHumanRuleLimitBean ruleLimitBean = presenter.getRuleLimitBean();

        if (humanDetectionBean == null || pedRuleIndex < 0
                || pedRuleIndex >= humanDetectionBean.getPedRules().size()) {
            Toast.makeText(this, FunSDK.TS("Data_exception"), Toast.LENGTH_SHORT).show();
            return;
        }

        HumanDetectionBean.PedRule pedRule = humanDetectionBean.getPedRules().get(pedRuleIndex);

        // 已选中时不重复设置
        boolean alreadySelected = pedRule.isEnable() && pedRule.getRuleType() == ruleType;
        if (!alreadySelected) {
            pedRule.setEnable(true);
            pedRule.setRuleType(ruleType);
            // 新选择规则：跳转绘图页前立即保存到设备（静默保存：切换类型不弹"保存成功"Toast）
            presenter.saveToDevice(false);
        }

        // 更新选中指示器
        updateSelection();

        // 记录当前编辑的 PedRule 下标
        mEditingPedRuleIndex = pedRuleIndex;

        if (ruleLimitBean == null) {
            Toast.makeText(this, FunSDK.TS("Data_exception"), Toast.LENGTH_SHORT).show();
            return;
        }

        // 跳转 AlertSetActivity
        Intent intent = new Intent(this, AlertSetActivity.class);
        intent.putExtra("devId", presenter.getDevId());
        intent.putExtra("HumanDetection", humanDetectionBean);
        intent.putExtra("ChannelHumanRuleLimit", ruleLimitBean);
        intent.putExtra("RuleType", ruleType);

        // 多目传感器：传递 mWndNum（绘图页用于选择预览画面和定位 PedRule）
        // 标题按规则类型显示（警戒区域/警戒线），与单目分支的标题行为保持一致
        if (sensorIndex >= 0) {
            intent.putExtra("mWndNum", sensorIndex);
            intent.putExtra("title", ruleType == RULE_TYPE_LINE
                    ? FunSDK.TS("alert_line") : FunSDK.TS("alert_area"));
        }

        try {
            startActivityForResult(intent, ruleType);
        } catch (Exception e) {
            Toast.makeText(this, FunSDK.TS("Function_Under_Development"), Toast.LENGTH_SHORT).show();
        }
    }

    // ==================== 结果回调 ====================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;

        HumanDetectionBean returnedBean = (HumanDetectionBean) data.getSerializableExtra("HumanDetection");
        if (returnedBean != null) {
            presenter.setHumanDetectionBean(returnedBean);
            // 确保编辑的 PedRule RuleType 与选择一致
            if (mEditingPedRuleIndex >= 0 && mEditingPedRuleIndex < returnedBean.getPedRules().size()) {
                if (requestCode == RULE_TYPE_LINE) {
                    returnedBean.getPedRules().get(mEditingPedRuleIndex)
                            .setRuleType(HumanDetectionBean.IA_TRIPWIRE);
                } else if (requestCode == RULE_TYPE_AREA) {
                    returnedBean.getPedRules().get(mEditingPedRuleIndex)
                            .setRuleType(HumanDetectionBean.IA_PERIMETER);
                }
            }
            updateSelection();
            // 绘图返回（RESULT_OK）：立即保存到设备
            presenter.saveToDevice();
        }
    }

    // ==================== 选中状态更新 ====================

    /**
     * 更新所有规则选项的选中指示器（左侧勾选图标）
     * 单目：更新静态 lsi_rule_area / lsi_rule_line
     * 多目：遍历 mSensorGroups 更新动态创建的 ListSelectItem
     */
    private void updateSelection() {
        HumanDetectionBean humanDetectionBean = presenter.getHumanDetectionBean();
        if (humanDetectionBean == null || humanDetectionBean.getPedRules().isEmpty()) {
            return;
        }

        if (mIsMultiSensor) {
            for (SensorGroup group : mSensorGroups) {
                if (group.pedRuleIndex >= humanDetectionBean.getPedRules().size()) continue;
                HumanDetectionBean.PedRule pedRule = humanDetectionBean.getPedRules().get(group.pedRuleIndex);
                boolean areaSelected = pedRule.isEnable() && pedRule.getRuleType() == RULE_TYPE_AREA;
                boolean lineSelected = pedRule.isEnable() && pedRule.getRuleType() == RULE_TYPE_LINE;
                if (group.areaItem != null) {
                    group.areaItem.setLeftImage(areaSelected
                            ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
                }
                if (group.lineItem != null) {
                    group.lineItem.setLeftImage(lineSelected
                            ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
                }
            }
        } else {
            HumanDetectionBean.PedRule pedRule = humanDetectionBean.getPedRules().get(0);
            boolean areaSelected = pedRule.isEnable() && pedRule.getRuleType() == RULE_TYPE_AREA;
            boolean lineSelected = pedRule.isEnable() && pedRule.getRuleType() == RULE_TYPE_LINE;
            mLsiRuleArea.setLeftImage(areaSelected
                    ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
            mLsiRuleLine.setLeftImage(lineSelected
                    ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
        }
    }

    // ==================== 返回结果 ====================

    /**
     * 返回上级页面：修改已在页面内实时保存到设备，此处仅回传最新序列化数据，
     * 供上级页面本地同步显示（syncPedRuleLocal），不触发设备保存
     */
    private void saveAndFinish() {
        if (!presenter.isDirty()) {
            // 本次会话未做任何修改：无需回传结果（修改均已实时保存到设备）
            finish();
            return;
        }
        HumanDetectionBean humanDetectionBean = presenter.getHumanDetectionBean();
        if (humanDetectionBean != null && !humanDetectionBean.getPedRules().isEmpty()) {
            String pedRuleJson = com.alibaba.fastjson.JSON.toJSONString(humanDetectionBean.getPedRules());
            Intent result = new Intent();
            result.putExtra(EXTRA_RESULT_PED_RULE, pedRuleJson);
            setResult(RESULT_OK, result);
        }
        finish();
    }

    // ==================== IAlgoRuleSettingView 实现 ====================

    @Override
    public void showWaitDialog() {
        super.showWaitDialog();
    }

    @Override
    public void hideWaitDialog() {
        super.hideWaitDialog();
    }

    @Override
    public void showError(String errMsg) {
        Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showSaveSuccess() {
        Toast.makeText(this, FunSDK.TS("Save_Success"), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConfigLoaded() {
        // 静默回调：不覆盖页面已展示的数据（EXTRA_CONFIG_DATA 与设备同源）
    }

    @Override
    public void onConfigSaveComplete(boolean success) {
        // 保存完成回调，无需额外处理
    }

    @Override
    public void returnResult(String pedRuleJson) {
        // 由 saveAndFinish 直接处理
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.release();
        }
    }

    // ==================== 返回键 ====================

    @Override
    public void onBackPressed() {
        saveAndFinish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSelection();
    }

    // ==================== 传感器分组模型 ====================

    /**
     * 传感器分组模型，记录每个镜头对应的 PedRule 下标、传感器序号、标题和选项视图
     */
    private static class SensorGroup {
        /** PedRule 数组下标 */
        int pedRuleIndex;
        /** 传感器序号（单目=-1，多目>=0，用于 AlertSetActivity 的 mWndNum） */
        int sensorIndex;
        /** 镜头标题（枪机01/球机01），单目为 null */
        String title;
        /** 警戒区域选项视图，不支持时为 null */
        ListSelectItem areaItem;
        /** 警戒线选项视图，不支持时为 null */
        ListSelectItem lineItem;
    }
}
