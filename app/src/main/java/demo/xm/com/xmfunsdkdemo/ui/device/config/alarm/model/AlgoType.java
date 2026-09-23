package demo.xm.com.xmfunsdkdemo.ui.device.config.alarm.model;

import demo.xm.com.xmfunsdkdemo.R;

/**
 * 多算法智能报警类型枚举
 * <p>
 * 共10种算法类型，按 视频AI -> 移动/PIR -> 音频AI 顺序排列。
 * <p>
 * 视频AI(6种): HumanDetect, CarShapeDetect, NonMotorDetect, LivestockDetect, PetDetect, FireDetect
 * 移动侦测(1种): MotionDetect
 * 音频AI(2种): CryDetect, VolumeDetect
 * PIR(1种): PIRDetect
 * <p>
 * 双配置体系：
 * - cfgName: Detect.*Detection 格式，用于 DevCmdGeneral 1042/1040 或 DevGetConfigByJson/DevSetConfigByJson
 * - abilityName: *RuleLimit 格式，用于 DevCmdGeneral 1360/1362 查询规则限制能力
 */
public enum AlgoType {

    /* ==================== 视频AI算法 ==================== */

    /** 人形侦测 (视频AI) */
    HumanDetect(
            "Detect.HumanDetection",
            "HumanRuleLimit",
            "TR_Human_Detection_Security",
            R.drawable.playback_icon_human,
            true, 1042, 1040, "Sensitivity", 0, 2, 1
    ),

    /** 车形侦测 (视频AI) */
    CarShapeDetect(
            "Detect.CarShapeDetection",
            "CarRuleLimit",
            "TR_Detect_Vehicle",
            R.drawable.playback_icon_car,
            true, 1042, 1040, "Sensitivity", 0, 2, 1
    ),

    /** 非机动车侦测 (视频AI) */
    NonMotorDetect(
            "Detect.NonMotorDetection",
            "NonMotorRuleLimit",
            "TR_Detect_NonMotorVehicle",
            R.drawable.playback_icon_bicycle,
            true, 1042, 1040, "Sensitivity", 0, 2, 1
    ),

    /** 畜类侦测 (视频AI) */
    LivestockDetect(
            "Detect.LivestockDetection",
            "LivestockRuleLimit",
            "TR_Detect_Animal",
            R.drawable.playback_icon_livestock,
            true, 1042, 1040, "Sensitivity", 0, 2, 1
    ),

    /** 宠物侦测 (视频AI) */
    PetDetect(
            "Detect.PetDetection",
            "PetRuleLimit",
            "TR_Detect_Pet",
            R.drawable.playback_icon_pet,
            true, 1042, 1040, "Sensitivity", 0, 2, 1
    ),

    /** 火焰侦测 (视频AI) */
    FireDetect(
            "Detect.FireDetection",
            "FireRuleLimit",
            "TR_Detect_Fire",
            R.drawable.playback_icon_hot,
            true, 1042, 1040, "Sensitivity", 0, 2, 1
    ),

    /* ==================== 移动侦测 ==================== */

    /** 移动侦测 */
    MotionDetect(
            "Detect.MotionDetect",
            "MotionRuleLimit",
            "Video_Motion",
            R.drawable.playback_icon_move,
            true, 1042, 1040, "Sensitivity", 0, 2, 1
    ),

    /* ==================== 音频AI算法 ==================== */

    /** 哭声侦测 (音频AI) */
    CryDetect(
            "Detect.CryDetection",
            "CryRuleLimit",
            "TR_Detect_Cry",
            R.drawable.playback_icon_cry,
            true, 1042, 1040, "Sensitivity", 0, 2, 1
    ),

    /** 异响侦测 (音频AI) */
    VolumeDetect(
            "Detect.VolumeDetection",
            "VolumeRuleLimit",
            "TR_Detect_AbnormalSound",
            R.drawable.playback_icon_noise,
            false, 1042, 1040, "Sensitivity", 0, 2, 1
    ),

    /* ==================== PIR ==================== */

    /** PIR移动侦测 */
    PIRDetect(
            "Alarm.PIR",
            "PIRRuleLimit",
            "TR_PIR_Detection",
            R.drawable.playback_icon_move,
            false, null, null, "PirSensitive", 1, 5, 5
    );

    /* ==================== 字段 ==================== */

    /** 设备配置名（Detect.*Detection 格式），用于 DevCmdGeneral 1042/1040 或 DevGetConfigByJson/DevSetConfigByJson 接口 */
    private final String cfgName;

