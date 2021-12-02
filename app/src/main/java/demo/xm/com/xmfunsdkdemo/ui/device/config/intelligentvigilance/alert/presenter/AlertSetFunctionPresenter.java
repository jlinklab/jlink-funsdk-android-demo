package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.presenter;

import android.content.Context;

import com.basic.G;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.model.FunctionViewItemElement;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view.AlertSetFunctionInterface;

import static com.lib.sdk.bean.HumanDetectionBean.IA_BIDIRECTION;
import static com.lib.sdk.bean.HumanDetectionBean.IA_DIRECT_BACKWARD;
import static com.lib.sdk.bean.HumanDetectionBean.IA_DIRECT_FORWARD;
import static com.manager.db.Define.ALERT_AREA_TYPE;
import static com.manager.db.Define.ALERT_lINE_TYPE;
import static com.xm.ui.widget.drawgeometry.model.GeometryInfo.GEOMETRY_AO;
import static com.xm.ui.widget.drawgeometry.model.GeometryInfo.GEOMETRY_L;
import static com.xm.ui.widget.drawgeometry.model.GeometryInfo.GEOMETRY_LINE;
import static com.xm.ui.widget.drawgeometry.model.GeometryInfo.GEOMETRY_PENTAGON;
import static com.xm.ui.widget.drawgeometry.model.GeometryInfo.GEOMETRY_RECTANGLE;
import static com.xm.ui.widget.drawgeometry.model.GeometryInfo.GEOMETRY_TRIANGLE;


public class AlertSetFunctionPresenter {
    private AlertSetFunctionInterface mSetFunctionInterface;
    private HashMap<Integer,Boolean> directEnableMap;
    private HashMap<Integer,Boolean> areaEnableMap;
    private List<FunctionViewItemElement> itemList;
    private int curSelectItemPos;
    private Context context;
    public AlertSetFunctionPresenter(Context context,AlertSetFunctionInterface functionInterface) {
        this.context = context;
        this.mSetFunctionInterface = functionInterface;
        directEnableMap = new HashMap<>();
        directEnableMap.put(IA_DIRECT_FORWARD,false);
        directEnableMap.put(IA_DIRECT_BACKWARD,false);
        directEnableMap.put(IA_BIDIRECTION,false);

        areaEnableMap = new HashMap<>();
        areaEnableMap.put(GEOMETRY_TRIANGLE,false);
        areaEnableMap.put(GEOMETRY_RECTANGLE,false);
        areaEnableMap.put(GEOMETRY_PENTAGON,false);
        areaEnableMap.put(GEOMETRY_L,false);
        areaEnableMap.put(GEOMETRY_AO,false);
    }

    public void showShapeOnCanvas(int position, int ruleType) {
        if (itemList == null || itemList.size() <= position) {
            return;
        }
        this.curSelectItemPos = position;
        int itemType = itemList.get(position).getItemType();
        switch (ruleType) {
            case ALERT_lINE_TYPE:
                mSetFunctionInterface.setGeometryType(GEOMETRY_LINE);
                mSetFunctionInterface.setAlertLineType(itemType);
                break;
            case ALERT_AREA_TYPE:
                mSetFunctionInterface.setGeometryType(itemType);
                break;
            default:
                break;
        }
    }

    public int getCurSelectItemPos() {
        return curSelectItemPos;
    }

    public int getItemType(int position) {
        if (itemList == null || itemList.size() <= position) {
            return 0;
        }
        return itemList.get(position).getItemType();
    }

    public int getCurSelItemType() {
        if (itemList == null || itemList.size() <= curSelectItemPos) {
            return 0;
        }
        return itemList.get(curSelectItemPos).getItemType();
    }

