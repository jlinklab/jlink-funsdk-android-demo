package demo.xm.com.xmfunsdkdemo.ui.feedback.customservice.manager;

import static demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication.PATH_LOG;

import com.lib.sdk.bean.StringUtils;
import com.manager.XMFunSDKManager;
import com.manager.account.code.AccountCode;
import com.xm.base.OkHttpManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import demo.xm.com.xmfunsdkdemo.utils.ZipUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;


public class LogManager {
    public LogManager() {

    }

    public static File getLogFileByZip() {
        String logFilePath = XMFunSDKManager.getInstance().getAppFilePath() + File.separator + ".log.txt";
        if (StringUtils.isStringNULL(logFilePath)) {
            return null;
        }

        try {
            String tempZipFile = PATH_LOG + File.separator + "log.zip";
            ZipUtils.zipFolder(logFilePath, tempZipFile);
            return new File(tempZipFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String getSchemeAndDomain(String url) {
        try {
            URL netUrl = new URL(url);
            String protocol = netUrl.getProtocol(); // http or https
            String host = netUrl.getHost();         // www.example.com
            return protocol + "://" + host;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void uploadLogFile(String uploadUrl, File file, OkHttpManager.OnOkHttpListener onOkHttpListener) {


        try {
            String hostUrl = getSchemeAndDomain(uploadUrl);
            LogManagerServerInteraction serverInteraction = OkHttpManager
                    .createOkHttp(hostUrl, LogManagerServerInteraction.class);
            Call<ResponseBody> call;
            RequestBody requestFile = RequestBody.create(MediaType.parse("application/x-zip-compressed"), file);
            call = serverInteraction.putBodyLogFile(
                    uploadUrl,
                    requestFile);
            call.enqueue(OkHttpManager.createCallbackForJson(new OkHttpManager.OnOkHttpListener() {
                @Override
                public void onSuccess(String originalJsonData, Object result) {
                    if (onOkHttpListener != null) {
                        onOkHttpListener.onSuccess(originalJsonData, result);
                    }
                }

                @Override
                public void onFailed(int errorId, String errorMsg) {
                    if (onOkHttpListener != null) {
                        onOkHttpListener.onFailed(errorId, errorMsg);
                    }
                }
            }, AccountCode.class));
        } catch (Exception e) {
            e.printStackTrace();
            if (onOkHttpListener != null) {
                onOkHttpListener.onFailed(AccountCode.SEND_FAILED.getCode(), AccountCode.SEND_FAILED.getMessage());
            }
        }
    }
}
