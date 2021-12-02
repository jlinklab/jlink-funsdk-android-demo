package demo.xm.com.xmfunsdkdemo.ui.device.alarm.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lib.FunSDK;
import com.lib.sdk.bean.WhiteLightBean;
import com.utils.XUtils;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.listselectitem.extra.view.ExtraSpinner;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.utils.ParseTimeUtil;

/**
 * 声光报警:音乐灯界面
 */
public class MusicLightActivity extends WhiteLightActivity{

    private String[] mMusicSwitchArray;
    private ExtraSpinner mSpMusicSwitch;
    private ListSelectItem mLsiMusicSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLsiMusicSwitch = findViewById(R.id.lsi_white_light_music_switch);
        mLsiMusicSwitch.setVisibility(View.VISIBLE);
        if(mLisWhiteLightSwitch != null){
            mLisWhiteLightSwitch.setVisibility(View.GONE);
        }
        initWhiteLightMusicSwitch();
    }


    private void initWhiteLightMusicSwitch() {
        //音乐灯泡控制开关
        initMusicSwitchData();
    }

    private void initMusicSwitchData() {
        mMusicSwitchArray = new String[]{
                FunSDK.TS("Auto_model"),FunSDK.TS("open"),FunSDK.TS("close"),
                FunSDK.TS("timing"),FunSDK.TS("Atmosphere"),FunSDK.TS("Glint")
        };
        mLsiMusicSwitch.setTip(XUtils.getLightViewTips(mMusicSwitchArray));
        mSpMusicSwitch = mLsiMusicSwitch.getExtraSpinner();
//        mSpMusicSwitch.setScrollEnable(false);
        mSpMusicSwitch.initData(mMusicSwitchArray,new Integer[]{0,1,2,3,4,5});
        mLsiMusicSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLsiMusicSwitch.toggleExtraView();
            }
        });
        mSpMusicSwitch.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
            @Override
            public void onItemClick(int position, String key, Object value) {
                mLsiMusicSwitch.toggleExtraView(true);
                WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
                if(null != mWhiteLight){
                    switch (position){
                        case 0:
                            mTimeSettingLayout.setVisibility(View.GONE);
                            mIntelligentModelLayout.setVisibility(View.GONE);
                            mWhiteLight.setWorkMode("Auto");
                            presenter.saveWhiteLight();
                            break;
                        case 1:
                            mTimeSettingLayout.setVisibility(View.GONE);
                            mIntelligentModelLayout.setVisibility(View.GONE);
                            mWhiteLight.setWorkMode("KeepOpen");
                            presenter.saveWhiteLight();
                            break;
                        case 2:
                            mTimeSettingLayout.setVisibility(View.GONE);
                            mIntelligentModelLayout.setVisibility(View.GONE);
                            mWhiteLight.setWorkMode("Close");
                            presenter.saveWhiteLight();
                            break;
                        case 3:
                            String openTime = ParseTimeUtil.parseTime(mWhiteLight.getWorkPeriod().getSHour(),
                                    mWhiteLight.getWorkPeriod().getSMinute());
                            String closeTime = ParseTimeUtil.parseTime(mWhiteLight.getWorkPeriod().getEHour(),
                                    mWhiteLight.getWorkPeriod().getEMinute());
                            mWhiteLightOpenTime.setText(openTime);
                            mWhiteLightCloseTime.setText(closeTime);
                            mWhiteLight.setWorkMode("Timing");
                            mTimeSettingLayout.setVisibility(View.VISIBLE);
                            mIntelligentModelLayout.setVisibility(View.GONE);
                            presenter.saveWhiteLight();
                            break;
                        case 4:
                            //气氛灯开关
                            mTimeSettingLayout.setVisibility(View.GONE);
                            mIntelligentModelLayout.setVisibility(View.GONE);
                            mWhiteLight.setWorkMode("Atmosphere");
                            presenter.saveWhiteLight();
                            break;
                        case 5:
                            //随音乐灯开关
                            mTimeSettingLayout.setVisibility(View.GONE);
                            mIntelligentModelLayout.setVisibility(View.GONE);

                            mWhiteLight.setWorkMode("Glint");
                            presenter.saveWhiteLight();
                            break;
                        default:
                            break;
                    }
                }
                mLsiMusicSwitch.setRightText(key);

            }
        });
    }


    @Override
    public void showWorkMode(WhiteLightBean mWhiteLight) {
        super.showWorkMode(mWhiteLight);
        if (mWhiteLight != null) {
            if (mWhiteLight.getWorkMode().equals("Auto")) {
                mSpMusicSwitch.setValue(0);
                mLsiMusicSwitch.setRightText((CharSequence)mSpMusicSwitch.getSelectedName());
            } else if (mWhiteLight.getWorkMode().equals("KeepOpen")) {
                mSpMusicSwitch.setValue(1);
                mLsiMusicSwitch.setRightText((CharSequence)mSpMusicSwitch.getSelectedName());
            } else if (mWhiteLight.getWorkMode().equals("Timing")) {
                mSpMusicSwitch.setValue(3);
                mLsiMusicSwitch.setRightText((CharSequence)mSpMusicSwitch.getSelectedName());
            } else if (mWhiteLight.getWorkMode().equals("Close")) {
                mSpMusicSwitch.setValue(2);
                mLsiMusicSwitch.setRightText((CharSequence)mSpMusicSwitch.getSelectedName());
            } else if (mWhiteLight.getWorkMode().equals("Intelligent") && null != mWhiteLight.getMoveTrigLight()) {

            } else if (mWhiteLight.getWorkMode().equals("Atmosphere")) {
                mSpMusicSwitch.setValue(4);
                mLsiMusicSwitch.setRightText((CharSequence)mSpMusicSwitch.getSelectedName());
            } else if (mWhiteLight.getWorkMode().equals("Glint")) {
                mSpMusicSwitch.setValue(5);
                mLsiMusicSwitch.setRightText((CharSequence)mSpMusicSwitch.getSelectedName());
            } else {
                Toast.makeText(getApplicationContext(), FunSDK.TS("Data_exception"), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
