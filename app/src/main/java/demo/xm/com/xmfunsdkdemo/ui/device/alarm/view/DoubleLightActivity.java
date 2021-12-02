package demo.xm.com.xmfunsdkdemo.ui.device.alarm.view;

import android.os.Bundle;
import android.view.View;

import com.lib.FunSDK;

/**
 * 声光报警:双光灯界面
 */
public class DoubleLightActivity extends WhiteLightActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mLsiMusicSwitch != null ){
            mLsiMusicSwitch.setVisibility(View.GONE);
        }
        if(mLisWhiteLightSwitch != null){
            mLisWhiteLightSwitch.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected String[] getWhiteLightSwitchData() {
        if (FunSDK.GetDevAbility(presenter.getDevId(), "OtherFunction/NotSupportAutoAndIntelligent") == 1) {
            return new String[]{FunSDK.TS("open"), FunSDK.TS("close")};
        } else {
            return new String[]{FunSDK.TS("Auto_model"), FunSDK.TS("open"),
                    FunSDK.TS("close"), FunSDK.TS("timing"), FunSDK.TS("Intelligent_switch")};
        }
    }

    @Override
    protected Integer[] getWhiteLightSwitchIntValue() {
        if (FunSDK.GetDevAbility(presenter.getDevId(), "OtherFunction/NotSupportAutoAndIntelligent") == 1) {
            return new Integer[]{1, 2};
        } else {
            return new Integer[]{0, 1, 2, 3, 4};
        }
    }

}
