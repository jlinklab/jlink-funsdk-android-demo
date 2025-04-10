# [English documentation](README_EN.md)
# [接口文档](https://oppf.xmcsrv.com/static/md/docs/javadoc/index.html)

# 快速集成
## 1. 通过Gradle集成
### 1.1 在Android Studio中新建工程。
### 1.2 在build.gradle文件里添加dependencies依赖库。
```
repositories {
    mavenCentral()
}

defaultConfig {
    ndk {
        abiFilters "armeabi-v7a","arm64-v8a"
    }
    //目前只支持armeabi-v7a和arm64-v8a，请不要在工程中添加x86，不然无法运行
 }

dependencies {
    implementation fileTree(include: ['*.jar', '*.aar'], dir: 'libs')
    implementation 'com.jftechsoft.funsdk:funsdk:5.0.0a' //或者直接从Demo中获取aar文件加载
    implementation 'io.github.xmcamera:uilibrary:2.1' //或者直接从Demo中获取aar文件加载
    implementation 'com.alibaba:fastjson:1.1.72.android'
    implementation 'org.apache.commons:commons-lang3:3.12.0'
    implementation "com.squareup.okhttp3:logging-interceptor:4.9.2"
    implementation 'com.squareup.okhttp3:okhttp:4.9.2'
}
```

```
repositories {
    maven { url 'https://repo.jftechsoft.com/repository/maven-releases/'}
}
```


## 2. 初始化
### 2.1.前往（[https://developer.jftech.com](https://aops.jftech.com/)） 新人指南，注册申请成为开放平台开发者，然后到【控制台】-【创建应用页面】中创建Android应用，等应用审核通过后就可以获取到AppKey、movedCard和AppSecret等信息。
### 2.2.在Application文件中添加以下几行初始化代码
```
// 如果是P2P定制服务器的话请参考以下方法
// int customPwdType 加密类型 默认传0
// String customPwd 加密字段 默认传 ""
// String customServerAddr 定制服务器域名或IP
// int customPort 定制服务器端口
// 从开发平台上获取到appUuid、appKey、appSecret、appMovedCard等信息
// isUseDefaultConfigPath 是否使用默认配置路径，该路径会在手机本地保存设备密码等信息
// XMFunSDKManager.getInstance(0,"",customServerAddr,customPort).initXMCloudPlatform(this,appUuid,appKey,appSecret,appMovedCard,true);

XMFunSDKManager.getInstance().initXMCloudPlatform(this,appUuid,appKey,appSecret,appMovedCard,true);
如果是低功耗设备（门铃、门锁等）还需要调用以下方法：
FunSDK.SetFunIntAttr(EFUN_ATTR.SUP_RPS_VIDEO_DEFAULT, SDKCONST.Switch.Open);
初始化打印：
XMFunSDKManager.getInstance().initLog();

```
## 3. 混淆处理
```
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends android.app.Fragment
-keep public class * extends androidx.appcompat.app.AppCompatActivity
-keep public class com.lib.** {*;}
-keep public class com.basic.** {*;}
-keep public class com.manager.**{public<methods>;public<fields>;}
-keep public  class com.video.opengl.GLSurfaceView20 {*;}
-keep public class com.xm.ui.**{public<methods>;protected<methods>;public<fields>;protected<fields>;}
-keep public class com.utils.**{public<methods>;}
-keep public class com.xm.activity.base.XMBasePresenter{public protected *;}
-keep public class com.xm.activity.base.XMBaseActivity{public<methods>;protected <fields>;}
-keep public class com.xm.activity.base.XMBaseFragment{public<methods>;protected <fields>;}
-keep public class com.xm.kotlin.**{public<methods>;protected<methods>;public<fields>;protected<fields>;}
-keep public class com.xm.ui.**{public<methods>;}
-keep public class com.xm.linke.**{public<methods>;}
-keep public class com.**$*{*;}
```

## 4.功能导图 
![](https://obs-xm-pub.obs.cn-south-1.myhuaweicloud.com/docs/20231216/1702718870769.png)

## 5.[详细开发文档](https://docs.jftech.com/#/docs?menusId=ab0ed73834f54368be3e375075e27fb2&siderid=45357c529496431590a7e3463b7cc520&lang=zh&directory=true)
