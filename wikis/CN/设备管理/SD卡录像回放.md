### 1.视频播放
- 接口说明

```
RecordManager createRecordPlayer(ViewGroup playView, String devId, int mediaType);
```
- 参数说明

| 参数     |类型  | 描述     | 取值范围|
| :--------- | -------- |------ |------ |
| playView | ViewGroup | 播放画布   | 非空|
| devId  | String| 设备序列号 | 非空|
| mediaType | int| 回放类型  |PLAY_DEV_PLAYBACK：本地录像回放；<br> PLAY_CLOUD_PLAYBACK：云存储远程录像回放 |


- 示例代码

```
/**
 * mediaType:
 * MediaManager.PLAY_DEV_PLAYBACK:本地录像回放
 * MediaManager.PLAY_CLOUD_PLAYBACK:云存储远程录像回放
 */
RecordManager mediaManager = createRecordPlayer(playView,getDevId(), recordType);
mediaManager.setChnId(0);//设置通道号
mediaManager.pausePlay();//暂停播放
mediaManager.rePlay();//恢复播放
mediaManager.stopPlay();//停止播放
mediaManager.destroyPlay();//销毁播放
mediaManager.capture(String savePicPath);//抓图
mediaManager.startRecord(String saveVideoPath);//开始视频剪切
mediaManager.stopRecord();//结束视频剪切
mediaManager.openVoiceBySound();//打开音频
mediaManager.closeVoiceBySound();//关闭音频

recordManager.setOnMediaManagerListener(new MediaManager.OnRecordManagerListener() {
            /**
             * 录像文件查询成功结果
             * @param playerAttribute 播放器属性（包括播放状态参考值等）
             * @param data H264_DVR_FILE_DATA[]或者char[][] 具体实现参考Demo
             */
            @Override
            public void searchResult(PlayerAttribute playerAttribute, Object data) {

            }

            /**
             * 播放状态
             * @param attribute 播放器属性（包括播放状态参考值等）
             * @param state 播放状态
             */
            @Override
            public void onMediaPlayState(PlayerAttribute attribute, int state) {
                switch (state) {
                    case PlayerAttribute.E_STATE_PlAY:
                        //播放状态
                        break;
                    case PlayerAttribute.E_STATE_PAUSE:
                        //暂停播放状态
                        break;
                    case PlayerAttribute.E_STATE_BUFFER:
                        //正在缓冲中
                        break;
                    case PlayerAttribute.E_STATE_STOP:
                        //停止播放状态
                        break;
                    case PlayerAttribute.E_STATE_RESUME:
                        //恢复播放状态
                        break;
                    case PlayerAttribute.E_STATE_MEDIA_SOUND_ON:
                        //音频打开状态
                        break;
                    case PlayerAttribute.E_STATE_MEDIA_SOUND_OFF:
                        //音频关闭状态
                        break;
                    case PlayerAttribute.E_STATE_PLAY_COMPLETED:
                        //录像播放结束状态
                        break;
                    case PlayerAttribute.E_STATE_PLAY_SEEK:
                        //录像回放定位状态
                        break;
                    case PlayerAttribute.E_STATE_SAVE_RECORD_FILE_S:
                        //视频剪切文件播放成功
                        break;
                    case PlayerAttribute.E_STATE_SAVE_PIC_FILE_S:
                        //图片文件保存成功
                        break;
                    default:
                        break;
                }

            }

            /**
             * 播放失败
             * @param attribute 播放器属性（包括播放状态参考值等）
             * @param msgId 消息Id
             * @param errorId 错误Id 参考 EFUN_ERROR
             */
            @Override
            public void onFailed(PlayerAttribute attribute, int msgId, int errorId) {

            }

            /**
             * 显示码流和时间戳
             * @param attribute 播放器属性（包括播放状态参考值等）
             * @param isShowTime 是否显示时间戳
             * @param time 时间戳
             * @param rate 码流
             */
            @Override
            public void onShowRateAndTime(PlayerAttribute attribute,  boolean isShowTime, String time, String rate) {

            }

            /**
             * 视频缓冲结束
             * @param attribute 播放器属性（包括播放状态参考值等）
             * @param msgContent
             */
            @Override
            public void onVideoBufferEnd(PlayerAttribute attribute, MsgContent msgContent) {

            }
        });
```
### 2.录像定位播放
- 接口说明

```java
/**
 * 录像时间定位
 * @param nTimes 24小时内换算的时间，单位秒
 * @param absTime
 * int[] time = {2020,11,5, 0, 0, 0};
 * absTime = nTimes + FunSDK.ToTimeType(time)
 * @return
 */
int seekToTime(int nTimes, int absTime)
```

- 示例代码

```java
/**
 * @param times 当前要播放的时间点（24小时内换算的时间，单位秒，比如：0点10分就是换成600秒）
 */
@Override
public void seekToTime(int times) {
    //获取当前播放的日期
    int[] dateTime = {searchTime.get(Calendar.YEAR), searchTime.get(Calendar.MONTH) + 1,
            searchTime.get(Calendar.DAY_OF_MONTH), 0, 0, 0};
    int absTime = FunSDK.ToTimeType(dateTime) + times;
    recordManager.seekToTime(times, absTime);
}

```

### 3.录像下载

- 接口说明

```
void startDownload();
```

- 示例代码

```
DownloadManager downloadManager = DownloadManager.getInstance(new DownloadManager.OnDownloadListener() {
            @Override
            public void onDownload(DownloadInfo downloadInfo) {
                if (state == DOWNLOAD_STATE_FAILED) {
                   //下载失败
                }else if (state == DOWNLOAD_STATE_START) {
                   //开始下载
                }else if (state == DOWNLOAD_STATE_COMPLETE_ALL) {
                   //下载完成
                }
            }
        });
H264_DVR_FILE_DATA data;
String fileName = data.getStartTimeOfYear() + "_" + data.getEndTimeOfYear() + ".mp4";//保存的录像文件名
DownloadInfo downloadInfo = new DownloadInfo();
downloadInfo.setStartTime(TimeUtils.getNormalFormatCalender(data.getStartTimeOfYear()));
downloadInfo.setEndTime(TimeUtils.getNormalFormatCalender(data.getEndTimeOfYear()));
downloadInfo.setDevId(getDevId());
downloadInfo.setObj(data);
downloadInfo.setDownloadType(recordPlayType == PLAY_CLOUD_PLAYBACK ? DOWNLOAD_VIDEO_BY_CLOUD : DOWNLOAD_VIDEO_BY_FILE);
downloadInfo.setSaveFileName("需要保存录像的文件路径");
downloadManager.addDownload(downloadInfo);//添加到下载队列中

downloadManager.startDownload();//下载

downloadManager.stopDownload();//停止下载


```

![](https://obs-xm-pub.obs.cn-south-1.myhuaweicloud.com/docs/20231215/1702631971427.gif)