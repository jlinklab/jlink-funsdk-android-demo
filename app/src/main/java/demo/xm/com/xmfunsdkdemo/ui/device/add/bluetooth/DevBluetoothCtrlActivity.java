package demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth;

import static com.constant.SDKLogConstant.APP_BLE;
import static com.lib.sdk.bean.bluetooth.XMBleHead.CMD_SEND;
import static com.manager.db.Define.LOGIN_NONE;
import static com.manager.device.media.audio.XMAudioManager.SPEAKER_TYPE_MAN;
import static com.manager.device.media.audio.XMAudioManager.SPEAKER_TYPE_NORMAL;
import static com.manager.device.media.audio.XMAudioManager.SPEAKER_TYPE_WOMAN;
import static com.utils.BleDistributionUtil.combineFlameHeader;
import static com.utils.BleDistributionUtil.createCheckCode;
import static com.utils.BleUtils.CmdId.APP_RESPONSE;
import static com.utils.BleUtils.CmdId.SEND;
import static com.utils.BleUtils.DataType.NO_ENCRY_BINARY;
import static com.utils.BleUtils.FunId.DMS_BY_BLE;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.bluetooth.XMBleData;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.bluetooth.IXMBleManager;
import com.manager.bluetooth.IXMBleManagerListener;
import com.manager.bluetooth.XMBleManager;
import com.manager.db.DevDataCenter;
import com.manager.device.DeviceManager;
import com.utils.LogUtils;
import com.utils.XUtils;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.BtnColorBK;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.RippleButton;
import com.xm.ui.widget.dialog.EditDialog;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.ptzview.PtzView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.view.ActivityGuideDeviceLanAlarm;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.view.DoubleLightActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.view.DoubleLightBoxActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.view.GardenDoubleLightActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.view.MusicLightActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.view.WhiteLightActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.cameralink.view.CameraLinkSetActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.detecttrack.DetectTrackActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.simpleconfig.view.DevSimpleConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.picture.view.DevPictureActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.view.DevMonitorActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.record.view.DevRecordActivity;
import demo.xm.com.xmfunsdkdemo.ui.entity.TuyaBleData;

/**
 * 蓝牙控制交互
 */
public class DevBluetoothCtrlActivity extends DemoBaseActivity {
    private static final int FUN_BLE_REMOTE_UN_DOOR = 61;//远程解锁
    private static final int FUN_BLE_ADD_UN_LOCK_TYPE = 1;//添加开锁方式
    private IXMBleManager xmBleManager;
    private String bleMac;
    private List<HashMap<String, Object>> bleFunList = new ArrayList<>();//蓝牙交互的功能列表
    private RecyclerView rvBleFun;
    private TextView receiveContent;

    @Override
    public XMBasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_ble_ctrl_test);
        initView();
        initData();
    }

    private void initData() {
        xmBleManager = XMBleManager.getInstance();
        xmBleManager.addNotifyManager(bleMac, ixmBleManagerListener);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_BLE_REMOTE_UN_DOOR);
        hashMap.put("itemName", "远程解锁");
        bleFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_BLE_ADD_UN_LOCK_TYPE);
        hashMap.put("itemName", "添加开锁方式");
        bleFunList.add(hashMap);

        bleMac = getIntent().getStringExtra("bleMac");
    }

    private void initView() {
        rvBleFun = findViewById(R.id.rv_ble_fun);
        rvBleFun.setLayoutManager(new LinearLayoutManager(this));

        receiveContent = findViewById(R.id.tv_result_content);

        BleFunAdapter bleFunAdapter = new BleFunAdapter();
        rvBleFun.setAdapter(bleFunAdapter);
    }

    private IXMBleManagerListener ixmBleManagerListener = new IXMBleManagerListener() {
        @Override
        public void onResponse(String mac, XMBleData data, int code) {
            super.onResponse(mac, data, code);
            TuyaBleData tuyaBleData = new TuyaBleData();
            tuyaBleData.parseData(data.getCmdId() == CMD_SEND, data.getContentDataHexString());

            switch (tuyaBleData.getDpId()) {
                case FUN_BLE_REMOTE_UN_DOOR:
                    receiveContent.setText(tuyaBleData.getDpDataContent());
                    break;
            }
        }
    };

    public class BleFunAdapter extends RecyclerView.Adapter<BleFunAdapter.ViewHolder> {
        @NonNull
        @Override
        public BleFunAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BleFunAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_monitor_fun, null));
        }

        @Override
        public void onBindViewHolder(@NonNull BleFunAdapter.ViewHolder holder, int position) {
            HashMap<String, Object> hashMap = bleFunList.get(position);
            if (hashMap != null) {
                String itemName = (String) hashMap.get("itemName");
                int itemId = (int) hashMap.get("itemId");
                holder.btnMonitorFun.setText(itemName);
                holder.btnMonitorFun.setTag(itemId);
            }
        }

        @Override
        public int getItemCount() {
            return bleFunList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            BtnColorBK btnMonitorFun;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                btnMonitorFun = itemView.findViewById(R.id.btn_monitor_fun);
                btnMonitorFun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        HashMap<String, Object> hashMap = bleFunList.get(position);
                        if (hashMap != null) {
                            int itemId = (int) hashMap.get("itemId");
                            boolean isBtnChange = dealWithBleFunction(itemId, btnMonitorFun.isSelected());
                            if (isBtnChange) {
                                btnMonitorFun.setSelected(!btnMonitorFun.isSelected());
                            }
                        }
                    }
                });

            }
        }

        public void changeBtnState(int itemId, boolean isSelected) {
            BtnColorBK btnMonitorFun = rvBleFun.findViewWithTag(itemId);
            if (btnMonitorFun != null) {
                btnMonitorFun.setSelected(isSelected);
            }
        }

        private boolean dealWithBleFunction(int itemId, boolean isSelected) {
            switch (itemId) {
                case FUN_BLE_REMOTE_UN_DOOR:
                    TuyaBleData tuyaBleData = new TuyaBleData();
                    tuyaBleData.setDpId(61);
                    tuyaBleData.setDpType(0);
                    tuyaBleData.setDpDataLen(23);
                    tuyaBleData.setDpDataContent("FFFF0001000000000000000000FFFF3034363837323533");
                    concatenateData(tuyaBleData.getSendData());
                    break;
                case FUN_BLE_ADD_UN_LOCK_TYPE:
                    concatenateData("0100001E01000002FF386CD30072BC9B7F0000000000000000000106010203040506");
                    break;
            }

            return false;
        }

        private void concatenateData(String data) {
            StringBuilder flameContent = new StringBuilder();
            flameContent.append(data);
            StringBuilder sendData = new StringBuilder();
            String flameHeaderStr = combineFlameHeader("01", SEND, "0030", NO_ENCRY_BINARY);//帧头
            sendData.append(flameHeaderStr);
            sendData.append(String.format("%04x", flameContent.length() / 2));/**帧内容长度**/
            sendData.append(flameContent);

            String checkCode = createCheckCode(sendData.toString());
            LogUtils.debugInfo(APP_BLE, "checkCode:" + checkCode);
            sendData.append(checkCode);

            String hexData = sendData.toString().toUpperCase();
            LogUtils.debugInfo(APP_BLE, "hexData:" + hexData);

            xmBleManager.write(bleMac, ConvertUtils.hexString2Bytes(hexData.toUpperCase()), ixmBleManagerListener);
        }
    }
}
