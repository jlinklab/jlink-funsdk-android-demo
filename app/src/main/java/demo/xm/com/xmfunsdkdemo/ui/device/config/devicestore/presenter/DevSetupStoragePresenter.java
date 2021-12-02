package demo.xm.com.xmfunsdkdemo.ui.device.config.devicestore.presenter;

import com.alibaba.fastjson.JSON;
import com.basic.G;
import com.lib.FunSDK;
import com.lib.SDKCONST;
import com.lib.sdk.bean.GeneralInfoBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.OPStorageManagerBean;
import com.lib.sdk.bean.StorageInfoBean;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.manager.db.DevDataCenter;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.manager.device.idr.IdrDefine;
import com.utils.FileUtils;
import com.xm.activity.base.XMBasePresenter;

import java.text.DecimalFormat;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.ui.device.config.devicestore.listener.DevSetupStorageContract;

/**
 * 设备存储管理界面,包括录像分区及图片分区的存储容量,剩余容量,录像满时停止或是循环录像.
 * Created by jiangping on 2018-10-23.
 */
public class DevSetupStoragePresenter extends XMBasePresenter<DeviceManager> implements DevSetupStorageContract.IDevSetupStoragePresenter {

    private DevSetupStorageContract.IDevSetupStorageView iDevSetupStorageView;
    private DevConfigManager devConfigManager;
    /**
     * 是否按百分比显示存储空间
     */
    private boolean mIsShowPercent;
    /**
     * 存储空间信息
     */
    private List<StorageInfoBean> mStorageInfoBean;

    /**
     * 存储空间总量
     */
    private long mTotalSize;
    /**
     * 剩余存储空间
     */
    private long mRemainSize;
    /**
     * 驱动类型
     */
    private long driverType;
    /**
     * 存储配置
     */
    private GeneralInfoBean mGeneralInfo;
    /**
     * 存储空间总量显示
     */
    private String mTotalSizeString;
    /**
     * 剩余存储空间显示
     */
    private String mRemainSizeString;
    /**
     * 视频分区空间总量显示
     */
    private String mVideoPartSizeString;
    /**
     * 图片分区空间总量显示
     */
    private String mPicPartSizeString;
    /**
     * 是否显示图片分区
     */
    private boolean mIsShowPicPart;

    public DevSetupStoragePresenter(DevSetupStorageContract.IDevSetupStorageView iDevSetupStorageView) {
        this.iDevSetupStorageView = iDevSetupStorageView;
    }


    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void setDevId(String devId) {
        devConfigManager = manager.getDevConfigManager(devId);
        super.setDevId(devId);
    }

