package demo.xm.com.xmfunsdkdemo.ui.device.add.list.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lib.EFUN_ERROR;
import com.lib.FunSDK;
import com.lib.sdk.bean.share.OtherShareDevUserBean;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.config.PwdErrorManager;
import com.utils.XUtils;
import com.xm.activity.device.devset.ability.view.XMDevAbilityActivity;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.dialog.EditDialog;

import java.util.ArrayList;
import java.util.HashMap;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.adapter.DevListAdapter;
import demo.xm.com.xmfunsdkdemo.ui.device.add.list.listener.DevListConnectContract;
import demo.xm.com.xmfunsdkdemo.ui.device.add.list.presenter.DevListConnectPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.add.share.view.DevShareManageActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.share.view.ShareDevListActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.view.DevAlarmMsgActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.cloud.view.CloudStateActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.interdevlinkage.view.InterDevLinkageActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.shadow.view.DevShadowConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.simpleconfig.view.DevSimpleConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.view.DevMonitorActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.push.view.DevPushActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.record.view.DevRecordActivity;
import demo.xm.com.xmfunsdkdemo.ui.widget.DividerItemDecoration;
import io.reactivex.annotations.Nullable;

import static com.manager.account.share.ShareInfo.SHARE_NOT_YET_ACCEPT;
import static com.manager.db.Define.LOGIN_BY_LOCAL;
import static com.xm.ui.dialog.PasswordErrorDlg.INPUT_TYPE_DEV_USER_PWD;


/**
 * 设备界面,显示相关的列表菜单
 * Device interface, showing the relevant list menu
 * Created by jiangping on 2018-10-23.
 */
