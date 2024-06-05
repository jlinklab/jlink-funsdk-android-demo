package demo.xm.com.xmfunsdkdemo.ui.device.config.door.view;

import android.os.Bundle;
import android.view.View;

import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.listselectitem.extra.view.ExtraSpinner;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.door.contract.DoorSettingContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.door.presenter.DoorSettingPresenter;

/**
 * 门锁相关配置
 * Door lock configuration
 * @author hws
 * @class
 * @time 2020/8/14 13:31
 */
public class DoorSettingActivity extends BaseConfigActivity<DoorSettingPresenter> implements DoorSettingContract.IDoorSettingView {
    private ListSelectItem lsiContact;
    private ListSelectItem lsiAutoSleep;
    private ListSelectItem lsiFlip;
    private ExtraSpinner spAutoSleep;
    private ExtraSpinner spFlip;
    @Override
    public DoorSettingPresenter getPresenter() {
        return new DoorSettingPresenter(this) ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_setting);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        lsiContact = findViewById(R.id.lsi_door_setting_contact);
        lsiAutoSleep = findViewById(R.id.lsi_door_setting_auto_sleep);
        lsiFlip = findViewById(R.id.lsi_door_setting_video_flip);

        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });

        titleBar.setTitleText(getString(R.string.door_setting));
        titleBar.setBottomTip(getClass().getName());
    }

    private void initSleepView() {
        lsiAutoSleep.setVisibility(View.VISIBLE);
        spAutoSleep = lsiAutoSleep.getExtraSpinner();
        spAutoSleep.initData(new String[] {"15","30"},new Integer[]{15,30});
        spAutoSleep.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
            @Override
            public void onItemClick(int position, String key, Object value) {
                lsiAutoSleep.toggleExtraView(true);
                lsiAutoSleep.setRightText(key);
                presenter.saveManageShutDown((Integer) value);
            }
        });

    }

    private void initFlip() {
        lsiFlip.setVisibility(View.VISIBLE);
        spFlip = lsiFlip.getExtraSpinner();
        spFlip.initData(new String[] {
                getString(R.string.video_flip_normal),
                getString(R.string.video_flip_90),
                getString(R.string.video_flip_180),
                getString(R.string.video_flip_270)},new Integer[]{0,1,2,3});
        spFlip.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
            @Override
            public void onItemClick(int position, String key, Object value) {
                lsiFlip.toggleExtraView(true);
                lsiFlip.setRightText(key);
                presenter.saveCorridorMode((Integer) value);
            }
        });
    }

    private void initListener() {
        lsiContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToActivity(DoorSettingContactActivity.class);
            }
        });

        lsiAutoSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lsiAutoSleep.toggleExtraView();
            }
        });

        lsiFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lsiFlip.toggleExtraView();
            }
        });
    }

    private void initData() {
        showWaitDialog();
        presenter.updateCorridorMode();
        presenter.updateManageShutDown();
    }

    @Override
    public void onUpdateManageShutDownResult(boolean isSuccess,int sleepTime) {
        hideWaitDialog();
        if (isSuccess) {
            initSleepView();
            spAutoSleep.setValue(sleepTime);
            lsiAutoSleep.setRightText(spAutoSleep.getSelectedName());
        }
    }

    @Override
    public void onUpdateCorridorModeResult(boolean isSuccess, int mode) {
        hideWaitDialog();
        if (isSuccess) {
            initFlip();
            spFlip.setValue(mode);
            lsiFlip.setRightText(spFlip.getSelectedName());
        }
    }
}
