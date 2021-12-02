package demo.xm.com.xmfunsdkdemo.ui.device.browse.presenter;

import com.manager.device.DeviceManager;

import com.xm.activity.base.XMBasePresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.browse.listener.DevBrowseFileContract;

/**
 * 查看设备文件界面,显示相关的列表菜单.
 * Created by jiangping on 2018-10-23.
 */
public class DevBrowseFilePresenter extends XMBasePresenter<DeviceManager> implements DevBrowseFileContract.IDevBrowseFilePresenter {

    private DevBrowseFileContract.IDevBrowseFileView iDevBrowseFileView;

    public DevBrowseFilePresenter(DevBrowseFileContract.IDevBrowseFileView iDevBrowseFileView) {
        this.iDevBrowseFileView = iDevBrowseFileView;
    }

    @Override
    public void devBrowseFile() {

    }

    @Override
    protected DeviceManager getManager() {
        return null;
    }
}

