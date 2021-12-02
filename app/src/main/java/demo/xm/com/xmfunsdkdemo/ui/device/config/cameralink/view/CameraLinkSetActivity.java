package demo.xm.com.xmfunsdkdemo.ui.device.config.cameralink.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lib.SDKCONST;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.cameralink.listener.CameraLinkSetContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.cameralink.presenter.CameraLinkSetPresenter;

/**
 * 相机联动（多目枪球云台定位）
 */
public class CameraLinkSetActivity extends DemoBaseActivity<CameraLinkSetPresenter> implements CameraLinkSetContract.ICameraLinkSetView {
    /**
     * 相机联动开关
     */
    private ListSelectItem lsiCameraLinkSwitch;
    /**
     * 相机联动校准
     */
    private ListSelectItem lsiCameraLinkAiming;

    private CameraLinkSetPresenter mPresenter;

    @Override
    public CameraLinkSetPresenter getPresenter() {
        return new CameraLinkSetPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_link_set);
        initView();
        initData();
    }

    private void initData() {
        presenter.getDevId();
    }

    private void initView() {
        lsiCameraLinkSwitch = findViewById(R.id.lsi_link_switch);
        lsiCameraLinkAiming = findViewById(R.id.lsi_link_aiming);

        lsiCameraLinkSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.saveCameraLinkEnable(lsiCameraLinkSwitch.getRightValue() == SDKCONST.Switch.Open ? false : true);
            }
        });

        lsiCameraLinkAiming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(CameraLinkSetActivity.this,CameraLinkInitActivity.class);
//                intent.putExtra("devId",presenter.getDevId());
//                startActivity(intent);
                presenter.initCameraLink();
            }
        });
        XTitleBar xTitleBar = (XTitleBar) findViewById(R.id.layoutTop);
        xTitleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });

    }

    @Override
    public void getCameraLinkFailed(String ex, int i, String s2, int i2) {
        Toast.makeText(this,getString( R.string.EE_AS_SYS_GET_USER_INFO_CODE4), Toast.LENGTH_LONG).show();
    }

    @Override
    public void getCameraLinkSuccess(org.json.JSONObject jsonObject) {
        Log.e("dzc", "jsonObject:" + jsonObject.toString());
        if (jsonObject.has("Ptz.GunBallLocate")) {
            try {
                boolean enable = false;
                JSONArray jsonArray = jsonObject.optJSONArray("Ptz.GunBallLocate");
                if (jsonArray != null && jsonArray.length() > 0) {
                    JSONObject  mJsonObject = (JSONObject) jsonArray.get(0);
                    if (mJsonObject != null && mJsonObject.has("Enable")) {
                        enable = mJsonObject.getBoolean("Enable");
                    }
                }

                lsiCameraLinkSwitch.setRightImage(enable ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
                if (enable) {
                    lsiCameraLinkSwitch.setRightImageResource(R.mipmap.icon_checked_yes);
                } else {
                    lsiCameraLinkSwitch.setRightImageResource(R.mipmap.icon_checked_no);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void setSaveSuccess() {
        lsiCameraLinkSwitch.setRightImage(lsiCameraLinkSwitch.getRightValue() == SDKCONST.Switch.Open ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
        if (lsiCameraLinkSwitch.getRightValue() == SDKCONST.Switch.Open) {
            lsiCameraLinkSwitch.setRightImageResource(R.mipmap.icon_checked_yes);
        } else {
            lsiCameraLinkSwitch.setRightImageResource(R.mipmap.icon_checked_no);
        }
    }

    @Override
    public void showWaitDlgShow(boolean show) {
        if (show) {
            showWaitDialog();
        } else {
            hideWaitDialog();
        }
    }

    @Override
    public void showWaitDlgShow(String str) {
        showWaitDialog(str);
    }

    @Override
    public void initFailed() {

    }

    @Override
    public Context getContext() {
        return this;
    }
}