    public List<FunctionViewItemElement> initFunctionViewData(int type) {
        itemList = new ArrayList();
        switch (type) {
            case ALERT_lINE_TYPE:
                if (directEnableMap.get(IA_DIRECT_FORWARD)) {
                    itemList.add(new FunctionViewItemElement(R.mipmap.smart_analyze__line_right_nor,
                            R.mipmap.smart_analyze__line_right_sel,context.getString(R.string.smart_analyze_line_left)
                            ,IA_DIRECT_FORWARD));
                }
                if (directEnableMap.get(IA_DIRECT_BACKWARD)) {
                    itemList.add(new FunctionViewItemElement(R.mipmap.smart_analyze__line_left_nor,
                            R.mipmap.smart_analyze__line_left_sel,
                            context.getString(R.string.smart_analyze_line_right),IA_DIRECT_BACKWARD));
                }
                if (directEnableMap.get(IA_BIDIRECTION)) {
                    itemList.add(new FunctionViewItemElement(R.mipmap.smart_analyze__line_middle_nor,
                            R.mipmap.smart_analyze__line_middle_sel,
                            context.getString(R.string.smart_analyze_line_middle),IA_BIDIRECTION));
                }
                break;
            case ALERT_AREA_TYPE:
                if (areaEnableMap.get(GEOMETRY_TRIANGLE)) {
                    itemList.add(new FunctionViewItemElement(R.mipmap.smart_analyze_shape_triangle_nor,
                            R.mipmap.smart_analyze_shape_triangle_sel,
                            context.getString(R.string.smart_analyze_shape_triangle),GEOMETRY_TRIANGLE));
                }
                if (areaEnableMap.get(GEOMETRY_RECTANGLE)) {
                    itemList.add(new FunctionViewItemElement(R.mipmap.smart_analyze_shape_rectangle_nor,
                            R.mipmap.smart_analyze_shape_rectangle_sel,
                            context.getString(R.string.smart_analyze_shape_rectangle),GEOMETRY_RECTANGLE));
                }
                if (areaEnableMap.get(GEOMETRY_PENTAGON)) {
                    itemList.add(new FunctionViewItemElement(R.mipmap.smart_analyze_shape_pentagram_nor,
                            R.mipmap.smart_analyze_shape_pentagram_sel,
                            context.getString(R.string.smart_analyze_shape_pentagram),GEOMETRY_PENTAGON));
                }
                if (areaEnableMap.get(GEOMETRY_L)) {
                    itemList.add(new FunctionViewItemElement(R.mipmap.smart_analyze_shape_l_nor,
                            R.mipmap.smart_analyze_shape_l_sel,
                            context.getString(R.string.smart_analyze_shape_l_sel),GEOMETRY_L));
                }
                if (areaEnableMap.get(GEOMETRY_AO)) {
                    itemList.add(new FunctionViewItemElement(R.mipmap.smart_analyze_shape_concave_nor,
                            R.mipmap.smart_analyze_shape_concave_sel,
                            context.getString(R.string.smart_analyze_shape_concave),GEOMETRY_AO));

                }
                //itemList.add(new FunctionViewItemElement(R.mipmap.smart_analyze_shape_customize_nor, R.mipmap.smart_analyze_shape_customize_sel, FunSDK.TS("smart_analyze_shape_customize")));
                break;
            default:
                break;
        }
        return itemList;
    }

    public void onDestroy() {
        mSetFunctionInterface = null;
    }

    public void setDirectionMask(String directionMask) {
        if (directEnableMap == null) {
            directEnableMap = new HashMap<>();
        }
        long mask = G.getLongFromHex(directionMask);
        int index = 0;
        while (mask > 0) {
            if ((mask & 0x01) == 1) {
                directEnableMap.put(index,true);
            }
            index++;
            mask = mask >> 1;
        }
    }

    public void setAreaMask(String areaMask) {
        if (areaEnableMap == null) {
            areaEnableMap = new HashMap<>();
        }
        long mask = G.getLongFromHex(areaMask);
        int index = 0;
        while (mask > 0) {
            if ((mask & 0x01) == 1) {
                areaEnableMap.put(index + 1,true);
            }
            index++;
            mask = mask >> 1;
        }
    }

    public boolean isDirectionDlgShow() {
        return directEnableMap != null
                && directEnableMap.get(IA_DIRECT_FORWARD)
                && directEnableMap.get(IA_DIRECT_BACKWARD)
                && directEnableMap.get(IA_BIDIRECTION);
    }
}
