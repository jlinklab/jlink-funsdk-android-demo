package demo.xm.com.xmfunsdkdemo.ui.entity;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * 工作模式
 */
public class WorkModeBean {
    public static final int WORK_MODE_LOW_POWER = 0;
    public static final int WORK_MODE_SMART = 1;
    /**
     * (0：低功耗模式，1:智能模式)
     */
    private int modeType;
    /**
     * (0：低功耗模式，1:智能模式)
     */
    private int WorkStateNow = -1;
    /**
     * 模式切换电量临界值
     */
    private Integer PowerThreshold;
    @JSONField(name = "MaskTimeSection")
    private int maskTimeSection;
    @JSONField(name = "NoSleepTimeSection")
    private ArrayList<NoSleepTimeSection> noSleepTimeSections;

    @JSONField(name = "ModeType")
    public int getModeType() {
        return modeType;
    }

    @JSONField(name = "ModeType")
    public void setModeType(int modeType) {
        this.modeType = modeType;
    }

    @JSONField(name = "WorkStateNow")
    public int getWorkStateNow() {
        return WorkStateNow;
    }

    @JSONField(name = "WorkStateNow")
    public void setWorkStateNow(int workStateNow) {
        WorkStateNow = workStateNow;
    }

    @JSONField(name = "PowerThreshold")
    public Integer getPowerThreshold() {
        return PowerThreshold;
    }

    @JSONField(name = "PowerThreshold")
    public void setPowerThreshold(Integer powerThreshold) {
        PowerThreshold = powerThreshold;
    }

    public int getMaskTimeSection() {
        return maskTimeSection;
    }

    public void setMaskTimeSection(int maskTimeSection) {
        this.maskTimeSection = maskTimeSection;
    }

    /**
     * 是否支持时间段设置
     * @return
     */
    @JSONField(serialize = false)
    public boolean isSupportTimeSet(int modeType) {
        if ((maskTimeSection >> modeType) == 1) {
            return true;
        }

        return false;
    }

    public ArrayList<NoSleepTimeSection> getNoSleepTimeSections() {
        return noSleepTimeSections;
    }

    public void setNoSleepTimeSections(ArrayList<NoSleepTimeSection> noSleepTimeSections) {
        this.noSleepTimeSections = noSleepTimeSections;
    }

    @JSONField(serialize = false)
    public NoSleepTimeSection getNoSleepTimeSection() {
        if (noSleepTimeSections == null ||
                modeType < 0 ||
                modeType >= noSleepTimeSections.size()) {
            return null;
        }

        return noSleepTimeSections.get(modeType);
    }

    @JSONField(serialize = false)
    public int[] getStartTimeBySelMode() {
        if (noSleepTimeSections == null ||
                modeType < 0 ||
                modeType >= noSleepTimeSections.size()) {
            return null;
        }

        NoSleepTimeSection noSleepTimeSection = noSleepTimeSections.get(modeType);
        if (noSleepTimeSection != null) {
            return timeStringToInt(noSleepTimeSection.getStartTime());
        }

        return null;
    }

    @JSONField(serialize = false)
    public void setStartTimeBySelMode(int[] startTime) {
        if (noSleepTimeSections == null ||
                modeType < 0 ||
                modeType >= noSleepTimeSections.size() ||
                startTime == null) {
            return;
        }

        try {
            NoSleepTimeSection noSleepTimeSection = noSleepTimeSections.get(modeType);
            if (noSleepTimeSection != null) {
                noSleepTimeSection.setStartTime(String.format("%02d:%02d", startTime[0], startTime[1]));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JSONField(serialize = false)
    public int[] getEndTimeBySelMode() {
        if (noSleepTimeSections == null ||
                modeType < 0 ||
                modeType >= noSleepTimeSections.size()) {
            return null;
        }

        NoSleepTimeSection noSleepTimeSection = noSleepTimeSections.get(modeType);
        if (noSleepTimeSection != null) {
            return timeStringToInt(noSleepTimeSection.getEndTime());
        }

        return null;
    }

    @JSONField(serialize = false)
    public void setEndTimeBySelMode(int[] endTime) {
        if (noSleepTimeSections == null ||
                modeType < 0 ||
                modeType >= noSleepTimeSections.size() ||
                endTime == null) {
            return;
        }

        try {
            NoSleepTimeSection noSleepTimeSection = noSleepTimeSections.get(modeType);
            if (noSleepTimeSection != null) {
                noSleepTimeSection.setEndTime(String.format("%02d:%02d", endTime[0], endTime[1]));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JSONField(serialize = false)
    public int getWeekMaskBySelMode() {
        if (noSleepTimeSections == null ||
                modeType < 0 ||
                modeType >= noSleepTimeSections.size()) {
            return 0;
        }

        NoSleepTimeSection noSleepTimeSection = noSleepTimeSections.get(modeType);
        if (noSleepTimeSection != null) {
            return noSleepTimeSection.getWeekMask();
        }

        return 0;
    }

    @JSONField(serialize = false)
    public void setWeekMaskBySelMode(int weekMask) {
        if (noSleepTimeSections == null ||
                modeType < 0 ||
                modeType >= noSleepTimeSections.size()) {
            return;
        }

        try {
            NoSleepTimeSection noSleepTimeSection = noSleepTimeSections.get(modeType);
            if (noSleepTimeSection != null) {
                noSleepTimeSection.setWeekMask(weekMask);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JSONField(serialize = false)
    public boolean isEnableBySelMode() {
        if (noSleepTimeSections == null ||
                modeType < 0 ||
                modeType >= noSleepTimeSections.size()) {
            return false;
        }

        NoSleepTimeSection noSleepTimeSection = noSleepTimeSections.get(modeType);
        if (noSleepTimeSection != null) {
            return noSleepTimeSection.isEnable();
        }

        return false;
    }

    @JSONField(serialize = false)
    public void setEnableBySelMode(boolean enable) {
        if (noSleepTimeSections == null ||
                modeType < 0 ||
                modeType >= noSleepTimeSections.size()) {
            return;
        }

        try {
            NoSleepTimeSection noSleepTimeSection = noSleepTimeSections.get(modeType);
            if (noSleepTimeSection != null) {
                noSleepTimeSection.setEnable(enable);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class NoSleepTimeSection {
        @JSONField(name = "Enable")
        private boolean enable;
        @JSONField(name = "StartTime")
        private String startTime;
        @JSONField(name = "EndTime")
        private String endTime;
        @JSONField(name = "WeekMask")
        private int weekMask;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public int getWeekMask() {
            return weekMask;
        }

        public void setWeekMask(int weekMask) {
            this.weekMask = weekMask;
        }
    }

    @JSONField(serialize = false)
    private int[] timeStringToInt(String time) {
        int[] iTimes = null;
        if (!TextUtils.isEmpty(time)) {
            String[] times = time.split(":");
            if (null != times && times.length == 2) {
                iTimes = new int[]{Integer.parseInt(times[0]),
                        Integer.parseInt(times[1])};
            }
        }
        return iTimes;
    }
}
