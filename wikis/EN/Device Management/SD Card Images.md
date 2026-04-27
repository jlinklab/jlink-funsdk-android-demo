# SD Card Images

## Image Query

- API Description

```java
/**
 * Query local device images
 * @param devId Device serial number
 * @param info Query information
 * @return Whether successful
 */
boolean searchPictureByFile(@NonNull String devId, @NonNull SearchFileInfo info);


```
- Sample Code


```java
  /**
 * Query images by file, display as a file list
 * @param searchTime Query time
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
    searchFileInfo.setChnId(0);// Channel number
    searchFileInfo.setStartTime(startTime);// Query start time
    searchFileInfo.setEndTime(endTime);// Query end time
    
    manager.searchPictureByFile(getDevId(), searchFileInfo);
}

```
## Image Download
- API Description

```java
/**
 * Add to download queue
 * @param info
 */
void addDownload(DownloadInfo info);

/**
 * Start download
 */
void startDownload();

/**
 * Stop download
 */
void stopDownload();


```
- Sample Code

```java
DownloadManager downloadManager = DownloadManager.getInstance(this);
H264_DVR_FILE_DATA data = picList.get(position);// List data returned from image query
if (data != null) {
    String fileName = data.getFileName() + ".jpg";// Image save path
    DownloadInfo downloadInfo = new DownloadInfo();
    downloadInfo.setStartTime(TimeUtils.getNormalFormatCalender(data.getStartTimeOfYear()));// Start time
    downloadInfo.setEndTime(TimeUtils.getNormalFormatCalender(data.getEndTimeOfYear()));// End time, same as start time
    downloadInfo.setDevId(getDevId());// Device serial number
    downloadInfo.setObj(data);
    downloadInfo.setDownloadType(0);// Download type: 0->File download by card storage 1->Cloud storage download 2->File download by time in card storage
    downloadInfo.setSaveFileName(SDKDemoApplication.PATH_PHOTO + File.separator + fileName);
    downloadManager.addDownload(downloadInfo);
    downloadManager.startDownload();// Start download
}


```

## Thumbnail Download

- API Description


```java
/**
 * Download device images
 *
 * @param h264DvrFileData Image information
 * @param nSeq            Returned Id (know which message sent in the callback)
 * @param imgWidth        Image width
 * @param imgHeight       Image height
 * @return
 */
public Bitmap downloadVideoThumb(H264_DVR_FILE_DATA h264DvrFileData, int nSeq, int imgWidth, int imgHeight);

```

- Sample Code


```java
// Initialize device image download management (thumbnail), passing the path to save the image
devImageManager = new DevImageManager(SDKDemoApplication.PATH_PHOTO_TEMP);
devImageManager.setOnImageManagerListener(new BaseImageManager.OnImageManagerListener() {
    /**
     * Download callback result
     *
     * @param isSuccess Whether successful
     * @param imagePath Image path
     * @param bitmap
     * @param mediaType Media type
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
     * Delete image callback result
     *
     * @param isSuccess
     * @param seq
     */
    @Override
    public void onDeleteResult(boolean isSuccess, int seq) {
        // Unable to delete individual images in the device card storage album, can only format the storage card
    }
});// Listen for callback events

devImageManager.setDevId(presenter.getDevId());// Set device serial number

Bitmap bitmap = devImageManager.downloadVideoThumb(imageInfo, position, 160, 90);


```
> <span style="color:red;">The downloadVideoThumb method, the first time it is called, will save the thumbnail to the local phone. The next time it is called, it will first get it from the local phone. If it fails to get it, it will download it from the device.</span>

## Image Calendar Query

- API Description


```java
/**
 * @param searchCalendar    Search date (month)
 * @param mediaType         File type 0->File stored on the card 1->File stored in the cloud 2->Alarm event
 * @param position          Returned Id (know which message sent in the callback)
 * @param isClearBeforeData Whether to clear the previously queried cached data
 */
public void searchFile(@NonNull Calendar searchCalendar, int mediaType, int position, boolean isClearBeforeData);


```

- Sample Code


```java
MediaFileCalendarManager mediaFileCalendarManager = new MediaFileCalendarManager(this);
mediaFileCalendarManager.init(devId, Calendar.getInstance(), "jpg");//devId -> Device serial number, query time (can pass null, pass it when calling search), jpg -> Media type: jpg, h264
Calendar searchCalendar = Calendar.getInstance();
mediaFileCalendarManager.searchFile(searchCalendar, Define.MEDIA_TYPE_DEVICE, 0);

```