# Cloud Service
```
For details, see CloudWebActivity file in Demo
WebView webView;
webView.getSettings().setTextZoom(100);
webView.getSettings().setJavaScriptEnabled(true);
webView.addJavascriptInterface(this, "XmAppJsSDK");
String ua = webView.getSettings().getUserAgentString();
webView.getSettings().setUserAgentString(ua + ";xm-android-m");
webView.getSettings().setCacheMode(WebSettings.LOAD_NORMAL);


Map<String, String> urlMap = new LinkedHashMap<>();
urlMap.put("user_id", userId);//Account Indicates the ID of a login user
urlMap.put("uuid", presenter.getDevId());//Equipment serial number
urlMap.put("lan",lan);//language
urlMap.put("appKey",appKey);//A registered appkey on an open platform
urlMap.put("goods",presenter.getGoodsType());//Product type: cloud storage, traffic, etc
urlMap.put("devName", devInfo.getDevName());//Device name

String url = getUrl("https://boss2.xmcsrv.net/index.do",urlMap)
webView.loadUrl(url);

```