package demo.xm.com.xmfunsdkdemo.ui.device.cloud.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.EFUN_ATTR;
import com.lib.FunSDK;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.utils.XUtils;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.rotateloadingview.LoadingView;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.base.DemoConstant;
import demo.xm.com.xmfunsdkdemo.ui.device.cloud.listener.CloudWebContract;
import demo.xm.com.xmfunsdkdemo.ui.device.cloud.presenter.CloudWebPresenter;

/**
 * @author hws
 * @class describe
 * @time 2019-10-23 9:38
 */
public class CloudWebActivity extends DemoBaseActivity<CloudWebPresenter> implements CloudWebContract.ICloudWebView {
    public static final String CLOUD_STORAGE_BASE_URL = "https://boss2.xmcsrv.net/index.do";
    /**
     * 流量
     */
    public static final String GOODS_TYPE_FLOW = "net.cellular";
    /**
     * 云电话
     */
    public static final String GOODS_TYPE_CALL = "xmc.voes";

    /**
     * 语音报警
     */
    public static final String GOODS_TYPE_VOICE_ALARM = "pms.voes";

    /**
     * 短信报警
     */
    public static final String GOODS_TYPE_MESSAGE_ALARM = "pms.sms";

    /**
     * 云增强
     */
    public static final String GOODS_TYPE_CLOUD_ENHANCE = "xmc.enhance";
    /**
     * 云智能
     */
    public static final String GOODS_TYPE_CLOUD_SMART = "xmc.ais";
    /**
     * 云存储
     */
    public static final String GOODS_TYPE_CLOUD_STORAGE = "xmc.css";
    /**
     * 阳光厨房
     */
    public static final String GOODS_TYPE_EXT_ALIELE = "ext.aliele";
    @BindView(R.id.wv_cloud_web_view)
    WebView webView;
    @BindView(R.id.pb_cloud_web)
    ProgressBar progressBar;
    @BindView(R.id.tv_cloud_web_title)
    TextView titleTv;
    @BindView(R.id.lv_cloud_web_waiting)
    LoadingView waiting;
    @BindView(R.id.iv_cloud_web_back)
    ImageView ivBack;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        try {
            transparencyStatusBar(this);
            setContentView(R.layout.activity_cloud_web);
            ButterKnife.bind(this);
        }catch (Exception e) {
            Toast.makeText(this, "无法创建webView", Toast.LENGTH_LONG).show();
            finish();
            e.printStackTrace();
            return;
        }
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }


        String goodsType = intent.getStringExtra("goodsType");
        presenter.setGoodsType(goodsType);

        webView.getSettings().setTextZoom(100);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "XmAppJsSDK");
        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua + ";xm-android-m");
        webView.getSettings().setCacheMode(WebSettings.LOAD_NORMAL);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("ccy", "shouldOverrideUrlLoading = " + url);

                if (url.contains("alipays://platformapi")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    if (intent.resolveActivity(getPackageManager()) != null) {  //检测是否安装了支付宝(未安装则网页支付）
                        startActivity(intent);
                        return true;
                    }
                }
                if (url.contains("weixin://wap/pay")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    if (intent.resolveActivity(getPackageManager()) != null) { //检测是否安装了微信
                        startActivity(intent);
                        return true;
                    }else {
                        Toast.makeText(CloudWebActivity.this, FunSDK.TS("Install_WeChat_Application"),Toast.LENGTH_LONG).show();
                        view.loadUrl(CloudWebActivity.this.url);  //回主页
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //完成支付跳回主界面后，要清空历史页面
                if (url.contains(CLOUD_STORAGE_BASE_URL)) {
                    view.clearHistory();
                }else {
                    if (url != null && url.contains("load=finish")) {
                        titleTv.setVisibility(View.INVISIBLE);
                        waiting.setVisibility(View.INVISIBLE);
                        ivBack.setVisibility(View.VISIBLE);
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                    Log.d("apple-progress",url);
                }
            }

            @Override
            public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                return super.onRenderProcessGone(view, detail);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (null != progressBar) {
                    progressBar.setProgress(newProgress);
                    if (newProgress == 100) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//允许https/http混合加载
        }

        url = createCloudUrl();

        if (url != null) {
            webView.loadUrl(url);
        }
    }

    @OnClick(R.id.iv_cloud_web_back)
    void onBack(View view) {
        finish();
    }

    private String createCloudUrl() {
        String userId = FunSDK.GetFunStrAttr(EFUN_ATTR.LOGIN_USER_ID);
        if (userId == null) {
            XMPromptDlg.onShow(this,"用户信息获取失败", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            return null;
        }

        //只有中文或英文,
        String lan =  Locale.getDefault().getLanguage();
        if(lan.compareToIgnoreCase("zh") == 0){
            lan = "zh-CN";//不是zh_CN
        }else {
            lan = "en";
        }

        ApplicationInfo appInfo = null;
        Map<String, String> urlMap = new LinkedHashMap<>();
        urlMap.put("user_id", userId);//账号登录后的用户ID
        urlMap.put("uuid", presenter.getDevId());//设备序列号
        urlMap.put("lan",lan);//语言
        urlMap.put("appKey", DemoConstant.APP_KEY);
        urlMap.put("goods",presenter.getGoodsType());// 根据需求传入对应的值，比如云存储->GOODS_TYPE_CLOUD_STORAGE，云电话->GOODS_TYPE_CALL，4G流量->GOODS_TYPE_FLOW, 如果不传的话默认云服务首页
        XMDevInfo devInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
        if (devInfo != null) {
            urlMap.put("devName", devInfo.getDevName());
        }

        return getUrl(CLOUD_STORAGE_BASE_URL, urlMap);
    }


    /**
     * url参数拼接
     */
    private String getUrl(String baseUrl, Map<String, String> values) {
        StringBuilder sb = new StringBuilder(baseUrl);
        if (values != null && !values.isEmpty()) {
            sb.append("?");
            for (Map.Entry<String, String> entry : values.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }

    /**
     * 设置状态栏为全透明
     * 通过设置theme的方式无法达到全透明效果
     *
     * @param activity
     */
    @TargetApi(19)
    private static void transparencyStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0及其以上
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4及其以上
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public void onUpdateSysInfoResult(boolean isSuccess, int errorId) {

    }

    @Override
    public CloudWebPresenter getPresenter() {
        return new CloudWebPresenter(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null){
            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);
            webView.removeAllViews();
            webView.destroy();

            ViewGroup view = (ViewGroup) getWindow().getDecorView();
            view.removeAllViews();
            ((ViewGroup)webView.getParent()).removeView(webView);
        }

        webView = null;
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    /**
     * 关闭当前窗口
     */
    @JavascriptInterface
    public void closeWindow(){
        finish();
    }

    /**
     * mediaType:0->图片(报警消息） 1->视频（录像回放）
     */
    @JavascriptInterface
    public void openCloudStorageList(int mediaType) {

    }

    /**
     * 开启二维码扫描
     */
    @JavascriptInterface
    public void openQRScan() {

    }

    /**
     * PayPal支付是否抓取信用卡支付并跳转到卡支付界面
     * @param status 如果status是1表示需要跳转到信用卡页面
     */
    @JavascriptInterface
    public void payPalCard(int status) {

    }

}
