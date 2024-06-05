package demo.xm.com.xmfunsdkdemo.ui.device.config.devicestore.view;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.FunSDK;
import com.lib.sdk.bean.GeneralInfoBean;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.StorageInfoBean;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.devicestore.listener.DevSetupStorageContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.devicestore.presenter.DevSetupStoragePresenter;
import io.reactivex.annotations.Nullable;

/**
 * 设备存储管理界面,包括录像分区及图片分区的存储容量,剩余容量,录像满时停止或是循环录像.
 */
public class DevSetupStorageActivity extends BaseConfigActivity<DevSetupStoragePresenter>
        implements DevSetupStorageContract.IDevSetupStorageView {

    /**
     * 两个作用：1.获取：处理设备存储管理的json数据，并显示到控件上。2.保存：根据控件信息生成json格式数据以用来保存
     */

    /**
     * 总容量
     */
    private TextView tvMemoryTotal;
    /**
     * 剩余容量
     */
    private TextView tvMemoryRemain;
    /**
     * 图片分区总容量
     */
    private TextView tvMemoryPicPart;
    /**
     * 视频分区总容量
     */
    private TextView tvMemoryVideoPart;
    /**
     * 格式化
     */
    private Button btnFormat;

    /**
     * 存储满时配置项
     */
    private RadioGroup rgRecordModeWhileFull;
    /**
     * 图片分区View
     */
    private View rlMemoryPicPart;
    @Override
    public DevSetupStoragePresenter getPresenter() {
        return new DevSetupStoragePresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_setup_storage);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_setup_storage));
        titleBar.setLeftClick(this);
        tvMemoryTotal = findViewById(R.id.tv_dev_set_memory_total);
        tvMemoryRemain = findViewById(R.id.tv_dev_set_memory_remain);
        tvMemoryPicPart = findViewById(R.id.tv_sto_img_part);
        tvMemoryVideoPart = findViewById(R.id.tv_sto_video_part);
        btnFormat = findViewById(R.id.btn_dev_set_format_storage);
        rlMemoryPicPart = findViewById(R.id.rl_sto_img_part);
        rgRecordModeWhileFull = findViewById(R.id.rg_record_mode_while_full);
        btnFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XMPromptDlg.onShow(DevSetupStorageActivity.this, String.valueOf(R.string.device_setup_storage_format_tip), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showWaitDialog();
                        //格式化
                        presenter.formatStorage();
                    }
                },null);
            }
        });

    }

    private void initData() {
        showWaitDialog();
        //获取存储容量信息
        presenter.getStorageInfo();
        //获取存储配置
        presenter.getStorageConfig();
    }


    /**
     * 获取存储容量信息成功
     */
    @Override
    public void getStorageDataSuccess(String totalSizeString, String remainSizeString, String videoPartSizeString, String picPartSizeString,boolean isShowPicPart) {
        hideWaitDialog();
        tvMemoryTotal.setText(totalSizeString);
        tvMemoryRemain.setText(remainSizeString);
        tvMemoryVideoPart.setText(videoPartSizeString);
        tvMemoryPicPart.setText(picPartSizeString);
        if(isShowPicPart){
            rlMemoryPicPart.setVisibility(View.VISIBLE);
        } else {
            rlMemoryPicPart.setVisibility(View.GONE);
        }
    }

    /**
     * 获取存储容量信息失败
     */
    @Override
    public void getStorageDataError(String errorString) {
        hideWaitDialog();
        showToast(errorString,Toast.LENGTH_LONG);
    }

    /**
     * 格式化结果
     */
    @Override
    public void onFormatResult(boolean isSuccess) {
        hideWaitDialog();
        showToast(isSuccess ? String.valueOf(R.string.device_setup_storage_format_success) : String.valueOf(R.string.device_setup_storage_format_failed),
                Toast.LENGTH_LONG);
        finish();
    }
    /**
     * 获取存储配置结果
     */
    @Override
    public void getStorageConfigResult(GeneralInfoBean generalInfoBean){
        if(generalInfoBean==null || generalInfoBean.OverWrite==null){
            rgRecordModeWhileFull.setVisibility(View.GONE);
        } else {
            rgRecordModeWhileFull.setVisibility(View.VISIBLE);
            if (generalInfoBean.OverWrite.equals("StopRecord")) {
                //存储满时：停止录像
                rgRecordModeWhileFull.check(R.id.rb_sto_video_stop);
            } else if (generalInfoBean.OverWrite.equals("OverWrite")) {
                //存储满时：循环录像
                rgRecordModeWhileFull.check(R.id.rb_sto_video_cir);
            }
        }
        rgRecordModeWhileFull.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sto_video_stop:
                        //存储满时：停止录像
                        showWaitDialog();
                        presenter.changeVideoFullState(true);
                        break;
                    case R.id.rb_sto_video_cir:
                        //存储满时：循环录像
                        showWaitDialog();
                        presenter.changeVideoFullState(false);
                        break;
                    default:
                        break;

                }
            }
        });
    }

    /**
     * 修改存储满时配置结果
     */
    @Override
    public void changeVideoFullStateResult(boolean isSuccess) {
        hideWaitDialog();
        if(isSuccess){
            showToast(FunSDK.TS("Save_Success"),Toast.LENGTH_LONG);
        } else {
            showToast(FunSDK.TS("Save_Failed"),Toast.LENGTH_LONG);
        }

    }
}
