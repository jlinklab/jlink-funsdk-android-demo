package demo.xm.com.xmfunsdkdemo.ui.device.cloud.view;

import static com.manager.db.XMDevInfo.OFF_LINE;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lib.EFUN_ATTR;
import com.lib.FunSDK;
import com.lib.sdk.bean.ChannelInfoBean;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.SysDevAbilityInfoBean;
import com.lib.sdk.bean.web.H5DevListBean;
import com.lib.sdk.bean.web.H5TitleBean;
import com.manager.account.XMAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.db.XMUserInfo;
import com.manager.sysability.SysAbilityManager;
import com.utils.XUtils;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    public static final String CLOUD_STORAGE_BASE_URL = "https://boss22-api.xmcsrv.net/index?";
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
    /**
     * 支付宝
     */
    private static final String URL_ALIPAYS = "alipays://platformapi";
    /**
     * 微信
     */
    private static final String URL_WECHAT_PAYS = "weixin://wap/pay";
    /**
     * PayPal
     */
    private static final String URL_PAYPAL = "https://www.paypal.com";
    @BindView(R.id.wv_cloud_web_view)
    WebView webView;
    @BindView(R.id.iv_more)
    ImageView ivMore;
    @BindView(R.id.xb_cloud_server_title)
    XTitleBar xTitleBar;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.view_split_line)
    View splitLine;
    String cloudUrl;

    H5TitleBean h5TitleBean;

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
        } catch (Exception e) {
            Toast.makeText(this, "无法创建webView", Toast.LENGTH_LONG).show();
            finish();
            e.printStackTrace();
            return;
        }

        ivMore.setImageResource(R.drawable.selector_cloud_server_more);
        ivMore.setOnClickListener(v -> {
            //更多
            if (h5TitleBean != null && h5TitleBean.rightMoreFun != null && h5TitleBean.rightMoreFun.size() > 0) {
                toShowMorePop();
            } else {
                webView.evaluateJavascript("javascript:XmAppJsSDK.moreContentResponse()", null);
            }
        });

        xTitleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                if (XUtils.isFastDoubleClick()) {
                    return;
                }
                baseBack();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void baseBack() {
        webView.evaluateJavascript("javascript:XmAppJsSDK.backResponse()", null);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }


        String goodsType = intent.getStringExtra("goodsType");
        if (TextUtils.isEmpty(goodsType)) {
            goodsType = GOODS_TYPE_CLOUD_STORAGE;
        }

        presenter.setGoodsType(goodsType);

        WebSettings webSetting = webView.getSettings();
        webSetting.setTextZoom(100);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setUseWideViewPort(false);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setLoadWithOverviewMode(true);//网页自适应
        webSetting.setDomStorageEnabled(true);//开启本地DOM存储
        webSetting.setLoadsImagesAutomatically(true); // 加载图片
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSetting.setDefaultTextEncodingName("utf-8");//设置编码格式
        webView.addJavascriptInterface(this, "XmAppJsSDK");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("ccy", "shouldOverrideUrlLoading = " + url);
                if (StringUtils.isStringNULL(url)) {
                    System.out.println("shouldOverrideUrlLoading:" + url);
                    return super.shouldOverrideUrlLoading(view, url);
                }

                if (url.startsWith("https://payapp.weixin.qq.com/papay")) {
                    try {
                        String decoderUrl = URLDecoder.decode(url, "utf-8");
                        if (decoderUrl.contains("return_url.do")) {
                            String[] split = decoderUrl.split("return_url.do");
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    String baseUrl = CLOUD_STORAGE_BASE_URL.replace("index", "wx");  // 这里需要吧index 该称微信 才能够正常跳转回来

                                    String loadUrl = baseUrl + "/return_url.do" + split[1];
                                    if (webView != null){
                                        webView.loadUrl(loadUrl);
                                    }
                                }
                            }, 6000);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                //支付宝支付
                if (url.contains(URL_ALIPAYS)) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //云服务弹窗提示
                        XMPromptDlg.onShow(CloudWebActivity.this,
                                getString(R.string.Install_Alipay_Application),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (view!=null){
                                            view.loadUrl(cloudUrl);  //回主页
                                        }
                                    }
                                }, null);
                    }
                    return true;
                }

                //微信支付
                if (url.contains(URL_WECHAT_PAYS)) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        XMPromptDlg.onShow(CloudWebActivity.this,
                                getString(R.string.Install_WeChat_Application),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (view!=null) {
                                            view.loadUrl(cloudUrl);  //回主页
                                        }
                                    }
                                }, null);
                    }

                    return true;
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
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //完成支付跳回主界面后，要清空历史页面
                if (url.contains(CLOUD_STORAGE_BASE_URL)) {
                    view.clearHistory();
                } else {

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
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//允许https/http混合加载
        }

        cloudUrl = createCloudUrl();

        if (cloudUrl != null) {
            webView.loadUrl(cloudUrl);
        }
    }

    private String createCloudUrl() {
        String userId = FunSDK.GetFunStrAttr(EFUN_ATTR.LOGIN_USER_ID);
        if (userId == null) {
            XMPromptDlg.onShow(this, "用户信息获取失败", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            return null;
        }

        //只有中文或英文,
        String lan = Locale.getDefault().getLanguage();
        if (lan.compareToIgnoreCase("zh") == 0) {
            lan = "zh-CN";//不是zh_CN
        } else {
            lan = "en";
        }

        Map<String, String> urlMap = new LinkedHashMap<>();
        urlMap.put("sn", presenter.getDevId());//设备序列号
        urlMap.put("lang", lan);//语言
        urlMap.put("appKey", DemoConstant.APP_KEY);
        urlMap.put("authorization", DevDataCenter.getInstance().getAccessToken());//账号登录Token
        urlMap.put("classifyId", presenter.getGoodsType());// 根据需求传入对应的值，比如云存储->GOODS_TYPE_CLOUD_STORAGE，云电话->GOODS_TYPE_CALL，4G流量->GOODS_TYPE_FLOW, 如果不传的话默认云服务首页
        urlMap.put("appVer",XUtils.getVersion(this));
        urlMap.put("routing","buy");//首页传index 购买页面传buy
        return getUrl(CLOUD_STORAGE_BASE_URL,urlMap);
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
        if (webView != null) {
            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);
            webView.removeAllViews();
            webView.destroy();

            ViewGroup view = (ViewGroup) getWindow().getDecorView();
            view.removeAllViews();
            ((ViewGroup) webView.getParent()).removeView(webView);
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
    public void closeWindow() {
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
     *
     * @param status 如果status是1表示需要跳转到信用卡页面
     */
    @JavascriptInterface
    public void payPalCard(int status) {

    }

    /**
     * 传递数据到H5端
     *
     * @return
     */
    @JavascriptInterface
    public String getSupportCloudServerDevList() {
        H5DevListBean bean = new H5DevListBean();
        // 获取手机信息
        bean.setAppName(XUtils.getAppName(this));
        bean.setAppVersion(XUtils.getVersion(this) + "(" + XUtils.getVersionCode(this) + ")");
        bean.setPackageName(XUtils.getPackageName(this));
        if (DevDataCenter.getInstance().isLoginByAccount()) {
            XMUserInfo xmUserInfo = XMAccountManager.getInstance().getXmUserInfo();
            bean.setUserId((xmUserInfo == null ? null : xmUserInfo.getUserId()));
        }
        List<H5DevListBean.DevBean> list = new ArrayList<>();
        List<String> devList = DevDataCenter.getInstance().getDevList();
        for (String devId : devList) {
            SysDevAbilityInfoBean sysDevAbilityInfoBean = SysAbilityManager.getInstance().getSysDevAbilityFromLocal(devId);
            if (sysDevAbilityInfoBean != null) {
                XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(devId);
                H5DevListBean.DevBean devBean = new H5DevListBean.DevBean();
                devBean.setDevId(devId);
                devBean.setDevName(xmDevInfo.getDevName());
                devBean.setDevType(xmDevInfo.getDevType());//设备类型（兼容老的devType和新的pid）参考： 03.设备类型

                SysAbilityManager.getInstance().getSysDevAbilityFromLocal(devId);
                devBean.setOemId(sysDevAbilityInfoBean.getMfrsOemId());
                devBean.setDevState(xmDevInfo.getDevState() == OFF_LINE ? 0 : 1);//设备状态，枚举： 0 -> 离线，1 -> 在线，2 -> 休眠，3 -> 深度休眠,
                devBean.setPid(xmDevInfo.getPid());
                List<ChannelInfoBean> channelInfoBeans = xmDevInfo.getSdbDevInfo().getChnInfos();
                if (channelInfoBeans != null && !channelInfoBeans.isEmpty()) {
                    List<H5DevListBean.ChnBean> chnBeans = new ArrayList<>();
                    for (ChannelInfoBean channelInfoBean : channelInfoBeans) {
                        chnBeans.add(new H5DevListBean.ChnBean(channelInfoBean.getChnId(), channelInfoBean.getChnState()));
                    }

                    devBean.setChnList(chnBeans);
                }

                list.add(devBean);
            }
        }
        bean.setDevList(list);
        Gson gson = new GsonBuilder().create();
        return gson.toJson(bean);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            baseBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @JavascriptInterface
    public void setTopBottomBar(String jsonData) {
        if (StringUtils.isStringNULL(jsonData)) {
            return;
        }

        System.out.println("jsonData:" + jsonData);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    h5TitleBean = JSON.parseObject(jsonData, H5TitleBean.class);
                    if (h5TitleBean != null) {
                        ivMore.setVisibility(View.VISIBLE);
                        splitLine.setVisibility(View.VISIBLE);
                        ivClose.setVisibility(View.VISIBLE);
                        ivClose.setImageResource(R.drawable.selector_cloud_server_close);
                        xTitleBar.setTitleText(h5TitleBean.getTitle());
                        if (!TextUtils.isEmpty(h5TitleBean.getTitleBackground())) {
                            xTitleBar.setBackgroundColor(Color.parseColor(h5TitleBean.getTitleBackground()));
                            xTitleBar.setShowBottomLine(false);
                        } else {
                            xTitleBar.setBackgroundColor(Color.WHITE);
                            xTitleBar.setShowBottomLine(true);
                        }
                        //是否显示标题栏
                        xTitleBar.setViewVisibility(XTitleBar.ViewName.mLeftIv, h5TitleBean.isBackShow() ? View.VISIBLE : View.GONE);
                        xTitleBar.requestLayout();
                        showMore(h5TitleBean);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void showMore(H5TitleBean h5TitleBean) {
        if (h5TitleBean.rightMoreFun == null || h5TitleBean.rightMoreFun.size() == 0) {
            ivMore.setVisibility(View.GONE);
            splitLine.setVisibility(View.GONE);
            ivClose.setVisibility(View.VISIBLE);
            ivClose.setImageResource(R.mipmap.icon_disk_home_close);
        }
    }

    private void toShowMorePop() {
        ArrayList<H5TitleBean.RightFunBtn> list = new ArrayList<>(h5TitleBean.rightMoreFun);
        TextView textView = new TextView(this);
        textView.setText("功能选择:");
        ListView listView = new ListView(this);
        listView.setBackgroundColor(Color.WHITE);
        ArrayList funNameList = new ArrayList();
        for (H5TitleBean.RightFunBtn rightFunBtn : list) {
            funNameList.add(rightFunBtn.funName);
        }

        String[] data = new String[funNameList.size()];
        funNameList.toArray(data);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listView.setAdapter(adapter);


        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.addView(textView);
        layout.addView(listView);
        Dialog dialog = XMPromptDlg.onShow(this, layout);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                H5TitleBean.RightFunBtn rightFunBtn = list.get(position);
                webView.evaluateJavascript("javascript:XmAppJsSDK.getRightFunResponse('" + rightFunBtn.funKey + "')", null);
                dialog.dismiss();
            }
        });
    }
}
