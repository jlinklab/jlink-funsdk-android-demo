package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by zhangyongyong on 2017-05-08-16:16.
 */

public class AdaptiveLayoutManager extends LinearLayoutManager {

    public AdaptiveLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        super.onMeasure(recycler, state, widthSpec, heightSpec);
        if (state.getItemCount() > 1) {
            View view = recycler.getViewForPosition(0);
            if (state.getItemCount() < 4) {
                if (view != null) {
                    measureChild(view, widthSpec, heightSpec);
                    int measuredWidth = view.getMeasuredWidth();
                    int measuredHeight = view.getMeasuredHeight();
                    setMeasuredDimension(measuredWidth * 3, measuredHeight);
                }
            } else {
                if (view != null) {
                    measureChild(view, widthSpec, heightSpec);
                    int measuredWidth = View.MeasureSpec.getSize(widthSpec);
                    int measuredHeight = view.getMeasuredHeight();
                    setMeasuredDimension(measuredWidth, measuredHeight);
                }
            }

        }
    }
}
