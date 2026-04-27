# Error Code
EE_NET = -99993; // Network error

// Error value enumeration used in this interface
EE_DVR_SDK_NOTVALID = -10000; // Invalid request
EE_DVR_NO_INIT = -10001; // SDK not initialized
EE_DVR_ILLEGAL_PARAM = -10002; // Invalid user parameter
EE_DVR_INVALID_HANDLE = -10003; // Invalid handle
EE_DVR_SDK_UNINIT_ERROR = -10004; // SDK cleanup error
EE_DVR_SDK_TIMEOUT = -10005; // Timeout
EE_DVR_SDK_MEMORY_ERROR = -10006; // Memory error, failed to allocate memory
EE_DVR_SDK_NET_ERROR = -10007; // Network error
EE_DVR_SDK_OPEN_FILE_ERROR = -10008; // Failed to open file
EE_DVR_SDK_UNKNOWNERROR = -10009; // Unknown error
EE_DVR_DEV_VER_NOMATCH = -11000; // Received data is incorrect, possibly version mismatch
EE_DVR_SDK_NOTSUPPORT = -11001; // Version not supported

EE_DVR_OPEN_CHANNEL_ERROR = -11200; // Failed to open channel
EE_DVR_CLOSE_CHANNEL_ERROR = -11201; // Failed to close channel
EE_DVR_SUB_CONNECT_ERROR = -11202; // Failed to establish media sub-connection
EE_DVR_SUB_CONNECT_SEND_ERROR = -11203; // Media sub-connection communication failed
EE_DVR_NATCONNET_REACHED_MAX = -11204; // Maximum number of Nat video connections reached, no new Nat video connections allowed

// User management error codes
EE_DVR_NOPOWER = -11300; // No permission
EE_DVR_PASSWORD_NOT_VALID = -11301; // Incorrect account password
EE_DVR_LOGIN_USER_NOEXIST = -11302; // User does not exist
EE_DVR_USER_LOCKED = -11303; // User is locked
EE_DVR_USER_IN_BLACKLIST = -11304; // User is not allowed to access (in blacklist)
EE_DVR_USER_HAS_USED = -11305; // User has logged in
EE_DVR_USER_NOT_LOGIN = -11306; // User is not logged in
EE_DVR_CONNECT_DEVICE_ERROR = -11307; // Device may be offline
EE_DVR_ACCOUNT_INPUT_NOT_VALID = -11308; // Invalid user management input
EE_DVR_ACCOUNT_OVERLAP = -11309; // Duplicate index
EE_DVR_ACCOUNT_OBJECT_NONE = -11310; // Object does not exist; used for querying
EE_DVR_ACCOUNT_OBJECT_NOT_VALID = -11311; // Object does not exist
EE_DVR_ACCOUNT_OBJECT_IN_USE = -11312; // Object is currently in use
EE_DVR_ACCOUNT_SUBSET_OVERLAP = -11313; // Subset exceeds range (e.g., group permissions exceed permission table, user permissions exceed group permission range, etc.)
EE_DVR_ACCOUNT_PWD_NOT_VALID = -11314; // Incorrect password
EE_DVR_ACCOUNT_PWD_NOT_MATCH = -11315; // Passwords do not match
EE_DVR_ACCOUNT_RESERVED = -11316; // Reserved account

// Configuration management related error codes
EE_DVR_OPT_RESTART = -11400; // Application restart required after saving configuration
EE_DVR_OPT_REBOOT = -11401; // System reboot required
EE_DVR_OPT_FILE_ERROR = -11402; // Error writing file
EE_DVR_OPT_CAPS_ERROR = -11403; // Configuration feature not supported
EE_DVR_OPT_VALIDATE_ERROR = -11404; // Configuration validation failed
EE_DVR_OPT_CONFIG_NOT_EXIST = -11405; // Requested or set configuration does not exist

//
EE_DVR_CTRL_PAUSE_ERROR = -11500; // Pause failed
EE_DVR_SDK_NOTFOUND = -11501; // Search failed, corresponding file not found
EE_DVR_CFG_NOT_ENABLE = -11502; // Configuration not enabled
EE_DVR_DECORD_FAIL = -11503; // Decoding failed

