package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view;

import com.xm.ui.widget.drawgeometry.model.GeometryInfo;

/**
 * Created by zhangyongyong on 2017-05-09-12:57.
 */

public interface AlertSetFunctionInterface {
    /**
     * 设置集合图形类型
     {@link GeometryInfo#GEOMETRY_CIRCULAR
      *             @link GeometryInfo#GEOMETRY_LINE
      *             @link GeometryInfo#GEOMETRY_TRIANGLE
      *             @link GeometryInfo#GEOMETRY_RECTANGLE
      *             @link GeometryInfo#GEOMETRY_PENTAGON
      *             @link GeometryInfo#GEOMETRY_L
      *             @link GeometryInfo#GEOMETRY_AO
      *             @link GeometryInfo#GEOMETRY_CUSTOM}
     * @param type
     */
    void setGeometryType(int type);

    /**
     * 初始化区域拌线类型
     * @param lineType
     */
    void initAlertLineType(int lineType);

    /**
     * 设置区域拌线类型
     * @param lineType
     */
    void setAlertLineType(int lineType);

    void initAlertAreaEdgeCount(int edgeCount);

    //设置方向掩码
    void setDirectionMask(String directionMask);

    //设置支持警戒区域种类掩码
    void setAreaMask(String areaMask);

    /**
     * 还原
     */
    void revert();

}
