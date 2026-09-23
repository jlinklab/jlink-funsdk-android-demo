package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.smartanalyze.Points;
import com.lib.sdk.struct.SDK_FishEyeFrame;
import com.lib.sdk.struct.SDK_TwoLensesInOne;
import com.manager.device.DeviceManager;
import com.manager.device.fisheye.FishEyeParamsCache;
import com.manager.device.media.MediaManager;
import com.manager.device.media.attribute.PlayerAttribute;
import com.manager.device.media.monitor.MonitorManager;
import com.utils.XUtils;
import com.xm.ui.widget.drawgeometry.listener.RevokeStateListener;
import com.xm.ui.widget.drawgeometry.view.DrawGeometry;
import com.xmgl.vrsoft.VRSoftDefine;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.presenter.AlertSetPreviewPresenter;

public class AlertSetPreviewFragment extends Fragment implements RevokeStateListener
        , MediaManager.OnMediaManagerListener {
    private View mLayout;
    private MonitorManager monitorManager;
    private static final int CONVERT_PARAMETER = 8192;
    private AlertSetPreviewPresenter mPresenter;
    private DrawGeometry mDrawGeometry;
    private int mDirection;
    private int mWndNum = -1;
    private int mChnId = 0;

    private String devId;
    private ViewGroup surfaceView;
    public AlertSetPreviewFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.fragment_alert_set_preview, container);
        surfaceView = mLayout.findViewById(R.id.video_view);
        initData();
        return mLayout;
    }

    private void initPlayer(){
        monitorManager = DeviceManager.getInstance().createMonitorPlayer(surfaceView,devId);
        monitorManager.setStreamType(SDKCONST.StreamType.Main);
        monitorManager.setChnId(mChnId);
        monitorManager.setNeedCorrectFishEye(true);
        monitorManager.setOnMediaManagerListener(this);

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
            initPlayer();
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

    public int getGeometryType(){
        return mDrawGeometry.getGeometryType();
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

    public void setWndNum(int wndNum) {
        mWndNum = wndNum;
    }

    public int getAlertDirection(){
        return mDrawGeometry.getDirection();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onVideoBufferEnd(PlayerAttribute playerAttribute, MsgContent msgContent) {
        if (mDrawGeometry != null) {
            ViewGroup.LayoutParams layoutParams = mDrawGeometry.getLayoutParams();
            int width = XUtils.getScreenWidth(getActivity());
            int height = mDrawGeometry.getHeight();
            float scale = monitorManager.getVideoScale();

            if (layoutParams != null && scale > 0) {
                float ratio = width * 1f / (height * 1f);
                if (ratio > scale) {
                    width = (int) (height * scale);
                } else {
                    height = (int) (width / scale);
                }

                SDK_FishEyeFrame fishEyeFrame = FishEyeParamsCache.getInstance().getFishFrame(monitorManager.getDevId() + monitorManager.getChnId());
                boolean isTwoLenses = fishEyeFrame instanceof SDK_TwoLensesInOne;
                if (isTwoLenses) {
                    //双目上下拼接，直接写死16 ：9
                    width = XUtils.getScreenWidth(getActivity());
                    height = width * 9 / 16;
                    if (mWndNum == 0) {
                        monitorManager.setTwoLensesDrawMode(VRSoftDefine.XMTwoLensesDrawMode.TopOnly);
                    } else {
                        monitorManager.setTwoLensesDrawMode(VRSoftDefine.XMTwoLensesDrawMode.BottomOnly);
                    }
                }
                layoutParams.width = width;
                layoutParams.height = height;
                monitorManager.changeVideoSize(width, height);
                mDrawGeometry.requestLayout();
                final ViewTreeObserver observerScr = mDrawGeometry.getViewTreeObserver();
                observerScr.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ((AlertSetActivity) getActivity()).initAlertView();
                        observerScr.removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }
    }


    public void setConvertPoint(List<Points> list,int size) {
        if(size > 0) {
            List<Points> _list = list.subList(0, size);
            mPresenter.setConvertPoint(_list, mDrawGeometry.getWidth(), mDrawGeometry.getHeight());
        }
    }

    @Override
    public void onRevokeEnable(boolean enable) {
        ((AlertSetActivity) getActivity()).changeRevokeState(enable);
    }

    @Override
    public void onMediaPlayState(PlayerAttribute playerAttribute, int i) {

    }

    @Override
    public void onFailed(PlayerAttribute playerAttribute, int i, int i1) {

    }

    @Override
    public void onShowRateAndTime(PlayerAttribute playerAttribute, boolean b, String s, long l) {

    }


    @Override
    public void onPlayStateClick(View view) {

    }
}
