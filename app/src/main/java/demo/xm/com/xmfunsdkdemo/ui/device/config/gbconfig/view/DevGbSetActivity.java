package demo.xm.com.xmfunsdkdemo.ui.device.config.gbconfig.view;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.gbconfig.listener.DevGbSetContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.gbconfig.presenter.DevGbSetPresenter;
import io.reactivex.annotations.Nullable;

/**
 * GB配置界面,包括使能标记,服务器地址,报警编号,设备编号,通道编号等
 * Created by jiangping on 2018-10-23.
 */
public class DevGbSetActivity extends BaseConfigActivity<DevGbSetPresenter> implements DevGbSetContract.IDevGbSetView, View.OnClickListener {
    private ImageButton mServerOpenImgBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_setup_spvmn_cfg_json);

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_setup_spvmn_cfg_json));
        titleBar.setRightBtnResource(R.mipmap.icon_save_normal,R.mipmap.icon_save_pressed);
        titleBar.setLeftClick(this);
        titleBar.setRightIvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {

            }
        });

        mServerOpenImgBtn = findViewById(R.id.img_setup_server_open_btn);
        mServerOpenImgBtn.setOnClickListener(this);
    }

    @Override
    public DevGbSetPresenter getPresenter() {
        return new DevGbSetPresenter(this);
    }

    @Override
    public void onUpdateView() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_setup_server_open_btn: {
                view.setSelected(!view.isSelected());
            }
            break;

        }
    }
}
