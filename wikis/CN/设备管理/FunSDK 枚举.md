
## 枚举

### 1. EUIMSG

```java
package com.lib;

public class EUIMSG {
	public final static int SYS_GET_DEV_INFO_BY_USER = 5000; // 获取设备信息
	public final static int SYS_USER_REGISTER = 5001; // 注册用户
	public final static int SYS_USER_CHECKVALID = 5002; // 检查用户名是否可用
	public final static int SYS_PSW_CHANGE = 5003; // 修改用户密码
	public final static int SYS_ADD_DEVICE = 5004; // 增加用户设备
	public final static int SYS_CHANGEDEVINFO = 5005; // 修改用户设备信息
	public final static int SYS_DELETE_DEV = 5006; // 删除设备
	public final static int SYS_TESTUPDATE = 5007; // 更新图片
	public final static int SYS_PSW_RESTORE = 5008; // 用户密码恢复
	public final static int SYS_GET_DEV_STATE = 5009; 	// 获取设备状态
	public final static int SYS_GET_PHONE_CHECK_CODE = 5010; // 获取手机验证码
	public final static int SYS_REGISER_USER_XM = 5011; // 用户注册
	public final static int SYS_GET_DEV_INFO_BY_USER_XM = 5012; // 同步登录
	public final static int SYS_EDIT_PWD_XM = 5013; // 忘记用户登录密码
	public final static int SYS_FORGET_PWD_XM = 5014; // 忘记用户登录密码
	public final static int SYS_REST_PWD_CHECK_XM = 5015; // 重置用户登录密码
	public final static int SYS_RESET_PWD_XM = 5016; // 重置用户登录密码

	public final static int SYS_DEV_GET_PUBLIC = 5017; // 获取用户公开设备列表
	public final static int SYS_DEV_GET_SHARE = 5018; // 获取用户共享设备列表
	public final static int SYS_DEV_PUBLIC = 5019; // 公开设备
	public final static int SYS_DEV_SHARE = 5020; // 分享设备(分享视频)
	public final static int SYS_DEV_CANCLE_PUBLIC = 5021; // 取消公开设备
	public final static int SYS_DEV_CANCLE_SHARE = 5022; // 取消分享设备
	public final static int SYS_DEV_REGISTER = 5023;
	public final static int SYS_DEV_COMMENT = 5024;
	public final static int SYS_DEV_GET_COMMENT_LIST = 5025;
	public final static int SYS_DEV_GET_VIDEO_INFO = 5026; // 视频广场获取录像信息
	public final static int SYS_DEV_UPLOAD_VIDEO = 5027; // 视频广场上传
	public final static int SYS_DEV_GET_PHOTOS_LIST = 5028;
	public final static int SYS_DEV_CREATE_PHOTOS = 5029;
	public final static int SYS_DEV_UPLOAD_PHOTO = 5030;
	public final static int SYS_DEV_EDIT_PHOTO = 5031;
	public final static int SYS_DEV_GET_VIDEO_LIST = 5032;// 视频广场 短片列表
	public final static int SYS_DEV_EDIT_VIDEO = 5033;// 视频广场上传 编辑短片
	public final static int SYS_DEV_GET_PHOTO_LIST = 5034;// 视频广场图片列表
	public final static int SYS_DEV_DELETE_VIDEO = 5035;// 视频广场 删除短片
	public final static int SYS_DEV_DELETE_PHOTO = 5036; // 删除图片
	public final static int SYS_DEV_DELETE_PHOTOS = 5037; // 删除相册
	public final static int SYS_CHECK_PWD_STRENGTH = 5039;// 检测密码合法性和强度
	public final static int SYS_SEND_EMAIL_CODE = 5041; //发送邮箱验证码
	public final static int SYS_REGISTE_BY_EMAIL = 5042; //邮箱注册
	public final static int SYS_SEND_EMAIL_FOR_CODE = 5043; //发送邮箱验证码（修改密码、重置密码）
	public final static int SYS_CHECK_CODE_FOR_EMAIL = 5044;//验证邮箱验证码（修改密码、重置密码）
	public final static int SYS_PSW_CHANGE_BY_EMAIL = 5045; //通过邮箱修改密码（重置密码）

	public final static int SYS_CHECK_USER_REGISTE = 5046;// 检查用户是否已被注册
	public final static int SYS_LOGOUT_TO_XM = 5047; // 同步退出
	public final static int SYS_NO_VALIDATED_REGISTER = 5048; // 无需验证注册
	public final static int SYS_GET_USER_INFO = 5049; // 获取用户信息
	public final static int SYS_SEND_BINDING_PHONE_CODE = 5050; // 绑定安全手机(1)—发送验证码
	public final static int SYS_BINDING_PHONE = 5051; // 绑定安全手机(2)—验证code并绑定
	public final static int SYS_CLOUDUPGRADE_CHECK = 5052; // 设备是否需要升级查询
	public final static int SYS_CLOUDUPGRADE_DOWNLOAD = 5053; // 设备云升级下载
	public final static int SYS_SEND_BINDING_EMAIL_CODE = 5054; // 绑定安全手机(1)—发送验证码
	public final static int SYS_BINDING_EMAIL = 5055; // 绑定安全手机(2)—验证code并绑定
	// EMSG_SYS_BINDING_EMAIL, // 绑定安全邮箱(2)—验证code并绑定
	// EMSG_SYS_REGISER_USER_XM_EXTEND, // 用户注册(Extend)
	// EMSG_SYS_REGISTE_BY_EMAIL_EXTEND, // 邮箱注册(Extend)
	// EMSG_SYS_NO_VALIDATED_REGISTER_EXTEND, // 无需验证注册(Extend)
	public final static int STOP_CLOUDUPGRADE_DOWNLOAD = 5059; // 停止下载
	
	public final static int SYS_ADD_DEV_BY_FILE = 5060;			//5060
	public final static int SYS_GET_DEV_INFO_BY_USER_INSIDE = 5061;  //内部获取设备列表，用于android报警推送
	public final static int SYS_GET_DEVLOG = 5062;    				// 获取设备端日志，并上传到服务器
	public final static int SYS_GET_DEVLOG_END = 5063;				// 获取设备端日志，并上传到服务器
	public final static int SYS_WX_ALARM_LISTEN_OPEN = 5064;     	// 开启微信报警监听
	public final static int SYS_WX_ALARM_LISTEN_CLOSE = 5065;    	// 关闭微信报警监听
	public final static int SYS_WX_ALARM_WXPMSCHECK = 5066;    	    // 关闭微信报警监听状态查询
	public final static int SYS_CHECK_CS_STATUS     = 5067;    	    // 实时从服务器上查询云存储状态
	public final static int SYS_DULIST     = 5068;					// 获取设备所在的帐户信息
	public final static int SYS_MDSETMA    = 5069;					// 指定设备的主帐户
	public final static int SYS_MODIFY_USERNAME = 5070;             // 修改登录用户名（只能修改微信等绑定帐户自动生成）
	public final static int SYS_ON_DEV_STATE = 5071;				// 设备状态变化通知
	public final static int SYS_IS_MASTERMA = 5072;					// 获取当前账号是否为该设备的主账号
	public final static int SYS_GET_ABILITY_SET = 5073;				// 从服务器端获取设备的能力集
	public final static int SYS_CHECK_DEV_VALIDITY = 5074;			// 从服务端验证设备的合法性
	public final static int SYS_CANCELLATION_USER_XM = 5075;		// 注销用户账号
	public final static int SYS_GET_LOGIN_ACCOUNT_CODE = 5076;		// 获取登陆账户验证码
	public final static int SYS_GET_DEV_INFO_BY_SMS = 5077;		    // 短信验证获取设备列表
	public final static int SYS_USER_WX_UNBIND = 5078;				// 用户解除微信绑定
	public final static int SYS_THIRD_PARTY_BINDING_ACCOUNT = 5079; // 第三方绑定用户
	public final static int SYS_FACE_CHECK_OCX = 5080; // 云平台app证书合法性校验
	public final static int SYS_GET_PHONE_SUPPORT_AREA_CODE = 5081; // 获取支持手机验证的全球区号
	public final static int SYS_SEND_GLOBAL_PHONE_CODE = 5082; // 全球国家区域手机短信验证
	public final static int SYS_THIRD_PARTY_ALARM_LISTEN_OPEN = 5083;         // 开启第三方报警监听
	public final static int SYS_THIRD_PARTY_ALARM_LISTEN_CLOSE = 5084;        // 关闭第三方报警监听
	public final static int SYS_THIRD_PARTY_ALARM_STATE_CHECK = 5085;          // 第三方报警状态查询
	public final static int SYS_VMS_CLOUD_GET_DEV_LIST = 5086; // 轻量化监控平台设备列表获取
	public final static int SYS_GET_CFGS_FROM_SHADOW_SERVER = 5087; // 通过影子服务器批量获取配置
    public final static int SYS_SHADOW_SERVER_CFGS_CHANGE_NOTIFY = 5088; // 影子服务设备配置状态变化通知
    public final static int SYS_GET_DEV_CAPABILITY_SET = 5089; // 获取设备能力集  用来替代EMSG_SYS_GET_ABILITY_SET

	/// TCS服务相关
	public final static int SYS_UPDATE_DEV_LOGIN_TOKEN_TO_TCS = 5090; ///< 更新设备登录令牌到TCS(令牌管理中心服务)
	public final static int SYS_GET_DEV_LOGIN_TOKEN_FROM_TCS = 5091; ///< 从TCS(令牌管理中心服务)获取设备登录令牌

	public final static int SYS_BATCH_GET_DEV_CAPABILITY_SET = 5092; /** 批量获取设备能力集 */
	public final static int SYS_GET_CURRENT_USER_DEV_LIST = 5093;//获取当前用户设备列表
	public final static int SYS_GET_DEV_ENC_TOKEN_FORM_RS = 5094; ///< 获取设备登录令牌信息

	public final static int APP_ON_SEND_LOG_FILE = 5098; // 日志信息回调
	public final static int APP_ON_MSG_LOG = 1; // 日志信息回调

	public final static int DEV_GET_CHN_NAME = 5100;
	public final static int DEV_FIND_FILE = 5101;
	public final static int DEV_FIND_FILE_BY_TIME = 5102;
	public final static int DEV_ON_DISCONNECT = 5103;
	public final static int DEV_ON_RECONNECT = 5104;
	public final static int DEV_PTZ_CONTROL = 5105;
	public final static int DEV_AP_CONFIG = 5106;
	public final static int DEV_GET_CONFIG = 5107;
	public final static int DEV_SET_CONFIG = 5108;
	public final static int DEV_GET_ATTR = 5109;
	public final static int DEV_SET_ATTR = 5110;
	public final static int DEV_START_TALK = 5111;
	public final static int DEV_SEND_MEDIA_DATA = 5112;
	public final static int DEV_STOP_TALK = 5113;
	public final static int ON_DEV_DISCONNECT = 5114;
	public final static int ON_REC_IMAGE_SYN = 5115; // 录像索引图片同步 param1 ==
														// 0：同步进度 总图片\已经同步图片
	// param1 == 1：param2 == 0 同步的数目
	public final static int ON_FILE_DOWNLOAD = 5116;
	public final static int ON_FILE_DLD_COMPLETE = 5117;
	public final static int ON_FILE_DLD_POS = 5118;
	public final static int DEV_START_UPGRADE = 5119; // param0表示表示结果
	public final static int DEV_ON_UPGRADE_PROGRESS = 5120; // param0==EUPGRADE_STEP
	// param1==2表示下载或升级进度或升级结果;
	// 进度0~100; 结果0成功 <0失败 200:已经是最新的程序
	public final static int DEV_STOP_UPGRADE = 5121;
	public final static int DEV_OPTION = 5122;
	public final static int DEV_START_SYN_IMAGE = 5123;
	public final static int DEV_STOP_SYN_IMAGE = 5124;
	public final static int DEV_CHECK_UPGRADE = 5125; // 检查设备升级状态,parma1<0:失败;==0:当前已经是最新程序;1:服务器上有最新的升级程序;2:支持云升级;
	public final static int DEV_SEARCH_DEVICES = 5126; // 查询设备
														// param1<0:失败;>=0返回查询到设备的个数(SDK_CONFIG_NET_COMMON_V2数组)
	public final static int DEV_SET_WIFI_CFG = 5127; // 直连模式下WIFI配置
	public final static int DEV_GET_JSON = 5128;
	public final static int DEV_SET_JSON = 5129;
	public final static int DEV_ON_TRANSPORT_COM_DATA = 5130;
	public final static int DEV_CMD_EN = 5131;
	public final static int DEV_GET_LAN_ALARM = 5132; // 局域网警报
	public final static int DEV_SEARCH_PIC = 5133;
	public final static int DEV_SEARCH_PIC_STOP = 5134;
	public final static int EMSG_DEV_START_UPLOAD_DATA = 5135;
	public final static int EMSG_DEV_STOP_UPLOAD_DATA = 5136;
	public final static int EMSG_DEV_ON_UPLOAD_DATA = 5137;
	public final static int ON_CLOSE_BY_LIB = 5138;
	public final static int DEV_LOGIN = 5139;
	public final static int DEV_BACKUP = 5140;
	public final static int DEV_SLEEP = 5141;
	public final static int DEV_WAKEUP = 5142;
	public final static int DEV_SET_NET_IP_BY_UDP = 5143;
	public final static int DEV_PREDATOR_FILES_OPERATION = 5144; //捕食器文件传输操作
	public final static int DEV_PREDATOR_SEND_FILE = 5145;
	public final static int DEV_PREDATOR_FILE_SAVE = 5146; //捕食器文件保存到本地
	public final static int DEV_START_PUSH_PICTURE = 5147; //开始推图
	public final static int DEV_STOP_PUSH_PICTURE = 5148;
	public final static int DEV_CONFIG_JSON_NOT_LOGIN = 5150; //设备配置获取，设置(Json格式，不需要登陆设备)
	public final static int DEV_GET_CONNECT_TYPE = 5151; // 获取设备网络状态
	public final static int DEV_START_FILE_TRANSFER = 5152; // 开启文件传输
	public final static int DEV_FILE_DATA_TRANSFER = 5153; // 传输文件数据
	public final static int DEV_STOP_FILE_TRANSFER = 5154; // 关闭文件传输
	
	public final static int GROUP_SEARCH_DEVICE_INFO = 5155; // 分区局域网广播搜索设备信息
	public final static int GROUP_SET_DEV_INFO = 5156; // 分区局域网广播设置设备信息
	public final static int GROUP_SEND_DATA_RADIO_OPERATION = 5157; //  发送实时音频广播数据
	public final static int DEV_START_FILE_DATA_RECV = 5160; // 开启文件接收
	public final static int DEV_FILE_DATA_RECV = 5161; // 文件接收回传
	
	public final static int START_UPGRADE_IPC = 5163;       // IPC开始升级
	public final static int ON_UPGRADE_IPC_PROGRESS = 5164; // IPC升级信息回调
	public final static int STOP_UPGRADE_IPC = 5165; // IPC升级停止
	public final static int DEV_ERROR_CODE_MONITOR = 5166; ///< 设备错误码监听回调
	
	public final static int SET_PLAY_SPEED = 5500;
	public final static int START_PLAY = 5501;
	public final static int STOP_PLAY = 5502;
	public final static int PAUSE_PLAY = 5503;
	public final static int MEDIA_PLAY_DESTORY = 5504; // 媒体播放退出,通知播放对象
	public final static int START_SAVE_MEDIA_FILE = 5505; // 保存录像,保存格式用后缀区分 =
															// .dav私有;.avi:AVI格式;.mp4:MP4格式
	public final static int STOP_SAVE_MEDIA_FILE = 5506; // 停止录像
	public final static int SAVE_IMAGE_FILE = 5507; // 抓图 =
													// 5000;保存格式用后缀区分,.bmp或.jpg
	public final static int ON_PLAY_INFO = 5508; // 回调播放信息
	public final static int ON_PLAY_END = 5509; // 录像播放结束
	public final static int SEEK_TO_POS = 5510;
	public final static int SEEK_TO_TIME = 5511;
	public final static int SET_SOUND = 5512; // 打开，关闭声音
	public final static int ON_MEDIA_NET_DISCONNECT = 5513; // 媒体通道网络异常断开
	public final static int ON_MEDIA_REPLAY = 5514; // 媒体重新播放
	public final static int START_PLAY_BYTIME = 5515;
	public final static int ON_PLAY_BUFFER_BEGIN = 5516; // 正在缓存数据
	public final static int ON_PLAY_BUFFER_END = 5517; // 缓存结束,开始播放
	public final static int ON_PLAY_ERROR = 5518; // 回调播放异常,长时间没有
	public final static int ON_SET_PLAY_SPEED = 5519; // 播放速度
	public final static int REFRESH_PLAY = 5520;
	public final static int MEDIA_BUFFER_CHECK = 5521; // 缓存检查
	public final static int MEDIA_SET_PLAY_SIZE = 5522; // 设置高清/标清
	public final static int MEDIA_FRAME_LOSS = 5523; // 视频帧不足提示（可能是网络原因）
	public final static int ON_YUV_DATA = 5524; // YUV数据回调
	public final static int EMSG_MEDIA_SETPLAYVIEW = 5525; // 改变显示View
	public final static int ON_FRAME_USR_DATA = 5526;	// 用户自定义信息帧回调
	public final static int ON_Media_Thumbnail = 5527;    // 抓取视频缩略图
	public final static int ON_MediaData_Save = 5528;	    // 媒体数据开始保存
	public final static int MediaData_Save_Process = 5529;  // 媒体数据已保存大小(kB)
	public final static int EMSG_Stop_DownLoad = 5530;		// 停止录像下载
	public final static int Stop_DownLoad = 5530;			// 停止录像下载Andoir都是没有EMSG_的。统一下
	public final static int SET_INTELL_PLAY = 5532;   		// 智能播放
	public final static int ON_MEDIA_DATA = 5533;   		// 解码前的媒体数据回调
	public final static int DOWN_RECODE_BPIC_START =  5534;    //录像缩略图下载开始
	public final static int DOWN_RECODE_BPIC_FILE  =  5535;    //录像缩略图下载--文件下载结果返回
	public final static int DOWN_RECODE_BPIC_COMPLETE =  5536; //录像缩略图下载-下载完成（结束）
	public final static int DEV_RETURN_REAL_STREAM_START = 5537; // 媒体数据开始实时返回
	public final static int MEDIA_UPDATE_UIVIEW_SIZE = 5538; // 更新显示窗口宽高
	public final static int MEDIA_CLOUD_PLAY_REAL_TOTAL_TIMES = 5539; // 云视频播放回传实际视频播放总时长
	public final static int GET_XTSC_CONNECT_QOS = 5540; 	// XTS/C实时链接传输效率
	public final static int ON_AUDIO_FRAME_DATA = 5541; // 获取码流音频数据
	public final static int GET_ALL_DECODER_FRAME_BITS_PS = 5542; // 统计所有decoder对象累加的码流平均值,即当前播放多通道的网速 单位：byte(字节)
	public final static int START_REAL_PLAY_SUPER_RESOLUTION = 5543; // 设置媒体分辨率重采样
	public final static int STOP_REAL_PLAY_SUPER_RESOLUTION = 5544; // 关闭媒体分辨率重采样
	public final static int EMSG_ON_PLAY_DATA_ERROR = 5545; // 播放异常:长时间（3s）没有I帧数据返回(包含数据0x00异常情况)
	public final static int SR_ON_PLAY_FRAMERATE_STATISTICS = 5546; // 帧率统计（超分, 第一次返回的是当前设备帧率，然后每4s更新统计一次）
	public final static int EMSG_ON_TALK_PCM_DATA = 5547; // 播放前对讲PCM数据回调(设备对讲过来的数据)
	public final static int ON_MULTI_VIEW_FRAME_DATA_CB = 5548;  // 用户自定义多目信息帧回调
	public final static int ON_DEV_LOGIN_TOKEN_ERROR_NOTIFY = 5549; // 设备登陆TOKEN(137)错误通知
	public final static int ON_GWM_MEDIA_DISCONNECT = 5550;  // GWM媒体通道异常断开
	
	// 报警推送
	public final static int MC_LinkDev = 6000;
	public final static int MC_UnlinkDev = 6001;
	public final static int MC_SendControlData = 6002;
	public final static int MC_SearchAlarmInfo = 6003;
	public final static int MC_SearchAlarmPic = 6004;
	public final static int MC_ON_LinkDisCb = 6005; // 设备断线
	public final static int MC_ON_ControlCb = 6006;
	public final static int MC_ON_AlarmCb = 6007; // 报警推送
	public final static int MC_ON_PictureCb = 6008; // 报警图片
	public final static int MC_ON_INIT = 6011; // 初始化
	public final static int MC_DeleteAlarm = 6012; 
	public final static int MC_GetAlarmRecordUrl = 6013; // 获取报警录像URL
	public final static int MC_SearchAlarmByMoth = 6014; // 获取报警的日历
	public final static int MC_OnRecvAlarmJsonData = 6015;  // 第三方服务器报警数据处理回调
	public final static int MC_StopDownloadAlarmImages = 6016;  //取消队列中图片下载
	public final static int MC_SearchAlarmLastTimeByType = 6017;  //按类型查询最后一条报警时间
	public final static int MC_AlarmJsonCfgOperation = 6018;  //通用报警相关配置操作
	public final static int MC_LinkDevs_Batch = 6019;  //批量报警订阅 
	public final static int MC_UnLinkDevs_Batch = 6020;  //批量取消报警订阅
	public final static int MC_GET_DEV_ALARM_SUB_STATUS_BY_TYPE = 6021; // 通过订阅类型从服务器端获取设备报警订阅状态
	public final static int MC_GET_DEV_ALARM_SUB_STATUS_BY_TOKEN = 6022; // 通过TOKEN从服务器端获取设备报警订阅状态
	public final static int MC_WhetherTheBatchQueryGeneratesAnAlarm = 6023; /**< 批量查询是否产生报警 */
	public final static int MC_QUERY_DEVS_STATUS_HISTORY_RECORD = 6024; /**< 查询设备(批量)状态历史记录 */
	public final static int LINK_BY_USERID = 6025; /**< 通过UserID进行报警订阅 */
	public final static int UNLINK_BY_USERID  = 6026; /**< 通过UserID进行报警取消订阅 */

	// 云存储目录操作
	public final static int CD_MediaList = 6207;
	public final static int CD_MediaTimeSect = 6200;
	public final static int CD_Media_Dates = 6201;
	public final static int MC_SearchMediaByMoth = 6202;
	public final static int MC_SearchMediaByTime = 6203;
	public final static int MC_DownloadMediaThumbnail = 6204;
	public final static int MC_SearchMediaTimeAxis = 6205;
	public final static int MC_CloudMediaSearchCssHls = 6206;


	// 视频广场
	public final static int XD_LinkMedia = 7001;
	public final static int XD_UnlinkMedia = 7002;
	public final static int XD_PublicHistoryList = 7003;
	public final static int XD_PublicCurrentList = 7004;
	public final static int XD_PublicDevInfo = 7005;
	public final static int XD_FetchPicture = 7006;

	public final static int CSS_API_CMD = 6600;
	public final static int KSS_API_UP_LOAD_VIDEO = 6601;// 上传视频至金山云
	public final static int KSS_API_CMD_GET = 6603;
	public final static int KSS_API_UP_LOAD_PHOTO = 6604;// 上传图片至金山云
	public final static int SYS_EDIT_USER_PHOTOS = 8500; // 修改相册

	// APP检查更新
	public final static int FIR_IM_CHECK_LATEST = 6800;

	public final static int JPEG_TO_MP4_ON_PROGRESS = 8000;
	public final static int JPEG_TO_MP4_ADD_FILE = 8001;
	public final static int JPEG_TO_MP4_CLOSE = 8002;
	public final static int JPEG_TO_MP4_CANCEL = 8003;
	
	public final static int SYS_BINDING_ACCOUNT = 8504; //绑定帐户
	public final static int OBJ_STATE_UPDATE = 5; 			// 状态信息回调显示
	
	// 其它自定义消息
    // 广告更新等消息返回
	public final static int CM_ON_VALUE_CHANGE = 8600;
	public final static int CM_ON_GET_SYS_MSG = 8603;
	public final static int CM_ON_GET_SYS_MSG_LIST = 8604;

	public final static int AP_ON_RECEIVE_SAMPLES = 8700;

}
```