    /**
     * 获取存储容量信息
     */
    @Override
    public void getStorageInfo() {
        //根据能力判断是否按百分比显示存储空间
        mIsShowPercent = false;//= FunSDK.GetDevAbility(getDevId(), "PreviewFunction/StorageSpaceUsePercent") == SDKCONST.Switch.Open;
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                String jsonStr = null;
                if (result instanceof String) {
                    jsonStr = (String) result;
                }else {
                    jsonStr =  JSON.toJSONString(result);
                }

                HandleConfigData handleConfigData = new HandleConfigData();
                if (handleConfigData.getDataObj(jsonStr, StorageInfoBean.class)) {
                    //存储信息解析
                    mStorageInfoBean = (List<StorageInfoBean>) handleConfigData.getObj();
                    dealWithStorageInfo();
                } else {
                    iDevSetupStorageView.getStorageDataError(FunSDK.TS("Json_Parse_F"));
                }

            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevSetupStorageView.getStorageDataError(FunSDK.TS("Data_exception")+"：" + errorId);
            }
        });

        devConfigInfo.setJsonName(JsonConfig.STORAGE_INFO);
        devConfigInfo.setChnId(-1);
        devConfigInfo.setCmdId(1020);
        devConfigManager.setDevCmd(devConfigInfo);
    }

    /**
     * 格式化SD卡
     */
    @Override
    public void formatStorage() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                iDevSetupStorageView.onFormatResult(true);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevSetupStorageView.onFormatResult(false);
            }
        });

        devConfigInfo.setJsonName(JsonConfig.OPSTORAGE_MANAGER);
        devConfigInfo.setTimeOut(10000);
        devConfigInfo.setChnId(-1);

        OPStorageManagerBean opStorageManagerBean = new OPStorageManagerBean();
        opStorageManagerBean.setAction("Clear");
        opStorageManagerBean.setSerialNo(0);
        opStorageManagerBean.setType("Data");
        opStorageManagerBean.setPartNo(0);//分区号，如果有多个分区需要多次发送格式化命令

        String jsonData = HandleConfigData.getSendData(JsonConfig.OPSTORAGE_MANAGER,"0x08",opStorageManagerBean);
        devConfigInfo.setJsonData(jsonData);
        devConfigManager.setDevConfig(devConfigInfo);
    }

    /**
     * 获取存储配置
     */
    @Override
    public void getStorageConfig() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                String jsonStr = null;
                if (result instanceof String) {
                    jsonStr = (String) result;
                }else {
                    jsonStr =  JSON.toJSONString(result);
                }

                HandleConfigData handleConfigData = new HandleConfigData();
                if (handleConfigData.getDataObj(jsonStr, GeneralInfoBean.class)) {
                    mGeneralInfo = (GeneralInfoBean) handleConfigData.getObj();
                    iDevSetupStorageView.getStorageConfigResult(mGeneralInfo);
                } else {
                    iDevSetupStorageView.getStorageConfigResult(null);
                }

            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevSetupStorageView.getStorageConfigResult(null);
            }
        });

        devConfigInfo.setJsonName(JsonConfig.GENERAL_GENERAL);
        devConfigInfo.setChnId(-1);
        devConfigInfo.setCmdId(1042);
        devConfigManager.setDevCmd(devConfigInfo);
    }


    /**
     * 保存存储配置
     */
    private void saveStorageConfig() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                iDevSetupStorageView.changeVideoFullStateResult(true);
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                iDevSetupStorageView.changeVideoFullStateResult(false);
            }
        });

        devConfigInfo.setJsonName(JsonConfig.GENERAL_GENERAL);
        String jsonData = HandleConfigData.getSendData(JsonConfig.GENERAL_GENERAL, "0x08", mGeneralInfo);
        devConfigInfo.setJsonData(jsonData);
        devConfigInfo.setChnId(-1);
        devConfigManager.setDevConfig(devConfigInfo);
    }


    /**
     * 处理存储信息
     */
    private void dealWithStorageInfo() {
        mRemainSize = 0;
        mTotalSize = 0;
        long videoSize = 0;
        long picSize = 0;
        if (mStorageInfoBean != null) {
            boolean hasStorageError = false;
            //累加存储信息列表及各分区的空间数据计算存储空间信息
            for (StorageInfoBean mStorageInfo : mStorageInfoBean) {
                for (int i = 0; i < mStorageInfo.PartNumber && i < mStorageInfo.Partition.size(); i++) {
                    StorageInfoBean.Partition partition = mStorageInfo.Partition.get(i);
                    if (null == partition) {
                        continue;
                    }
                    if (partition.Status != 0) {
                        //先记录状态,存储状态异常
                        hasStorageError = true;
                        continue;
                    }

                    driverType = partition.DirverType;
                    long remainSize = G.getLongFromHex(partition.RemainSpace);
                    long totalSize = G.getLongFromHex(partition.TotalSpace);

                    if (driverType == SDKCONST.SDK_FileSystemDriverTypes.SDK_DRIVER_READ_WRITE
                            || driverType == SDKCONST.SDK_FileSystemDriverTypes.SDK_DRIVER_IMPRCD) {
                        //视频分区
                        mRemainSize += remainSize;
                        mTotalSize += totalSize;
                        videoSize += totalSize;
                    } else if (driverType == SDKCONST.SDK_FileSystemDriverTypes.SDK_DRIVER_SNAPSHOT) {
                        //图片分区
                        mRemainSize += remainSize;
                        mTotalSize += totalSize;
                        picSize += totalSize;
                    }

                }
            }
            if (mTotalSize == 0 && hasStorageError) {
                //存储容量为0，且存储状态异常，显示存储异常
                iDevSetupStorageView.getStorageDataError("存储状态异常");
                return;
            }

            if (mTotalSize == 0) {
                //无存储卡
                iDevSetupStorageView.getStorageDataError("无存储卡");
                return;
            }

            if (mTotalSize > 0) {
                //有SD卡
                double remainPercent = Math.min(((double) mRemainSize * 100 / (double) mTotalSize),100);

                String remain;
                DecimalFormat decimalFormat = new DecimalFormat("#0.0");
                remain = decimalFormat.format(remainPercent) + "%";

                if (mIsShowPercent) {
                    //按百分比显示存储空间
                    double videoPercent = Math.min(((double) videoSize * 100 / (double) mTotalSize),100);
                    double picPercent = Math.min(((double) picSize * 100 / (double) mTotalSize),100);
                    mTotalSizeString = "100.0%";
                    mRemainSizeString = remain;
                    mVideoPartSizeString = decimalFormat.format(videoPercent) + "%";
                    mPicPartSizeString = decimalFormat.format(picPercent) + "%";
                }else {
                    //按空间大小显示
                    mTotalSizeString = FileUtils.FormetFileSize(mTotalSize, 2);
                    mRemainSizeString = FileUtils.FormetFileSize(mRemainSize, 2);
                    mVideoPartSizeString = FileUtils.FormetFileSize(videoSize, 2);
                    mPicPartSizeString = FileUtils.FormetFileSize(picSize, 2);
                }
            }
        }

        SDBDeviceInfo info = DevDataCenter.getInstance().getDevInfo(getDevId()).getSdbDevInfo();
        if (info != null && IdrDefine.isIDR(info.st_7_nType)) {
            //低功耗设备无图片分区
            mIsShowPicPart = false;
        } else {
            mIsShowPicPart = true;
        }

        iDevSetupStorageView.getStorageDataSuccess(mTotalSizeString,mRemainSizeString,mVideoPartSizeString,mPicPartSizeString,mIsShowPicPart);
    }

    /**
     * 修改存储满时配置
     */
    @Override
    public void changeVideoFullState(boolean isStopWrite) {
        if (mGeneralInfo != null) {
            if(isStopWrite){
                //存储满时：停止录像
                mGeneralInfo.OverWrite = "StopRecord";
            } else {
                //存储满时：循环录像
                mGeneralInfo.OverWrite = "OverWrite";
            }
            saveStorageConfig();
        } else {
            iDevSetupStorageView.changeVideoFullStateResult(false);
        }
    }
}

