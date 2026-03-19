package demo.xm.com.xmfunsdkdemo.ui.device.config.devability;

import android.os.Build;

import com.xm.activity.device.devset.ability.view.XMDevAbilityActivity;

import demo.xm.com.xmfunsdkdemo.utils.StatusBarUtils;

public class DevAbilityActivity extends XMDevAbilityActivity {
    private boolean isInit = false;

    @Override
    protected void onResume() {
        super.onResume();
        if(!isInit){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                StatusBarUtils.setRootView(this);
            }
            isInit = true;
        }
    }
}
