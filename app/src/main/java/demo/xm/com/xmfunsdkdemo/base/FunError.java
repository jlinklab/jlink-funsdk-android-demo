package demo.xm.com.xmfunsdkdemo.base;

import java.util.HashMap;
import java.util.Map;

import demo.xm.com.xmfunsdkdemo.R;


public class FunError {

	public static final int EE_OK = 0;
	public static final int EE_OBJ_NOT_EXIST = -1239510;
	public static final int EE_ERROR = -100000;
	public static final int EE_PARAM_ERROR = -99999;
	public static final int EE_CREATE_FILE = -99998;
	public static final int EE_OPEN_FILE = -99997;
	public static final int EE_WRITE_FILE = -99996;
	public static final int EE_READ_FILE = -99995;
	public static final int EE_NO_SUPPORTED = -99994;
	public static final int EE_NET = -99993;					// 网络错误

	// 本接口用到的错误值枚举
	public static final int EE_DVR_SDK_NOTVALID			= -10000;			// 非法请求
	public static final int EE_DVR_NO_INIT				= -10001;			// SDK未经初始化
	public static final int EE_DVR_ILLEGAL_PARAM		= -10002;			// 用户参数不合法
	public static final int EE_DVR_INVALID_HANDLE		= -10003;			// 句柄无效
	public static final int EE_DVR_SDK_UNINIT_ERROR		= -10004;			// SDK清理出错
	public static final int EE_DVR_SDK_TIMEOUT			= -10005;			// 等待超时
	public static final int EE_DVR_SDK_MEMORY_ERROR		= -10006;			// 内存错误，创建内存失败
	public static final int EE_DVR_SDK_NET_ERROR		= -10007;			// 网络错误
	public static final int EE_DVR_SDK_OPEN_FILE_ERROR	= -10008;			// 打开文件失败
	public static final int EE_DVR_SDK_UNKNOWNERROR		= -10009;			// 未知错误
	public static final int EE_DVR_DEV_VER_NOMATCH		= -11000;			// 收到数据不正确，可能版本不匹配
	public static final int EE_DVR_SDK_NOTSUPPORT		= -11001;			// 版本不支持
	
	public static final int EE_DVR_OPEN_CHANNEL_ERROR	= -11200;			// 打开通道失败
	public static final int EE_DVR_CLOSE_CHANNEL_ERROR	= -11201;			// 关闭通道失败
	public static final int EE_DVR_SUB_CONNECT_ERROR	= -11202;			// 建立媒体子连接失败
	public static final int EE_DVR_SUB_CONNECT_SEND_ERROR = -11203;			// 媒体子连接通讯失败
	public static final int EE_DVR_NATCONNET_REACHED_MAX  = -11204;         // Nat视频链接达到最大，不允许新的Nat视频链接 
	
	// 用户管理部分错误码
	public static final int EE_DVR_NOPOWER					= -11300;			// 无权限
	public static final int EE_DVR_PASSWORD_NOT_VALID		= -11301;			// 账号密码不对
	public static final int EE_DVR_LOGIN_USER_NOEXIST		= -11302;			// 用户不存在
	public static final int EE_DVR_USER_LOCKED				= -11303;			// 该用户被锁定
	public static final int EE_DVR_USER_IN_BLACKLIST		= -11304;			// 该用户不允许访问(在黑名单中)
	public static final int EE_DVR_USER_HAS_USED			= -11305;			// 该用户以登陆
	public static final int EE_DVR_USER_NOT_LOGIN			= -11306;			// 该用户没有登陆
	public static final int EE_DVR_CONNECT_DEVICE_ERROR    = -11307;			// 设备可能离线
	public static final int EE_DVR_ACCOUNT_INPUT_NOT_VALID = -11308;			// 用户管理输入不合法
	public static final int EE_DVR_ACCOUNT_OVERLAP			= -11309;			// 索引重复
	public static final int EE_DVR_ACCOUNT_OBJECT_NONE		= -11310;			// 不存在对象; 用于查询时
	public static final int EE_DVR_ACCOUNT_OBJECT_NOT_VALID= -11311;			// 不存在对象
	public static final int EE_DVR_ACCOUNT_OBJECT_IN_USE	= -11312;			// 对象正在使用
	public static final int EE_DVR_ACCOUNT_SUBSET_OVERLAP	= -11313;			// 子集超范围 (如组的权限超过权限表，用户权限超出组的权限范围等等)
	public static final int EE_DVR_ACCOUNT_PWD_NOT_VALID	= -11314;			// 密码不正确
	public static final int EE_DVR_ACCOUNT_PWD_NOT_MATCH	= -11315;			// 密码不匹配
	public static final int EE_DVR_ACCOUNT_RESERVED			= -11316;			// 保留帐号

	// 配置管理相关错误码
	public static final int EE_DVR_OPT_RESTART				= -11400;			// 保存配置后需要重启应用程序
	public static final int EE_DVR_OPT_REBOOT				= -11401;			// 需要重启系统
	public static final int EE_DVR_OPT_FILE_ERROR			= -11402;			// 写文件出错
	public static final int EE_DVR_OPT_CAPS_ERROR			= -11403;			// 配置特性不支持
	public static final int EE_DVR_OPT_VALIDATE_ERROR		= -11404;			// 配置校验失败
	public static final int EE_DVR_OPT_CONFIG_NOT_EXIST		= -11405;			// 请求或者设置的配置不存在

	// 
	public static final int EE_DVR_CTRL_PAUSE_ERROR		= -11500;			  // 暂停失败
	public static final int EE_DVR_SDK_NOTFOUND			= -11501;			  // 查找失败，没有找到对应文件
	public static final int EE_DVR_CFG_NOT_ENABLE       = -11502;             // 配置未启用
	public static final int EE_DVR_DECORD_FAIL          = -11503;             // 解码失败

	//DNS协议解析返回错误码
	public static final int EE_DVR_SOCKET_ERROR             = -11600;         // 创建套节字失败
	public static final int EE_DVR_SOCKET_CONNECT           = -11601;         // 连接套节字失败
	public static final int EE_DVR_SOCKET_DOMAIN            = -11602;         // 域名解析失败
	public static final int EE_DVR_SOCKET_SEND              = -11603;         // 发送数据失败
	public static final int EE_DVR_ARSP_NO_DEVICE           = -11604;         // 没有获取到设备信息，设备应该不在线
	public static final int EE_DVR_ARSP_BUSING              = -11605;         // ARSP服务繁忙
	public static final int EE_DVR_ARSP_BUSING_SELECT       = -11606;         // ARSP服务繁忙;select失败
	public static final int EE_DVR_ARSP_BUSING_RECVICE		= -11607;         // ARSP服务繁忙;recvice失败
	public static final int EE_DVR_CONNECTSERVER_ERROR      = -11608;         // 连接服务器失败
	public static final int EE_DVR_ARSP_USER_NOEXIST        = -11609;        // 用户不存在 
	public static final int EE_DVR_ARSP_PASSWORD_ERROR		= -11610;		  // 账号密码不对
	public static final int EE_DVR_ARSP_QUERY_ERROR			= -11611;		  // 查询失败
	public static final int EE_DVR_CONNECT_FULL			= -11612;       // 服务器连接数已满

	//版权相关
	public static final int EE_DVR_PIRATESOFTWARE           =-11700;		 // 设备盗版

	public static final int EE_ILLEGAL_PARAM = -200000;		// 无效参数
	public static final int EE_USER_NOEXIST = -100001;		// 用户不存在
	public static final int EE_SQL_ERROR = -100002;			// sql失败
	public static final int EE_PASSWORD_NOT_VALID = -100003;	// 密码不正确
	public static final int EE_USER_NO_DEV = -100004;		// 用户没有该设备
	public static final int EE_DEV_NOT_LOGIN = -100005;		// 登录失败
	
	public static final int EE_USER_DEV_MAC_EXSIT = -200004;		// 设备已存在,同 EE_CLOUD_DEV_MAC_EXSIT
	
	public final static int EE_DSS_NOT_SUP_MAIN = -210008;	// DSS未开通高清功能
	public final static int EE_TPS_NOT_SUP_MAIN = -210009;	// 转发不支持高清
	
	public static final int EE_MC_NOTFOUND = -201110; // 查找失败，没有找到对应文件
	public static final int EE_LINK_SERVER_ERROR = -201118;			// 连接服务器失败
	
	public static final int EE_AS_PHONE_CODE0 =-210002; //接口验证失败
	public static final int EE_AS_PHONE_CODE1 =-210003; //参数错误
	public static final int EE_AS_PHONE_CODE2 =-210004; //手机号码已被注册
	public static final int EE_AS_PHONE_CODE3 =-210005; //超出短信每天发送次数限制(每个号码发送注册验证信息1天只能发送三次)
	public static final int EE_AS_PHONE_CODE4 =-210010; //发送失败
	public static final int EE_AS_PHONE_CODE5 =-210017; // 120秒之内只能发送一次，
	
