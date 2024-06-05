package demo.xm.com.xmfunsdkdemo.ui.device.config.simpleconfig.view;

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

import com.alibaba.fastjson.JSONObject;
import com.lib.sdk.bean.CameraFishEyeBean;
import com.lib.sdk.bean.CameraParamBean;
import com.lib.sdk.bean.ConfigJsonNameLink;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.StringUtils;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ItemSetLayout;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.listselectitem.extra.view.ExtraSpinner;

import java.lang.reflect.Field;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.simpleconfig.listener.DevSimpleConfigContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.simpleconfig.presenter.DevSimpleConfigPresenter;
import io.reactivex.annotations.Nullable;

/**
 * 关于设备界面,包含设备基本信息(序列号,设备型号,硬件版本,软件版本,
 * 发布时间,设备时间,运行时间,网络模式,云连接状态,固件更新及恢复出厂设置)
 * Created by jiangping on 2018-10-23.
 */
public class DevSimpleConfigActivity extends BaseConfigActivity<DevSimpleConfigPresenter> implements DevSimpleConfigContract.IDevSimpleConfigView, View.OnClickListener {
    private ListSelectItem lsiConfigName;
    private ListSelectItem lsiConfigChn;
    private ListSelectItem lsiConfigCmdId;
    private ExtraSpinner<Integer> spConfigChn;

    private ItemSetLayout isReceiveConfigContent;
    private ItemSetLayout isSendConfigContent;
    private Button btnGetConfig;
    private Button btnSaveConfig;
    private Button btnShowExample;
    private EditText etReceiveConfigContent;
    private EditText etSendConfigContent;
    private String jsonExample;
    private String jsonName;
    private String configName;
    private Integer cmdId;
    private String jsonData;

    @Override
    public DevSimpleConfigPresenter getPresenter() {
        return new DevSimpleConfigPresenter(this);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_simple_config);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.simple_config));
        titleBar.setLeftClick(this);
        lsiConfigChn = findViewById(R.id.lis_config_chn);
        lsiConfigName = findViewById(R.id.lis_config_name);
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
                if (StringUtils.isStringNULL(jsonName)) {
                    showToast(getString(R.string.select_config_name), Toast.LENGTH_LONG);
                    return;
                }

                showWaitDialog();
                presenter.saveConfig(jsonName, spConfigChn.getSelectedValue(), etReceiveConfigContent.getText().toString());
                isReceiveConfigContent.setLeftTitle(jsonName);
            }
        });

        btnGetConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isStringNULL(jsonName)) {
                    showToast(getString(R.string.select_config_name), Toast.LENGTH_LONG);
                    return;
                }

                showWaitDialog();
                isReceiveConfigContent.setLeftTitle(configName);

                if (cmdId == null || cmdId == 1042) {
                    presenter.getConfig(jsonName, spConfigChn.getSelectedValue());
                    JSONObject jsonObject = new JSONObject();
                    if (spConfigChn.getSelectedValue() == -1) {
                        jsonObject.put("Name", jsonName);
                    } else {
                        jsonObject.put("Name", jsonName + ".[" + spConfigChn.getSelectedValue() + "]");
                    }

                    jsonObject.put("SessionID", "0x08");
                    etSendConfigContent.setText(jsonObject.toJSONString());
                } else {
                    presenter.cmdConfig(jsonName, cmdId, spConfigChn.getSelectedValue(), jsonData);
                    if (!StringUtils.isStringNULL(jsonData)) {
                        etSendConfigContent.setText(jsonData);
                    }
                }
            }
        });

        initConfigName();
        initConfigChn();

        lsiConfigCmdId = findViewById(R.id.lsi_config_cmd_id);
        btnShowExample = findViewById(R.id.btn_look_json_demo);
    }

    private void initConfigName() {
        lsiConfigName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(DevSimpleConfigActivity.this, ConfigNameSelActivity.class), 0);
            }
        });
    }

    private void initConfigChn() {
        spConfigChn = lsiConfigChn.getExtraSpinner();
        spConfigChn.initData(new String[]{getString(R.string.chnId) + ":-1",
                getString(R.string.chnId) + ":0",
                getString(R.string.chnId) + ":1",
                getString(R.string.chnId) + ":2",
                getString(R.string.chnId) + ":3",
                getString(R.string.chnId) + ":4",
                getString(R.string.chnId) + ":5",
                getString(R.string.chnId) + ":6",
                getString(R.string.chnId) + ":7",
                getString(R.string.chnId) + ":8"}, new Integer[]{-1, 0, 1, 2, 3, 4, 5, 6, 7, 8});
        lsiConfigChn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lsiConfigChn.toggleExtraView();
            }
        });
        spConfigChn.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
            @Override
            public void onItemClick(int i, String s, Object o) {
                lsiConfigChn.toggleExtraView(true);
                lsiConfigChn.setRightText(s);
            }
        });
    }


    private void initData() {
        lsiConfigName.setRightText(configName);
        spConfigChn.setValue(-1);
        lsiConfigChn.setRightText(spConfigChn.getSelectedName());

        dealWithSearchData(getIntent());
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onSendDataResult(String result) {

    }

    @Override
    public void onReceiveDataResult(String state, String result) {
        hideWaitDialog();
        showToast(state, Toast.LENGTH_LONG);
        etReceiveConfigContent.setText(result);
    }

    public void onLookJsonDemo(View view) {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.WHITE);
        TextView textView = new TextView(this);
        textView.setText(jsonExample);
        textView.setBackgroundColor(Color.WHITE);
        scrollView.addView(textView);
        XMPromptDlg.onShow(this, scrollView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            dealWithSearchData(data);
        }
    }

    private void dealWithSearchData(Intent data) {
        jsonName = data.getStringExtra("jsonName");
        configName = data.getStringExtra("configName");
        jsonData = data.getStringExtra("jsonData");
        cmdId = data.getIntExtra("cmdId", 1042);
        lsiConfigName.toggleExtraView(true);
        lsiConfigName.setRightText(configName);

        if (jsonName == null) {
            return;
        }

        /**
         * 如果命令ID不是 1042（配置）都是走命令方式的
         * If the command ID is not 1042 (configuration), it is all in the command mode
         */
        if (cmdId != 1042) {
            lsiConfigCmdId.setRightText(cmdId + "");
            lsiConfigCmdId.setVisibility(View.VISIBLE);
            btnGetConfig.setText(R.string.send_cmd);
            btnSaveConfig.setVisibility(View.GONE);
        } else {
            lsiConfigCmdId.setVisibility(View.GONE);
            btnGetConfig.setText(R.string.get_config);
            btnSaveConfig.setVisibility(View.VISIBLE);
        }

        isSendConfigContent.setVisibility(View.VISIBLE);
        if (!StringUtils.isStringNULL(jsonData)) {
            jsonExample = jsonData;
            btnShowExample.setVisibility(View.VISIBLE);
        } else {
            btnShowExample.setVisibility(View.GONE);
            if (cmdId != 1042) {
                isSendConfigContent.setVisibility(View.GONE);
            }
        }
    }
}