### 2. EFUN_ATTR

```java
package com.lib;

public class EFUN_ATTR {
	public static final int APP_PATH = 1;
	public static final int DOC_PATH = 2;
	public static final int CONFIG_PATH = 2;
	public static final int UPDATE_FILE_PATH = 3; // 升级文件存储目录
	public static final int SAVE_LOGIN_USER_INFO = 4; // 升级文件存储目录
	public static final int AUTO_DL_UPGRADE = 5; // 是否自动下载升级文件0:NO 1:WIFI下载 2:网络通就下载
	public static final int FUN_MSG_HANDLE = 6; // 接收FunSDK返回的设备断开等消息
	public static final int SET_NET_TYPE = 7; // ENET_MOBILE_TYPE(0:未知 1:WIFI 2:移动网络 4:物理网卡线)
	public static final int GET_IP_FROM_SN = 8; // 通过序列号获取局域网IP
	public static final int TEMP_FILES_PATH = 9;      // 临时文件目录
	public static final int USER_PWD_DB = 10;		  // 保存密码的文件设置
	public static final int LOGIN_ENC_TYPE = 11;      // 指定登录加密类型，默认为0:MD5&RSA 1:RSA(需要设备支持)
	public static final int LOGIN_USER_ID = 12;       // Login  user id
	public static final int CLEAR_SDK_CACHE_DATA = 13;// Clear sdk cache data
	public static final int DSS_STREAM_ENC_SYN_DEV = 14;// DSS码流校验规则是否同步设备方式设置（0:通用方式校验，1:跟设备登录密码相同方式校验）
	public static final int CDATACENTER_LANGUAGE = 15;// 设置语言类型,FunSDK初始化里面的语言设置，app后续会再次改的语言类型
	public static final int LOGIN_SUP_RSA_ENC = 16; // 登录加密是否支持RSA，0:不支持RSA  1:支持RSA(默认)（ 为了临时解决设备端对登录RSA加密校验方式和sdk这边的方式不同）
	public static final int JUDEGE_RPSVIDEO_ABILITY = 17;  // 是否只进行rps在线状态判断，不进行能力级判断（"OtherFunction/SupportRPSVideo"）0:只判断在线状态，不判断能力级， 1:同时判断
	public static final int LOGIN_AES_ENC_RESULT = 18; // 0：AES获取加密信息，未返回明确结果，则使用明文登录 1：未返回明确结果，则返回错误，不会去登录
	public static final int FACE_CHECK_OCX = 19; // 判断当前app账户证书合法性 0:非法 1:合法, 默认合法;app调用FUN_XMCloundPlatformInit接口时sdk会主动调用此属性或者上层APP主动调用此属性
	public static final int GET_ALL_DECODER_FRAME_BITS_PS = 20; // 统计所有decoder对象累加的码流平均值,即当前播放多通道的网速 单位：byte(字节)
	public static final int LOGIN_RPS_STATE_ALLOW = 21; // 登录设备之前是否rps状态需要查询得到非E_FUN_NotAllowed结果 0:不需要  1:需要 (只是判断条件之一),调用FUN_XMCloundPlatformInit接口自动设置为1
	public static final int SUP_RPS_VIDEO_DEFAULT = 22; // RPS视频能力级默认值  0： 默认不支持 1：默认支持
	public static final int SET_DSS_REAL_PLAY_FLUENCY = 23; // DSS视频播放流畅度设置  (等级设置值:8~11,其他值无效!),设置之后不再根据I帧间隔动态改变
	public static final int SET_CLOUD_UPGRADE_CHECK_URL = 24; ///< 云端升级检测url自定义设置
	public static final int CLOUD_UPGRADE_DOWNLOAD_URL = 25; ///< 云端升级固件下载url自定义设置
	public static final int SET_RPS_DATA_ENCRYPT_ENABLE = 26; ///< RPS协议数据交互加密开关  0:关闭 1:开启(默认值)
	public static final int SET_HTTP_PROTOCOL_USER_AGENT = 27; ///< http协议用户代码信息设置
	public static final int SET_MULTI_VIEW_DROP_FRAME_NUMBER = 28; ///< 多目信息帧发生改变，阻塞回调给app之后，默认丢弃视频帧个数  *默认值:0
	public static final int GET_USER_ACCOUNT_DATA_INFO = 29; ///< 获取用户账户数据信息
	public static final int SET_SUPPORT_CFG_CLOUD_UPGRADE = 30; ///< 是否默认支持设备云端升级
	public static final int DEV_TOKEN_ERROR_LISTENER = 31; ///< 设备TOKEN错误(137)监听，通过消息ID:5549异步回调
	public static final int QUERY_P2P_STATUS_ENABLE_FORM_SERVER = 32; ///< @deprecated废弃使用EFUN_ATTR == 33代替; 从服务器查询P2P状态使能  TRUE(非0):查询 FALSE(0):不查询 *默认TRUE(查询)
	public static final int QUERY_P2P_STATUS_ENABLE = 33; ///< 查询P2P状态使能 详见SDKCONST.QueryP2PEnable
	public static final int SET_CLOUD_DOWNLOAD_NETPTL = 34;  ///< 设置云服务数据下载网络协议  详见@class SDKCONST.ECloudDownloadNetPtl
	public static final int SUP_HISI_H265_DEC = 35; /** 是否支持hisi的h.265解码 0：不支持(默认值，默认使用ffmpeg) 1：支持 */

	// 对象属性值
	public static final int EOA_DEVICE_ID = 10000;
	public static final int EOA_CHANNEL_ID = 10001;
	public static final int EOA_IP = 10002;
	public static final int EOA_PORT = 10003;
	public static final int EOA_IP_PORT = 10004;
	public static final int EOA_STREAM_TYPE = 10005;
	public static final int EOA_NET_MODE = 10006;
	public static final int EOA_COM_TYPE = 10007;
	public static final int EOA_VIDEO_WIDTH_HEIGHT = 10008; // 获取视频的宽和高信息
	public static final int EOA_VIDEO_FRATE = 10009; // 获取视频帧率信息
	public static final int EOA_VIDEO_BUFFER_SIZE = 10010; // 获取缓冲的帧数
	public static final int EOA_PLAY_INFOR = 10011;
	public static final int EOA_PCM_SET_SOUND = 10012;		  // -100~100
	public static final int EOA_CUR_PLAY_TIME = 10013;       // 获取当前播放的时间,返回uint64单位毫秒
	public static final int EOA_MEDIA_YUV_USER = 10014;				// 设置YUV回调
	public static final int EOA_SET_MEDIA_VIEW_VISUAL = 10015;		// 是否画视频数据
	public static final int EOA_SET_MEDIA_DATA_USER_AND_NO_DEC = 10016; // 解码前数据回调，不播放
	public static final int EOA_SET_MEDIA_DATA_USER = 10017;			// 解码前数据回调，同时播放
	public static final int EOA_DISABLE_DSS_FUN = 10018;				// 禁用DSS功能
	public static final int EOA_DEV_REAL_PLAY_TYPE = 10019;				// 实时媒体连接方式指定
	public static final int EOA_SET_PLAYER_USER = 10020;            // 设置回调消息接收者
	public static final int EOA_GET_ON_FRAME_USER_DATA = 10021;     // 重新回调一次信息帧（ON_FRAME_USER_DATA）,如果没有就没有回调
	public static final int EOA_GET_XTSC_CONNECT_QOS = 10022;    	// 查询链接的传输效率 >0% <=0失败
	public static final int EOA_GET_ON_AUDIO_FRAME_DATA = 10023; // // 获取当前音频帧信息，帧信息有变化实时更新  通道布局/采样格式/采样率
	public static final int EOA_SET_AUDIO_FRAME_SAMPLES_TYPE = 10024; ///< @deprecated废弃，新增接口Fun_DevStartTalk支持采样频率设置
	public static final int EOA_SET_DEV_TALK_DATA_USER = 10025; // 设置对讲音频(设备端对讲过来的音频)数据回调
	
	// 操作ID
	public static final int EDOPT_STORAGEMANAGE = 1; // 磁盘管理
	public static final int EDOPT_DEV_CONTROL = 2; // Deivce Control
	public static final int EDOPT_DEV_GET_IMAGE = 3; // 设备抓图

	public final static int EDA_STATE_CHN = 1;
	public final static int EDA_OPT_ALARM = 2;
	public final static int EDA_OPT_RECORD = 3;
	public final static int EDA_DEV_INFO = 4;
	public final static int EDA_DEV_OPEN_TANSPORT_COM = 5;
	public final static int EDA_DEV_CLOSE_TANSPORT_COM = 6;
	public final static int EDA_DEV_TANSPORT_COM_READ = 7;
	public final static int EDA_DEV_TANSPORT_COM_WRITE = 8;
	public final static int EDA_NET_KEY_CLICK = 9;
}

```

