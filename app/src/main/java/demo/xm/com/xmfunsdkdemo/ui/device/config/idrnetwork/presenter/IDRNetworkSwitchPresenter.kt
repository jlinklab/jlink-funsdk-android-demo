package demo.xm.com.xmfunsdkdemo.ui.device.config.idrnetwork.presenter

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.basic.G
import com.blankj.utilcode.util.LogUtils.json
import com.lib.EUIMSG
import com.lib.FunSDK
import com.lib.FunSDK.GetId
import com.lib.IFunSDKResult
import com.lib.MsgContent
import com.lib.sdk.bean.Dev4GInfoBean
import com.lib.sdk.bean.HandleConfigData
import com.lib.sdk.bean.JsonConfig.DEV_4G_INFO
import com.lib.sdk.bean.JsonConfig.OP_SWITCH_SIM
import com.lib.sdk.bean.StringUtils
import com.lib.sdk.bean.SysDevAbilityInfoBean
import com.lib.sdk.bean.SystemFunctionBean
import com.manager.device.DeviceManager
import com.manager.device.DeviceManager.OnDevManagerListener
import com.manager.device.config.DevConfigInfo
import com.manager.device.config.DevConfigManager
import com.manager.device.config.DevConfigManager.OnDevConfigResultListener
import com.manager.sysability.OnSysAbilityResultListener
import com.manager.sysability.SysAbilityManager
import com.xm.activity.base.XMBasePresenter
import com.xm.base.code.ErrorCodeManager
import com.xm.ui.dialog.XMPromptDlg
import com.xm.ui.widget.dialog.LoadingDialog
import demo.xm.com.xmfunsdkdemo.ui.device.config.idrnetwork.listener.IDRNetworkSwitchContract
import kotlin.jvm.java

