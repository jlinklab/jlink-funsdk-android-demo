package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.lib.sdk.bean.ChannelHumanRuleLimitBean;
import com.lib.sdk.bean.HumanDetectionBean;
import com.lib.sdk.bean.smartanalyze.Points;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.drawgeometry.model.GeometryInfo;

import java.util.ArrayList;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;

import static com.lib.sdk.bean.HumanDetectionBean.IA_BIDIRECTION;
import static com.lib.sdk.bean.HumanDetectionBean.IA_DIRECT_BACKWARD;
import static com.lib.sdk.bean.HumanDetectionBean.IA_DIRECT_FORWARD;
import static com.manager.db.Define.ALERT_AREA_TYPE;
import static com.manager.db.Define.ALERT_lINE_TYPE;
import static com.xm.ui.widget.drawgeometry.model.DirectionPath.DIRECTION_BACKWARD;
import static com.xm.ui.widget.drawgeometry.model.DirectionPath.DIRECTION_FORWARD;
import static com.xm.ui.widget.drawgeometry.model.DirectionPath.NO_DIRECTION;
import static com.xm.ui.widget.drawgeometry.model.DirectionPath.TWO_WAY;
import static com.xm.ui.widget.drawgeometry.model.GeometryInfo.GEOMETRY_LINE;

/**
 * 警戒线&警戒区域设置
 */
