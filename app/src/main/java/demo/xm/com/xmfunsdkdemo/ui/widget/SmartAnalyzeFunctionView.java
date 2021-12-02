package demo.xm.com.xmfunsdkdemo.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.model.FunctionViewItemElement;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view.AdaptiveLayoutManager;


/**
 * Created by zhangyongyong on 2017-05-08-10:53.
 */

public class SmartAnalyzeFunctionView extends RelativeLayout implements FunctionViewAdapter.OnItemClickListener {


    private RecyclerView recyclerView;
    private AdaptiveLayoutManager mLayoutManager;
    private Context mContext;
    private FunctionViewAdapter mViewAdapter;
    private OnItemClickListener mItemClickListener;
    private int directionPos = -1;
    public SmartAnalyzeFunctionView(Context context, List<FunctionViewItemElement> functionList) {
        super(context);
        this.mContext = context;
        initView(functionList);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initView(List<FunctionViewItemElement> functionList) {
        recyclerView = new RecyclerView(mContext);
        recyclerView.setBackground(mContext.getResources().getDrawable(R.drawable.smart_analyze_item_bg));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(CENTER_IN_PARENT);
        recyclerView.setLayoutParams(layoutParams);
        mLayoutManager = new AdaptiveLayoutManager(mContext);
        mLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        mViewAdapter = new FunctionViewAdapter(mContext, functionList);
        mViewAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mViewAdapter);
        addView(recyclerView);

        initData();
    }

    private void initData() {
        if (directionPos == -1) {
            directionPos = 0;
        }

        mViewAdapter.setItemSelected(directionPos);
    }

    public void setData(List<FunctionViewItemElement> functionList) {
        mViewAdapter.setData(functionList);
    }

    @Override
    public void onItemClick(View view, int position, String label) {
        mViewAdapter.setItemSelected(position);
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(view, position, label);
        }
    }

    public void setItemSelected(int position) {
        this.directionPos = position;
        if (mViewAdapter != null) {
           mViewAdapter.setItemSelected(position);
        }
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, String label);
    }
}