	public static final int EE_AS_REGISTER_CODE0 =-210102; //接口验证失败
	public static final int EE_AS_REGISTER_CODE1 =-210103; //参数错误
	public static final int EE_AS_REGISTER_CODE2 =-210104; //手机号码已被注册
	public static final int EE_AS_REGISTER_CODE3 =-210106; //用户名已被注册
	public static final int EE_AS_REGISTER_CODE4 =-210107; //注册码验证错误
	public static final int EE_AS_REGISTER_CODE5 =-210110; //注册失败（msg里有失败具体信息）
	public static final int EE_AS_LOGIN_CODE1 =-210202; //接口验证失败
	public static final int EE_AS_LOGIN_CODE2 =-210203; //参数错误
	public static final int EE_AS_LOGIN_CODE3 =-210210; //登录失败
	public static final int EE_AS_EIDIT_PWD_CODE1 =-210302; // 接口验证失败
	public static final int EE_AS_EIDIT_PWD_CODE2 =-210303; // 参数错误
	public static final int EE_AS_EIDIT_PWD_CODE3 =-210311; // 新密码不符合要求
	public static final int EE_AS_EIDIT_PWD_CODE4 =-210313; // 原密码错误
	public static final int EE_AS_EIDIT_PWD_CODE5 =-210315; // 请输入与原密码不同的新密码
	public static final int EE_AS_FORGET_PWD_CODE1 =-210402; // 接口验证失败
	public static final int EE_AS_FORGET_PWD_CODE2 =-210403; // 参数错误
	public static final int EE_AS_FORGET_PWD_CODE3 =-210405; // 超出短信每天发送次数限制(每个号码发送注册验证信息1天只能发送三次)
	public static final int EE_AS_FORGET_PWD_CODE4 =-210410; // 发送失败（msg里有失败具体信息）
	public static final int EE_AS_FORGET_PWD_CODE5 =-210414; // 手机号码不存在
	public static final int EE_AS_RESET_PWD_CODE1 =-210502; //接口验证失败
	public static final int EE_AS_RESET_PWD_CODE2 =-210503; //参数错误
	public static final int EE_AS_RESET_PWD_CODE3 =-210511; //新密码不符合要求
	public static final int EE_AS_RESET_PWD_CODE4 =-210512; //两次输入的新密码不一致
	public static final int EE_AS_RESET_PWD_CODE5 =-210514; //手机号码不存在
	public static final int EE_AS_CHECK_PWD_CODE1 =-210602; //接口验证失败
	public static final int EE_AS_CHECK_PWD_CODE2 =-210603; //参数错误
	public static final int EE_AS_CHECK_PWD_CODE3 =-210607; //验证码错误
	public static final int EE_AS_CHECK_PWD_CODE4 =-210614; //手机号码不存在
	//视频广场相关
	public static final int EE_AS_GET_PUBLIC_DEV_LIST_CODE = -210700; // 服务器响应失败
	public static final int EE_AS_GET_SHARE_DEV_LIST_CODE = -210800; // 服务器响应失败
	public static final int EE_AS_SET_DEV_PUBLIC_CODE = -210900; // 服务器响应失败
	public static final int EE_AS_SHARE_DEV_VIDEO_CODE = -211000; // 服务器响应失败
	public static final int EE_AS_CANCEL_DEV_PUBLIC_CODE = -211100; // 服务器响应失败
	public static final int EE_AS_CANCEL_SHARE_VIDEO_CODE = -211200; // 服务器响应失败
	public static final int EE_AS_DEV_REGISTER_CODE = -211300; // 服务器响应失败
	public static final int EE_AS_SEND_COMMNET_CODE  = -211400; // 服务器响应失败
	public static final int EE_AS_GET_COMMENT_LIST_CODE = -211500; // 服务器响应失败
	public static final int EE_AS_GET_VIDEO_INFO_CODE = -211600; // 服务器响应失败
	public static final int EE_AS_UPLOAD_LOCAL_VIDEO_CODE = -211700; // 服务器响应失败
	public static final int EE_AS_UPLOAD_LOCAL_VIDEO_CODE1 = -211703; // 缺少上传文件
	public static final int EE_AS_GET_SHORT_VIDEO_LIST_CODE = -211800; // 服务响应失败
	public static final int EE_AS_EDIT_SHORT_VIDEO_INFO_CODE = -211900; // 服务响应失败
	public static final int EE_AS_DELETE_SHORT_VIDEO_CODE = -212000; // 服务响应失败
	public static final int EE_AS_SELECT_AUTHCODE_CDOE =  -212100; // 服务响应失败
	public static final int EE_AS_SELECT_AUTHCODE_CDOE1 =  -212104; // 服务器查询失败
	public static final int EE_AS_GET_USER_PHOTOS_LIST_CODE = -212200; // 服务响应失败
	public static final int EE_AS_CREATE_USER_PHOTOS_CODE = -212300; // 服务响应失败
	public static final int EE_AS_UPLOAD_PHOTO_CODE = -212400; // 服务响应失败
	public static final int EE_AS_UPLOAD_PHOTO_CODE1 = -212402; // 没有接受到上传的文件
	public static final int EE_AS_EDIT_PHOTO_INFO_CODE = -212500; // 服务响应失败
	public static final int EE_AS_GET_PHOTO_LIST_CODE = -212600; // 服务响应失败
	public static final int EE_AS_DELETE_PHOTO_CODE = -212700; // 服务响应失败
	public static final int EE_AS_DELETE_PHOTOS_CODE = -212800; //服务响应失败
	
	public static final int EE_AS_CHECK_PWD_STRENGTH_CODE = -212900; //服务器响应失败
	public static final int EE_AS_CHECK_PWD_STRENGTH_CODE1 = -212902; //接口验证失败
	public static final int EE_AS_CHECK_PWD_STRENGTH_CODE2 = -212903; //参数错误
	public static final int EE_AS_CHECK_PWD_STRENGTH_CODE3 = -212910; //密码不合格
	
	//云服务通过邮箱找回密码返回错误
	public static final int EE_HTTP_PWBYEMAIL_UNFINDNAME = -213000;  //无此用户名
	public static final int EE_HTTP_PWBYEMAIL_FAILURE = -213001;    //发送失败
	
	public static final int EE_AS_SEND_EMAIL_CODE = -213100;    // 服务响应失败
	public static final int EE_AS_SEND_EMAIL_CODE1 = -213102;   // 接口验证失败
	public static final int EE_AS_SEND_EMAIL_CODE2 = -213103;   //参数错误
	public static final int EE_AS_SEND_EMAIL_CODE3 = -213108;   //邮箱已被注册
	public static final int EE_AS_SEND_EMAIL_CODE4 = -213110;   //发送失败
	
	public static final int EE_AS_REGISTE_BY_EMAIL_CODE = -213200;    // 服务响应失败
	public static final int EE_AS_REGISTE_BY_EMAIL_CODE1 = -213202;    // 接口验证失败
	public static final int EE_AS_REGISTE_BY_EMAIL_CODE2 = -213203;    // 参数错误
	public static final int EE_AS_REGISTE_BY_EMAIL_CODE3 = -213206;    // 用户名已被注册
	public static final int EE_AS_REGISTE_BY_EMAIL_CODE4 = -213207;    // 注册码验证错误
	public static final int EE_AS_REGISTE_BY_EMAIL_CODE5 = -213208;    // 邮箱已被注册
	public static final int EE_AS_REGISTE_BY_EMAIL_CODE6 = -213210;    // 注册失败
	
	public static final int EE_AS_SEND_EMAIL_FOR_CODE = -213300;    // 服务响应失败
	public static final int EE_AS_SEND_EMAIL_FOR_CODE1 = -213302;    // 接口验证失败
	public static final int EE_AS_SEND_EMAIL_FOR_CODE3 = -213303;    // 参数错误
	public static final int EE_AS_SEND_EMAIL_FOR_CODE4 = -213310;    // 发送失败
	public static final int EE_AS_SEND_EMAIL_FOR_CODE5 = -213314;    // 邮箱不存在
	public static final int EE_AS_SEND_EMAIL_FOR_CODE6 = -213316;    // 箱和用户名不匹配
	
	public static final int EE_AS_CHECK_CODE_FOR_EMAIL = -213400;    // 服务响应失败
	public static final int EE_AS_CHECK_CODE_FOR_EMAIL1 = -213402;    // 接口验证失败
	public static final int EE_AS_CHECK_CODE_FOR_EMAIL2 = -213403;    // 参数错误
	public static final int EE_AS_CHECK_CODE_FOR_EMAIL3 = -213407;    // 验证码错误
	public static final int EE_AS_CHECK_CODE_FOR_EMAIL4 = -213414;    // 邮箱不存在
	
	public static final int EE_AS_RESET_PWD_BY_EMAIL = -213500;   // 服务响应失败
	public static final int EE_AS_RESET_PWD_BY_EMAIL1 = -213502;   // 接口验证失败
	public static final int EE_AS_RESET_PWD_BY_EMAIL2 = -213503;   // 参数错误
	public static final int EE_AS_RESET_PWD_BY_EMAIL3 = -213513;   // 重置失败
	public static final int EE_AS_RESET_PWD_BY_EMAIL4 = -213514;   //手机号码或邮箱不存在
	
