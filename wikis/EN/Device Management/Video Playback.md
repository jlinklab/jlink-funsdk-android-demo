# Video Playback
### 1.Video Play
- Interface Description

```
RecordManager createRecordPlayer(ViewGroup playView, String devId, int mediaType);

```

- Parameter Description



|Parameter | Comment|
| :--------- | -------- |
|PlayView | Play the canvas|
|DevId | Device serial number|
| mediaType | mediaType  |


- Sample code


```
/**
 * mediaType:
 * MediaManager.PLAY_DEV_PLAYBACK:Local video playback
 * MediaManager.PLAY_CLOUD_PLAYBACK:Cloud storage remote video playback
 */
RecordManager mediaManager = createRecordPlayer(playView,getDevId(), recordType);
mediaManager.setChnId(0);//Setting a Channel Number
mediaManager.pausePlay();//Pause play
mediaManager.rePlay();//Resuming play
mediaManager.stopPlay();//Stop playing
mediaManager.destroyPlay();//Destroy play
mediaManager.capture(String savePicPath);//screenshots
mediaManager.startRecord(String saveVideoPath);//Start video cutting
mediaManager.stopRecord();//End Video Clipping
mediaManager.openVoiceBySound();//Turn on the audio
mediaManager.closeVoiceBySound();//Turn off audio

recordManager.setOnMediaManagerListener(new MediaManager.OnRecordManagerListener() {
            /**
             * Successful query result of the video file
             * @param playerAttribute Player properties (including playback status reference values, etc.）
             * @param data H264_DVR_FILE_DATA[] or char[][] For details, refer to Demo
             */
            @Override
            public void searchResult(PlayerAttribute playerAttribute, Object data) {

            }

            /**
             * Playing status
             * @param attribute Player Properties（This parameter includes the reference value of Playing status）
             * @param state Playing status
             */
            @Override
            public void onMediaPlayState(PlayerAttribute attribute, int state) {
                switch (state) {
                    case PlayerAttribute.E_STATE_PlAY:
                        //Playing status
                        break;
                    case PlayerAttribute.E_STATE_PAUSE:
                        //suspended Playing status
                        break;
                    case PlayerAttribute.E_STATE_BUFFER:
                        //In the buffer
                        break;
                    case PlayerAttribute.E_STATE_STOP:
                        //stop Playing status
                        break;
                    case PlayerAttribute.E_STATE_RESUME:
                        //restore Playing status
                        break;
                    case PlayerAttribute.E_STATE_MEDIA_SOUND_ON:
                        //Audio on state
                        break;
                    case PlayerAttribute.E_STATE_MEDIA_SOUND_OFF:
                        //Audio off state
                        break;
                    case PlayerAttribute.E_STATE_PLAY_COMPLETED:
                        //End Status of video playback
                        break;
                    case PlayerAttribute.E_STATE_PLAY_SEEK:
                        //Video playback location status
                        break;
                    case PlayerAttribute.E_STATE_SAVE_RECORD_FILE_S:
                        //The video clipping file is played successfully
                        break;
                    case PlayerAttribute.E_STATE_SAVE_PIC_FILE_S:
                        //The image file is saved successfully. Procedure
                        break;
                    default:
                        break;
                }

            }

            /**
             * Play failure
             * @param attribute Player properties (including Playing status reference value, etc.)
             * @param msgId Message Id
             * @param errorId For the error Id, see EFUN_ERROR
             */
            @Override
            public void onFailed(PlayerAttribute attribute, int msgId, int errorId) {

            }

            /**
             * Displays the stream and timestamp
             * @param attribute Player properties (including Playing status reference value, etc.)
             * @param isShowTime Whether to display the timestamp
             * @param time Time stamp
             * @param rate stream
             */
            @Override
            public void onShowRateAndTime(PlayerAttribute attribute,  boolean isShowTime, String time, String rate) {

            }

            /**
             * End of video buffer
             * @param attribute Player properties (including Playing status reference value, etc.)
             * @param msgContent
             */
            @Override
            public void onVideoBufferEnd(PlayerAttribute attribute, MsgContent msgContent) {

            }
        });

```

### 2.Video download
- Interface Description

```
void startDownload();
```

- Sample code

```
DownloadManager downloadManager = DownloadManager.getInstance(new DownloadManager.OnDownloadListener() {
            @Override
            public void onDownload(DownloadInfo downloadInfo) {
                if (state == DOWNLOAD_STATE_FAILED) {
                   //Download failed
                }else if (state == DOWNLOAD_STATE_START) {
                   //Start downloading
                }else if (state == DOWNLOAD_STATE_COMPLETE_ALL) {
                   //Download completed
                }
            }
        });
H264_DVR_FILE_DATA data;
String fileName = data.getStartTimeOfYear() + "_" + data.getEndTimeOfYear() + ".mp4";//File name of the saved video
DownloadInfo downloadInfo = new DownloadInfo();
downloadInfo.setStartTime(TimeUtils.getNormalFormatCalender(data.getStartTimeOfYear()));
downloadInfo.setEndTime(TimeUtils.getNormalFormatCalender(data.getEndTimeOfYear()));
downloadInfo.setDevId(getDevId());
downloadInfo.setObj(data);
downloadInfo.setDownloadType(recordPlayType == PLAY_CLOUD_PLAYBACK ? DOWNLOAD_VIDEO_BY_CLOUD : DOWNLOAD_VIDEO_BY_FILE);
downloadInfo.setSaveFileName("The file path to which you want to save the video");
downloadManager.addDownload(downloadInfo);//Add to the download list

downloadManager.startDownload();//download

downloadManager.stopDownload();//Stop downloading

```