// DNS protocol resolution error codes
EE_DVR_SOCKET_ERROR = -11600; // Failed to create socket
EE_DVR_SOCKET_CONNECT = -11601; // Failed to connect socket
EE_DVR_SOCKET_DOMAIN = -11602; // Domain name resolution failed
EE_DVR_SOCKET_SEND = -11603; // Failed to send data
EE_DVR_ARSP_NO_DEVICE = -11604; // Failed to obtain device information, device may not be online
EE_DVR_ARSP_BUSING = -11605; // ARSP service busy
EE_DVR_ARSP_BUSING_SELECT = -11606; // ARSP service busy; select failed
EE_DVR_ARSP_BUSING_RECVICE = -11607; // ARSP service busy; receive failed
EE_DVR_CONNECTSERVER_ERROR = -11608; // Failed to connect to server
EE_DVR_ARSP_USER_NOEXIST = -11609; // User does not exist
EE_DVR_ARSP_PASSWORD_ERROR = -11610; // Incorrect account password
EE_DVR_ARSP_QUERY_ERROR = -11611; // Query failed
EE_DVR_CONNECT_FULL = -11612; // Server connection limit reached

// Copyright-related
EE_DVR_PIRATESOFTWARE = -11700; // Device piracy

EE_ILLEGAL_PARAM = -200000; // Invalid parameter
EE_USER_NOEXIST = -100001; // User does not exist
EE_SQL_ERROR = -100002; // SQL operation failed
EE_PASSWORD_NOT_VALID = -100003; // Incorrect password
EE_USER_NO_DEV = -100004; // User does not have access to the device
EE_DEV_NOT_LOGIN = -100005; // Login failed

EE_USER_DEV_MAC_EXSIT = -200004; // Device already exists, same as EE_CLOUD_DEV_MAC_EXSIT

EE_MC_NOTFOUND = -201110; // Search failed, corresponding file not found
EE_LINK_SERVER_ERROR = -201118; // Failed to connect to the server

EE_AS_PHONE_CODE0 = -210002; // Interface verification failed
EE_AS_PHONE_CODE1 = -210003; // Parameter error
EE_AS_PHONE_CODE2 = -210004; // Phone number has been registered
EE_AS_PHONE_CODE3 = -210005; // Exceeded the daily limit of SMS sending (each number can only send registration verification messages three times a day)
EE_AS_PHONE_CODE4 = -210010; // Sending failed
EE_AS_PHONE_CODE5 = -210017; // Can only send once within 120 seconds

EE_AS_REGISTER_CODE0 = -210102; // Interface verification failed
EE_AS_REGISTER_CODE1 = -210103; // Parameter error
EE_AS_REGISTER_CODE2 = -210104; // Phone number has been registered
EE_AS_REGISTER_CODE3 = -210106; // Username has been registered
EE_AS_REGISTER_CODE4 = -210107; // Registration code verification error
EE_AS_REGISTER_CODE5 = -210110; // Registration failed (specific failure information in msg)

EE_AS_LOGIN_CODE1 = -210202; // Interface verification failed
EE_AS_LOGIN_CODE2 = -210203; // Parameter error
EE_AS_LOGIN_CODE3 = -210210; // Login failed

EE_AS_EIDIT_PWD_CODE1 = -210302; // Interface verification failed
EE_AS_EIDIT_PWD_CODE2 = -210303; // Parameter error
EE_AS_EIDIT_PWD_CODE3 = -210311; // New password does not meet the requirements
EE_AS_EIDIT_PWD_CODE4 = -210313; // Incorrect original password
EE_AS_EIDIT_PWD_CODE5 = -210315; // Please enter a new password different from the original password

EE_AS_FORGET_PWD_CODE1 = -210402; // Interface verification failed
EE_AS_FORGET_PWD_CODE2 = -210403; // Parameter error
EE_AS_FORGET_PWD_CODE3 = -210405; // Exceeded the daily limit of SMS sending (each number can only send registration verification messages three times a day)
EE_AS_FORGET_PWD_CODE4 = -210410; // Sending failed (specific failure information in msg)
EE_AS_FORGET_PWD_CODE5 = -210414; // Phone number does not exist

EE_AS_RESET_PWD_CODE1 = -210502; // Interface verification failed
EE_AS_RESET_PWD_CODE2 = -210503; // Parameter error
EE_AS_RESET_PWD_CODE3 = -210511; // New password does not meet the requirements
EE_AS_RESET_PWD_CODE4 = -210512; // The two entered new passwords do not match
EE_AS_RESET_PWD_CODE5 = -210514; // Phone number does not exist

