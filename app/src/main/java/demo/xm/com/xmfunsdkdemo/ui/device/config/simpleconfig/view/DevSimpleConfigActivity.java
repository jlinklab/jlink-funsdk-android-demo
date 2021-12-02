package demo.xm.com.xmfunsdkdemo.ui.device.config.simpleconfig.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ToastUtils;
import com.lib.sdk.bean.CameraFishEyeBean;
import com.lib.sdk.bean.CameraParamBean;
import com.lib.sdk.bean.ConfigJsonNameLink;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.StringUtils;
import com.manager.XMFunSDKManager;
import com.utils.XUtils;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ItemSetLayout;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XMEditText;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.listselectitem.extra.view.ExtraSpinner;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.activity.scanqrcode.CaptureActivity;
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
    /**
     * 和后端设备交互 透传前缀(通过NVR将数据透传给IPC前段)
     */
    private static final String DEV_CONFIG_PENETRATE_PREFIX = "bypass@";
    private ListSelectItem lsiConfigName;
    private ListSelectItem lsiConfigChn;
    private ListSelectItem lsiConfigCmdId;
    private Button btnShareDevData;
    private Button btnViewDoc;//查看文档
    private ExtraSpinner<Integer> spConfigChn;

    private ItemSetLayout isReceiveConfigContent;
    private ItemSetLayout isSendConfigContent;
    private Button btnGetConfig;
    private Button btnSaveConfig;
    private Button btnShowExample;
    private EditText etReceiveConfigContent;
    private EditText etSendConfigContent;
    private XMEditText etInputJsonName;//可以手动输入jsonName信息
    private CheckBox cbNvrPenetrate;//NVR将配置透传到IPC
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
        titleBar.setRightTitleText(getString(R.string.scan_json));
        titleBar.setRightTvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                Dialog dialog = null;
                View layout = LayoutInflater.from(DevSimpleConfigActivity.this).inflate(R.layout.layout_show_json_layout, null);

                ItemSetLayout itemSetLayout = layout.findViewById(R.id.item_json);
                TextView textView = itemSetLayout.findViewById(R.id.tv_content);
                textView.setText("{\n" +
                        "    \"data\": {\n" +
                        "        //要发送给设备的json数据\n" +
                        "    },\n" +
                        "    \"cmdId\": //消息ID,\n" +
                        "    \"jsonName\":,\n" +
                        "    \"chn\"://通道号，-1表示针对设备，其他值表示具体的通道号\n" +
                        "}\n" + "比如:\n" +
                        "{\n" +
                        "    \"data\": {\n" +
                        "        \"OPLogQuery\": {\n" +
                        "            \"Type\": \"LogAll\",\n" +
                        "            \"BeginTime\": \"2023-10-23 00:00:00\",\n" +
                        "            \"EndTime\": \"2023-10-23 24:00:00\"\n" +
                        "        }\n" +
                        "    },\n" +
                        "    \"cmdId\": 1442,\n" +
                        "    \"jsonName\": \"OPLogQuery\",\n" +
                        "    \"chn\": -1\n" +
                        "}");
                Button button = layout.findViewById(R.id.btn_ok);

                dialog = XMPromptDlg.onShow(DevSimpleConfigActivity.this, layout);
                Dialog finalDialog = dialog;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (finalDialog != null) {
                            finalDialog.dismiss();
                        }
                        Intent intent = new Intent();
                        intent.setClass(DevSimpleConfigActivity.this, CaptureActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });
            }
        });
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
                //如果手动输入jsonName的编辑框显示了，需要判断当前的jsonName是否获取到，如果没有的话，就以etInputJsonName编辑框中的内容为主
                if (etInputJsonName.getVisibility() == View.VISIBLE && StringUtils.isStringNULL(jsonName)) {
                    jsonName = etInputJsonName.getEditText().trim();
                    //如果是NVR透传给IPC的，需要在前缀加bypass@
                    if (cbNvrPenetrate.isSelected()) {
                        jsonName = DEV_CONFIG_PENETRATE_PREFIX + jsonName;
                    }
                }

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
                    presenter.cmdConfig(jsonName, cmdId, spConfigChn.getSelectedValue(), etSendConfigContent.getText().toString());
                }
            }
        });

        lsiConfigCmdId = findViewById(R.id.lsi_config_cmd_id);
        btnShowExample = findViewById(R.id.btn_look_json_demo);

        etInputJsonName = findViewById(R.id.et_input_json_name);

        cbNvrPenetrate = findViewById(R.id.cb_nvr_penetrate);
        cbNvrPenetrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jsonName != null) {
                    if (jsonName.startsWith(DEV_CONFIG_PENETRATE_PREFIX)) {
                        jsonName = jsonName.replace(DEV_CONFIG_PENETRATE_PREFIX, "");
                    }

                    if (cbNvrPenetrate.isChecked()) {
                        jsonName = DEV_CONFIG_PENETRATE_PREFIX + jsonName;
                    }
                }

                initConfigChn();
            }
        });

        btnShareDevData = isReceiveConfigContent.findViewById(R.id.btn_share_dev_data);
        btnShareDevData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File file = new File(XMFunSDKManager.getInstance().getAppFilePath() + File.separator + "devData.txt");
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] bytes = etReceiveConfigContent.getText().toString().getBytes();
                    fos.write(bytes);
                    fos.close();

                    Uri fileUri = FileProvider.getUriForFile(DevSimpleConfigActivity.this, "demo.xm.com.xmfunsdkdemo.fileprovider", file);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    DevSimpleConfigActivity.this.startActivity(Intent.createChooser(shareIntent, "分享设备配置"));
                } catch (IOException e) {
                    e.printStackTrace();
                    ToastUtils.showLong("分享失败!");
                }
            }
        });

        btnViewDoc = isReceiveConfigContent.findViewById(R.id.btn_view_doc);
        btnViewDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBrowser(androidJsonDoc);
            }
        });

        initConfigName();
        initConfigChn();
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
        if (cbNvrPenetrate.isChecked()) {
            spConfigChn.initData(new String[]{
                    getString(R.string.chnId) + ":0",
                    getString(R.string.chnId) + ":1",
                    getString(R.string.chnId) + ":2",
                    getString(R.string.chnId) + ":3",
                    getString(R.string.chnId) + ":4",
                    getString(R.string.chnId) + ":5",
                    getString(R.string.chnId) + ":6",
                    getString(R.string.chnId) + ":7",
                    getString(R.string.chnId) + ":8"}, new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8});
        } else {
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
        }

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

        lsiConfigChn.setRightText(spConfigChn.getSelectedName());
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
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                dealWithSearchData(data);
            } else if (requestCode == 1) {
                String result = data.getStringExtra("result");
                try {
                    org.json.JSONObject jsonObject = new org.json.JSONObject(result);
                    if (jsonObject.has("cmdId")) {
                        cmdId = (Integer) jsonObject.get("cmdId");
                    }

                    if (jsonObject.has("data")) {
                        jsonData = (String) jsonObject.getString("data");
                        etSendConfigContent.setText(jsonData);
                    }

                    if (jsonObject.has("jsonName")) {
                        jsonName = (String) jsonObject.get("jsonName");
                    }

                    if (jsonObject.has("chn")) {
                        int chn = (int) jsonObject.get("chn");
                        spConfigChn.setValue(chn);
                    }


                    if (jsonData != null && jsonName != null && cmdId > 0) {
                        btnGetConfig.performClick();
                    } else {
                        ToastUtils.showLong("数据解析异常，无法正常获取到有效数据");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showLong("数据解析异常，无法正常获取到有效数据");
                }
            }
        }
    }

    private void dealWithSearchData(Intent data) {
        jsonName = data.getStringExtra("jsonName");
        //如果是NVR透传给IPC的，需要在前缀加bypass@
        if (cbNvrPenetrate.isChecked()) {
            jsonName = DEV_CONFIG_PENETRATE_PREFIX + jsonName;
        }
        configName = data.getStringExtra("configName");
        jsonData = data.getStringExtra("jsonData");
        cmdId = data.getIntExtra("cmdId", 1042);
        lsiConfigName.toggleExtraView(true);
        lsiConfigName.setRightText(configName);

        if (jsonName == null) {
            return;
        }

        etInputJsonName.setEditText(jsonName);

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

        if (!StringUtils.isStringNULL(jsonExample)) {
            jsonData = jsonExample;
        }

        etSendConfigContent.setText(jsonExample);
    }
}
