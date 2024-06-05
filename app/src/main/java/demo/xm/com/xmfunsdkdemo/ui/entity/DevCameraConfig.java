package demo.xm.com.xmfunsdkdemo.ui.entity;

import com.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import demo.xm.com.xmfunsdkdemo.ui.device.setting.IConfigBase;

/**
 * 图像配置（Camera.Param）:{"AeSensitivity":5,"ApertureMode":"0x00000000","BLCMode":"0x00000000","DayNightColor":"0x00000003","Day_nfLevel":3,"DncThr":30,"ElecLevel":50,"EsShutter":"0x00000002","ExposureParam":{"LeastTime":"0x00000100","Level":0,"MostTime":"0x00010000"},"GainParam":{"AutoGain":1,"Gain":50},"IRCUTMode":0,"InfraredSwap":0,"IrcutSwap":0,"Night_nfLevel":3,"PictureFlip":"0x00000000","PictureMirror":"0x00000000","RejectFlicker":"0x00000000","WhiteBalance":"0x00000000"}
 */
public class DevCameraConfig implements IConfigBase {

    /**
     * 后面的值即前面POST发送是数据里Name对应的；
     * 图像配置（Camera.Param）:
     */
    private String mName;
    private static final String NAME = "Name";

    /**
     * AE灵敏度:摄像机所处环境明暗发生快速变化时，摄像机是否需要调整以适应新环境，灵敏度越高切换时间越快，反之越长。当对着类似经常有车开过的场景时，可以适当降低灵敏度以避免车开过引起的画面闪烁、IR来回切换。
     */
    private int mAeSensitivity;
    private static final String AESENSITIVITY = "AeSensitivity";

    /**
     * 自动光圈:摄像机通常在大范围光照度变化的场合应用时，为保证摄像机能够正确曝光成像，就必须随时调整镜头的光圈，以保证监控画面不出现‘限幅’现象，
     * 否则可能使图像亮处失去灰度层次，或因通光量减小而使画面灰暗且出现噪点。但是摄像机位置一旦固定下来以后，手动调整光圈是非常不便的，
     * 只有使摄像机能够附带自动光圈功能（提供驱动自动光圈镜头的接口）普通摄像机此选项设置无效，才能在配接自动光圈镜头的情况下，
     * 使摄像机输出的视频图像信号自动地保持在标准状态。（限幅：是指曝光过度或亮度反差太大，是整个画面或画面局部失去层次（一片白)）。
     */
    private String mApertureMode;
    private static final String APERTUREMODE = "ApertureMode";

    /**
     * 背光补偿:能提供在非常强的背景光线前面目标的理想的曝光；即指能看到逆光的物体，但是物体背后的事物看不清楚；现在主要考虑目标在视野中间位置情况。
     * 此功能打开以后自动曝光算法会优先考虑中间部分的亮度，侧重点会放在中心物体，曝光是全局处理，提升亮度看清中心关注的目标，但周围很可能会过曝；根据侧重点选择背光补偿的开关。
     */
    private boolean mBlcMode;
    private static final String BLCMODE = "BLCMode";

    /**
     * 日夜模式:默认值："0x00000000"（星光红外）
     * 共有六个选项（ 0:星光红外  1:白光全彩  2:黑白模式  3:智能警戒  4:智能变光  5:智能红外  6:车牌模式）前三个是常规选项。
     * <p>
     * 星光红外：在星星点点的微光下，也可以呈现出彩色画面。这是因为当采用这个模式时摄像机采用更大光圈和更灵敏的传感器，比普通模式下进光量更多，感光更好。
     * 但当外界环境亮度低于日夜切换阈值时，摄像机便会自动开启红外灯，画面切换到黑白模式。
     * <p>
     * 白光全彩模式：无论外部环境的明暗程度如何变化，摄像机的监视画面始终都是彩色的   不受日夜转换值影响。
     * <p>
     * 黑白模式：无论外部环境的明暗程度如何变化，摄像机的监视画面始终都是黑白的，进行设置时如果设备带有IRCUT，IRCUT会进行同时切换  不受日夜转换值影响。
     */
    private int mDayNightColor;
    private static final String DAYNIGHTCOLOR = "DayNightColor";

    /**
     * 白天降噪等级:默认值：3
     * <p>
     * 白天对应彩色情况下，晚上对应黑白模式下；主要是3D降噪方面的限制，有些客户对噪点敏感有些对拖影敏感，值越大噪点被抑制越强，拖影相对也会变严重；
     * 3D降噪：有帧与帧之间的比较，通过对比判定出噪点。优点：很有效、明显的降低噪点，且对清晰度影响要小很多。降噪可以减少雪花点，图片更加清晰；
     * 夜晚图像很容易出现噪点，导致本来清晰的图像，因为这些“雪花点”变得模糊；降噪等级并不是越高越高，开得过高，有可能导致运动画面有拖尾现象或者画面边缘模糊、细节丢失等情况。
     * 所以还是需要根据现场环境选择最佳等级。
     */
    private int mDayLevel;
    private static final String DAY_NFLEVEL = "Day_nfLevel";