public class AlertSetActivity extends BaseConfigActivity implements DirectionSelectDialog.OnDirectionSelListener {
    private int mRuleType;
    private FragmentManager mFragmentManager;
    private AlertSetPreviewFragment mPreviewFragment;
    private AlertSetFunctionFragment mFunctionFragment;
    private HumanDetectionBean mHumanDetection;
    private ArrayList<HumanDetectionBean.PedRule> mPedRule;
    private boolean mIsInit;
    private DirectionSelectDialog directionSelectFragment;
    private ChannelHumanRuleLimitBean channelHumanRuleLimitBean;
    private String devId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_set);
        mFragmentManager = getSupportFragmentManager();
        mPreviewFragment = (AlertSetPreviewFragment) mFragmentManager.findFragmentById(R.id.fragment_alert_set_preview);
        mFunctionFragment = (AlertSetFunctionFragment) mFragmentManager.findFragmentById(R.id.fragment_alert_set_function);
        titleBar = findViewById(R.id.title_bar);
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        devId = intent.getStringExtra("devId");
        mRuleType = intent.getExtras().getInt("RuleType", ALERT_AREA_TYPE);
        mPreviewFragment.setDevId(devId);
        mHumanDetection = (HumanDetectionBean) intent.getSerializableExtra("HumanDetection");
        channelHumanRuleLimitBean = (ChannelHumanRuleLimitBean) intent.getSerializableExtra("ChannelHumanRuleLimit");
        mPedRule = mHumanDetection.getPedRules();

        switch (mRuleType) {
            case ALERT_AREA_TYPE:
                titleBar.setTitleText(getString(R.string.type_alert_area));
                break;
            case ALERT_lINE_TYPE:
                titleBar.setTitleText(getString(R.string.type_alert_line));
                break;
            default:
                break;
        }
    }

    public int getRuleType() {
        return mRuleType;
    }

    /**
     * 设置集合图形类型
     * {@link GeometryInfo#GEOMETRY_CIRCULAR
     *
     * @param type
     * @link GeometryInfo#GEOMETRY_LINE
     * @link GeometryInfo#GEOMETRY_TRIANGLE
     * @link GeometryInfo#GEOMETRY_RECTANGLE
     * @link GeometryInfo#GEOMETRY_PENTAGON
     * @link GeometryInfo#GEOMETRY_L
     * @link GeometryInfo#GEOMETRY_AO
     * @link GeometryInfo#GEOMETRY_CUSTOM}
     */
    public void setGeometryType(int type) {
        mPreviewFragment.setDrawGeometryType(type);
    }

    public void revert() {
        mPreviewFragment.revert();
        mFunctionFragment.revert();
    }

    public void retreatStep() {
        mPreviewFragment.retreatStep();
    }

    public void saveConfig() {
        dealWithData();
        Intent intent = new Intent();
        intent.putExtra("HumanDetection", mHumanDetection);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void dealWithData() {
        List<Points> points = mPreviewFragment.getConvertPoint();
        switch (mRuleType) {
            case ALERT_lINE_TYPE:
                HumanDetectionBean.PedRule.RuleLine.Pts pts = mPedRule.get(0).getRuleLine().getPts();
                pts.setStartX((int) points.get(0).getX());
                pts.setStartY((int) points.get(0).getY());
                pts.setStopX((int) points.get(1).getX());
                pts.setStopY((int) points.get(1).getY());
                break;
            case ALERT_AREA_TYPE:
                HumanDetectionBean.PedRule.RuleRegion ruleRegion = mPedRule.get(0).getRuleRegion();
                ruleRegion.setPtsNum(points.size());
                ruleRegion.setPtsByPoints(points);
                break;
            default:
                break;
        }

    }

    public void setAlertLineDirection(int position) {
        HumanDetectionBean.PedRule.RuleLine ruleLine = mPedRule.get(0).getRuleLine();
        switch (position) {
            case IA_DIRECT_FORWARD:
                mPreviewFragment.setAlertDirection(DIRECTION_FORWARD);
                ruleLine.setAlarmDirect(IA_DIRECT_FORWARD);
                break;
            case IA_DIRECT_BACKWARD:
                mPreviewFragment.setAlertDirection(DIRECTION_BACKWARD);
                ruleLine.setAlarmDirect(IA_DIRECT_BACKWARD);
                break;
            case IA_BIDIRECTION:
                mPreviewFragment.setAlertDirection(TWO_WAY);
                ruleLine.setAlarmDirect(IA_BIDIRECTION);
                break;
            default:
                mPreviewFragment.setAlertDirection(NO_DIRECTION);
                break;
        }
    }

    public void changeRevokeState(boolean state) {
        mFunctionFragment.changeRevokeState(state);
    }

    public void showAlertDirectionDialog() {
        HumanDetectionBean.PedRule.RuleRegion ruleRegion = mPedRule.get(0).getRuleRegion();
        int direction = ruleRegion.getAlarmDirect();
        if (directionSelectFragment == null) {
            directionSelectFragment = new DirectionSelectDialog();
            directionSelectFragment.setOnDirectionSelListener(this);
        }
        directionSelectFragment.setDirection(direction);
        directionSelectFragment.show(getSupportFragmentManager(), "DirectionSel");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        int size = 0;
        int direct = IA_DIRECT_FORWARD;
        if (!mIsInit && hasFocus && null != mPedRule) {
            List<Points> points = null;
            switch (mRuleType) {
                case ALERT_lINE_TYPE:
                    HumanDetectionBean.PedRule.RuleLine.Pts linePts = mPedRule.get(0).getRuleLine().getPts();
                    if (linePts != null) {
                        points = new ArrayList<Points>();
                        points.add(new Points(linePts.getStartX(), linePts.getStartY()));
                        points.add(new Points(linePts.getStopX(), linePts.getStopY()));
                        size = 2;
                    }
                    direct = mPedRule.get(0).getRuleLine().getAlarmDirect();
                    System.out.println("direct:" + direct + "startX:" + linePts.getStartX() + "startY:" + linePts.getStartY()
                            + "stopX:" + linePts.getStopX() + "stopY:" + linePts.getStopY());
                    String lineDirect = channelHumanRuleLimitBean.getDwLineDirect();
                    mFunctionFragment.setDirectionMask(lineDirect);
                    setGeometryType(GEOMETRY_LINE);
                    switch (direct) {
                        case IA_DIRECT_FORWARD:
                            mPreviewFragment.initAlertDirection(DIRECTION_FORWARD);
                            break;
                        case IA_DIRECT_BACKWARD:
                            mPreviewFragment.initAlertDirection(DIRECTION_BACKWARD);
                            break;
                        case IA_BIDIRECTION:
                            mPreviewFragment.initAlertDirection(TWO_WAY);
                            break;
                        default:
                            mPreviewFragment.initAlertDirection(NO_DIRECTION);
                            break;
                    }
                    mFunctionFragment.initAlertLineType(direct);
                    break;
                case ALERT_AREA_TYPE:
                    String areaLine = channelHumanRuleLimitBean.getDwAreaLine();
                    mFunctionFragment.setAreaMask(areaLine);
                    String areaDirect = channelHumanRuleLimitBean.getDwAreaDirect();
                    mFunctionFragment.setDirectionMask(areaDirect);
                    HumanDetectionBean.PedRule.RuleRegion ruleRegion = mPedRule.get(0).getRuleRegion();
                    size = ruleRegion.getPtsNum();
                    points = ruleRegion.getPointsList();
                    direct = ruleRegion.getAlarmDirect();
                    mFunctionFragment.initAlertAreaEdgeCount(size);
                    switch (direct) {
                        case IA_DIRECT_FORWARD:
                            mPreviewFragment.initAlertDirection(DIRECTION_FORWARD);
                            break;
                        case IA_DIRECT_BACKWARD:
                            mPreviewFragment.initAlertDirection(DIRECTION_BACKWARD);
                            break;
                        case IA_BIDIRECTION:
                            mPreviewFragment.initAlertDirection(TWO_WAY);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            mPreviewFragment.setConvertPoint(points, size);
            mIsInit = true;
        }
    }

    @Override
    public void onDirection(int direction) {
        mPedRule.get(0).getRuleRegion().setAlarmDirect(direction);
        switch (direction) {
            case IA_DIRECT_FORWARD:
                mPreviewFragment.initAlertDirection(DIRECTION_FORWARD);
                break;
            case IA_DIRECT_BACKWARD:
                mPreviewFragment.initAlertDirection(DIRECTION_BACKWARD);
                break;
            case IA_BIDIRECTION:
                mPreviewFragment.initAlertDirection(TWO_WAY);
                break;
            default:
                break;
        }
    }
}
