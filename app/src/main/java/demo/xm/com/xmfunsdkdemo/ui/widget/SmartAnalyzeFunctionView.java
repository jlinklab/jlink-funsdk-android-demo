package demo.xm.com.xmfunsdkdemo.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;


import com.utils.XUtils;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.model.FunctionViewItemElement;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view.AdaptiveLayoutManager;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view.HorizontalItemDecoration;

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
    private List<FunctionViewItemElement> functionList;

    public SmartAnalyzeFunctionView(Context context, List<FunctionViewItemElement> functionList) {
        super(context);
        this.mContext = context;
        this.functionList = functionList;
        initView(functionList);
    }

    public SmartAnalyzeFunctionView(Context context , AttributeSet attrs){
        super(context, attrs);
        this.mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initView(List<FunctionViewItemElement> functionList) {
        recyclerView = new RecyclerView(mContext);
        // 修改布局参数，确保RecyclerView正确填充父容器
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(CENTER_IN_PARENT);
        layoutParams.setMargins(XUtils.dp2px(mContext,14f),0,XUtils.dp2px(mContext,14f),0);
        recyclerView.setLayoutParams(layoutParams);

        // 使用自定义的LayoutManager或确保正确设置
        mLayoutManager = new AdaptiveLayoutManager(mContext);
        mLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addItemDecoration(new HorizontalItemDecoration(XUtils.dp2px(mContext,7f),mContext));
        mViewAdapter = new FunctionViewAdapter(mContext, functionList);
        mViewAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mViewAdapter);

        // 确保RecyclerView在添加到父视图后正确布局
        addView(recyclerView);

        // 添加布局完成监听器
        recyclerView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                recyclerView.removeOnLayoutChangeListener(this);
                initData();
            }
        });
    }

    private void initData() {
        if (directionPos == -1) {
            directionPos = 0;
        }

        mViewAdapter.setItemSelected(directionPos);

        // 确保RecyclerView滚动到正确位置
        if (mLayoutManager != null) {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mLayoutManager.scrollToPositionWithOffset(directionPos, 0);
                }
            });
        }
    }

    public void setData(List<FunctionViewItemElement> List) {
        this.functionList = List;
        if (mViewAdapter != null) {
            mViewAdapter.setData(functionList);
            mViewAdapter.notifyDataSetChanged();
            initData();
        } else {
            initView(functionList);
        }
    }

    @Override
    public void onItemClick(View view, int position, String label) {
        mViewAdapter.setItemSelected(position);
        directionPos = position; // 更新当前位置

        // 确保点击后滚动到正确位置
        mLayoutManager.scrollToPositionWithOffset(position, 0);

        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(view, position, label);
        }
    }

    public void setItemSelected(int position) {
        this.directionPos = position;
        if (mViewAdapter != null) {
            mViewAdapter.setItemSelected(position);
            // 确保设置选中项后滚动到正确位置
            mLayoutManager.scrollToPositionWithOffset(position, 0);
        }
    }

    public void setItemUnSelected() {
        this.directionPos = -1;
        if (mViewAdapter != null) {
            mViewAdapter.setItemUnSelected();
        }
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, String label);
    }

    // 添加一个方法来获取当前RecyclerView的滚动位置
    public int getCurrentScrollPosition() {
        if (mLayoutManager != null) {
            return mLayoutManager.findFirstVisibleItemPosition();
        }
        return 0;
    }
}