    /**
     * 日夜转换阀值:默认值：30
     */
    private int mDnc;
    private static final String DNCTHR = "DncThr";

    private int mElec;
    private static final String ELECLEVEL = "ElecLevel";

    private String mEsShut;
    private static final String ESSHUTTER = "EsShutter";

    private static final String EXPOSUREPARAM = "ExposureParam";

    /**
     * 自动曝光最小时间：默认值："0x00000100"（0.256ms）
     */
    private String mLeastTime;
    private static final String LEASTTIME = "LeastTime";

    /**
     * 曝光模式：默认值：0（自动曝光）（1~9为手动曝光档位）
     * 自动模式：摄像机的曝光时间长度在设置的最小时间和最大时间之间根据实际场景进行自动确定曝光时间，自动曝光（AE）算法，
     * 使摄像机在不同的场景下，达到同样的目标亮度（亮度效果），曝光参数由AE算法设定自动模式所有场景，摄像机出来的亮度效果都会自动调整到差不多。
     * <p>
     * 手动模式：-1/50：曝光时间为0.02s即20ms，手动可选择：1/50 1/120 1/250 1/500 1/1000 1/2000 1/4000  1/10000；
     * 使用场景： 有些特殊的使用（场景亮度固定不变，抓拍机对拖影特别敏感的）；但是固定曝光，如果放到暗的地方画面就会偏暗，放到亮的地方画面就过曝（不会随场景变化）。
     * <p>
     * 区间时间：0.256-65.536ms，此区间修改设置无效，程序算法自动调节的区间，在自动模式下才应用到。
     */
    private int mLevel;
    private static final String LEVEL = "Level";

    /**
     * 自动曝光最大时间:默认值："0x00010000"（65.536ms）
     */
    private String mMostTime;
    private static final String MOSTTIME = "MostTime";

    private JSONObject mGain;
    private static final String GAINPARAM = "GainParam";

    /**
     * IRCUT切换模式:默认值：0（是和红外灯自动切换，还是按时间段进行切换，0不确定是哪种）
     */
    private int mIRCutMode;
    private static final String IRCUTMODE = "IRCUTMode";

    /**
     * IR-CUT是否要反序:默认值：0（正常序）
     */
    private int mIRCutSwap;
    private static final String IRCUTSWAP = "IrcutSwap";

    /**
     * 夜晚降噪等级:默认值：3
     * 具体说明看白天降噪等级
     */
    private int mNightLevel;
    private static final String NIGHT_NFLEVEL = "Night_nfLevel";

    /**
     * 翻转：默认值："0x00000000"
     * 监视画面上下对调。
     */
    private boolean mPictureFlip;
    private static final String PICTUREFLIP = "PictureFlip";

    /**
     * 镜像：默认值："0x00000000"
     * 监视画面左右对调。
     */
    private boolean mPictureMirror;
    private static final String PICTUREMIRROR = "PictureMirror";

    /**
     * 日光灯反闪:默认值：
     */
    private String mRejectFlicker;
    private static final String REJECTFLICKER = "RejectFlicker";

    /**
     * 白平衡：默认值："0x00000000"
     * 白平衡是监控摄像头针对画面偏色问题而推出的一个功能。
     * 通过调整白平衡可以纠正由于环境光线引起的色彩偏差。实际环境中白色的物体在成像时，由于画面中其他环境光导致白色会偏向环境光的颜色。
     * 我们的视觉系统会自动对不同的光线作出补偿，所以无论在暖调还是冷调的光线环境下，我们看一张白纸永远还是白色的。
     * 但相机则不然，它只会直接记录呈现在它面前的色彩，这就会导致画面色彩偏暖或偏冷。
     * <p>
     * 自动：自动白平衡是指摄像机能够根据拍摄的光线条件在一定色温范围内自动地进行白平衡校正而不需要干预。
     * <p>
     * 室内：选择在室内情况下进行的白平衡调节。
     * <p>
     * 室外：选择在室外情况下进行的白平衡调节。
     */
    private String mWB;
    private static final String WHITEBALANCE = "WhiteBalance";

    private int state;

