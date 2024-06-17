package demo.xm.com.xmfunsdkdemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xm.activity.base.XMBaseActivity;
import com.xm.activity.device.devset.ability.view.XMDevAbilityActivity;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;

import java.util.ArrayList;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.adapter.DevConfigAdapter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.about.view.DevAboutActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.advance.view.DevAdvanceActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.alarmconfig.view.DevAlarmSetActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.cameralink.view.CameraLinkSetActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.decodeconfig.view.DevDecodeSetActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.devicestore.view.DevSetupStorageActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.door.view.DoorSettingActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.filetransfer.view.FileTransferActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.idr.view.IDRSettingActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.imageconfig.view.DevCameraSetActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.view.IntelligentVigilanceActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.pwdmodify.view.DevModifyPwdActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.serialport.view.SerialPortActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.shadow.view.DevShadowConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.simpleconfig.view.DevSimpleConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.videoconfig.view.DevRecordSetActivity;
import demo.xm.com.xmfunsdkdemo.ui.vm.DevConfigViewModel;
import io.reactivex.annotations.Nullable;

/**
 * 设备预览界面中的配置选项列表,包括编码配置,报警配置,录像配置,图像配置,高级配置,鱼眼灯泡控制,设备存储管理,
 * 密码修改,系统功能列表(仅Demo使用),Front Panel Operate,报警中心,GB配置,Json和DevCmd调试,关于设备.
 * Configuration options list in the device preview interface,including encoding configuration,
 * alarm configuration, video configuration, image configuration,advanced configuration,
 * fisheye bulb control, device storage management,Password change, list of system functions (Demo only),
 * Front Panel Operate, Alarm Center,GB configuration,Json and DevCmd debug, about the device.
 * <p>
 * 以下因还未实现，所以暂时没有设置，不过相关代码类已经创建好（双击shift搜索均能搜到），在此记录一下，以供以后如果开发参考。
 * 高级配置：DevAdvanceActivity
 * 鱼眼灯泡控制：DeviceConfigActivity
 * Front Panel Operate:DevFrontSetActivity
 * 报警中心：DevAlarmCenterActivity
 * GB配置：DevGbSetActivity
 * Json和DevCmd调试：DevJsonCmdActivity
 */
public class DeviceConfigActivity extends DemoBaseActivity<DeviceConfigPresenter> implements DeviceConfigContract.IDeviceConfigView {

    private final List<DevConfigViewModel> deviceList = new ArrayList<>();

    @Override
    public DeviceConfigPresenter getPresenter() {
        return new DeviceConfigPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_config);
        initDevices();
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setLeftClick(this);
        titleBar.setTitleText(getString(R.string.device_setup));
        titleBar.setBottomTip(getClass().getName());

