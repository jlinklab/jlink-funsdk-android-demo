package demo.xm.com.xmfunsdkdemo.ui.device.config.jdevdebug.view;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.jdevdebug.listener.DevJsonCmdContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.jdevdebug.presenter.DevJsonCmdPresenter;
import io.reactivex.annotations.Nullable;

/**
 * Json 和 DevCmd 调试界面,设置或获得相应配置名称和通道编号的字符串
 * Created by jiangping on 2018-10-23.
 */
public class DevJsonCmdActivity extends BaseConfigActivity<DevJsonCmdPresenter> implements DevJsonCmdContract.IDevJsonCmdView, OnCheckedChangeListener {
    private RadioGroup optModeConfig;

    @Override
    public DevJsonCmdPresenter getPresenter() {
        return new DevJsonCmdPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_setup_json);

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_setup_jsonanddevcmd));
        titleBar.setLeftClick(this);

        optModeConfig = findViewById(R.id.radioConfigMode);
        optModeConfig.check(R.id.radioBtnbyjson);
        optModeConfig.setOnCheckedChangeListener(this);

    }

    @Override
    public void onUpdateView() {

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.radioBtnbyjson:
                findViewById(R.id.linearlayout_seq).setVisibility(View.GONE);
                break;
            case R.id.radioBtnbydevcmd:
                findViewById(R.id.linearlayout_seq).setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