	public static final int EE_CLOUD_DEV_MAC_BACKLIST = -213600;   //101://在黑名单中(mac)
	public static final int EE_CLOUD_DEV_MAC_EXSIT = -213601;    //102://已存在
	public static final int EE_CLOUD_DEV_MAC_EMPTY = -213602;     //104: //mac值为空
	public static final int EE_CLOUD_DEV_MAC_INVALID = -213603;	 //105: //格式不对(mac地址长度不为16位或者有关键字)
	public static final int EE_CLOUD_DEV_MAC_UNREDLIST = -213604;     //107:  //不存在白名单
	public static final int EE_CLOUD_DEV_NAME_EMPTY = -213605;     //109: //设备名不能为空
	public static final int EE_CLOUD_DEV_USERNAME_INVALID = -213606; //111: //设备用户名格式不正确，含关键字
	public static final int EE_CLOUD_DEV_PASSWORD_INVALID = -213607;  //112: //设备密码格式不正确，含关键字
	public static final int EE_CLOUD_DEV_NAME_INVALID = -213608;     //113: //设备名称格式不正确，含关键字
	
	public static final int EE_CLOUD_PARAM_INVALID = -213610;      //10003: //参数异常（dev.mac传入的参数不对）
	public static final int EE_CLOUD_USERNAME_NOTEXIST = -213611; //41001: //用户名不存在(获取设备列表)
	public static final int EE_CLOUD_CHANGEDEVINFO = -213612;     //编辑设备信息失败
	
	public static final int EE_CLOUD_SERVICE_ACTIVATE = -213620;      //10002://开通失败
	public static final int EE_CLOUD_SERVICE_UNAVAILABLE = -213621;    //40001: //没有开通云存储（1、用户不存在；2、用户没有开通 ）
	
	public static final int EE_CLOUD_AUTHENTICATION_FAILURE = -213630 ;    //150000: //验证授权失败（用户名或密码错误）

	
	public static final int EE_AS_CHECK_USER_REGISTE_CODE = -213700;    // 服务响应失败
	public static final int EE_AS_CHECK_USER_REGISTE_CODE1 = -213702;    // 接口验证失败
	public static final int EE_AS_CHECK_USER_REGISTE_CODE2 = -213703;    // 参数错误
	public static final int EE_AS_CHECK_USER_REGISTE_CODE3 = -213706;    // 用户名已被注册

	public static final int EE_CLOUD_UPGRADE_UPDATE = -213800; //成功，需要更新
	public static final int EE_CLOUD_UPGRADE_LASTEST = -213801; //成功，已是最新，无需更新
	public static final int EE_CLOUD_UPGRADE_INVALIDREQ = -213802;//失败，无效请求
	public static final int EE_CLOUD_UPGRADE_UNFINDRES = -213803;   //失败，资源未找到
	public static final int EE_CLOUD_UPGRADE_SERVER = -213804;     //失败，服务器内部错误
	public static final int EE_CLOUD_UPGRADE_SERVER_UNAVAIL = -213805;  //失败，服务器暂时不可用，此时Retry-After指定下次请求的时间

	public static final int EE_AS_SYS_LOGOUT_CODE = -214100; // 服务器向应失败
	public static final int EE_AS_SYS_LOGOUT_CODE1 = -214102; // 接口验证失败
	public static final int EE_AS_SYS_LOGOUT_CODE2 = -214103; // 参数错误

	public static final int EE_AS_SYS_NO_VALIDATED_REGISTER_CODE = -214200; // 服务器响应失败
	public static final int EE_AS_SYS_NO_VALIDATED_REGISTER_CODE1 = -214202; // 接口验证失败
	public static final int EE_AS_SYS_NO_VALIDATED_REGISTER_CODE2 = -214203; // 参数错误
	public static final int EE_AS_SYS_NO_VALIDATED_REGISTER_CODE3 = -214206; // 用户名已被注册
	public static final int EE_AS_SYS_NO_VALIDATED_REGISTER_CODE4 = -214210; // 注册失败

	public static final int EE_AS_SYS_GET_USER_INFO_CODE = -214300; // 服务器响应失败
	public static final int EE_AS_SYS_GET_USER_INFO_CODE1 = -214302; // 接口验证失败
	public static final int EE_AS_SYS_GET_USER_INFO_CODE2 = -214303; // 参数错误
	public static final int EE_AS_SYS_GET_USER_INFO_CODE3 = -214306; // 用户名不存在
	public static final int EE_AS_SYS_GET_USER_INFO_CODE4 = -214310; // 获取失败
	public static final int EE_AS_SYS_GET_USER_INFO_CODE5 = -214313; // 用户密码错误

	public static final int EE_AS_SYS_SEND_BINDING_PHONE_CODE = -214400; // 服务器响应失败
	public static final int EE_AS_SYS_SEND_BINDING_PHONE_CODE1 = -214402; // 接口验证失败
	public static final int EE_AS_SYS_SEND_BINDING_PHONE_CODE2 = -214403; // 参数错误
	public static final int EE_AS_SYS_SEND_BINDING_PHONE_CODE3 = -214404; // 手机号码已被绑定
	public static final int EE_AS_SYS_SEND_BINDING_PHONE_CODE4 = -214405; // 超出短信每天发送次数限制
	public static final int EE_AS_SYS_SEND_BINDING_PHONE_CODE5 = -214406; // 用户名不存在
	public static final int EE_AS_SYS_SEND_BINDING_PHONE_CODE6 = -214410; // 发送失败
	public static final int EE_AS_SYS_SEND_BINDING_PHONE_CODE7 = -214413; // 用户密码错误
	public static final int EE_AS_SYS_SEND_BINDING_PHONE_CODE8 = -214417; // 120秒之内只能发送一次

	public static final int EE_AS_SYS_BINDING_PHONE_CODE = -214500; // 服务器响应失败
	public static final int EE_AS_SYS_BINDING_PHONE_CODE1 = -214502; // 接口验证失败
	public static final int EE_AS_SYS_BINDING_PHONE_CODE2 = -214503; // 参数错误
	public static final int EE_AS_SYS_BINDING_PHONE_CODE3 = -214504; // 手机号码已被绑定
	public static final int EE_AS_SYS_BINDING_PHONE_CODE4 = -214506; // 用户名不存在
	public static final int EE_AS_SYS_BINDING_PHONE_CODE5 = -214507; // 验证码错误
	public static final int EE_AS_SYS_BINDING_PHONE_CODE6 = -214510; // 绑定失败a
	public static final int EE_AS_SYS_BINDING_PHONE_CODE7 = -214513; // 用户密码错误

	public static final int EE_AS_REGISTER_EXTEND_CODE0 =-214802; //接口验证失败
	public static final int EE_AS_REGISTER_EXTEND_CODE1 =-214803; //参数错误
	public static final int EE_AS_REGISTER_EXTEND_CODE2 =-214804; //手机号码已被注册
	public static final int EE_AS_REGISTER_EXTEND_CODE3 =-214806; //用户名已被注册
	public static final int EE_AS_REGISTER_EXTEND_CODE4 =-214807; //注册码验证错误
	public static final int EE_AS_REGISTER_EXTEND_CODE5 =-214810; //注册失败（msg里有失败具体信息）

	public static final int EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE = -214900;    // 服务响应失败
	public static final int EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE1 = -214902;    // 接口验证失败
	public static final int EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE2 = -214903;    // 参数错误
	public static final int EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE3 = -214906;    // 用户名已被注册
	public static final int EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE4 = -214907;    // 注册码验证错误
	public static final int EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE5 = -214908;    // 邮箱已被注册
	public static final int EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE6 = -214910;    // 注册失败

	public static final int EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE = -215000; // 服务器响应失败
	public static final int EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE1 = -215002; // 接口验证失败
	public static final int EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE2 = -215003; // 参数错误
	public static final int EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE3 = -215006; // 用户名已被注册
	public static final int EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE4 = -215010; // 注册失败
	
	public static final int EE_ACCOUNT_HTTP_USERNAME_PWD_ERROR = -604000;     //4000 : 用户名或密码错误

	public static final int EE_ACCOUNT_VERIFICATION_CODE_ERROR = -604010;     //4010 : 验证码错误
	public static final int EE_ACCOUNT_PASSWORD_NOT_SAME = -604011;           //4011 : 密码不一致
	public static final int EE_ACCOUNT_USERNAME_HAS_BEEN_REGISTERED = -604012;//4012 : 用户名已被注册
	public static final int EE_ACCOUNT_USERNAME_IS_EMPTY = -604013;           //4013 : 用户名为空
	public static final int EE_ACCOUNT_PASSWORD_IS_EMPTY = -604014;                    //4014 : 密码为空
	public static final int EE_ACCOUNT_COMFIRMPWD_IS_EMPTY = -604015;                  //4015 : 确认密码为空
	public static final int EE_ACCOUNT_PHONE_IS_EMPTY = -604016;                       //4016 : 手机号为空
	public static final int EE_ACCOUNT_USERNAME_FORMAT_NOT_CORRECT = -604017;          //4017 : 用户名格式不正确
	public static final int EE_ACCOUNT_PASSWORD_FORMAT_NOT_CORRECT = -604018;          //4018 : 密码格式不正确
	public static final int EE_ACCOUNT_COMFIRMPWD_FORMAT_NOT_CORRECT = -604019;        //4019 : 确认密码格式不正确
	public static final int EE_ACCOUNT_PHONE_FORMAT_NOT_CORRECT = -604020;             //4020 : 手机号格式不正确
	public static final int EE_ACCOUNT_PHONE_IS_EXIST = -604021;                       //4021 : 手机号已存在
	public static final int EE_ACCOUNT_PHONE_NOT_EXSIT = -604022;                      //4022 : 手机号不存在
	public static final int EE_ACCOUNT_EMAIL_IS_EXIST = -604023;                       //4023 : 邮箱已存在
	public static final int EE_ACCOUNT_EMAIL_NOT_EXIST = -604024;                      //4024 : 邮箱不存在
	public static final int EE_ACCOUNT_USER_NOT_EXSIT = -604025;                       //4025 : 用户不存在
	public static final int EE_ACCOUNT_OLD_PASSWORD_ERROR = -604026;                   //4026 : 原始密码错误
	public static final int EE_ACCOUNT_MODIFY_PASSWORD_ERROR = -604027;                //4027 : 修改密码失败