EE_AS_CHECK_PWD_CODE1 = -210602; // Interface verification failed
EE_AS_CHECK_PWD_CODE2 = -210603; // Parameter error
EE_AS_CHECK_PWD_CODE3 = -210607; // Incorrect verification code
EE_AS_CHECK_PWD_CODE4 = -210614; // Phone number does not exist

EE_HTTP_PWBYEMAIL_UNFINDNAME = -213000; // No such username
EE_HTTP_PWBYEMAIL_FAILURE = -213001; // Sending failed

EE_AS_SEND_EMAIL_CODE = -213100; // Service response failed
EE_AS_SEND_EMAIL_CODE1 = -213102; // Interface verification failed
EE_AS_SEND_EMAIL_CODE2 = -213103; // Parameter error
EE_AS_SEND_EMAIL_CODE3 = -213108; // Email has been registered
EE_AS_SEND_EMAIL_CODE4 = -213110; // Sending failed

EE_AS_REGISTE_BY_EMAIL_CODE = -213200; // Service response failed
EE_AS_REGISTE_BY_EMAIL_CODE1 = -213202; // Interface verification failed
EE_AS_REGISTE_BY_EMAIL_CODE2 = -213203; // Parameter error
EE_AS_REGISTE_BY_EMAIL_CODE3 = -213206; // Username has been registered
EE_AS_REGISTE_BY_EMAIL_CODE4 = -213207; // Registration code verification error
EE_AS_REGISTE_BY_EMAIL_CODE5 = -213208; // Email has been registered
EE_AS_REGISTE_BY_EMAIL_CODE6 = -213210; // Registration failed

EE_AS_SEND_EMAIL_FOR_CODE = -213300; // Service response failed
EE_AS_SEND_EMAIL_FOR_CODE1 = -213302; // Interface verification failed
EE_AS_SEND_EMAIL_FOR_CODE3 = -213303; // Parameter error
EE_AS_SEND_EMAIL_FOR_CODE4 = -213310; // Sending failed
EE_AS_SEND_EMAIL_FOR_CODE5 = -213314; // Email does not exist
EE_AS_SEND_EMAIL_FOR_CODE6 = -213316; // Email and username do not match

EE_AS_CHECK_CODE_FOR_EMAIL = -213400; // Service response failed
EE_AS_CHECK_CODE_FOR_EMAIL1 = -213402; // Interface verification failed
EE_AS_CHECK_CODE_FOR_EMAIL2 = -213403; // Parameter error
EE_AS_CHECK_CODE_FOR_EMAIL3 = -213407; // Incorrect verification code
EE_AS_CHECK_CODE_FOR_EMAIL4 = -213414; // Email does not exist

EE_AS_RESET_PWD_BY_EMAIL = -213500; // Service response failed
EE_AS_RESET_PWD_BY_EMAIL1 = -213502; // Interface verification failed
EE_AS_RESET_PWD_BY_EMAIL2 = -213503; // Parameter error
EE_AS_RESET_PWD_BY_EMAIL3 = -213513; // Reset failed
EE_AS_RESET_PWD_BY_EMAIL4 = -213514; // Phone number or email does not exist

EE_CLOUD_DEV_MAC_BACKLIST = -213600; //101: In the blacklist (mac)
EE_CLOUD_DEV_MAC_EXSIT = -213601; //102: Already exists
EE_CLOUD_DEV_MAC_EMPTY = -213602; //104: MAC value is empty
EE_CLOUD_DEV_MAC_INVALID = -213603; //105: Invalid format (MAC address length is not 16 characters or contains keywords)
EE_CLOUD_DEV_MAC_UNREDLIST = -213604; //107: Not in the whitelist
EE_CLOUD_DEV_NAME_EMPTY = -213605; //109: Device name cannot be empty
EE_CLOUD_DEV_USERNAME_INVALID = -213606; //111: Incorrect device username format, contains keywords
EE_CLOUD_DEV_PASSWORD_INVALID = -213607; //112: Incorrect device password format, contains keywords
EE_CLOUD_DEV_NAME_INVALID = -213608; //113: Incorrect device name format, contains keywords