    /** 能力查询名（*RuleLimit 格式），用于 DevCmdGeneral 1360/1362 接口查询规则限制能力 */
    private final String abilityName;

    /** 显示名称的语言文件key */
    private final String displayNameKey;

    /** 图标资源ID */
    private final int iconRes;

    /** 是否为通道配置（true=需要追加.[channel]后缀，使用bypass@前缀） */
    private final boolean channelConfig;

    /** 获取命令ID（null表示使用SDK默认，即DevGetConfigByJson） */
    private final Integer getCommand;

    /** 设置命令ID（null表示使用SDK默认，即DevSetConfigByJson） */
    private final Integer setCommand;

    /** 灵敏度字段名 */
    private final String sensitivityField;

    /** 灵敏度最小值 */
    private final int sensitivityMin;

    /** 灵敏度最大值 */
    private final int sensitivityMax;

    /** 灵敏度默认值 */
    private final int sensitivityDefault;

    AlgoType(String cfgName, String abilityName, String displayNameKey, int iconRes,
             boolean channelConfig, Integer getCommand, Integer setCommand,
             String sensitivityField, int sensitivityMin, int sensitivityMax, int sensitivityDefault) {
        this.cfgName = cfgName;
        this.abilityName = abilityName;
        this.displayNameKey = displayNameKey;
        this.iconRes = iconRes;
        this.channelConfig = channelConfig;
        this.getCommand = getCommand;
        this.setCommand = setCommand;
        this.sensitivityField = sensitivityField;
        this.sensitivityMin = sensitivityMin;
        this.sensitivityMax = sensitivityMax;
        this.sensitivityDefault = sensitivityDefault;
    }

    /* ==================== Getter ==================== */

    public String getCfgName() {
        return cfgName;
    }

    public String getAbilityName() {
        return abilityName;
    }

    public String getDisplayNameKey() {
        return displayNameKey;
    }

    public int getIconRes() {
        return iconRes;
    }

    public boolean isChannelConfig() {
        return channelConfig;
    }

    public Integer getGetCommand() {
        return getCommand;
    }

    public Integer getSetCommand() {
        return setCommand;
    }

    public String getSensitivityField() {
        return sensitivityField;
    }

    public int getSensitivityMin() {
        return sensitivityMin;
    }

    public int getSensitivityMax() {
        return sensitivityMax;
    }

    public int getSensitivityDefault() {
        return sensitivityDefault;
    }

    /**
     * 是否使用 DevCmdGeneral 命令（getCommand != null）
     * PIR 返回 false，使用 DevGetConfigByJson/DevSetConfigByJson
     */
    public boolean hasDevCmdGeneral() {
        return getCommand != null;
    }

    /* ==================== 类型判断 ==================== */

    /**
     * 是否为视频AI算法
     * HumanDetect/CarShapeDetect/NonMotorDetect/LivestockDetect/PetDetect/FireDetect -> true
     */
    public boolean isVideoAI() {
        return this == HumanDetect
                || this == CarShapeDetect
                || this == NonMotorDetect
                || this == LivestockDetect
                || this == PetDetect
                || this == FireDetect;
    }

    /**
     * 是否为音频AI算法
     * CryDetect/VolumeDetect -> true
     */
    public boolean isAudioAI() {
        return this == CryDetect || this == VolumeDetect;
    }

    /**
     * 是否为移动侦测
     * MotionDetect -> true
     */
    public boolean isMotionDetect() {
        return this == MotionDetect;
    }

    /**
     * 是否为PIR移动侦测
     * PIRDetect -> true
     */
    public boolean isPIRDetect() {
        return this == PIRDetect;
    }

    /* ==================== 能力判断 ==================== */

    /**
     * 是否支持灵敏度设置
     * HumanDetect/MotionDetect/CryDetect/VolumeDetect/PIRDetect -> true
     */
    public boolean supportsSensitivity() {
        return this == HumanDetect
                || this == MotionDetect
                || this == CryDetect
                || this == VolumeDetect
                || this == PIRDetect;
    }

    /**
     * 是否支持规则设置（警戒线/警戒区域）
     * 视频AI类 -> true
     */
    public boolean supportsRuleSetting() {
        return isVideoAI();
    }

    /**
     * 是否支持轨迹跟踪显示(ShowTrack)
     * 视频AI类 -> true
     */
    public boolean supportsShowTrack() {
        return isVideoAI();
    }

