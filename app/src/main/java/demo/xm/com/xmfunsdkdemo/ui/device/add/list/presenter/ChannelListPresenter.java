package demo.xm.com.xmfunsdkdemo.ui.device.add.list.presenter;

import com.manager.device.DeviceManager;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.device.add.list.listener.ChannelListContract;

/**
 * 通道列表
 * @author hws
 * @class
 * @time 2020/10/27 15:09
 */
public class ChannelListPresenter extends XMBasePresenter<DeviceManager> implements ChannelListContract.IChannelListPresenter {
    private ChannelListContract.IChannelListView iChannelListView;
    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    public ChannelListPresenter(ChannelListContract.IChannelListView iChannelListView) {
        this.iChannelListView = iChannelListView;
    }
}