	public static final int EE_ACCOUNT_USERID_IS_EMPTY = -604029;               //4029 : 用户ID为空
	public static final int EE_ACCOUNT_VERIFICATION_CODE_IS_EMPTY = -604030;           //4030 : 验证码错误
	public static final int EE_ACCOUNT_EMAIL_IS_EMPTY = -604031;                       //4031 : 邮箱为空
	public static final int EE_ACCOUNT_EMAIL_FORMATE_NOT_CORRECT = -604032;            //4032 : 邮箱格式不正确

	public static final int EE_ACCOUNT_DEVICE_ILLEGAL_NOT_ADD = -604100;        //4100 : 设备非法不允许添加
	public static final int EE_ACCOUNT_DEVICE_ALREADY_EXSIT = -604101;                 //4101 : 设备已经存在
	public static final int EE_ACCOUNT_DEVICE_CHANGE_IFNO_FAIL = -604103;       //4103 : 设备信息修改失败

	public static final int EE_ACCOUNT_SEND_CODE_FAIL  = -604300;                //4300 : 发送失败

	public static final int EE_ACCOUNT_MESSAGE_INTERFACE_CHECK_ERROR  = -604400; //4400 : 短信接口验证失败，请联系我们
	public static final int EE_ACCOUNT_MESSAGE_INTERFACE_PARM_ERROR = -604401;         //4401 : 短信接口参数错误，请联系我们
	public static final int EE_ACCOUNT_MESSAGE_TIME_MORE_THAN_THREE = -604402;         //4402 : 短信发送超过次数，每个手机号一天只能发送三次
	public static final int EE_ACCOUNT_MESSAGE_SEND_ERROR = -604403;                   //4403 : 发送失败，请稍后再试
	public static final int EE_ACCOUNT_MESSAGE_SEND_OFTEN = -604404;                   //4404 : 发送太频繁了，请间隔120秒

	public static final int EE_ACCOUNT_HTTP_SERVER_ERROR = -600900 ;            //5000 : 服务器故障
	public static final int EE_ACCOUNT_CERTIFICATE_NOT_EXIST = -605001;                //5001 : 证书不存在
	public static final int EE_ACCOUNT_HTTP_HEADER_ERROR = -605002;                    //5002 : 请求头信息错误
	public static final int EE_ACCOUNT_CERTIFICATE_FAILURE = -605003;                  //5003 : 证书失效
	public static final int EE_ACCOUNT_ENCRYPT_CHECK_FAILURE = -605004;                //5004 : 生成密钥校验错误
	public static final int EE_ACCOUNT_PARMA_ABNORMAL = -605005;                       //5005 : 参数异常

	
	
	private static Map<Integer, Integer> mErrStrMap = new HashMap<Integer, Integer>();
	
