package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view;

/**
 * Created by zhangyongyong on 2017-05-09-12:57.
 */

public interface AlertSetFunctionInterface {

    void setShapeType(int type);

    void initAlertLineType(int lineType);

    void setAlertLineType(int lineType);

    void initAlertAreaEdgeCount(int edgeCount);

    //设置方向掩码
    void setDirectionMask(String directionMask);

    //设置支持警戒区域种类掩码
    void setAreaMask(String areaMask);

}