### 3. EFunDevState

```java
public interface EFunDevState
{
	public static final int UNKOWN = 0;           // 未知
	public static final int LINE = 1;          // 在线（如果是门铃，同时说明在唤醒状态）
	public static final int SLEEP = 2;         // 休眠状态
	public static final int SLEEP_UNWEAK = 3;  // 休眠状态不可唤醒
	public static final int OFF_LINE = -1;        // 不在线
	public static final int NO_SUPPORT = -2;      // 不支持
	public static final int NotAllowed = -3;      // 没权限
}
```

### 4. EDevStatusType

```java
public interface EDevStatusType {
	public static final int E_DevStatus_P2P = 0; // P2P要用新的状态服务查下
	public static final int E_DevStatus_TPS_V0 = 1; // 老的那种转发，用于老程序（2016年以前的）的插座，新的插座程序使用的是TPS
	public static final int E_DevStatus_TPS = 2; // 透传服务
	public static final int E_DevStatus_DSS = 3; // 媒体直播服务
	public static final int E_DevStatus_CSS = 4; // 云存储服务
	public static final int E_DevStatus_P2P_V0 = 5; // P2P用老的方式;通过穿透库查询获取到的设备P2P状态
	public static final int E_DevStatus_IP = 6; // IP方式
	public static final int E_DevStatus_RPS = 7; // RPS可靠的转发
	public static final int EDevStatusType_SIZE = 8; // NUM....
}
```

