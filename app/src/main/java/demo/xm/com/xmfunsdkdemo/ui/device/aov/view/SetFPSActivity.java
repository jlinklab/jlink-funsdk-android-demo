package demo.xm.com.xmfunsdkdemo.ui.device.aov.view;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.aov.listener.SetFPSContract;
import demo.xm.com.xmfunsdkdemo.ui.device.aov.presenter.SetFPSPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
/**
 * AOV设备设置fps、录像延迟、报警间隔
 */
public class SetFPSActivity extends BaseConfigActivity<SetFPSPresenter> implements
        SetFPSContract.ISetFPSView, View.OnClickListener{
    public static final String MODE_FPS = "fps";
    public static final String MODE_RECORD_LATCH = "recordLatch";
    public static final String MODE_ALARM_TIME_INTERVAL = "alarmTimeInterval";
    private RecyclerView rlFPS;

    private BaseQuickAdapter adapter;
    private TextView tvTips;
    private String devId;


    @Override
    public SetFPSPresenter getPresenter() {
        return new SetFPSPresenter(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_fps_layout);
        initView();
        initData();

    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        titleBar.setBottomTip(getClass().getName());
        tvTips = findViewById(R.id.tvTips);
        rlFPS = findViewById(R.id.rlFps);
        rlFPS.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initData() {
        showWaitDialog();
        devId = getIntent().getStringExtra("devId");
        presenter.setDevId(devId);
        if (getIntent() != null) {
            presenter.setCurFps(getIntent().getStringExtra("fps"));
            presenter.setMode(getIntent().getStringExtra("mode"));
            presenter.setCurRecordLatch(getIntent().getIntExtra("recordLatch", 0));
            presenter.setCurInterval(getIntent().getIntExtra("alarmTimeInterval",0));
        }
        if (StringUtils.contrast(presenter.getMode(), SetFPSActivity.MODE_FPS)) {
            titleBar.setTitleText(FunSDK.TS("TR_AOV_Fps"));
            adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.fps_select_item_layout, presenter.getFpsList()) {
                @Override
                protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
                    TextView textView = baseViewHolder.findView(R.id.tvTitle);
                    ImageView imageView = baseViewHolder.findView(R.id.ivSelect);
                    textView.setText(s+" fps");
                    Drawable drawable;
                    if (StringUtils.contrast(presenter.getCurFps(), s)) {
                        drawable = getDrawable(R.drawable.check_pre);
                        imageView.setImageDrawable(drawable);
                    } else {
                        drawable = getDrawable(R.drawable.check_nor);
                        imageView.setImageDrawable(drawable);
                    }
                    if (presenter.getFpsList().indexOf(s) == presenter.getFpsList().size() - 1) {
                        baseViewHolder.findView(R.id.spilt_view).setVisibility(View.GONE);
                    } else {
                        baseViewHolder.findView(R.id.spilt_view).setVisibility(View.VISIBLE);
                    }
                    baseViewHolder.findView(R.id.xm_root_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            presenter.setCurFps(s);
                            adapter.notifyDataSetChanged();
                            Intent intent = new Intent();
                            intent.putExtra("mode", presenter.getMode());
                            intent.putExtra(MODE_FPS, presenter.getCurFps());
                            setResult(1, intent);
                            finish();
                        }
                    });
                }
            };
            if(presenter.getCurFps().contains("/")){
                String[] split = presenter.getCurFps().split("/");
                if(split.length >= 2){
                    tvTips.setText(presenter.getCurFps()+"fps,"+String.format(FunSDK.TS("TR_Set_Aov_Fps_Tips"),split[1],split[0]));
                    tvTips.setVisibility(View.VISIBLE);
                }

            }else {
                tvTips.setText(presenter.getCurFps()+"fps,"+String.format(FunSDK.TS("TR_Set_Aov_Fps_Tips"),1,presenter.getCurFps()));
                tvTips.setVisibility(View.VISIBLE);
            }


        } else if (StringUtils.contrast(presenter.getMode(), MODE_RECORD_LATCH)) {
            titleBar.setTitleText(FunSDK.TS("TR_Setting_Event_Record_Delay"));
            adapter = new BaseQuickAdapter<Integer, BaseViewHolder>(R.layout.fps_select_item_layout, presenter.getRecordLatchList()) {
                @Override
                protected void convert(@NonNull BaseViewHolder baseViewHolder, Integer s) {
                    TextView textView = baseViewHolder.findView(R.id.tvTitle);
                    ImageView imageView = baseViewHolder.findView(R.id.ivSelect);
                    textView.setText(s.toString() +"s");
                    Drawable drawable;
                    if (presenter.getCurRecordLatch() == s.intValue()) {
                        drawable = getDrawable(R.drawable.check_pre);
                        imageView.setImageDrawable(drawable);
                    } else {
                        drawable = getDrawable(R.drawable.check_nor);
                        imageView.setImageDrawable(drawable);
                    }
                    if (presenter.getRecordLatchList().indexOf(s) == presenter.getRecordLatchList().size() - 1) {
                        baseViewHolder.findView(R.id.spilt_view).setVisibility(View.GONE);
                    } else {
                        baseViewHolder.findView(R.id.spilt_view).setVisibility(View.VISIBLE);
                    }
                    baseViewHolder.findView(R.id.xm_root_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            presenter.setCurRecordLatch(s);
                            adapter.notifyDataSetChanged();
                            Intent intent = new Intent();
                            intent.putExtra("mode", presenter.getMode());
                            intent.putExtra(MODE_RECORD_LATCH, presenter.getCurRecordLatch());
                            setResult(1, intent);
                            finish();
                        }
                    });
                }
            };
            tvTips.setVisibility(View.GONE);
        } else if (StringUtils.contrast(presenter.getMode(),MODE_ALARM_TIME_INTERVAL)) {
            titleBar.setTitleText(FunSDK.TS("TR_AOV_Alarm_interval"));
            adapter = new BaseQuickAdapter<Integer, BaseViewHolder>(R.layout.fps_select_item_layout, presenter.getAovAlarmTimeIntervalList()) {
                @Override
                protected void convert(@NonNull BaseViewHolder baseViewHolder, Integer s) {
                    TextView textView = baseViewHolder.findView(R.id.tvTitle);
                    ImageView imageView = baseViewHolder.findView(R.id.ivSelect);
                    if(s == 0){
                        textView.setText(FunSDK.TS("TR_Smart_PowerReal"));
                    }else {
                        textView.setText(s.toString() +"s");
                    }
                    Drawable drawable;
                    if (presenter.getCurInterval() == s.intValue()) {
                        drawable = getDrawable(R.drawable.check_pre);
                        imageView.setImageDrawable(drawable);
                    } else {
                        drawable = getDrawable(R.drawable.check_nor);
                        imageView.setImageDrawable(drawable);
                    }
                    if (presenter.getAovAlarmTimeIntervalList().indexOf(s) == presenter.getAovAlarmTimeIntervalList().size() - 1) {
                        baseViewHolder.findView(R.id.spilt_view).setVisibility(View.GONE);
                    } else {
                        baseViewHolder.findView(R.id.spilt_view).setVisibility(View.VISIBLE);
                    }
                    baseViewHolder.findView(R.id.xm_root_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            presenter.setCurInterval(s);
                            adapter.notifyDataSetChanged();
                            Intent intent = new Intent();
                            intent.putExtra("mode", presenter.getMode());
                            intent.putExtra(MODE_ALARM_TIME_INTERVAL, presenter.getCurInterval());
                            setResult(1, intent);
                            finish();
                        }
                    });
                }
            };

        }
        rlFPS.setAdapter(adapter);
        presenter.getAovAbility();
    }


    @Override
    public void onShowWaitDialog() {
        showWaitDialog();
    }

    @Override
    public void onHideWaitDialog() {
        hideWaitDialog();
    }

    @Override
    public void showAovAbility() {
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public Activity getActivity() {
        return SetFPSActivity.this;
    }
}
