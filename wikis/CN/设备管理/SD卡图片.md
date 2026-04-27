## 图片查询
- 接口

```java
/**
 * 查询设备本地图片
 * @param devId 设备序列号
 * @param info 查询信息
 * @return 是否成功
 */
boolean searchPictureByFile(@NonNull String devId,@NonNull SearchFileInfo info);

```

- 参数说明

| 参数     | 类型  | 描述     |取值范围 |
| :------|--- | -------- | --- |
| devId|`String `| 设备序列号   | 非空|
| info| `SearchFileInfo `| 查询信息 | 非空|

- 示例


```java
 /**
 * 按文件方式查询图片，以文件列表方式显示
 * @param searchTime 查询时间
 */
@Override
public void searchPicByFile(Calendar searchTime) {
    Calendar startTime = searchTime;
    startTime.set(Calendar.HOUR_OF_DAY, 0);
    startTime.set(Calendar.MINUTE, 0);
    startTime.set(Calendar.SECOND, 0);

    Calendar endTime = (Calendar) searchTime.clone();
    endTime.set(Calendar.HOUR_OF_DAY, 23);
    endTime.set(Calendar.MINUTE, 59);
    endTime.set(Calendar.SECOND, 59);

    SearchFileInfo searchFileInfo = new SearchFileInfo();
    searchFileInfo.setChnId(0);//通道号
    searchFileInfo.setStartTime(startTime);//查询开始时间
    searchFileInfo.setEndTime(endTime);//查询结束时间
    
    manager.searchPictureByFile(getDevId(), searchFileInfo);
}

```
## 图片下载
- 接口

```java
/**
 * 添加到下载队列
 * @param info
 */
void addDownload(DownloadInfo info);

/**
 * 开始下载
 */
void startDownload();

/**
 * 停止下载
 */
void stopDownload();

```
- 示例

```java
DownloadManager downloadManager = DownloadManager.getInstance(this);
H264_DVR_FILE_DATA data = picList.get(position);//图片查询返回的列表数据
if (data != null) {
    String fileName = data.getFileName() + ".jpg";//图片保存路径
    DownloadInfo downloadInfo = new DownloadInfo();
    downloadInfo.setStartTime(TimeUtils.getNormalFormatCalender(data.getStartTimeOfYear()));//开始时间
    downloadInfo.setEndTime(TimeUtils.getNormalFormatCalender(data.getEndTimeOfYear()));//结束时间和结束时间是一样的
    downloadInfo.setDevId(getDevId());//设备序列号
    downloadInfo.setObj(data);
    downloadInfo.setDownloadType(0);//下载类型：0->卡存按文件下载 1->云存储下载 2->卡存按时间下载
    downloadInfo.setSaveFileName(SDKDemoApplication.PATH_PHOTO + File.separator + fileName);
    downloadManager.addDownload(downloadInfo);
    downloadManager.startDownload();//开始下载
}

```

## 缩略图下载
- 接口

```java
/**
 * 下载设备端图片
 *
 * @param h264DvrFileData 图片信息
 * @param nSeq            回传的Id（在回调的时候知道是哪个消息发送的）
 * @param imgWidth        图片宽度
 * @param imgHeight       图片高度
 * @return
 */
public Bitmap downloadVideoThumb(H264_DVR_FILE_DATA h264DvrFileData, int nSeq, int imgWidth, int imgHeight)

```
- 参数说明

| 参数     | 类型  | 描述     |取值范围 |
| :------|--- | -------- | --- |
| h264DvrFileData|`H264_DVR_FILE_DATA `| 图片信息   | 非空|
| nSeq            | `int `| 回传的Id（在回调的时候知道是哪个消息发送的） | |
| imgWidth        | `int `| 图片宽度 | |
| imgHeight       | `int `| 图片高度 | |

- 示例

```java
//初始化设备图片下载管理（缩略图），传入要保存的图片路径
devImageManager = new DevImageManager(SDKDemoApplication.PATH_PHOTO_TEMP);
devImageManager.setOnImageManagerListener(new BaseImageManager.OnImageManagerListener() {
    /**
     * 下载回调结果
     *
     * @param isSuccess 是否成功
     * @param imagePath 图片路径
     * @param bitmap
     * @param mediaType 媒体类型
     * @param seq
     */
    @Override
    public void onDownloadResult(boolean isSuccess, String imagePath, Bitmap bitmap, int mediaType, int seq) {
        ListSelectItem listSelectItem = rvDevPic.findViewWithTag(seq);
        if (listSelectItem != null) {
            if (isSuccess) {
                listSelectItem.getImageLeft().setImageBitmap(bitmap);
            } else {
                listSelectItem.setLeftImageResource(R.mipmap.ic_thumb);
            }
        }
    }

    /**
     * 删除图片回调结果
     *
     * @param isSuccess
     * @param seq
     */
    @Override
    public void onDeleteResult(boolean isSuccess, int seq) {
        // 设备卡存相册中的图片无法单个删除，只能格式化存储卡
    }
});//监听回调事件

devImageManager.setDevId(presenter.getDevId());//设置设备序列号

Bitmap bitmap = devImageManager.downloadVideoThumb(imageInfo, position, 160, 90);

```
> <span style="color:red;">downloadVideoThumb 这个方法，第一次调用的时候会把缩略图保存到手机本地，下次调用的时候先从手机本地获取，获取失败的时候才去设备端下载 </span>

## 图片日历查询
- 接口

```java
/**
 * @param searchCalendar    搜索日期（月份）
 * @param mediaType         文件类型 0->卡存的文件 1->云存储的文件 2->报警事件
 * @param position          回传的Id（在回调的时候知道是哪个消息发送的）
 * @param isClearBeforeData 是否清除之前查询缓存的数据
 */
public void searchFile(@NonNull Calendar searchCalendar, int mediaType, int position, boolean isClearBeforeData)

```

- 参数说明

| 参数     | 类型  | 描述     |取值范围 |
| :------|--- | -------- | --- |
| searchCalendar|`Calendar `|  搜索日期（月份）   | 非空|
| mediaType         | `int `| 文件类型 |0->卡存的文件 1->云存储的文件 2->报警事件 |
| position          | `int `| 回传的Id（在回调的时候知道是哪个消息发送的） |大于等于0 |
| isClearBeforeData | `boolean`| 是否清除之前查询缓存的数据 | |

- 示例

```java
MediaFileCalendarManager mediaFileCalendarManager = new MediaFileCalendarManager(this);
mediaFileCalendarManager.init(devId, Calendar.getInstance(), "jpg");//devId -> 设备序列号，查询的时间（可以传null，在调用search的时候再传也行），jpg -> 媒体类型：jpg、h264
Calendar searchCalendar = Calendar.getInstance();
mediaFileCalendarManager.searchFile(searchCalendar, Define.MEDIA_TYPE_DEVICE, 0);
```