### 5. EDECODE_TYPE

```java
public interface EDECODE_TYPE
{
	public static final int EDECODE_REAL_TIME_STREAM0 = 0;      // 最实时--适用于IP\AP模式等网络状态很好的情况
	public static final int EDECODE_REAL_TIME_STREAM1 = 1;      //
	public static final int EDECODE_REAL_TIME_STREAM2 = 2;      //
	public static final int EDECODE_REAL_TIME_STREAM3 = 3;      // 中等
	public static final int EDECODE_REAL_TIME_STREAM4 = 4;      //
	public static final int EDECODE_REAL_TIME_STREAM5 = 5;      //
	public static final int EDECODE_REAL_TIME_STREAM6 = 6;      // 最流畅--适用于网络不好,网络波动大的情况
	public static final int EDECODE_FILE_STREAM = 100;			// 文件流
}
```

### 6. SDK_CAPTURE_COMP

```java
// 捕获压缩格式类型
public interface SDK_CAPTURE_COMP {
	public static final int SDK_CAPTURE_COMP_DIVX_MPEG4 = 0; // /< DIVX
																// MPEG4。
	public static final int SDK_CAPTURE_COMP_MS_MPEG4 = 1; // /< MS MPEG4。
	public static final int SDK_CAPTURE_COMP_MPEG2 = 2; // /< MPEG2。
	public static final int SDK_CAPTURE_COMP_MPEG1 = 3; // /< MPEG1。
	public static final int SDK_CAPTURE_COMP_H263 = 4; // /< H.263
	public static final int SDK_CAPTURE_COMP_MJPG = 5; // /< MJPG
	public static final int SDK_CAPTURE_COMP_FCC_MPEG4 = 6; // /< FCC MPEG4
	public static final int SDK_CAPTURE_COMP_H264 = 7; // /< H.264
	public static final int SDK_CAPTURE_COMP_H265 = 8; // /< H.265
	public static final int SDK_CAPTURE_COMP_NR = 9; // /< 枚举的压缩标准数目。
};
```

### 7. SDK_CAPTURE_SIZE_t

```java
// 这些结构体和枚举是提供给外部使用，所有可能会和设备那边定义了2次,所以都在前面加了SDK_
public interface SDK_CAPTURE_SIZE_t {
    public static final int JSON_D1 = 0; // /< 720*576(PAL) 720*480(NTSC)
    public static final int JSON_HD1 = 1; // /< 352*576(PAL) 352*480(NTSC)
    public static final int JSON_BCIF = 2; // /< 720*288(PAL) 720*240(NTSC)
    public static final int JSON_CIF = 3; // /< 352*288(PAL) 352*240(NTSC)
    public static final int JSON_QCIF = 4; // /< 176*144(PAL) 176*120(NTSC)
    public static final int JSON_VGA = 5; // /< 640*480(PAL) 640*480(NTSC)
    public static final int JSON_QVGA = 6; // /< 320*240(PAL) 320*240(NTSC)
    public static final int JSON_SVCD = 7; // /< 480*480(PAL) 480*480(NTSC)
    public static final int JSON_QQVGA = 8; // /< 160*128(PAL) 160*128(NTSC)
    public static final int JSON_ND1 = 9; // /< 240*192
    public static final int JSON_650TVL = 10; // /< 926*576
    public static final int JSON_720P = 11; // /< 1280*720
    public static final int JSON_1_3M = 12; // /< 1280*960
    public static final int JSON_UXGA = 13; // /< 1600*1200
    public static final int JSON_1080P = 14; // /< 1920*1080
    public static final int JSON_WUXGA = 15; // /< 1920*1200
    public static final int JSON_2_5M = 16; // /< 1872*1408
    public static final int JSON_3M = 17; // /< 2048*1536
    public static final int JSON_5M = 18; // /< 3744*1408
    public static final int JSON_NR = 19;
    public static final int JSON_1080N = 19;// <960*1080>
    public static final int JSON_4M = 20;// <2592*1520>
    public static final int JSON_6M = 21; // 3072*2048
    public static final int JSON_8M = 22; // 3264 *2448
    public static final int JSON_12M = 23;// 4000*3000
    public static final int JSON_4K = 24; // 4096*2160
    public static final int JSON_EXT_V2_NR = 25;
    public static final int JSON_720N = 25;// 640*720
    public static final int JSON_WSVGA = 26; // 1024*576
    public static final int JSON_NHD = 27; // Wifi IPC 640*360
    public static final int JSON_3M_N = 28; // < 1024*1536
    public static final int JSON_4M_N = 29; // < 1296*1520
    public static final int JSON_5M_N = 30; // < 1872*1408
    public static final int JSON_4K_N = 31; // < 2048 * 2160í¨ó?/1920*2160o￡??
    public static final int JSON_3_6M = 32; // 2560*1440
    public static final int JSON_2_7K = 33; // 2704*1521
    public static final int JSON_EXT_V3_NR = 33;

    ////////////////////// 兼容老的库/////////////////////////////////////////////////////////
    public static final int SDK_CAPTURE_SIZE_D1 = 0; // /< 720*576(PAL)
                                                     // 720*480(NTSC)
    public static final int SDK_CAPTURE_SIZE_HD1 = 1; // /< 352*576(PAL)
                                                      // 352*480(NTSC)
    public static final int SDK_CAPTURE_SIZE_BCIF = 2; // /< 720*288(PAL)
                                                       // 720*240(NTSC)
    public static final int SDK_CAPTURE_SIZE_CIF = 3; // /< 352*288(PAL)
                                                      // 352*240(NTSC)
    public static final int SDK_CAPTURE_SIZE_QCIF = 4; // /< 176*144(PAL)
                                                       // 176*120(NTSC)
    public static final int SDK_CAPTURE_SIZE_VGA = 5; // /< 640*480(PAL)
                                                      // 640*480(NTSC)
    public static final int SDK_CAPTURE_SIZE_QVGA = 6; // /< 320*240(PAL)
                                                       // 320*240(NTSC)
    public static final int SDK_CAPTURE_SIZE_SVCD = 7; // /< 480*480(PAL)
                                                       // 480*480(NTSC)
    public static final int SDK_CAPTURE_SIZE_QQVGA = 8; // /< 160*128(PAL)
                                                        // 160*128(NTSC)
    public static final int SDK_CAPTURE_SIZE_ND1 = 9; // /< 240*192
    public static final int SDK_CAPTURE_SIZE_650TVL = 10; // /< 926*576
    public static final int SDK_CAPTURE_SIZE_720P = 11; // /< 1280*720
    public static final int SDK_CAPTURE_SIZE_1_3M = 12; // /< 1280*960
    public static final int SDK_CAPTURE_SIZE_UXGA = 13; // /< 1600*1200
    public static final int SDK_CAPTURE_SIZE_1080P = 14; // /< 1920*1080
    public static final int SDK_CAPTURE_SIZE_WUXGA = 15; // /< 1920*1200
    public static final int SDK_CAPTURE_SIZE_2_5M = 16; // /< 1872*1408
    public static final int SDK_CAPTURE_SIZE_3M = 17; // /< 2048*1536
    public static final int SDK_CAPTURE_SIZE_5M = 18;
    public static final int SDK_CAPTURE_SIZE_EXT_NR = 19;
    public static final int SDK_CAPTURE_SIZE_1080N = 19; // /< 3744*1408
    public static final int SDK_CAPTURE_SIZE_4M = 20; /// < 2592*1520
    public static final int SDK_CAPTURE_SIZE_6M = 21; /// < 3072×2048
    public static final int SDK_CAPTURE_SIZE_8M = 22; /// < 3264×2448
    public static final int SDK_CAPTURE_SIZE_12M = 23; /// < 4000*3000
    public static final int SDK_CAPTURE_SIZE_4K = 24; /// < 4096 * 2160通用/3840*2160海思
    public static final int SDK_CAPTURE_SIZE_EXT_V2_NR = 25;
    public static final int SDK_CAPTURE_SIZE_720N = 25; // 640*720
    public static final int SDK_CAPTURE_SIZE_WSVGA = 26; /// < 1024*576
    public static final int SDK_CAPTURE_SIZE_NHD = 27; // Wifi IPC 640*360
    public static final int SDK_CAPTURE_SIZE_3M_N = 28; // 1024 * 1536
    public static final int SDK_CAPTURE_SIZE_4M_N = 29; // 1280 * 1440
    public static final int SDK_CAPTURE_SIZE_5M_N = 30; // 1872 * 1408
    public static final int SDK_CAPTURE_SIZE_4K_N = 31; // 2048 * 2160
    public static final int SDK_CAPTURE_SIZE_3_6M = 32;// 2560*1440
    public static final int SDK_CAPTURE_SIZE_2_7K = 33; // 2704*1521
    public static final int SDK_CAPTURE_SIZE_EXT_V3_NR = JSON_EXT_V3_NR;
    ////////////////////// 结束/////////////////////////////////////////////////////////////
};
```

