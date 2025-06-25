package demo.xm.com.xmfunsdkdemo.ui.device.cloud.view;

import static demo.xm.com.xmfunsdkdemo.ui.device.cloud.view.CloudWebActivity.GOODS_TYPE_CLOUD_STORAGE;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.manager.device.media.MediaManager;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.cloud.listener.CloudStateContract;
import demo.xm.com.xmfunsdkdemo.ui.device.cloud.presenter.CloudStatePresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.record.view.DevRecordActivity;

/**
 * @author hws
 * @class 云服务状态
 * @time 2020/7/28 11:14
 */
public class CloudStateActivity extends DemoBaseActivity<CloudStatePresenter> implements CloudStateContract.ICloudStateView {
    private ListSelectItem lsiCloudStorage;
    private Button btnCloudPlayback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_cloud);
        initView();
        initData();
    }

    private void initView() {
        lsiCloudStorage = findViewById(R.id.lsi_cloud_storage);
        lsiCloudStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter.isCanTurnToCloudWeb()) {
                    //goodsType 根据需求传入对应的值，比如云存储->GOODS_TYPE_CLOUD_STORAGE，云电话->GOODS_TYPE_CALL，4G流量->GOODS_TYPE_FLOW, 如果不传的话默认云服务首页
                    turnToActivity(CloudWebActivity.class,new String[][]{{"devId",presenter.getDevId(),"goodsType",GOODS_TYPE_CLOUD_STORAGE}});
                }
            }
        });

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.cloud_service));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        titleBar.setBottomTip(getClass().getName());

        btnCloudPlayback = findViewById(R.id.btn_cloud_playback);
        btnCloudPlayback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter.isCanTurnToCloudWeb()) {
                    turnToActivity(DevRecordActivity.class, new Object[][]{{"devId", presenter.getDevId()},
                            {"recordType", MediaManager.PLAY_CLOUD_PLAYBACK}});
                }else {
                    showToast("不支持云存储，无法查看云回放功能", Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void initData() {
        showWaitDialog();
        presenter.updateCloudState(this);
    }

    @Override
    public CloudStatePresenter getPresenter() {
        return new CloudStatePresenter(this);
    }

    @Override
    public void onUpdateCloudStateResult(int state,String result) {
        hideWaitDialog();
        lsiCloudStorage.setRightText(result);
    }
}
