package demo.xm.com.xmfunsdkdemo.ui.device.config.devability;

import com.xm.activity.device.devset.ability.view.XMDevAbilityActivity;

import demo.xm.com.xmfunsdkdemo.utils.StatusBarUtils;

public class DevAbilityActivity extends XMDevAbilityActivity {
    private boolean isInit = false;

    @Override
    protected void onResume() {
        super.onResume();
        if(!isInit){
            StatusBarUtils.setRootView(this);
            isInit = true;
        }
    }
}
