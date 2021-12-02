package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lib.sdk.bean.smartanalyze.Points;
import com.manager.device.DeviceManager;
import com.manager.device.media.monitor.MonitorManager;
import com.xm.ui.widget.drawgeometry.listener.RevokeStateListener;
import com.xm.ui.widget.drawgeometry.view.DrawGeometry;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.presenter.AlertSetPreviewPresenter;

public class AlertSetPreviewFragment extends Fragment implements RevokeStateListener {
    private View mLayout;
    private MonitorManager monitorManager;
    private static final int CONVERT_PARAMETER = 8192;
    private AlertSetPreviewPresenter mPresenter;
    private DrawGeometry mDrawGeometry;
    private int mDirection;
    private String devId;
    private ViewGroup surfaceView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.fragment_alert_set_preview, container);
        surfaceView = mLayout.findViewById(R.id.video_view);
        initData();
        return mLayout;
    }

    private void initData() {
        mDrawGeometry = (DrawGeometry) mLayout.findViewById(R.id.shape_view);
        mDrawGeometry.setOnRevokeStateListener(this);
        mPresenter = new AlertSetPreviewPresenter(mDrawGeometry);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (monitorManager == null) {
            monitorManager = DeviceManager.getInstance().createMonitorPlayer(surfaceView,devId);
        }

        monitorManager.startMonitor();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != monitorManager) {
            monitorManager.destroyPlay();
        }
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public void setDrawGeometryType(int type) {
        if (mDrawGeometry != null) {
            mDrawGeometry.setGeometryType(type);
        }
    }

    public void revert() {
        if (mDrawGeometry != null) {
            mDrawGeometry.revertToDefaultPoints();
        }
    }

    public void retreatStep() {
        if (mDrawGeometry != null) {
            mDrawGeometry.retreatToPreviousOperationPoints();
        }
    }

  
    public List<Points> getConvertPoint() {
       return mPresenter.getConvertPoint(mDrawGeometry.getWidth(), mDrawGeometry.getHeight());
    }

    public void initAlertDirection(int direction) {
        this.mDirection = direction;
        mDrawGeometry.initDirection(direction);
    }

    public void setAlertDirection(int direction) {
        this.mDirection = direction;
        mDrawGeometry.setDirection(direction);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    public void setConvertPoint(List<Points> list,int size) {
    	if(size > 0) {
	    	List<Points> _list = list.subList(0, size);
	    	mPresenter.setConvertPoint(_list, mDrawGeometry.getWidth(), mDrawGeometry.getHeight());
    	}
    }

    @Override
    public void onRevokeEnable(boolean state) {
        ((AlertSetActivity) getActivity()).changeRevokeState(state);
    }
}