    public DevCameraConfig(String mName) {
        this.mName = mName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getmAeSensitivity() {
        return mAeSensitivity;
    }

    public void setmAeSensitivity(int mAeSensitivity) {
        this.mAeSensitivity = mAeSensitivity;
    }

    public boolean ismBlcMode() {
        return mBlcMode;
    }

    public void setmBlcMode(boolean mBlcMode) {
        this.mBlcMode = mBlcMode;
    }

    public boolean ismPictureFlip() {
        return mPictureFlip;
    }

    public void setmPictureFlip(boolean mPictureFlip) {
        this.mPictureFlip = mPictureFlip;
    }

    public boolean ismPictureMirror() {
        return mPictureMirror;
    }

    public void setmPictureMirror(boolean mPictureMirror) {
        this.mPictureMirror = mPictureMirror;
    }

    public int getmDayNightColor() {
        return mDayNightColor;
    }

    public void setmDayNightColor(int mDayNightColor) {
        this.mDayNightColor = mDayNightColor;
    }

    @Override
    public String getSendMsg() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(NAME, getKey(mName));
            JSONObject jCP = new JSONObject();
            jCP.put(AESENSITIVITY, mAeSensitivity);
            jCP.put(APERTUREMODE, mApertureMode);
            jCP.put(BLCMODE, "0x0000000" + (mBlcMode ? 1 : 0));
            jCP.put(DAYNIGHTCOLOR, "0x0000000" + mDayNightColor);
            jCP.put(DAY_NFLEVEL, mDayLevel);
            jCP.put(DNCTHR, mDnc);
            jCP.put(ELECLEVEL, mElec);
            jCP.put(ESSHUTTER, mEsShut);

            JSONObject mEP = new JSONObject();
            mEP.put(LEASTTIME, mLeastTime);
            mEP.put(LEVEL, mLevel);
            mEP.put(MOSTTIME, mMostTime);

            jCP.put(EXPOSUREPARAM, mEP);
            jCP.put(GAINPARAM, mGain);
            jCP.put(IRCUTMODE, mIRCutMode);
            jCP.put(IRCUTSWAP, mIRCutSwap);
            jCP.put(NIGHT_NFLEVEL, mNightLevel);
            jCP.put(PICTUREFLIP, "0x0000000" + (mPictureFlip ? 1 : 0));
            jCP.put(PICTUREMIRROR, "0x0000000" + (mPictureMirror ? 1 : 0));
            jCP.put(REJECTFLICKER, mRejectFlicker);
            jCP.put(WHITEBALANCE, mWB);
            jsonObj.put(getKey(mName), jCP);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObj.toString();
    }

    @Override
    public boolean onParse(String json) {
        try {
            String jsonData = "{\"" + getKey(mName) + "\":" + json.substring(1, json.length() - 1) + "}";
            LogUtils.debugInfo("zzw", "mName=" + mName +",json=" + json + ",jsonData=" + jsonData);
            JSONObject c_obj = new JSONObject(jsonData).optJSONObject(getKey(mName));
            if (c_obj != null) {
                if (c_obj.has(AESENSITIVITY)) {
                    mAeSensitivity = c_obj.getInt(AESENSITIVITY);
                }
                if (c_obj.has(APERTUREMODE)) {
                    mApertureMode = c_obj.getString(APERTUREMODE);
                }
                if (c_obj.has(BLCMODE)) {
                    mBlcMode = Integer.parseInt(c_obj.getString(BLCMODE).substring(2), 16) == 1;
                }
                if (c_obj.has(DAYNIGHTCOLOR)) {
                    mDayNightColor = Integer.parseInt(c_obj.getString(DAYNIGHTCOLOR).substring(2), 16);
                }
                if (c_obj.has(DAY_NFLEVEL)) {
                    mDayLevel = c_obj.getInt(DAY_NFLEVEL);
                }
                if (c_obj.has(DNCTHR)) {
                    mDnc = c_obj.getInt(DNCTHR);
                }
                if (c_obj.has(ELECLEVEL)) {
                    mElec = c_obj.getInt(ELECLEVEL);
                }
                if (c_obj.has(ESSHUTTER)) {
                    mEsShut = c_obj.getString(ESSHUTTER);
                }
                if (c_obj.has(EXPOSUREPARAM)) {
                    JSONObject mEP = c_obj.optJSONObject(EXPOSUREPARAM);
                    if (mEP.has(LEASTTIME)) {
                        mLeastTime = mEP.getString(LEASTTIME);
                    }
                    if (mEP.has(LEVEL)) {
                        mLevel = mEP.getInt(LEVEL);
                    }
                    if (mEP.has(MOSTTIME)) {
                        mMostTime = mEP.getString(MOSTTIME);
                    }
                }
                if (c_obj.has(GAINPARAM)) {
                    mGain = c_obj.getJSONObject(GAINPARAM);
                }
                if (c_obj.has(IRCUTMODE)) {
                    mIRCutMode = c_obj.getInt(IRCUTMODE);
                }
                if (c_obj.has(IRCUTSWAP)) {
                    mIRCutSwap = c_obj.getInt(IRCUTSWAP);
                }
                if (c_obj.has(NIGHT_NFLEVEL)) {
                    mNightLevel = c_obj.getInt(NIGHT_NFLEVEL);
                }
                if (c_obj.has(PICTUREFLIP)) {
                    mPictureFlip = Integer.parseInt(c_obj.getString(PICTUREFLIP).substring(2), 16) == 1;
                }

                if (c_obj.has(PICTUREMIRROR)) {
                    mPictureMirror = Integer.parseInt(c_obj.getString(PICTUREMIRROR).substring(2), 16) == 1;
                }

                if (c_obj.has(REJECTFLICKER)) {
                    mRejectFlicker = c_obj.getString(REJECTFLICKER);
                }

                if (c_obj.has(WHITEBALANCE)) {
                    mWB = c_obj.getString(WHITEBALANCE);
                }
                return true;
            }
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getKey(String name) {
        return name + "." + "[" +
                0 + "]";
    }
}
