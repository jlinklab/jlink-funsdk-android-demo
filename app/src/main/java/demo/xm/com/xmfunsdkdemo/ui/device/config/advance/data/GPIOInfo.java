package demo.xm.com.xmfunsdkdemo.ui.device.config.advance.data;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * GPIO信息
 */
public class GPIOInfo {
    /**
     * GPIO口序号(不是真实的编号，需要配置文件进行设定)
     * <p>
     * 取值范围： 1,2
     */
    @JSONField(name = "Type")
    private int type;
    /**
     * 状态
     * 取值范围：
     * 0：不使能/拉低
     * 1：使能/拉高
     * 2：每500毫秒自动周期性变化
     */
    @JSONField(name = "Status")
    private int status;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