	static {
		mErrStrMap.put(EE_OK, R.string.EE_OK);
		mErrStrMap.put(EE_OBJ_NOT_EXIST, R.string.EE_OBJ_NOT_EXIST);
		mErrStrMap.put(EE_ERROR, R.string.EE_ERROR);
		mErrStrMap.put(EE_PARAM_ERROR, R.string.EE_PARAM_ERROR);
		mErrStrMap.put(EE_CREATE_FILE, R.string.EE_CREATE_FILE);
		mErrStrMap.put(EE_OPEN_FILE, R.string.EE_OPEN_FILE);
		mErrStrMap.put(EE_WRITE_FILE, R.string.EE_WRITE_FILE);
		mErrStrMap.put(EE_READ_FILE, R.string.EE_READ_FILE);
		mErrStrMap.put(EE_NO_SUPPORTED, R.string.EE_NO_SUPPORTED);
		mErrStrMap.put(EE_NET, R.string.EE_NET);
		mErrStrMap.put(EE_DVR_SDK_NOTVALID, R.string.EE_DVR_SDK_NOTVALID);
		mErrStrMap.put(EE_DVR_NO_INIT, R.string.EE_DVR_NO_INIT);
		mErrStrMap.put(EE_DVR_ILLEGAL_PARAM, R.string.EE_DVR_ILLEGAL_PARAM);
		mErrStrMap.put(EE_DVR_INVALID_HANDLE, R.string.EE_DVR_INVALID_HANDLE);
		mErrStrMap.put(EE_DVR_SDK_UNINIT_ERROR, R.string.EE_DVR_SDK_UNINIT_ERROR);
		mErrStrMap.put(EE_DVR_SDK_TIMEOUT, R.string.EE_DVR_SDK_TIMEOUT);
		mErrStrMap.put(EE_DVR_SDK_MEMORY_ERROR, R.string.EE_DVR_SDK_MEMORY_ERROR);
		mErrStrMap.put(EE_DVR_SDK_NET_ERROR, R.string.EE_DVR_SDK_NET_ERROR);
		mErrStrMap.put(EE_DVR_SDK_OPEN_FILE_ERROR, R.string.EE_DVR_SDK_OPEN_FILE_ERROR);
		mErrStrMap.put(EE_DVR_SDK_UNKNOWNERROR, R.string.EE_DVR_SDK_UNKNOWNERROR);
		mErrStrMap.put(EE_DVR_DEV_VER_NOMATCH, R.string.EE_DVR_DEV_VER_NOMATCH);
		mErrStrMap.put(EE_DVR_SDK_NOTSUPPORT, R.string.EE_DVR_SDK_NOTSUPPORT);
		mErrStrMap.put(EE_DVR_OPEN_CHANNEL_ERROR, R.string.EE_DVR_OPEN_CHANNEL_ERROR);
		mErrStrMap.put(EE_DVR_CLOSE_CHANNEL_ERROR, R.string.EE_DVR_CLOSE_CHANNEL_ERROR);
		mErrStrMap.put(EE_DVR_SUB_CONNECT_ERROR, R.string.EE_DVR_SUB_CONNECT_ERROR);
		mErrStrMap.put(EE_DVR_SUB_CONNECT_SEND_ERROR, R.string.EE_DVR_SUB_CONNECT_SEND_ERROR);
		mErrStrMap.put(EE_DVR_NATCONNET_REACHED_MAX, R.string.EE_DVR_NATCONNET_REACHED_MAX);
		mErrStrMap.put(EE_DVR_NOPOWER, R.string.EE_DVR_NOPOWER);
		mErrStrMap.put(EE_DVR_PASSWORD_NOT_VALID, R.string.EE_DVR_PASSWORD_NOT_VALID);
		mErrStrMap.put(EE_DVR_LOGIN_USER_NOEXIST, R.string.EE_DVR_LOGIN_USER_NOEXIST);
		mErrStrMap.put(EE_DVR_USER_LOCKED, R.string.EE_DVR_USER_LOCKED);
		mErrStrMap.put(EE_DVR_USER_IN_BLACKLIST, R.string.EE_DVR_USER_IN_BLACKLIST);
		mErrStrMap.put(EE_DVR_USER_HAS_USED, R.string.EE_DVR_USER_HAS_USED);
		mErrStrMap.put(EE_DVR_USER_NOT_LOGIN, R.string.EE_DVR_USER_NOT_LOGIN);
		mErrStrMap.put(EE_DVR_CONNECT_DEVICE_ERROR, R.string.EE_DVR_CONNECT_DEVICE_ERROR);
		mErrStrMap.put(EE_DVR_ACCOUNT_INPUT_NOT_VALID, R.string.EE_DVR_ACCOUNT_INPUT_NOT_VALID);
		mErrStrMap.put(EE_DVR_ACCOUNT_OVERLAP, R.string.EE_DVR_ACCOUNT_OVERLAP);
		mErrStrMap.put(EE_DVR_ACCOUNT_OBJECT_NONE, R.string.EE_DVR_ACCOUNT_OBJECT_NONE);
		mErrStrMap.put(EE_DVR_ACCOUNT_OBJECT_NOT_VALID, R.string.EE_DVR_ACCOUNT_OBJECT_NOT_VALID);
		mErrStrMap.put(EE_DVR_ACCOUNT_OBJECT_IN_USE, R.string.EE_DVR_ACCOUNT_OBJECT_IN_USE);
		mErrStrMap.put(EE_DVR_ACCOUNT_SUBSET_OVERLAP, R.string.EE_DVR_ACCOUNT_SUBSET_OVERLAP);
		mErrStrMap.put(EE_DVR_ACCOUNT_PWD_NOT_VALID, R.string.EE_DVR_ACCOUNT_PWD_NOT_VALID);
		mErrStrMap.put(EE_DVR_ACCOUNT_PWD_NOT_MATCH, R.string.EE_DVR_ACCOUNT_PWD_NOT_MATCH);
		mErrStrMap.put(EE_DVR_ACCOUNT_RESERVED, R.string.EE_DVR_ACCOUNT_RESERVED);
		mErrStrMap.put(EE_DVR_OPT_RESTART, R.string.EE_DVR_OPT_RESTART);
		mErrStrMap.put(EE_DVR_OPT_REBOOT, R.string.EE_DVR_OPT_REBOOT);
		mErrStrMap.put(EE_DVR_OPT_FILE_ERROR, R.string.EE_DVR_OPT_FILE_ERROR);
		mErrStrMap.put(EE_DVR_OPT_CAPS_ERROR, R.string.EE_DVR_OPT_CAPS_ERROR);
		mErrStrMap.put(EE_DVR_OPT_VALIDATE_ERROR, R.string.EE_DVR_OPT_VALIDATE_ERROR);
		mErrStrMap.put(EE_DVR_OPT_CONFIG_NOT_EXIST, R.string.EE_DVR_OPT_CONFIG_NOT_EXIST);
		mErrStrMap.put(EE_DVR_CTRL_PAUSE_ERROR, R.string.EE_DVR_CTRL_PAUSE_ERROR);
		mErrStrMap.put(EE_DVR_SDK_NOTFOUND, R.string.EE_DVR_SDK_NOTFOUND);
		mErrStrMap.put(EE_DVR_CFG_NOT_ENABLE, R.string.EE_DVR_CFG_NOT_ENABLE);
		mErrStrMap.put(EE_DVR_DECORD_FAIL, R.string.EE_DVR_DECORD_FAIL);
		mErrStrMap.put(EE_DVR_SOCKET_ERROR, R.string.EE_DVR_SOCKET_ERROR);
		mErrStrMap.put(EE_DVR_SOCKET_CONNECT, R.string.EE_DVR_SOCKET_CONNECT);
		mErrStrMap.put(EE_DVR_SOCKET_DOMAIN, R.string.EE_DVR_SOCKET_DOMAIN);
		mErrStrMap.put(EE_DVR_SOCKET_SEND, R.string.EE_DVR_SOCKET_SEND);
		mErrStrMap.put(EE_DVR_ARSP_NO_DEVICE, R.string.EE_DVR_ARSP_NO_DEVICE);
		mErrStrMap.put(EE_DVR_ARSP_BUSING, R.string.EE_DVR_ARSP_BUSING);
		mErrStrMap.put(EE_DVR_ARSP_BUSING_SELECT, R.string.EE_DVR_ARSP_BUSING_SELECT);
		mErrStrMap.put(EE_DVR_ARSP_BUSING_RECVICE, R.string.EE_DVR_ARSP_BUSING_RECVICE);
		mErrStrMap.put(EE_DVR_CONNECTSERVER_ERROR, R.string.EE_DVR_CONNECTSERVER_ERROR);
		mErrStrMap.put(EE_DVR_ARSP_USER_NOEXIST, R.string.EE_DVR_ARSP_USER_NOEXIST);
		mErrStrMap.put(EE_DVR_ARSP_PASSWORD_ERROR, R.string.EE_DVR_ARSP_PASSWORD_ERROR);
		mErrStrMap.put(EE_DVR_ARSP_QUERY_ERROR, R.string.EE_DVR_ARSP_QUERY_ERROR);
		mErrStrMap.put(EE_DVR_CONNECT_FULL, R.string.EE_DVR_CONNECT_FULL);
		mErrStrMap.put(EE_DVR_PIRATESOFTWARE, R.string.EE_DVR_PIRATESOFTWARE);
		mErrStrMap.put(EE_ILLEGAL_PARAM, R.string.EE_ILLEGAL_PARAM);
		mErrStrMap.put(EE_USER_NOEXIST, R.string.EE_USER_NOEXIST);
		mErrStrMap.put(EE_SQL_ERROR, R.string.EE_SQL_ERROR);
		mErrStrMap.put(EE_PASSWORD_NOT_VALID, R.string.EE_PASSWORD_NOT_VALID);
		mErrStrMap.put(EE_USER_NO_DEV, R.string.EE_USER_NO_DEV);
		mErrStrMap.put(EE_USER_DEV_MAC_EXSIT, R.string.EE_USER_DEV_MAC_EXSIT);
		mErrStrMap.put(EE_DSS_NOT_SUP_MAIN, R.string.EE_DSS_NOT_SUP_MAIN);
		mErrStrMap.put(EE_TPS_NOT_SUP_MAIN, R.string.EE_TPS_NOT_SUP_MAIN);
		mErrStrMap.put(EE_MC_NOTFOUND, R.string.EE_MC_NOTFOUND);
		mErrStrMap.put(EE_LINK_SERVER_ERROR, R.string.EE_LINK_SERVER_ERROR);
		mErrStrMap.put(EE_DEV_NOT_LOGIN, R.string.EE_DEV_NOT_LOGIN);
		mErrStrMap.put(EE_AS_PHONE_CODE0, R.string.EE_AS_PHONE_CODE0);
		mErrStrMap.put(EE_AS_PHONE_CODE1, R.string.EE_AS_PHONE_CODE1);
		mErrStrMap.put(EE_AS_PHONE_CODE2, R.string.EE_AS_PHONE_CODE2);
		mErrStrMap.put(EE_AS_PHONE_CODE3, R.string.EE_AS_PHONE_CODE3);
		mErrStrMap.put(EE_AS_PHONE_CODE4, R.string.EE_AS_PHONE_CODE4);
		mErrStrMap.put(EE_AS_PHONE_CODE5, R.string.EE_AS_PHONE_CODE5);
		mErrStrMap.put(EE_AS_REGISTER_CODE0, R.string.EE_AS_REGISTER_CODE0);
		mErrStrMap.put(EE_AS_REGISTER_CODE1, R.string.EE_AS_REGISTER_CODE1);
		mErrStrMap.put(EE_AS_REGISTER_CODE2, R.string.EE_AS_REGISTER_CODE2);
		mErrStrMap.put(EE_AS_REGISTER_CODE3, R.string.EE_AS_REGISTER_CODE3);
		mErrStrMap.put(EE_AS_REGISTER_CODE4, R.string.EE_AS_REGISTER_CODE4);
		mErrStrMap.put(EE_AS_REGISTER_CODE5, R.string.EE_AS_REGISTER_CODE5);
		mErrStrMap.put(EE_AS_LOGIN_CODE1, R.string.EE_AS_LOGIN_CODE1);
		mErrStrMap.put(EE_AS_LOGIN_CODE2, R.string.EE_AS_LOGIN_CODE2);
		mErrStrMap.put(EE_AS_LOGIN_CODE3, R.string.EE_AS_LOGIN_CODE3);
		mErrStrMap.put(EE_AS_EIDIT_PWD_CODE1, R.string.EE_AS_EIDIT_PWD_CODE1);
		mErrStrMap.put(EE_AS_EIDIT_PWD_CODE2, R.string.EE_AS_EIDIT_PWD_CODE2);
		mErrStrMap.put(EE_AS_EIDIT_PWD_CODE3, R.string.EE_AS_EIDIT_PWD_CODE3);
		mErrStrMap.put(EE_AS_EIDIT_PWD_CODE4, R.string.EE_AS_EIDIT_PWD_CODE4);
		mErrStrMap.put(EE_AS_EIDIT_PWD_CODE5, R.string.EE_AS_EIDIT_PWD_CODE5);
		mErrStrMap.put(EE_AS_FORGET_PWD_CODE1, R.string.EE_AS_FORGET_PWD_CODE1);
		mErrStrMap.put(EE_AS_FORGET_PWD_CODE2, R.string.EE_AS_FORGET_PWD_CODE2);
		mErrStrMap.put(EE_AS_FORGET_PWD_CODE3, R.string.EE_AS_FORGET_PWD_CODE3);
		mErrStrMap.put(EE_AS_FORGET_PWD_CODE4, R.string.EE_AS_FORGET_PWD_CODE4);
		mErrStrMap.put(EE_AS_FORGET_PWD_CODE5, R.string.EE_AS_FORGET_PWD_CODE5);
		mErrStrMap.put(EE_AS_RESET_PWD_CODE1, R.string.EE_AS_RESET_PWD_CODE1);
		mErrStrMap.put(EE_AS_RESET_PWD_CODE2, R.string.EE_AS_RESET_PWD_CODE2);
		mErrStrMap.put(EE_AS_RESET_PWD_CODE3, R.string.EE_AS_RESET_PWD_CODE3);
		mErrStrMap.put(EE_AS_RESET_PWD_CODE4, R.string.EE_AS_RESET_PWD_CODE4);
		mErrStrMap.put(EE_AS_RESET_PWD_CODE5, R.string.EE_AS_RESET_PWD_CODE5);
		mErrStrMap.put(EE_AS_CHECK_PWD_CODE1, R.string.EE_AS_CHECK_PWD_CODE1);
		mErrStrMap.put(EE_AS_CHECK_PWD_CODE2, R.string.EE_AS_CHECK_PWD_CODE2);
		mErrStrMap.put(EE_AS_CHECK_PWD_CODE3, R.string.EE_AS_CHECK_PWD_CODE3);
		mErrStrMap.put(EE_AS_CHECK_PWD_CODE4, R.string.EE_AS_CHECK_PWD_CODE4);
		mErrStrMap.put(EE_AS_GET_PUBLIC_DEV_LIST_CODE, R.string.EE_AS_GET_PUBLIC_DEV_LIST_CODE);
		mErrStrMap.put(EE_AS_GET_SHARE_DEV_LIST_CODE, R.string.EE_AS_GET_SHARE_DEV_LIST_CODE);
		mErrStrMap.put(EE_AS_SET_DEV_PUBLIC_CODE, R.string.EE_AS_SET_DEV_PUBLIC_CODE);
		mErrStrMap.put(EE_AS_SHARE_DEV_VIDEO_CODE, R.string.EE_AS_SHARE_DEV_VIDEO_CODE);
		mErrStrMap.put(EE_AS_CANCEL_DEV_PUBLIC_CODE, R.string.EE_AS_CANCEL_DEV_PUBLIC_CODE);
		mErrStrMap.put(EE_AS_CANCEL_SHARE_VIDEO_CODE, R.string.EE_AS_CANCEL_SHARE_VIDEO_CODE);
		mErrStrMap.put(EE_AS_DEV_REGISTER_CODE, R.string.EE_AS_DEV_REGISTER_CODE);
		mErrStrMap.put(EE_AS_SEND_COMMNET_CODE, R.string.EE_AS_SEND_COMMNET_CODE);
		mErrStrMap.put(EE_AS_GET_COMMENT_LIST_CODE, R.string.EE_AS_GET_COMMENT_LIST_CODE);
		mErrStrMap.put(EE_AS_GET_VIDEO_INFO_CODE, R.string.EE_AS_GET_VIDEO_INFO_CODE);
		mErrStrMap.put(EE_AS_UPLOAD_LOCAL_VIDEO_CODE, R.string.EE_AS_UPLOAD_LOCAL_VIDEO_CODE);
		mErrStrMap.put(EE_AS_UPLOAD_LOCAL_VIDEO_CODE1, R.string.EE_AS_UPLOAD_LOCAL_VIDEO_CODE1);
		mErrStrMap.put(EE_AS_GET_SHORT_VIDEO_LIST_CODE, R.string.EE_AS_GET_SHORT_VIDEO_LIST_CODE);
		mErrStrMap.put(EE_AS_EDIT_SHORT_VIDEO_INFO_CODE, R.string.EE_AS_EDIT_SHORT_VIDEO_INFO_CODE);
		mErrStrMap.put(EE_AS_DELETE_SHORT_VIDEO_CODE, R.string.EE_AS_DELETE_SHORT_VIDEO_CODE);
		mErrStrMap.put(EE_AS_SELECT_AUTHCODE_CDOE, R.string.EE_AS_SELECT_AUTHCODE_CDOE);
		mErrStrMap.put(EE_AS_SELECT_AUTHCODE_CDOE1, R.string.EE_AS_SELECT_AUTHCODE_CDOE1);
		mErrStrMap.put(EE_AS_GET_USER_PHOTOS_LIST_CODE, R.string.EE_AS_GET_USER_PHOTOS_LIST_CODE);
		mErrStrMap.put(EE_AS_CREATE_USER_PHOTOS_CODE, R.string.EE_AS_CREATE_USER_PHOTOS_CODE);
		mErrStrMap.put(EE_AS_UPLOAD_PHOTO_CODE, R.string.EE_AS_UPLOAD_PHOTO_CODE);
		mErrStrMap.put(EE_AS_UPLOAD_PHOTO_CODE1, R.string.EE_AS_UPLOAD_PHOTO_CODE1);
		mErrStrMap.put(EE_AS_EDIT_PHOTO_INFO_CODE, R.string.EE_AS_EDIT_PHOTO_INFO_CODE);
		mErrStrMap.put(EE_AS_GET_PHOTO_LIST_CODE, R.string.EE_AS_GET_PHOTO_LIST_CODE);
		mErrStrMap.put(EE_AS_DELETE_PHOTO_CODE, R.string.EE_AS_DELETE_PHOTO_CODE);
		mErrStrMap.put(EE_AS_DELETE_PHOTOS_CODE, R.string.EE_AS_DELETE_PHOTOS_CODE);
		mErrStrMap.put(EE_AS_CHECK_PWD_STRENGTH_CODE, R.string.EE_AS_CHECK_PWD_STRENGTH_CODE);
		mErrStrMap.put(EE_AS_CHECK_PWD_STRENGTH_CODE1, R.string.EE_AS_CHECK_PWD_STRENGTH_CODE1);
		mErrStrMap.put(EE_AS_CHECK_PWD_STRENGTH_CODE2, R.string.EE_AS_CHECK_PWD_STRENGTH_CODE2);
		mErrStrMap.put(EE_AS_CHECK_PWD_STRENGTH_CODE3, R.string.EE_AS_CHECK_PWD_STRENGTH_CODE3);
		mErrStrMap.put(EE_HTTP_PWBYEMAIL_UNFINDNAME, R.string.EE_HTTP_PWBYEMAIL_UNFINDNAME);
		mErrStrMap.put(EE_HTTP_PWBYEMAIL_FAILURE, R.string.EE_HTTP_PWBYEMAIL_FAILURE);
		mErrStrMap.put(EE_AS_SEND_EMAIL_CODE, R.string.EE_AS_SEND_EMAIL_CODE);
		mErrStrMap.put(EE_AS_SEND_EMAIL_CODE1, R.string.EE_AS_SEND_EMAIL_CODE1);
		mErrStrMap.put(EE_AS_SEND_EMAIL_CODE2, R.string.EE_AS_SEND_EMAIL_CODE2);
		mErrStrMap.put(EE_AS_SEND_EMAIL_CODE3, R.string.EE_AS_SEND_EMAIL_CODE3);
		mErrStrMap.put(EE_AS_SEND_EMAIL_CODE4, R.string.EE_AS_SEND_EMAIL_CODE4);
		mErrStrMap.put(EE_AS_REGISTE_BY_EMAIL_CODE, R.string.EE_AS_REGISTE_BY_EMAIL_CODE);
		mErrStrMap.put(EE_AS_REGISTE_BY_EMAIL_CODE1, R.string.EE_AS_REGISTE_BY_EMAIL_CODE1);
		mErrStrMap.put(EE_AS_REGISTE_BY_EMAIL_CODE2, R.string.EE_AS_REGISTE_BY_EMAIL_CODE2);
		mErrStrMap.put(EE_AS_REGISTE_BY_EMAIL_CODE3, R.string.EE_AS_REGISTE_BY_EMAIL_CODE3);
		mErrStrMap.put(EE_AS_REGISTE_BY_EMAIL_CODE4, R.string.EE_AS_REGISTE_BY_EMAIL_CODE4);
		mErrStrMap.put(EE_AS_REGISTE_BY_EMAIL_CODE5, R.string.EE_AS_REGISTE_BY_EMAIL_CODE5);
		mErrStrMap.put(EE_AS_REGISTE_BY_EMAIL_CODE6, R.string.EE_AS_REGISTE_BY_EMAIL_CODE6);
		mErrStrMap.put(EE_AS_SEND_EMAIL_FOR_CODE, R.string.EE_AS_SEND_EMAIL_FOR_CODE);
		mErrStrMap.put(EE_AS_SEND_EMAIL_FOR_CODE1, R.string.EE_AS_SEND_EMAIL_FOR_CODE1);
		mErrStrMap.put(EE_AS_SEND_EMAIL_FOR_CODE3, R.string.EE_AS_SEND_EMAIL_FOR_CODE3);
		mErrStrMap.put(EE_AS_SEND_EMAIL_FOR_CODE4, R.string.EE_AS_SEND_EMAIL_FOR_CODE4);
		mErrStrMap.put(EE_AS_SEND_EMAIL_FOR_CODE5, R.string.EE_AS_SEND_EMAIL_FOR_CODE5);
		mErrStrMap.put(EE_AS_SEND_EMAIL_FOR_CODE6, R.string.EE_AS_SEND_EMAIL_FOR_CODE6);
		mErrStrMap.put(EE_AS_CHECK_CODE_FOR_EMAIL, R.string.EE_AS_CHECK_CODE_FOR_EMAIL);
		mErrStrMap.put(EE_AS_CHECK_CODE_FOR_EMAIL1, R.string.EE_AS_CHECK_CODE_FOR_EMAIL1);
		mErrStrMap.put(EE_AS_CHECK_CODE_FOR_EMAIL2, R.string.EE_AS_CHECK_CODE_FOR_EMAIL2);
		mErrStrMap.put(EE_AS_CHECK_CODE_FOR_EMAIL3, R.string.EE_AS_CHECK_CODE_FOR_EMAIL3);
		mErrStrMap.put(EE_AS_CHECK_CODE_FOR_EMAIL4, R.string.EE_AS_CHECK_CODE_FOR_EMAIL4);
		mErrStrMap.put(EE_AS_RESET_PWD_BY_EMAIL, R.string.EE_AS_RESET_PWD_BY_EMAIL);
		mErrStrMap.put(EE_AS_RESET_PWD_BY_EMAIL1, R.string.EE_AS_RESET_PWD_BY_EMAIL1);
		mErrStrMap.put(EE_AS_RESET_PWD_BY_EMAIL2, R.string.EE_AS_RESET_PWD_BY_EMAIL2);
		mErrStrMap.put(EE_AS_RESET_PWD_BY_EMAIL3, R.string.EE_AS_RESET_PWD_BY_EMAIL3);
		mErrStrMap.put(EE_AS_RESET_PWD_BY_EMAIL4, R.string.EE_AS_RESET_PWD_BY_EMAIL4);
		mErrStrMap.put(EE_CLOUD_DEV_MAC_BACKLIST, R.string.EE_CLOUD_DEV_MAC_BACKLIST);
		mErrStrMap.put(EE_CLOUD_DEV_MAC_EXSIT, R.string.EE_CLOUD_DEV_MAC_EXSIT);
		mErrStrMap.put(EE_CLOUD_DEV_MAC_EMPTY, R.string.EE_CLOUD_DEV_MAC_EMPTY);
		mErrStrMap.put(EE_CLOUD_DEV_MAC_INVALID, R.string.EE_CLOUD_DEV_MAC_INVALID);
		mErrStrMap.put(EE_CLOUD_DEV_MAC_UNREDLIST, R.string.EE_CLOUD_DEV_MAC_UNREDLIST);
		mErrStrMap.put(EE_CLOUD_DEV_NAME_EMPTY, R.string.EE_CLOUD_DEV_NAME_EMPTY);
		mErrStrMap.put(EE_CLOUD_DEV_USERNAME_INVALID, R.string.EE_CLOUD_DEV_USERNAME_INVALID);
		mErrStrMap.put(EE_CLOUD_DEV_PASSWORD_INVALID, R.string.EE_CLOUD_DEV_PASSWORD_INVALID);
		mErrStrMap.put(EE_CLOUD_DEV_NAME_INVALID, R.string.EE_CLOUD_DEV_NAME_INVALID);
		mErrStrMap.put(EE_CLOUD_PARAM_INVALID, R.string.EE_CLOUD_PARAM_INVALID);
		mErrStrMap.put(EE_CLOUD_USERNAME_NOTEXIST, R.string.EE_CLOUD_USERNAME_NOTEXIST);
		mErrStrMap.put(EE_CLOUD_CHANGEDEVINFO, R.string.EE_CLOUD_CHANGEDEVINFO);
		mErrStrMap.put(EE_CLOUD_SERVICE_ACTIVATE, R.string.EE_CLOUD_SERVICE_ACTIVATE);
		mErrStrMap.put(EE_CLOUD_SERVICE_UNAVAILABLE, R.string.EE_CLOUD_SERVICE_UNAVAILABLE);
		mErrStrMap.put(EE_CLOUD_AUTHENTICATION_FAILURE, R.string.EE_CLOUD_AUTHENTICATION_FAILURE);
		mErrStrMap.put(EE_AS_CHECK_USER_REGISTE_CODE, R.string.EE_AS_CHECK_USER_REGISTE_CODE);
		mErrStrMap.put(EE_AS_CHECK_USER_REGISTE_CODE1, R.string.EE_AS_CHECK_USER_REGISTE_CODE1);
		mErrStrMap.put(EE_AS_CHECK_USER_REGISTE_CODE2, R.string.EE_AS_CHECK_USER_REGISTE_CODE2);
		mErrStrMap.put(EE_AS_CHECK_USER_REGISTE_CODE3, R.string.EE_AS_CHECK_USER_REGISTE_CODE3);
		mErrStrMap.put(EE_CLOUD_UPGRADE_UPDATE, R.string.EE_CLOUD_UPGRADE_UPDATE);
		mErrStrMap.put(EE_CLOUD_UPGRADE_LASTEST, R.string.EE_CLOUD_UPGRADE_LASTEST);
		mErrStrMap.put(EE_CLOUD_UPGRADE_INVALIDREQ, R.string.EE_CLOUD_UPGRADE_INVALIDREQ);
		mErrStrMap.put(EE_CLOUD_UPGRADE_UNFINDRES, R.string.EE_CLOUD_UPGRADE_UNFINDRES);
		mErrStrMap.put(EE_CLOUD_UPGRADE_SERVER, R.string.EE_CLOUD_UPGRADE_SERVER);
		mErrStrMap.put(EE_CLOUD_UPGRADE_SERVER_UNAVAIL, R.string.EE_CLOUD_UPGRADE_SERVER_UNAVAIL);
		mErrStrMap.put(EE_AS_SYS_LOGOUT_CODE, R.string.EE_AS_SYS_LOGOUT_CODE);
		mErrStrMap.put(EE_AS_SYS_LOGOUT_CODE1, R.string.EE_AS_SYS_LOGOUT_CODE1);
		mErrStrMap.put(EE_AS_SYS_LOGOUT_CODE2, R.string.EE_AS_SYS_LOGOUT_CODE2);
		mErrStrMap.put(EE_AS_SYS_NO_VALIDATED_REGISTER_CODE, R.string.EE_AS_SYS_NO_VALIDATED_REGISTER_CODE);
		mErrStrMap.put(EE_AS_SYS_NO_VALIDATED_REGISTER_CODE1, R.string.EE_AS_SYS_NO_VALIDATED_REGISTER_CODE1);
		mErrStrMap.put(EE_AS_SYS_NO_VALIDATED_REGISTER_CODE2, R.string.EE_AS_SYS_NO_VALIDATED_REGISTER_CODE2);
		mErrStrMap.put(EE_AS_SYS_NO_VALIDATED_REGISTER_CODE3, R.string.EE_AS_SYS_NO_VALIDATED_REGISTER_CODE3);
		mErrStrMap.put(EE_AS_SYS_NO_VALIDATED_REGISTER_CODE4, R.string.EE_AS_SYS_NO_VALIDATED_REGISTER_CODE4);
		mErrStrMap.put(EE_AS_SYS_GET_USER_INFO_CODE, R.string.EE_AS_SYS_GET_USER_INFO_CODE);
		mErrStrMap.put(EE_AS_SYS_GET_USER_INFO_CODE1, R.string.EE_AS_SYS_GET_USER_INFO_CODE1);
		mErrStrMap.put(EE_AS_SYS_GET_USER_INFO_CODE2, R.string.EE_AS_SYS_GET_USER_INFO_CODE2);
		mErrStrMap.put(EE_AS_SYS_GET_USER_INFO_CODE3, R.string.EE_AS_SYS_GET_USER_INFO_CODE3);
		mErrStrMap.put(EE_AS_SYS_GET_USER_INFO_CODE4, R.string.EE_AS_SYS_GET_USER_INFO_CODE4);
		mErrStrMap.put(EE_AS_SYS_GET_USER_INFO_CODE5, R.string.EE_AS_SYS_GET_USER_INFO_CODE5);
		mErrStrMap.put(EE_AS_SYS_SEND_BINDING_PHONE_CODE, R.string.EE_AS_SYS_SEND_BINDING_PHONE_CODE);
		mErrStrMap.put(EE_AS_SYS_SEND_BINDING_PHONE_CODE1, R.string.EE_AS_SYS_SEND_BINDING_PHONE_CODE1);
		mErrStrMap.put(EE_AS_SYS_SEND_BINDING_PHONE_CODE2, R.string.EE_AS_SYS_SEND_BINDING_PHONE_CODE2);
		mErrStrMap.put(EE_AS_SYS_SEND_BINDING_PHONE_CODE3, R.string.EE_AS_SYS_SEND_BINDING_PHONE_CODE3);
		mErrStrMap.put(EE_AS_SYS_SEND_BINDING_PHONE_CODE4, R.string.EE_AS_SYS_SEND_BINDING_PHONE_CODE4);
		mErrStrMap.put(EE_AS_SYS_SEND_BINDING_PHONE_CODE5, R.string.EE_AS_SYS_SEND_BINDING_PHONE_CODE5);
		mErrStrMap.put(EE_AS_SYS_SEND_BINDING_PHONE_CODE6, R.string.EE_AS_SYS_SEND_BINDING_PHONE_CODE6);
		mErrStrMap.put(EE_AS_SYS_SEND_BINDING_PHONE_CODE7, R.string.EE_AS_SYS_SEND_BINDING_PHONE_CODE7);
		mErrStrMap.put(EE_AS_SYS_SEND_BINDING_PHONE_CODE8, R.string.EE_AS_SYS_SEND_BINDING_PHONE_CODE8);
		mErrStrMap.put(EE_AS_SYS_BINDING_PHONE_CODE, R.string.EE_AS_SYS_BINDING_PHONE_CODE);
		mErrStrMap.put(EE_AS_SYS_BINDING_PHONE_CODE1, R.string.EE_AS_SYS_BINDING_PHONE_CODE1);
		mErrStrMap.put(EE_AS_SYS_BINDING_PHONE_CODE2, R.string.EE_AS_SYS_BINDING_PHONE_CODE2);
		mErrStrMap.put(EE_AS_SYS_BINDING_PHONE_CODE3, R.string.EE_AS_SYS_BINDING_PHONE_CODE3);
		mErrStrMap.put(EE_AS_SYS_BINDING_PHONE_CODE4, R.string.EE_AS_SYS_BINDING_PHONE_CODE4);
		mErrStrMap.put(EE_AS_SYS_BINDING_PHONE_CODE5, R.string.EE_AS_SYS_BINDING_PHONE_CODE5);
		mErrStrMap.put(EE_AS_SYS_BINDING_PHONE_CODE6, R.string.EE_AS_SYS_BINDING_PHONE_CODE6);
		mErrStrMap.put(EE_AS_SYS_BINDING_PHONE_CODE7, R.string.EE_AS_SYS_BINDING_PHONE_CODE7);
		mErrStrMap.put(EE_AS_REGISTER_EXTEND_CODE0, R.string.EE_AS_REGISTER_EXTEND_CODE0);
		mErrStrMap.put(EE_AS_REGISTER_EXTEND_CODE1, R.string.EE_AS_REGISTER_EXTEND_CODE1);
		mErrStrMap.put(EE_AS_REGISTER_EXTEND_CODE2, R.string.EE_AS_REGISTER_EXTEND_CODE2);
		mErrStrMap.put(EE_AS_REGISTER_EXTEND_CODE3, R.string.EE_AS_REGISTER_EXTEND_CODE3);
		mErrStrMap.put(EE_AS_REGISTER_EXTEND_CODE4, R.string.EE_AS_REGISTER_EXTEND_CODE4);
		mErrStrMap.put(EE_AS_REGISTER_EXTEND_CODE5, R.string.EE_AS_REGISTER_EXTEND_CODE5);
		mErrStrMap.put(EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE, R.string.EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE);
		mErrStrMap.put(EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE1, R.string.EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE1);
		mErrStrMap.put(EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE2, R.string.EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE2);
		mErrStrMap.put(EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE3, R.string.EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE3);
		mErrStrMap.put(EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE4, R.string.EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE4);
		mErrStrMap.put(EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE5, R.string.EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE5);
		mErrStrMap.put(EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE6, R.string.EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE6);
		mErrStrMap.put(EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE, R.string.EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE);
		mErrStrMap.put(EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE1, R.string.EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE1);
		mErrStrMap.put(EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE2, R.string.EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE2);
		mErrStrMap.put(EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE3, R.string.EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE3);
		mErrStrMap.put(EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE4, R.string.EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE4);
		mErrStrMap.put(EE_ACCOUNT_HTTP_USERNAME_PWD_ERROR, R.string.EE_ACCOUNT_HTTP_USERNAME_PWD_ERROR);
		mErrStrMap.put(EE_ACCOUNT_VERIFICATION_CODE_ERROR, R.string.EE_ACCOUNT_VERIFICATION_CODE_ERROR);
		mErrStrMap.put(EE_ACCOUNT_PASSWORD_NOT_SAME, R.string.EE_ACCOUNT_PASSWORD_NOT_SAME);
		mErrStrMap.put(EE_ACCOUNT_USERNAME_HAS_BEEN_REGISTERED, R.string.EE_ACCOUNT_USERNAME_HAS_BEEN_REGISTERED);
		mErrStrMap.put(EE_ACCOUNT_USERNAME_IS_EMPTY, R.string.EE_ACCOUNT_USERNAME_IS_EMPTY);
		mErrStrMap.put(EE_ACCOUNT_PASSWORD_IS_EMPTY, R.string.EE_ACCOUNT_PASSWORD_IS_EMPTY);
		mErrStrMap.put(EE_ACCOUNT_COMFIRMPWD_IS_EMPTY, R.string.EE_ACCOUNT_COMFIRMPWD_IS_EMPTY);
		mErrStrMap.put(EE_ACCOUNT_PHONE_IS_EMPTY, R.string.EE_ACCOUNT_PHONE_IS_EMPTY);
		mErrStrMap.put(EE_ACCOUNT_USERNAME_FORMAT_NOT_CORRECT, R.string.EE_ACCOUNT_USERNAME_FORMAT_NOT_CORRECT);
		mErrStrMap.put(EE_ACCOUNT_PASSWORD_FORMAT_NOT_CORRECT, R.string.EE_ACCOUNT_PASSWORD_FORMAT_NOT_CORRECT);
		mErrStrMap.put(EE_ACCOUNT_COMFIRMPWD_FORMAT_NOT_CORRECT, R.string.EE_ACCOUNT_COMFIRMPWD_FORMAT_NOT_CORRECT);
		mErrStrMap.put(EE_ACCOUNT_PHONE_FORMAT_NOT_CORRECT, R.string.EE_ACCOUNT_PHONE_FORMAT_NOT_CORRECT);
		mErrStrMap.put(EE_ACCOUNT_PHONE_IS_EXIST, R.string.EE_ACCOUNT_PHONE_IS_EXIST);
		mErrStrMap.put(EE_ACCOUNT_PHONE_NOT_EXSIT, R.string.EE_ACCOUNT_PHONE_NOT_EXSIT);
		mErrStrMap.put(EE_ACCOUNT_EMAIL_IS_EXIST, R.string.EE_ACCOUNT_EMAIL_IS_EXIST);
		mErrStrMap.put(EE_ACCOUNT_EMAIL_NOT_EXIST, R.string.EE_ACCOUNT_EMAIL_NOT_EXIST);
		mErrStrMap.put(EE_ACCOUNT_USER_NOT_EXSIT, R.string.EE_ACCOUNT_USER_NOT_EXSIT);
		mErrStrMap.put(EE_ACCOUNT_OLD_PASSWORD_ERROR, R.string.EE_ACCOUNT_OLD_PASSWORD_ERROR);
		mErrStrMap.put(EE_ACCOUNT_MODIFY_PASSWORD_ERROR, R.string.EE_ACCOUNT_MODIFY_PASSWORD_ERROR);
		mErrStrMap.put(EE_ACCOUNT_USERID_IS_EMPTY, R.string.EE_ACCOUNT_USERID_IS_EMPTY);
		mErrStrMap.put(EE_ACCOUNT_VERIFICATION_CODE_IS_EMPTY, R.string.EE_ACCOUNT_VERIFICATION_CODE_IS_EMPTY);
		mErrStrMap.put(EE_ACCOUNT_EMAIL_IS_EMPTY, R.string.EE_ACCOUNT_EMAIL_IS_EMPTY);
		mErrStrMap.put(EE_ACCOUNT_EMAIL_FORMATE_NOT_CORRECT, R.string.EE_ACCOUNT_EMAIL_FORMATE_NOT_CORRECT);
		mErrStrMap.put(EE_ACCOUNT_DEVICE_ILLEGAL_NOT_ADD, R.string.EE_ACCOUNT_DEVICE_ILLEGAL_NOT_ADD);
		mErrStrMap.put(EE_ACCOUNT_DEVICE_ALREADY_EXSIT, R.string.EE_ACCOUNT_DEVICE_ALREADY_EXSIT);
		mErrStrMap.put(EE_ACCOUNT_DEVICE_CHANGE_IFNO_FAIL, R.string.EE_ACCOUNT_DEVICE_CHANGE_IFNO_FAIL);
		mErrStrMap.put(EE_ACCOUNT_SEND_CODE_FAIL, R.string.EE_ACCOUNT_SEND_CODE_FAIL);
		mErrStrMap.put(EE_ACCOUNT_MESSAGE_INTERFACE_CHECK_ERROR, R.string.EE_ACCOUNT_MESSAGE_INTERFACE_CHECK_ERROR);
		mErrStrMap.put(EE_ACCOUNT_MESSAGE_INTERFACE_PARM_ERROR, R.string.EE_ACCOUNT_MESSAGE_INTERFACE_PARM_ERROR);
		mErrStrMap.put(EE_ACCOUNT_MESSAGE_TIME_MORE_THAN_THREE, R.string.EE_ACCOUNT_MESSAGE_TIME_MORE_THAN_THREE);
		mErrStrMap.put(EE_ACCOUNT_MESSAGE_SEND_ERROR, R.string.EE_ACCOUNT_MESSAGE_SEND_ERROR);
		mErrStrMap.put(EE_ACCOUNT_MESSAGE_SEND_OFTEN, R.string.EE_ACCOUNT_MESSAGE_SEND_OFTEN);
		mErrStrMap.put(EE_ACCOUNT_HTTP_SERVER_ERROR, R.string.EE_ACCOUNT_HTTP_SERVER_ERROR);
		mErrStrMap.put(EE_ACCOUNT_CERTIFICATE_NOT_EXIST, R.string.EE_ACCOUNT_CERTIFICATE_NOT_EXIST);
		mErrStrMap.put(EE_ACCOUNT_HTTP_HEADER_ERROR, R.string.EE_ACCOUNT_HTTP_HEADER_ERROR);
		mErrStrMap.put(EE_ACCOUNT_CERTIFICATE_FAILURE, R.string.EE_ACCOUNT_CERTIFICATE_FAILURE);
		mErrStrMap.put(EE_ACCOUNT_ENCRYPT_CHECK_FAILURE, R.string.EE_ACCOUNT_ENCRYPT_CHECK_FAILURE);
		mErrStrMap.put(EE_ACCOUNT_PARMA_ABNORMAL, R.string.EE_ACCOUNT_PARMA_ABNORMAL);
	}
	
	public static String getErrorStr(Integer errCode) {
		Integer errStrId = mErrStrMap.get(errCode);
		return "Unknown Error [" + errCode + "]";
	}
}