        DevConfigAdapter adapter = new DevConfigAdapter(DeviceConfigActivity.this, R.layout.layout_guide_list_item, deviceList);
        ListView listView = findViewById(R.id.list_device);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = null;
            switch (i) {
                case 0:
                    intent = new Intent(view.getContext(), DevSimpleConfigActivity.class);
                    break;
                //编码配置
                case 1:
                    intent = new Intent(view.getContext(), DevDecodeSetActivity.class);
                    break;
                //报警配置
                case 2:
                    intent = new Intent(view.getContext(), DevAlarmSetActivity.class);
                    break;
                //录像设置
                case 3:
                    intent = new Intent(view.getContext(), DevRecordSetActivity.class);
                    break;
                //图像设置
                case 4:
                    intent = new Intent(view.getContext(), DevCameraSetActivity.class);
                    break;
                //录像存储功能
                case 5:
                    intent = new Intent(view.getContext(), DevSetupStorageActivity.class);
                    break;
                //修改设备密码
                case 6:
                    intent = new Intent(view.getContext(), DevModifyPwdActivity.class);
                    break;
                //设备能力集
                case 7:
                    intent = new Intent(DeviceConfigActivity.this, XMDevAbilityActivity.class);
                    break;
                case 8://串口透传
                    intent = new Intent(view.getContext(), SerialPortActivity.class);
                    break;
                // 门锁配置
                case 9:
                    intent = new Intent(view.getContext(), DoorSettingActivity.class);
                    break;
                case 10:
                    //文件传输
                    intent = new Intent(view.getContext(), FileTransferActivity.class);
                    break;
                case 11:// 低功耗设备配置
                    intent = new Intent(view.getContext(), IDRSettingActivity.class);
                    break;
                case 12:// 高级配置
                    intent = new Intent(view.getContext(), DevAdvanceActivity.class);
                    break;
                case 13://关于设备
                    XMPromptDlg.onShow(this, getString(R.string.is_multi_module_upgrade), getString(R.string.mcu), getString(R.string.main_control), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(view.getContext(), DevAboutActivity.class);
                            intent.putExtra("firmwareType", "Mcu");
                            intent.putExtra("devId", presenter.getDevId());
                            startActivity(intent);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(view.getContext(), DevAboutActivity.class);
                            intent.putExtra("firmwareType", "System");
                            intent.putExtra("devId", presenter.getDevId());
                            startActivity(intent);
                        }
                    });
                    break;
                case 14:
                    XMPromptDlg.onShow(DeviceConfigActivity.this,
                            getResources().getString(R.string.device_reboot_context), v -> presenter.rebootDev(), null);
                    break;
                case 15://影子服务配置
                    intent = new Intent(view.getContext(), DevShadowConfigActivity.class);
                    break;
                default:
                    break;
            }

            if (intent != null) {
                intent.putExtra("devId", presenter.getDevId());
                startActivity(intent);
            }
        });
    }

    private void initDevices() {

        DevConfigViewModel item0 = new DevConfigViewModel(R.string.device_setup_simple, R.string.device_setup_hint_simple);
        deviceList.add(item0);

        //编码配置
        DevConfigViewModel item1 = new DevConfigViewModel(R.string.device_setup_encode, R.string.device_setup_hint_encode_config_alarm);
        deviceList.add(item1);

        //报警配置
        DevConfigViewModel item2 = new DevConfigViewModel(R.string.device_opt_alarm_config, R.string.device_setup_hint_alarm_config_alarm);
        deviceList.add(item2);

        //录像设置
        DevConfigViewModel item3 = new DevConfigViewModel(R.string.device_setup_record, R.string.device_setup_hint_record_config_alarm);
        deviceList.add(item3);

        //图像设置
        DevConfigViewModel item4 = new DevConfigViewModel(R.string.device_setup_image, R.string.device_setup_hint_picture_config_alarm);
        deviceList.add(item4);

        //录像存储功能
        DevConfigViewModel item5 = new DevConfigViewModel(R.string.device_setup_storage, R.string.device_setup_hint_harddisk_config_alarm);
        deviceList.add(item5);

        //修改设备密码
        DevConfigViewModel item6 = new DevConfigViewModel(R.string.device_setup_change_password, R.string.device_setup_hint_pwd_modify_alarm);
        deviceList.add(item6);

        //设备能力集
        DevConfigViewModel item7 = new DevConfigViewModel(R.string.device_setup_system_function, -1);
        deviceList.add(item7);

        //串口透传
        DevConfigViewModel item8 = new DevConfigViewModel(R.string.device_opt_transmission, R.string.device_setup_hint_about_dev_alarm);
        deviceList.add(item8);

        // 门锁配置
        DevConfigViewModel item9 = new DevConfigViewModel(R.string.door_setting, -1);
        deviceList.add(item9);

        //文件传输
        DevConfigViewModel item10 = new DevConfigViewModel(R.string.file_transfer, -1);
        deviceList.add(item10);

        // 低功耗设备配置
        DevConfigViewModel item11 = new DevConfigViewModel(R.string.idr_setting, -1);
        deviceList.add(item11);

        // 高级配置
        DevConfigViewModel item12 = new DevConfigViewModel(R.string.device_setup_expert, -1);
        deviceList.add(item12);

        //关于设备
        DevConfigViewModel item13 = new DevConfigViewModel(R.string.device_system_info, R.string.about_dev_tips);
        deviceList.add(item13);

        //重启设备
        DevConfigViewModel item14 = new DevConfigViewModel(R.string.device_reboot, -1);
        deviceList.add(item14);

        //影子服务
        DevConfigViewModel item15 = new DevConfigViewModel(R.string.shadow_config, -1);
        deviceList.add(item15);
    }

    @Override
    public void onUpdateView() {

    }

}
