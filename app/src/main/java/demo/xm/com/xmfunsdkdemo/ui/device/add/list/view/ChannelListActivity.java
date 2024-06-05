package demo.xm.com.xmfunsdkdemo.ui.device.add.list.view;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basic.G;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.lib.sdk.struct.SDK_ChannelNameConfigAll;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.widget.XTitleBar;

import java.util.ArrayList;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.adapter.ChannelListAdapter;
import demo.xm.com.xmfunsdkdemo.ui.device.add.list.listener.ChannelListContract;
import demo.xm.com.xmfunsdkdemo.ui.device.add.list.presenter.ChannelListPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.view.DevMonitorActivity;

/**
 * 通道列表
 * List of channels
 * @author hws
 * @class
 * @time 2020/10/27 14:42
 */
public class ChannelListActivity extends DemoBaseActivity<ChannelListPresenter> implements
        ChannelListAdapter.OnItemChannelClickListener, ChannelListContract.IChannelListView {
    private RecyclerView rvChannelList;
    private ChannelListAdapter channelListAdapter;
    @Override
    public ChannelListPresenter getPresenter() {
        return new ChannelListPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);
        initView();
        initData();
    }

    private void initView() {
        rvChannelList = findViewById(R.id.rv_channel_list);
        rvChannelList.setLayoutManager(new LinearLayoutManager(this));
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.channel_list));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        titleBar.setBottomTip(getClass().getName());
    }

    private void initData(){
        channelListAdapter = new ChannelListAdapter(this);
        rvChannelList.setAdapter(channelListAdapter);

        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
        if (xmDevInfo != null) {
            SDBDeviceInfo sdbDeviceInfo = xmDevInfo.getSdbDevInfo();
            if (sdbDeviceInfo != null) {
                SDK_ChannelNameConfigAll channelInfos = sdbDeviceInfo.getChannel();
                if (channelInfos != null) {
                    List<String> channelList = new ArrayList<>();
                    for (int i = 0 ; i < channelInfos.nChnCount; ++i) {
                        byte[] info = channelInfos.st_channelTitle[i];
                        String channelName = getString(R.string.channel) + ":" + i;
                        if (info != null) {
                            channelName += "(" + G.ToString(info) + ")";
                        }

                        channelList.add(channelName);
                    }
                    channelListAdapter.setData(channelList);
                }
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        turnToActivity(DevMonitorActivity.class,new Object[][]{{"chnId",position},{"chnCount",channelListAdapter.getItemCount()}});
    }

    @Override
    public boolean onLongItemClick(int position) {
        return false;
    }

}
