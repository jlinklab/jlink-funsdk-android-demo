package demo.xm.com.xmfunsdkdemo.ui.device.browse.listener;

/**
 * 查看设备文件界面,显示相关的列表菜单.
 * Created by jiangping on 2018-10-23.
 */
public class DevBrowseFileContract {
    public interface IDevBrowseFileView {
        void onUpdateView();
    }

    public interface IDevBrowseFilePresenter {
        void devBrowseFile();
    }
}
