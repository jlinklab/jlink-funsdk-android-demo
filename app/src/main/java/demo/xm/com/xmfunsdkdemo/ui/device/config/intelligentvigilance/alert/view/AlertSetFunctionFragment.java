package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Stack;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.SmartAnalyzeAlertType;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.model.FunctionViewItemElement;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.presenter.AlertSetFunctionPresenter;
import demo.xm.com.xmfunsdkdemo.ui.widget.SmartAnalyzeFunctionView;

import static com.manager.db.Define.ALERT_AREA_TYPE;
import static com.manager.db.Define.ALERT_lINE_TYPE;
import static com.manager.db.Define.GOODS_RETENTION_TYPE;
import static com.manager.db.Define.STOLEN_GOODS_TYPE;

import com.lib.FunSDK;


public class AlertSetFunctionFragment extends Fragment implements View.OnClickListener,
        SmartAnalyzeFunctionView.OnItemClickListener, AlertSetFunctionInterface {
    private int mRuleType;
    private View mLayout;
    private LinearLayout mAlertAreaSetting;
    private Button mBoundaryAlertDirection;
    private Button mAlertLineTriggerDirection;
    private Button mGoodsApplicationScenarios;
    private RelativeLayout mContainer;
    private SmartAnalyzeFunctionView mFunctionView;
    private AlertSetFunctionPresenter mFunctionPresenter;
    private Button mSave;
    private Button mRevert;
    private Button mRevoke;
    private int itemPos = -1;
    private int edgeCount = 0;
    private TextView tips ; // 提示语
    private LinearLayout tipsLayout;
    private Stack<Integer> clickStack;

    public AlertSetFunctionFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFunctionPresenter = new AlertSetFunctionPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.fragment_alert_set_function, container);
        mAlertAreaSetting = (LinearLayout) mLayout.findViewById(R.id.alert_area_setting);
        mBoundaryAlertDirection = (Button) mLayout.findViewById(R.id.boundary_alert_direction);
        mBoundaryAlertDirection.setText(FunSDK.TS("boundary_alert_direction"));
        mBoundaryAlertDirection.setOnClickListener(this);
        mAlertLineTriggerDirection = (Button) mLayout.findViewById(R.id.alert_line_trigger_direction);
        mAlertLineTriggerDirection.setText(FunSDK.TS("alert_line_trigger_direction"));
        mGoodsApplicationScenarios = (Button) mLayout.findViewById(R.id.goods_application_scenarios);
        mGoodsApplicationScenarios.setText(FunSDK.TS("TR_Alert_Shape_Area"));
        mContainer = (RelativeLayout) mLayout.findViewById(R.id.layoutRoot);
        mSave = mLayout.findViewById(R.id.smart_analyze_save);
        mSave.setText(FunSDK.TS("Done"));
        mSave.setOnClickListener(this);
        mRevoke = mLayout.findViewById(R.id.smart_analyze_revoke);
        mRevoke.setText(FunSDK.TS("smart_analyze_revoke"));
        mRevert = mLayout.findViewById(R.id.smart_analyze_revert);
        mRevert.setText(FunSDK.TS("smart_analyze_restore"));
        mFunctionView = mLayout.findViewById(R.id.alert_set_function_smart_layout);
        mFunctionView.setOnItemClickListener(this);
        tips = mLayout.findViewById(R.id.alert_set_function_tips);
        tipsLayout = mLayout.findViewById(R.id.alert_set_function_tips_layout);
        mRevoke.setOnClickListener(this);
        mRevert.setOnClickListener(this);
        clickStack = new Stack<>();
        return mLayout;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRuleType = ((AlertSetActivity) getActivity()).getRuleType();
        initFunctionView();
    }

    private void initFunctionView() {
        List<FunctionViewItemElement> functionList = mFunctionPresenter.initFunctionViewData(mRuleType);
        if (functionList == null) {
            return;
        }
        if(functionList != null && functionList.size() > 0){
            tipsLayout.setVisibility(View.VISIBLE);
        }
        mAlertAreaSetting.setVisibility(View.VISIBLE);
        switch (mRuleType) {
            case ALERT_lINE_TYPE:
                mAlertLineTriggerDirection.setVisibility(View.GONE);
                tips.setText(FunSDK.TS("TR_Alert_Set_Alert_Line_Tip"));
                break;
            case SmartAnalyzeAlertType.ALERT_AREA_TYPE:
                mAlertAreaSetting.setVisibility(View.VISIBLE);
                tips.setText(FunSDK.TS("TR_Alert_Set_Application_Scenarios_Tip"));
                break;
            case SmartAnalyzeAlertType.GOODS_RETENTION_TYPE:
                mGoodsApplicationScenarios.setVisibility(View.VISIBLE);
                tipsLayout.setVisibility(View.GONE);
                break;
            case SmartAnalyzeAlertType.STOLEN_GOODS_TYPE:
                mGoodsApplicationScenarios.setVisibility(View.VISIBLE);
                tipsLayout.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        mFunctionView.setData(functionList);
        initData();
    }

    private void initData() {
//        if (itemPos == -1) {
//            itemPos = 0;
//        }
//        mFunctionView.setItemSelected(itemPos);
        if (mFunctionPresenter.isDirectionDlgShow()) {
            mBoundaryAlertDirection.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.smart_analyze_save:
                ((AlertSetActivity) getActivity()).saveConfig();
                break;
            case R.id.smart_analyze_revoke:
                ((AlertSetActivity) getActivity()).retreatStep();
                itemRetreatStep();
                break;
            case R.id.smart_analyze_revert:
                ((AlertSetActivity) getActivity()).revert();
                clickStack.clear();
                break;
            case R.id.boundary_alert_direction:
                ((AlertSetActivity) getActivity()).showAlertDirectionDialog();
                break;
            default:
                break;
        }

    }

    private void itemRetreatStep() {
//        if(!clickStack.empty() && clickStack.size()>1){
//            clickStack.pop();
//            mFunctionView.setItemSelected(clickStack.peek());
//        }else {
//            ((AlertSetActivity) getActivity()).revert();
//            clickStack.clear();
//        }

    }

    @Override
    public void onItemClick(View view, int position, String label) {
        mFunctionPresenter.showShapeOnCanvas(position, mRuleType);
//        clickStack.push(position);
    }

    @Override
    public void setShapeType(int type) {
        ((AlertSetActivity) getActivity()).setShapeType(type);
    }

    @Override
    public void initAlertLineType(int lineType) {
        this.itemPos = lineType;
        if (mFunctionView != null) {
            mFunctionView.setItemSelected(itemPos);
        }
    }

    @Override
    public void setAlertLineType(int position) {
        try {
            ((AlertSetActivity) getActivity()).setAlertLineDirection(position);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initAlertAreaEdgeCount(int edgeCount) {
        this.edgeCount = edgeCount;
        if (edgeCount <= 6) {
            itemPos = edgeCount - 3;
        }else if (edgeCount == 8){
            itemPos = 4;
        }else {
            itemPos = 5;
        }
        if (mFunctionView != null) {
            mFunctionView.setItemSelected(itemPos);
        }
    }

    @Override
    public void setDirectionMask(String directionMask) {
        mFunctionPresenter.setDirectionMask(directionMask);
        initFunctionView();
    }

    @Override
    public void setAreaMask(String areaMask) {
        mFunctionPresenter.setAreaMask(areaMask);
        initFunctionView();
    }

    public void changeRevokeState(boolean state) {
        mRevoke.setEnabled(state);
        mRevert.setEnabled(state);
    }

    @Override
    public void onDestroy() {
        mFunctionPresenter.onDestroy();
        mFunctionPresenter = null;
        super.onDestroy();
    }

    /**
     * 设置提示是否显示
     * @param state
     */
    public void setTipsView(int state) {
        tipsLayout.setVisibility(state);

    }
}
