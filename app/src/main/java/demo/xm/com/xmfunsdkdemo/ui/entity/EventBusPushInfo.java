package demo.xm.com.xmfunsdkdemo.ui.entity;

/**
 * @class 报警消息操作传递
 */
public class EventBusPushInfo {
    private PUSH_OPERATION operationType;
    public enum PUSH_OPERATION {
        LINK,
        UNLINK,
        ADD_DEV,
        REMOVE_DEV,
        LOGOUT,
        SHARE_ACCEPT,
    }

    public PUSH_OPERATION getOperationType() {
        return this.operationType;
    }

    public void setOperationType(PUSH_OPERATION operationType) {
        this.operationType = operationType;
    }


}
