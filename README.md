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
    implementation 'com.jftechsoft.funsdk:funsdk:5.1.1' //Or directly obtain the AAR file from the demo for loading
    implementation 'io.github.xmcamera:uilibrary:2.1' //Or directly obtain the AAR file from the demo for loading
    implementation 'com.alibaba:fastjson:1.1.72.android'
    implementation 'org.apache.commons:commons-lang3:3.12.0'
    implementation "com.squareup.okhttp3:logging-interceptor:4.9.2"
    implementation 'com.squareup.okhttp3:okhttp:4.9.2'
}
```

```
repositories {
    maven { url 'https://repo.jftechsoft.com/repository/maven-releases/' }
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
### 4.1 [Account Registration](/wikis/EN/Account%20Management/Account%20Registration.md)
### 4.2 [Login Account](/wikis/EN/Account%20Management/Login%20Account.md)
### 4.3 [Change password](/wikis/EN/Account%20Management/Change%20password.md)
### 4.4 [User Information](/wikis/EN/Account%20Management/User%20Information.md)
### 4.5 [Forgot password](/wikis/EN/Account%20Management/Forgot%20password.md)
### 4.6 [Device List](/wikis/EN/Account%20Management/Device%20List.md)
### 4.7 [Account Logout](/wikis/EN/Account%20Management/Account%20Logout.md)
### 4.8 [Account Cancellation](/wikis/EN/Account%20Management/Account%20Cancellation.md)

## 5.Device Management
### 5.1 [Add Device](/wikis/EN/Device%20Management/Add%20Device.md)
### 5.2 [Login device](/wikis/EN/Device%20Management/Login%20device.md)
### 5.3 [Live Video](/wikis/EN/Device%20Management/Live%20Video.md)

### 5.4 [Device Configuration](/wikis/EN/Device%20Management/Device%20Configuration.md)
- [Device 4G Signal](/wikis/EN/Device%20Management/Device%20Configuration/Device%204G%20Signal.md)
- [Device WiFi Signal](/wikis/EN/Device%20Management/Device%20Configuration/Device%20WiFi%20Signal.md)
- [Get Device Disk Information](/wikis/EN/Device%20Management/Device%20Configuration/Get%20Device%20Disk%20Information.md)
- [Recording Configuration](/wikis/EN/Device%20Management/Device%20Configuration/Recording%20Configuration.md)
- [Time Synchronization](/wikis/EN/Device%20Management/Device%20Configuration/Time%20Synchronization.md)

### 5.5 [PTZ](/wikis/EN/Device%20Management/PTZ.md)
### 5.6 [SD Card Images](/wikis/EN/Device%20Management/SD%20Card%20Images.md)
### 5.7 [Video Playback](/wikis/EN/Device%20Management/Video%20Playback.md)
### 5.8 [Delete Device](/wikis/EN/Device%20Management/Delete%20Device.md)
### 5.9 [Alarm Push](/wikis/EN/Device%20Management/Alarm%20Push.md)
### 5.10 [Device Firmware Upgrade](/wikis/EN/Device%20Management/Device%20Firmware%20Upgrade.md)
### 5.11 [Cloud Service](/wikis/EN/Device%20Management/Cloud%20Service.md)
### 5.12 [Capability](/wikis/EN/Device%20Management/Capability.md)
### 5.13 [Device Type](/wikis/EN/Device%20Management/Device%20Type.md)
### 5.14 [Error Code](/wikis/EN/Device%20Management/Error%20Code.md)
### 5.15 [Server-Side Alarm Message Integration](/wikis/EN/Device%20Management/Server-Side%20Alarm%20Message%20Integration.md)
### 5.16 [Share Devices](/wikis/EN/Device%20Management/Share%20Devices.md)
### 5.17 [Successful Networking Process (for Token-based Login Devices)](/wikis/EN/Device%20Management/Successful%20Networking%20Process%20(for%20Token-based%20Login%20Devices).md)

## 6.Feature Map
<img src="https://obs-xm-pub.obs.cn-south-1.myhuaweicloud.com/docs/20240222/1708580505803.png" alt="Image" width="560" height="1450">