### 8. DEVICE_TYPE

```java
//设备类型定义规则:
//产品类型目前是一个int，32位
//4位:版本号----------------------------------默认为0
//// 版本号1解析规则
//4位:产品大类：未知/IPC/NVR/DVR/HVR------------默认为0
//4位:镜头类型：未知/鱼眼/180/普通------------默认为0
//4位:厂家分类：未知/XM/JF/定制---------------默认为0
//16位：产品序列：（最多65535）
//示例：
//DEV_CZ_IDR 定制门铃是 0x11130001 ----> 二进制 0001 0001 0001 0011 0000000000000001 ---> 1->版本号, 1->IPC, 1->鱼眼, 3->定制, 1->产品序列
//EE_DEV_WBS 无线基站是 0x12310001 ----> 二进制 0001 0010 0011 0001 0000000000000001 ---> 1->版本号, 2->NVR, 3->普通镜头, 1->XM, 1->产品序列 
public interface DEVICE_TYPE {
    public static final int MONITOR = 0;// 监控设备
    public static final int SOCKET = 1;// 插座设备
    public static final int BULB = 2;// 情景灯泡
    public static final int BULB_SOCKET = 3;// 灯座
    public static final int CAR = 4;// 汽车伴侣
    public static final int BEYE = 5;// 大眼睛
    public static final int SEYE = 6;// 小眼睛/小雨点
    public static final int NSEYE = 601;// 直播小雨点
    public static final int ROBOT = 7;// 雄迈摇头机
    public static final int MOV = 8;// 运动摄像机
    public static final int FEYE = 9;// 鱼眼小雨点
    public static final int FBULB = 10;// 鱼眼灯泡/全景智能灯泡
    public static final int BOB = 11;// 小黄人
    public static final int MUSIC_BOX = 12;// wifi音乐盒
    public static final int SPEAKER = 13;// wifi音响
    public static final int LINKCENTERT = 14; // 智联中心
    public static final int DASH_CAMERA = 15; // 勇士行车记录仪
    public static final int POWERSTRIP = 16; // 智能排插
    public static final int FISH_FUN = 17; // 鱼眼模组
    public static final int DRIVE_BEYE = 18;// 大眼睛行车记录仪
    public static final int UFO = 20;// 飞碟设备
    public static final int IDR = 21; // 智能门铃--xmjp_idr_xxxx
    public static final int BULLET = 22; // ED型枪机
    public static final int DRUM = 23; // 架子鼓
    public static final int CAMERA = 24; // 摄像机
    public static final int PEEPHOLE = 26;// 猫眼
    public static final int DEV_CZ_IDR = 0x11130001; // 定制门铃1--dev_cz_idr_xxxx
    public static final int EE_DEV_LOW_POWER = 0x11030002;// 低功耗无线消费类产品
    public static final int EE_DEV_DOORLOCK = 0x11110027;// 门锁设备--xmjp_stl_xxxx
    public static final int EE_DEV_BULLET_EG = 0x11310028;// EG型枪机
    public static final int EE_DEV_BULLET_EC = 0x11310029;// EC型枪机
    public static final int EE_DEV_BULLET_EB = 0x11310030;// EB型枪机
    public static final int EE_DEV_DOORLOCK_V2 = 0x11110031;// 门锁设备支持音频和对讲--xmjp_stl_xxxx
    public static final int EE_DEV_SMALL_V = 0x11110032; // 小V设备--camera_xxxx
    public static final int EE_DEV_DOORLOCK_PEEPHOLE = 0x11110033;// 门锁猫眼
    public static final int EE_DEV_XIAODING = 0x11110034;// 小丁设备
    public static final int EE_DEV_SMALL_V_2 = 0x11110035;// 小V200万（XM530）设备
    public static final int EE_DEV_BULLET_ESC_WB3F = 0x11110036;// Elsys WB3F
    public static final int EE_NO_NETWORK_BULLET = 0x11310037;// 没有外网的枪机设备
    public static final int EE_DEV_ESC_WY3 = 0x11110038;// Elsys 的设备
    public static final int EE_DEV_ESC_WR3F = 0x11110039;// ESC-WR3F
    public static final int EE_DEV_ESC_WR4F = 0x11110040;// ESC-WR4F Elsys的设备
    public static final int EE_DEV_K_FEED = 0x11310041;// 小K宠物喂食器
    public static final int EE_DEV_B_FEED = 0x11310042;// 小兔看护宠物喂食器
    public static final int EE_DEV_C_FEED = 0x11310043;// 小C宠物保鲜喂食器
    public static final int EE_DEV_F_FEED = 0x11310044;// 小方宠物喂水器
    public static final int EE_DEV_CAT = 0x11310045;// 小方宠物喂水器
    /// 此后严格遵守设备类型命名规则
    public static final int EE_DEV_WBS = 0x12310001; // 无线基站
    public static final int EE_DEV_WNVR = 0x12310002; // 无线NVR
    public static final int EE_DEV_WBS_IOT = 0x12310003; // 无线基站支持IOT
}
```

### 9. EDEV_OPTERATE

```java
package com.lib;

public class EDEV_OPTERATE {
	public final static int EDOPT_STORAGEMANAGE = 1; // 磁盘管理
	public final static int EDOPT_DEV_CONTROL = 2; // Deivce Controlparam1
													// :type==0 重启设备，1 清除日志 2 关机
													// 3.恢复记录日志 4.停止记录日志
													// 5.手机对讲恢复关闭之前临时打开过的音频
	public final static int EDOPT_DEV_GET_IMAGE = 3; // 设备抓图

	// 透明串口
	public final static int EDOPT_DEV_OPEN_TANSPORT_COM = 5;
	public final static int EDOPT_DEV_CLOSE_TANSPORT_COM = 6;
	public final static int EDOPT_DEV_TANSPORT_COM_READ = 7;
	public final static int EDOPT_DEV_TANSPORT_COM_WRITE = 8;

	// 备份录像到u盘
	public final static int EDOPT_DEV_BACKUP = 9;
	public final static int EDOPT_NET_KEY_CLICK = 10;
};
```

### 10. EDEV_STREM_TYPE

```java
package com.lib;

public class EDEV_STREM_TYPE {
	public final static int EDEV_STREM_TYPE_FD = 0;	//1、	流畅（等级0）：         分辨率＜40W像素
	public final static int EDEV_STREM_TYPE_SD = 1;	//2、	标清（等级1）：   40W≤分辨率＜100W像素
	public final static int EDEV_STREM_TYPE_HD = 2;	//3、	高清（等级2）   100W≤分辨率＜200W像素
	public final static int EDEV_STREM_TYPE_FHD = 3;//4、	全高清（等级3） 200W≤分辨率＜400W
	public final static int EDEV_STREM_TYPE_SUD = 4;//5、	超高清（等级4） 400W≤分辨率＜？？？
}

```

### 11. EPTZCMD

```java
public class EPTZCMD {
    public static final int TILT_UP = 0; // 上
    public static final int TILT_DOWN = 1; // 下
    public static final int PAN_LEFT = 2; // 左
    public static final int PAN_RIGHT = 3; // 右
    public static final int PAN_LEFTTOP = 4; // 左上
    public static final int PAN_LEFTDOWN = 5; // 左下
    public static final int PAN_RIGTHTOP = 6; // 右上
    public static final int PAN_RIGTHDOWN = 7; // 右下
    public static final int ZOOM_OUT = 8; // 变倍小
    public static final int ZOOM_IN = 9; // 变倍大
    public static final int FOCUS_FAR = 10; // 焦点后调
    public static final int FOCUS_NEAR = 11; // 焦点前调
    public static final int IRIS_OPEN = 12; // 光圈扩大
    public static final int IRIS_CLOSE = 13; // 光圈缩小13
    public static final int EXTPTZ_OPERATION_ALARM = 14; /// < 报警功能
    public static final int EXTPTZ_LAMP_ON = 15; /// < 灯光开
    public static final int EXTPTZ_LAMP_OFF = 16; // 灯光关
    public static final int EXTPTZ_POINT_SET_CONTROL = 17; // 设置预置点
    public static final int EXTPTZ_POINT_DEL_CONTROL = 18; // 清除预置点
    public static final int EXTPTZ_POINT_MOVE_CONTROL = 19; // 转预置点
    public static final int EXTPTZ_STARTPANCRUISE = 20; // 开始水平旋转
    public static final int EXTPTZ_STOPPANCRUISE = 21; // 停止水平旋转
    public static final int EXTPTZ_SETLEFTBORDER = 22; // 设置左边界
    public static final int EXTPTZ_SETRIGHTBORDER = 23; // 设置右边界
    public static final int EXTPTZ_STARTLINESCAN = 24; // 自动扫描开始
    public static final int EXTPTZ_CLOSELINESCAN = 25; // 自动扫描开停止
    public static final int EXTPTZ_ADDTOLOOP = 26; // 加入预置点到巡航 p1巡航线路 p2预置点值
    public static final int EXTPTZ_DELFROMLOOP = 27; // 删除巡航中预置点 p1巡航线路 p2预置点值
    public static final int EXTPTZ_POINT_LOOP_CONTROL = 28; // 开始巡航
    public static final int EXTPTZ_POINT_STOP_LOOP_CONTROL = 29; // 停止巡航
    public static final int EXTPTZ_CLOSELOOP = 30; // 清除巡航 p1巡航线路
    public static final int EXTPTZ_FASTGOTO = 31; // 快速定位
    public static final int EXTPTZ_AUXIOPEN = 32; // 辅助开关，关闭在子命令中
    public static final int EXTPTZ_OPERATION_MENU = 33; // 球机菜单操作，其中包括开，关，确定等等
    public static final int EXTPTZ_REVERSECOMM = 34; // 镜头翻转
    public static final int EXTPTZ_OPERATION_RESET = 35; /// < 云台复位
    public static final int EXTPTZ_TOTAL = 36;
};
```

### 12. ECONFIG

