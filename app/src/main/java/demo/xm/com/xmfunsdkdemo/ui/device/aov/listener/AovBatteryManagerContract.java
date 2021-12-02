package demo.xm.com.xmfunsdkdemo.ui.device.aov.listener;

import android.app.Activity;

import com.github.mikephil.charting.data.Entry;

import java.util.Calendar;
import java.util.List;

public class AovBatteryManagerContract {
    public interface IAovBatteryManagerView {

        void onShowWaitDialog();

        Activity getActivity();

        void onHideWaitDialog();

        /**
         * 显示设备的电源状态
         * @param isCharging 是否正在充电中
         * @param curBattery 当前电池电量百分比
         */
        void showPowerState(boolean isCharging, int curBattery);

        /**
         * 显示预览时长
         * @param time 时间字符串
         */
        void showPrePreViewTime(String time);

        /**
         * 显示唤醒时间
         * @param time 唤醒时间
         */
        void showWakeTime(String time);

        /**
         * 显示电源模式
         * @param powerThreshold 电源阈值，用于判断电源模式
         */
        void showPowerMode(int powerThreshold);

        /**
         * 显示电池电量水平
         * @param maxElectr 电池最大电量
         * @param minElectr 电池最小电量
         * @param curElectr 当前电量
         */
        void showBatteryLevel(int maxElectr, int minElectr, int curElectr);

        /**
         * 显示图表数据
         * @param isSearchToday 是否搜索今天的图表数据
         */
        void showChartData(boolean isSearchToday);


        /**
         * 显示报警数量
         */
        void showAlarmNum(String todayAlarmTimeStr);
    }


    public interface IAovBatteryManagerPresenter {
        /**
         * 释放IDR model资源
         */
        public void releaseIDRModel();

        /**
         * 设置电池数据
         * @param curBattery 当前电池电量
         * @param isCharging 电池是否正在充电，true表示正在充电，false表示未在充电
         * 该方法用于更新应用内部记录的设备电池状态，包括当前电量和充电状态
         */

        public void setBatteryData(int curBattery,boolean isCharging);

        /**
         * 初始化数据
         */
        void initData();

        /**
         * 设置是否今日数据的标志
         * @param isSearchToday 表示是否需要搜索和加载今日数据
         */
        void setIsSearchToday(boolean isSearchToday);

        /**
         * 初始化今日图表数据
         * 该方法用于准备或重置今日数据的图表展示，确保图表能正确反映今日数据
         */
        void initTodayChartData();

        /**
         * 获取今日报警次数
         * @return 返回今天的闹报警次数
         */
        int getTodayAlarmTime();

        /**
         * 获取今日预览唤醒时间
         */
        public void getTodayWakePreViewTime();

        /**
         * 初始化一周数据
         */
        void initOneWeekData();

        /**
         * 获取一周报警次数
         * @return 返回这一周的报警次数
         */
        int getOneWeekAlarmTime();

        /**
         * 获取一周预览唤醒时长
         */
        void getOneWeekWakePreViewTime();

        /**
         * 保存低电量模式阈值
         * @param process 低电量模式阈值
         */
        void saveLowElectrMode(int process);

        /**
         * 获取电池数据条目列表
         * @return 返回包含电池电量信息的数据条目列表
         */
        List<Entry> getBatteryEntry();

        /**
         * 获取信号强度数据条目列表
         * @return 返回包含信号强度信息的数据条目列表
         */
        List<Entry> getSignalEntry();

        /**
         * 获取结束时间日历对象
         * @return 返回结束时间的日历对象
         */
        Calendar getEndCalendar();

        /**
         * 获取当天开始时间的日历对象
         * @return 返回当天开始时间的日历对象
         */
        Calendar getToDayStCalendar();

        /**
         * 获取一周开始时间的日历对象
         * @return 返回一周开始时间的日历对象
         */
        Calendar getOnWeekStCalendar();

        /**
         * 判断是否搜索今天数据
         * @return 返回true表示要搜索今天的数据，false表示一周的数据
         */
        boolean isSearchToday();
    }
}
