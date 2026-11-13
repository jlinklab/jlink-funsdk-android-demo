package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.lib.FunSDK;
import com.lib.MsgContent;
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
    private XTitleBar mXTitleBar;
    private DirectionSelectDialog directionSelectFragment;
    private ChannelHumanRuleLimitBean channelHumanRuleLimitBean;
    private int direct = IA_DIRECT_FORWARD; //保存最初的方向
    private int size = 0;  //保存最初的多边形的边
    private int mWndNum = -1;
    private int index = 0;
    private String title;

    private String devId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_set);
        mFragmentManager = getSupportFragmentManager();
        mPreviewFragment = (AlertSetPreviewFragment) mFragmentManager.findFragmentById(R.id.fragment_alert_set_preview);
        mFunctionFragment = (AlertSetFunctionFragment) mFragmentManager.findFragmentById(R.id.fragment_alert_set_function);
        mXTitleBar = findViewById(R.id.title_bar);
        mXTitleBar.setBottomTip(getClass().getName());
        mXTitleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
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
        mPreviewFragment.setDevId(devId);

        try {
            mHumanDetection = (HumanDetectionBean) intent.getSerializableExtra("HumanDetection");
            channelHumanRuleLimitBean = (ChannelHumanRuleLimitBean) intent.getSerializableExtra("ChannelHumanRuleLimit");
            mPedRule = mHumanDetection.getPedRules();
            mRuleType = intent.getExtras().getInt("RuleType", ALERT_AREA_TYPE);
            mWndNum = intent.getExtras().getInt("mWndNum", -1);
            title = intent.getStringExtra("title");
            if (mWndNum >= 0 && mPreviewFragment != null) {
                mPreviewFragment.setWndNum(mWndNum);
                int[] sensorOrder = channelHumanRuleLimitBean.getMultiSensor().getSensorOrder();
                for (int i = 0; i < sensorOrder.length; i++) {
                    if (sensorOrder[i] == mWndNum) {
                        index = i;
                        break;
                    }
                }
                mXTitleBar.setTitleText(title);

            } else {
                switch (mRuleType) {
                    case ALERT_AREA_TYPE:
                        mXTitleBar.setTitleText(FunSDK.TS("type_alert_area"));
                        break;
                    case ALERT_lINE_TYPE:
                        mXTitleBar.setTitleText(FunSDK.TS("type_alert_line"));
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRuleType() {
        return mRuleType;
    }

    public void setShapeType(int type) {
        mPreviewFragment.setDrawGeometryType(type);
    }

    public void revert() {
        mPreviewFragment.revert();
        mFunctionFragment.initAlertLineType(direct);
        if(mRuleType == ALERT_lINE_TYPE ) {
            mFunctionFragment.initAlertLineType(direct);
        } else {
            mFunctionFragment.initAlertAreaEdgeCount(size);
        }
    }

    public void retreatStep() {
        mPreviewFragment.retreatStep();
        if(mRuleType == ALERT_lINE_TYPE){
            if(mPreviewFragment.getAlertDirection() > 0){
                mFunctionFragment.initAlertLineType(mPreviewFragment.getAlertDirection() - 1);
            }

        }else {
            if(mPreviewFragment.getGeometryType() > 0){
                mFunctionFragment.initAlertAreaEdgeCount(mPreviewFragment.getGeometryType());
            }
        }

    }

    public void saveConfig() {
        if(isLegalData()){
            dealWithData();
            Intent intent = new Intent();
            intent.putExtra("HumanDetection",mHumanDetection);
            if (mWndNum >= 0) {
                intent.putExtra("mWndNum", mWndNum);
            }
            setResult(Activity.RESULT_OK,intent);
            finish();
        }else {
            Toast.makeText(this,FunSDK.TS("Data_exception"),Toast.LENGTH_LONG).show();
        }

    }

    private void dealWithData() {
        List<Points> points = mPreviewFragment.getConvertPoint();
        switch (mRuleType) {
            case ALERT_lINE_TYPE:
                if (points.size() < 2) {
                    return;
                }
                HumanDetectionBean.PedRule.RuleLine.Pts pts = mPedRule.get(0).getRuleLine().getPts();
                pts.setStartX((int) points.get(0).getX());
                pts.setStartY((int) points.get(0).getY());
                pts.setStopX((int) points.get(1).getX());
                pts.setStopY((int) points.get(1).getY());
                break;
            case ALERT_AREA_TYPE:
                HumanDetectionBean.PedRule.RuleRegion ruleRegion = mPedRule.get(index).getRuleRegion();
                ruleRegion.setPtsNum(points.size());
                ruleRegion.setPtsByPoints(points);
                break;
            default:
                break;
        }

    }

    /**
     * 判断所有点中是否有两根线条相交
     * @return
     */
    private boolean isLegalData(){
        List<Points> points = mPreviewFragment.getConvertPoint();
        if(points.size() > 3){
            for (int i = 0; i+1 < points.size(); i++) {
                for (int j = i + 2; j < points.size(); j++){
                    if(j+1 < points.size()){
                        if(intersection(points.get(i),points.get(i+1),points.get(j),points.get(j+1))){
                            return false;
                        }
                    }else if(j+1 == points.size() && i != 0) {
                        if(intersection(points.get(i),points.get(i+1),points.get(j),points.get(0))){
                            return false;
                        }
                    }

                }
            }
        }else {
            return true;
        }
        return true;
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
        directionSelectFragment.show(getSupportFragmentManager(),"DirectionSel");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
    }

    public void initAlertView() {
        if (mPedRule == null) {
            return;
        }

        List<Points> points = null;
        switch (mRuleType) {
            case ALERT_lINE_TYPE:
                HumanDetectionBean.PedRule.RuleLine.Pts linePts = mPedRule.get(0).getRuleLine().getPts();
                if (linePts != null) {
                    points = new ArrayList<Points>();
                    points.add(new Points(linePts.getStartX(),linePts.getStartY()));
                    points.add(new Points(linePts.getStopX(), linePts.getStopY()));
                    size = 2;
                }
                direct = mPedRule.get(0).getRuleLine().getAlarmDirect();
                System.out.println("direct:" + direct + "startX:" + linePts.getStartX() + "startY:" + linePts.getStartY()
                        + "stopX:" + linePts.getStopX() + "stopY:" + linePts.getStopY());
                String lineDirect = channelHumanRuleLimitBean.getDwLineDirect();
                mFunctionFragment.setDirectionMask(lineDirect);
                mFunctionFragment.initAlertLineType(direct);
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
                break;
            case ALERT_AREA_TYPE:
                String areaLine = channelHumanRuleLimitBean.getDwAreaLine();
                mFunctionFragment.setAreaMask(areaLine);
                String areaDirect = channelHumanRuleLimitBean.getDwAreaDirect();
                mFunctionFragment.setDirectionMask(areaDirect);
                HumanDetectionBean.PedRule.RuleRegion ruleRegion = mPedRule.get(index).getRuleRegion();
                size = ruleRegion.getPtsNum();
                points =  ruleRegion.getPointsList();
                direct = ruleRegion.getAlarmDirect();
                mFunctionFragment.initAlertAreaEdgeCount(size);
                switch (direct) {
                    case IA_DIRECT_FORWARD:
                        mPreviewFragment.initAlertDirection(DIRECTION_FORWARD);
                        mFunctionFragment.setTipsView(View.GONE);
                        break;
                    case IA_DIRECT_BACKWARD:
                        mPreviewFragment.initAlertDirection(DIRECTION_BACKWARD);
                        mFunctionFragment.setTipsView(View.VISIBLE);
                        break;
                    case IA_BIDIRECTION:
                        mPreviewFragment.initAlertDirection(TWO_WAY);
                        mFunctionFragment.setTipsView(View.GONE);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        mPreviewFragment.setConvertPoint(points,size);
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


    /**
     * 判断两根线段是否相交 line1 有p1 和 p2 组成 line2 由 p3 p4组成
     *
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return
     */
    public static boolean intersection(Points p1, Points p2, Points p3, Points p4) {
        float l1x1 = p1.getX();
        float l1y1 = p1.getY();
        float l1x2 = p2.getX();
        float l1y2 = p2.getY();
        float l2x1 = p3.getX();
        float l2y1 = p3.getY();
        float l2x2 = p4.getX();
        float l2y2 = p4.getY();
        // 快速排斥实验 首先判断两条线段在 x 以及 y 坐标的投影是否有重合。 有一个为真，则代表两线段必不可交。
        if (Math.max(l1x1, l1x2) < Math.min(l2x1, l2x2)
                || Math.max(l1y1, l1y2) < Math.min(l2y1, l2y2)
                || Math.max(l2x1, l2x2) < Math.min(l1x1, l1x2)
                || Math.max(l2y1, l2y2) < Math.min(l1y1, l1y2)) {
            return false;
        }
        // 跨立实验  如果相交则矢量叉积异号或为零，大于零则不相交
        if ((((l1x1 - l2x1) * (l2y2 - l2y1) - (l1y1 - l2y1) * (l2x2 - l2x1))
                * ((l1x2 - l2x1) * (l2y2 - l2y1) - (l1y2 - l2y1) * (l2x2 - l2x1))) > 0
                || (((l2x1 - l1x1) * (l1y2 - l1y1) - (l2y1 - l1y1) * (l1x2 - l1x1))
                * ((l2x2 - l1x1) * (l1y2 - l1y1) - (l2y2 - l1y1) * (l1x2 - l1x1))) > 0) {
            return false;
        }
        return true;
    }
}