class IDRNetworkSwitchPresenter(private val iIDRNetworkSwitchView: IDRNetworkSwitchContract.IIDRNetworkSwitchView) :
    XMBasePresenter<DeviceManager>(), IFunSDKResult{


    private var _msgId = 0xff00ff

    private var dev4GInfoBean: Dev4GInfoBean? = null
    private var hasInitData = false
    private var isInitExchange = false
    private var hasExchange: Boolean = false
    private var isSwitching: Boolean = false
    private var iccid1: String = ""
    private var iccid2: String = ""
    private var provider1: Int = 0
    private var provider2: Int = 0
    private var supportCellular: Boolean = true
    private var card2State = 0
    private var selectCard = -1

    private var mDevConfigManager: DevConfigManager? = null

    override fun getManager(): DeviceManager? {
        return DeviceManager.getInstance()
    }


    override fun setDevId(devId: String?) {
        mDevConfigManager = manager.getDevConfigManager(devId)
        super.setDevId(devId)
    }


    fun initData() {
        SysAbilityManager.getInstance().isSupports(
            iIDRNetworkSwitchView.getActivity(),
            getDevId(),
            true,
            object : OnSysAbilityResultListener<Map<String?, Any?>> {
                override fun onSupportResult(
                    it: Map<String?, Any?>,
                    p1: Boolean
                ) {
                    if (it.containsKey(SysDevAbilityInfoBean.XMC_NET_CELLULAR_SUPPORT)
                        && it[SysDevAbilityInfoBean.XMC_NET_CELLULAR_SUPPORT] is Boolean
                        && it[SysDevAbilityInfoBean.XMC_NET_CELLULAR_SUPPORT] as Boolean
                    ) {
                        if (it.containsKey("net.cellular.iccid")) {
                            val iccid = it["net.cellular.iccid"]
                            if (iccid is String) {
                                iccid1 = iccid
                            }
                            val iccid2 = it["net.cellular.2ndiccid"]
                            if (iccid2 is String) {
                                this@IDRNetworkSwitchPresenter.iccid2 = iccid2
                            }
                            val provider = it["net.cellular.provider"]
                            if (provider is Int) {
                                provider1 = provider
                            }
                            val provider2 = it["net.cellular.2ndprovider"]
                            if (provider is Int) {
                                this@IDRNetworkSwitchPresenter.provider2 = provider2 as Int
                            }
                        }
                        FunSDK.Log("Switch NetWork provider1:$provider1 provider2:$provider2")
                    } else {
                        //不支持流量能力，认为是不限制的设备，可以随意切换流量卡
                        supportCellular = false
                        FunSDK.Log("Switch NetWork supportCellular:false")
                    }
                }
            },
            "net.cellular"
        )
        getConfig(0)
    }


    private fun getConfig(seq: Int) {


        val devConfigInfo = DevConfigInfo.create(object : OnDevManagerListener<String?> {
            override fun onSuccess(devId: String?, operationType: Int, result: String?) {
                LoadingDialog.getInstance(iIDRNetworkSwitchView.getActivity()).dismiss()
                isSwitching = false
                iIDRNetworkSwitchView.hideSwitchTips()

                val handleConfigData = HandleConfigData<Any>();
                if (handleConfigData.getDataObj(
                        result,
                        Dev4GInfoBean::class.java
                    )
                ) {
                    dev4GInfoBean = handleConfigData.obj as Dev4GInfoBean?
                    if (dev4GInfoBean != null) {
                        if (!hasInitData) {
                            hasInitData = true
                            isInitExchange = dev4GInfoBean?.dualSimInfo?.preferredSimSlot == 1
                        }
                        //如果发送的选中卡和切换结束后的选中卡不一致，说明交换卡失败，也要把卡交换回去
                        val changeFailed =
                            hasExchange && selectCard != dev4GInfoBean?.dualSimInfo?.preferredSimSlot
                                    && dev4GInfoBean?.dualSimInfo?.simInfoList?.size == 2
                                    && !TextUtils.isEmpty(
                                dev4GInfoBean?.dualSimInfo?.simInfoList?.get(
                                    0
                                )?.iccid
                            )
                                    && !TextUtils.isEmpty(
                                dev4GInfoBean?.dualSimInfo?.simInfoList?.get(
                                    1
                                )?.iccid
                            )
                        if ((hasExchange && isInitExchange && dev4GInfoBean?.dualSimInfo?.simInfoList?.size == 2
                                    && (TextUtils.isEmpty(
                                dev4GInfoBean?.dualSimInfo?.simInfoList?.get(
                                    0
                                )?.iccid
                            )
                                    || TextUtils.isEmpty(
                                dev4GInfoBean?.dualSimInfo?.simInfoList?.get(
                                    1
                                )?.iccid
                            )))
                            || changeFailed
                        ) {
                            //交换卡后，发现只有一张卡了，需要把位置交换回原始位置
                            iIDRNetworkSwitchView.resetLastCardLocation()
                        } else {
                            if (!hasExchange) {
                                isInitExchange =
                                    dev4GInfoBean?.dualSimInfo?.preferredSimSlot == 1
                            }
                            iIDRNetworkSwitchView.dealWith4GInfo()
                        }
                        if (selectCard == -1) {
                            selectCard = dev4GInfoBean?.dualSimInfo?.preferredSimSlot!!
                        }
                    } else {
                        Toast.makeText(iIDRNetworkSwitchView.getActivity(),FunSDK.TS("Data_exception"), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(iIDRNetworkSwitchView.getActivity(),FunSDK.TS("Data_exception"), Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailed(devId: String?, msgId: Int, jsonName: String?, errorId: Int) {
                LoadingDialog.getInstance(iIDRNetworkSwitchView.getActivity()).dismiss()
                if (seq == 100) {
                    //请求失败，登出设备再重试
                    FunSDK.DevLogout(GetId(), getDevId(), 0)
                    getConfig(100)
                } else {
                    XMPromptDlg.onShowErrorDlg(
                        iIDRNetworkSwitchView.getActivity(),
                        ErrorCodeManager.getSDKStrErrorByNO(errorId),
                        null,
                        true
                    )
                }
            }
        })
        devConfigInfo.setChnId(0)
        devConfigInfo.setTimeOut(5000) //设置超时时间
        devConfigInfo.setJsonName(DEV_4G_INFO)
        devConfigInfo.setCmdId(1020)
        devConfigInfo.setSeq(seq)
        mDevConfigManager?.setDevCmd(devConfigInfo)
    }



    fun saveConfig() {
        val json = JSONObject()
        json["Name"] = OP_SWITCH_SIM
        val jsonObj = JSONObject()
        if (dev4GInfoBean!!.dualSimInfo.preferredSimSlot == 0) {
            jsonObj["SrcIccid"] = dev4GInfoBean!!.dualSimInfo.simInfoList[0].iccid
            jsonObj["DestIccid"] = dev4GInfoBean!!.dualSimInfo.simInfoList[1].iccid
            selectCard = 1
        } else {
            jsonObj["SrcIccid"] = dev4GInfoBean!!.dualSimInfo.simInfoList[1].iccid
            jsonObj["DestIccid"] = dev4GInfoBean!!.dualSimInfo.simInfoList[0].iccid
            selectCard = 0
        }
        json[OP_SWITCH_SIM] = jsonObj

        val devConfigInfo = DevConfigInfo.create(object : OnDevManagerListener<Any?> {
            override fun onSuccess(devId: String?, operationType: Int, result: Any?) {
                LoadingDialog.getInstance(iIDRNetworkSwitchView.getActivity()).dismiss()
                //切换卡回调
                //先登出设备
                FunSDK.DevLogout(GetId(), getDevId(), 0)
                Handler(Looper.getMainLooper()).postDelayed({
                    getConfig(100)
                }, 10000)
            }

            override fun onFailed(devId: String?, msgId: Int, jsonName: String?, errorId: Int) {
                LoadingDialog.getInstance(iIDRNetworkSwitchView.getActivity()).dismiss()
                XMPromptDlg.onShowErrorDlg(
                    iIDRNetworkSwitchView.getActivity(),
                    ErrorCodeManager.getSDKStrErrorByNO(errorId),
                    null,
                    true
                )
            }
        })
        devConfigInfo.setChnId(0)
        devConfigInfo.setTimeOut(8000) //设置超时时间
        devConfigInfo.setJsonName(OP_SWITCH_SIM)
        devConfigInfo.setJsonData(JSON.toJSONString(json))
        devConfigInfo.setCmdId(1450)
        mDevConfigManager?.setDevCmd(devConfigInfo)
    }


    @Synchronized
    fun GetId(): Int {
        _msgId = FunSDK.GetId(_msgId, this)
        return _msgId
    }

    fun isSwitching(): Boolean {
        return isSwitching
    }

    fun hasExchange(): Boolean {
        return hasExchange
    }

    fun getDev4GInfoBean(): Dev4GInfoBean? {
        return dev4GInfoBean
    }

    fun isInitExchange(): Boolean {
        return isInitExchange
    }

    fun isSupportSwitch(cardNum: Int): Boolean {
        return !supportCellular
                || (dev4GInfoBean!!.dualSimInfo.simInfoList[cardNum].iccid.equals(iccid1) && provider1 != 11 && provider1 != 12 && provider1 != 13 && provider1 != 14)
                || (dev4GInfoBean!!.dualSimInfo.simInfoList[cardNum].iccid.equals(iccid2) && provider2 != 11 && provider2 != 12 && provider2 != 13 && provider2 != 14)
    }

    fun getCard2State(): Int {
        return card2State
    }

    fun changeHasExchange() {
        hasExchange = !hasExchange
    }

    fun setIsSwitching(){
        isSwitching = true
    }

    fun setCard2StateWhenHasInitExchange() {
        if(hasExchange){
            card2State =
                if (dev4GInfoBean!!.dualSimInfo.simInfoList[0].iccid.equals(iccid1)) {
                    provider2
                } else {
                    provider1
                }
        } else {
            card2State =
                if (dev4GInfoBean!!.dualSimInfo.simInfoList[0].iccid.equals(iccid1)) {
                    provider1
                } else {
                    provider2
                }
        }
    }


    fun setCard2StateWhenNotInitExchange() {
        if(hasExchange){
            card2State = provider1
        } else {
            card2State = provider2
        }
    }

    override fun OnFunSDKResult(p0: Message?, p1: MsgContent?): Int {
        return 0
    }
}