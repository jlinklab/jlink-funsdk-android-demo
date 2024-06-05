package demo.xm.com.xmfunsdkdemo.ui.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Message;

import androidx.core.content.FileProvider;

import com.lib.EFUN_ATTR;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.manager.XMFunSDKManager;
import com.manager.account.XMAccountManager;

import com.manager.path.PathManager;
import com.utils.FileUtils;
import com.xm.activity.base.XMBasePresenter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class AboutPresenter extends XMBasePresenter<XMAccountManager> implements AboutContract.IAboutPresenter {
    private AboutContract.IAboutView iAboutView;

    public AboutPresenter(AboutContract.IAboutView iAboutView) {
        this.iAboutView = iAboutView;
    }

    @Override
    public void onSuccess(int msgId) {

    }

    @Override
    public void onFailed(int msgId, int errorId) {

    }

    @Override
    public void onFunSDKResult(Message msg, MsgContent ex) {

    }

    @Override
    protected XMAccountManager getManager() {
        return null;
    }

    /**
     * @param context      上下文
     * @return
     */
    public static boolean sendFeedBack(Context context) {
        File file = new File(XMFunSDKManager.getInstance().getAppFilePath() + File.separator + ".log.txt");
        Uri fileUri = FileProvider.getUriForFile(context, "demo.xm.com.xmfunsdkdemo.fileprovider", file);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "分享文件"));
        return true;
    }
}
