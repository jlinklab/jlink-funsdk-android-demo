package demo.xm.com.xmfunsdkdemo.ui.device.config.alarmvoice.view;

import static com.manager.account.share.ShareInfo.SHARE_ACCEPT;
import static com.manager.account.share.ShareInfo.SHARE_NOT_YET_ACCEPT;
import static com.manager.account.share.ShareInfo.SHARE_REJECT;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.lib.sdk.bean.VoiceTipBean;
import com.lib.sdk.bean.VoiceTipTypeBean;
import com.lib.sdk.bean.share.MyShareUserInfoBean;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.widget.XTitleBar;

import java.util.ArrayList;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.adapter.ItemSetAdapter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.alarmvoice.listener.AlarmVoiceListContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.alarmvoice.presenter.AlarmVoiceListPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.filetransfer.view.FileTransferActivity;

/**
 * 设备警铃列表
 */
public class AlarmVoiceListActivity extends DemoBaseActivity<AlarmVoiceListPresenter>
        implements AlarmVoiceListContract.IAlarmVoiceListView, ItemSetAdapter.OnItemSetClickListener {
    private static final int CUSTOM_ENUM = 550;//自定义
    private ItemSetAdapter itemSetAdapter;
    private RecyclerView rvVoiceNameList;
    private int selVoiceType;//当前选择的警铃类型Id
    private int selVoiceTypeItem;//当前选择的ItemId
    private int alarmVoiceIntervalTime;//警铃间隔时间
    private List<VoiceTipBean> voiceTipBeanList;
    private ViewGroup alarmVoiceIntervalTimeLayout;//警铃间隔时间布局
    private RadioGroup rpAlarmVoiceType;//警铃报警间隔类型：仅一次或者循环报警

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_alarm_voice_name_list);
        initView();
        initData();
    }

    private void initView() {
        rvVoiceNameList = findViewById(R.id.rv_dev_alarm_voice_name_list);
        rvVoiceNameList.setLayoutManager(new LinearLayoutManager(this));

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.alarm_voice_name_list));
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(getClass().getName());
        titleBar.setRightBtnResource(R.mipmap.icon_save_normal, R.mipmap.icon_save_pressed);
        titleBar.setRightTvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                Intent intent = new Intent();
                intent.putExtra("voiceType", selVoiceType);
                intent.putExtra("alarmVoiceIntervalTime", alarmVoiceIntervalTime);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        alarmVoiceIntervalTimeLayout = findViewById(R.id.ll_alarm_voice_time_setting);
        rpAlarmVoiceType = alarmVoiceIntervalTimeLayout.findViewById(R.id.rg_alarm_voice_time_type);
        rpAlarmVoiceType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.mode_one_time:
                        alarmVoiceIntervalTime = 0;
                        break;
                    case R.id.mode_repeat:
                        alarmVoiceIntervalTime = 5;//这里设置了每隔5秒一次报警，你可以按照需求设置大于0的值，单位是秒
                        break;
                }
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        selVoiceType = intent.getIntExtra("voiceType", 0);
        alarmVoiceIntervalTime = intent.getIntExtra("alarmVoiceIntervalTime", 0);//默认是0，警报仅一次
        itemSetAdapter = new ItemSetAdapter(rvVoiceNameList, this);
        rvVoiceNameList.setAdapter(itemSetAdapter);

        presenter.getConfig();
    }

    @Override
    public AlarmVoiceListPresenter getPresenter() {
        return new AlarmVoiceListPresenter(this);
    }

    @Override
    public void onGetVoiceNameListResult(VoiceTipTypeBean voiceTipTypeBean, int errorId) {
        if (voiceTipTypeBean != null) {
            this.voiceTipBeanList = voiceTipTypeBean.getVoiceTips();
            List<String> data = new ArrayList<>();
            for (int i = 0; i < voiceTipTypeBean.getVoiceTipCount(); ++i) {
                VoiceTipBean voiceTipBean = voiceTipTypeBean.getVoiceTips().get(i);
                if (voiceTipBean != null) {
                    data.add(voiceTipBean.getVoiceText());
                    if (voiceTipBean.getVoiceEnum() == selVoiceType) {
                        selVoiceTypeItem = i;
                    }
                }
            }

            itemSetAdapter.setData(data);

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    itemSetAdapter.initSelItemColor(selVoiceTypeItem, getResources().getColor(R.color.theme_color));
                }
            }, 500);
        } else {
            itemSetAdapter.setData(null);
            ToastUtils.showLong(R.string.not_found);
        }
    }

    /**
     * 是否支持警铃间隔时间设置
     *
     * @param isSupport true支持 false不支持
     */
    @Override
    public void isSupportAlarmVoiceTipIntervalResult(boolean isSupport) {
        if (isSupport) {
            alarmVoiceIntervalTimeLayout.setVisibility(View.VISIBLE);
            if (alarmVoiceIntervalTime > 0) {
                rpAlarmVoiceType.check(R.id.mode_repeat);
            } else {
                rpAlarmVoiceType.check(R.id.mode_one_time);
            }
        } else {
            alarmVoiceIntervalTimeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItem(int position) {
        if (voiceTipBeanList != null && position < voiceTipBeanList.size()) {
            VoiceTipBean voiceTipBean = voiceTipBeanList.get(position);
            selVoiceType = voiceTipBean.getVoiceEnum();
            if (voiceTipBean.getVoiceEnum() == CUSTOM_ENUM) {
                Intent intent = new Intent(AlarmVoiceListActivity.this, FileTransferActivity.class);
                intent.putExtra("devId", presenter.getDevId());
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
