package demo.xm.com.xmfunsdkdemo.ui.feedback.customservice.view;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

import static demo.xm.com.xmfunsdkdemo.base.DemoConstant.APP_UUID;
import static demo.xm.com.xmfunsdkdemo.ui.device.cloud.view.CloudWebActivity.GOODS_TYPE_CLOUD_STORAGE;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lib.EFUN_ATTR;
import com.lib.FunSDK;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.manager.ScreenOrientationManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.utils.XUtils;
import com.xm.base.OkHttpManager;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.cloud.view.CloudWebActivity;
import demo.xm.com.xmfunsdkdemo.ui.entity.EventBusPushInfo;
import demo.xm.com.xmfunsdkdemo.ui.entity.FeedBackDataBean;
import demo.xm.com.xmfunsdkdemo.ui.entity.OpenWebViewBean;
import demo.xm.com.xmfunsdkdemo.ui.feedback.customservice.manager.GlideEngine;
import demo.xm.com.xmfunsdkdemo.ui.feedback.customservice.manager.LogManager;
import demo.xm.com.xmfunsdkdemo.utils.LanguageUtils;

public class CustomServiceWebViewFragment extends Fragment {


    //服务域名
    //todo 按实际情况填写替换
    public static final String DOMAIN_URL = "https://XXX";

    //主oemId,公司主要使用的设备oemId
    //todo 按实际情况填写替换
    public static final String MAIN_OEM_ID = "XXX";
    public static final String OPEN_USER_ID = APP_UUID;



    protected Activity mActivity;

    protected View mLayout;

    public static final String TAG = "CustomServiceWebView";

    private static final String CUSTOM_SERVICE_URL = DOMAIN_URL+"/h5/customH5";
    /**
     * 客服系统帮助
     */
    private static final String HELP_URL = DOMAIN_URL+"/h5/customH5/index.html#/";



    /**
     * 客服系统聊天
     */
    private static final String CHAT_URL = DOMAIN_URL+"/h5/customH5/index.html#/pages/liveChat/index";





    private final int TYPE_IMAGE = 1; // TYPE_IMAGE = 1   TYPE_VIDEO = 2
    private final int TYPE_VIDEO = 2;
    private int chooseType;


    private WebView mWebView;

    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private ValueCallback<Uri> mUploadCallbackBelow;
    private Uri imageUri;
    private boolean mIsHelpPage;



    //如果以这个路径为后缀  就说明是需要下载的文件
    private String[] endList = {".pdf", ".ppt", ".doc", ".docx", ".xls", ".xlsx", ".txt", ".zip", ".rar", ".pptx", ".mp4", ".avi"};

    private boolean mIsNeedReloadUrl = false;

    private ScreenOrientationManager mScreenOrientationManager = ScreenOrientationManager.getInstance();

    /**
     * 视频全屏参数
     */
    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS =
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private View customView;
    private FrameLayout fullscreenContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;