```java
package com.lib;

public class ECONFIG {
	public static int INDEX = 0;
	public final static int NOTHING = INDEX++; //
	public final static int USER = INDEX++; // 用户信息，包含了权限列表，用户列表和组列表
											// 对应结构体USER_MANAGE_INFO
	public final static int ADD_USER = INDEX++; // 增加用户 对应结构体USER_INFO
	public final static int MODIFY_USER = INDEX++; // 修改用户 对应结构体CONF_MODIFYUSER
	public final static int DELETE_USER = INDEX++; // 对应结构体USER_INFO
	public final static int ADD_GROUP = INDEX++; // 增加组 对应结构体USER_GROUP_INFO
	public final static int MODIFY_GROUP = INDEX++; // 修改组 对应结构体CONF_MODIFYGROUP
	public final static int DELETE_GROUP = INDEX++; // 对应结构体USER_GROUP_INFO
	public final static int MODIFY_PSW = INDEX++; // 修改密码 对应结构体_CONF_MODIFY_PSW
	public final static int ABILITY_SYSFUNC = INDEX++;// 支持的网络功能
														// 对应结构体SDK_SystemFunction
	public final static int ABILTY_ENCODE = INDEX++; // 首先获得编码能力
														// 对应结构体CONFIG_EncodeAbility
	public final static int ABILITY_PTZPRO = INDEX++; // 云台协议
														// 对应结构体SDK_PTZPROTOCOLFUNC
	public final static int ABILITY_COMMPRO = INDEX++; // 串口协议 对应结构体SDK_COMMFUNC

	public final static int ABILITY_MOTION_FUNC = INDEX++; // 动态检测块
															// 对应结构体SDK_MotionDetectFunction
	public final static int ABILITY_BLIND_FUNC = INDEX++; // 视频遮挡块
															// 对应结构体SDK_BlindDetectFunction
	public final static int ABILITY_DDNS_SERVER = INDEX++; // DDNS服务支持类型
															// 对应结构体SDK_DDNSServiceFunction
	public final static int ABILITY_TALK = INDEX++; // 对讲编码类型
													// 对应结构体SDK_DDNSServiceFunction
	public final static int SYSINFO = INDEX++; // 系统信息 对应结构体H264_DVR_DEVICEINFO
	public final static int SYSNORMAL = INDEX++; // 普通配置 对应结构体SDK_CONFIG_NORMAL
	public final static int SYSENCODE = INDEX++; // 编码配置
													// 对应结构体SDK_EncodeConfigAll_SIMPLIIFY
	public final static int SYSNET = INDEX++; // 网络设置 对应结构体SDK_CONFIG_NET_COMMON
	public final static int PTZ = INDEX++; // 云台页面 对应结构体SDK_STR_PTZCONFIG_ALL
	public final static int COMM = INDEX++; // 串口页面 对应结构体SDK_CommConfigAll
	public final static int RECORD = INDEX++; // 录像设置界面 对应结构体SDK_RECORDCONFIG
	public final static int MOTION = INDEX++; // 动态检测页面 对应结构体SDK_MOTIONCONFIG
	public final static int SHELTER = INDEX++; // 视频遮挡
												// 对应结构体SDK_BLINDDETECTCONFIG
	public final static int VIDEO_LOSS = INDEX++; // 视频丢失 = INDEX++;
													// 对应结构体SDK_VIDEOLOSSCONFIG
	public final static int ALARM_IN = INDEX++; // 报警输入
												// 对应结构体SDK_ALARM_INPUTCONFIG
	public final static int ALARM_OUT = INDEX++; // 报警输出
													// 对应结构体SDK_AlarmOutConfigAll
	public final static int DISK_MANAGER = INDEX++;// 硬盘管理界面
													// 对应结构体SDK_StorageDeviceControl
	public final static int OUT_MODE = INDEX++; // 输出模式界面
												// 对应结构体SDK_VideoWidgetConfi
	public final static int CHANNEL_NAME = INDEX++;// 通道名称
													// 对应结构体SDK_ChannelNameConfigAll
	public final static int AUTO = INDEX++; // 自动维护界面配置
											// 对应结构体SDK_AutoMaintainConfig
	public final static int DEFAULT = INDEX++; // 恢复默认界面配置
												// 对应结构体SDK_SetDefaultConfigTypes
	public final static int DISK_INFO = INDEX++; // 硬盘信息
													// 对应结构体SDK_StorageDeviceInformationAll
	public final static int LOG_INFO = INDEX++; // 查询日志 对应结构体SDK_LogList
	public final static int NET_IPFILTER = INDEX++; // 黑名单配置
													// 对应结构体SDK_NetIPFilterConfig
	public final static int NET_DHCP = INDEX++; // DHC配置
												// 对应结构体SDK_NetDHCPConfigAll
	public final static int NET_DDNS = INDEX++; // DDNS信息
												// 对应结构体SDK_NetDDNSConfigALL
	public final static int NET_EMAIL = INDEX++; // EMAIL
													// 对应结构体SDK_NetEmailConfig
	public final static int NET_MULTICAST = INDEX++; // 组播
														// 对应结构体SDK_NetMultiCastConfig
	public final static int NET_NTP = INDEX++; // NTP 对应结构体SDK_NetNTPConfig
	public final static int NET_PPPOE = INDEX++; // PPPOE
													// 对应结构体SDK_NetPPPoEConfig
	public final static int NET_DNS = INDEX++; // DNS 对应结构体SDK_NetDNSConfig
	public final static int NET_FTPSERVER = INDEX++; // FTP
														// 对应结构体SDK_FtpServerConfig

	public final static int SYS_TIME = INDEX++; // 系统时间 对应结构体SDK_SYSTEM_TIME
	public final static int CLEAR_LOG = INDEX++; // 清除日志
	public final static int REBOOT_DEV = INDEX++; // 重启启动设备
	public final static int ABILITY_LANG = INDEX++; // 支持语言
													// 对应结构体SDK_MultiLangFunction
	public final static int VIDEO_FORMAT = INDEX++;
	public final static int COMBINEENCODE = INDEX++; // 组合编码
														// 对应结构体SDK_CombineEncodeConfigAll
	public final static int EXPORT = INDEX++; // 配置导出
	public final static int IMPORT = INDEX++; // 配置导入
	public final static int LOG_EXPORT = INDEX++; // 日志导出
	public final static int COMBINEENCODEMODE = INDEX++; // 组合编码模式
															// 对应结构体SDK_CombEncodeModeAll
	public final static int WORK_STATE = INDEX++; // 运行状态
	public final static int ABILITY_LANGLIST = INDEX++; // 实际支持的语言集
	public final static int NET_ARSP = INDEX++; // ARSP
												// 对应结构体SDK_NetARSPConfigAll
	public final static int SNAP_STORAGE = INDEX++;// 对应结构体SDK_SnapshotConfig
	public final static int NET_3G = INDEX++; // 3G拨号 对应结构体SDK_Net3GConfig
	public final static int NET_MOBILE = INDEX++; // 手机监控
													// 对应结构体SDK_NetMoblieConfig
	public final static int UPGRADEINFO = INDEX++; // 获取升级信息 参数 文件名
	public final static int NET_DECODER = INDEX++;
	public final static int ABILITY_VSTD = INDEX++; // 实际支持的视频制式
	public final static int CFG_ABILITY_VSTD = INDEX++; // 支持视频制式
														// 对应结构体SDK_MultiVstd
	public final static int NET_UPNP = INDEX++; // UPUN设置 对应结构体SDK_NetUPNPConfig
	public final static int NET_WIFI = INDEX++;// WIFI 对应结构体SDK_NetWifiConfig
	public final static int NET_WIFI_AP_LIST = INDEX++;// 对应结构体SDK_NetWifiDeviceAll
	public final static int SYSENCODE_SIMPLIIFY = INDEX++; // 简化的编码配置
															// 对应结构体SDK_EncodeConfigAll_SIMPLIIFY
	public final static int ALARM_CENTER = INDEX++; // 告警中心
													// 对应结构体SDK_NetAlarmServerConfigAll
	public final static int NET_ALARM = INDEX++;
	public final static int NET_MEGA = INDEX++; // 互信互通
	public final static int NET_XINGWANG = INDEX++; // 星望
	public final static int NET_SHISOU = INDEX++; // 视搜
	public final static int NET_VVEYE = INDEX++; // VVEYE
	public final static int NET_PHONEMSG = INDEX++; // 短信
													// 对应结构体SDK_NetShortMsgCfg
	public final static int NET_PHONEMEDIAMSG = INDEX++; // 彩信
															// 对应结构体SDK_NetMultimediaMsgCfg
	public final static int VIDEO_PREVIEW = INDEX++;
	public final static int NET_DECODER_V2 = INDEX++;
	public final static int NET_DECODER_V3 = INDEX++;// 数字通道
														// 对应结构体SDK_NetDecorderConfigAll_V3
	public final static int ABILITY_SERIALNO = INDEX++; // 序列号
	public final static int NET_RTSP = INDEX++; // RTSP 对应结构体SDK_NetRTSPConfig
	public final static int GUISET = INDEX++; // GUISET 对应结构体SDK_GUISetConfig
	public final static int CATCHPIC = INDEX++; // 抓图
	public final static int VIDEOCOLOR = INDEX++; // 视频颜色设置
	public final static int COMM485 = INDEX++;// 对应结构体SDK_STR_RS485CONFIG_ALL
	public final static int COMFIG_ABILITY_COMMPRO485 = INDEX++; // 串口485
																	// 对应结构体SDK_COMMFUNC
	public final static int SYS_TIME_NORTC = INDEX++; // 系统时间noRtc
														// 对应结构体SDK_COMMFUNC
	public final static int REMOTECHANNEL = INDEX++; // 远程通道
														// 对应结构体SDK_CONFIG_NET_COMMON
	public final static int OPENTRANSCOMCHANNEL = INDEX++; // 打开透明串口
															// 对应结构体TransComChannel
	public final static int CLOSETRANSCOMCHANNEL = INDEX++; // 关闭透明串口
	public final static int SERIALWIRTE = INDEX++; // 写入透明串口信息
	public final static int SERIALREAD = INDEX++; // 读取透明串口信息
	public final static int CHANNELTILE_DOT = INDEX++; // 点阵信息
	public final static int CAMERA = INDEX++; // 摄象机参数 对应结构体SDK_CameraParam
	public final static int ABILITY_CAMERA = INDEX++; // 曝光能力级
														// 对应结构体SDK_CameraAbility
	public final static int BUGINFO = INDEX++; // 命令调试
	public final static int STORAGENOTEXIST = INDEX++;// 硬盘不存在
														// 对应结构体SDK_VIDEOLOSSCONFIG
	public final static int STORAGELOWSPACE = INDEX++; // 硬盘容量不足
														// 对应结构体SDK_StorageLowSpaceConfig
	public final static int STORAGEFAILURE = INDEX++; // 硬盘出错
														// 对应结构体SDK_VIDEOLOSSCONFIG
	public final static int NETIPCONFLICT = INDEX++; // IP冲突
														// 对应结构体SDK_VIDEOLOSSCONFIG
	public final static int NETABORT = INDEX++; // 网络异常 对应结构体SDK_VIDEOLOSSCONFIG
	public final static int CHNSTATUS = INDEX++; // 通道状态
													// 对应结构体SDK_NetDecorderChnStatusAll
	public final static int CHNMODE = INDEX++; // 通道模式
												// 对应结构体SDK_NetDecorderChnModeConfig
	public final static int NET_DAS = INDEX++; // 主动注册 对应结构体SDK_DASSerInfo
	public final static int CAR_INPUT_EXCHANGE = INDEX++; // 外部信息输入与车辆状态的对应关系
	public final static int DELAY_TIME = INDEX++; // 车载系统延时配置
	public final static int NET_ORDER = INDEX++; // 网络优先级
	public final static int ABILITY_NETORDER = INDEX++; // //网络优先级设置能力
	public final static int CARPLATE = INDEX++; // 车牌号配置
	public final static int LOCALSDK_NET_PLATFORM = INDEX++; // //网络平台信息设置
																// 对应SDK_LocalSdkNetPlatformConfig
	public final static int GPS_TIMING = INDEX++; // GPS校时相关配置
													// 对应SDK_GPSTimingConfig
	public final static int VIDEO_ANALYZE = INDEX++; // 视频分析(智能DVR)
														// 对应SDK_RULECONFIG
	public final static int GODEYE_ALARM = INDEX++; // 神眼接警中心系统
													// 对应SDK_GodEyeConfig
	public final static int NAT_STATUS_INFO = INDEX++; // nat状态信息
														// 对应SDK_NatStatusInfo
	public final static int BUGINFOSAVE = INDEX++; // 命令调试(保存)
	public final static int MEDIA_WATERMARK = INDEX++;// 水印设置
														// 对应SDK_WaterMarkConfigAll
	public final static int ENCODE_STATICPARAM = INDEX++; // 编码器静态参数
															// 对应SDK_EncodeStaticParamAll
	public final static int LOSS_SHOW_STR = INDEX++; // 视频丢失显示字符串
	public final static int DIGMANAGER_SHOW = INDEX++; // 通道管理显示配置
														// 对应SDK_DigManagerShowStatus
	public final static int ABILITY_ANALYZEABILITY = INDEX++; // 智能分析能力
																// 对应SDK_ANALYZEABILITY
	public final static int VIDEOOUT_PRIORITY = INDEX++; // 显示HDMI VGA优先级别配置
	public final static int NAT = INDEX++; // NAT功能，MTU值配置 对应SDK_NatConfig
	public final static int CPCINFO = INDEX++; // 智能CPC计数数据信息 对应SDK_CPCDataAll
	public final static int STORAGE_POSITION = INDEX++; // 录像存储设备类型， 对应
														// SDK_RecordStorageType
	public final static int ABILITY_CARSTATUSNUM = INDEX++; // 车辆状态数 对应
															// SDK_CarStatusNum
	public final static int VPN = INDEX++; // VPN 对应SDK_VPNConfig
	public final static int VIDEOOUT = INDEX++; // /VGA视频分辨率 对应SDK_VGAresolution
	public final static int ABILITY_VGARESOLUTION = INDEX++; // 支持的VGA分辨率列表
																// 对应SDK_VGAResolutionAbility
	public final static int NET_LOCALSEARCH = INDEX++; // 搜索设备，设备端的局域网设备
														// 对应SDK_NetDevList
	public final static int NETPLAT_KAINENG = INDEX++; // SDK_CONFIG_KAINENG_INFO
	public final static int ENCODE_STATICPARAM_V2 = INDEX++; // DVR编码器静态参数
																// SDK_EncodeStaticParamV2
	public final static int ABILITY_ENC_STATICPARAM = INDEX++; // 静态编码能力集 DVR
																// SDK_EncStaticParamAbility
																// (掩码)
	public final static int C7_PLATFORM = INDEX++; // SDK_C7PlatformConfig
	public final static int MAIL_TEST = INDEX++; // //邮件测试 SDK_NetEmailConfig
	public final static int NET_KEYBOARD = INDEX++; // 网络键盘服务
													// SDK_NetKeyboardConfig
	public final static int ABILITY_NET_KEYBOARD = INDEX++; // 网络键盘协议
															// SDK_NetKeyboardAbility
	public final static int SPVMN_PLATFORM = INDEX++; // 28181协议配置
														// SDK_ASB_NET_VSP_CONFIG
	public final static int PMS = INDEX++; // 手机服务 SDK_PMSConfig
	public final static int OSD_INFO = INDEX++; // 屏幕提示信息 SDK_OSDInfoConfigAll
	public final static int KAICONG = INDEX++; // SDK_KaiCongAlarmConfig
	public final static int DIGITAL_REAL = INDEX++; // 真正支持的通道模式
													// SDK_VideoChannelManage
	public final static int ABILITY_PTZCONTROL = INDEX++; // PTZ控制能力集
															// SDK_PTZControlAbility
	public final static int XMHEARTBEAT = INDEX++; // SDK_XMHeartbeatConfig
	public final static int MONITOR_PLATFORM = INDEX++; // SDK_MonitorPlatformConfig
	public final static int PARAM_EX = INDEX++; // 摄像头扩展参数 // SDK_CameraParamEx
	public final static int NETPLAT_ANJU_P2P = INDEX++; // 安巨P2P
														// SDK_NetPlatformCommonCfg
	public final static int GPS_STATUS = INDEX++;// /< GPS连接信息 SDK_GPSStatusInfo
	public final static int WIFI_STATUS = INDEX++;// /< Wifi连接信息
													// SDK_WifiStatusInfo
	public final static int CFG_3G_STATUS = INDEX++; // /< 3G连接信息
														// SDK_WirelessStatusInfo
	public final static int DAS_STATUS = INDEX++; // /< 主动注册状态 SDK_DASStatusInfo
	public final static int ABILITY_DECODE_DELEY = INDEX++; // 解码策略能力---对应结构体SDK_DecodeDeleyTimePrame
	public final static int CFG_DECODE_PARAM = INDEX++; // 解码最大延时---对应结构体SDK_DecodeParam
	public final static int CFG_VIDEOCOLOR_CUSTOM = INDEX++; // SDK_VIDEOCOLOR_PARAM_CUSTOM
	public final static int ABILITY_ONVIF_SUB_PROTOCOL = INDEX++;// onvif子协议---对应结构体SDK_AbilityMask
	public final static int CONFIG_EXPORT_V2 = INDEX++; // 导出设备默认配置，即出厂的配置
	public final static int CFG_CAR_BOOT_TYPE = INDEX++; // 车载开关机模式---对应结构体SDK_CarBootTypeConfig
	public final static int CFG_IPC_ALARM = INDEX++; // IPC网络报警---对应结构体SDK_IPCAlarmConfigAll
	public final static int CFG_NETPLAT_TUTK_IOTC = INDEX++; // TUTK
																// IOTC平台配置---对应结构体SDK_NetPlatformCommonCfg
	public final static int CFG_BAIDU_CLOUD = INDEX++; // 百度云---对应结构体SDK_BaiduCloudCfg
	// INDEX = 160;
	public final static int CFG_PMS_MSGNUM = INDEX++; // 手机订阅数---对应结构体SDK_PhoneInfoNum
	public final static int CFG_IPC_IP = INDEX++; // 控制DVR去修改设备IP---对应结构体SDK_IPSetCfg
	public final static int ABILITY_DIMEN_CODE = INDEX++; // 二维码点阵---对应结构体SDK_DimenCodeAll
	public final static int CFG_MOBILE_WATCH = INDEX++; // 中国电信手机看店平台配置---对应结构体SDK_MobileWatchCfg
	public final static int CFG_BROWSER_LANGUAGE = INDEX++; // 使用浏览器时使用的语言---对应结构体SDK_BrowserLanguageType
	public final static int CFG_TIME_ZONE = INDEX++; // 时区配置---对应结构体SDK_TimeZone
	public final static int CFG_NETBJTHY = INDEX++; // 客户配置---对应结构体SDK_MonitorPlatformConfig
	public final static int ABILITY_MAX_PRE_RECORD = INDEX++; // 最大可设置预录时间1~30---对应结构体SDK_AbilityMask
	public final static int CFG_DIG_TIME_SYN = INDEX++; // 数字通道时间同步配置(决定前端同步方式)---对应结构体SDK_TimeSynParam
	public final static int CONFIG_OSDINFO_DOT = INDEX++; // 3行OSD
	// INDEX = 170;
	public final static int NET_POS = INDEX++; // POS机配置---对应结构体SDK_NetPosConfigAll
	public final static int CFG_CUSTOMIZE_OEMINFO = INDEX++; // 定制OEM客户版本信息---对应结构体SDK_CustomizeOEMInfo
	public final static int CFG_DIGITAL_ENCODE = INDEX++; // 数字通道精简版编码配置---对应结构体SDK_EncodeConfigAll_SIMPLIIFY
	public final static int CFG_DIGITAL_ABILITY = INDEX++; // 数字通道的编码能力---对应结构体SDK_DigitDevInfo
	public final static int CFG_ENCODECH_DISPLAY = INDEX++; // IE端编码配置显示的前端通道号---对应结构体SDK_EncodeChDisplay
	public final static int CFG_RESUME_PTZ_STATE = INDEX++; // 开机云台状态---对应结构体SDK_ResumePtzState
	public final static int CFG_LAST_SPLIT_STATE = INDEX++; // 最近一次的画面分割模式，用于重启后恢复之前的分割模式
	public final static int CFG_SYSTEM_TIMING_WORK = INDEX++; // 设备定时开关机时间配置。隐藏在自动维护页面里，要用超级密码登陆才能看到界面
	public final static int CFG_GBEYESENV = INDEX++; // 宝威环境监测平台配置---对应结构体SDK_NetPlatformCommonCfg
	public final static int ABILITY_AHD_ENCODE_L = INDEX++; // AHDL能力集---对应结构体SDK_AHDEncodeLMask
	// INDEX = 180;
	public final static int CFG_SPEEDALARM = INDEX++; // 速度报警---对应结构体SDK_SpeedAlarmConfigAll
	public final static int CFG_CORRESPONDENT_INFO = INDEX++; // 用户自定义配置---对应结构体SDK_CorrespondentOwnInfo
	public final static int SET_OSDINFO = INDEX++; // OSD信息设置---对应结构体SDK_OSDInfo
	public final static int SET_OSDINFO_V2 = INDEX++; // OSD信息叠加，不保存配置---对应结构体SDK_OSDInfoConfigAll
	public final static int ABILITY_SUPPORT_EXTSTREAM = INDEX++;// 支持辅码流录像---对应结构体SDK_AbilityMask
	public final static int CFG_EXT_RECORD = INDEX++; // 辅码流配置---对应结构体SDK_RECORDCONFIG_ALL/SDK_RECORDCONFIG
	public final static int CFG_APP_DOWN_LINK = INDEX++; // 用于用户定制下载链接---对应结构体SDK_AppDownloadLink
	public final static int CFG_EX_USER_MAP = INDEX++; // 用于保存明文数据---对应结构体SDK_UserMap
	public final static int CFG_TRANS_COMM_DATA = INDEX++; // 串口数据主动上传到UDP或TCP服务器，其中TCP服务器可以支持双向通信---对应结构体SDK_NetTransCommData
	public final static int EXPORT_LANGUAGE = INDEX++; // 语言导出
	// INDEX = 190;
	public final static int IMPORT_LANGUAGE = INDEX++; // 语言导入
	public final static int DELETE_LANGUAGE = INDEX++; // 语言删除
	public final static int CFG_UPGRADE_VERSION_LIST = INDEX++; // 云升级文件列表---对应结构体SDK_CloudUpgradeList
	public final static int CFG_GSENSORALARM = INDEX++; // GSENSOR报警
	public final static int CFG_USE_PROGRAM = INDEX++; // 启动客户小程序---对应结构体SDK_NetUseProgram
	public final static int CFG_FTP_TEST = INDEX++; // FTP测试---对应结构体SDK_FtpServerConfig
	public final static int CFG_FbExtraStateCtrl = INDEX++; // 消费类产品的录像灯的状态---对应结构体SDK_FbExtraStateCtrl
	public final static int CFG_PHONE = INDEX++; // 手机用
	public final static int PicInBuffer = INDEX++; // 手机抓图 = INDEX++;弃用
	public final static int GUARD = INDEX++; // 布警 弃用
	// 200
	public final static int UNGUARD = INDEX++; // 撤警，弃用
	public final static int CFG_START_UPGRADE = INDEX++; // 开始升级，弃用
	public final static int CFG_AUTO_SWITCH = INDEX++; // 插座定时开关---获取配置都用H264_DVR_GetDevConfig_Json
														// =
														// INDEX++;配置使用H264_DVR_SetDevConfig_Json(配置时的格式见智能插座用到的命令.doc)(两个接口简称Json接口
														// = INDEX++;下面用简称)
														// "Name":"PowerSocket.AutoSwitch"
	public final static int CFG_POWER_SOCKET_SET = INDEX++; // 控制插座开关---Json接口
															// "Name":"OPPowerSocketGet"
	public final static int CFG_AUTO_ARM = INDEX++; // 插座的定时布撤防---Json接口
													// "Name":"PowerSocket.AutoArm"
	public final static int CFG_WIFI_MODE = INDEX++; // Wifi模式配置，用于行车记录仪切换AP模式---对应结构体SDK_NetWifiMode
	public final static int CFG_CIENT_INFO = INDEX++; // 传递手机客户端信息---Json接口
														// "Name":"PowerSocket.ClientInfo"
	public final static int CFG_ATHORITY = INDEX++; // SDK_Authority---Json接口
													// "Name":"PowerSocket.Authority"
	public final static int CFG_ARM = INDEX++; // SDK_Arm---Json接口
												// "Name":"PowerSocket.Arm"
	public final static int CFG_AUTOLIGHT = INDEX++; // 设置夜灯的定时开关功能 --Json接口
														// "Name" :
														// "PowerSocket.AutoLight"
														// = INDEX++;
	// 210
	public final static int CFG_LIGHT = INDEX++; // 使能和禁止夜灯的动检响应功能---Json接口
													// "Name" :
													// "PowerSocket.Light" =
													// INDEX++;
	public final static int CFG_WORKRECORD = INDEX++; // 进行电量统计---Json接口 "Name"
														// :
														// "PowerSocket.WorkRecord"
														// = INDEX++;
	public final static int CFG_SYSTEMTIME = INDEX++; // 设置时间的命令 =
														// INDEX++;当局域网连接的时候 =
														// INDEX++;连接的时候 =
														// INDEX++;发送对时命令
														// --Json接口
														// "Name":"System.Time"
	public final static int CFG_USB = INDEX++; // usb接口控制功能---Json接口
												// "Name":"PowerSocket.Usb"
	public final static int CFG_NETPLAT_BJHONGTAIHENG = INDEX++;// 北京鸿泰恒平台---对应结构体SDK_CONFIG_NET_BJHONGTAIHENG
	public final static int CFG_CLOUD_STORAGE = INDEX++; // 云存储相关配置---对应结构体SDK_CloudRecordConfigAll
	public final static int CFG_IDLE_PTZ_STATE = INDEX++; // 云台空闲动作相关配置---对应结构体SDK_PtzIdleStateAll
	public final static int CFG_CAMERA_CLEAR_FOG = INDEX++; // 去雾功能配置---对应结构体SDK_CameraClearFogAll
	public final static int CFG_WECHATACCOUNT = INDEX++; // ---对应json
															// "Name":"PowerSocket.WechatAccount"
	public final static int CFG_WECHATRENEW = INDEX++; // ---对应json
														// "Name":"PowerSocket.WechatRenew"
	// 220
	public final static int CFG_POWERSOCKET_WIFI = INDEX++;// ---对应json
															// "Name":"PowerSocket.WiFi"
	public final static int CFG_CAMERA_MOTOR_CONTROL = INDEX++; // 机器人马达控制---对应结构体SDK_CameraMotorCtrl
	public final static int CFG_ENCODE_ADD_BEEP = INDEX++; // 设置编码加入每隔30秒beep声---对应结构体SDK_EncodeAddBeep
	public final static int CFG_DATALINK = INDEX++; // datalink客户在网络服务中的执行程序使能配置---对应结构体
													// SDK_DataLinkConfig
	public final static int CFG_FISH_EYE_PARAM = INDEX++; // 鱼眼功能参数配置---对应结构体SDK_FishEyeParam
	public final static int OPERATION_SET_LOGO = INDEX++; // 视频上叠加雄迈等厂家的LOGO---对应结构体SDK_SetLogo
	public final static int CFG_SPARSH_HEARTBEAT = INDEX++; // Sparsh客户的心跳功能配置---对应结构体
															// SDK_SparshHeartbeat
	public final static int CFG_LOGIN_FAILED = INDEX++; // 登录失败时发送邮件，使用结构体:基本事件结构---对应结构体
														// SDK_VIDEOLOSSCONFIG

	public final static int CFG_NETPLAT_SPVMN_NAS= INDEX++;//安徽超清客户的nas服务器配置---对应结构体SDK_SPVMN_NAS_SERVER
	public final static int CFG_DDNS_APPLY= INDEX++;			//ddns 按键功能测试---对应结构体SDK_NetDDNSConfigALL
	public final static int OPERATION_NEW_UPGRADE_VERSION_REQ= INDEX++;	///新版云升级版本查询请求---对应结构体SDK_CloudUpgradeVersionRep
	public final static int CFG_IPV6_ADDRESS= INDEX++;		//ipv6------对应的结构体SDK_IPAddressV6
	public final static int CFG_DDNS_IPMSG= INDEX++;        //DDNS外网IP地址
	public final static int CFG_ONLINE_UPGRADE= INDEX++;	//在线升级相关配置-----对应的结构体SDK_OnlineUpgradeCfg
	public final static int CFG_CONS_SENSOR_ALARM= INDEX++;  //家用产品433报警联动项配置-----对应的SDK_ConsSensorAlarmCfg

	public final static int ENET_TYPE_UNKNOWN = 0; // 未知
	public final static int ENET_TYPE_WIFI = 1; // WIFI
	public final static int ENET_TYPE_MOBILE = 2; // 移动网络
	public final static int ENET_TYPE_NET_LINE = 4; // 物理网卡线

	public final static int EUPGRADE_STEP_COMPELETE = 10; // 完成升级
	public final static int EUPGRADE_STEP_DOWN = 1; // 表示下载进度;(从服务器或网络下载文件到手机(云升级是下载到设备))
	public final static int EUPGRADE_STEP_UP = 2; // 表示上传进度;(本地上传文件到设备)
	public final static int EUPGRADE_STEP_UPGRADE = 3; // 升级过程

	public final static int LOG_UI_MSG = 1; // 日志输出到手机日志页面
	public final static int LOG_FILE = 2; // 写到日志文件
	public final static int LOG_NET_MSG = 4; // 输出到网络端
	
	// EDEV_UPGRADE_TYPE--检查升级返回结果
	public final static int EDUT_NONE = 0;                  // 没有更新
	public final static int EDUT_DEV_CLOUD = 1;             // 升级方式1:设备连接升级服务器升级
	public final static int EDUT_LOCAL_NEED_DOWN = 2;       // 升级方式2:本地升级,但升级文件还没有下载下来
	public final static int EDUT_LOCAL_DOWN_COMPLETE = 3;   // 升级方式3:本地升级,升级文件已经下载下来了
}
```