EE_CLOUD_PARAM_INVALID = -213610; //10003: Parameter exception (incorrect dev.mac parameter)
EE_CLOUD_USERNAME_NOTEXIST = -213611; //41001: User does not exist (get device list)
EE_CLOUD_CHANGEDEVINFO = -213612; //Failed to edit device information

EE_CLOUD_SERVICE_ACTIVATE = -213620; //10002: Activation failed
EE_CLOUD_SERVICE_UNAVAILABLE = -213621; //40001: Cloud storage not available (1. User does not exist; 2. User has not activated)

EE_CLOUD_AUTHENTICATION_FAILURE = -213630; //150000: Authentication verification failed (incorrect username or password)

EE_AS_CHECK_USER_REGISTE_CODE = -213700; // Service response failed
EE_AS_CHECK_USER_REGISTE_CODE1 = -213702; // Interface verification failed
EE_AS_CHECK_USER_REGISTE_CODE2 = -213703; // Parameter error
EE_AS_CHECK_USER_REGISTE_CODE3 = -213706; // Username has been registered

EE_CLOUD_UPGRADE_UPDATE = -213800; // Success, update required
EE_CLOUD_UPGRADE_LASTEST = -213801; // Success, already up to date, no update required
EE_CLOUD_UPGRADE_INVALIDREQ = -213802; // Failed, invalid request
EE_CLOUD_UPGRADE_UNFINDRES = -213803; // Failed, resource not found
EE_CLOUD_UPGRADE_SERVER = -213804; // Failed, internal server error
EE_CLOUD_UPGRADE_SERVER_UNAVAIL = -213805; // Failed, server temporarily unavailable, Retry-After specifies the time for the next request

EE_AS_SYS_LOGOUT_CODE = -214100; // Server response failed
EE_AS_SYS_LOGOUT_CODE1 = -214102; // Interface verification failed
EE_AS_SYS_LOGOUT_CODE2 = -214103; // Parameter error

EE_AS_SYS_NO_VALIDATED_REGISTER_CODE = -214200; // Server response failed
EE_AS_SYS_NO_VALIDATED_REGISTER_CODE1 = -214202; // Interface verification failed
EE_AS_SYS_NO_VALIDATED_REGISTER_CODE2 = -214203; // Parameter error
EE_AS_SYS_NO_VALIDATED_REGISTER_CODE3 = -214206; // Username has been registered
EE_AS_SYS_NO_VALIDATED_REGISTER_CODE4 = -214210; // Registration failed

EE_AS_SYS_GET_USER_INFO_CODE = -214300; // Server response failed
EE_AS_SYS_GET_USER_INFO_CODE1 = -214302; // Interface verification failed
EE_AS_SYS_GET_USER_INFO_CODE2 = -214303; // Parameter error
EE_AS_SYS_GET_USER_INFO_CODE3 = -214306; // Username does not exist
EE_AS_SYS_GET_USER_INFO_CODE4 = -214310; // Failed to retrieve
EE_AS_SYS_GET_USER_INFO_CODE5 = -214313; // Incorrect user password

EE_AS_SYS_SEND_BINDING_PHONE_CODE = -214400; // Server response failed
EE_AS_SYS_SEND_BINDING_PHONE_CODE1 = -214402; // Interface verification failed
EE_AS_SYS_SEND_BINDING_PHONE_CODE2 = -214403; // Parameter error
EE_AS_SYS_SEND_BINDING_PHONE_CODE3 = -214404; // Phone number already bound
EE_AS_SYS_SEND_BINDING_PHONE_CODE4 = -214405; // Exceeded the daily limit of SMS sending
EE_AS_SYS_SEND_BINDING_PHONE_CODE5 = -214406; // Username does not exist
EE_AS_SYS_SEND_BINDING_PHONE_CODE6 = -214410; // Sending failed
EE_AS_SYS_SEND_BINDING_PHONE_CODE7 = -214413; // Incorrect user password
EE_AS_SYS_SEND_BINDING_PHONE_CODE8 = -214417; // Can only send once within 120 seconds

