package demo.xm.com.xmfunsdkdemo.ui.entity;

import com.google.gson.Gson;
import com.lib.JSONCONFIG;
import com.lib.sdk.bean.JsonConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import demo.xm.com.xmfunsdkdemo.ui.device.setting.IConfigBase;

/**
 * 录像配置（Record  ExRecord）:{"Mask":[["0x00000006","0x00000000","0x00000000","0x00000000","0x00000000","0x00000000"],["0x00000006","0x00000000","0x00000000","0x00000000","0x00000000","0x00000000"],["0x00000006","0x00000000","0x00000000","0x00000000","0x00000000","0x00000000"],["0x00000006","0x00000000","0x00000000","0x00000000","0x00000000","0x00000000"],["0x00000006","0x00000000","0x00000000","0x00000000","0x00000000","0x00000000"],["0x00000006","0x00000000","0x00000000","0x00000000","0x00000000","0x00000000"],["0x00000006","0x00000000","0x00000000","0x00000000","0x00000000","0x00000000"]],"PacketLength":5,"PreRecord":5,"RecordMode":"ConfigRecord","Redundancy":false,"TimeSection":[["1 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00"],["1 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00"],["1 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00"],["1 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00"],["1 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00"],["1 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00"],["1 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00","0 00:00:00-24:00:00"]]}
 */
public class DevRecordConfig implements IConfigBase {
    /**
     * 后面的值即前面POST发送是数据里Name对应的；
     * 录像配置之主码流（Record）：是一个数组，每个录像通道对应一个配置
     * 录像配置之辅码流（ExRecord）：同上
     */
    private final String mName;
    private static final String NAME = "Name";
    /**
     * 录像类型
     * 一维数组总共有7组表示星期，从星期天开始
     * 二维数组总共有6组
     * 0x06 表示报警录像
     * 0x07 表示报警录像+普通录像
     */
    private String[][] mMask;
    private static final String MASK = "Mask";

    /**
     * 录像打包时间（秒），取值范围为[1, 120]
     */
    private int mPacketLength;
    private static final String PACKETLENGTH = "PacketLength";

    /**
     * 预录时间（分钟），0为关闭，取值范围为[0, 30]
     */
    private int mPreRecord;
    private static final String PRERECORD = "PreRecord";

    /**
     * 录像模式：关闭录像（ClosedRecord），手动录像（ManualRecord），配置录像（ConfigRecord）
     */
    private String mRecordMode;
    /**
     * 关闭录像（ClosedRecord）
     */
    public static final String RECORD_MODE_CLOSED_RECORD = "ClosedRecord";
    /**
     * 手动录像（ManualRecord）
     */
    public static final String RECORD_MODE_MANUAL_RECORD = "ManualRecord";
    /**
     * 配置录像（ConfigRecord）
     */
    public static final String RECORD_MODE_CONFIG_RECORD = "ConfigRecord";
    private static final String RECORDMODE = "RecordMode";

    /**
     * 冗余开关
     */
    private boolean mRedundancy;
    private static final String REDUNDANCY = "Redundancy";
    /**
     * 录像工作表（启动录像的时间段）
     * 一维数组总共有7组表示星期,从星期天开始
     * 二维数组总共有6组
     */
    private String[][] mTimeSection;
    private static final String TIMESECTION = "TimeSection";

    /**
     * 状态：0:保存失败，1：保存成功，-1：不可见状态,2: 可见，3：还未加载
     */
    private int state;