    /**
     * 是否支持报警闪光灯(WarningLight)
     * 视频AI + 移动侦测 -> true
     */
    public boolean supportsWarningLight() {
        return isVideoAI() || isMotionDetect();
    }

    /**
     * 是否支持设备警铃(DeviceBell)
     * 除音频AI(CryDetect/VolumeDetect)外所有算法 -> true
     * <p>
     * 音频AI（哭声/异响侦测）不支持设备警铃，隐藏警铃相关配置项：
     * DeviceBellEnable、VoiceTimeSection、BellSoundSelect、BellDuration
     */
    public boolean supportsDeviceBell() {
        return !isAudioAI();
    }

    /**
     * 是否支持录制时长设置(RecordDuration)
     * 仅 PIRDetect -> true
     */
    public boolean supportsRecordDuration() {
        return this == PIRDetect;
    }

    /**
     * 是否支持警铃持续时间设置(BellDuration)
     * 仅 PIRDetect -> true
     */
    public boolean supportsBellDuration() {
        return this == PIRDetect;
    }

    /* ==================== 目标标记提示 ==================== */

    /** 人形-目标标记提示key（视频中出现人时，会对人做画框或画线标记） */
    private static final String TIP_KEY_SHOW_TRACES = "TR_Show_Traces_Tip";

    /** 车形-目标标记提示key（视频中出现车时，会对车做画框标记） */
    private static final String TIP_KEY_CAR_FRAME = "TR_Detect_Vehicle_Mark_Tip";

    /** 非机动车-目标标记提示key（视频中出现非机动车时，会对非机动车做画框标记） */
    private static final String TIP_KEY_NONMOTOR_FRAME = "TR_Detect_NonMotor_Mark_Tip";

    /** 畜类-目标标记提示key（视频中出现畜类时，会对畜类做画框标记） */
    private static final String TIP_KEY_ANIMAL_FRAME = "TR_Detect_Animal_Mark_Tip";

    /** 宠物-目标标记提示key（视频中出现宠物时，会对宠物做画框或画线标记） */
    private static final String TIP_KEY_PET_TRACES = "tr_settings_alarm_traces_pet_tip";

    /** 火焰-目标标记提示key（视频中出现火焰时，会对火焰做画框标记） */
    private static final String TIP_KEY_FLAME_FRAME = "TR_Detect_Fire_Mark_Tip";

    /**
     * 获取目标标记框(ShowTrack)开关的副标题提示key
     * - HumanDetect -> TR_Show_Traces_Tip
     * - CarShapeDetect -> TR_Detect_Vehicle_Mark_Tip
     * - NonMotorDetect -> TR_Detect_NonMotor_Mark_Tip
     * - LivestockDetect -> TR_Detect_Animal_Mark_Tip
     * - PetDetect -> tr_settings_alarm_traces_pet_tip
     * - FireDetect -> TR_Detect_Fire_Mark_Tip
     *
     * @return 语言文件key；非视频AI算法返回null（不显示该提示，由调用方兜底）
     */
    public String getTargetMarkTipKey() {
        switch (this) {
            case HumanDetect:
                return TIP_KEY_SHOW_TRACES;
            case CarShapeDetect:
                return TIP_KEY_CAR_FRAME;
            case NonMotorDetect:
                return TIP_KEY_NONMOTOR_FRAME;
            case LivestockDetect:
                return TIP_KEY_ANIMAL_FRAME;
            case PetDetect:
                return TIP_KEY_PET_TRACES;
            case FireDetect:
                return TIP_KEY_FLAME_FRAME;
            default:
                return null;
        }
    }

    /* ==================== 静态方法 ==================== */

    /**
     * 根据IntellAlgoList返回的Name字段获取对应的AlgoType
     *
     * @param name 算法英文名，如 "HumanDetect"
     * @return 对应的AlgoType，未匹配返回null
     */
    public static AlgoType fromName(String name) {
        if (name == null) {
            return null;
        }
        switch (name) {
            case "HumanDetect":
                return HumanDetect;
            case "CarShapeDetect":
                return CarShapeDetect;
            case "NonMotorDetect":
                return NonMotorDetect;
            case "LivestockDetect":
                return LivestockDetect;
            case "PetDetect":
                return PetDetect;
            case "FireDetect":
                return FireDetect;
            case "MotionDetect":
                return MotionDetect;
            case "CryDetect":
                return CryDetect;
            case "VolumeDetect":
                return VolumeDetect;
            case "PIRDetect":
                return PIRDetect;
            default:
                return null;
        }
    }
}