### 13. EMSSubType

```java
// 媒体查询子类型(Media Search SubType)
public interface EMSSubType {
	public static int ALL = 0x3FFFFFF;
	public static int ALERT = 0; // 外部报警录像/图片('A')
	public static int DYNAMIC = 12; // 视频侦测录像/图片('M')
	public static int HAND = 7; // 手动录像/手动抓图('H')
	public static int SPT_KEY = 10; // 运动相机关键录像('K')
	public static int KEY = 10; // 普通设备关键录像('K')
	public static int URGENT = 21; // 紧急录像('V')
	public static int ORIGINAL = 17; // 原始录像(注意此种Type类型为mp4)('R')
	public static int INVASION = 8; // 入侵('I')
	public static int STRANDED = 18; // 盗移、滞留('S')
	public static int FACE = 5; // 人脸识别录像('F')
	public static int CARNO = 13; // 车牌识别('N')
	public static int CHANGE = 6; // 场景切换('G')
}
```

### 14. EDEV_ATTR

```java
package com.lib;

public class EDEV_ATTR {
	public final static int EDA_STATE_CHN = 1;
	public final static int EDA_OPT_ALARM = 2;
	public final static int EDA_OPT_RECORD = 3;
	public final static int EDA_DEV_INFO = 4;
}
```

