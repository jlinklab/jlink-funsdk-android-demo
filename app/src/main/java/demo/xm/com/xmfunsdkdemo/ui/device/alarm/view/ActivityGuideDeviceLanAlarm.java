package demo.xm.com.xmfunsdkdemo.ui.device.alarm.view;


import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.basic.G;

import com.lib.EDEV_ATTR;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.sdk.bean.alarm.AlarmInfo;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.xm.base.code.ErrorCodeManager;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.FunError;
import demo.xm.com.xmfunsdkdemo.base.FunLog;
import demo.xm.com.xmfunsdkdemo.ui.ActivityDemo;
import demo.xm.com.xmfunsdkdemo.ui.ListAdapterDeviceComComands;


/**
 * Demo: 局域网报警功能测试(不需要报警服务器)
 * 
 *
 */
public class ActivityGuideDeviceLanAlarm extends ActivityDemo implements OnClickListener, IFunSDKResult {

	private static final String TAG = "LanAlarm";
	
	private XTitleBar titleBar;
	
	private ListView mListAlarms = null;
	private ListAdapterDeviceComComands mAdapter = null;
	
	private Button mBtnOpen = null;
	private Button mBtnClose = null;
	
	private XMDevInfo xmDevInfo=null;
	
	private int mUserId = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_device_lan_alarm);

		titleBar = findViewById(R.id.layoutTop);
		titleBar.setTitleText(getString(R.string.device_lan_alarm_test));
		titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
			@Override
			public void onLeftclick() {
				finish();
			}
		});
		titleBar.setBottomTip(getClass().getName());
		
		mListAlarms = (ListView)findViewById(R.id.listAlarm);
		mAdapter = new ListAdapterDeviceComComands(this);
		mListAlarms.setAdapter(mAdapter);
		
		mBtnOpen = (Button)findViewById(R.id.btnOpen);
		mBtnClose = (Button)findViewById(R.id.btnClose);
		mBtnOpen.setOnClickListener(this);
		mBtnClose.setOnClickListener(this);
		
		String devId = getIntent().getStringExtra("devId");
	    xmDevInfo = DevDataCenter.getInstance().getDevInfo(devId);
		if ( null == xmDevInfo ) {
			finish();
			return;
		}
		// 以下是使用FunSDK接口,监听局域网内报警消息的例子
		
		// 注册一个User操作句柄
		mUserId = FunSDK.RegUser(this);
		
		// 设置局域网报警消息监听,如果是多个设备,也只需要设置一次即可,所有设备报警都在此返回
		FunSDK.DevSetAlarmListener(mUserId, 0);
		
		// 登录设备
		loginDevice();
		
		// 打开/关闭局域网报警,请参考函数setLanAlarm()
	}
	

	@Override
	protected void onDestroy() {
		
		if ( mUserId >= 0 ) {
			FunSDK.UnRegUser(mUserId);
			mUserId = -1;
		}
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.backBtnInTopLayout:
			{
				// 返回/退出
				finish();
			}
			break;
		case R.id.btnOpen:
			{
				setLanAlarm(true);
			}
			break;
		case R.id.btnClose:
			{
				setLanAlarm(false);
			}
			break;
		}
	}
	
	private void loginDevice() {
		showWaitDialog();

		DeviceManager deviceManager = DeviceManager.getInstance();
		deviceManager.loginDev(xmDevInfo.getDevId(), "admin", "", new DeviceManager.OnDevManagerListener() {
			@Override
			public void onSuccess(String s, int i, Object o) {
			      hideWaitDialog();
				}
			@Override
			public void onFailed(String s, int i, String s1, int i1) {
				hideWaitDialog();
				showToast(R.string.user_register_login_fail);
			}
		});
	}
	
	private int getHandler() {
		return mUserId;
	}
	
	private boolean setLanAlarm(boolean enable) {
		int nValue = enable ? 1 : 0;	// 0为关闭 ，非0为开启（建议赋1）
    	int result = FunSDK.DevSetAttrAlarmByInt(getHandler(), 
    			xmDevInfo.getDevId(),
    			EDEV_ATTR.EDA_OPT_ALARM, 
    			nValue, 
    			0);
    	return (result == 1);
	}
	
	private void dumpAlarm(byte[] data) {
		String str = "";
		for ( int i = 0; i < data.length; i ++ ) {
			str += String.format("%02x ", data[i]&0xff);
		}
		
		dumpAlarm(str);
	}
	
	private void dumpAlarm(String content) {
		
		mAdapter.updateCommand(new ListAdapterDeviceComComands.ComCommand(false, content));
		mListAlarms.post(new Runnable() {
			
			@Override
			public void run() {
				mListAlarms.setSelection(mAdapter.getCount()-1);
			}
		});
	}


	@Override
	public int OnFunSDKResult(Message msg, MsgContent msgContent) {
		FunLog.d(TAG, "msg.what : " + msg.what);
		FunLog.d(TAG, "msg.arg1 : " + msg.arg1 + " [" + FunError.getErrorStr(msg.arg1) + "]");
		FunLog.d(TAG, "msg.arg2 : " + msg.arg2);
		if ( null != msgContent ) {
			FunLog.d(TAG, "msgContent.sender : " + msgContent.sender);
			FunLog.d(TAG, "msgContent.seq : " + msgContent.seq);
			FunLog.d(TAG, "msgContent.str : " + msgContent.str);
			FunLog.d(TAG, "msgContent.arg3 : " + msgContent.arg3);
            FunLog.d(TAG, "msgContent.pData : " + msgContent.pData);
		}

		switch(msg.what) {
		case EUIMSG.DEV_LOGIN:
			{
				if ( msg.arg1 == FunError.EE_OK ) {
					// 设备登录成功
					hideWaitDialog();
				} else {
					// 设备登录失败
					showToast(ErrorCodeManager.getSDKStrErrorByNO(msg.arg1));
				}
			}
			break;
		case EUIMSG.DEV_SET_ATTR:
			{
				if ( msg.arg1 == FunError.EE_OK ) {
					
				} else {
					showToast(ErrorCodeManager.getSDKStrErrorByNO(msg.arg1));
				}
			}
			break;
		case EUIMSG.DEV_GET_LAN_ALARM:
			{
				// 收到局域网报警消息
				
				// 设备的序列号
				String devSn = msgContent.str;
				
				String dumpStr = devSn;
				
				try {
					String json = G.ToString(msgContent.pData);
					FunLog.d(TAG, json);
					
					AlarmInfo alarmInfo = new AlarmInfo();
					alarmInfo.onParse(json);
					
					dumpStr += " | " + alarmInfo.getStartTime() + " | " + alarmInfo.getEvent();
					
				} catch ( Exception e ) {
					e.printStackTrace();
				}
				
				dumpAlarm("alarm from -> " + dumpStr);
			}
			break;
		}
		
		return 0;
	}

}
