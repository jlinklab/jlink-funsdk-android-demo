package demo.xm.com.xmfunsdkdemo.ui.device.config.idrnetwork.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.lib.FunSDK
import com.xm.ui.widget.dialog.LoadingDialog
import demo.xm.com.xmfunsdkdemo.R
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity
import demo.xm.com.xmfunsdkdemo.databinding.ActivityIdrNetworkSwitchBinding
import demo.xm.com.xmfunsdkdemo.ui.device.config.idrnetwork.listener.IDRNetworkSwitchContract
import demo.xm.com.xmfunsdkdemo.ui.device.config.idrnetwork.presenter.IDRNetworkSwitchPresenter
import demo.xm.com.xmfunsdkdemo.ui.dialog.NetworkSwitchDlg
import demo.xm.com.xmfunsdkdemo.utils.NetworkCardUtils

class IDRNetworkSwitchActivity : DemoBaseActivity<IDRNetworkSwitchPresenter>(),
    IDRNetworkSwitchContract.IIDRNetworkSwitchView {

    var binding: ActivityIdrNetworkSwitchBinding? = null

    private val devSignalSrc = intArrayOf(
        R.drawable.signal_1,
        R.drawable.signal_1,
        R.drawable.signal_2,
        R.drawable.signal_3,
        R.drawable.signal_4
    )


    override fun getPresenter(): IDRNetworkSwitchPresenter {
        return IDRNetworkSwitchPresenter(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdrNetworkSwitchBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initListener()
        LoadingDialog.getInstance(this).show()
        presenter.initData()
    }


    private fun initListener() {
        binding?.layoutTop?.setLeftClick {
            //正在切换中，不允许返回
            if (!presenter.isSwitching()) {
                finish()
            } else {
                binding?.layoutTop?.setLeftBtnValue(1)
            }
        }
        //点击交换按钮
        binding?.clSwitch?.setOnClickListener {
            switchCard(
                binding?.clCard1,
                binding?.clCard2
            )
        }
        //点击交换按钮
        binding?.clSwitch1?.setOnClickListener {
            switchCard(
                binding?.clCard2,
                binding?.clCard1
            )
        }
        binding?.ivCopyIccid?.setOnClickListener {
            copyText(binding?.tvIccid1)
        }
        binding?.ivCopyImei?.setOnClickListener {
            copyText(binding?.tvImei)
        }
        binding?.ivCopyIccid2?.setOnClickListener {
            copyText(binding?.tvIccid2)
        }
        binding?.ivCopyImei2?.setOnClickListener {
            copyText(binding?.tvImei2)
        }
    }

    private fun copyText(textView: TextView?) {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.text = textView?.text?.toString()?.replace(" ", "")
        Toast.makeText(
            this,
            FunSDK.TS("TR_Copy_Success"),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun getCarrierName(iccid: String?): String? {
        return when (NetworkCardUtils.getCarrierFromICCID(iccid)) {
            NetworkCardUtils.CellNetworkCardType.China_Mobile -> FunSDK.TS("TR_Setting_China_Mobile")
            NetworkCardUtils.CellNetworkCardType.China_Unicom -> FunSDK.TS("TR_Setting_China_Unicom")
            NetworkCardUtils.CellNetworkCardType.China_Telecom -> FunSDK.TS("TR_Setting_China_Telecom")
            else -> FunSDK.TS("TR_Unknow")
        }
    }

    private fun switchCard(layout1: ConstraintLayout?, layout2: ConstraintLayout?) {
        if (presenter.isSwitching()) {
            return
        }
        val dev4GInfoBean = presenter.getDev4GInfoBean()
        if (dev4GInfoBean!!.dualSimInfo.simInfoList.size > 1) {
            val tips = if (dev4GInfoBean!!.dualSimInfo.preferredSimSlot == 0) {
                String.format(
                    FunSDK.TS("TR_Setting_Whether_Switch_To_X_Network"),
                    getCarrierName(dev4GInfoBean!!.dualSimInfo.simInfoList[1].iccid)
                )
            } else {
                String.format(
                    FunSDK.TS("TR_Setting_Whether_Switch_To_X_Network"),
                    getCarrierName(dev4GInfoBean!!.dualSimInfo.simInfoList[0].iccid)
                )
            }
            val dialog: NetworkSwitchDlg = NetworkSwitchDlg(this)
            dialog.setPositiveButton {
                dialog.dismiss()
                val layout1Location = IntArray(2)
                val layout2Location = IntArray(2)
                layout1?.getLocationOnScreen(layout1Location)
                layout2?.getLocationOnScreen(layout2Location)
                Log.e(
                    "idrNetwork---",
                    "layout1Location:" + layout1Location[1] + " layout2Location:" + layout2Location[1]
                )
                // 动画：将layout1移到layout2的位置
                val anim1: ObjectAnimator = if (!presenter.hasExchange()) {
                    ObjectAnimator.ofFloat(
                        layout1, "translationY",
                        0f, (layout2Location[1] - layout1Location[1]) * 1f
                    )
                } else {
                    ObjectAnimator.ofFloat(
                        layout1, "translationY",
                        (layout1Location[1] - layout2Location[1]) * 1f, 0f
                    )
                }
                anim1.duration = 500
                // 动画：将layout2移到layout1的位置
                val anim2: ObjectAnimator = if (!presenter.hasExchange()) {
                    ObjectAnimator.ofFloat(
                        layout2, "translationY", 0f,
                        (layout1Location[1] - layout2Location[1]) * 1f
                    )
                } else {
                    ObjectAnimator.ofFloat(
                        layout2, "translationY", (layout2Location[1] - layout1Location[1]) * 1f, 0f

                    )
                }
                anim2.duration = 500
                val listener: Animator.AnimatorListener = object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator, isReverse: Boolean) {
                        super.onAnimationEnd(animation)
                        //处理位置交换后的逻辑
                        Log.e("idrNetwork---", "onAnimationEnd")
                        presenter.changeHasExchange()
                        presenter.setIsSwitching()
                        dealWith4GInfo()
                        presenter.saveConfig()
                    }
                }
                anim1.addListener(listener)
                // 启动动画
                anim1.start()
                anim2.start()
            }.setNegativeButton {
                dialog.dismiss()
            }.show()
        }
    }

    override fun resetLastCardLocation() {
        val layout1 = binding?.clCard2
        val layout2 = binding?.clCard1
        if (presenter.isSwitching()) {
            return
        }
        val dev4GInfoBean = presenter.getDev4GInfoBean()
        if (dev4GInfoBean!!.dualSimInfo.simInfoList.size > 1) {
            val layout1Location = IntArray(2)
            val layout2Location = IntArray(2)
            layout1?.getLocationOnScreen(layout1Location)
            layout2?.getLocationOnScreen(layout2Location)
            Log.e(
                "idrNetwork---",
                "2 layout1Location:" + layout1Location[1] + " layout2Location:" + layout2Location[1]
            )
            // 动画：将layout1移到layout2的位置
            val anim1: ObjectAnimator = if (!presenter.hasExchange()) {
                ObjectAnimator.ofFloat(
                    layout1, "translationY",
                    0f, (layout2Location[1] - layout1Location[1]) * 1f
                )
            } else {
                ObjectAnimator.ofFloat(
                    layout1, "translationY",
                    (layout1Location[1] - layout2Location[1]) * 1f, 0f
                )
            }
            anim1.duration = 500
            // 动画：将layout2移到layout1的位置
            val anim2: ObjectAnimator = if (!presenter.hasExchange()) {
                ObjectAnimator.ofFloat(
                    layout2, "translationY", 0f,
                    (layout1Location[1] - layout2Location[1]) * 1f
                )
            } else {
                ObjectAnimator.ofFloat(
                    layout2, "translationY", (layout2Location[1] - layout1Location[1]) * 1f, 0f

                )
            }
            anim2.duration = 500
            val listener: Animator.AnimatorListener = object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator, isReverse: Boolean) {
                    super.onAnimationEnd(animation)
                    //处理位置交换后的逻辑
                    presenter.changeHasExchange()
                    dealWith4GInfo()
                }
            }
            anim1.addListener(listener)
            // 启动动画
            anim1.start()
            anim2.start()
        }
    }

    override fun hideSwitchTips() {
        binding?.clSwitchTips1?.visibility = View.GONE
        binding?.clSwitchTips2?.visibility = View.GONE
        binding?.layoutTop?.setLeftBtnValue(0)
    }


    private fun stringAddSpace(iccid: String): String {
        val result = StringBuilder()

        for (i in iccid.indices) {
            result.append(iccid[i])
            if ((i + 1) % 4 == 0 && i < iccid.length - 1) {
                result.append(" ")
            }
        }
        return result.toString()
    }

    override fun dealWith4GInfo() {
        val dev4GInfoBean = presenter.getDev4GInfoBean()
        binding?.tvImei?.text = stringAddSpace(dev4GInfoBean!!.imei)
        binding?.tvImei2?.text = stringAddSpace(dev4GInfoBean!!.imei)
        var isEmpty = false
        binding?.tvTips?.visibility = View.VISIBLE
        if (dev4GInfoBean!!.dualSimInfo.simInfoList != null && dev4GInfoBean!!.dualSimInfo.simInfoList.size > 0) {
            var level = 0
            if (dev4GInfoBean!!.signalLevel < 0) {
                level = 0
            } else if (devSignalSrc.size > dev4GInfoBean!!.signalLevel + 1) {
                level = dev4GInfoBean!!.signalLevel + 1
            }
            if (!presenter.isInitExchange()) {
                //当前使用的是卡槽1
                binding?.tvCard1?.visibility = View.VISIBLE
                binding?.clCard1?.visibility = View.VISIBLE
                binding?.tvCard2?.visibility = View.VISIBLE
                binding?.clCard2?.visibility = View.VISIBLE
                binding?.ivSingleLevel?.setImageResource(devSignalSrc[level])
                binding?.ivSingleLevel2?.setImageResource(devSignalSrc[level])
                val iccid1Type =
                    NetworkCardUtils.getCarrierFromICCID(dev4GInfoBean!!.dualSimInfo.simInfoList[0].iccid)
                var iccid2Type = NetworkCardUtils.CellNetworkCardType.Unknown
                binding?.tvIccid1?.text =
                    stringAddSpace(dev4GInfoBean!!.dualSimInfo.simInfoList[0].iccid)
                binding?.tvCarrierName?.text =
                    NetworkCardUtils.getCarrierName(dev4GInfoBean!!.dualSimInfo.simInfoList[0].iccid)
                val supportSwitch: Boolean = if (dev4GInfoBean!!.dualSimInfo.simInfoList.size > 1) {
                    if (android.text.TextUtils.isEmpty(dev4GInfoBean!!.dualSimInfo.simInfoList[1].iccid)) {
                        false
                    } else {
                        presenter.isSupportSwitch(1)

                    }
                } else {
                    false
                }
                if (!android.text.TextUtils.isEmpty(dev4GInfoBean!!.dualSimInfo.simInfoList[1].iccid)) {
                    iccid2Type =
                        NetworkCardUtils.getCarrierFromICCID(dev4GInfoBean!!.dualSimInfo.simInfoList[1].iccid)
                    binding?.tvIccid2?.text =
                        stringAddSpace(dev4GInfoBean!!.dualSimInfo.simInfoList[1].iccid)
                    binding?.tvCarrierName2?.text =
                        NetworkCardUtils.getCarrierName(dev4GInfoBean!!.dualSimInfo.simInfoList[1].iccid)
                }
                if (presenter.hasExchange()) {
                    //交换了位置
                    binding?.ivSingleLevel?.visibility = View.GONE
                    binding?.ivSelectedTips?.visibility = View.GONE
                    binding?.ivSelectedTips2?.visibility = View.VISIBLE
                    if (presenter.isSwitching()) {
                        binding?.clSwitch1?.visibility = View.GONE
                        binding?.ivSingleLevel2?.visibility = View.GONE
                        binding?.clSwitchTips2?.visibility = View.VISIBLE
                        binding?.switchLoading2?.setAnimation("img/signal.json")
                    } else {
                        if (supportSwitch) {
                            binding?.clSwitch1?.visibility = View.VISIBLE
                        } else {
                            binding?.clSwitch1?.visibility = View.GONE
                        }
                        binding?.ivSingleLevel2?.visibility = View.VISIBLE
                        binding?.clSwitchTips2?.visibility = View.GONE
                    }
                    binding?.clSwitch?.visibility = View.GONE
                    dealWithCardSelectBg(iccid1Type, binding?.clCard1, binding?.ivCard1, false)
                    dealWithCardSelectBg(iccid2Type, binding?.clCard2, binding?.ivCard2, true)
                    binding?.ivCopyIccid?.setImageResource(R.drawable.ic_copy_gray)
                    binding?.ivCopyImei?.setImageResource(R.drawable.ic_copy_gray)
                    binding?.ivCopyIccid2?.setImageResource(R.drawable.ic_copy)
                    binding?.ivCopyImei2?.setImageResource(R.drawable.ic_copy)
                    setTextViewGray(
                        binding?.tvCarrierName,
                        binding?.tvIccid1,
                        binding?.tvImeiName,
                        binding?.tvImei
                    )
                    setTextViewWhite(
                        binding?.tvCarrierName2,
                        binding?.tvIccid2,
                        binding?.tvImeiName2,
                        binding?.tvImei2
                    )
                } else {
                    binding?.ivSelectedTips?.visibility = View.VISIBLE
                    binding?.ivSelectedTips2?.visibility = View.GONE
                    if (presenter.isSwitching()) {
                        binding?.ivSingleLevel?.visibility = View.GONE
                        binding?.clSwitch?.visibility = View.GONE
                        binding?.clSwitchTips1?.visibility = View.VISIBLE
                        binding?.switchLoading1?.setAnimation("img/signal.json")
                    } else {
                        binding?.ivSingleLevel?.visibility = View.VISIBLE
                        if (supportSwitch) {
                            binding?.clSwitch?.visibility = View.VISIBLE
                        } else {
                            binding?.clSwitch?.visibility = View.GONE
                        }
                        binding?.clSwitchTips1?.visibility = View.GONE
                    }
                    binding?.ivSingleLevel2?.visibility = View.GONE
                    binding?.clSwitch1?.visibility = View.GONE
                    dealWithCardSelectBg(iccid1Type, binding?.clCard1, binding?.ivCard1, true)
                    dealWithCardSelectBg(iccid2Type, binding?.clCard2, binding?.ivCard2, false)
                    binding?.ivCopyIccid?.setImageResource(R.drawable.ic_copy)
                    binding?.ivCopyImei?.setImageResource(R.drawable.ic_copy)
                    binding?.ivCopyIccid2?.setImageResource(R.drawable.ic_copy_gray)
                    binding?.ivCopyImei2?.setImageResource(R.drawable.ic_copy_gray)
                    setTextViewWhite(
                        binding?.tvCarrierName,
                        binding?.tvIccid1,
                        binding?.tvImeiName,
                        binding?.tvImei
                    )
                    setTextViewGray(
                        binding?.tvCarrierName2,
                        binding?.tvIccid2,
                        binding?.tvImeiName2,
                        binding?.tvImei2
                    )
                }
                if (presenter.hasExchange()) {
                    binding?.tvCard1?.text = com.lib.FunSDK.TS("TR_Setting_Card_Slot") + "2:"
                    binding?.tvCard2?.text = com.lib.FunSDK.TS("TR_Setting_Card_Slot") + "1:"
                } else {
                    binding?.tvCard1?.text = com.lib.FunSDK.TS("TR_Setting_Card_Slot") + "1:"
                    binding?.tvCard2?.text = com.lib.FunSDK.TS("TR_Setting_Card_Slot") + "2:"
                }
                presenter.setCard2StateWhenNotInitExchange()
            } else {
                //当前使用的是卡槽2
                if (dev4GInfoBean!!.dualSimInfo.simInfoList.size > 1) {
                    binding?.tvCard1?.visibility = View.VISIBLE
                    binding?.clCard1?.visibility = View.VISIBLE
                    binding?.tvCard2?.visibility = View.VISIBLE
                    binding?.clCard2?.visibility = View.VISIBLE
                    binding?.ivSingleLevel?.setImageResource(devSignalSrc[level])
                    binding?.ivSingleLevel2?.setImageResource(devSignalSrc[level])
                    val iccid1Type =
                        NetworkCardUtils.getCarrierFromICCID(dev4GInfoBean!!.dualSimInfo.simInfoList[1].iccid)
                    var iccid2Type =
                        NetworkCardUtils.getCarrierFromICCID(dev4GInfoBean!!.dualSimInfo.simInfoList[0].iccid)
                    binding?.tvIccid1?.text =
                        stringAddSpace(dev4GInfoBean!!.dualSimInfo.simInfoList[1].iccid)
                    binding?.tvCarrierName?.text =
                        NetworkCardUtils.getCarrierName(dev4GInfoBean!!.dualSimInfo.simInfoList[1].iccid)
                    binding?.tvIccid2?.text =
                        stringAddSpace(dev4GInfoBean!!.dualSimInfo.simInfoList[0].iccid)
                    binding?.tvCarrierName2?.text =
                        NetworkCardUtils.getCarrierName(dev4GInfoBean!!.dualSimInfo.simInfoList[0].iccid)
                    val supportSwitch: Boolean =
                        if (dev4GInfoBean!!.dualSimInfo.simInfoList.size > 1) {
                            if (android.text.TextUtils.isEmpty(dev4GInfoBean!!.dualSimInfo.simInfoList[0].iccid)) {
                                false
                            } else {
                                presenter.isSupportSwitch(0)

                            }
                        } else {
                            false
                        }
                    if (presenter.hasExchange()) {
                        //交换了位置
                        binding?.ivSingleLevel?.visibility = View.GONE
                        binding?.ivSelectedTips?.visibility = View.GONE
                        binding?.ivSelectedTips2?.visibility = View.VISIBLE
                        if (presenter.isSwitching()) {
                            binding?.clSwitch1?.visibility = View.GONE
                            binding?.ivSingleLevel2?.visibility = View.GONE
                            binding?.clSwitchTips2?.visibility = View.VISIBLE
                            binding?.switchLoading2?.setAnimation("img/signal.json")
                        } else {
                            if (supportSwitch) {
                                binding?.clSwitch1?.visibility = View.VISIBLE
                            } else {
                                binding?.clSwitch1?.visibility = View.GONE
                            }
                            binding?.ivSingleLevel2?.visibility = View.VISIBLE
                            binding?.clSwitchTips1?.visibility = View.GONE
                        }
                        binding?.clSwitch?.visibility = View.GONE
                        dealWithCardSelectBg(iccid1Type, binding?.clCard1, binding?.ivCard1, false)
                        dealWithCardSelectBg(iccid2Type, binding?.clCard2, binding?.ivCard2, true)
                        binding?.ivCopyIccid?.setImageResource(R.drawable.ic_copy_gray)
                        binding?.ivCopyImei?.setImageResource(R.drawable.ic_copy_gray)
                        binding?.ivCopyIccid2?.setImageResource(R.drawable.ic_copy)
                        binding?.ivCopyImei2?.setImageResource(R.drawable.ic_copy)
                        setTextViewGray(
                            binding?.tvCarrierName,
                            binding?.tvIccid1,
                            binding?.tvImeiName,
                            binding?.tvImei
                        )
                        setTextViewWhite(
                            binding?.tvCarrierName2,
                            binding?.tvIccid2,
                            binding?.tvImeiName2,
                            binding?.tvImei2
                        )
                    } else {
                        binding?.ivSelectedTips?.visibility = View.VISIBLE
                        binding?.ivSelectedTips2?.visibility = View.GONE
                        if (presenter.isSwitching()) {
                            binding?.ivSingleLevel?.visibility = View.GONE
                            binding?.clSwitch?.visibility = View.GONE
                            binding?.clSwitchTips1?.visibility = View.VISIBLE
                            binding?.switchLoading1?.setAnimation("img/signal.json")
                        } else {
                            binding?.ivSingleLevel?.visibility = View.VISIBLE
                            if (supportSwitch) {
                                binding?.clSwitch?.visibility = View.VISIBLE
                            } else {
                                binding?.clSwitch?.visibility = View.GONE
                            }
                            binding?.clSwitchTips1?.visibility = View.GONE
                        }
                        binding?.ivSingleLevel2?.visibility = View.GONE
                        binding?.clSwitch1?.visibility = View.GONE
                        dealWithCardSelectBg(iccid1Type, binding?.clCard1, binding?.ivCard1, true)
                        dealWithCardSelectBg(iccid2Type, binding?.clCard2, binding?.ivCard2, false)
                        binding?.ivCopyIccid?.setImageResource(R.drawable.ic_copy)
                        binding?.ivCopyImei?.setImageResource(R.drawable.ic_copy)
                        binding?.ivCopyIccid2?.setImageResource(R.drawable.ic_copy_gray)
                        binding?.ivCopyImei2?.setImageResource(R.drawable.ic_copy_gray)
                        setTextViewWhite(
                            binding?.tvCarrierName,
                            binding?.tvIccid1,
                            binding?.tvImeiName,
                            binding?.tvImei
                        )
                        setTextViewGray(
                            binding?.tvCarrierName2,
                            binding?.tvIccid2,
                            binding?.tvImeiName2,
                            binding?.tvImei2
                        )
                    }

                    if (presenter.hasExchange()) {
                        binding?.tvCard1?.text = com.lib.FunSDK.TS("TR_Setting_Card_Slot") + "1:"
                        binding?.tvCard2?.text = com.lib.FunSDK.TS("TR_Setting_Card_Slot") + "2:"

                    } else {
                        binding?.tvCard1?.text = com.lib.FunSDK.TS("TR_Setting_Card_Slot") + "2:"
                        binding?.tvCard2?.text = com.lib.FunSDK.TS("TR_Setting_Card_Slot") + "1:"

                    }
                    presenter.setCard2StateWhenHasInitExchange()
                    if (presenter.isSwitching()) {
                        binding?.layoutTop?.setLeftBtnValue(1)
                    } else {
                        binding?.layoutTop?.setLeftBtnValue(0)
                    }
                } else {
                    binding?.tvCard1?.visibility = View.GONE
                    binding?.clCard1?.visibility = View.GONE
                    binding?.tvCard2?.visibility = View.GONE
                    binding?.clCard2?.visibility = View.GONE
                    binding?.tvTips?.visibility = View.GONE
                }
            }
            if (android.text.TextUtils.isEmpty(dev4GInfoBean!!.dualSimInfo!!.simInfoList[0].iccid)
                || android.text.TextUtils.isEmpty(dev4GInfoBean!!.dualSimInfo!!.simInfoList[1].iccid)
            ) {
                isEmpty = true
            }
            binding?.tvCard1State?.text = com.lib.FunSDK.TS("TR_Setting_In_Use")
            if (isEmpty) {
                //空卡
                binding?.tvCard2State?.text = com.lib.FunSDK.TS("TR_Setting_Empty")
                binding?.clEmptyTips?.visibility = View.VISIBLE
                binding?.tvCard2State?.setTextColor(
                    resources.getColor(
                        R.color.color_f05656
                    )
                )
            } else {
                when (presenter.getCard2State()) {

                    11, 12, 13 -> {
                        //provider大于10，就是已注销
                        binding?.tvCard2State?.text = com.lib.FunSDK.TS("TR_Logged_Out")
                        binding?.tvCard2State?.setTextColor(
                            resources.getColor(
                                R.color.color_f05656,
                            )
                        )
                    }

                    else -> {
                        //空闲
                        binding?.tvCard2State?.text = com.lib.FunSDK.TS("TR_Seting_Idle")
                        binding?.tvCard2State?.setTextColor(
                            resources.getColor(
                                R.color.color_text_light1,
                            )
                        )
                    }
                }
            }
            if (presenter.isSwitching()) {
                //如果处于切换状态，需要把卡1和卡2的状态设置为切换中
                binding?.tvCard1State?.text = com.lib.FunSDK.TS("TR_Setting_In_Transition")
                binding?.tvCard1State?.setTextColor(
                    resources.getColor(
                        R.color.color_text_light1,

                        )
                )
                binding?.tvCard2State?.text = com.lib.FunSDK.TS("TR_Setting_In_Transition")
                binding?.tvCard2State?.setTextColor(
                    resources.getColor(
                        R.color.color_text_light1,

                        )
                )
            }
        } else {
            binding?.tvCard1?.visibility = View.GONE
            binding?.clCard1?.visibility = View.GONE
            binding?.tvCard2?.visibility = View.GONE
            binding?.clCard2?.visibility = View.GONE
            binding?.tvTips?.visibility = View.GONE
        }
    }

    private fun setTextViewWhite(vararg tvViews: android.widget.TextView?) {
        for (textView in tvViews) {
            textView?.setTextColor(android.graphics.Color.WHITE)
        }
    }

    private fun setTextViewGray(vararg tvViews: android.widget.TextView?) {
        for (textView in tvViews) {
            textView?.setTextColor(resources.getColor(R.color.color_text_light1))
        }
    }

    private fun dealWithCardSelectBg(
        iccidType: NetworkCardUtils.CellNetworkCardType,
        view: androidx.constraintlayout.widget.ConstraintLayout?,
        ivCard: android.widget.ImageView?,
        selected: Boolean
    ) {
        when (iccidType) {
            NetworkCardUtils.CellNetworkCardType.China_Mobile -> {
                if (selected) {
                    ivCard?.setImageResource(R.drawable.ic_mobile)
                    view?.background = getDrawable(R.drawable.bg_card_china_mobile)
                } else {
                    ivCard?.setImageResource(R.drawable.ic_mobile_no)
                    view?.background = getDrawable(R.drawable.bg_card_no_selected)
                }
            }

            NetworkCardUtils.CellNetworkCardType.China_Telecom -> {
                if (selected) {
                    ivCard?.setImageResource(R.drawable.ic_telecom)
                    view?.background = getDrawable(R.drawable.bg_card_china_telecom)
                } else {
                    ivCard?.setImageResource(R.drawable.ic_telecom_no)
                    view?.background = getDrawable(R.drawable.bg_card_no_selected)
                }
            }

            NetworkCardUtils.CellNetworkCardType.China_Unicom -> {
                if (selected) {
                    ivCard?.setImageResource(R.drawable.ic_unicom)
                    view?.background = getDrawable(R.drawable.bg_card_china_unicom)
                } else {
                    ivCard?.setImageResource(R.drawable.ic_unicom_no)
                    view?.background = getDrawable(R.drawable.bg_card_no_selected)
                }
            }

            else -> {
                ivCard?.setImageResource(R.drawable.ic_telecom_no)
                view?.background = getDrawable(R.drawable.bg_card_no_selected)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (presenter.isSwitching() && keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            //正在切换中，不允许返回
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun getActivity(): Activity {
       return this@IDRNetworkSwitchActivity
    }

}