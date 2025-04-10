package demo.xm.com.xmfunsdkdemo.ui.device.add.lan.view;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.basic.G;
import com.lib.FunSDK;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.manager.db.XMDevInfo;
import com.xm.activity.base.XMBaseActivity;
import com.xm.base.code.ErrorCodeManager;
import com.xm.ui.widget.XTitleBar;

import java.util.ArrayList;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.lan.listener.DevLanConnectContract;
import demo.xm.com.xmfunsdkdemo.ui.device.add.lan.presenter.DevLanConnectPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.add.list.view.DevListActivity;
import io.reactivex.annotations.Nullable;

/**
 * 局域网连接设备界面,显示设备列表菜单,包括名称,类型,mac,sn,ip,状态,以及是否在线
 * LAN connection device interface, display device list menu, including name,
 * type,mac,sn,ip, status, and whether online
 * Created by jiangping on 2018-10-23.
 */
public class DevLanConnectActivity extends DemoBaseActivity<DevLanConnectPresenter>
        implements DevLanConnectContract.IDevLanConnectView,AdapterView.OnItemClickListener {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_lan);

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.guide_module_title_device_lan));
        titleBar.setRightBtnResource(R.mipmap.icon_refresh_normal,R.mipmap.icon_refresh_pressed);
        titleBar.setLeftClick(this);
        titleBar.setRightIvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {//Searching for Devices
                showWaitDialog();
                presenter.searchLanDevice();
            }
        });
        titleBar.setBottomTip(getClass().getName());
        listView = findViewById(R.id.list_lan);
        listView.setOnItemClickListener(this);
    }


    @Override
    public void onUpdateView() {   //Gets the callback for the list of LAN links
        hideWaitDialog();
        List<XMDevInfo> localDevList = presenter.getLanDevList();
        if (localDevList != null && !localDevList.isEmpty()) {
            List<String> devinfoList = new ArrayList<>();
            for (XMDevInfo xmDevInfo : localDevList) {
                devinfoList.add(xmDevInfo.toString() + "\nPID:" + xmDevInfo.getPid());
            }
            adapter = new ArrayAdapter<>(this, R.layout.adapter_simple_list, devinfoList);
            listView.setAdapter(adapter);
        }else {
            Toast.makeText(this, FunSDK.TS("No_device"), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAddDevResult(boolean isSuccess,int errorId) { //Add direct jump successfully
        if (isSuccess) {
            turnToActivity(DevListActivity.class);
        }else {
            showToast(getString(R.string.Add_Dev_Failed) + ":" + ErrorCodeManager.getSDKStrErrorByNO(errorId),Toast.LENGTH_LONG);
        }
    }

    @Override
    public DevLanConnectPresenter getPresenter() {
        return new DevLanConnectPresenter(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        XMDevInfo xmDevInfo = presenter.getLanDevInfo(i);
        if (xmDevInfo != null) {
            showWaitDialog();
            presenter.addDeviceToAccount(xmDevInfo); // Just click and it will be added to the current account
        }
    }
}