EE_AS_SYS_BINDING_PHONE_CODE = -214500; // Server response failed
EE_AS_SYS_BINDING_PHONE_CODE1 = -214502; // Interface verification failed
EE_AS_SYS_BINDING_PHONE_CODE2 = -214503; // Parameter error
EE_AS_SYS_BINDING_PHONE_CODE3 = -214504; // Phone number already bound
EE_AS_SYS_BINDING_PHONE_CODE4 = -214506; // Username does not exist
EE_AS_SYS_BINDING_PHONE_CODE5 = -214507; // Incorrect verification code
EE_AS_SYS_BINDING_PHONE_CODE6 = -214510; // Binding failed
EE_AS_SYS_BINDING_PHONE_CODE7 = -214513; // Incorrect user password

EE_AS_REGISTER_EXTEND_CODE0 = -214802; // Interface verification failed
EE_AS_REGISTER_EXTEND_CODE1 = -214803; // Parameter error
EE_AS_REGISTER_EXTEND_CODE2 = -214804; // Phone number has been registered
EE_AS_REGISTER_EXTEND_CODE3 = -214806; // Username has been registered
EE_AS_REGISTER_EXTEND_CODE4 = -214807; // Registration code verification error
EE_AS_REGISTER_EXTEND_CODE5 = -214810; // Registration failed (specific failure information in msg)

EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE = -214900; // Service response failed
EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE1 = -214902; // Interface verification failed
EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE2 = -214903; // Parameter error
EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE3 = -214906; // Username has been registered
EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE4 = -214907; // Registration code verification error
EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE5 = -214908; // Email has been registered
EE_AS_REGISTE_BY_EMAIL_EXTEND_CODE6 = -214910; // Registration failed

EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE = -215000; // Server response failed
EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE1 = -215002; // Interface verification failed
EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE2 = -215003; // Parameter error
EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE3 = -215006; // Username has been registered
EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND_CODE4 = -215010; // Registration failed

EE_ACCOUNT_HTTP_USERNAME_PWD_ERROR = -604000; //4000: Username or password error

EE_ACCOUNT_VERIFICATION_CODE_ERROR = -604010; //4010: Incorrect verification code
EE_ACCOUNT_PASSWORD_NOT_SAME = -604011; //4011: Passwords do not match
EE_ACCOUNT_USERNAME_HAS_BEEN_REGISTERED = -604012; //4012: Username has been registered
EE_ACCOUNT_USERNAME_IS_EMPTY = -604013; //4013: Username is empty
EE_ACCOUNT_PASSWORD_IS_EMPTY = -604014; //4014: Password is empty
EE_ACCOUNT_COMFIRMPWD_IS_EMPTY = -604015; //4015: Confirm password is empty
EE_ACCOUNT_PHONE_IS_EMPTY = -604016; //4016: Phone number is empty
EE_ACCOUNT_USERNAME_FORMAT_NOT_CORRECT = -604017; //4017: Incorrect username format
EE_ACCOUNT_PASSWORD_FORMAT_NOT_CORRECT = -604018; //4018: Incorrect password format
EE_ACCOUNT_COMFIRMPWD_FORMAT_NOT_CORRECT = -604019; //4019: Incorrect confirm password format
EE_ACCOUNT_PHONE_FORMAT_NOT_CORRECT = -604020; //4020: Incorrect phone number format
EE_ACCOUNT_PHONE_IS_EXIST = -604021; //4021: Phone number already exists
EE_ACCOUNT_PHONE_NOT_EXSIT = -604022; //4022: Phone number does not exist
EE_ACCOUNT_EMAIL_IS_EXIST = -604023; //4023: Email already exists
EE_ACCOUNT_EMAIL_NOT_EXIST = -604024; //4024: Email does not exist
EE_ACCOUNT_USER_NOT_EXSIT = -604025; //4025: User does not exist
EE_ACCOUNT_OLD_PASSWORD_ERROR = -604026; //4026: Incorrect original password
EE_ACCOUNT_MODIFY_PASSWORD_ERROR = -604027; //4027: Failed to modify password

EE_ACCOUNT_USERID_IS_EMPTY = -604029; //4029: User ID is empty
EE_ACCOUNT_VERIFICATION_CODE_IS_EMPTY = -604030; //4030: Verification code is empty
EE_ACCOUNT_EMAIL_IS_EMPTY = -604031; //4031: Email is empty
EE_ACCOUNT_EMAIL_FORMATE_NOT_CORRECT = -604032; //4032: Incorrect email format

