package demo.xm.com.xmfunsdkdemo.ui.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.Gson;

public class AovWorkModeBean {
    @JSONField(name = "Mode")
    private String Mode;
    @JSONField(name = "Custom")
    private WorkInfo Custom;
    @JSONField(name = "Performance")
    private WorkInfo Performance;
    @JSONField(name = "Balance")
    private WorkInfo Balance;

    @JSONField(name = "AlarmHoldTime")
    private Integer AlarmHoldTime;

    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }

    public WorkInfo getCustom() {
        return Custom;
    }

    public void setCustom(WorkInfo custom) {
        Custom = custom;
    }

    public WorkInfo getPerformance() {
        return Performance;
    }

    public void setPerformance(WorkInfo performance) {
        Performance = performance;
    }

    public WorkInfo getBalance() {
        return Balance;
    }

    public void setBalance(WorkInfo balance) {
        Balance = balance;
    }

    public Integer getAlarmHoldTime() {
        return AlarmHoldTime;
    }

    public void setAlarmHoldTime(Integer alarmHoldTime) {
        AlarmHoldTime = alarmHoldTime;
    }

    public static class WorkInfo {
        @JSONField(name = "Fps")
        private String Fps;
        @JSONField(name = "RecordLatch")

        private int RecordLatch;

        public String getFps() {
            return Fps;
        }

        public void setFps(String fps) {
            Fps = fps;
        }

        public int getRecordLatch() {
            return RecordLatch;
        }

        public void setRecordLatch(int recordLatch) {
            RecordLatch = recordLatch;
        }
    }


    public static AovWorkModeBean ParseAvoWorkModeByJson(String json) {
        try {
            AovWorkModeBean AovWorkModeBean = new Gson().fromJson(json, AovWorkModeBean.class);
            return AovWorkModeBean;
        } catch (Exception e) {
            return null;
        }

    }

}