public class DevListActivity extends DemoBaseActivity<DevListConnectPresenter>
        implements DevListConnectContract.IDevListConnectView,
        XTitleBar.OnRightClickListener,
        DevListAdapter.OnItemDevClickListener {

    private RecyclerView listView;
    private DevListAdapter adapter;
    private SwipeRefreshLayout slRefresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_list);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.set_list));
        titleBar.setRightTitleText(getString(R.string.share_dev_list));
        titleBar.setBottomTip(DevListActivity.class.getName());
        titleBar.setLeftClick(this);
        titleBar.setRightIvClick(this);
        titleBar.setRightTvClick(this);
        //如果不是账号登录，需要隐藏分享功能改成批量删除设备功能
        if (!DevDataCenter.getInstance().isLoginByAccount()) {
            titleBar.setRightTitleText(getString(R.string.clear_dev_list));
        }

        listView = findViewById(R.id.listViewDevice);
        LinearLayoutManager llManager = new LinearLayoutManager(this);
        listView.setLayoutManager(llManager);
        listView.addItemDecoration(new DividerItemDecoration(this, llManager.getOrientation()));
        slRefresh = findViewById(R.id.sl_refresh);
        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.updateDevState();
                slRefresh.setRefreshing(true);
            }
        });
        refreshTitle();
    }

    private void initData() {
        showWaitDialog();
        adapter = new DevListAdapter(getApplication(), listView, (ArrayList<HashMap<String, Object>>) presenter.getDevList(), this);
        listView.setAdapter(adapter);
        presenter.updateDevState();//Update the status of the list
    }


    @Override
    public DevListConnectPresenter getPresenter() {
        return new DevListConnectPresenter(this);
    }

    private void refreshTitle() {
        if (null != titleBar) {
            if (DevDataCenter.getInstance().isLoginByAccount()) {
                titleBar.setTitleText(String.format(getString(R.string.user_device_list), DevDataCenter.getInstance().getAccountUserName()));
            } else {
                int loginType = DevDataCenter.getInstance().getLoginType();
                if (loginType == LOGIN_BY_LOCAL) {
                    titleBar.setTitleText(String.format(getString(R.string.user_device_list), getString(R.string.login_by_local)));
                }
            }
        }
    }

    @Override
    public void onRightClick() {
        if (DevDataCenter.getInstance().isLoginByAccount()) {
            turnToActivity(ShareDevListActivity.class);
        } else {
            XMPromptDlg.onShow(this, getString(R.string.is_sure_delete_all_devices), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.deleteAllDevs();
                }
            }, null);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (adapter != null) {
            adapter.setData((ArrayList<HashMap<String, Object>>) presenter.getDevList());
        }
    }

    @Override
    public void onUpdateDevListView() {
        adapter.setData((ArrayList<HashMap<String, Object>>) presenter.getDevList());
    }

    @Override
    public void onUpdateDevStateResult(boolean isSuccess) {//Repeated the walk many times
        hideWaitDialog();
        if (isSuccess) {
            adapter.setData((ArrayList<HashMap<String, Object>>) presenter.getDevList());
        } else {
            showToast(getString(R.string.Refresh_Dev_Status_F), Toast.LENGTH_LONG);
        }
        slRefresh.setRefreshing(false);//Cancel refresh
    }

    @Override
    public void onModifyDevNameFromServerResult(boolean isSuccess) {
        hideWaitDialog();
        if (isSuccess) {
            showToast(getString(R.string.TR_Modify_Dev_Name_S), Toast.LENGTH_LONG);
            adapter.setData((ArrayList<HashMap<String, Object>>) presenter.getDevList());
        } else {
            showToast(getString(R.string.TR_Modify_Dev_Name_F), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onDeleteDevResult(boolean isSuccess) {
        hideWaitDialog();
        adapter.setData((ArrayList<HashMap<String, Object>>) presenter.getDevList());
        if (isSuccess) {
            showToast(getString(R.string.delete_s), Toast.LENGTH_LONG);
        } else {
            showToast(getString(R.string.delete_f), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onAcceptDevResult(boolean isSuccess) {
        hideWaitDialog();
        adapter.setData((ArrayList<HashMap<String, Object>>) presenter.getDevList());
        if (isSuccess) {
            showToast(getString(R.string.accept_share_s), Toast.LENGTH_LONG);
        } else {
            showToast(getString(R.string.accept_share_f), Toast.LENGTH_LONG);
        }
    }

    /**
     * 获取通道列表回调
     * get the channel list callback
     *
     * @param isSuccess
     * @param resultId
     */
    @Override
    public void onGetChannelListResult(boolean isSuccess, int resultId) {
        hideWaitDialog();
        if (isSuccess) {
            //如果返回的数据是通道数并且大于1就跳转到通道列表
            /*If the number of channels returned is greater than 1, jump to the list of channels*/
            if (resultId > 1) {
                turnToActivity(ChannelListActivity.class);
            } else {
                turnToActivity(DevMonitorActivity.class);
            }
        } else {
            if (resultId == EFUN_ERROR.EE_DVR_PASSWORD_NOT_VALID) {
                XMDevInfo devInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
                XMPromptDlg.onShowPasswordErrorDialog(this, devInfo.getSdbDevInfo(),
                        0, new PwdErrorManager.OnRepeatSendMsgListener() {
                            @Override
                            public void onSendMsg(int msgId) {
                                showWaitDialog();
                                presenter.getChannelList();
                            }
                        }, false);
            }  else if (resultId == EFUN_ERROR.EE_DVR_LOGIN_USER_NOEXIST) {
                XMDevInfo devInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
                XMPromptDlg.onShowPasswordErrorDialog(this, devInfo.getSdbDevInfo(),
                        0,getString(R.string.input_username_password),INPUT_TYPE_DEV_USER_PWD, true,new PwdErrorManager.OnRepeatSendMsgListener() {
                            @Override
                            public void onSendMsg(int msgId) {
                                showWaitDialog();
                                presenter.getChannelList();
                            }
                        }, false);
            } else if (resultId < 0) {
                showToast(getString(R.string.login_dev_failed) + resultId, Toast.LENGTH_LONG);
                turnToActivity(DevMonitorActivity.class);
            }
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onItemClick(int position, XMDevInfo xmDevInfo) {
        if (xmDevInfo.isShareDev()) {
            OtherShareDevUserBean otherShareDevUserBean = xmDevInfo.getOtherShareDevUserBean();
            if (otherShareDevUserBean != null) {
                int iShareState = otherShareDevUserBean.getShareState();
                if (iShareState == SHARE_NOT_YET_ACCEPT) {
                    XMPromptDlg.onShow(getContext(), getString(R.string.is_accept_share_dev), getString(R.string.reject_share), getString(R.string.accept_share), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            presenter.rejectShare(otherShareDevUserBean);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            presenter.acceptShare(otherShareDevUserBean);
                        }
                    });
                    return;
                }
            }
        }

        if (xmDevInfo.getDevState() != XMDevInfo.OFF_LINE) {
            if (xmDevInfo.getDevState() == XMDevInfo.SLEEP_UNWAKE) {
                showToast(getString(R.string.dev_unwake), Toast.LENGTH_LONG);
                presenter.setDevId(xmDevInfo.getDevId());
                turnToActivity(DevShadowConfigActivity.class);
                return;
            }

            showWaitDialog(getString(R.string.get_channel_info));
            String devId = presenter.getDevId(position);
            presenter.setDevId(devId);

            //低功耗设备不需要获取通道列表，直接跳转到预览页面
            /*Low power devices do not need to get the list of channels and jump directly to the preview page*/
            if (DevDataCenter.getInstance().isLowPowerDev(xmDevInfo.getDevType())) {
                turnToActivity(DevMonitorActivity.class);
            } else {
                presenter.getChannelList();
            }
        } else {
            showToast(FunSDK.TS(getString(R.string.dev_offline)), Toast.LENGTH_LONG);
            presenter.setDevId(xmDevInfo.getDevId());
            turnToActivity(DevShadowConfigActivity.class);
        }


    }

    @Override
    public boolean onLongItemClick(final int position, XMDevInfo xmDevInfo) {
        XMPromptDlg.onShow(this, getString(R.string.is_sure_delete_dev), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                presenter.deleteDev(position);
            }
        }, null);
        return false;
    }

    /**
     * 跳转到报警消息
     * jump to alarm message
     *
     * @param position
     */
    @Override
    public void onTurnToAlarmMsg(int position) {// This is push messaging
        String devId = presenter.getDevId(position);
        presenter.setDevId(devId);
        turnToActivity(DevAlarmMsgActivity.class);
    }

    /**
     * 跳转到云服务
     * jump to alarm message
     *
     * @param position
     */
    @Override
    public void onTurnToCloudService(int position) {
        String devId = presenter.getDevId(position);
        presenter.setDevId(devId);
        turnToActivity(CloudStateActivity.class);
    }

    /**
     * 跳转到推送设置
     * Jump to push Settings
     *
     * @param position
     */
    @Override
    public void onTurnToPushSet(int position) {
        String devId = presenter.getDevId(position);
        presenter.setDevId(devId);
        turnToActivity(DevPushActivity.class);
    }

    @Override
    public void onModifyDevName(int position, XMDevInfo xmDevInfo) {
        XMPromptDlg.onShowEditDialog(this, getString(R.string.modify_dev_name), xmDevInfo.getDevName(), new EditDialog.OnEditContentListener() {
            @Override
            public void onResult(String devName) {
                presenter.modifyDevNameFromServer(position, devName);
            }
        });
    }

    @Override
    public void onShareDevManage(int position, XMDevInfo xmDevInfo) {
        presenter.setDevId(xmDevInfo.getDevId());
        turnToActivity(DevShareManageActivity.class);
    }

    /**
     * 跳转到设备本地用户名和密码页面
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onTurnToEditLocalDevUserPwd(int position, XMDevInfo xmDevInfo) {
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_local_dev_user_pwd, null);
        TextView tvDevId = layout.findViewById(R.id.tv_devid);
        tvDevId.setText(xmDevInfo.getDevId());
        EditText etDevUser = layout.findViewById(R.id.et_local_dev_user);
        etDevUser.setText(xmDevInfo.getDevUserName());
        EditText etDevPwd = layout.findViewById(R.id.et_local_dev_pwd);
        etDevPwd.setText(xmDevInfo.getDevPassword());

        Dialog dialog = XMPromptDlg.onShow(this, layout,
                (int) (XUtils.getScreenWidth(this) * 0.8), (int) (XUtils.getScreenHeight(this) * 0.6), false, null);

        dialog.show();

        Button btnOk = layout.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String devUserName = etDevUser.getText().toString().trim();
                String devPwd = etDevPwd.getText().toString().trim();
                presenter.editLocalDevUserPwd(
                        position,
                        xmDevInfo.getDevId(),
                        devUserName,
                        devPwd);
                dialog.dismiss();
            }
        });

        Button btnCancel = layout.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 唤醒设备并开锁
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onWakeUpDev(int position, XMDevInfo xmDevInfo) {
        presenter.wakeUpDev(position, xmDevInfo.getDevId());
    }

    /**
     * 跳转到SD卡录像回放页面
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onTurnToSdPlayback(int position, XMDevInfo xmDevInfo) {
        presenter.setDevId(xmDevInfo.getDevId());
        turnToActivity(DevRecordActivity.class);
    }

    /**
     * 跳转到设备之间联动
     *
     * @param position
     * @param xmDevInfo
     * @param bundle
     */
    @Override
    public void onTurnToInterDevLinkage(int position, XMDevInfo xmDevInfo, Bundle bundle) {
        presenter.setDevId(xmDevInfo.getDevId());
        turnToActivity(InterDevLinkageActivity.class, "data", bundle);
    }

    /**
     * 跳转到设备能力集页面
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onTurnToDevAbility(int position, XMDevInfo xmDevInfo) {
        presenter.setDevId(xmDevInfo.getDevId());
        turnToActivity(XMDevAbilityActivity.class);
    }

    @Override
    public void onPingTest(int position, XMDevInfo xmDevInfo) {
        turnToActivity(DevSimpleConfigActivity.class, new Object[][]{{"devId", xmDevInfo.getDevId()},{"jsonName", "Ping"}, {"configName", "Ping"}, {"cmdId", 1052},{"jsonData","{\n" +
                "    \"Ping\": {\n" +
                "        \"URL\": \"\",\n" +
                "        \"Num\": 10,\n" +
                "        \"Timeout\": 5\n" +
                "    },\n" +
                "    \"Name\": \"Ping\"\n" +
                "}"}});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.release();
        }

        presenter.clear();
    }
}