EE_ACCOUNT_DEVICE_ILLEGAL_NOT_ADD = -604100; //4100: Illegal device, not allowed to add
EE_ACCOUNT_DEVICE_ALREADY_EXSIT = -604101; //4101: Device already exists
EE_ACCOUNT_DEVICE_CHANGE_IFNO_FAIL = -604103; //4103: Failed to modify device information
EE_ACCOUNT_DEVICE_UUID_ILLEGAL = -604104; //4104: Device UUID parameter exception
EE_ACCOUNT_DEVICE_USERNAME_ILLEGAL = -604105; //4105: Device username parameter exception
EE_ACCOUNT_DEVICE_PASSWORD_ILLEGAL = -604106; //4106: Device password parameter exception
EE_ACCOUNT_DEVICE_PORT_ILLEGAL = -604107; //4107: Device port parameter exception
EE_ACCOUNT_DEVICE_EXTEND_ILLEGAL = -604108; //4108: Device extended field parameter exception (DEV_EXT_FAIL)
EE_ACCOUNT_DEV_PASS_CONPASS_DIFF = -604109; //4109:
EE_ACCOUNT_DEV_NEW_PASSWORD_FAIL = -604110; //4110: New password verification failed
EE_ACCOUNT_DEV_CONFIRM_PASSWORD_FAIL = -604111; //4111: Confirm password verification failed
EE_ACCOUNT_DEV_NICKNAME_FAIL = -604112; //4112: Device nickname verification failed
EE_ACCOUNT_DEV_IP_FAIL = -604113; //4113:
EE_ACCOUNT_DEV_CLOUD_STORAGE_SUPPORT = -604114; //4114: Cloud storage supported
EE_ACCOUNT_DEV_CLOUD_STORAGE_UNSUPPORT = -604115; //4115: Cloud storage not supported
EE_ACCOUNT_SETMA_FAIL = -604116; //4116: Failed to transfer device master account to another user. Check if the user has the device and the user has device master account permissions
EE_ACCOUNT_NOT_MASTER_ACCOUNT = -604117; //4117: The current account is not the master account of the current device
EE_ACCOUNT_DEVICE_NOT_EXSIT = -604118; //4118: Device no longer exists, it has been removed
EE_ACCOUNT_DEVICE_ADD_NOT_UNIQUE = -604119; //4119: Device addition is not unique, it has been added by another account
EE_ACCOUNT_DEVICE_ADD_MAX_LIMIT = -604120; //4120: Maximum limit reached for adding devices (100)
EE_ACCOUNT_DEV_SUPTOKEN_CAN_ONLY_ADDED_BY_ONE_ACCOUNT = -604126; //4126: Device supports token and can only be added by one account
EE_ACCOUNT_TOKEN_IS_MISSING = -604127; //4127: Device token is missing
EE_ACCOUNT_DEV_PID_FAIL = -604129; //4129: Device PID parameter exception (length greater than 60)

EE_ACCOUNT_SEND_CODE_FAIL = -604300; //4300: Failed to send

EE_ACCOUNT_MESSAGE_INTERFACE_CHECK_ERROR = -604400; //4400: SMS interface verification failed, please contact us
EE_ACCOUNT_MESSAGE_INTERFACE_PARM_ERROR = -604401; //4401: SMS interface parameter error, please contact us
EE_ACCOUNT_MESSAGE_TIME_MORE_THAN_THREE = -604402; //4402: SMS sending exceeded the limit, each phone number can only send three times a day
EE_ACCOUNT_MESSAGE_SEND_ERROR = -604403; //4403: Failed to send, please try again later
EE_ACCOUNT_MESSAGE_SEND_OFTEN = -604404; //4404: Sending too frequently, please wait for 120 seconds between each send

EE_ACCOUNT_HTTP_SERVER_ERROR = -600900; //5000: Server error
EE_ACCOUNT_CERTIFICATE_NOT_EXIST = -605001; //5001: Certificate does not exist
EE_ACCOUNT_HTTP_HEADER_ERROR = -605002; //5002: Request header information error
EE_ACCOUNT_CERTIFICATE_FAILURE = -605003; //5003: Certificate expired
EE_ACCOUNT_ENCRYPT_CHECK_FAILURE = -605004; //5004: Encryption key verification error
EE_ACCOUNT_PARMA_ABNORMAL = -605005; //5005: Parameter exception

EE_ACCOUNT_USER_IS_NOT_EXSIT = -661412; //1412: Username does not exist

                   