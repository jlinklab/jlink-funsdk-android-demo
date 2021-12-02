package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lib.SDKCONST;
import com.lib.sdk.bean.HumanDetectionBean;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view.AlertSetActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.listener.IntelligentVigilanceContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.presenter.IntelligentVigilancePresenter;
import io.reactivex.annotations.Nullable;

import static com.lib.sdk.bean.HumanDetectionBean.IA_PERIMETER;
import static com.lib.sdk.bean.HumanDetectionBean.IA_TRIPWIRE;
import static com.manager.db.Define.ALERT_AREA_TYPE;
import static com.manager.db.Define.ALERT_lINE_TYPE;

/**
 * 人形检测、智能警戒等
 * @author hws
 * @class describe
 * @time 2020/12/15 18:42
 */
public class IntelligentVigilanceActivity extends BaseConfigActivity<IntelligentVigilancePresenter> implements IntelligentVigilanceContract.IIntelligentVigilanceView {
    @BindView(R.id.lsi_human_detection_switch)
    ListSelectItem lsiSwitch;
    @BindView(R.id.lsi_human_detection_track)
    ListSelectItem lsiTrack;
    @BindView(R.id.lsi_human_detection_line)
    ListSelectItem lsiLine;
    @BindView(R.id.lsi_human_detection_area)
    ListSelectItem lsiArea;
    @BindView(R.id.lsi_human_detection_perimeter)
    ListSelectItem lsiPerimeter;
    @BindView(R.id.ll_human_detection_perimeter)
    LinearLayout llPerimeter;
    @Override
    public IntelligentVigilancePresenter getPresenter() {
        return new IntelligentVigilancePresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human_detection);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.hunman_detect));
        titleBar.setRightBtnResource(R.mipmap.icon_save_normal,R.mipmap.icon_save_pressed);
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });

        titleBar.setRightTvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                showWaitDialog();
                presenter.saveHumanDetect();
            }
        });

        lsiLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lsiLine.setRightImage(SDKCONST.Switch.Open);
                lsiArea.setRightImage(SDKCONST.Switch.Close);
                presenter.setRuleType(IA_TRIPWIRE);

                Intent intent = new Intent(IntelligentVigilanceActivity.this, AlertSetActivity.class);
                intent.putExtra("devId",presenter.getDevId());
                intent.putExtra("HumanDetection",presenter.getHumanDetection());
                intent.putExtra("RuleType", ALERT_lINE_TYPE);
                intent.putExtra("ChannelHumanRuleLimit",presenter.getChannelHumanRuleLimitBean());
                startActivityForResult(intent, ALERT_lINE_TYPE);
            }
        });

        lsiArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lsiArea.setRightImage(SDKCONST.Switch.Open);
                lsiLine.setRightImage(SDKCONST.Switch.Close);
                presenter.setRuleType(IA_PERIMETER);
                Intent intent = new Intent(IntelligentVigilanceActivity.this, AlertSetActivity.class);
                intent.putExtra("devId",presenter.getDevId());
                intent.putExtra("HumanDetection",presenter.getHumanDetection());
                intent.putExtra("RuleType", ALERT_AREA_TYPE);
                intent.putExtra("ChannelHumanRuleLimit",presenter.getChannelHumanRuleLimitBean());
                startActivityForResult(intent, ALERT_AREA_TYPE);
            }
        });

        lsiSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lsiSwitch.setRightImage(lsiSwitch.getRightValue()
                        == SDKCONST.Switch.Open ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
                presenter.setHumanDetectEnable(lsiSwitch.getRightValue() == SDKCONST.Switch.Open);
            }
        });

        lsiTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lsiTrack.setRightImage(lsiTrack.getRightValue()
                        == SDKCONST.Switch.Open ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
                presenter.setShowTrack(lsiTrack.getRightValue() == SDKCONST.Switch.Open);
            }
        });

        lsiPerimeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lsiPerimeter.setRightImage(lsiPerimeter.getRightValue()
                        == SDKCONST.Switch.Open ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
                presenter.setRuleEnable(lsiPerimeter.getRightValue() == SDKCONST.Switch.Open);
            }
        });
    }

    private void initData() {
        showWaitDialog();
        presenter.updateHumanDetectAbility();
    }

    @Override
    public void updateHumanRuleSupportResult(boolean isSupport) {
        if (isSupport) {
            lsiTrack.setVisibility(presenter.isTrackSupport() ? View.VISIBLE : View.GONE);
            lsiLine.setVisibility(presenter.isLineSupport() ? View.VISIBLE : View.GONE);
            lsiArea.setVisibility(presenter.isAreaSupport() ? View.VISIBLE : View.GONE);

            lsiTrack.setRightImage(presenter.isShowTrack() ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
            if (presenter.getRuleType() == IA_TRIPWIRE) {
                lsiLine.setRightImage(SDKCONST.Switch.Open);
                lsiArea.setRightImage(SDKCONST.Switch.Close);
            }else if (presenter.getRuleType() == IA_PERIMETER){
                lsiLine.setRightImage(SDKCONST.Switch.Close);
                lsiArea.setRightImage(SDKCONST.Switch.Open);
            }

            lsiPerimeter.setRightImage(presenter.isRuleEnable() ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
        }else {
            lsiTrack.setVisibility(View.GONE);
            lsiLine.setVisibility(View.GONE);
            lsiArea.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateHumanDetectResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        if (isSuccess) {
            lsiSwitch.setRightImage(presenter.isHumanDetectEnable() ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
        }else {
            showToast(getString(R.string.dev_get_human_dissupport) + ":" + errorId, Toast.LENGTH_LONG);
            finish();
        }
    }

    @Override
    public void saveHumanDetectResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        if (isSuccess) {
            showToast(getString(R.string.set_dev_config_success), Toast.LENGTH_LONG);
        }else {
            showToast(getString(R.string.set_dev_config_failed) + ":" + errorId, Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ALERT_lINE_TYPE
                    || requestCode == ALERT_AREA_TYPE ) {
                HumanDetectionBean humanDetectionBean = (HumanDetectionBean) data.getSerializableExtra("HumanDetection");
                if (humanDetectionBean != null) {
                    presenter.setHumanDetection(humanDetectionBean);
                }
                if (requestCode == ALERT_lINE_TYPE) {
                    presenter.setRuleType(IA_TRIPWIRE);
                    lsiLine.setRightImage(SDKCONST.Switch.Open);
                    lsiArea.setRightImage(SDKCONST.Switch.Close);
                }else {
                    presenter.setRuleType(IA_PERIMETER);
                    lsiLine.setRightImage(SDKCONST.Switch.Close);
                    lsiArea.setRightImage(SDKCONST.Switch.Open);
                }
            }
        }
    }
}