    public DevRecordConfig(String mName) {
        this.mName = mName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getmPacketLength() {
        return mPacketLength;
    }

    public void setmPacketLength(int mPacketLength) {
        this.mPacketLength = mPacketLength;
    }

    public int getmPreRecord() {
        return mPreRecord;
    }

    public void setmPreRecord(int mPreRecord) {
        this.mPreRecord = mPreRecord;
    }

    public String getRecordMode() {
        return mRecordMode;
    }

    /**
     * 获取当前录制模式在选项中的位置数值
     */
    public int getRecordModeLevel() {
        switch (mRecordMode) {
            case RECORD_MODE_CLOSED_RECORD:
                return 0;
            case RECORD_MODE_MANUAL_RECORD:
                return 1;
            case RECORD_MODE_CONFIG_RECORD:
                return 2;
            default:
                return -1;
        }
    }

    /**
     * 将选项位置数值转换为录制模式字符串设置
     */
    public void setmRecordMode(int level) {
        if (level == 0) {
            this.mRecordMode = RECORD_MODE_CLOSED_RECORD;
        } else if (level == 1) {
            this.mRecordMode = RECORD_MODE_MANUAL_RECORD;
        } else if (level == 2) {
            this.mRecordMode = RECORD_MODE_CONFIG_RECORD;
        }
    }

    /**
     * 根据变量生成json格式数据
     *
     */
    @Override
    public String getSendMsg() {
        JSONObject origin_obj = new JSONObject();
        try {
            origin_obj.put(NAME, getKey(mName));
            JSONObject c_obj = new JSONObject();
            c_obj.put(MASK, getJSONArrayFromStringDyadicArray(mMask));
            c_obj.put(PACKETLENGTH, mPacketLength);
            c_obj.put(PRERECORD, mPreRecord);
            c_obj.put(RECORDMODE, mRecordMode);
            c_obj.put(REDUNDANCY, mRedundancy);
            c_obj.put(TIMESECTION, getJSONArrayFromStringDyadicArray(mTimeSection));
            origin_obj.put(getKey(mName), c_obj);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return origin_obj.toString();
    }

    /**
     * 处理传过来的json数据
     *
     */
    @Override
    public boolean onParse(String json) {
        try {
            JSONObject origin_obj = null;
            if (JsonConfig.RECORD.equals(mName)){
                origin_obj = new JSONArray(json).getJSONObject(0);
            }else {
                origin_obj = new JSONObject(json).optJSONArray(mName).getJSONObject(0);
            }
            if (origin_obj != null) {
                if (origin_obj.has(MASK)) {
                    mMask = getStringDyadicArrayFromJSONArray(origin_obj.getJSONArray(MASK));
                }
                if (origin_obj.has(PACKETLENGTH)) {
                    mPacketLength = origin_obj.getInt(PACKETLENGTH);
                }
                if (origin_obj.has(PRERECORD)) {
                    mPreRecord = origin_obj.getInt(PRERECORD);
                }
                if (origin_obj.has(RECORDMODE)) {
                    mRecordMode = origin_obj.getString(RECORDMODE);
                }
                if (origin_obj.has(REDUNDANCY)) {
                    mRedundancy = origin_obj.getBoolean(REDUNDANCY);
                }
                if (origin_obj.has(TIMESECTION)) {
                    mTimeSection = getStringDyadicArrayFromJSONArray(origin_obj.getJSONArray(TIMESECTION));
                }
                return true;
            } else {
                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getKey(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(".").append("[")
                .append(0).append("]");
        return sb.toString();
    }

    /**
     * 将json数组数据装换成String类型二维数组
     *
     */
    public String[][] getStringDyadicArrayFromJSONArray(JSONArray jsonArray){
        String[][] stringDyadicArray = null;
        try {
            if(jsonArray!=null && jsonArray.length()>0){
                stringDyadicArray = new String[jsonArray.length()][];
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONArray strJsonArray = jsonArray.optJSONArray(i);
                    if(strJsonArray!=null && strJsonArray.length()>0){
                        String[] strArray = new String[strJsonArray.length()];
                        for (int j = 0; j < strJsonArray.length(); ++j) {
                            strArray[j] = strJsonArray.optString(j);
                        }
                        stringDyadicArray[i] = strArray;
                    } else {
                        stringDyadicArray[i] = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return stringDyadicArray;
    }


    /**
     * 将String类型二维数组数据装换成json数组
     *
     */
    public JSONArray getJSONArrayFromStringDyadicArray(String[][] stringDyadicArray){
        JSONArray jsonArray = null;
        try {
            if(stringDyadicArray!=null && stringDyadicArray.length>0){
                jsonArray = new JSONArray();
                for (int i = 0; i < stringDyadicArray.length; ++i) {
                    String[] stringArray = stringDyadicArray[i];
                    if(stringArray!=null && stringArray.length>0){
                        JSONArray strJSONArray = new JSONArray();
                        for (int j = 0; j < stringArray.length; ++j) {
                            strJSONArray.put(j,stringArray[j]);
                        }
                        jsonArray.put(i,strJSONArray);
                    } else {
                        jsonArray.put(i,null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return jsonArray;
    }


    public String[][] getMask() {
        return mMask;
    }

    public String[][] getTimeSection() {
        return mTimeSection;
    }
}