    public static CustomServiceWebViewFragment newInstance(boolean isHelpPage) {
        Bundle args = new Bundle();
        args.putBoolean("isHelpPage", isHelpPage);
        CustomServiceWebViewFragment fragment = new CustomServiceWebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mActivity = getActivity();
        Bundle it = getArguments();
        mIsHelpPage = it.getBoolean("isHelpPage");
        mLayout = inflater.inflate(R.layout.fragment_customer_service_web_view, null);
        if(mIsHelpPage){
            ((XTitleBar) mLayout.findViewById(R.id.live_chat_title)).setVisibility(View.GONE);
        } else {
            ((XTitleBar) mLayout.findViewById(R.id.live_chat_title)).setVisibility(View.VISIBLE);
            ((XTitleBar) mLayout.findViewById(R.id.live_chat_title)).setTitleText(FunSDK.TS("CZ_OnlineService"));
        }

        initWebView();
        return mLayout;
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        mWebView = mLayout.findViewById(R.id.webview);
        ((XTitleBar) mLayout.findViewById(R.id.live_chat_title)).setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                backPressed();
            }
        });
        //允许https/http混合加载
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        mWebSettings.setSupportZoom(true);//是否可以缩放，默认true
        mWebSettings.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        mWebSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        mWebSettings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        mWebSettings.setDomStorageEnabled(true);//开启本地DOM存储
        mWebSettings.setLoadsImagesAutomatically(true); // 加载图片
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        mWebSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        mWebSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        mWebSettings.setDomStorageEnabled(true);//设置适应Html5 重点是这个设置
        mWebView.addJavascriptInterface(this, "XmAppJsSDK");
        reloadUrl();
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (checkEnd(url)) {
                    //文件类型执行下载操作
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse(request.getUrl().toString()));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if(!TextUtils.isEmpty(url) && !checkCustomServiceUrl(url) && ((url.startsWith("http://") || url.startsWith("https://")))){
                    //非客服页面的url，跳转到浏览器打开
                    Intent intent = new Intent();
                    // 设置意图动作为打开浏览器
                    intent.setAction(Intent.ACTION_VIEW);
                    // 声明一个Uri
                    Uri uri = Uri.parse(url);
                    intent.setData(uri);
                    mActivity.startActivity(intent);
                    return true;
                } else {
                    //正常加载其它的标签
                    return super.shouldOverrideUrlLoading(view, request);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LoadingDialog.getInstance(getActivity()).dismiss();
            }

        });

        mWebView.setWebChromeClient(new WebChromeClient(){

            @Nullable
            @Override
            public View getVideoLoadingProgressView() {
                FrameLayout frameLayout = new FrameLayout(getActivity());
                frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                return frameLayout;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                showCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                hideCustomView();
            }
            private void openFileChooser(ValueCallback<Uri> uploadMsg) {
                // (2)该方法回调时说明版本API < 21，此时将结果赋值给 mUploadCallbackBelow，使之 != null
                mUploadCallbackBelow = uploadMsg;
                openAlbum(chooseType);
            }


            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                // 这里我们就不区分input的参数了，直接用拍照
                mUploadCallbackBelow = uploadMsg;
                openFileChooser(uploadMsg);
            }

            /**
             * 16(Android 4.1.2) <= API <= 20(Android 4.4W.2)回调此方法
             */
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                // 这里我们就不区分input的参数了，直接用拍照
                mUploadCallbackBelow = uploadMsg;
                openFileChooser(uploadMsg);
            }

            /**
             * API >= 21(Android 5.0.1)回调此方法
             */
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
                // (1)该方法回调时说明版本API >= 21，此时将结果赋值给 mUploadCallbackAboveL，使之 != null
                mUploadCallbackAboveL = valueCallback;
                if(fileChooserParams!=null && fileChooserParams.isCaptureEnabled()){
                    takePhoto();
                }else{
                    String[] acceptTypes = fileChooserParams.getAcceptTypes();
                    if(acceptTypes != null && acceptTypes.length > 0){
                        if(acceptTypes[0].contains("image")){
                            chooseType = TYPE_IMAGE;
                        }else{
                            chooseType = TYPE_VIDEO;
                        }
                    }else{
                        chooseType = TYPE_VIDEO;
                    }
                    openAlbum(chooseType);
                }
                return true;
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume()");
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Log.d(TAG,"onHiddenChanged()=false");
            if(mIsNeedReloadUrl){
                reloadUrl();
            }
        }
    }


    public void setNeedReloadUrl(){
        Log.d(TAG,"setNeedReloadUrl()");
        mIsNeedReloadUrl = true;
    }


    //设备增加删除需要重新加载页面
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveEventBusPushInfo(EventBusPushInfo pushInfo) {
        if (pushInfo != null) {
            switch (pushInfo.getOperationType()) {
                case ADD_DEV:
                case REMOVE_DEV:
                    setNeedReloadUrl();
                    break;
                default:
                    break;
            }
        }
    }


    public void reloadUrl(){
        if(mWebView!=null){
            mWebView.loadUrl((mIsHelpPage ? HELP_URL : CHAT_URL) + "?lang="+ LanguageUtils.getLanguageNoTxt()+"&openUserId="+OPEN_USER_ID+"&oemId="+MAIN_OEM_ID);
            mIsNeedReloadUrl = false;
        }
    }

    private boolean checkEnd(String url) {
        for (int i = 0; i < endList.length; i++) {
            if (url.endsWith(endList[i])) {
                return true;
            }
        }
        return false;
    }


    private boolean checkCustomServiceUrl(String url) {
        if(!TextUtils.isEmpty(url) && url.contains(CUSTOM_SERVICE_URL)){
            return true;
        }
        return false;
    }

    /**
     * 调用相机
     */
    private void takePhoto() {
        PictureSelector.create(getActivity())
                .openCamera(SelectMimeType.ofAll())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        if (result != null && result.size() > 0) {
                            String availablePath = result.get(0).getRealPath();
                            File fileByPath = FileUtils.getFileByPath(availablePath);
                            Uri fromFile = Uri.fromFile(fileByPath);
                            Intent data = new Intent();
                            data.setData(fromFile);
                            data.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                            onActivityResultAboveL(3, -1, data);
                        }
                    }

                    @Override
                    public void onCancel() {
                        cancelFilePathCallback();
                    }
                });

    }

    public void openAlbum(int chooseType){
        PictureSelector.create(getActivity())
                .openGallery(chooseType)
                .setImageEngine(GlideEngine.createGlideEngine())
                .setSelectionMode(SelectModeConfig.SINGLE)
                .isPreviewImage(false)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        if (result != null && result.size() > 0) {
                            String availablePath = result.get(0).getRealPath();
                            File fileByPath = FileUtils.getFileByPath(availablePath);
                            Uri fromFile = Uri.fromFile(fileByPath);
                            Intent data = new Intent();
                            data.setData(fromFile);
                            data.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                            onActivityResultAboveL(3, -1, data);
                        }
                    }
                    @Override
                    public void onCancel() {
                        cancelFilePathCallback();

                    }
                });

    }



    /**
     * H5端请求App上传日志文件
     *
     * @return
     */
    @JavascriptInterface
    public void uploadLogFile(String uploadLogFileUrl) {
        try {
            File file = LogManager.getLogFileByZip();

            if (file == null) {
                return;
            }

            LogManager.uploadLogFile(uploadLogFileUrl, file, new OkHttpManager.OnOkHttpListener() {
                @Override
                public void onSuccess(String originalJsonData, Object result) {
                    Log.e("lmy", "uploadLogFile success  originalJsonData:" + originalJsonData);
                }

                @Override
                public void onFailed(int errorId, String errorMsg) {
                    Log.e("lmy", "uploadLogFile onFailed  errorId:" + errorId + " errorMsg:" + errorMsg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 传递数据到H5端
     * @return
     */
    @JavascriptInterface
    public String getAppDeviceData() {
        FeedBackDataBean bean = new FeedBackDataBean();
        // 获取手机信息
        bean.appName = XUtils.getAppName(getActivity());
        bean.appversion = XUtils.getVersion(getActivity()) + "(" + XUtils.getVersionCode(getActivity()) + ")";
        bean.packageName = XUtils.getPackageName(getActivity());


        bean.userId = FunSDK.GetFunStrAttr(EFUN_ATTR.LOGIN_USER_ID);


        List<XMDevInfo> devList = new ArrayList<>();

        for (String devId : DevDataCenter.getInstance().getDevList()) {
            XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(devId);
            if (xmDevInfo != null) {
                devList.add(xmDevInfo);
            }
        }
        List<FeedBackDataBean.DevBean> list = new ArrayList<>();
        if(devList != null && devList.size() > 0){
            for(int i = 0 ; i < devList.size(); i++){
                FeedBackDataBean.DevBean devBean = new FeedBackDataBean.DevBean();
                devBean.devId = devList.get(i).getDevId();
                devBean.devName =  devList.get(i).getDevName();
                devBean.devType = devList.get(i).getDevType();
                String loginName = TextUtils.isEmpty(FunSDK.DevGetLocalUserName(devList.get(i).getDevId())) ? "admin" :  FunSDK.DevGetLocalUserName(devList.get(i).getDevId());
                String content  = devList.get(i).getDevId() + "," + loginName +"," + FunSDK.DevGetLocalPwd(devList.get(i).getDevId()) + "," +devList.get(i).getDevType();
                devBean.qrcodeInfo = FunSDK.EncGeneralDevInfo(content);
                //todo
                //caps获取的oemId，非必须，最好获取一下
                //devBean.oemId = "xxx";
                //扫码添加的二维码信息中的oemId，非必须
                //devBean.qrCodeOem = "xxx";
                //设备pid，非必须，最好获取一下
                //devBean.pid = "xxx";
                list.add(devBean);
            }
        }
        bean.devList = list;
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(bean);
        return json;
    }

    @JavascriptInterface
    public void back(){
        new Handler(Looper.getMainLooper()).post(() -> {
            backPressed();
        });

    }




    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                results = new Uri[]{imageUri};
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        if (results != null) {
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        } else {
            results = new Uri[]{};
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        }
        return;
    }



    private void cancelFilePathCallback() {
        if (mUploadCallbackAboveL != null) {
            mUploadCallbackAboveL.onReceiveValue(null);
            mUploadCallbackAboveL = null;
        } else if (mUploadCallbackBelow != null) {
            mUploadCallbackBelow.onReceiveValue(null);
            mUploadCallbackBelow = null;
        }
    }

    public void onBackPressed() {
        if(mIsHelpPage){
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return;
            }
        }
        backPressed();
    }


    public void backPressed() {
        if(mActivity==null){
            return;
        }
        mActivity.finish();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @JavascriptInterface
    public void sendVideoSizeToApp(int width, int height) {
        Log.d("sendVideoSizeToApp","width="+width+",height="+height);
        mLastFullScreenVideoWidth = width;
        mLastFullScreenVideoHeight = height;
    }

    private int mLastFullScreenVideoWidth = -1;
    private int mLastFullScreenVideoHeight = -1;


    /**
     * 视频播放全屏
     *
     * @param view
     * @param callback
     */
    private void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        Log.d("showCustomView","width="+view.getWidth()+",height="+view.getHeight());
        Log.d("showCustomView","mLastFullScreenVideoWidth="+mLastFullScreenVideoWidth+",mLastFullScreenVideoHeight="+mLastFullScreenVideoHeight);
        //如果视频宽度大于视频高度则全屏的同时要进行旋转
        if(mLastFullScreenVideoWidth>0 && mLastFullScreenVideoHeight>0 && mLastFullScreenVideoWidth>mLastFullScreenVideoHeight){
            mScreenOrientationManager.landscapeScreen(getActivity(), false);
        }
//        mLastFullScreenVideoWidth = -1;
//        mLastFullScreenVideoHeight = -1;

        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }
        getActivity().getWindow().getDecorView();
        FrameLayout decor = (FrameLayout) getActivity().getWindow().getDecorView();
        fullscreenContainer = new FullscreenHolder(getActivity());
        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
        customView = view;
        setStatusBarVisibility(false);
        customViewCallback = callback;
    }

    /**
     * 隐藏视频全屏
     */
    private void hideCustomView() {
        mScreenOrientationManager.portraitScreen(getActivity(), false);
        if (customView == null) {
            return;
        }
        setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) getActivity().getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        customView = null;
        customViewCallback.onCustomViewHidden();
        mWebView.setVisibility(View.VISIBLE);
    }

    /**
     * 全屏容器界面
     */
    static class FullscreenHolder extends FrameLayout {
        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getActivity().getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 重新打开一个云服务WebView
     */
    @JavascriptInterface
    public void openCloudWebView(String jsonData) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                OpenWebViewBean openWebViewBean = new Gson().fromJson(jsonData, OpenWebViewBean.class);
                ((CustomServiceWebViewActivity)getActivity()).turnToActivity(CloudWebActivity.class,new String[][]{{"devId",openWebViewBean.getSn(),"goodsType",GOODS_TYPE_CLOUD_STORAGE}});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