### 15.  服务器设置对应字符串（用于修改服务默认ip、port）

```java
    "DSS_SERVER"
    "SQUARE_SERVER"
    "PMS_SERVER"
    "MI_SERVER" // 通用帐户云服务
    "KSS_SERVER"
    "CFS_SERVER"
    "SQUARE"
    "XM030"
    "UPGRADE_SERVER" // 固件升级服务器
    "VIDEO_SQUARE"   // 视频广场
    "APP_SERVER"     // app相关服务器
    "CAPS_SERVER"    // 能力集控制服务器
    "XMCLOUD_SERVER"
    "QINGTING_SERVER"// 设备状态查询服务器
    "STATUS_P2P_SERVER" //-->P2P穿透服务的设备状态
    "STATUS_TPS_SERVER" //-->透明传输代理的设备状态
    "STATUS_DSS_SERVER" //-->媒体直播系统中的设备状态
    "STATUS_CSS_SERVER" //-->云存储服务的设备状态
    "STATUS_RPS_SERVER" //-->可靠的代理服务设备状态
    "STATUS_IDR_SERVER" //-->低功耗设备状态
    "CONFIG_SERVER"     // 配置管理中心
    "PMS_ALM_SERVER"    // 报警推送服务器 -->消息推送：供设备推送报警消息; 短连接
    "ALC_ALM_SERVER"    // 消息推送：供android客户端用,长连接
    "PMS_PIC_SERVER"    // 报警图片服务
    "ACCESS_CSS_SERVER" // 云存储：签名专用,短连接
    "HLS_DSS_SERVER"    // DSS码流分配服务器
    "VMS_CLOUD_SERVER"  // BCloud365
    "SHADOW_SERVER"     // 影子服务 
```
