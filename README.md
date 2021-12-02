# [Interface Documentation](https://oppf.xmcsrv.com/static/md/docs/javadoc/index.html)
# Fast Integration
## 1. By Gradle integration
### 1.1 Create a Project in Android Studio.
### 1.2 Configure build.gradle：Add the dependencies downloaded in the integration preparation to the build.gradle file.
```
repositories {
    mavenCentral()
}

defaultConfig {
    ndk {
        abiFilters "armeabi-v7a","arm64-v8a"
    }
    // Currently, armeabi-v7a and arm64-v8a are supported. Please do not   add x86 to the project. Otherwise, it will not work
 }
dependencies {
    // The latest stable version of FunSDK:
    implementation 'io.github.xmcamera:libxmfunsdk:4.4' //Or directly obtain the AAR file from the demo for loading
    implementation 'io.github.xmcamera:uilibrary:2.1' //Or directly obtain the AAR file from the demo for loading
    implementation 'com.alibaba:fastjson:1.1.72.android'
    implementation 'org.apache.commons:commons-lang3:3.12.0'
    implementation "com.squareup.okhttp3:logging-interceptor:4.9.2"
    implementation 'com.squareup.okhttp3:okhttp:4.9.2'
}
```

## 2. Initialization
### 2.1. Visit ([https://aops.jftech.com/](https://aops.jftech.com/)) The new guidelines, register to  be an open platform developers, then go to the [console] - [page] to create Android applications, and other applications can be obtained through the review after the AppKey, movedCard and AppSecret and other information.
### 2.2. Application processing
```
// If it is a P2P customized server, please refer to the following method
// int customPwdType The default encryption type is 0
// String customPwd The encryption field is passed "" by default.
// String customServerAddr Customize the server domain name or IP address
// int customPort Customizing a server port
// Get appUuid, appKey, appSecret, appMovedCard and other information from https://developer.jftech.com
// isUseDefaultConfigPath Whether to use the default configuration path, which will save the device password and other information locally on the phone
// XMFunSDKManager.getInstance(0,"",customServerAddr,customPort).initXMCloudPlatform(this,appUuid,appKey,appSecret,appMovedCard,true);

XMFunSDKManager.getInstance().initXMCloudPlatform(this,appUuid,appKey,appSecret,appMovedCard,true);
//If it is a low-power device (doorbell, door lock, etc.), you also need to call the following methods:
FunSDK.SetFunIntAttr(EFUN_ATTR.SUP_RPS_VIDEO_DEFAULT, SDKCONST.Switch.Open);
//Initial printing:
XMFunSDKManager.getInstance().initLog();
```
## 3. Obfuscation
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
## 4.Account Management
### 4.1 [Account Registration](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Account%20Management/Account-Registration)
### 4.2 [Login Account](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Account%20Management/Login-Account)
### 4.3 [Change password](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Account%20Management/Change-password)
### 4.4 [User Information](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Account%20Management/User-Information)
### 4.5 [Forgot password](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Account%20Management/Forgot-password)
### 4.6 [Device List](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Account%20Management/Device-List)
### 4.7 [Account Logout](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Account%20Management/Account-Logout)
### 4.8 [Account Cancellation](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Account%20Management/Account-Cancellation)

## 5.Device Management
### 5.1 [Add Device](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Add-Device)
### 5.2 [Login device](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Login-device)
### 5.3 [Live Video](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Live-Video)

### 5.4 [Device Configuration](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Device-Configuration)
- [Device 4G Signal](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Device%20Configuration/Device-4G-Signal)
- [Device WiFi Signal](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Device%20Configuration/Device-WiFi-Signal)
- [Get Device Disk Information](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Device%20Configuration/Get-Device-Disk-Information)
- [Recording Configuration](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Device%20Configuration/Recording-Configuration)
- [Time Synchronization](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Device%20Configuration/Time-Synchronization)

### 5.5 [PTZ](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/PTZ)
### 5.6 [SD Card Images](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/SD-Card-Images)
### 5.7 [Video Playback](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Video-Playback)
### 5.8 [Delete Device](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Delete-Device)
### 5.9 [Alarm Push](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Alarm-Push)
### 5.10 [Device Firmware Upgrade](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Device-Firmware-Upgrade)
### 5.11 [Cloud Service](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Cloud-Service)
### 5.12 [Capability](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Capability)
### 5.13 [Device Type](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Device-Type)
### 5.14 [Error Code](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Error-Code)
### 5.15 [Server-Side Alarm Message Integration](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Server-Side-Alarm-Message-Integration)
### 5.16 [Share Devices](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Share-Devices)
### 5.17 [Successful Networking Process (for Token-based Login Devices)](https://gitlab.xmcloud.io/demo/FunSDKDemo_Android/-/wikis/EN/Device%20Management/Successful-Networking-Process-(for-Token-based-Login-Devices))

## 6.Feature Map
<img src="https://obs-xm-pub.obs.cn-south-1.myhuaweicloud.com/docs/20240222/1708580505803.png" alt="Image" width="560" height="1450">
