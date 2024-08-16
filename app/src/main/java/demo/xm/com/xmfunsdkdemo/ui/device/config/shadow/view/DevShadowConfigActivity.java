package demo.xm.com.xmfunsdkdemo.ui.device.config.shadow.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.basic.G;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.lib.sdk.bean.StringUtils;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ItemSetLayout;
import com.xm.ui.widget.ListSelectItem;

import java.util.HashMap;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.shadow.listener.DevShadowConfigContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.shadow.presenter.DevShadowConfigPresenter;
import io.reactivex.annotations.Nullable;

/**
 * 设备影子服务配置
 */
public class DevShadowConfigActivity extends BaseConfigActivity<DevShadowConfigPresenter> implements DevShadowConfigContract.IDevShadowConfigView,
        View.OnClickListener {
    private ListSelectItem lsiFieldName;
    private ItemSetLayout isReceiveConfigContent;
    private ItemSetLayout isSendConfigContent;
    private Button btnGetConfig;
    private Button btnSaveConfig;
    private EditText etReceiveConfigContent;
    private EditText etSendConfigContent;
    private String fieldName;

    @Override
    public DevShadowConfigPresenter getPresenter() {
        return new DevShadowConfigPresenter(this);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_shadow_config);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.shadow_config));
        titleBar.setLeftClick(this);
        lsiFieldName = findViewById(R.id.lis_config_name);
        isReceiveConfigContent = findViewById(R.id.is_receive_config_content);
        etReceiveConfigContent = isReceiveConfigContent.findViewById(R.id.et_content);
        isSendConfigContent = findViewById(R.id.is_send_config_content);
        etSendConfigContent = isSendConfigContent.findViewById(R.id.et_content);

        isReceiveConfigContent.findViewById(R.id.ll_ctrl_button).setVisibility(View.VISIBLE);
        btnSaveConfig = isReceiveConfigContent.findViewById(R.id.btn_save_config);
        btnGetConfig = isReceiveConfigContent.findViewById(R.id.btn_get_config);

        btnSaveConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isStringNULL(fieldName)) {
                    showToast(getString(R.string.select_config_name), Toast.LENGTH_LONG);
                    return;
                }

                showWaitDialog();
                isReceiveConfigContent.setLeftTitle(fieldName);
                Gson gson = new Gson();
                HashMap hashMap = gson.fromJson(etReceiveConfigContent.getText().toString(), HashMap.class);
                presenter.setConfig(fieldName,hashMap);
            }
        });

        btnGetConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isStringNULL(fieldName)) {
                    showToast(getString(R.string.select_config_name), Toast.LENGTH_LONG);
                    return;
                }

                showWaitDialog();
                isReceiveConfigContent.setLeftTitle(fieldName);
                presenter.getConfig(fieldName);
                etSendConfigContent.setText(fieldName);
            }
        });

        initFieldName();
    }

    private void initFieldName() {
        lsiFieldName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(DevShadowConfigActivity.this, ShadowConfigNameSelActivity.class), 0);
            }
        });
    }

    private void initData() {
        lsiFieldName.setRightText(fieldName);
        dealWithSearchData(getIntent());
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onSendDataResult(String result) {
        ToastUtils.showLong(result);
        hideWaitDialog();
    }

    @Override
    public void onReceiveDataResult(String state, String result) {
        hideWaitDialog();
        showToast(state, Toast.LENGTH_LONG);

        HashMap hashMap = JSON.parseObject(result,HashMap.class);
        JSONObject jsonObject = (JSONObject) hashMap.get("data");
        if (jsonObject != null) {
            String dataJson = jsonObject.getString(fieldName);
            etReceiveConfigContent.setText(dataJson);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            dealWithSearchData(data);
        }
    }

    private void dealWithSearchData(Intent data) {
        fieldName = data.getStringExtra("fieldName");
        boolean isOnlyRead = data.getBooleanExtra("isOnlyRead", true);
        lsiFieldName.toggleExtraView(true);
        lsiFieldName.setRightText(fieldName);

        if (fieldName == null) {
            return;
        }

        btnGetConfig.setText(R.string.get_config);

        if (!isOnlyRead) {
            btnSaveConfig.setVisibility(View.VISIBLE);
            isSendConfigContent.setVisibility(View.VISIBLE);
        } else {
            btnSaveConfig.setVisibility(View.GONE);
            isSendConfigContent.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.release();
        }
    }
}
