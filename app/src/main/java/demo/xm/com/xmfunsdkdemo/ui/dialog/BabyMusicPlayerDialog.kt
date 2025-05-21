package demo.xm.com.xmfunsdkdemo.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSONObject
import com.basic.G
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.lib.EUIMSG
import com.lib.FunSDK
import com.lib.IFunSDKResult
import com.lib.MsgContent
import com.lib.SDKCONST
import com.lib.sdk.bean.OPTUpDataBean
import com.lib.sdk.bean.StringUtils
import com.utils.XUtils
import demo.xm.com.xmfunsdkdemo.R
import demo.xm.com.xmfunsdkdemo.base.BaseBottomDialog
import demo.xm.com.xmfunsdkdemo.bean.music.MusicCtrlContent
import demo.xm.com.xmfunsdkdemo.bean.music.MusicInfo
import demo.xm.com.xmfunsdkdemo.bean.music.MusicInfoChangeBean
import demo.xm.com.xmfunsdkdemo.databinding.DialogBabyMusicPlayerBinding
import demo.xm.com.xmfunsdkdemo.ui.widget.VerticalSeekBar
import demo.xm.com.xmfunsdkdemo.utils.dp
import demo.xm.com.xmfunsdkdemo.utils.setVisible


class BabyMusicPlayerDialog(
    var mContext: Context,
    var devId: String? = null,
    var musicCtrlBean: MusicCtrlContent,
    var onUpdateMusicConfigListener: OnUpdateMusicConfigListener? = null,
    var height: Int,
) : IFunSDKResult, BaseBottomDialog<DialogBabyMusicPlayerBinding>(
    DialogBabyMusicPlayerBinding::inflate,
    onDismiss = {},
    onBind = { dialog, dialogBinding ->
    }
) {

    private var mUserId = 0
    private var sendJsonData = ""
    private var isStopping = false
    override fun initView(dialog: Dialog) {
        val layoutParams = binding?.clDialogRoot?.layoutParams
        val layoutHeight = Math.max(height, 350.dp);
        layoutParams?.height = layoutHeight
        binding?.clDialogRoot?.layoutParams = layoutParams
        //初始化播放列表
        initRecyclerView()
        initListener()
        updateConfig(true)
        listenerSongChange()
        //默认展开
        val bottomSheetDialog = dialog as BottomSheetDialog
        val bottomSheet =
            bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).isDraggable = false
            binding?.bottomSheetDragView?.setBottomSheetBehavior(dialog, binding?.clDialogRoot)
        }
    }

    private fun initListener() {
        //关闭监听
        dialog?.setOnDismissListener {
            if (StringUtils.isStringNULL(devId)) {
                return@setOnDismissListener
            }
            stopListener()
        }
        //循环
        binding?.ivPlaySortType?.setOnClickListener {
            if (XUtils.isFastDoubleClick()) return@setOnClickListener
            //循环模式
            when (musicCtrlBean.loop) {
                0 -> {//0：单曲循环
                    musicCtrlBean.loop = 1
                }

                else -> {//1：列表循环
                    musicCtrlBean.loop = 0
                }
            }
            updateConfig()
        }
        //上一首
        binding?.ivPlayerPrevious?.setOnClickListener {
            if (XUtils.isFastDoubleClick()) return@setOnClickListener
            val selectMusicInfos = arrayListOf<MusicInfo>()
            var currentMusic: MusicInfo? = null
            musicCtrlBean.musicInfo.forEach {
                if (it.select == 1) {
                    selectMusicInfos.add(it)
                }
                if (it.index == musicCtrlBean.music) {
                    currentMusic = it
                }
            }
            //选择的音乐小于2
            if (selectMusicInfos.size < 2) {
                return@setOnClickListener
            }
            //当前没有播放中的音乐 就播放第一首
            if (currentMusic == null) {
                musicCtrlBean.music = selectMusicInfos[0].index
                musicCtrlBean.play = 1
                updateConfig()
                return@setOnClickListener
            }
            var previousItem: MusicInfo? = null
            run {
                musicCtrlBean.musicInfo.forEach {
                    if (currentMusic == it) {
                        return@run
                    }
                    if (it.select == 1) {
                        previousItem = it
                    }
                }
            }
            if (previousItem == null) {
                previousItem = selectMusicInfos.last()
            }
            musicCtrlBean.music = previousItem!!.index
            musicCtrlBean.play = 1
            updateConfig()
        }
        //下一首
        binding?.ivPlayerNext?.setOnClickListener {
            if (XUtils.isFastDoubleClick()) return@setOnClickListener
            val selectMusicInfos = arrayListOf<MusicInfo>()
            var currentMusic: MusicInfo? = null
            musicCtrlBean.musicInfo.forEach {
                if (it.select == 1) {
                    selectMusicInfos.add(it)
                }
                if (it.index == musicCtrlBean.music) {
                    currentMusic = it
                }
            }
            //选择的音乐小于2
            if (selectMusicInfos.size < 2) {
                return@setOnClickListener
            }
            //当前没有播放中的音乐 就播放第一首
            if (currentMusic == null) {
                musicCtrlBean.music = selectMusicInfos[0].index
                musicCtrlBean.play = 1
                updateConfig()
                return@setOnClickListener
            }
            var nextItem: MusicInfo? = null
            var isNext = false//下一个是否是结果
            run {
                musicCtrlBean.musicInfo.forEach {
                    if (isNext && it.select == 1) {
                        nextItem = it
                        return@run
                    }
                    if (currentMusic == it) {
                        isNext = true
                    }
                }
            }

            if (nextItem == null) {
                nextItem = selectMusicInfos.first()
            }
            musicCtrlBean.music = nextItem!!.index
            musicCtrlBean.play = 1
            updateConfig()
        }
        //暂停/播放
        binding?.ivPlayer?.setOnClickListener {
            if (XUtils.isFastDoubleClick()) return@setOnClickListener
            val selectMusicInfos = arrayListOf<MusicInfo>()
            musicCtrlBean.musicInfo.forEach {
                if (it.select == 1) {
                    selectMusicInfos.add(it)
                }
            }
            if (selectMusicInfos.size == 0) return@setOnClickListener
            musicCtrlBean.play = if (musicCtrlBean.play == 1) 0 else 1
            updateConfig()
        }
        //音量
        binding?.ivPlayVoice?.setOnClickListener {
            if (XUtils.isFastDoubleClick()) return@setOnClickListener
            showMusicVoice(it)
        }
    }

    private fun showMusicVoice(view: View) {
        // 创建 PopupWindow
        val popupView = LayoutInflater.from(mContext).inflate(R.layout.pop_music_voice, null)
        val popupWindow = PopupWindow(
            popupView,
            70.dp,
            215.dp
        )
        popupWindow.elevation = 5.dp.toFloat()
        // 设置 PopupWindow 的点击外部区域是否消失
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        // 显示 PopupWindow
        popupWindow.showAsDropDown(
            view,
            -25.dp,
            0
        )
        val voiceSeek = popupView.findViewById<VerticalSeekBar>(R.id.voiceSeek)
        if (musicCtrlBean.volume >= 0) {
            voiceSeek.progress = musicCtrlBean.volume
        }
        if (musicCtrlBean.volume <= 0) {
            binding?.ivPlayVoice?.setImageResource(R.drawable.player_icon_mute)
        } else {
            binding?.ivPlayVoice?.setImageResource(R.drawable.player_icon_voice)
        }
        //音量
        voiceSeek.setStopScrollCallBack {
            musicCtrlBean.volume = voiceSeek.progress
            if (musicCtrlBean.volume <= 0) {
                binding?.ivPlayVoice?.setImageResource(R.drawable.player_icon_mute)
            } else {
                binding?.ivPlayVoice?.setImageResource(R.drawable.player_icon_voice)
            }
            updateConfig()
        }
    }

    val mAdapter =
        object : BaseQuickAdapter<MusicInfo, BaseViewHolder>(
            R.layout.item_music_player,
            musicCtrlBean.musicInfo
        ) {
            override fun convert(holder: BaseViewHolder, item: MusicInfo) {
                val tvName = holder.getView<TextView>(R.id.tvName)
                val ivSelect = holder.getView<ImageView>(R.id.ivSelect)
                val ivPlaying = holder.getView<ImageView>(R.id.ivPlaying)
                val clRoot = holder.getView<View>(R.id.clRoot)
                tvName.text = item.name
                ivSelect.isSelected = item.select == 1
                clRoot.setBackgroundColor(if (musicCtrlBean.music == item.index) mContext.getResources().getColor(R.color.color_f9f9fa) else Color.WHITE)
                ivPlaying.setVisible(musicCtrlBean.music == item.index)
                tvName.isSelected = musicCtrlBean.music == item.index
            }
        }

    private fun initRecyclerView() {
        binding?.recyclerView?.layoutManager = LinearLayoutManager(mContext)
        binding?.recyclerView?.adapter = mAdapter
        mAdapter.addChildClickViewIds(R.id.ivSelect, R.id.tvName)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.ivSelect -> {
                    if (XUtils.isFastDoubleClick()) return@setOnItemChildClickListener
                    val item = musicCtrlBean.musicInfo[position]
                    item.select = if (item.select == 0) 1 else 0
                    updateConfig()
                }

                R.id.tvName -> {
                    if (XUtils.isFastDoubleClick()) return@setOnItemChildClickListener
                    val item = musicCtrlBean.musicInfo[position]
                    //如果当前播放的就是这首 不做处理
                    if (musicCtrlBean.play == 1 && musicCtrlBean.music == item.index) {
                        return@setOnItemChildClickListener
                    }
                    item.select = 1
                    musicCtrlBean.play = 1
                    musicCtrlBean.music = item.index
                    updateConfig()
                }
            }

        }
    }

    private fun updateConfig(isOnlyUpdateView: Boolean = false) {
        val selectMusicInfos = arrayListOf<MusicInfo>()
        musicCtrlBean.musicInfo.forEach {
            if (it.select == 1) {
                selectMusicInfos.add(it)
            }
        }
        //循环模式
        when (musicCtrlBean.loop) {
            0 -> {//0：单曲循环
                binding?.ivPlaySortType?.setImageResource(R.drawable.player_icon_single)
            }

            1 -> {//1：列表循环
                binding?.ivPlaySortType?.setImageResource(R.drawable.player_icon_loop)
            }

            2 -> {//2：随机循环
                binding?.ivPlaySortType?.setImageResource(R.drawable.player_icon_random)
            }
        }
        //上一首下一首
        if (selectMusicInfos.size < 2) {
            binding?.ivPlayerPrevious?.setImageResource(R.drawable.player_icon_previous_no)
            binding?.ivPlayerNext?.setImageResource(R.drawable.player_icon_next_no)
        } else {
            binding?.ivPlayerPrevious?.setImageResource(R.drawable.player_icon_previous)
            binding?.ivPlayerNext?.setImageResource(R.drawable.player_icon_next)
        }
        //暂停/播放
        if (musicCtrlBean.play == 1) {
            if (selectMusicInfos.size == 0) {
                binding?.ivPlayer?.setImageResource(R.drawable.player_icon_suspend_no_gery)
            } else {
                binding?.ivPlayer?.setImageResource(R.drawable.player_icon_suspend)
            }
        } else {
            if (selectMusicInfos.size == 0) {
                binding?.ivPlayer?.setImageResource(R.drawable.player_icon_play_gery)
            } else {
                binding?.ivPlayer?.setImageResource(R.drawable.player_icon_play)
            }
        }
        //音量
        if (musicCtrlBean.volume <= 0) {
            binding?.ivPlayVoice?.setImageResource(R.drawable.player_icon_mute)
        } else {
            binding?.ivPlayVoice?.setImageResource(R.drawable.player_icon_voice)
        }
        mAdapter.notifyDataSetChanged()
        if (isOnlyUpdateView) {
            run {
                mAdapter.data.forEachIndexed { index, musicInfo ->
                    if (musicCtrlBean.music == musicInfo.index) {
                        binding?.recyclerView?.scrollToPosition(index)
                        return@run
                    }
                }
            }
        }
        if (!isOnlyUpdateView) {
            musicCtrlBean.selMusicNum = selectMusicInfos.size
            onUpdateMusicConfigListener?.updateMusicConfig(musicCtrlBean)
        }
    }

    override fun needBgDim(): Boolean {
        return false
    }

    /**
     * 监听歌曲变化
     */
    private fun listenerSongChange() {
        if (isStopping) return
        mUserId = FunSDK.GetId(mUserId, this)
        val jsonObject = JSONObject()
        try {
            jsonObject["Name"] = "OPTUpData"
            jsonObject["SessionID"] = "0x08"
            val optUpDataBean = OPTUpDataBean()
            optUpDataBean.specificType = "MusicPlay"
            optUpDataBean.upLoadDataType = SDKCONST.UploadDataType.GENERAL_STATE
            jsonObject["OPTUpData"] = optUpDataBean
            sendJsonData = jsonObject.toString()
            if (TextUtils.isEmpty(devId)) {
                return
            }
            FunSDK.DevGeneralStartUploadData(
                mUserId,
                devId,
                sendJsonData,
                0,
                SDKCONST.UploadDataType.GENERAL_STATE,
                0
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 停止监听歌曲变化
     */
    private fun stopListener() {
        isStopping = true
        FunSDK.DevGeneralStopUploadData(
            mUserId,
            devId,
            sendJsonData,
            0,
            SDKCONST.UploadDataType.GENERAL_STATE,
            0
        )
    }

    override fun OnFunSDKResult(msg: Message, ex: MsgContent): Int {
        when (msg.what) {
            EUIMSG.EMSG_DEV_START_UPLOAD_DATA -> {
                Log.e("tag1", "监听开始" + msg.arg1)
                //如果失败了重试三次
                if (msg.arg1 < 0 && ex.seq < 3 && !isStopping) {
                    val sn = G.ToString(ex.pData)
                    if (XUtils.isSn(sn)) {
                        FunSDK.DevGeneralStartUploadData(
                            mUserId,
                            sn,
                            sendJsonData,
                            0,
                            SDKCONST.UploadDataType.GENERAL_STATE,
                            ex.seq + 1
                        )
                    }
                }
            }

            EUIMSG.EMSG_DEV_STOP_UPLOAD_DATA -> {
                Log.e("tag1", "监听停止" + msg.arg1)
            }

            EUIMSG.EMSG_DEV_ON_UPLOAD_DATA -> {
                try {
                    val resultStr = G.ToString(ex.pData)
                    Log.e("tag1", "监听中$resultStr")
                    val musicInfoChangeBean =
                        Gson().fromJson(resultStr, MusicInfoChangeBean::class.java)
                    if (musicInfoChangeBean != null && musicInfoChangeBean.dateType == "MusicPlay") {
                        musicCtrlBean.music = musicInfoChangeBean.extData.index
                        updateConfig(true)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return 0
    }

}


interface OnUpdateMusicConfigListener {
    fun updateMusicConfig(musicCtrlContent: MusicCtrlContent)
}